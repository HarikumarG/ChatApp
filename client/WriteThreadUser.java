import java.io.*;
import java.net.*;

/*
	This class Writes message to outputstream so that server gets that message
*/
public class WriteThreadUser extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private ChatUser client;
	
	WriteThreadUser(Socket socket, ChatUser client) {
		this.socket = socket;
		this.client = client;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output,true);
		} catch(IOException exp) {
			System.out.println("Error getting output stream: " + exp.getMessage());
		}
	}

	public void run() {
		Console console = System.console();
		String username = client.getUserName();
		String userdetail = "customer:"+username;
		writer.println(userdetail);
		System.out.println("Wait for the server to respond !");
		//this thread is stopped because it waits for the agent to be assigned first
		this.suspend();
		String text;
		String forclose;
		do {
			text = console.readLine("[" + username + "]: ");
			forclose = text;
			text = "agent:"+text;
			writer.println(text);
		} while (!forclose.equals("BYE"));

		try {
			socket.close();
		} catch (IOException exp) {
			System.out.println("Error writing to server: " + exp.getMessage());
		}
	}
}