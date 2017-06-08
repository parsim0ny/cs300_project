package demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	static Scene main;
	static Button sendButton;
	static TextField sendText;
	static VBox channels;
	static VBox users;
	static HBox messageSender;
	static VBox messages;
	static ScrollPane messageDisplay;
	static ScrollPane channelDisplay;
	static ScrollPane userDisplay;
	static BorderPane borderpane;
	
	static Scene connect;
	static Button connectButton;
	static TextField serverText;
	static TextField portText;
	static Text Server;
	static Text Port;
	static HBox serverPrompt;
	static HBox portPrompt;
	static VBox connectDisplay;

	private static String hostName;
	private static int portNumber;
	private static Socket connection;
	private static Connection client;
	private static OutputStream output;
	private static InputStream input;
	private static PrintWriter out;
	private static BufferedReader in;
	static String newUser = null;
	static String newChan = null;
	
	static Timeline timeline = new Timeline(new KeyFrame(
	        Duration.millis(10),
	        ae -> {
	        	updateFields();
			}));

	static Queue<String> newUsers = new LinkedList<String>();
	static Queue<String> newChans = new LinkedList<String>();
	static Queue<String> newMessages = new LinkedList<String>();
	static Queue<String> usersToRemove = new LinkedList<String>();
	
	static boolean CLEAR_MESSAGES = false;
	static boolean UNAUTHORIZED_LOGIN = true;
	static String SET_CHANNEL = null;

	public static void main(String[] args) {
		launch(args);
	}

	public static boolean Initialize() {
		try {
			messages.getChildren().add(new Text("Connecting to " + hostName + ":" + portNumber));
			connection = new Socket(hostName, portNumber);
			output = connection.getOutputStream();
			input = connection.getInputStream();
			out = new PrintWriter(output);
			in = new BufferedReader(new InputStreamReader(input));
			out.println("1");
			out.flush();
			client = new Connection(connection, messages);
			Thread reader = new Thread(client);
			messages.getChildren().add(new Text("Connected."));
			reader.start();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static synchronized void addUser(String user) {
		newUsers.add(user);
	}
	
	public static synchronized void addChannel(String chan) {
		newChans.add(chan);
	}
	
	public static synchronized void addMessage(String msg) {
		newMessages.add(msg);
	}
	
	public static synchronized void removeUser(String user) {
		usersToRemove.add(user);
	}
	
	public static synchronized void setCurrentChannel(String chan) {
		SET_CHANNEL = new String(chan);
	}
	
	public static synchronized void startUpdates() {
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("IRRC Client Prototype");
		primaryStage.setMinHeight(300);
		primaryStage.setMinWidth(500);

		sendText = new TextField();
		sendText.setPromptText("Message");
		sendText.setPrefColumnCount(30);
		sendButton = new Button("Send");

		messageSender = new HBox();
		messageSender.getChildren().addAll(sendText, sendButton);
		messageSender.setAlignment(Pos.CENTER);

		messages = new VBox();
		messages.setAlignment(Pos.TOP_LEFT);

		channels = new VBox();
		channels.setAlignment(Pos.TOP_LEFT);

		users = new VBox();
		users.setAlignment(Pos.TOP_LEFT);

		messageDisplay = new ScrollPane();
		messageDisplay.setContent(messages);

		channelDisplay = new ScrollPane();
		channelDisplay.setContent(channels);

		userDisplay = new ScrollPane();
		userDisplay.setContent(users);

		borderpane = new BorderPane();
		borderpane.setBottom(messageSender);
		borderpane.setCenter(messageDisplay);
		borderpane.setLeft(userDisplay);
		borderpane.setRight(channelDisplay);
		
		connectButton = new Button("Connect");
		
		serverText = new TextField();
		serverText.setPromptText("Hostname");
		
		portText = new TextField();
		portText.setPromptText("Port Number");
		
		Server = new Text("Enter Host Name:    ");
		Port = new Text("Enter Port Number: ");
		
		serverPrompt = new HBox();
		serverPrompt.setAlignment(Pos.CENTER);
		serverPrompt.getChildren().addAll(Server, serverText);
		
		portPrompt = new HBox();
		portPrompt.setAlignment(Pos.CENTER);
		portPrompt.getChildren().addAll(Port, portText);
		
		connectDisplay = new VBox();
		connectDisplay.setAlignment(Pos.CENTER);
		connectDisplay.getChildren().addAll(serverPrompt, portPrompt, connectButton);

		connectButton.setOnAction(e -> {
			hostName = new String(serverText.getText());
			try {
				portNumber = Integer.parseInt(portText.getText());
			}
			catch (Exception e1) {
				portNumber = 0;
			}

			if (!Initialize()) {
				AlertBox.display("Error", "Error Connecting to Server");
				messages.getChildren().clear();
			}
			else {
				LoginWindow.display("Login", out, in, false);
				while(UNAUTHORIZED_LOGIN && LoginWindow.hasNotExited()) {
					LoginWindow.display("Login", out, in, true);
				}
				if (!UNAUTHORIZED_LOGIN) {
					primaryStage.setScene(main);
					startUpdates();
				}
			}
		});

		sendText.setOnAction(e -> {
			if (!sendText.getText().equalsIgnoreCase("")) {
				out.println(sendText.getText());
				out.flush();
				sendText.setText("");
			}
		});

		sendButton.setOnAction(e -> {
			if (!sendText.getText().equalsIgnoreCase("")) {
				out.println(sendText.getText());
				out.flush();
				sendText.setText("");
			}
		});

		primaryStage.setOnCloseRequest(e -> System.exit(0));

		main = new Scene(borderpane, 500, 300);
		connect = new Scene(connectDisplay, 500, 300);
		
		primaryStage.setScene(connect);
		primaryStage.show();
		
	}
	
	static void updateFields() {
		if (CLEAR_MESSAGES) {
			messages.getChildren().clear();
			CLEAR_MESSAGES = false;
		}
		if (!newUsers.isEmpty()) {
			Button newUser = new Button(newUsers.remove());
			newUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			            if(mouseEvent.getClickCount() == 2){
			                out.println("/dm " + newUser.getText());
			                out.flush();
			            }
			        }
			    }
			});
			newUser.setStyle("-fx-text-fill: #00ff00; ");
			users.getChildren().add(newUser);
		}
		if (!newChans.isEmpty()) {
			Button newChannel = new Button(newChans.remove());
			newChannel.setOnAction(e -> {
				out.println("/chan " + newChannel.getText());
				out.flush();
			});
			channels.getChildren().add(newChannel);
		}
		if (!newMessages.isEmpty()) {
			messages.getChildren().add(new Text(newMessages.remove()));
			messageDisplay.layout();
			messageDisplay.setVvalue(1);
		}
		if (!usersToRemove.isEmpty()) {
			String user = usersToRemove.remove();
			List<Node> userList = users.getChildren();
			Node toDelete = null;
			for (Node node : userList) {
				if (user.compareTo(((Button) node).getText()) == 0) {
					toDelete = node;
					break;
				}
			}
			users.getChildren().remove(toDelete);
		}
		if (SET_CHANNEL != null) {
			List<Node> channelList = channels.getChildren();
			Button toHilight = null;
			for (Node node : channelList) {
				if (SET_CHANNEL.compareTo(((Button) node).getText()) == 0) {
					toHilight = (Button)node;
					continue;
				}
				((Button) node).setStyle("-fx-text-fill: #000000; ");
			}
			toHilight.setStyle("-fx-text-fill: #ff0000; ");
			SET_CHANNEL = null;
		}
	}
}
