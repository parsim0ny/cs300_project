package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Connection implements Runnable {

	protected VBox messages;
	protected TextField toSend;
	protected Button Send;
	protected Socket connection;
	protected InputStream input;
	protected BufferedReader in;
	protected OutputStream output;
	protected PrintWriter out;

	public Connection(Socket server, VBox messageHolder) throws IOException {
		connection = server;
		messages = messageHolder;
		input = connection.getInputStream();
		in = new BufferedReader(new InputStreamReader(input));
		output = connection.getOutputStream();
		out = new PrintWriter(output);
	}

	@Override
	public void run() {

		while (true) {
			try {
				String input = in.readLine();
				if (input == null) {
					System.exit(0);
				}
				if (input.indexOf("!NEWUSER!") == 0) {
					Main.addUser(input.substring(9, input.length()));
				}

				else if (input.indexOf("!NEWCHAN!") == 0) {
					Main.addChannel(input.substring(9, input.length()));
				}
				
				else if (input.indexOf("!DELUSER!") == 0) {
					Main.removeUser(input.substring(9, input.length()));
				}
				
				else if (input.indexOf("!CLEAR!") == 0) {
					Main.CLEAR_MESSAGES = true;
				}
				else if (input.indexOf("!CURCHAN!") == 0) {
					Main.setCurrentChannel(input.substring(9, input.length()));
				}
				else if (input.indexOf("!AUTHORIZED!") == 0) {
					Main.UNAUTHORIZED_LOGIN = false;
				}

				else {
					Main.addMessage(input);
				}
			}

			catch (IOException e) {
				// do something here
				System.exit(1);
			}
			
		}
	}
}
