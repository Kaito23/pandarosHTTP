package de.koetter.robin.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

import org.apache.log4j.Logger;

public class Server implements Runnable {
	/** Port des Servers */
	int port = 9666;
	/** Pfad zum Server Ordner */
	String serverFolder;
	/** ServerSocket */
	ServerSocket serverSocket = null;
	@Getter
	private boolean running = false;
	/** Der Clientsocket */
	private Socket clientSocket;

	/** Logger */
	private static Logger logger = Logger.getLogger(Server.class);

	/**
	 * TOOD
	 */
	public Server() {
		loadProperties();
	}

	/**
	 * TODO
	 * 
	 * @param port
	 */
	public Server(int port) {
		this();
		this.port = port;
	}

	/**
	 * TODO
	 */
	public void startServer() {
		running = true;
		System.out.println("Versuche Server auf Port " + port + " zu starten.");

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server wurde gestartet.");
		} catch (IOException e1) {
			System.err
					.println("Es scheint bereits ein Server auf dem Port zu laufen!");
			running = false;
			System.exit(0);
		}

		if (serverFolder.isEmpty()) {
			running = false;
			throw new IllegalArgumentException(
					"Es wurde kein g�ltiger Server-Pfad eingegeben!");
		}

		ExecutorService executor = Executors.newFixedThreadPool(15);
		while (running) {
			try {
				clientSocket = serverSocket.accept();
			} catch (SocketException se) {
				System.out.println("Der Server wurde gestoppt.");
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
				System.out.println("Finished all threads");
			} catch (IOException e1) {
				System.out.println("Es ist ein Problem bei der Anfrage aufgetreten.");
				logger.error(e1);
			}

			if (clientSocket != null) {
				try {
					RequestHandler rh = new RequestHandler(clientSocket,
							serverFolder);
					executor.execute(rh);
				} catch (Exception e) {
					System.out.println("Es ist ein Problem bei der Bearbeitung der Anfrage aufgetreten.");
					logger.error(e);
				}
			}

		}

	}

	/**
	 * Stoppt den Server und schlie�t alle sockets.
	 */
	public void stopServer() {
		running = false;
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * L�dt die Wichtigen Daten aus der properties Datei.
	 */
	public void loadProperties() {
		Properties properties = new Properties();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(
					"server.properties"));
		} catch (FileNotFoundException e1) {
			System.out.println("Die properties Dtei konnte nicht gefunden werden!");
			logger.error(e1);
			System.exit(0);
		}
		try {
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			logger.error(e);
			System.out
					.println("Es ist ein Fehler beim Laden der Properties aufgetreten");
			System.exit(0);
		}

		try {
			serverFolder = properties.getProperty("path");
		} catch (Exception e) {
			System.out
					.println("Es wurde kein g�ltiger Pfad in der Propertie eingertagen!\n\rProgramm wird beendet.");
			System.exit(0);
		}
		try {
			port = Integer.parseInt(properties.getProperty("port"));
		} catch (Exception e) {
			System.out
					.println("Es wurde kein g�ltiger Port in der Property eingetragen!");
		}

	}

	/**
	 * Startet den Server.
	 */
	@Override
	public void run() {
		startServer();
	}
}
