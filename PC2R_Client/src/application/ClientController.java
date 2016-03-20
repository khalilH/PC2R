package application;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientController implements Initializable{
	@FXML private BorderPane mainFrame;
	@FXML private TextArea sendChatTextArea;
	@FXML private TextArea chatTextArea;
	@FXML private Label hostAdressLabel;
	@FXML private Label test;
	private Client client;

	@FXML protected void disconnect(MouseEvent event) {
		//		client.disconnect();
		System.out.println("Deconnexion");
	}



	@FXML protected void sendMessage(KeyEvent event) {
		event.consume();
		if (event.getCode() == KeyCode.ENTER) {
			//TODO promptText ne se remet pas
			chatTextArea.appendText("Recu : "+sendChatTextArea.getText()+"\n");
			sendChatTextArea.setText("");
			sendChatTextArea.setPromptText("Enter a message");		
		}
	}

	@FXML protected void sendMessageWithButton(ActionEvent event) {
		chatTextArea.appendText("Recu : "+sendChatTextArea.getText()+"\n");
		sendChatTextArea.setText("");
		sendChatTextArea.setPromptText("Enter a message");
		//client.getOut(truc);
	}

	protected void hello(String txt) {
		chatTextArea.appendText(txt+"\n");
		test.setText("lu = "+txt);
		System.out.println(txt);	
	}

	//	public void init(Client client) {
	//		this.client = client;
	//	}





	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		if (client == null) 
			System.out.println("HELLO");
		else
			chatTextArea.textProperty().bindBidirectional(client.firstNameProperty());

	}



	public void init(Client client2) {
		this.client = client2;

	}



	public void send(PrintStream out, String string) {
		String cmd = "CHAT/ladi/"+string+"\n";
		out.print(cmd);
		// TODO Auto-generated method stub
		
	}  
}