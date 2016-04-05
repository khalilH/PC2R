package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import rasendeRoboter.Bilan;
import rasendeRoboter.Bilan.Score;
import rasendeRoboter.Enigme;
import rasendeRoboter.Phase;
import rasendeRoboter.Plateau;

/**
 * 
 * @author Ladislas Halifa -
 * Classe Principale du Client
 */
public class Client extends Application {

	private static final String GAME_SCREEN_UI = "Game.fxml";
	private static final String CORRECT = "audio/correct.mp3";
	private static final String WRONG = "audio/wrong.mp3";
	private static final String CHAT = "audio/chat.mp3";
	private static final String ENCHERE = "audio/enchere.mp3";
	private static final String LOGIN = "audio/login.mp3";
	private static final String LOGOUT = "audio/logout.mp3";
	private static final String REFLEXION = "audio/reflexion.mp3";
	private static final String RESOLUTION = "audio/resolution.mp3";


	/* Client stuff */
	private BufferedReader in;
	private Calendar calendar = Calendar.getInstance();
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private PrintStream out;
	private Receive receiver;
	private Socket socket;
	private Stage stage;
	private String userName;

	/* Audio */
	Media correctSound = null;
	Media wrongSound = null;
	Media chatSound = null;
	Media enchereSound = null;
	Media loginSound = null;
	Media logoutSound = null;
	Media reflexionSound = null;
	Media resolutionSound = null;
	MediaPlayer correctMediaPlayer = null;
	MediaPlayer wrongMediaPlayer = null;
	MediaPlayer chatMediaPlayer = null;
	MediaPlayer enchereMediaPlayer = null;
	MediaPlayer loginMediaPlayer = null;
	MediaPlayer logoutMediaPlayer = null;
	MediaPlayer reflexionMediaPlayer = null;
	MediaPlayer resolutionMediaPlayer = null;

	/* Game Stuff */
	private Bilan bilan;
	private boolean isAudioReady = false;
	private boolean attenteStatutSolution = false;
	private boolean tuAsTrouve = false;
	private boolean tuEnchere = false;
	private boolean taperCouleurRobot= true;
	private double interv = 100;
	private Enigme enigme;
	private Phase phase = null;
	private Plateau plateau = null;
	private String currentSolution = "";
	private String lastActif = "";

	/* javaFX Nodes */
	private AnchorPane root;
	private Button loginButton;
	private Button logoutButton;
	private Button sendChatButton;
	private Button solutionButton;
	private Button trouveEnchereButton;
	private GridPane plateauGrid;
	private Label errorLabel;
	private Label coupsSolutionLabel;
	private Label phaseLabel;
	private Label tourLabel;
	
	private Text errorMessageText;
	private TextArea solutionTextArea;
	private TextArea serverAnswer;
	private TextArea chatTextArea;
	private TextArea sendChatTextArea;
	private TextField coupTextField;
	private TextField hostTextField;
	private TextField userTextField;
	private TableView<Score> scoreTableView;
	private VBox solutionVBox;


	public static void main(String[] args) {
		try {
			launch(args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new AnchorPane();
		if (stage == null) {
			this.stage = primaryStage;
		}
		initClientGUI(primaryStage);
	}

	/**
	 * initialise la fenetre principale du client
	 * @param stage le stage de l'application
	 */
	private void initClientGUI(Stage stage) {
		if (root != null) {
			root.getChildren().clear();
		}
		try {
			BorderPane game = (BorderPane) FXMLLoader.load(getClass().getResource(GAME_SCREEN_UI));
			root.getChildren().add(game);		
			initInternalNodes();
			isAudioReady = initMedia();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override public void handle(WindowEvent t) {
					deconnexion();
				}
			});
			Scene scene = new Scene(root);

			stage.setResizable(false);
			stage.setScene(scene);
			stage.setTitle("Rasende Roboter Client");
			stage.centerOnScreen();
			stage.show();
			installEventHandler();
		} catch(Exception e) {
			System.err.println("[initClientGUI] "+e.getMessage());
		}
	}

	/**
	 * initialisation des differents Noeuds JavaFX
	 */
	@SuppressWarnings("unchecked")
	private void initInternalNodes() {
		if (root != null) {
			userTextField = (TextField) root.lookup("#userTextField");
			loginButton = (Button) root.lookup("#loginButton");
			hostTextField = (TextField) root.lookup("#hostTextField");
			errorMessageText = (Text) root.lookup("#errorMessageText");
			serverAnswer = (TextArea) root.lookup("#serverAnswer");
			chatTextArea = (TextArea) root.lookup("#chatTextArea");
			sendChatTextArea = (TextArea) root.lookup("#sendChatTextArea");
			logoutButton = (Button) root.lookup("#logoutButton");
			plateauGrid = (GridPane) root.lookup("#plateauGrid");
			coupTextField = (TextField) root.lookup("#coupTextField");
			trouveEnchereButton = (Button) root.lookup("#trouveEnchereButton");
			errorLabel = (Label) root.lookup("#errorLabel");
			tourLabel = (Label) root.lookup("#tourLabel");
			scoreTableView = (TableView<Bilan.Score>) root.lookup("#scoreTableView");
			TableColumn<Score,String> userCol = 
					new TableColumn<Score,String>("Username");
			userCol.setMinWidth(150);
			userCol.setCellValueFactory(
					new PropertyValueFactory<>("user"));

			TableColumn<Score,Integer> scoreCol = 
					new TableColumn<Score,Integer>("Score");
			scoreCol.setMinWidth(100);
			scoreCol.setCellValueFactory(
					new PropertyValueFactory<>("score"));
			scoreTableView.getColumns().add(userCol);
			scoreTableView.getColumns().add(scoreCol);
			solutionButton = (Button) root.lookup("#solutionButton");
			solutionTextArea = (TextArea) root.lookup("#solutionTextArea");
			solutionVBox = (VBox) root.lookup("#solutionVBox");
			coupsSolutionLabel =  (Label) root.lookup("#coupsSolutionLabel");
			phaseLabel = (Label) root.lookup("#phaseLabel");
			sendChatButton = (Button) root.lookup("#sendChatButton");
		}
	}

	/**
	 * Ajout des EventHandler  
	 */
	private void installEventHandler() {
		/* taper entree pour essayer de connecter au serveur*/
		userTextField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
				}
			}
		});
		hostTextField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
				}
			}
		});
		/* bouton pour essayer de se connecter au serveur*/
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				socket = connexion(userTextField.getText(), hostTextField.getText(),errorMessageText);
			}
		});

		/* taper Entree pour envoyer un message instantane */
		sendChatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					event.consume();
					envoyerMessageChat();
				}
				else if(event.getCode() == KeyCode.SLASH) {
					event.consume();
				}
			}
			
		});
		/* bouton pour envoyer un message instantane */
		sendChatButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				envoyerMessageChat();
			}
		});

		/* bouton de deconnexion */
		logoutButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		/* bouton pour encherir ou annoncer qu'on a une solution */
		trouveEnchereButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				envoyerEnchere();
			}
		});
		/* taper ENTREE pour encherir ou annoncer qu'on a une solution */
		coupTextField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER)
					envoyerEnchere();
			}
		});
		/* bouton pour envoyer une solution */
		solutionButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				envoyerSolution();
			}
		});
		/* saisie d'une solution au clavier avec les fleches directionnelles*/
		solutionTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				event.consume();
				KeyCode key = event.getCode();

				if (taperCouleurRobot) {
					switch (key) {
					case R:
						currentSolution += "R";
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case B:
						currentSolution += "B";
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case J:
						currentSolution += "J"; 
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case V:
						currentSolution += "V"; 
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case DELETE:
						if (currentSolution.length() > 0) {
							taperCouleurRobot = !taperCouleurRobot;
							currentSolution = currentSolution.substring(0, currentSolution.length()-1);
						}
						break;
					case BACK_SPACE:
						if (currentSolution.length() > 0) {
							taperCouleurRobot = !taperCouleurRobot;
							currentSolution = currentSolution.substring(0, currentSolution.length()-1);
						}
						break;
					case ENTER:
						envoyerSolution();
						break;
					default:
					}
				}
				else {
					switch(key) {
					case UP:
						currentSolution += "H"; 
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case DOWN:
						currentSolution += "B";
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case LEFT:
						currentSolution += "G";
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case RIGHT:
						currentSolution += "D";
						taperCouleurRobot = !taperCouleurRobot;
						break;
					case DELETE:
						if (currentSolution.length() > 0) {
							taperCouleurRobot = !taperCouleurRobot;
							currentSolution = currentSolution.substring(0, currentSolution.length()-1);
						}
						break;
					case BACK_SPACE:
						if (currentSolution.length() > 0) {
							taperCouleurRobot = !taperCouleurRobot;
							currentSolution = currentSolution.substring(0, currentSolution.length()-1);
						}
						break;
					default:
					}
				}
				solutionTextArea.setText(currentSolution);
			}
		});
	}

	/**
	 * Initialise les sons utilise par le client
	 * @return true si les sons sont bien initialises, false sinon
	 */
	private boolean initMedia() {
		/* ne marche que sur ma machine, exception levee a la ppti */
		try {
			correctSound = new Media(new File(CORRECT).toURI().toString());
			wrongSound = new Media(new File(WRONG).toURI().toString());
			chatSound = new Media(new File(CHAT).toURI().toString());
			enchereSound = new Media(new File(ENCHERE).toURI().toString());
			loginSound = new Media(new File(LOGIN).toURI().toString());
			logoutSound = new Media(new File(LOGOUT).toURI().toString());
			reflexionSound = new Media(new File(REFLEXION).toURI().toString());
			resolutionSound = new Media(new File(RESOLUTION).toURI().toString());
			correctMediaPlayer = new MediaPlayer(correctSound);
			wrongMediaPlayer = new MediaPlayer(wrongSound);
			chatMediaPlayer = new MediaPlayer(chatSound);
			enchereMediaPlayer = new MediaPlayer(enchereSound);
			loginMediaPlayer = new MediaPlayer(loginSound);
			logoutMediaPlayer = new MediaPlayer(logoutSound);
			reflexionMediaPlayer= new MediaPlayer(reflexionSound);
			resolutionMediaPlayer= new MediaPlayer(resolutionSound);
			return true;
		}
		catch (MediaException me) {
			System.err.println("Probleme avec l'initialisation des sons");
//			me.printStackTrace();
			return false;
		}
	}

	/**
	 * Essaie de se connecter sur un serveur
	 * @param username le nom d'utilisateur choisi
	 * @param host l'adresse du serveur
	 * @param actionTarget le label permettant d'afficher un message d'erreur
	 * @return la socket si la connexion est reussie, null sinon
	 */
	private Socket connexion(String username, String host, Text actionTarget)  {
		boolean ok = Outils.checkHostAndCheckUsername(username, host, actionTarget);
		if (ok) {
			this.userName = username;
			try {
				if (socket == null) {
					this.socket = new Socket(host, Protocole.PORT);
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					this.out = new PrintStream(socket.getOutputStream(), true);
				}
				Protocole.connect(userName, out);
				String reponse = in.readLine();
				if (reponse.equals(Protocole.BIENVENUE+"/"+username+"/")) { 

					serverAnswer.appendText("Bienvenue "+userName+"\n");
					if (!isAudioReady) {
						serverAnswer.appendText("Effets sonores non disponibles.\n");
						serverAnswer.appendText("Lisez le manuel pour resoudre le probleme\n");
					}
					sendChatButton.setDisable(false);
					sendChatTextArea.setDisable(false);
					userTextField.setEditable(false);
					hostTextField.setEditable(false);
					logoutButton.setDisable(false);
					loginButton.setVisible(false);
					errorMessageText.setVisible(false);
					bilan = new Bilan();
					this.receiver = new Receive();
					receiver.start();
				}
				else {
					actionTarget.setText(username+" deja utilise");
					actionTarget.setFill(Color.FIREBRICK);
					return null;
				}
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

	/**
	 * Permet de signaler au serveur que le client se deconnecte, et de liberer
	 * les ressources audio si besoin
	 */
	private void deconnexion() {
		if(out != null) {
			Protocole.disconnect(userName, out);
			if (isAudioReady) {
				correctMediaPlayer.dispose();
				wrongMediaPlayer.dispose();
				chatMediaPlayer.dispose();
				enchereMediaPlayer.dispose();
				loginMediaPlayer.dispose();
				logoutMediaPlayer.dispose();
				reflexionMediaPlayer.dispose();
				resolutionMediaPlayer.dispose();
			}
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Permet d'envoyer un message instantanne
	 */
	private void envoyerMessageChat() {
		String msg = sendChatTextArea.getText();
		Protocole.sendChat(userName, msg, out);
		updateChat("Me : "+msg);
		sendChatTextArea.setText("");
		sendChatTextArea.setPromptText("Enter a message ...");
	}

	/**
	 * Permet d'envoyer une enchere au serveur
	 */
	private void envoyerEnchere() {
		if (phase == Phase.REFLEXION) {
			errorLabel.setText("");
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
			errorLabel.setText("");
			String coups = coupTextField.getText();
			coupTextField.setText("");
			if(coups.matches("\\d+")) {
				Protocole.sendEnchere(userName, coups, out);
				tuEnchere = true;
			}
			else {
				errorLabel.setText("Veuillez saisir un nombre");
				errorLabel.setTextFill(Color.FIREBRICK);
			}
		}
		else {
			System.err.println("[envoyerEnchere] Hors phase reflexion ou enchere");
		}		
	}

	/**
	 * Permet d'envoyer une solution au serveur
	 */
	private void envoyerSolution() {
		if (phase == Phase.RESOLUTION) {
			String deplacements = solutionTextArea.getText();
			currentSolution = "";
			if (Outils.isValidSolution(deplacements)) {
				Protocole.sendSolution(userName,deplacements,out);
				solutionTextArea.setText("");
				solutionButton.setDisable(true);
				solutionTextArea.setDisable(true);
				solutionVBox.setVisible(false);
			}
			else {
				System.err.println("[envoyerSolution] solution mal formee");
			}
		}
		else {
			System.err.println("[envoyerSolution] Hors Phase RESOLUTION");
		}
	}

	/**
	 * Fonction principale de traitement d'une requete du serveur
	 * @param reponse la requete du serveur
	 */
	private void traitementReponseServeur(String reponse) {
		String commande = Outils.getCommandeName(reponse);
		String user, message;
		switch (commande) {
		case Protocole.RECEIVE_CHAT:
			user = Outils.getFirstArg(reponse);
			message = Outils.getSecondArg(reponse);
			if (!user.equals(userName)) {
				if (isAudioReady) {
					chatMediaPlayer.play();
					chatMediaPlayer.seek(Duration.ZERO);
				}
				updateChat(user+" : "+message);
			}
			break;
		case Protocole.CONNECTE:
			user = Outils.getFirstArg(reponse);
			if (isAudioReady) {
				loginMediaPlayer.play();
				loginMediaPlayer.seek(Duration.ZERO);
			}
			updateServerAnswer(user+" s'est connecte");
			break;
		case Protocole.DECONNEXION:
			user = Outils.getFirstArg(reponse);
			if (isAudioReady) {
				logoutMediaPlayer.play();
				logoutMediaPlayer.seek(Duration.ZERO);
			}
			updateServerAnswer(user+" s'est deconnecte");
			break;
		default:
			traitementReponseServerSync(reponse);
			break;
		}
	}

	/**
	 * Fonction de traitement d'une requete du serveur hors notification
	 * chat ou connexion/deconnexion d'un utilisateur
	 * @param reponse la requete du serveur
	 */
	private synchronized void traitementReponseServerSync(String reponse) {
		System.out.println("Traitement : "+reponse+"\n\n\n");
		String commande = Outils.getCommandeName(reponse);
		String user, data, enigme;
		switch (commande) {
		case Protocole.BIENVENUE:
			System.err.println("["+Protocole.BIENVENUE+"] Je ne dois pas passer ici");
			break;
		case Protocole.SESSION:
			data = Outils.getFirstArg(reponse);
			phase = Phase.ATTENTE_TOUR;
			updatePhaseLabel(phase);
			updateServerAnswer("Debut Session");
			if (bilan != null) {
				bilan.reset();
			}
			if (plateau != null) {
				plateau.reset();
			}
			this.plateau = new Plateau(data);
			updatePlateau();
			break;
		case Protocole.VAINQUEUR:
			data = Outils.getFirstArg(reponse);
			phase = Phase.ATTENTE_TOUR;
			updatePhaseLabel(phase);
			if (plateau != null) {
				plateau.reset();
			}
			updatePlateau();
			updateServerAnswer("Fin de la session");
			bilan.decoderBilan(data);
			updateBilan();
			break;
		case Protocole.TOUR:
			enigme = Outils.getFirstArg(reponse);
			data= Outils.getSecondArg(reponse);
			bilan.decoderBilan(data);
			updateBilan();
			this.plateau.enleverRobots();
			updatePlateau();
			this.enigme = new Enigme(enigme);
			this.plateau.setEnigme(this.enigme);
			if (phase == Phase.ATTENTE_TOUR) {
				phase = Phase.REFLEXION;
				if (isAudioReady) {
					reflexionMediaPlayer.play();
					reflexionMediaPlayer.seek(Duration.ZERO);
				}
				updatePhaseLabel(phase);
				Platform.runLater(new Runnable() {			
					@Override
					public void run() {
						trouveEnchereButton.setText("Trouve");
						trouveEnchereButton.setDisable(false);
						coupTextField.setDisable(false);
						coupTextField.requestFocus();
						errorLabel.setText("");
						coupsSolutionLabel.setText("");
					}
				});
				updatePlateau();
				updateServerAnswer("La phase de reflexion commence");
			}
			else {
				System.err.println("["+Protocole.TOUR+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.TU_AS_TROUVE:
			if (phase == Phase.REFLEXION && tuAsTrouve) {
				phase = Phase.ENCHERE;
				updatePhaseLabel(phase);
				updateServerAnswer("Votre annonce est validee !\nLa phase d'enchere commence");
				if (isAudioReady) {
					enchereMediaPlayer.play();
					enchereMediaPlayer.seek(Duration.ZERO);
				}
				updateTrouveEnchereButton("Encherir");
				coupTextField.requestFocus();
				tuAsTrouve = false;
			}
			else {
				System.err.println("["+Protocole.TU_AS_TROUVE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.IL_A_TROUVE:
			if (phase == Phase.REFLEXION) {
				phase = Phase.ENCHERE;
				updatePhaseLabel(phase);
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				updateServerAnswer(user+" a trouve en "+data+" coups !\nLa phase d'enchere commence");
				if (isAudioReady) {
					enchereMediaPlayer.play();
					enchereMediaPlayer.seek(Duration.ZERO);
				}
				updateTrouveEnchereButton("Encherir");
				coupTextField.requestFocus();
				tuAsTrouve = false;
			}
			else {
				System.err.println("["+Protocole.IL_A_TROUVE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_REFLEXION:
			if (phase == Phase.REFLEXION) {
				phase = Phase.ENCHERE;		
				updatePhaseLabel(phase);
				updateServerAnswer("Expiration du delai imparti a la reflexion");
				if (isAudioReady) {
					enchereMediaPlayer.play();
					enchereMediaPlayer.seek(Duration.ZERO);
				}
				updateServerAnswer("La phase d'enchere commence");
				updateTrouveEnchereButton("Encherir");
				coupTextField.requestFocus();
				trouveEnchereButton.setDisable(false);
			}
			else {
				System.err.println("["+Protocole.FIN_REFLEXION+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.VALIDATION:
			if (phase == Phase.ENCHERE && tuEnchere) {
				updateServerAnswer("Enchere validee");
				tuEnchere = false;				
			}
			else {
				System.err.println("["+Protocole.VALIDATION+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.ECHEC:
			if (phase == Phase.ENCHERE && tuEnchere) {
				user = Outils.getFirstArg(reponse);
				updateServerAnswer("Enchere annulee car incoherente avec celle de "+user);
				tuEnchere = false;				
			}
			else {
				System.err.println("["+Protocole.ECHEC+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.NOUVELLE_ENCHERE:
			if (phase == Phase.ENCHERE) {
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				updateServerAnswer(user+" a encheri avec "+data+" coups");
			}
			else {
				System.err.println("["+Protocole.NOUVELLE_ENCHERE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_ENCHERE:
			if (phase == Phase.ENCHERE) {
				phase = Phase.RESOLUTION;
				updatePhaseLabel(phase);
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				if (!user.equals("null")) {
					lastActif = user;
					updateServerAnswer("La phase de resolution commence");
					if (isAudioReady) {
						resolutionMediaPlayer.play();
						resolutionMediaPlayer.seek(Duration.ZERO);
					}
					trouveEnchereButton.setDisable(true);
					coupTextField.setDisable(true);
					if (!user.equals(userName)) {
						if (!user.equals("")) {
							updateServerAnswer("Le joueur actif est "+user);
						}
					}
					else {
						updateServerAnswer("Taper votre solution dans la zone ci-dessus\n"+
								"Touches autorisees : r, b, j, v\n"+
								"et fleches directionnelles");
						solutionButton.setDisable(false);
						solutionTextArea.requestFocus();
						solutionTextArea.setDisable(false);
						solutionVBox.setVisible(true);
						taperCouleurRobot = true;
						currentSolution = "";
					}
				}
			}
			else {
				System.err.println("["+Protocole.FIN_ENCHERE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.SA_SOLUTION:
			if (phase == Phase.RESOLUTION && attenteStatutSolution == false) {
				user = Outils.getFirstArg(reponse);
				data = Outils.getSecondArg(reponse);
				if (!user.equals(userName)) {
					updateServerAnswer(user+" a propose la solution suivante");
				}
				else {
					updateServerAnswer("Vous avez propose la solution suivante");
				}
				attenteStatutSolution = true;
				startAnimation(data);
			}
			else {
				System.err.println("["+Protocole.SA_SOLUTION+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.BONNE:
			if (phase == Phase.RESOLUTION && attenteStatutSolution) {
				if (isAudioReady) {
					correctMediaPlayer.play();
					correctMediaPlayer.seek(Duration.ZERO);
				}
				updateServerAnswer("Solution correcte, Fin du tour");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						solutionTextArea.setText("");						
					}
				});
				attenteStatutSolution = false;
				phase = Phase.ATTENTE_TOUR;
				updatePhaseLabel(phase);
			}
			else {
				System.err.println("["+Protocole.BONNE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.MAUVAISE:
			if (phase == Phase.RESOLUTION && attenteStatutSolution) {
				user = Outils.getFirstArg(reponse);
				attenteStatutSolution = false;
				lastActif = user;
				updateServerAnswer("Solution refusee");
				if (isAudioReady) {
					wrongMediaPlayer.play();
					wrongMediaPlayer.seek(Duration.ZERO);
				}
				plateau.initPositionsRobots();
				updatePlateau();
				if (!user.equals(userName)) {
					updateServerAnswer("Le joueur actif est "+user);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							coupsSolutionLabel.setText("");
						}
					});
				}
				else {
					updateServerAnswer("Taper votre solution dans la zone ci-dessus\n"+
							"Touches autorisees : r, b, j, v\n"+
							"et fleches directionnelles");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							solutionTextArea.setText("");
							solutionButton.setDisable(false);
							solutionTextArea.setDisable(false);
							solutionTextArea.requestFocus();
							solutionVBox.setVisible(true);
							coupsSolutionLabel.setText("");
						}
					});
					taperCouleurRobot = true;
					currentSolution = "";
				}

			}
			else {
				System.err.println("["+Protocole.MAUVAISE+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.FIN_RESOLUTION:
			if (phase == Phase.RESOLUTION || phase == Phase.REFLEXION) {
				updateServerAnswer("Solution refusee");
				updateServerAnswer("Plus de joueurs restants, Fin du tour");
				attenteStatutSolution = false;
				if (wrongMediaPlayer != null)
					wrongMediaPlayer.play();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						solutionTextArea.setText("");						
					}
				});
				phase = Phase.ATTENTE_TOUR;
				updatePhaseLabel(phase);
				if (lastActif.equals(userName)) {
					solutionButton.setDisable(true);
					solutionTextArea.setDisable(true);
					solutionVBox.setVisible(false);
					taperCouleurRobot = true;
					currentSolution = "";
				}
			}
			else {
				System.err.println("["+Protocole.FIN_RESOLUTION+"] Je ne dois pas passer ici");
			}
			break;
		case Protocole.TROP_LONG:
			if (phase == Phase.RESOLUTION) {
				attenteStatutSolution = false;
				if (isAudioReady) {
					wrongMediaPlayer.play();
					wrongMediaPlayer.seek(Duration.ZERO);
				}
				user = Outils.getFirstArg(reponse);
				updateServerAnswer("Temps depasse");
				if (lastActif.equals(userName)) {
					solutionButton.setDisable(true);
					solutionTextArea.setDisable(true);
					solutionVBox.setVisible(false);
					taperCouleurRobot = true;
					currentSolution = "";
				}
				else {
					lastActif = user;
					if (!user.equals(userName)) {
						updateServerAnswer("Le joueur actif est "+user);
					}
					else {
						updateServerAnswer("Taper votre solution dans la zone ci-dessus\n"+
								"Touches autorisees : r, b, j, v\n"+
								"et fleches directionnelles");
						solutionButton.setDisable(false);
						solutionTextArea.requestFocus();
						solutionTextArea.setDisable(false);
						solutionVBox.setVisible(true);
						taperCouleurRobot = true;
						currentSolution = "";
					}
				}
			}
			else {
				System.err.println("["+Protocole.TROP_LONG+"] Je ne dois pas passer ici");
			}
			break;
		default:
			System.err.println("[default] "+reponse);
			break;
		}
	}

	/**
	 * Permet de lancer une animation de huit secondes d'une solution proposee 
	 * @param data la solution a afficher sur le plateau
	 */
	private void startAnimation(String data) {
		ArrayList<String> coups = Outils.getCoups(data);
		/* pre-calcul de l'intervalle de temps necessaire entre chaque 
		 * deplacement d'une case pour avoir une animation totale de huit
		 * secondes */
		(new Runnable() {
			@Override
			public void run() {
				Plateau copiePlateau = new Plateau(plateau.toString());
				Enigme copieEnigme = new Enigme(plateau.getEnigme().toString());
				copiePlateau.setEnigme(copieEnigme);

				int nbMove = 1;
				for (String coup : coups) {
					while(copiePlateau.move(coup)) {
						nbMove++;
					}
				}
				interv = 7.6*1000/nbMove;
			}
		}).run();

		/* Debut effectif de l'animation */
		int nbCoups = 1;
		for(String coup : coups) {
			while (plateau.move(coup)) {
				try {
					Thread.sleep((long) interv);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updatePlateauAnim();
					}
				});
			}
			updateNombreCoupsSolution(nbCoups);
			nbCoups++;
		}
	}

	/**
	 * Permet de recuperer l'heure pour la messagerie instantannee
	 * @return une chaine de caractere de l'heure
	 */
	private String getTime() {
		calendar = Calendar.getInstance();
		return "["+dateFormat.format(calendar.getTime())+"] ";
	}

	/**
	 * Mise a jour visuelle du tableau des scores et du numero de tour courant
	 */
	private void updateBilan() {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				int tour = bilan.getTour();
				tourLabel.setText("Tour "+tour);
				scoreTableView.setItems(bilan.getScoreSheet());
			}
		});
	}

	/**
	 * Permet d'afficher les messages instantannes recus
	 * @param msg le message a afficher
	 */
	private void updateChat(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatTextArea.appendText(getTime()+msg+"\n");
			}
		});
	}

	/**
	 * Met a jour visuellement le nombre de coups actuel d'une solution en 
	 * train d'etre affichee sur le plateau
	 * @param nbCoups le nombre de coups de la solution
	 */
	private void updateNombreCoupsSolution(int nbCoups) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				coupsSolutionLabel.setText(nbCoups+" coups");
				coupsSolutionLabel.setTextFill(Color.BLUEVIOLET);
			}
		});
	}

	/**
	 * Permet de mettre a jour l'affichage de la phase courante
	 * @param phase la nouvelle phase
	 */
	private void updatePhaseLabel(Phase phase) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String s = "";
				switch(phase) {
				case ATTENTE_TOUR:
					s = "ATTENTE DU TOUR SUIVANT";
					break;
				case ENCHERE:
					s = "PHASE D'ENCHERE";
					break;
				case REFLEXION:
					s = "PHASE DE REFLEXION";
					break;
				case RESOLUTION:
					s = "PHASE DE RESOLUTION";
					break;
				}
				phaseLabel.setText(s);
			}
		});
	}

	/**
	 * Permet de mettre a jour l'affichage du plateau 
	 */
	private void updatePlateau(){
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

	/**
	 * Permet de mettre a jour l'affichage du plateau a chaque deplacement
	 * de case d'un robot lors de l'animation d'une solution
	 */
	private void updatePlateauAnim(){
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

	/**
	 * Permet d'afficher un message du serveur 
	 * @param msg le message a afficher
	 */
	private  void updateServerAnswer(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				serverAnswer.appendText(msg+"\n");
			}
		});

	}

	/**
	 * Modifie le texte du bouton trouveEnchere
	 * @param text le texte a afficher pour le bouton
	 */
	private void updateTrouveEnchereButton(String text) {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				trouveEnchereButton.setText(text);
			}
		});
	}

	/**
	 * 
	 * @author Ladislas Halifa
	 * Service toujours a l'ecoute du serveur, lance une nouvelle Thread
	 * qui va traiter la commande recue.
	 */
	class Receive extends Service<Void> {
		String recu;
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {

					try {
						while ((recu = in.readLine()) != null) {
							System.out.println("Reception : "+recu.length()+" - "+recu);
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									traitementReponseServeur(recu);									
								}
							});
							t.start();
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
