package rasendeRoboter;

import java.awt.Point;
import java.util.HashMap;

import javafx.scene.layout.BorderPane;

public class Plateau {
	private Case[][] plateau;
	private Enigme enigme;
	private Point cible;
	private HashMap<String, Point> robots;
	/* penser a garder un plateau original */
	/* voir si je met une Enigme ici */

	public Plateau() {
		plateau = new Case[16][16];
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[i][j] = new Case();
			}
		}
		robots = new HashMap<String,Point>();
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

	public void initPositionsRobots() {
		robots.clear();
	}
	
	private void placerRobot() {
		initPositionsRobots();
		robots.put("R", enigme.getRouge());
		robots.put("B", enigme.getBleu());
		robots.put("J", enigme.getJaune());
		robots.put("V", enigme.getVert());
		plateau[getRobotX("R")][getRobotY("R")].setRobot("R");
		plateau[getRobotX("B")][getRobotY("B")].setRobot("B");
		plateau[getRobotX("J")][getRobotY("J")].setRobot("J");
		plateau[getRobotX("V")][getRobotY("V")].setRobot("V");
	}

	public void setEnigme(Enigme enigme) {
		this.enigme = enigme;
		cible = enigme.getCiblePosition();
		plateau[cible.x][cible.y].setCible(enigme.getCibleColor());
		placerRobot();
	}

	public Point getPositionRobot(String color) {
		return robots.get(color);
	}

	public boolean move(String coup) {
		boolean hasMove = false;
		String color = coup.substring(0, 1);
		String direction = coup.substring(1, 2);
		Point p = getPositionRobot(color);
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
		return hasMove;
	}

	public int getRobotX(String color) {
		return robots.get(color).x;
	}

	public int getRobotY(String color) {
		return robots.get(color).y;
	}

	public Enigme getEnigme() {
		return enigme;
	}

	public Case getCase(int i, int j) {
		return plateau[i][j];
	}

	public BorderPane getPane(int i, int j) {
		return plateau[i][j].getPane();
	}

	public void reset() {
		for (int i=0; i < plateau.length; i++) {
			for (int j=0; j<plateau.length; j++) {
				plateau[j][i].reset();
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
