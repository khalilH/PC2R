package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * 
 * @author Ladislas Halifa
 * Controlleur de la vue Login
 */
public class LoginController {

	/**
	 * zone de saisie du nom d'utilisateur
	 */
    @FXML private TextField userTextField;

    /**
     * bouton de connexiont
     */
    @FXML private Button loginButton;

    /**
     * zone de saisie de l'adresse du serveur
     */
    @FXML private TextField hostTextField;

    /**
     * Conteneur principal
     */
    @FXML private GridPane mainFrame;
    
    /**
     * zone d'affichage d'un message d'erreur
     */
    @FXML private Text errorMessageText;

}
