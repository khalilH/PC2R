package rasendeRoboter;

import java.awt.Point;

public class Enigme {
	/* robots[0] = rouge */
	/* robots[1] = bleu*/
	/* robots[2] = jaune */
	/* robots[3] = vert */
	/* robots[4] = cible */
	private Point[] robots;
	private String cible;
	
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
	
	public String toString() {
		String ret = "";
		for (int i = 0; i<robots.length; i++) {
			ret += "("+robots[i].getX()+","+robots[i].getY()+")\n";
		}
		ret += cible+"\n";
		return ret;
	}
	
	
	
	public Point getRouge() {
		return robots[0];
	}
	
	public Point getBleu() {
		return robots[1];
	}
	
	public Point getJaune() {
		return robots[2];
	}
	
	public Point getVert() {
		return robots[3];
	}
	
	public Point getCiblePosition() {
		return robots[4];
	}
	
	public String getCibleColor() {
		return cible;
	}
}
