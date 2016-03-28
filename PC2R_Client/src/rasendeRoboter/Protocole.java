package rasendeRoboter;

import java.io.PrintStream;

public class Protocole {

	public static final int PORT = 2016;

	public static final String BIENVENUE = "BIENVENUE";
	public static final String CONNECTE = "CONNECTE";
	public static final String DECONNEXION = "DECONNEXION";
	
	public static final String RECEIVE_CHAT = "RECEIVE";
	
	public static final String SESSION = "SESSION";
	public static final String VAINQUEUR = "VAINQUEUR";
	
	public static final String TOUR = "TOUR";
	public static final String TU_AS_TROUVE = "TUASTROUVE";
	public static final String IL_A_TROUVE = "ILATROUVE";
	public static final String FIN_REFLEXION = "FINREFLEXION";
	
	public static final String VALIDATION = "VALIDATION";
	public static final String ECHEC = "ECHEC";
	public static final String NOUVELLE_ENCHERE = "NOUVELLEENCHERE";
	public static final String FIN_ENCHERE = "FINENCHERE";
	
	public static final String SA_SOLUTION = "SASOLUTION";
	public static final String BONNE = "BONNE";
	public static final String MAUVAISE = "MAUVAISE";
	public static final String FIN_RESOLUTION = "FINRESO";
	public static final String TROP_LONG = "TROPLONG";
	
	public static final String CONNEXION = "CONNEXION";
	public static final String SORT = "SORT";
	public static final String SEND_CHAT = "SEND";
	
	public static final String TROUVE = "TROUVE";
	
	public static final String ENCHERE = "ENCHERE";
	
	public static final String SOLUTION = "SOLUTION";

	public static void disconnect(String username, PrintStream out) {
		send(SORT, username, out);
	}

	public static void connect(String username, PrintStream out) {
		send(CONNEXION, username, out);
	}

	public static void sendChat(String username, String message, PrintStream out) {
		send(SEND_CHAT, username, message, out);
	}
	
	public static void sendTrouve(String username, String coups, PrintStream out) {
		send(TROUVE, username, coups, out);
	}



	private static void send(String commande, String param1, PrintStream out){
		out.print(commande+"/"+param1+"/\n");
	}

	private static void send(String commande, String param1, String param2, PrintStream out){
		out.print(commande+"/"+param1+"/"+param2+"/\n");
	}

	public static void sendEnchere(String userName, String coups, PrintStream out) {
		// TODO Auto-generated method stub
		
	}


	//TODO rajouter le reste

}
