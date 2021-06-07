/**********************************************
Workshop 9
Course: JAP444 - 4th Semester
Last Name: Fedchenko
First Name: Andrei
ID: 159867183
Section: NDD
This assignment represents my own work in accordance with Seneca Academic Policy.
Andrei Fedchenko
Date: 12/03/2020
**********************************************/
package ca.senecacollege.ws9;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.w3c.dom.Document;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class ChatroomClient extends Application {
	Socket connection = null;
	DataOutputStream out = null;
	BufferedReader in = null;
	String message;
	WebEngine webEngine;
	
	ObservableList<RoomData> data = FXCollections.observableArrayList();

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		int hSize = 800, vSize = 400;
		//--------------------------< Login Screen >---------------------------
		VBox loginScreen = new VBox(); // 300 100
		
		final Scene mainScene = new Scene(loginScreen, hSize, vSize);
		
		loginScreen.setSpacing(15);
		loginScreen.setAlignment(Pos.CENTER);
		loginScreen.setPadding(new Insets(5, 20, 5, 20));
		
		HBox LSrow1 = new HBox();
		LSrow1.setAlignment(Pos.CENTER);
		LSrow1.setSpacing(10);
		LSrow1.getChildren().add(new Label("Nickname:"));
		TextField nickname = LimitedTextField(30);
		nickname.setMaxWidth(143);
		LSrow1.getChildren().add(nickname);
		
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(Color.RED);
		colorPicker.setMaxWidth(40);
		LSrow1.getChildren().add(colorPicker);
		
		HBox LSrow2 = new HBox();
		LSrow2.setAlignment(Pos.CENTER);
		LSrow2.setSpacing(20);
	
		Button exitBtn = new Button("Exit");
		LSrow2.getChildren().add(exitBtn);
		Button enterBtn = new Button("Enter");
		LSrow2.getChildren().add(enterBtn);
		
		loginScreen.getChildren().addAll(LSrow1, LSrow2);
		
		//---------------------------< Server List >---------------------------
		VBox serverList = new VBox();
		serverList.setPadding(new Insets(10, 10, 10, 10));
		serverList.setSpacing(10);
			
		HBox topPanel = new HBox();
		topPanel.setAlignment(Pos.CENTER);
		topPanel.setStyle("-fx-font: 14 arial");
		topPanel.setSpacing(30);
		
		Button logOutBtn = new Button("Log Out");
		topPanel.getChildren().add(logOutBtn);
		
		HBox welcomeMessage = new HBox();
		welcomeMessage.setAlignment(Pos.CENTER);
		welcomeMessage.getChildren().add(new Label("Welcome, "));
		Label userName = new Label("Username");
		userName.setTextFill(Color.web("#fa0000"));
		welcomeMessage.getChildren().add(userName);
		topPanel.getChildren().add(welcomeMessage);
		
		HBox joinRoomBox = new HBox();
		joinRoomBox.setAlignment(Pos.CENTER);
		joinRoomBox.setSpacing(5);
		TextField roomNameField = LimitedTextField(50);
		roomNameField.maxWidth(80);
		roomNameField.setPromptText("Room name");
		joinRoomBox.getChildren().add(roomNameField);
		Button joinBtn = new Button("Join");
		joinRoomBox.getChildren().add(joinBtn);
		topPanel.getChildren().add(joinRoomBox);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		topPanel.getChildren().add(spacer);

		// add room button
		Button addBtn = new Button("+");
		addBtn.setStyle("-fx-font: 18 arial");
		topPanel.getChildren().add(addBtn);
		
		//-----< Popup form for room information that appears when add button is pressed >------
		Popup roomPopUp = new Popup();
		
		VBox roomForm = new VBox();
		roomForm.setMaxHeight(150);
		roomForm.setMaxWidth(400);
		roomForm.setAlignment(Pos.CENTER);
		roomForm.setSpacing(5);
		roomForm.setPadding(new Insets(5, 5, 5, 5));
		roomForm.setStyle("-fx-background-color: white; -fx-border-color: black;");
		
		HBox PUrow1 = new HBox();
		PUrow1.setSpacing(10);
		PUrow1.setAlignment(Pos.CENTER_LEFT);
		PUrow1.getChildren().add(new Label("Title:"));
		TextField roomTitle = LimitedTextField(50);
		PUrow1.getChildren().add(roomTitle);
		PUrow1.getChildren().add(new Label("Max Slots:"));
		ObservableList<Integer> options = FXCollections.observableArrayList(new ArrayList<Integer>());
		options.addAll(2,3,4,5);
		ComboBox<Integer> maxSlots = new ComboBox<>(options);
		PUrow1.getChildren().add(maxSlots);
		roomForm.getChildren().add(PUrow1);
		
		roomForm.getChildren().add(new Label("Description:"));
		TextArea roomDescription = new TextArea();
		roomDescription.setWrapText(true);
		roomDescription.setStyle("-fx-border-color: black");
		roomForm.getChildren().add(roomDescription);
		
		HBox PUrow2 = new HBox();
		PUrow2.setSpacing(50);
		PUrow2.setPadding(new Insets(10, 10, 10, 10));
		PUrow2.setAlignment(Pos.CENTER);
		Button cancelRoomPopUpBtn = new Button("Cancel");
		PUrow2.getChildren().add(cancelRoomPopUpBtn);
		Button addRoomPopUpBtn = new Button("Add");
		PUrow2.getChildren().add(addRoomPopUpBtn);
		roomForm.getChildren().add(PUrow2);
		roomPopUp.getContent().add(roomForm);
		
		// refresh button
		Button refreshBtn = new Button("\uD83D\uDDD8");
		refreshBtn.setStyle("-fx-font: 18 arial");
		topPanel.getChildren().add(refreshBtn);
		serverList.getChildren().add(topPanel);
		
		
		TableView<RoomData> table = new TableView<>();
		table.setEditable(true);
		table.setStyle("-fx-background-color: none");
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<RoomData, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<RoomData, String>("title"));
		titleColumn.setResizable(false);
		titleColumn.setReorderable(false);
		titleColumn.setMinWidth(150);
		titleColumn.setMaxWidth(150);
		TableColumn<RoomData, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<RoomData, String>("description"));
		descriptionColumn.setReorderable(false);
		descriptionColumn.setMaxWidth(587);
		descriptionColumn.setMinWidth(200);
		TableColumn<RoomData, String> slotsColumn = new TableColumn<>("Slots");
		slotsColumn.setResizable(false);
		slotsColumn.setReorderable(false);
		slotsColumn.setMaxWidth(40);
		slotsColumn.setCellValueFactory(new Callback<CellDataFeatures<RoomData, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<RoomData, String> roomData) {
				return new ReadOnlyObjectWrapper<String>(roomData.getValue().getStringSlots());
			}
		});
		
		table.getColumns().addAll(titleColumn, descriptionColumn, slotsColumn);
		table.setItems(data);
		serverList.getChildren().add(table);
		
		//----------------------------< Chat Room >----------------------------
		VBox chatroom = new VBox();
		chatroom.setPadding(new Insets(10, 10, 10, 10));
		chatroom.setSpacing(10);
		
		HBox chatroomTopPanel = new HBox();
		chatroomTopPanel.setAlignment(Pos.CENTER_LEFT);
		chatroomTopPanel.setStyle("-fx-font: 14 arial");
		chatroomTopPanel.setSpacing(30);
		Button logOutBtn2 = new Button("Log Out");
		HBox welcomeMessage2 = new HBox();
		welcomeMessage2.setAlignment(Pos.CENTER);
		welcomeMessage2.getChildren().add(new Label("Welcome, "));
		Label userName2 = new Label("Username");
		welcomeMessage2.getChildren().add(userName2);
		chatroomTopPanel.getChildren().addAll(logOutBtn2, welcomeMessage2);
		chatroom.getChildren().add(chatroomTopPanel);
		Region spacer2 = new Region();
		HBox.setHgrow(spacer2, Priority.ALWAYS);
		chatroomTopPanel.getChildren().add(spacer2);
		Button leaveBtn = new Button("Leave");
		chatroomTopPanel.getChildren().add(leaveBtn);
		
		final WebView chatbox = new WebView();
		chatbox.setMinHeight(280);
		chatbox.setMaxHeight(280);
		webEngine = chatbox.getEngine();
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
		    if (newState == State.SUCCEEDED) {
		        Document doc = webEngine.getDocument();
		    }
		});
		webEngine.loadContent("");
		webEngine.setUserStyleSheetLocation(getClass().getResource("/stylesheet.css").toString());
		chatroom.getChildren().add(chatbox);
		
		HBox sendMessageArea = new HBox();
		sendMessageArea.setAlignment(Pos.CENTER);
		sendMessageArea.setSpacing(10);
		TextArea messagebox = new TextArea();
		messagebox.setMaxHeight(100);
		messagebox.setWrapText(true);
		sendMessageArea.getChildren().add(messagebox);
		Button sendMessageBtn = new Button("Send");
		sendMessageBtn.setMaxWidth(50);
		sendMessageBtn.setMaxHeight(40);
		sendMessageBtn.setMinWidth(50);
		sendMessageArea.getChildren().add(sendMessageBtn);
		chatroom.getChildren().add(sendMessageArea);
		
		//----------------------< Buttons Functionality >----------------------
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					if (connection != null)  {
						out.writeBytes("c/exit");
						connection.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				primaryStage.close();
			}
		});
		
		enterBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (nickname.getText().length() > 2) {
					startConnection();
					try {
						String nick = nickname.getText();
						Color color = colorPicker.getValue();
						String hexColor = String.format( "#%02X%02X%02X",
					            (int)( color.getRed() * 255 ),
					            (int)( color.getGreen() * 255 ),
					            (int)( color.getBlue() * 255 ));
						out.writeBytes(nick + "\n");
						out.writeBytes(hexColor + "\n");
						userName.setText(nick);
						userName2.setText(nick);
						userName.setTextFill(Color.web(color.toString()));
						userName2.setTextFill(Color.web(color.toString()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					getRooms();
					mainScene.setRoot(serverList);
				}
			}			
		});
		
		EventHandler<ActionEvent> logOutAction = new EventHandler<>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					if (connection != null) {
						out.writeBytes("c/exit\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				mainScene.setRoot(loginScreen);
			}			
		};
		
		logOutBtn.setOnAction(logOutAction);
		
		logOutBtn2.setOnAction(logOutAction);
		
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				roomPopUp.show(primaryStage);
			}
		});
		
		cancelRoomPopUpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				roomPopUp.hide();
			}
		});
		
		refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getRooms();
			}
		});
		
		leaveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					out.writeBytes("c/leave\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				mainScene.setRoot(serverList);
				webEngine.executeScript("document.body.innerHTML = '';");
			}
		});
		
		addRoomPopUpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String title = roomTitle.getText();
				String description = roomDescription.getText();
				int slots = maxSlots.getValue();
				try {
					out.writeBytes("c/create\n");
					out.writeBytes(title + "\n");
					out.writeBytes(description + "\n");
					out.write(slots);
					roomPopUp.hide();
					mainScene.setRoot(chatroom);
					webEngine.executeScript("document.body.innerHTML += \"" + in.readLine() + "\";");
					new Thread(new ChatListener()).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		joinBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					out.writeBytes("c/" + roomNameField.getText() + "/join\n");
					webEngine.executeScript("document.body.innerHTML += \"" + in.readLine() + "\";");
					if (in.readLine().equals("r/true")) {
						new Thread(new ChatListener()).start();
						mainScene.setRoot(chatroom);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		sendMessageBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String message = messagebox.getText();
				messagebox.setText("");
				if (message.length() > 0) {
					try {
						out.writeBytes("m/" + message + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//---------------------------< Stage setup >--------------------------
		mainScene.getStylesheets().add("/stylesheet.css");
		mainScene.widthProperty().addListener((obs, oldVal, newVal) -> {
			descriptionColumn.setMaxWidth(newVal.doubleValue() - 213);
		});
		mainScene.heightProperty().addListener((obs, oldVal, newVal) -> {
			chatbox.setMinHeight(newVal.doubleValue() - 120);
			chatbox.setMaxHeight(newVal.doubleValue() - 120);
		});
		primaryStage.setTitle("Chatroom");
		primaryStage.setScene(mainScene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					if (connection != null)  {
						out.writeBytes("c/exit\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		primaryStage.show();
	}

	public void startConnection() {
		try {
			System.out.println("Connecting...");
			connection = new Socket("localhost", 3000);
			System.out.println("Connection established!");
			out = new DataOutputStream(connection.getOutputStream());
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getRooms() {
		System.out.println("Request room list...");
		data.clear();
		try {
			out.writeBytes("c/roomlist\n");
			String[] roomlistString = in.readLine().split("/", 0);
			for (int i = 1; i < roomlistString.length; i += 4) {
				RoomData roomData = new RoomData(roomlistString[i], roomlistString[i + 1], Integer.parseInt(roomlistString[i + 2]), Integer.parseInt(roomlistString[i + 3]));
				data.add(roomData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TextField LimitedTextField(int maxLength) {
		TextField tf = new TextField();
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {
				if (newVal.length() > maxLength) {
					tf.setText(oldVal);
				}
			}
		});
		return tf;
	}
	
	class ChatListener extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			System.out.println("Start listening to the chat");
				try {
					message = in.readLine();
					while(!message.equals("r/true")) {
						Platform.runLater(() -> {
							webEngine.executeScript("document.body.innerHTML += \"" + message + "\";");
						});
						message = in.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
	}
}
