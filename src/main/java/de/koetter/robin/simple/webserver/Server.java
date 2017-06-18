package de.koetter.robin.simple.webserver;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server {
	/**  */
	int port = 2708;

	/**
	 * TOOD
	 */
	public Server() {
	}

	/**
	 * TODO
	 * 
	 * @param port
	 */
	public Server(final int port) {
		this.port = port;
	}

	/**
	 * TODO
	 */
	public void startServer() {

		while (true) {
			try {
				final ServerSocket serverSocket = new ServerSocket(13001);
				final Socket clientSocket = serverSocket.accept();

				final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

				final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				final String request = in.readLine();
				final String requestedFile = request.split(" ")[1];

				FileInputStream fin = null;
				boolean fileExist = true;
				try {
					fin = new FileInputStream("C:/Users/Robin/Desktop/myserver" + requestedFile);
				} catch (final Exception ex) {
					fileExist = false;
				}

				String fileType = null;
				try {
					fileType = requestedFile.split("\\.")[1];
				} catch (final Exception e) {
					System.out.println(e);
				}

				if (fileExist) {
					if (fileType.equals("html")) {
						out.println("HTTP/1.1 200 OK");
						out.println("Content-Type: text/html");
						// out.println("Content-Type: image/jpg");
						out.println("\r\n");
						out.println(readFile("C:/Users/Robin/Desktop/myserver" + requestedFile));

					} else if (fileType.equals("jpg")) {
						BufferedImage bimg;
						final DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
						bimg = ImageIO.read(new File("C:/Users/Robin/Desktop/myserver/1.jpg"));
						ImageIO.write(bimg, "JPG", dout);
					}
				} else {
					out.println("HTTP/1.0 200 OK");
					out.println("Content-type: text/html");
					out.println("\r\n");
					out.println("<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not Found</BODY></HTML>");
				}

				out.flush();
				out.close();
				clientSocket.close();
				serverSocket.close();

			} catch (final Exception e) {
				e.printStackTrace();
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
	 */
	public void stopServer() {

	}

}
