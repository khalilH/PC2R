package rasendeRoboter;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Outils {
	
	public static boolean checkUsername(String username) {
		if (username == null || username.equals(""))
			return false;
		return username.matches("\\w+");
	}
	
	public static boolean checkHost(String host) {
		if (host == null)
			return false;
		if (host.equals("localhost")) {
			return true;
		}
		if (host.matches("^\\d+.\\d+.\\d+.\\d+$")) {
			String[] ip = host.split("\\.");
			for (String s : ip) {
				System.out.println(s);
				int number = Integer.parseInt(s);
				if (!(number < 256 && number >= 0)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean isValidSolution(String deplacements) {
		if (deplacements == null || deplacements.equals(""))
			return false;
		return deplacements.matches("([RBJV][HBGD])+");
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
	
	public static String getCommandeName(String reponse) {
		return reponse.split("/")[0];
	}
	
	public static String getFirstArg(String reponse) {
		String[] cmd = reponse.split("/");
		if (cmd.length > 1)
			return cmd[1];
		else
			return null;
	}
	
	public static String getSecondArg(String reponse) {
		String[] cmd = reponse.split("/");
		if (cmd.length > 2)
			return cmd[2];
		else
			return null;
	}
}
