package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rasendeRoboter.Outils;
import rasendeRoboter.Phase;
import rasendeRoboter.Plateau;
import rasendeRoboter.Protocole;

public class Client extends Application {

	private static final String LOGIN_SCREEN_UI = "Login.fxml";
	private static final String GAME_SCREEN_UI = "Game.fxml";

	private String userName, host;
	private AnchorPane root;
	private Scene scene;
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private Receive receiver;
	private TextArea serverAnswer, chatTextArea, sendChatTextArea;
	private Button logoutButton;
	private Stage stage;
	private boolean premierLancement = true, tuAsTrouve = false,
			tuEnchere = false;
	private boolean attenteStatutSolution = false;
	private Plateau plateau;
	private GridPane plateauGrid;
	private Phase phase = null;
	private TextField coupTextField;
	private Button trouveEnchereButton;
	private Label errorLabel;
	private EventHandler<KeyEvent> filter;
	private int lastEnchere = Integer.MAX_VALUE;





	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new AnchorPane();
		if (stage == null) {
			this.stage = primaryStage;
		}
		initLoginGUI(primaryStage);
	}

	public void initLoginGUI(Stage stage) {
		if (root != null) {
			root.getChildren().clear();
		}

		try {
			GridPane rootLogin = (GridPane) FXMLLoader.load(getClass().getResource(LOGIN_SCREEN_UI));
			Button btn = (Button) rootLogin.lookup("#loginButton");
			TextField userTextField = (TextField) rootLogin.lookup("#userTextField");
			TextField hostTextField = (TextField) rootLogin.lookup("#hostTextField");
			Text errorMessageText= (Text) rootLogin.lookup("#errorMessageText");
			if (premierLancement) {
				btn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
						if (socket != null) 
							initClientGUI(stage);
					}
				});
				filter = new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.ENTER) {
							socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
							if (socket != null) 
								initClientGUI(stage);
						}
					}
				};
				stage.addEventHandler(KeyEvent.KEY_PRESSED, filter);
				premierLancement = false;
			}
			root.getChildren().add(rootLogin);
			if (scene == null)
				scene = new Scene(root);
			stage.setMinWidth(400);
			stage.setMinHeight(300);
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.setTitle("Rasende Roboter Launcher");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public Socket connexion(String username, String host, Text actionTarget)  {
		actionTarget.setFill(Color.FIREBRICK);
		boolean ok = Outils.checkHostAndCheckUsername(username, host, actionTarget);
		if (ok) {
			this.host= host;
			this.userName = username;
			try {
				if (socket == null) {
					this.socket = new Socket(host, Protocole.PORT);
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					this.out = new PrintStream(socket.getOutputStream(), true);
				}
				Protocole.connect(userName, out);
//				TODO decommenter pour gestion BIENVENUE ou login non dispo
				/************************************************************/
				String serverAnswer = in.readLine();
				if (serverAnswer.equals(Protocole.BIENVENUE+"/"+username+"/")) { 

					this.receiver = new Receive();
					receiver.start();
				}
				else {
					actionTarget.setText("Invalid name"); // TODO changer message affichage
					return null;
				}
				/**************************************************************/
			} catch (UnknownHostException e) {
				actionTarget.setText(e.getMessage());
				return null;
			} catch (IOException e) {
				actionTarget.setText(e.getMessage());
				return null;
			}
		}
		return socket;
	}

	public void initInternalNodes() {
		if (root != null) {
			serverAnswer = (TextArea) root.lookup("#serverAnswer");
			chatTextArea = (TextArea) root.lookup("#chatTextArea");
			sendChatTextArea = (TextArea) root.lookup("#sendChatTextArea");
			logoutButton = (Button) root.lookup("#logoutButton");
			plateauGrid = (GridPane) root.lookup("#plateauGrid");
			coupTextField = (TextField) root.lookup("#coupTextField");
			trouveEnchereButton = (Button) root.lookup("#trouveEnchereButton");
			errorLabel = (Label) root.lookup("#errorLabel");

		}
	}

	public void installEventHandler() {
		sendChatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					event.consume();
					String msg = sendChatTextArea.getText();
					Protocole.sendChat(userName, msg, out);
					updateChat("Me : "+msg);
					sendChatTextArea.setText("");
					sendChatTextArea.setPromptText("Enter a message ...");
				}
			}
		});
		logoutButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		trouveEnchereButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (phase == Phase.REFLEXION) {
					String coups = coupTextField.getText();
					coupTextField.setText("");
					if(coups.matches("\\d+")) {
						errorLabel.setText("");
						Protocole.sendTrouve(userName, coups, out);
						tuAsTrouve = true;
					}
					else {
						errorLabel.setText("Veuillez saisir un nombre");
						errorLabel.setTextFill(Color.FIREBRICK);
					}
				}
				else if (phase == Phase.ENCHERE) {
					String coups = coupTextField.getText();
					coupTextField.setText("");
					if(coups.matches("\\d+")) {
						int enchere = Integer.parseInt(coups);
						if (enchere >= lastEnchere) {
							errorLabel.setText("Ceci n'est pas une enchere");
							errorLabel.setTextFill(Color.FIREBRICK);
						}
						else {
							Protocole.sendEnchere(userName, coups, out);
							lastEnchere = enchere;
							tuEnchere = true;
						}
					}
					else {
						errorLabel.setText("Veuillez saisir un nombre");
						errorLabel.setTextFill(Color.FIREBRICK);
					}
				}
				else {
					System.err.println("trouveEnchereButtonEventHandle : Je ne dois pas passer ici");
				}
			}
		});
	}

	public void initClientGUI(Stage stage) {
		if (root != null) {
			root.getChildren().clear();
		}
		try {
			BorderPane game = (BorderPane) FXMLLoader.load(getClass().getResource(GAME_SCREEN_UI));
			root.getChildren().add(game);
			initInternalNodes();
			installEventHandler();
			stage.removeEventHandler(KeyEvent.KEY_PRESSED, filter);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override public void handle(WindowEvent t) {
					if(out != null) {
						Protocole.disconnect(userName, out);//TODO gerer fermeture fenetre	
					}
				}
			});
			/* debut traitement */

			serverAnswer.appendText("Bienvenue "+userName+"\n");
			Label hostAdressLabel = (Label) game.lookup("#hostAdressLabel");
			hostAdressLabel.setText(host);

			/* fin traitement */

			if (scene == null)
				scene = new Scene(root);

			stage.setMinWidth(1204);
			stage.setMinHeight(680);
			stage.setScene(scene);
			stage.setTitle("Rasende Roboter Client");
			stage.centerOnScreen();
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	// TODO revoir l'affichage et la mise a jour du plateau
	public void updatePlateau(){
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				BorderPane caseGUI;
				plateauGrid.setGridLinesVisible(false);
				plateauGrid.getChildren().clear();

				for (int i = 0; i<16 ; i++) {
					for (int j = 0; j<16 ; j++) {
						caseGUI = plateau.getCase(i, j).render();
						GridPane.setColumnIndex(caseGUI, j);
						GridPane.setRowIndex(caseGUI, i);

						plateauGrid.getChildren().add(caseGUI);
					}
				}
				plateauGrid.setGridLinesVisible(true);
			}
		});
	}

	public void decoderReponseServer(String reponse) {
		String commande = Outils.getCommandeName(reponse);
		String user, message, data, enigme, bilan;
		switch (commande) {
		case Protocole.BIENVENUE:
			System.out.println("BIENVENUE");
			break;
		case Protocole.CONNECTE:
			user = Outils.getFirstArg(reponse);
			updateServerAnswer(user+" s'est connecte");
			break;
		case Protocole.DECONNEXION:
			user = Outils.getFirstArg(reponse);
			updateServerAnswer(user+" s'est deconnecte");
			break;
		case Protocole.RECEIVE_CHAT:
			user = Outils.getFirstArg(reponse);
			message = Outils.getSecondArg(reponse);
			if (!user.equals(userName))
				updateChat(user+" : "+message);
			break;
		case Protocole.SESSION:
			data = Outils.getFirstArg(reponse);
			if (plateau != null) {
				plateau.reset();
			}
			plateau = new Plateau(data);
			updatePlateau();
			phase = Phase.ATTENTE_TOUR;
			updateServerAnswer("Debut Session");
			break;
		case Protocole.VAINQUEUR:
			user = Outils.getFirstArg(reponse);
			updateServerAnswer("Fin de la session");
			updateServerAnswer("Le vainqueur est : "+user);
			break;
		case Protocole.TOUR:
			enigme = Outils.getFirstArg(reponse);
			bilan = Outils.getFirstArg(reponse);
			// TODO faire qqch avec enigme et bilan, lancer timer
			if (phase == Phase.ATTENTE_TOUR) {
				phase = Phase.REFLEXION;
				trouveEnchereButton.setText("Trouve");
				trouveEnchereButton.setDisable(false);
				updateServerAnswer("Debut de la phase de reflexion");
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.TU_AS_TROUVE:
			if (phase == Phase.REFLEXION && tuAsTrouve) {
				phase = Phase.ENCHERE;
				updateServerAnswer("Annonce validee");
				updateServerAnswer("Fin de la phase de reflexion");
				updateTrouveEnchereButton("Encherir");
				tuAsTrouve = false;
				lastEnchere = Integer.MAX_VALUE;
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.IL_A_TROUVE:
			if (phase == Phase.REFLEXION) {
				phase = Phase.ENCHERE;
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				updateServerAnswer(user+" a trouve une solution en "+data+" coups");
				updateServerAnswer("Fin de la phase de reflexion");
				updateTrouveEnchereButton("Encherir");
				tuAsTrouve = false;
				lastEnchere = Integer.MAX_VALUE;
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_REFLEXION:
			if (phase == Phase.REFLEXION) {
				phase = Phase.ENCHERE;			
				updateServerAnswer("Expiration du delai imparti a la reflexion");
				updateServerAnswer("Fin de la phase de reflexion");
				// TODO Debut de la phade d'enchere plus adapte ?
				updateTrouveEnchereButton("Encherir");
				lastEnchere = Integer.MAX_VALUE;
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.VALIDATION:
			if (phase == Phase.ENCHERE && tuEnchere) {
				updateServerAnswer("Enchere validee");
				trouveEnchereButton.setDisable(true);
				tuEnchere = false;				
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.ECHEC:
			if (phase == Phase.ENCHERE && tuEnchere) {
				user = Outils.getFirstArg(reponse);
				updateServerAnswer("Enchere annulee car incoherente avec celle de "+user);
				tuEnchere = false;				
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.NOUVELLE_ENCHERE:
			if (phase == Phase.ENCHERE) {
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				updateServerAnswer(user+" a encheri avec "+data+" coups");
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_ENCHERE:
			if (phase == Phase.ENCHERE) {
				phase = Phase.RESOLUTION;
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				updateServerAnswer("Fin des encheres");

				if (!user.equals(userName)) {
					updateServerAnswer("Le joueur actif est "+user);
					// TODO mettre info du joueur dans un Label
				}
				else {
					updateServerAnswer("Taper votre solution");
					//TODO preparer envoie solution, utiliser un boolean
				}
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.SA_SOLUTION:
			if (phase == Phase.RESOLUTION && attenteStatutSolution == false) {
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				if (!user.equals(userName)) {
					updateServerAnswer(user+" a propose une solution");
				}
				attenteStatutSolution = true;
				updateServerAnswer("debug : debut animation");
				//TODO ici faire l'animation de la solution
				updateServerAnswer("debug : fin animation");
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.BONNE:
			if (phase == Phase.RESOLUTION && attenteStatutSolution) {
				updateServerAnswer("Solution correcte");
				updateServerAnswer("Fin du tour");
				attenteStatutSolution = false;
				phase = Phase.ATTENTE_TOUR;
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.MAUVAISE:
			if (phase == Phase.RESOLUTION && attenteStatutSolution) {
				user = Outils.getFirstArg(reponse);
				updateServerAnswer("Solution refusee");
				if (!user.equals(userName)) {
					updateServerAnswer("Le joueur actif est "+user);
					// TODO mettre info du joueur dans un Label
				}
				else {
					updateServerAnswer("Taper votre solution");
					//TODO preparer envoie solution, utiliser un boolean
				}
				attenteStatutSolution = false;
			}
			else {
				System.err.println("Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_RESOLUTION:
			if (phase == Phase.RESOLUTION) {
				updateServerAnswer("Plus de joueurs restants");
				updateServerAnswer("Fin du tour");
				//TODO bien verifier que les boolean sont reset
				phase = Phase.ATTENTE_TOUR;
			}
			else {
				System.err.println("je ne dois pas passer ici");
			}
			break;
		case Protocole.TROP_LONG:
			if (phase == Phase.RESOLUTION) {
				user = Outils.getFirstArg(reponse);
				updateServerAnswer("Temps depasse");
				if (!user.equals(userName)) {
					updateServerAnswer("Le joueur actif est "+user);
					// TODO mettre info du joueur dans un Label
				}
				else {
					updateServerAnswer("Taper votre solution");
					//TODO preparer envoie solution, utiliser un boolean
				}
			}
			else {
				System.err.println(Protocole.TROP_LONG+" - je ne dois pas passer ici");
			}
			break;
		default:
			updateServerAnswer("default "+reponse);


		}
	}

	private void updateTrouveEnchereButton(String text) {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				trouveEnchereButton.setText("Encherir");
			}
		});
	}

	private void updateServerAnswer(String s) {
		serverAnswer.appendText(s+"\n");
	}

	private void updateChat(String s) {
		chatTextArea.appendText(s+"\n");
	}


	class Receive extends Service<Void> {

		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					String recu;
					try {
						while ((recu = in.readLine()) != null) {
							decoderReponseServer(recu);
							updateValue(null);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}	

		/*	public void pause() {
			updateServerAnswer("pause de la thread");
			this.cancel();
		}

		public void resume() {
			updateServerAnswer("relance");
			this.restart();
		}*/
	};




}
