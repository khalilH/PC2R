package rasendeRoboter;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Ladislas Halifa 
 * Cette classe permet de representer l'etat d'un plateau ainsi qu'une enigme.
 */
public class Plateau {
	private Case[][] plateau;
	/**
	 * L'enigme actuelle du plateau
	 */
	private Enigme enigme;
	/**
	 * Liste associant la couleur d'un robot representee par un String,
	 *  avec sa position courante dans le plateau representee par un Point
	 */
	private HashMap<String, Point> robots;

	/**
	 * Initialise une instance de Plateau, sans enigme
	 * @param description decrit l'etat du plateau pour une session, compose
	 * d'une suite de mur
	 */
	public Plateau(String description) {
		plateau = new Case[16][16];
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[i][j] = new Case();
			}
		}
		enigme = null;
		robots = new HashMap<String,Point>();
		decoderString(description);
	}

	/**
	 * Getter pour obtenir la case (i,j) du plateau
	 * @param i la ligne de la case
	 * @param j la colonne de la case
	 * @return une instance de Case
	 */
	public Case getCase(int i, int j) {
		return plateau[i][j];
	}

	/**
	 * Permet d'obtenir la position d'un robot
	 * @param color la couleur du robot (R, B, J, V)
	 * @return un Point contenant la ligne et la colonne de la position du robot
	 */
	public Point getPositionRobot(String color) {
		return robots.get(color);
	}

	/**
	 * Getter pour la ligne de la position d'un robot 
	 * @param color la couleur du robot (R, B, J, V)
	 * @return un entier compris entre 0 et 15
	 */
	public int getRobotX(String color) {
		return robots.get(color).x;
	}

	/**
	 * Getter pour la colonne de la position d'un robot 
	 * @param color la couleur du robot (R, B, J, V)
	 * @return un entier compris entre 0 et 15
	 */
	public int getRobotY(String color) {
		return robots.get(color).y;
	}

	/**
	 * Prepare le plateau avec les positions des robots et de la cible d'une
	 * enigme donne
	 * @param enigme une instance d'enigme
	 */
	public void setEnigme(Enigme enigme) {
		this.enigme = enigme;
		Point cible = enigme.getCiblePosition();
		plateau[cible.x][cible.y].setCible(enigme.getCibleColor());
		placerRobot();
	}

	/**
	 * Permet de placer les robots a leurs positions indiquees par l'enigme
	 */
	private void placerRobot() {
		robots.put("R", new Point(enigme.getRouge()));
		robots.put("B", new Point(enigme.getBleu()));
		robots.put("J", new Point(enigme.getJaune()));
		robots.put("V", new Point(enigme.getVert()));
		plateau[getRobotX("R")][getRobotY("R")].setRobot("R");
		plateau[getRobotX("B")][getRobotY("B")].setRobot("B");
		plateau[getRobotX("J")][getRobotY("J")].setRobot("J");
		plateau[getRobotX("V")][getRobotY("V")].setRobot("V");
	}

	/**
	 * Permet de reinitialiser l'etat du plateau en remettant les robots a
	 * leurs position initiales
	 */
	public void initPositionsRobots() {
		for(Entry<String, Point> e : robots.entrySet()) {
			plateau[e.getValue().x][e.getValue().y].enleverRobot();
		}
		if (enigme != null)
		placerRobot();
	}
	
	public void enleverRobots() {
		for(Entry<String, Point> e : robots.entrySet()) {
			System.out.println(e.getKey()+" "+e.getValue().toString());
			plateau[e.getValue().x][e.getValue().y].enleverRobot();
		}
	}

	/**
	 * Effectue un deplacement a partir d'un coup
	 * @param coup le deplacement a effectue constitue de deux lettres, la 
	 * premiere represente la couleur du robot (R, B, J, V), 
	 * la deuxieme la direction du deplacement (H, B, G, D)
	 * @return true si le coup a fait deplacer le robot d'une case, false sinon
	 */
	public boolean move(String coup) {
		boolean hasMove = false;
		String color = coup.substring(0, 1);
		String direction = coup.substring(1, 2);
		Point p = getPositionRobot(color);
		if (p != null) {
			switch (direction) {
			case "G":
				if (p.y != 0) {
					if (!plateau[p.x][p.y].isGauche() && !plateau[p.x][p.y-1].isDroit() && 
							plateau[p.x][p.y-1].isVide()) {
						plateau[p.x][p.y].enleverRobot();
						p.setLocation(p.x, p.y-1);
						plateau[p.x][p.y].setRobot(color);
						robots.put(color, p);
						hasMove = true;
					}
				}
				break;
			case "D":
				if (p.y != 15) {
					if (!plateau[p.x][p.y].isDroit() && !plateau[p.x][p.y+1].isGauche() && 
							plateau[p.x][p.y+1].isVide()) {
						plateau[p.x][p.y].enleverRobot();
						p.setLocation(p.x, p.y+1);
						plateau[p.x][p.y].setRobot(color);
						robots.put(color, p);
						hasMove = true;
					}
				}
				break;
			case "H":
				if (p.x != 0) {
					if (!plateau[p.x][p.y].isHaut() && !plateau[p.x-1][p.y].isBas() && 
							plateau[p.x-1][p.y].isVide()) {
						plateau[p.x][p.y].enleverRobot();
						p.setLocation(p.x-1, p.y);
						plateau[p.x][p.y].setRobot(color);
						robots.put(color, p);
						hasMove = true;
					}
				}
				break;
			case "B":
				if (p.x != 15) {
					if (!plateau[p.x][p.y].isBas() && !plateau[p.x+1][p.y].isHaut() && 
							plateau[p.x+1][p.y].isVide()) {
						plateau[p.x][p.y].enleverRobot();
						p.setLocation(p.x+1, p.y);
						plateau[p.x][p.y].setRobot(color);
						robots.put(color, p);
						hasMove = true;
					}
				}
				break;
			default:
				System.err.println("move : je ne dois pas passer ici");
				break;
			}
		}
		else {
			System.err.println("move : je ne dois pas passer ici");
		}
		return hasMove;
	}

	/**
	 * Parse une chaine de caractere decrivant un plateau pour construire
	 * les murs du plateau de jeu 
	 * @param description decrit l'etat du plateau pour une session, compose
	 * d'une suite de mur
	 */
	public void decoderString(String description) {
		String tmp = description.replaceAll("\\)\\(", "\\);\\(");
		String tmpTab[] = tmp.split(";");
		for (String s : tmpTab) {
			if (s.length() > 1) {
			String aux = s.substring(1, s.length()-1);
			String auxTab[] = aux.split(",");
			int i = Integer.parseInt(auxTab[0]);
			int j = Integer.parseInt(auxTab[1]);
			String mur = auxTab[2];
			plateau[i][j].buildWall(mur);
			}
		}
	}

	/**
	 * Permet de "detruire" tout les murs du plateau
	 */
	public void reset() {
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[j][i].reset();
			}
		}
	}

	/**
	 * Redefinition de la methode toString
	 * @return la chaine de caracteres d'un plateau
	 */
	public String toString() {
		String ret = "";
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				Case c = plateau[i][j];
					if (c.isBas())
						ret += "("+i+","+j+",B)";
					if (c.isHaut())
						ret += "("+i+","+j+",H)";
					if (c.isGauche())
						ret += "("+i+","+j+",G)";
					if (c.isDroit())
						ret += "("+i+","+j+",D)";
			}
		}
		return ret;
	}
	
	public Enigme getEnigme() {
		return enigme;
	}

	
	public static void main(String[] args) {
		Plateau p = new Plateau("(0,3,D)(0,11,D)(0,13,B)(1,12,D)(2,5,D)(2,5,B)(2,9,D)(2,9,B)(4,0,B)(4,2,D)(4,2,H)(4,15,H)(5,7,G)(5,7,B)(5,14,G)(5,14,B)(6,1,G)(6,1,H)(6,11,H)(6,11,D)(7,7,G)(7,7,H)(7,8,H)(7,8,D)(8,7,G)(8,7,B)(8,8,B)(8,8,D)(8,5,H)(8,5,D)(9,1,D)(9,1,B)(9,12,D)(9,15,B)(10,4,G)(10,4,B)(11,0,B)(12,9,H)(12,9,G)(13,5,D)(13,5,H)(13,14,G)(13,14,B)(14,3,G)(14,3,H)(14,11,D)(14,11,B)(15,14,G)(15,6,D)");
		if (Outils.isValidPlateau(p.toString())) {
			System.out.println("BON");
		}
		else {
			System.out.println("MAUVAIS");
		}
	}

}
