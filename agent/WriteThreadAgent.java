import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
	This class Writes message to outputstream so that server gets that message
*/

public class WriteThreadAgent extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private ChatAgent agent;

	WriteThreadAgent(Socket socket, ChatAgent agent) {
		this.socket = socket;
		this.agent = agent;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output,true);
		} catch(IOException exp) {
			System.out.println("Error getting output stream: " + exp.getMessage());
		}
	}

	//send 'YES' or 'NO' to the server
	void sendreply(String text) {
		writer.println(text);
	}

	public void run() {
		Scanner scan = new Scanner(System.in);
		Console console = System.console();
		String agentname = agent.getUserName();
		String agentdetail = "support:"+agentname;
		String check;
		writer.println(agentdetail);
		/*	
			Here two do-while loop, if the agent says 'BYE' he/she will be disconnected 
			and will be connected to another customer and not completely exit from server
		*/
		do {
			System.out.println("Wait for the customer to connect !");
			//this thread is stopped because it waits for the customer to be assigned first
			this.suspend();
			String text;
			String forclose;
			do {
				text = console.readLine("[" + agentname + "]: ");
				forclose = text;
				text = "user:"+text;
				if(forclose.equals("EXIT")) {
					break;
				}
				writer.println(text);
			} while (!forclose.equals("BYE"));
			System.out.println("Enter 'LOGIN' to stay online Else Enter Any Character to 'LOGOUT'");
			check = scan.next();
		} while(check.equals("LOGIN"));
		//to remove the connection from server agent list
		writer.println("user:EXIT");
		try {
			socket.close();
		} catch (IOException exp) {
			System.out.println("Error writing to server: " + exp.getMessage());
		}
	}
}