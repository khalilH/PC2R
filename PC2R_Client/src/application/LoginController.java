package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class LoginController {

	
	
    @FXML
    private TextField userTextField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField hostTextField;

    @FXML
    private GridPane mainFrame;
    
    @FXML
    private Text errorMessageText;

}
