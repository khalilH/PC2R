package fakeServer;
import java.net.ServerSocket;
import java.net.Socket;

public class FakeServer {


	
	public static void main(String[] args) {
		int port = 2016;
		ServerSocket s;
		try {
			s = new ServerSocket(port);
			while (true) {
				Socket client = s.accept();
				Thread t = new ClientThread(client);
				t.start();		
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
