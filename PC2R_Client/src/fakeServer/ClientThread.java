package fakeServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread extends Thread {

	class Receive extends Thread {
		private BufferedReader in;
		
		public Receive(BufferedReader in) {
			this.in = in;
		}
		
		@Override
		public void run() {
			String recu;
			try {
				while ((recu = in.readLine()) != null) {
//					recu = in.readLine();
//					if (recu != null)
					System.out.println("recu : "+recu);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	Socket client;
	Scanner sc;
	Receive receiver;
	public ClientThread(Socket c) {
		this.client = c;
		sc = new Scanner(System.in);
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			receiver = new Receive(in);
			receiver.start();
			PrintStream out = new PrintStream(client.getOutputStream());
			while (receiver.isAlive()) {
				String msg = sc.nextLine();
				out.print(msg+"\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
}
