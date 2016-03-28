package rasendeRoboter;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Case {
	BorderPane caseGUI;
	String cible;
	String robot;
	private boolean haut;
	private boolean bas;
	private boolean gauche;
	private boolean droit;

	public Case() {
		caseGUI = new BorderPane();
		cible = "";
		robot = "";
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

	public boolean isCible() {
		return !cible.equals("");
	}
	
	public void setCible(String cible) {
		this.cible = cible;
	}

	public boolean isRobot() {
		return !robot.equals("");
	}
	
	public void setRobot(String robot) {
		this.robot = robot;
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

	public BorderPane getPane() {
		return caseGUI;
	}

	public BorderPane render() {
		caseGUI.getChildren().clear();
		if (!cible.equals("")) 
			displayCible();
		if (!robot.equals(""))
			displayRobot();
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

	public void displayRobot() {
		Pane l = new Pane();
		Label ll;
		if (cible.equals("")) {
			switch (robot) {
			case "R":
				l.setStyle("-fx-background-color: #DC143C;");
				break;
			case "B":
				l.setStyle("-fx-background-color: #00BFFF;");
				break;
			case "J":
				l.setStyle("-fx-background-color: #FFFF00;");
				break;
			case "V":
				l.setStyle("-fx-background-color: #00FF00;");
				break;
			default:
				System.err.println("displayRobot : je ne dois pas passer ici");
				break;
			}
			caseGUI.setCenter(l);
		}
		else {
			 ll = new Label("X"+cible+"");
			switch (robot) {
			case "R":
				ll.setStyle("-fx-background-color: #DC143C; -fx-font-weight: bold; -fx-font-size: 20px;");
				break;
			case "B":
				ll.setStyle("-fx-background-color: #00BFFF; -fx-font-weight: bold; -fx-font-size: 20px;");
				break;
			case "J":
				ll.setStyle("-fx-background-color: #FFFF00; -fx-font-weight: bold; -fx-font-size: 20px;");
				break;
			case "V":
				ll.setStyle("-fx-background-color: #00FF00; -fx-font-weight: bold; -fx-font-size: 20px;");
				break;
			default:
				System.err.println("displayRobot : je ne dois pas passer ici");
				break;
			}
			caseGUI.setCenter(ll);
		}

	}

	public void displayCible() {
		Label l = new Label("X");
		if (robot.equals("")) {
			switch (cible) {
			case "R":
				l.setTextFill(Color.CRIMSON);
				break;
			case "B":
				l.setTextFill(Color.DEEPSKYBLUE);
				break;
			case "J":
				l.setTextFill(Color.YELLOW);
				break;
			case "V":
				l.setTextFill(Color.LIME);
				break;
			default:
				System.err.println("displayCible : je ne dois pas passer ici");
				break;
			}
			l.setStyle("-fx-background-color: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 20px;");
			caseGUI.setCenter(l);
		}
	}

}
