package com.noeticworld.sgw.util;

import java.io.*;
import java.net.*;


public class TCPClient {
	   
	private static Socket clientSocket;
		
	public static void Connect(String ServerIP, int ServerPort) {
		//create client socket, connect to server
			try {
				clientSocket = new Socket(ServerIP,ServerPort);

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public boolean socketIsOpen(){
		return clientSocket.isConnected();
	}
	
	//Input Stream
	protected InputStream Read() {
		InputStream MyInputStream=null;
		try {
			MyInputStream =  TCPClient.clientSocket.getInputStream();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MyInputStream;
	}
	
	//Get Stream	
	protected OutputStream getStream() throws SocketException {
		OutputStream outputStream=null;
		System.out.println("Setting Time Out");
		try {
			outputStream =  TCPClient.clientSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream;
	}

	

}