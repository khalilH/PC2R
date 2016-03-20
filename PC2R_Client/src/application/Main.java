package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ConnectException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rasendeRoboter.Outils;


public class Main extends Application {

	protected static final int PORT=2016;

	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private Receive receiver;
	private String userName;
	private String host;
	private Stage stage;
	private BorderPane root;
	private Scene scene2;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		initConnexionGUI(primaryStage);
		//TODO remettre initConnexionGUI quand initClientGUIfini
		//		initClientGUI(primaryStage);

	}  

	public HBox createTopBar() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);   // Gap between nodes
		hbox.setStyle("-fx-background-color: #116699;");
		Color.LIGHTGREY.toString();
		Text hostLabel= new Text("Connecte sur 132.125.133.199");
		hostLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
		hostLabel.setFill(Color.WHITE);
		Button boutonDeconnexion = new Button("Deconnexion");
		boutonDeconnexion.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(out != null) {
					out.print("SORT/"+userName+"/\n");
				}
				System.out.print("SORT/"+userName+"/\n");
			}
		});
		hbox.setAlignment(Pos.BASELINE_RIGHT);
		hbox.getChildren().addAll(hostLabel, boutonDeconnexion);
		return hbox;
	}

	public void initClientGUI(Stage stage) {
		//
		//		primaryStage.setTitle("Rasende Roboter");
		//		StackPane root = new StackPane();
		//		BorderPane border = new BorderPane();
		//		HBox topHbox = createTopBar();
		//		border.setTop(topHbox);
		//
		//
		//		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		//			@Override public void handle(WindowEvent t) {
		//				if(out != null) {
		//					out.print("SORT/"+userName+"/\n");
		//				}
		//				System.out.println("CLOSING");
		//			}
		//		});
		//		root.getChildren().add(border);
		//		primaryStage.setScene(new Scene(root,800,600));
		//		primaryStage.setX(100.0);
		//		primaryStage.setY(100.0);
		//		primaryStage.show();

		try {
			root = (BorderPane) FXMLLoader.load(getClass().getResource("fxml_client.fxml"));
			Scene scene = new Scene(root);
			GridPane plateau = (GridPane) root.getCenter();
			ClientController clientController = getClientController();
			//		clientController.init(this);

			for (int i = 0; i<16 ; i++) {

				for (int j = 0; j<16 ; j++) {

					BorderPane casePlateau = buildG();
					if (i==1 && j == 1) {
						casePlateau.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {

							@Override
							public void handle(Event event) {
							}
						});
					}
					GridPane.setRowIndex(casePlateau, i);
					GridPane.setColumnIndex(casePlateau, j);
					plateau.getChildren().add(casePlateau);
				}
			}
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override public void handle(WindowEvent t) {
					if(out != null) {
						out.print("SORT/"+userName+"/\n");
					}
					initConnexionGUI(stage);
				}
			});
			stage.setScene(scene);
			stage.setTitle("Rasende Roboter");
			this.stage = stage;
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public ClientController getClientController() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResource("fxml_client.fxml").openStream());
			ClientController clientController = (ClientController) fxmlLoader.getController();
			return clientController;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public BorderPane buildG() {
		BorderPane casePlateau = new BorderPane();
		casePlateau.setLeft(leftWall());
		return casePlateau;
	}

	public Pane leftWall() {
		Pane left = new Pane();
		left.setStyle("-fx-background-color: #000000;");
		left.setPrefSize(3.0, 15.0);
		return left;
	}


	public void initConnexionGUI(Stage primaryStage) {
		primaryStage.setTitle("Connexion");
		StackPane root = new StackPane();
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,25,25,25));

		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		Label userNameLabel = new Label("User Name");
		grid.add(userNameLabel, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label hostLabel= new Label("Serveur");
		grid.add(hostLabel, 0, 2);

		TextField hostTextField = new TextField("localhost");
		grid.add(hostTextField, 1, 2);

		final Text actionTarget = new Text();
		actionTarget.setTextAlignment(TextAlignment.CENTER);
		HBox bhAt = new HBox(10);
		bhAt.setAlignment(Pos.BOTTOM_RIGHT);
		bhAt.getChildren().add(actionTarget);
		grid.add(bhAt, 1, 6);
				grid.setGridLinesVisible(true); //TODO grid

		Button btn = new Button("Connexion");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				socket = connexion(userTextField.getText(), hostTextField.getText(),actionTarget);
				if (socket != null) {
					root.getChildren().clear();
					initClientGUI(primaryStage);
				}
			}
		});
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, 
				new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					socket = connexion(userTextField.getText(), hostTextField.getText(),actionTarget);
					if (socket != null) {
						root.getChildren().clear();
						initClientGUI(primaryStage);
					}
				}
			}
		});


		grid.add(hbBtn, 1, 4);
		root.getChildren().add(grid);
		//		primaryStage.setAlwaysOnTop(true);
		primaryStage.setScene(new Scene(root,400,300));
		primaryStage.show();

	}

	public void disconnect() {
		if(out != null) {
			out.print("SORT/"+userName+"/\n");
		}
		initConnexionGUI(stage);
	}

	public String getUserName() {
		return this.userName;
	}

	public String gethost() {
		return this.host;
	}

	public Socket connexion(String username, String host, Text actionTarget)  {
		boolean ok = Outils.checkHostAndCheckUsername(username, host, actionTarget);
		if (ok) {
			this.host= host;
			this.userName = username;
			//			System.out.println("login "+userName+" @ "+host);
			try {
				this.socket = new Socket(host, PORT);
				this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = new PrintStream(socket.getOutputStream(), true);
				this.receiver = new Receive();
				receiver.start();
				out.print("CONNEX/"+username+"/\n");
			}catch (ConnectException e) {
				e.printStackTrace();
				return null;
			} 
			catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return socket;
	}

	class Receive extends Thread {
		;

		public Receive() {
		}

		@Override
		public void run() {
			String recu;
			try {
				while ((recu = in.readLine()) != null) {
					//TODO voir pk null avec server pat
					decoderCommandeServer(recu);
					System.out.println(recu);
					yield();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Stage getStage() {
		// TODO Auto-generated method stub
		return stage;
	}

	public void decoderCommandeServer(String recu) {
		TextArea ta = (TextArea) root.lookup("#serverAnswer"); 
		ta.appendText(recu+"\n");
		// TODO Auto-generated method stub

	}
}
