package rasendeRoboter;

import java.util.Random;

public class BotData {
	
	private static final Random rand = new Random(System.currentTimeMillis());
	
	public static final String MESSAGE_BIENVENUE = "Salut !";
	public static final String MESSAGE_BIENVENUE_2 = "Bonjour a tous ";
	public static final String MESSAGE_BIENVENUE_3 = "Yo";
	
	public static final String[] BIENVENUE = {MESSAGE_BIENVENUE, MESSAGE_BIENVENUE_2, MESSAGE_BIENVENUE_3};
	
	public static final String MESSAGE_OK = "ok";
	public static final String MESSAGE_OK_1= "d'accord";
	
	public static final String MESSAGE_PROVOC = "Trop facile";
	public static final String MESSAGE_PROVOC_2 = "beaucoup trop simple pour moi";
	public static final String MESSAGE_PROVOC_3 = "c'est pour moi";
	
	public static String getMessageBienvenue(){
		return null;
	}
	
	
	public static String getMessageOK(){
		return null;
	}

	public static String getMessageProvoc(){
		return null;
	}
	
	public static String getMessageFelicitation(){
		return null;
	}
	
	public static String getMessageBienTente() {
		return null;
	}

	public static String getMessageBye(){
		return null;
	}
	
}
