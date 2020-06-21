import java.io.*;
import java.net.*;
import java.util.*;

/*
	This is the main server class which handles all the socket connections of both
	support agent and customer
*/
public class ChatServer {

	private int port;
	//maintains user lists
	private HashMap<String,ClientThread> userLists = new HashMap<>();
	//maintains connected agents
	private HashMap<String,ClientThread> busyList = new HashMap<>();
	//maintains agents in online and waiting for customer
	private LinkedHashMap<String, ClientThread> agentLists = new LinkedHashMap<String, ClientThread>();

	ChatServer(int port) {
		this.port = port;
	}

	void execute() {

		try {
			//Start server
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Chat Server is listening on port " + port);

			while(true) {
				//accepts the incoming requests
				Socket socket = serverSocket.accept();
				System.out.println("New User/Agent connected");
				//create new thread for each socket connection
				ClientThread newClient = new ClientThread(socket,this);
				newClient.start();
			}

		} catch (IOException exp) {
			System.out.println("Error in the server: " + exp.getMessage());
		}
	}
	//add user to users list
	void addUserData(String name, ClientThread newClient) {
		userLists.put(name,newClient);
		System.out.println(name+": This User is added Successfully");
	}
	//add agent to agents list
	void addAgentData(String name, ClientThread newClient) {
		agentLists.put(name,newClient);
		System.out.println(name+": This agent is added Successfully "+agentLists.size());
	}
	//remove user from users list
	void removeUser(String username, ClientThread client) {
		if(!username.equals(null) && userLists.containsKey(username)) {
			userLists.remove(username);
			System.out.println("User - "+username+" Quitted from logged in list Successfully!");
		} else {
			System.out.println("No such User - "+username+" is available in logged in list..");
		} 
	}
	//remove agent from both busy list and agent list
	void removeAgent(String agentname, ClientThread client) {
		if(!agentname.equals(null) && agentLists.containsKey(agentname)) {
			agentLists.remove(agentname);
			System.out.println("Agent - "+agentname+" Quitted from logged in list Successfully!");
		} 
		if(!agentname.equals(null) && busyList.containsKey(agentname)) {
			busyList.remove(agentname);
			System.out.println("Agent - "+agentname+" removed from busy list Successfully!");	
		}
	}
	//move agent from busy list to available list
	void addAgentFromBusytoAvailable(String agentname, ClientThread client) {
		if(!agentname.equals(null) && busyList.containsKey(agentname)) {
			ClientThread tempobj = busyList.get(agentname);
			busyList.remove(agentname);
			agentLists.put(agentname,tempobj);
			System.out.println(agentname+" is available in busy list to convert to available");
		} else {
			System.out.println(agentname+" is not available in busy list to convert to available");
		}
	}
	//Round robin algorithm, This client object is customer's object
	boolean connectagent(ClientThread client) {
		//this queue used to implements the concept FIFO for the agents list
		HashMap<String,ClientThread> fifoQueue = new HashMap<>();
		int n = agentLists.size();
		boolean assigned = false;
		if(n == 0) {
			client.sendMessagefromServer("Sorry Currently! There is no agent available. Try again after sometime");		
			return false;			
		}
		for(Map.Entry<String,ClientThread> eachagent: agentLists.entrySet()) {
			String agentname = eachagent.getKey();
			ClientThread tempobj = eachagent.getValue();
			tempobj.sendMessagefromServer(client.from+": This Customer wants to connect with you 'YES' or 'NO'@"+agentname);
			String reply = tempobj.listenReplyfromAgent();
			System.out.println("Reply from agent "+reply);
			if(reply.equals("YES")) {
				tempobj.resume();
				assigned = true;
				agentLists.remove(agentname);
				busyList.put(agentname,tempobj);
				client.sendto = agentname;
				tempobj.sendto = client.from;
				client.sendMessagefromServer("You are now connected to our support agent! "+agentname);
				client.sendMessagefromServer("CONNECTED");
				tempobj.sendMessagefromServer("You are now connected to Customer "+client.from);
				tempobj.sendMessagefromServer("CONNECTED");
				break;
			} else {
				fifoQueue.put(agentname,tempobj);
			}
		}
		if(!assigned) {
			client.sendMessagefromServer("Sorry! Everyone rejected your request. Try again after sometime");
			removeUser(client.from,client);	
			return false;
		}
		if(fifoQueue.size() != 0) {
			for(Map.Entry<String,ClientThread> eachagent: fifoQueue.entrySet()) {
				String agentname = eachagent.getKey();
				ClientThread tempobj = eachagent.getValue();
				fifoQueue.remove(agentname);
				agentLists.put(agentname,tempobj);
			}
		}
		return true;
	}
	//Sends message from user to agent, This client object is customer's object
	void msgFromUsertoAgent(String sendto, String message, ClientThread client) {
		boolean agentexist = false;
		for(Map.Entry<String,ClientThread> eachagent: busyList.entrySet()) {
			if(eachagent.getKey().equals(sendto)) {
				agentexist = true;
				ClientThread tempobj = eachagent.getValue();
				tempobj.sendMessagefromServer(message);
			}
		}
		if(!agentexist) {
			client.sendMessagefromServer("Sorry agent left! Disconnect and try again");
			removeUser(client.from,client);	
		}
	}
	//Sends message from agent to user, This client object is agent's object
	void msgFromAgenttoUser(String sendto, String message,ClientThread client) {
		boolean userexist = false;
		for(Map.Entry<String,ClientThread> eachuser: userLists.entrySet()) {
			if(eachuser.getKey().equals(sendto)) {
				userexist = true;
				ClientThread tempobj = eachuser.getValue();
				tempobj.sendMessagefromServer(message);
			}
		}
		if(!userexist) {
			client.sendMessagefromServer("Sorry User left!");
		}
	}

	//Starting point
	public static void main(String[] args) {
		int port = 1234;
		ChatServer server = new ChatServer(port);
		server.execute();
	}

}