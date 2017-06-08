package demos;

import java.io.IOException;
import java.util.LinkedList;

public class DirectMessage extends Channel {

	protected User recipient;
	
	public DirectMessage start() throws IOException {
		addUser(recipient);
		recipient.channels.add(this);
		return this;
	}
	
	public DirectMessage(String id, User sendTo) {
		super(id);
		recipient = sendTo;
	}
	
	public DirectMessage(DirectMessage toCopy) {
		super(toCopy.id);
		messages = new LinkedList<String>(toCopy.messages);
		users = new LinkedList<User>(toCopy.users);
		onlineUsers = new LinkedList<ConnectionHandler>(toCopy.onlineUsers);
		recipient = toCopy.recipient;
	}
	
	public boolean isEmpty() {
		if (messages.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void addUser(User toAdd) throws IOException {
		users.add(toAdd);
		if (toAdd.isOnline()) {
			onlineUsers.add(toAdd.connection);
			toAdd.connection.sendLine("!NEWCHAN!" + id);
		}
	}
	
	@Override
	public void delUser(ConnectionHandler toDel) throws IOException {
		onlineUsers.remove(toDel);
	}
}
