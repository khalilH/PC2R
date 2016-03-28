package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import rasendeRoboter.Bilan.Score;

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
    
    @FXML
    private Label tourLabel;
    
    @FXML
    private TableView<Score> scoreTableView;
    
    @FXML
    private TextField solutionTextField;
    
    @FXML
    private TextArea solutionTextArea;
    
    @FXML
    private Button solutionButton;

    @FXML
    private TextArea serverAnswer;
    
    @FXML
    private VBox solutionVBox;
}