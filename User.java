package demos;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class User {
	protected String username;
	private String password;
	List<Channel> channels;
	
	ConnectionHandler connection;
	
	public User(String username, String password, ConnectionHandler client) {
		connection = client;
		this.username = new String(username);
		this.password = new String(password);
		channels = new LinkedList<Channel>();
	}
	
	public String Username() {
		return username;
	}
	
	public boolean authenticate(String p, ConnectionHandler client) {
		if (p.compareTo(password) == 0) {
			connection = client;
			return true;
		}
		return false;
	}
	
	public boolean isOnline() {
		if (connection != null) {
			return true;
		}
		return false;
	}
	
	public DirectMessage newDM(User user) throws IOException {
		Channel dm = new DirectMessage(username + "->" + user.Username(), user);
		dm.addUser(this);
		channels.add(dm);
		return (DirectMessage) dm;
	}
}
