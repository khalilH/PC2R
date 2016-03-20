package rasendeRoboter;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Case {
	BorderPane caseGUI;
	Pane center;
	private boolean haut;
	private boolean bas;
	private boolean gauche;
	private boolean droit;

	public Case() {
		caseGUI = new BorderPane();
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
	
	public void reset() {
		haut = false;
		bas= false;
		gauche = false;
		droit = false;
	}

	protected void buildWall(String mur) {
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

	public BorderPane render() {
		caseGUI.getChildren().clear();
		if (isBas())
			botWall();
		if (isHaut())
			topWall();
		if (isDroit())
			rightWall();
		if (isGauche())
			leftWall();
		return caseGUI;
	}

	private void leftWall() {
		Pane left = new Pane();
		left.setStyle("-fx-background-color: #000000;");
		left.setPrefSize(3.0, 15.0);
		caseGUI.setLeft(left);

	}

	private void rightWall() {
		Pane right = new Pane();
		right.setStyle("-fx-background-color: #000000;");
		right.setPrefSize(3.0, 15.0);
		caseGUI.setRight(right);
	}

	private void topWall() {
		Pane top = new Pane();
		top.setStyle("-fx-background-color: #000000;");
		top.setPrefSize(15.0, 3.0);
		caseGUI.setTop(top);
	}

	private void botWall() {
		Pane bot = new Pane();
		bot.setStyle("-fx-background-color: #000000;");
		bot.setPrefSize(15.0, 3.0);
		caseGUI.setBottom(bot);
	}

}
