package com.morgan.demo.codetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket连接中的服务端。
 * 
 * @author Morgan.Ji
 * 
 */
public class SocketServer {

	private ServerSocket sever;

	public SocketServer(int port) {
		try {
			sever = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void beginListen() {
		while (true) {
			try {
				final Socket socket = sever.accept();
				new Thread(new Runnable() {
					public void run() {
						BufferedReader in;
						try {
							in = new BufferedReader(new InputStreamReader(
									socket.getInputStream(), "UTF-8"));
							PrintWriter out = new PrintWriter(socket.getOutputStream());
							while (!socket.isClosed()) {
								String str;
								str = in.readLine();
								out.println("Hello world! " + str);
								out.flush();
								if (str == null || str.equals("end"))
									break;
								System.out.println(str);
							}
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}