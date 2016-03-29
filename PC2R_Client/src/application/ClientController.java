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

/**
 * 
 * @author Ladislas Halifa
 * Controlleur de la vue Client
 */
public class ClientController {
	/**
	 * Conteneur principal
	 */
	@FXML private BorderPane mainFrame;
	
	/**
	 * zone de saisie du chat
	 */
	@FXML private TextArea sendChatTextArea;
	
	/**
	 * zone d'affichage des messages du chat
	 */
	@FXML private TextArea chatTextArea;
	
	/**
	 * zone d'affichage de l'adresse du serveur
	 */
	@FXML private Label hostAdressLabel;
	

	@FXML private Label version;
	
	/**
	 * Bouton de deconnexion
	 */
	@FXML private Button logoutButton;
	
	/**
	 * zone d'affichage du plateau de jeu
	 */
	@FXML private GridPane plateauGrid;
	
	/**
	 * Bouton pour Encherir ou annonce que l'on a trouve une solution
	 */
	@FXML private Button trouveEnchereButton;

	/**
	 * zone de saisie du nombre de coups
	 */
	@FXML private TextField coupTextField;
    
	/**
	 * zone d'affichage d'erreur sur la saisie d'un nombre de coup
	 */
	@FXML private Label errorLabel;
    
	/**
	 * zone d'affichage du numero du tour actuel
	 */
    @FXML private Label tourLabel;
    
    /**
     * zone d'affichage des score
     */
    @FXML private TableView<Score> scoreTableView;
    
    /**
     * zone de saisie et d'affichage d'une solution en cours de saisie
     */
    @FXML private TextArea solutionTextArea;
    
    /**
     * Bouton pour soumettre une solution
     */
    @FXML private Button solutionButton;

    /**
     * Zone d'affichage des messages recus
     */
    @FXML private TextArea serverAnswer;
    
    /**
     * Conteneur de la zone de saisie d'une solution
     */
    @FXML private VBox solutionVBox;
}