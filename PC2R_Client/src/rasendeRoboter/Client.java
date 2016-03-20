package rasendeRoboter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

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
//					recu = in.readLine();
					decoderCommandeServer(recu);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	private static final int PORT = 0;
	
	
	
	private Socket socket;
	private final String user;
	private BufferedReader in;
	private PrintStream out;
	private Scanner sc = new Scanner(System.in);
	private Plateau plateau;
	private Enigme enigme;
	private Receive receiver;

	//TODO Thread envoie + Thread reception
	
	public Client() {
		user = "ladi";
	}

	public static void main(String[] args) {
		Client client = new Client();
		if (args.length != 1) {
			System.err.println("Usage: java Client <hote>");
			System.exit(1);			
		}
		try {
			client.connect(args[0]);
			client.boucle();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void connect(String host) throws UnknownHostException, IOException {
		this.socket = new Socket(host, PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintStream(socket.getOutputStream(), true);
		receiver = new Receive(in);
		receiver.start();
		out.print("CONNEX/"+user+"/\n");
	}

	public void logout(String user, BufferedReader in, PrintStream out) {
		out.print("SORT/"+user+"/\n");
	}

	public void boucle() throws IOException {
		String fromClient;
		while((fromClient= sc.nextLine()) != null) {
			if (fromClient.equals("QUIT")) {
				logout(user,in,out);
				break;
			}
			out.print(fromClient+"\n");
		}
		sc.close();
		socket.close();
	}
	
	public void boucleSession() throws IOException {
		String fromServer, fromClient;
		String cmds[];
		while((fromServer = in.readLine()) != null) {
			cmds = fromServer.split("/");
			if (cmds[0].equals("VAINQUEUR")) {
				break;
			}
			decoderCommandeServer(fromServer);
			fromClient = sc.nextLine();
			out.print(fromClient+"\n");
		}
	}


	public void decoderCommandeServer(String commande) {
		String [] cmds = commande.split("/");
		if (cmds[0].equals("BIENVENUE") && cmds.length == 2) {
			System.out.println("Connexion reussie");
		}
		else if (cmds[0].equals("CONNECTE") && cmds.length == 2) {
			String user = cmds[1];
			System.out.println(user+" s'est connecte" );
		}
		else if (cmds[0].equals("SORTI") && cmds.length == 2) {
			String user = cmds[1];
			System.out.println(user+" s'est deconnecte");
		}
		else if (cmds[0].equals("SESSION") && cmds.length == 2) {
			String descriptionPlateau = cmds[1];
			plateau = new Plateau(descriptionPlateau);
			System.out.println(plateau);
		}
		else{
			System.out.println("Commande inconnue....");
		}
	}


}