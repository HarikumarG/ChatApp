import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
	This class Reads all responses from server
*/
public class ReadThreadAgent extends Thread {
	private BufferedReader reader;
	private Socket socket;
	private ChatAgent agent;
	private WriteThreadAgent writethread;
	private boolean canChat = false;

	ReadThreadAgent(Socket socket, ChatAgent agent) {
		this.socket = socket;
		this.agent = agent;

		try {
			InputStream input =  socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
		} catch(IOException exp) {
			System.out.println("Error getting input stream: " + exp.getMessage());
		}
	}

	//this function is for setting  writethread object in read thread class to resume that thread.
	void setWritethreadObj(WriteThreadAgent thread) {
		this.writethread = thread;
	}

	//this function is used to send 'YES' or 'NO' to connect that particular customer
	//when the response comes with '@' character from server this then function gets executed
	void parseMessage(String data) {
		if(data != null && data.indexOf('@') != -1) {
			Scanner scan = new Scanner(System.in);
			String message[] = data.split("@");
			if(message[1] != null) {
				String name = message[1];
				if(name.equals(agent.getUserName())) {
					System.out.println("Enter YES or NO");
					String reply = scan.next();
					if(reply != null && (reply.equals("YES") || reply.equals("NO"))) {
						writethread.sendreply(reply);
					}
				}
			}
		}
	}

	public void run() {
		while(true) {
			try {
				String response = reader.readLine();
				System.out.print("\n"+response+"\n");
				parseMessage(response);
				if(canChat) {
					System.out.print("[" + agent.getUserName() + "]: ");
				}
				if(response != null && response.equals("CONNECTED")) {
				//Here server said that customer is assgined ("CONNECTED") so that writethread is resumed
					writethread.resume();
					canChat = true;
				}
			} catch (IOException exp) {
				System.out.println("Error reading from server: " + exp.getMessage());
				break;
			}
		}
	}
}