package demos;

import java.net.*;
import java.io.*;
import java.util.List;

public class ConnectionHandler implements Runnable {

	protected Socket clientSocket = null;
	protected String serverText = null;
	protected InputStream input = null;
	protected BufferedReader in = null;
	protected OutputStream output = null;
	protected boolean GUI = false;
	protected String username = null;
	protected String currentChan = null;
	protected List<Channel> channels = null;
	protected List<Channel> userChannels = null;
	protected static List<ConnectionHandler> onlineUsers = null;
	protected static List<User> users = null;
	protected User user = null;
	protected DirectMessage currentDM = null;
	protected boolean LOGGED_IN = false;

	public ConnectionHandler(Socket clientSocket, String serverText, List<Channel> channels) throws IOException {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		try {
			input = clientSocket.getInputStream();
			in = new BufferedReader(new InputStreamReader(input));
			output = clientSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.channels = channels;
		currentChan = new String("#general");
		onlineUsers = SocketServerDemo.onlineUsers;
		users = SocketServerDemo.users;
	}

	public String Username() {
		return username;
	}

	public void send(String message) throws IOException {
		this.output.write((message).getBytes());
	}

	public void sendLine(String message) throws IOException {
		this.output.write((message + "\n").getBytes());
	}

	public boolean currentChannel(String channel) {
		if (channel.equalsIgnoreCase(currentChan)) {
			return true;
		}
		return false;
	}
	
	public List<Channel> getChannels() {
		return channels;
	}
	
	private void close() throws IOException {
		SocketServerDemo.removeUser(this);
		if (LOGGED_IN) {
			if (!userChannels.isEmpty()) {
				for (Channel channel : userChannels) {
					channel.delUser(this);
				}
			}
			user.connection = null;
			SocketServerDemo.sendNotification("!DELUSER!" + username);
			System.out.println("--> User " + username + " has disconnected.");
		}
		in.close();
		input.close();
		output.close();
		clientSocket.close();
		clientSocket = null;
	}

	@Override
	public void run() {
		try {
			if (in.readLine().equalsIgnoreCase("1")) {
				GUI = true;
			}
			if (GUI) {
				boolean newUser = false;
				while (user == null) {
					newUser = false;
					String isNew = in.readLine();
					String Username = in.readLine();
					String Password = in.readLine();
					if (isNew.compareTo("NEW") == 0) {
						user = SocketServerDemo.newUser(Username, Password, this);
						newUser = true;
					}
					else {
						user = SocketServerDemo.authenticate(Username, Password, this);
					}
					System.out.println("--> Attempted Login: " + Username + ":" + Password);
					if (user == null) {
						sendLine("!UNAUTHORIZED!");
					}
				}
				sendLine("!AUTHORIZED!");
				userChannels = user.channels;
				username = user.Username();
				LOGGED_IN = true;
				String inputLine;
				char c;
				if (newUser) {
					System.out.println("newUser");
					user.channels.add(channels.get(0));
				}
				if (userChannels != null) {
					for (Channel channel : userChannels) {
						channel.addUser(user);
					}
				}
				channels.get(0).display(this);
				System.out.println("--> User " + username + " has joined.");
				for (ConnectionHandler user : onlineUsers) {
					if (user == this) {
						continue;
					}
					sendLine("!NEWUSER!" + user.Username());
				}
				SocketServerDemo.sendNotification("!NEWUSER!" + username);
				while (true) {
					inputLine = in.readLine();
					if (inputLine == null) {
						close();
						return;
					}
					c = inputLine.charAt(0);
					if (c == '|')
						SocketServerDemo.serverStop();
					if (c == '/') {
						String[] parts = inputLine.split(" ");
						boolean switched = false;
						try {
							if (parts[0].equalsIgnoreCase("/chan")) {
								for (Channel channel : userChannels) {
									if (parts[1].equalsIgnoreCase(channel.ID())) {
										currentChan = channel.ID();
										channel.display(this);
										switched = true;
										break;
									}
								}
								if (!switched) {
									boolean joined = false;
									for (Channel channel : channels) {
										if (parts[1].equalsIgnoreCase(channel.ID())) {
											currentChan = channel.ID();
											userChannels.add(channel);
											channel.addUser(user);
											channel.display(this);
											joined = true;
											break;
										}
									}
									if (!joined) {
										sendLine("Channel not found.");
									}
								}
							}
							if (parts[0].equalsIgnoreCase("/dm")) {
								boolean opened = false;
								for (User sendTo : users) {
									if (parts[1].compareTo(sendTo.Username()) == 0) {
										if (sendTo.isOnline()) {
											currentDM = this.user.newDM(sendTo);
											currentChan = currentDM.ID();
											currentDM.display(this);
											opened = true;
										}
									}
								}
								if (!opened) {
									sendLine("Could not find online user: " + parts[1]);
								}
							}
						}
						catch (ArrayIndexOutOfBoundsException e) {
							sendLine("Command not recognized.");
						}
					} 
					else {
						if (currentDM != null) {
							if (currentChan == currentDM.ID()) {
								SocketServerDemo.channels.add(currentDM.start());
								currentChan = new String(currentDM.ID());
								currentDM = null;
							}
						}
						System.out.println("--> " + username + ": " + currentChan + ": " + inputLine);
						SocketServerDemo.sendMessage(username + ": " + inputLine, currentChan);
					}
				}
			} 
		} catch (Exception e) {
			try {
				close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
	}
}