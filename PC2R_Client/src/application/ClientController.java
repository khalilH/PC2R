package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class ClientController {
	@FXML private BorderPane mainFrame;
	@FXML private TextArea sendChatTextArea;
	@FXML private TextArea chatTextArea;
	@FXML private Label hostAdressLabel;
	@FXML private Label version;
	@FXML private Button logoutButton;

}