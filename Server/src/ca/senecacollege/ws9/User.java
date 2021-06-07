package ca.senecacollege.ws9;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class User implements Runnable {
	String nickname;
	String color;
	Socket socket;
	BufferedReader in;
	DataOutputStream out;
	Room enteredRoom;
	
	User(Socket socket) {
		try {
			this.socket = socket;
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			nickname = in.readLine();
			color = in.readLine();
			System.out.println("User created. User name: " + nickname + " Color: " + color);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getNickname() {
		return nickname;
	}

	public String getColor() {
		return color;
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	@Override
	public void run() {
		String message = null;
		try {
			message = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!message.equals("c/exit")) {
			if (message.startsWith("c/")) {
				//--------------------< Enter Room >------------------------
				if (message.endsWith("/join")) {
					for (int i = 0; i < ChatroomServer.rooms.size(); i++) {
						Room room = ChatroomServer.rooms.get(i);
						if (room.getTitle().equals(message.split("/")[1])) {
							if (room.addUser(this)) {
								try {
									enteredRoom = room;
									out.writeBytes("r/true\n");
									System.out.println("User " + this.getNickname() + " joined room " + room.getTitle());
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								try {
									out.writeBytes("\nr/full\n");
									System.out.println("User " + this.getNickname() + " failed to join room " + room.getTitle());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
						}
					}
					if (enteredRoom == null) {
						try {
							out.writeBytes("\nr/wrongname\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				//-------------------< Leave Room >----------------------
				} else if (message.endsWith("/leave")) {
					if (enteredRoom.removeUser(this)) {
						try {
							if (enteredRoom.getUsers().size() == 0) 
								ChatroomServer.rooms.remove(enteredRoom);
							enteredRoom = null;
							out.writeBytes("r/true\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						try {
							out.writeBytes("r/false\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				//------------------< Create Room >---------------------
				} else if (message.endsWith("/create")) {
					try {
						System.out.println("Creating the room...");
						String roomName = in.readLine();
						String roomDesc = in.readLine();
						int maxSlots = in.read();
						Room newRoom = new Room(roomName, roomDesc, maxSlots);
						newRoom.addUser(this);
						enteredRoom = newRoom;
						ChatroomServer.rooms.add(newRoom);
						System.out.println("Room created!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//---------------< Get list of rooms >-------------------
				else if (message.endsWith("/roomlist")) {
					try {
						System.out.println("Retrieving room list...");
						String roomlist = "";
						for (Room room : ChatroomServer.rooms) {
							roomlist += "/" + room.getTitle() + "/" + room.getDescription() + "/" + room.getUsers().size() + "/" + room.getMaxSlots();
						}
						out.writeBytes(roomlist + "\n");
						System.out.println("Room list retrieved.");
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				String htmlMessage = "<p><span style='color:" + this.getColor() + "'>[" + this.getNickname() + "]</span>: " + message.split("/")[1] + "</p>\n"; 
				enteredRoom.processMessage(htmlMessage);
			}
			try {
				message = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (enteredRoom != null) {
			try {
				out.writeBytes("r/true\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			enteredRoom.removeUser(this);
			if (enteredRoom.getUsers().size() == 0) 
				ChatroomServer.rooms.remove(enteredRoom);
			enteredRoom = null;
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
