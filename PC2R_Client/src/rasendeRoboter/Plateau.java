package rasendeRoboter;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Plateau {
	private Case[][] plateau;
	/* penser a garder un plateau original */
	/* voir si je met une Enigme ici */
	
	public Plateau() {
		plateau = new Case[16][16];
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[i][j] = new Case();
			}
		}
	}
	
	public Plateau(String description) {
		this();
		decoderString(description);
	}
	
	public void decoderString(String description) {
		String tmp = description.replaceAll("\\)\\(", "\\);\\(");
		String tmpTab[] = tmp.split(";");
		for (String s : tmpTab) {
			String aux = s.substring(1, s.length()-1);
			String auxTab[] = aux.split(",");
			int i = Integer.parseInt(auxTab[0]);
			int j = Integer.parseInt(auxTab[1]);
			String mur = auxTab[2];
			plateau[i][j].buildWall(mur);
		}
	}
	
	public Case getCase(int i, int j) {
		return plateau[i][j];
	}
	
	public void reset() {
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[i][j].reset();
			}
		}
	}
	
	public String toString() {
		String ret = "";
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				Case c = plateau[i][j];
				if (c.hasWall()) {
					if (c.isBas())
					ret += "("+i+","+j+",B)\n";
					if (c.isHaut())
						ret += "("+i+","+j+",H)\n";
					if (c.isGauche())
						ret += "("+i+","+j+",G)\n";
					if (c.isDroit())
						ret += "("+i+","+j+",D)\n";
				}
			}
		}
		return ret;
	}
	

}
