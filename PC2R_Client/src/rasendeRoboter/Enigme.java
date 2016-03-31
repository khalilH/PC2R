package rasendeRoboter;

import java.awt.Point;

/**
 * 
 * @author Ladislas Halifa
 * Cette classe permet de representer une enigme composee des positions
 * initiales des robots rouge, bleu, jaune, vert et de la cible ainsi que la 
 * couleur de la cible
 */


public class Enigme {
	/* robots[0] = rouge */
	/* robots[1] = bleu*/
	/* robots[2] = jaune */
	/* robots[3] = vert */
	/* robots[4] = cible */

	/**
	 * Tableau de Point
	 */
	private Point[] robots;
	/**
	 * Couleur de la cible (R, B, J, V)
	 */
	private String cible;

	/**
	 * Contructeur d'une Enigme
	 * @param enigme la description d'une enigme
	 */
	public Enigme(String enigme) {
		robots = new Point[5];
		String tmp = enigme.substring(1, enigme.length()-1);
		String tmpTab[] = tmp.split(",");
		for (int i = 0; i < 5 ; i++) {
			int x = Integer.parseInt(tmpTab[2 * i]);
			int y = Integer.parseInt(tmpTab[2 * i + 1]);
			robots[i] = new Point(x,y);
		}
		cible = tmpTab[10];
	}

	/**
	 * Redefinition de la methode toString
	 * @return la chaine de caracteres d'une enigme
	 */
	public String toString() {
		String ret = "(";
		for (int i = 0; i<robots.length; i++) {
			if (i == 0)
				ret += robots[i].x+","+robots[i].y;
			else
				ret += ","+robots[i].x+","+robots[i].y;
		}
		ret += ","+cible+")";
		return ret;
	}

	/**
	 * Getter de la position initiale du robot rouge
	 * @return un Point contenant la ligne et la colonne de la position du robot
	 */
	public Point getRouge() {
		return robots[0];
	}

	/**
	 * Getter de la position initiale du robot bleu
	 * @return un Point contenant la ligne et la colonne de la position du robot
	 */
	public Point getBleu() {
		return robots[1];
	}

	/**
	 * Getter de la position initiale du robot jaune
	 * @return un Point contenant la ligne et la colonne de la position du robot
	 */
	public Point getJaune() {
		return robots[2];
	}

	/**
	 * Getter de la position initiale du robot vert
	 * @return un Point contenant la ligne et la colonne de la position du robot
	 */
	public Point getVert() {
		return robots[3];
	}

	/**
	 * Getter de la position de la cible
	 * @return Point contenant la ligne et la colonne de la position de la cible
	 */
	public Point getCiblePosition() {
		return robots[4];
	}

	/**
	 * Getter de la couleur de la cible
	 * @return la couleur de la cible (R, B, J, V) 
	 */
	public String getCibleColor() {
		return cible;
	}

	public static void main(String[] args) {
		Enigme n = new Enigme("(13,5,9,12,6,1,5,14,8,5,R)");
		System.out.println(n.toString());
	}
}
