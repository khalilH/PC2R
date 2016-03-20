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
import javafx.concurrent.WorkerStateEvent;
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
	private boolean premierLancement = true;
	private Plateau plateau;
	private GridPane plateauGrid;




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
				stage.addEventHandler(KeyEvent.KEY_PRESSED, 
						new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.ENTER) {
							socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
							if (socket != null) 
								initClientGUI(stage);
						}
					}
				});
				premierLancement = false;
			}
			root.getChildren().add(rootLogin);
			if (scene == null)
				scene = new Scene(root);
			stage.setMinWidth(400);
			stage.setMinHeight(300);
			stage.setScene(scene);
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
			//			System.out.println("login "+userName+" @ "+host);
			try {
				if (socket == null) {
					this.socket = new Socket(host, Protocole.PORT);
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					this.out = new PrintStream(socket.getOutputStream(), true);
				}
				Protocole.connect(userName, out);
				//				String serverAnswer = in.readLine();
				//if (serverAnswer.equals(Protocole.BIENVENUE+"/"+username+"/")) {
				this.receiver = new Receive();
				receiver.start();
				//				}
				//				else {
				//					actionTarget.setText("Invalid name"); // TODO changer message affichage
				//					return null;
				//				}
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
		}
	}

	public void updatePlateau(){
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				BorderPane caseGUI;
				for (int i = 0; i<16 ; i++) {
					for (int j = 0; j<16 ; j++) {
						caseGUI = plateau.getCase(i, j).render();
						GridPane.setColumnIndex(caseGUI, j);
						GridPane.setRowIndex(caseGUI, i);
						plateauGrid.getChildren().add(caseGUI);
					}
				}
			}
		});
	}

	public void installEventHandler() {
		sendChatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					event.consume();
					String msg = sendChatTextArea.getText();
					Protocole.sendChat(userName, msg, out);
					chatTextArea.appendText("Me : "+msg);
					sendChatTextArea.setText("");
					sendChatTextArea.setPromptText("Enter a message ...");
				}
			}
		});
		logoutButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Protocole.disconnect(userName, out);
				initLoginGUI(stage);
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
			stage.setMinHeight(661);
			stage.setScene(scene);
			stage.setTitle("Rasende Roboter Client");
			stage.centerOnScreen();
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void decoderReponseServer(String reponse) {
		String commande = Outils.getCommandeName(reponse);
		String user, message, data;
		switch (commande) {
		case Protocole.BIENVENUE:
			System.out.println("BIENVENUE");
			break;
		case Protocole.CONNECTE:
			user = Outils.getFirstArg(reponse);
			serverAnswer.appendText(user+" s'est connecte\n");
			break;
		case Protocole.SORTI:
			user = Outils.getFirstArg(reponse);
			serverAnswer.appendText(user+" s'est deconnecte\n");
			break;
		case Protocole.RECEIVE_CHAT:
			user = Outils.getFirstArg(reponse);
			message = Outils.getSecondArg(reponse);
			if (!user.equals(userName))
				chatTextArea.appendText(user+" : "+message+"\n");
			break;
		case Protocole.SESSION:
			data = Outils.getFirstArg(reponse);
			plateau = new Plateau(data);
			updatePlateau();
			serverAnswer.appendText("Debut Session\n");
			break;
		default:
			serverAnswer.appendText(reponse+"\n");


		}
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
	};




}
