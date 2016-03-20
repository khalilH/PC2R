package interf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import rasendeRoboter.Outils;

public class FenetreClient extends JFrame  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final int PORT=2016;
	private static String host = "localhost";

	private JPanel topPanel, botPanel, sidePanel, chatPanel, scorePanel;
	private PlateauActivePanel centerPanel;
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private Receive receiver;
	private final String username;
	private JTextArea serverAnswer;

	//TODO ajouter bouton Deconnexion

	public FenetreClient() {
		super("Rasende Roboter");
		setSize(900, 600);
		setLayout(new BorderLayout(10, 10));
		//TODO setResizable
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		createTopPanel();
		createBotPanel();
		username = connexion();
		if (socket != null) {			
			setVisible(true);
		}
	}

	private void createTopPanel() {
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(10,10));
		topPanel.setPreferredSize(new Dimension (800,400));
		createSidePanel();
		createCenterPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
	}

	private void createBotPanel() {
		botPanel = new JPanel();
		botPanel.setLayout(new BorderLayout(5,5));
		botPanel.setPreferredSize(new Dimension(800,200));
		botPanel.setBorder(new EmptyBorder(5,5,5,5));
		createChatPanel();
		createScorePanel();
		getContentPane().add(botPanel, BorderLayout.CENTER);
	}

	private void createSidePanel() {
		sidePanel = new JPanel();
		sidePanel.setBackground(Color.gray);
		sidePanel.setLayout(new BorderLayout());
		sidePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePanel.setPreferredSize(new Dimension(300,400));

		serverAnswer= new JTextArea();
		serverAnswer.setLineWrap(true);
		JScrollPane ScrollPanel = new JScrollPane(serverAnswer,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		ScrollPanel.setPreferredSize(new Dimension(300,100));
		sidePanel.add(ScrollPanel,BorderLayout.SOUTH);

		topPanel.add(sidePanel, BorderLayout.WEST);
	}

	private void createCenterPanel() {
		centerPanel = new PlateauActivePanel(null);
		centerPanel.setBackground(Color.blue);
		topPanel.add(centerPanel,BorderLayout.CENTER);
	}

	private void createChatPanel() {
		chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout(5,5));
		chatPanel.setPreferredSize(new Dimension(300,200));

		/* Affichage des messages du chat */
		JTextArea chatTextArea = new JTextArea();
		chatTextArea.setLineWrap(true);
		chatTextArea.append("ladicvbcfffffffffffffffffffvbv\nladi\nladi\nladi\nladi\n");		
		JScrollPane chatScrollPanel = new JScrollPane(chatTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		chatScrollPanel.setPreferredSize(new Dimension(300,100));
		chatPanel.add(chatScrollPanel,BorderLayout.NORTH);


		/* Zone de saisie de messages */
		JTextArea saisieTextArea = new JTextArea();
		saisieTextArea.setLineWrap(true);
		saisieTextArea.setBackground(Color.yellow);
		JScrollPane saisieScrollPanel = new JScrollPane(saisieTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		saisieScrollPanel.setPreferredSize(new Dimension(210,20));
		chatPanel.add(saisieScrollPanel,BorderLayout.WEST);

		JButton button = new JButton("Send");
		chatPanel.add(button,BorderLayout.CENTER);

		botPanel.add(chatPanel,BorderLayout.WEST);
	}

	private void createScorePanel() {
		scorePanel = new JPanel();
		scorePanel.setBackground(Color.green);
		scorePanel.setPreferredSize(new Dimension(900,200));
		botPanel.add(scorePanel,BorderLayout.CENTER);
	}

	public String connexion()  {
		String username = null;
		while (!Outils.checkUsername(username)) {
			username = JOptionPane.showInputDialog(null, "Veuillez choisir votre identité", "Connexion", JOptionPane.QUESTION_MESSAGE);
		}
		System.out.println("login "+username);
		try {
			this.socket = new Socket(host, PORT);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintStream(socket.getOutputStream(), true);
			this.receiver = new Receive(in);
			receiver.start();
			out.print("CONNEX/"+username+"/\n");

		}catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, "Erreur Serveur non disponible", "Probleme",JOptionPane.WARNING_MESSAGE);
			this.dispose();
		} 
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Erreur Serveur non disponible", "Probleme",JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//TODO ouvrir socket
		//TODO send message to server
		return username;
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		FenetreClient fenetre = new FenetreClient();
	}


	public void decoderCommandeServer(String commande) {
		System.out.println(commande+" ; size = "+commande.length());
		String [] cmds = commande.split("/");
		if (cmds.length == 2 && cmds[0].equals("BIENVENUE") &&  username.equals(cmds[1])) {
			System.out.println("Connexion reussie");
			serverAnswer.append("Connexion reussie\n");
		}
		else if (cmds.length == 2 && cmds[0].equals("CONNECTE") && !username.equals(cmds[1])) {
			String user = cmds[1];
			System.out.println(user+" s'est connecte" );
			serverAnswer.append(user+" s'est connecte\n" );
		}
		else if (cmds.length == 2 &&cmds[0].equals("SORTI") ) {
			String user = cmds[1];
			System.out.println(user+" s'est deconnecte");
			serverAnswer.append(user+" s'est deconnecte\n");
		}
		else{
			System.out.println("Commande inconnue.... "+commande);
			serverAnswer.append("Commande inconnue...."+commande+"\n");
		}
	}

	class Receive extends Thread {
		private BufferedReader in;

		public Receive(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			String recu;
			try {
				while ((recu = in.readLine()) != null) {
					//TODO voir pk null avec server pat
					//					System.out.println("attente");
					//					recu = in.readLine()
					decoderCommandeServer(recu);
					yield();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
