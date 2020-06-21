import java.io.*;
import java.net.*;

/*
	This class Reads all responses from server
*/
public class ReadThreadUser extends Thread {
	private BufferedReader reader;
	private Socket socket;
	private ChatUser client;
	private WriteThreadUser writethread;
	private boolean canChat = false;

	ReadThreadUser(Socket socket, ChatUser client) {
		this.socket = socket;
		this.client = client;

		try {
			InputStream input =  socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
		} catch(IOException exp) {
			System.out.println("Error getting input stream: " + exp.getMessage());
		}
	}

	//this function is for setting  writethread object in read thread class to resume that thread.
	void setWritethreadObj(WriteThreadUser thread) {
		this.writethread = thread;
	}

	public void run() {
		while(true) {
			try {
				String response = reader.readLine();
				if(response != null) {
					System.out.print("\n"+response+"\n");
				}
				if(canChat) {
					System.out.print("[" + client.getUserName() + "]: ");
				}
				if(response != null && response.equals("CONNECTED")) {
					//Here server said that agent is assgined ("CONNECTED") so that writethread is resumed
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