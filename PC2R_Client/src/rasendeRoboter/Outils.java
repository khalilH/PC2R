package rasendeRoboter;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Outils {
	
	public static boolean checkUsername(String username) {
		//TODO rajouter regexp
		if (username == null || username.equals(""))
			return false;
		return true;
	}
	
	public static boolean checkHost(String host) {
		//TODO rajouter regexp
		if (host == null || host.equals(""))
			return false;
		return true;
	}
	
	public static boolean checkHostAndCheckUsername(
			String username, String host, Text actionTarget) {
		if (!checkUsername(username)) {
			actionTarget.setFill(Color.FIREBRICK);
			actionTarget.setText("UserName non valide");
			return false;
		}
		if (!checkHost(host)) {
			actionTarget.setFill(Color.FIREBRICK);
			actionTarget.setText("Serveur non valide");
			return false;
		}
		return true;
	}
	
	public static String getCommandeName(String commande) {
		return commande.split("/")[0];
	}
	
	public static String getFirstArg(String commande) {
		String[] cmd = commande.split("/");
		if (cmd.length > 1)
			return cmd[1];
		else
			return null;
	}
	
	public static String getSecondArg(String commande) {
		String[] cmd = commande.split("/");
		if (cmd.length > 2)
			return cmd[2];
		else
			return null;
	}
}
