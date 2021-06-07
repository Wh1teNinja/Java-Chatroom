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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatroomServer {
	public static List<Room> rooms = new ArrayList<>();
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(3000, 10);
			System.out.println("Server started...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true) {
			try {
				System.out.println("Waiting for a client...");
				Socket socket = serverSocket.accept();
				System.out.println("Client found. Creating new user...");
				Thread user = new Thread(new User(socket));
				user.start();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	
	
}
