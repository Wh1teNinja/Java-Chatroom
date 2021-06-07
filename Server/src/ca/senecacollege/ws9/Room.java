package ca.senecacollege.ws9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Room {
	String title;
	String description;
	int maxSlots;
	List<User> users = new ArrayList<>();
	
	public Room(String title, String description, int maxSlots) {
		this.title = title;
		this.description = description;
		this.maxSlots = maxSlots;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getMaxSlots() {
		return maxSlots;
	}

	public List<User> getUsers() {
		return users;
	}
	
	public boolean addUser(User user) {
		System.out.println("Adding user");
		if (users.size() < maxSlots) {
			users.add(user);
			for (User u : users) {
				try {
					if (!u.getNickname().equals(user.getNickname())) {
						u.getOut().writeBytes("<p style='color: grey'>" + user.getNickname() + " joined the room.</p>\n");
					} else {
						u.getOut().writeBytes("<p style='color: grey'>Welcome to room " + title + "!</p>\n");
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean removeUser(User user) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).equals(user)) {
				users.remove(i);
				for (User u : users) {
					try {
						u.getOut().writeBytes("<p style='color: grey'>" + user.getNickname() + " left the room</p>\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public void processMessage(String message) {
		for (User user : users) {
			try {
				user.getOut().writeBytes(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
