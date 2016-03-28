package rasendeRoboter;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Bilan {
	private int tour;
	private HashMap<String,Integer> scoreSheet;
	private final ObservableList<Score> data = FXCollections.observableArrayList();

	public Bilan() {
		tour = 0;
		scoreSheet = new HashMap<>();		
	}

	public int getTour() {
		return tour;
	}

	public ObservableList<Score> getScoreSheet() {
		data.clear();
		for (Entry<String, Integer> e : scoreSheet.entrySet()) {
			data.add(new Score(e.getKey(), e.getValue()));
		}
		return data;
	}

	public void decoderBilan(String bilan) {
		String tmp = bilan.replaceAll("\\(", ";").replaceAll("\\)", "");
		String bilanSplit[] = tmp.split(";");

		boolean tourRecupere = false;
		for (String s : bilanSplit) {
			if (!tourRecupere) {
				tour = Integer.parseInt(s);
				tourRecupere = true;
			}
			else {
				String aux[] = s.split(",");
				String user = aux[0];
				int score = Integer.parseInt(aux[1]);
				updateScore(user, score);
			}
		}
	}

	private void updateScore(String user, int score) {
		scoreSheet.put(user, score);
	}

	// TODO a supprimer
	public void print() {
		System.out.println("Tour = " +tour);
		for (String user : scoreSheet.keySet()) {
			System.out.println("user: "+user+"; score: "+scoreSheet.get(user));
		}
		System.out.println("=================================");
		System.out.println();
	}




	public static class Score {

		private final SimpleStringProperty user;
		private final SimpleIntegerProperty score;

		private Score(String user, Integer score){
			this.user = new SimpleStringProperty(user);
			this.score = new SimpleIntegerProperty(score);
		}

		public String getUser() {
			return user.get();
		}

		public void setUser(String u) {
			this.user.set(u);
		}

		public Integer getScore() {
			return score.get();
		}

		public void setScore(Integer sc) {
			score.set(sc);
		}
	}


	public static void main(String[] args) {
		Bilan b = new Bilan();
		b.decoderBilan("6(saucisse,3)(brouette,0)");
		b.print();
		b.decoderBilan("7(saucisse,3)(brouette,0)(marco,1)");
		b.print();
	}
}
