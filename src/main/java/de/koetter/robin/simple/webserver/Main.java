package de.koetter.robin.simple.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	/**
	 * TODO
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		/*try {
			ServerSocket serverSocket = new ServerSocket(13005);
			Socket clientSocket = serverSocket.accept();
		
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
					true);
		
			out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/html");
			out.println("\r\n");
			out.println(readFile("C:/Users/Robin/Desktop/myserver/index.html"));
		
			out.flush();
			out.close();
			clientSocket.close();
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		final Server server = new Server();
		server.startServer();
		/*
		System.out.println(args.length);
		
		if (args.length > 0 && args[0].equals("-gui")) {
			Application.launch(MainFX.class);
		} else {
			Server server;
			if (args.length == 3 && args[2] != null) {
				server = new Server();
			} else {
				server = new Server(Integer.parseInt(args[3]));
			}
			server.startServer();
		}
		
		if (args.length == 2 && args[1].equals("-tray")) {
			// Create tray icon TODO
		}*/

	}

	/**
	 * TODO
	 * 
	 * @param folder
	 */
	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
				System.out.println("--> " + fileEntry.getName());
			} else {
				System.out.println(fileEntry.getName());
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static String readFile(final String fileName) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			final StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

}
