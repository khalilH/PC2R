package rasendeRoboter;

import java.io.PrintStream;

public class Protocole {

	public static final int PORT = 2016;

	public static final String BIENVENUE = "BIENVENUE";
	public static final String CONNECTE = "CONNECTE";
	public static final String SORTI = "SORTI";
	public static final String RECEIVE_CHAT = "RECEIVE";
	public static final String SESSION = "SESSION";

	public static final String CONNEX = "CONNEX";
	public static final String SORT = "SORT";
	public static final String SEND_CHAT = "SEND";

	public static void disconnect(String username, PrintStream out) {
		send(SORT, username, out);
	}

	public static void connect(String username, PrintStream out) {
		send(CONNEX, username, out);
	}

	public static void sendChat(String username, String message, PrintStream out) {
		send(SEND_CHAT, username, message, out);
	}



	private static void send(String commande, String param1, PrintStream out){
		out.print(commande+"/"+param1+"\n");
	}

	private static void send(String commande, String param1, String param2, PrintStream out){
		out.print(commande+"/"+param1+"/"+param2+"\n");
	}


	//TODO rajouter le reste

}
