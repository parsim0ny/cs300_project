package demos;

import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.*;

public class SocketServerDemo {
	static ConnectionHandler toAdd = null;
	static List<User> users = new LinkedList<User>();
	static List<ConnectionHandler> onlineUsers = new LinkedList<ConnectionHandler>();
	static List<Channel> channels = new LinkedList<Channel>();
	static String message = null;
	static String motd = "IRRC Test Server!";
	static ServerSocket serverSocket = null;
	
	static synchronized void sendMessage(String input, String channel) throws IOException {
		message = input;
		addMessage(message, channel);
		for (ConnectionHandler user : getUsers(channel)) {
			if (user.currentChannel(channel)) {
				user.sendLine(message);
			}
		}
	}
	
	static synchronized void sendNotification(String input) throws IOException	{
		message = input;
		for (ConnectionHandler user : onlineUsers) {
			user.sendLine(message);
		}
	}
	
	static List<ConnectionHandler> getUsers(String channel) {
		for (Channel chan : channels) {
			if (channel.equalsIgnoreCase(chan.ID())) {
				return chan.getUsers();
			}
		}
		System.out.println("Fatal Error: Channel " + channel + " not found.");
		return null;
	}
	
	static synchronized void removeUser(ConnectionHandler userObject) {
		onlineUsers.remove(userObject);
	}
	
	static void addMessage(String message, String channel) {
		for (Channel chan : channels) {
			if (channel.equalsIgnoreCase(chan.ID())) {
				chan.addMessage(message);
			}
		}
	}
	
	static User authenticate(String username, String password, ConnectionHandler client) {
		for (User user : users) {
			if (username.compareTo(user.Username()) == 0) {
				if (user.authenticate(password, client)) {
					return user;
				}
				return null;
			}
		}
		return null;
	}
	
	static User newUser(String username, String password, ConnectionHandler client) {
		User newUser = new User(username, password, client);
		users.add(newUser);
		return newUser;
	}
	
	static void serverStop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException {
		Socket clientSocket = null;
		System.out.println("Initializing Server...");
		int portNumber = 1337;
		System.out.println("Set port: " + portNumber);
 		System.out.println("Creating channels...");
 		Channel general = new Channel("#general");
 		Channel test1 = new Channel("#test1");
 		Channel test2 = new Channel("#test2");
 		System.out.println("Starting channels...");
 		channels.add(general);
 		channels.add(test1);
 		channels.add(test2);
        try {
        	System.out.println("Opening connection socket...");
        	serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
        	System.out.println("Port Error\n");
        	System.out.println(e.getMessage());
    	}
        System.out.println("Done.");
        System.out.println("Server accepting connections on port " + portNumber + " with motd: " + motd);
        while(true) {
        	try {
        		clientSocket = serverSocket.accept();
        	}
        	
        	catch (SocketException se) {
        		System.out.println("Server socket no longer available...closing");
        		System.exit(0);
        	}
        	toAdd = new ConnectionHandler(clientSocket, motd, channels);
        	new Thread(toAdd).start();
        	onlineUsers.add(toAdd);
        }
    }
}
