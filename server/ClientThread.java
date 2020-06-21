import java.io.*;
import java.net.*;
import java.util.*;

/*
	This is Thread class which is created for each customer and each agent
	for reading and writing to particular socket connection
*/
public class ClientThread extends Thread {
	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;
	private BufferedReader reader;
	//to whom the message to be send
	public String sendto = null;
	//from whom the message is sent
	public String from = null;
	//to check whether the socket connection is from customer or agent
	private boolean isAgent = false;
	//the result for Round Robin algo, to check that the agent is assigned or not
	private boolean connectagentstatus = true;

	ClientThread(Socket socket,ChatServer server) {
		this.socket = socket;
		this.server = server;
	}
	//to check whether the connected socket is from customer or agent
	void parsePerson(String data) {
		String message[] = data.split(":");
		String person = message[0];
		String name = message[1];
		from = name;
		if(person.equals("customer")) {
			server.addUserData(name,this);
			connectagentstatus = server.connectagent(this);
		} else if(person.equals("support")) {
			isAgent = true;
			server.addAgentData(name,this);
		}
	}
	//to check from whom to whom the message should send
	String parseMessage(String data) {
		String message[] = data.split(":");
		String person = message[0];
		String text = message[1];
		//this sends message from user to agent
		if(person.equals("agent")) {
			String finaltext = "[" + from + "]: " + text;
			if(sendto != null) {
				server.msgFromUsertoAgent(sendto,finaltext,this);
			} else {
				sendMessagefromServer("Send to is null");
			}
		} 
		//this sends message from agent to user
		else if(person.equals("user")) {
			if(!text.equals("EXIT")) {
				String finaltext = "[" + from + "]: " + text;
				if(sendto != null) {
					server.msgFromAgenttoUser(sendto,finaltext,this);
				} else {
					sendMessagefromServer("Send to is null");
				}
			}
		}
		return text;
	}
	//this method sends message from server to particular socket which called this function
	public void sendMessagefromServer(String message) {
		writer.println(message);
	}
	//this function returns the reply of the agent to connect to that customer or not
	public String listenReplyfromAgent() {
		String txt = null;
		System.out.println("listenReplyfromAgent is called");
		while(true) {
			try {
				txt = reader.readLine();
				if(txt != null) {
					break;
				}
			} catch(Exception e) {
				System.out.println("Exception in listenReplyfromAgent Function"+e.getMessage());
				break;
			}
		}
		return txt;
	}

	public void run() {

		try {

			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
			//reads the initial message that the socket is from agent or from customer
			String data = reader.readLine();
			parsePerson(data);
			//to check whether Agent is still online and he is not quitted
			boolean quitAgent = false;
			//if connectagentstatus is true then RR algo is successful
			if(connectagentstatus) {
				do {
					if(isAgent) {
						//suspend agent's thread to wait for customer to connect
						this.suspend();
					}
					quitAgent = false;
					String text = null;
					do {
						String datatext = reader.readLine();
						if(datatext != null) {
							text = parseMessage(datatext);
							if(text != null && text.equals("EXIT")) {
								server.removeAgent(from,this);
								socket.close();
								quitAgent = true;
								break;
							} else if(text == null){
								System.out.println("The message is null");
								break;
							}
						}
					} while(!text.equals("BYE"));
					if(isAgent == false) {
						server.removeUser(from,this);
						socket.close();
					} else if(isAgent == true && quitAgent == false){
						sendto = null;
						//agent is free so move from busy list to agent list
						server.addAgentFromBusytoAvailable(from,this);
					}
				} while(isAgent == true && quitAgent == false);

			} 
			//if not a agent and RR algo is failed then remove the customer from list
			else if(isAgent == false && connectagentstatus == false) {
				server.removeUser(from,this);
				socket.close();
			}
		} catch (IOException exp) {
			System.out.println("Error in ClientThread: "+isAgent +" "+exp.getMessage());
		}
	}
}