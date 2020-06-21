import java.net.*;
import java.io.*;
import java.util.Scanner;


/* 
	This class is the Main Class for customer login. 
	This Class creates two threads one for reading the message from server and
	another for sending message to server.
*/

public class ChatUser {

	private String host;
	private int port;
	private String username;

	ChatUser(String host,int port,String username) {
		this.host = host;
		this.port = port;
		this.username = username;
	}

	String getUserName() {
		return this.username;
	}

	void execute() {
		try {
			//Connect to server
			Socket socket = new Socket(host,port); 
			System.out.print("Connected to the server\n");
			//writethread to send message to server
			WriteThreadUser writethread = new WriteThreadUser(socket,this);
			//readthread to read message from server
			ReadThreadUser readthread = new ReadThreadUser(socket,this);
			readthread.setWritethreadObj(writethread);
			readthread.start();
			writethread.start();
			
		} catch (UnknownHostException exp) {
			System.out.println("Server Not found: "+exp.getMessage());
		} catch (IOException exp) {
			System.out.println("I/O Error: "+exp.getMessage());
		}
	}

	//Starting Point 
	public static void main(String args[]) {

		Scanner scan = new Scanner(System.in);
		String host = "localhost";
		int port = 1234;
		System.out.println("Enter 'BYE' to close your connection if not press 'CTRL+C' to exit\n");
		System.out.println("Please Enter your name !");
		String username = scan.next();
		ChatUser user = new ChatUser(host,port,username);
		user.execute();

	}

}