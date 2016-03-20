package application;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Client extends Application {

	URL location = getClass().getResource("fxml_client.fxml");
	private StringProperty chatText = new SimpleStringProperty(this, "chatText", "");
	public final String getFirstName() { return chatText.get(); }
	public final void setFirstName(String value) { chatText.set(value); }
	public final StringProperty firstNameProperty() { return chatText; }

	@Override
	public void start(Stage stage) {
		BorderPane root;
		//		FXMLLoader fxmlLoader = new FXMLLoader(location);
		try {
			root = (BorderPane) FXMLLoader.load(getClass().getResource("fxml_client.fxml"));
			//			root = (BorderPane) fxmlLoader.load();

			GridPane plateau = (GridPane) root.getCenter();
			ClientController clientController = getClientController();

			for (int i = 0; i<16 ; i++) {

				for (int j = 0; j<16 ; j++) {

					BorderPane casePlateau = buildG();
					if (i==1 && j == 1) {
						casePlateau.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {

							@Override
							public void handle(Event event) {
								clientController.sendMessage(new KeyEvent(KeyEvent.KEY_PRESSED, "d", "je sais pas", KeyCode.ENTER, false, false, false, false));
//								clientController.hello("Bonjour ladi");

							}
						});
					}
					GridPane.setRowIndex(casePlateau, i);
					GridPane.setColumnIndex(casePlateau, j);
					plateau.getChildren().add(casePlateau);
				}
			}
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("FXML");
			stage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	public static void main(String[] args) {
		launch(args);
	}
}
