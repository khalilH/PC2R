package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class ClientController {
	@FXML private BorderPane mainFrame;
	@FXML private TextArea sendChatTextArea;
	@FXML private TextArea chatTextArea;
	@FXML private Label hostAdressLabel;
	@FXML private Label version;
	@FXML private Button logoutButton;
	@FXML private GridPane plateauGrid;
	@FXML
    private Button trouveEnchereButton;

    @FXML
    private TextField coupTextField;
    @FXML
    private Label errorLabel;

}