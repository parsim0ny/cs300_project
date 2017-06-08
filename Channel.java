package demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Channel {
	protected String id = null;
	protected List<String> messages = null;
	protected List<ConnectionHandler> onlineUsers = null;
	protected List<User> users = null;
	
	public Channel(String id) {
		this.id = new String(id);
		messages = new LinkedList<String>();
		onlineUsers = new LinkedList<ConnectionHandler>();
		users = new LinkedList<User>();
	}
	
	public String ID() {
		return id;
	}
	
	public List<ConnectionHandler> getUsers() {
		return onlineUsers;
	}
	
	public void display(ConnectionHandler displayTo) throws IOException {
		displayTo.sendLine("!CLEAR!");
		displayTo.sendLine("!CURCHAN!" + id);
		for (String message : messages) {
			displayTo.sendLine(message);
		}
	}
	
	public boolean inChannel(ConnectionHandler user) {
		for (ConnectionHandler u : onlineUsers) {
			if (user == u) {
				return true;
			}
		}
		return false;
	}
	
	public void addUser(User toAdd) throws IOException {
		String joined = new String(toAdd.Username() + " has joined " + id);
		SocketServerDemo.sendMessage(joined, id);
		System.out.println(joined);
		users.add(toAdd);
		if (toAdd.isOnline()) {
			onlineUsers.add(toAdd.connection);
			toAdd.connection.sendLine("!NEWCHAN!" + id);
		}
	}
	
	public void delUser(ConnectionHandler toDel) throws IOException {
		onlineUsers.remove(toDel);
		String left = new String(toDel.Username() + " has left " + id);
		System.out.println(left);
		SocketServerDemo.sendMessage(left,  id);
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
}
