package rasendeRoboter;

public class Case {
	private boolean haut;
	private boolean bas;
	private boolean gauche;
	private boolean droit;
	
	public Case() {
		haut = false;
		bas= false;
		gauche = false;
		droit = false;
	}

	public boolean isHaut() {
		return haut;
	}

	public void setHaut(boolean haut) {
		this.haut = haut;
	}

	public boolean isBas() {
		return bas;
	}

	public void setBas(boolean bas) {
		this.bas = bas;
	}

	public boolean isGauche() {
		return gauche;
	}

	public void setGauche(boolean gauche) {
		this.gauche = gauche;
	}

	public boolean isDroit() {
		return droit;
	}

	public void setDroit(boolean droit) {
		this.droit = droit;
	}
	
	public boolean caseAdjacente() {
		return (gauche && haut) || (gauche && bas) 
				|| (droit && haut) || (bas && droit);
	}

	public boolean hasWall() {
		return gauche || droit || haut || bas;
	}
	
	public void buildWall(String mur) {
		if (mur.equals("H")) {
			setHaut(true);
		}
		else if (mur.equals("B")) {
			setBas(true);
		}
		else if (mur.equals("G")) {
			setGauche(true);
		}
		else if (mur.equals("D")) {
			setDroit(true);
		}	
	}
}
