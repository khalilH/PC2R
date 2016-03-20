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
}
