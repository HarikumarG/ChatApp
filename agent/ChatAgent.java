import java.net.*;
import java.io.*;
import java.util.*;

/* 
	This class is the Main Class for Agent login. 
	This Class creates two threads one for reading the message from server and
	another for sending message to server.
*/
public class ChatAgent {

	private String host;
	private int port;
	private String agent;

	ChatAgent(String host,int port,String agent) {
		this.host = host;
		this.port = port;
		this.agent = agent;
	}

	String getUserName() {
		return this.agent;
	}

	void execute() {
		try {
			//Connect to server
			Socket socket = new Socket(host,port);
			System.out.print("Connected to the server\n");
			//writethread to send message to server
			WriteThreadAgent writethread =  new WriteThreadAgent(socket,this);
			//readthread to read message from server
			ReadThreadAgent readthread =  new ReadThreadAgent(socket,this);
			readthread.setWritethreadObj(writethread);
			readthread.start();
			writethread.start();

		} catch (UnknownHostException exp) {
			System.out.println("Server Not found: "+exp.getMessage());
		} catch (IOException exp) {
			System.out.println("I/O Error: "+exp.getMessage());
		}
	}

	// This function is used to verify the credentials of the agent
	public static boolean verifyAgent(String agentname,String password) {

		HashMap<String,String> AgentList = new HashMap<>();
		AgentList.put("Hari","4039");
		AgentList.put("Ravi","4040");
		AgentList.put("Ram","4041");
		AgentList.put("Gomathi","4042");
		if(AgentList.containsKey(agentname)) {
			if(AgentList.get(agentname).equals(password)) {
				return true;
			}
		}
		return false;
	}

	//Starting point
	public static void main(String args[]) {

		Scanner scan = new Scanner(System.in);
		String host = "localhost";
		int port = 1234;
		System.out.println("Enter 'EXIT' to close your connection if not press 'CTRL+C' to exit\n");
		System.out.println("Enter 'BYE' to wait for next customer to connect Only if customer left");
		System.out.println("Enter your name !");
		String agentname = scan.next();
		System.out.println("Enter your password !");
		String password = scan.next();
		boolean verify = verifyAgent(agentname,password);
		if(verify) {
			ChatAgent agentobj = new ChatAgent(host,port,agentname);
			agentobj.execute();
		} else {
			System.out.println("Incorrect credentials !");
		}
	}

}