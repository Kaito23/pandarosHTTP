package de.koetter.robin;

import java.util.Scanner;

import de.koetter.robin.server.Server;

public class CMDLineReader implements Runnable {
	/** The Server */
	private Server server;
	
	/**
	 * Handelt die inputs in der Konsole.
	 * 
	 * @param server der Server, der verwaltet werden soll.
	 */
	public CMDLineReader(Server server) {
		this.server = server;
	}

	/**
	 * TODO
	 */
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			// TODO Auto-generated method stub
			String input = "";

			input = scanner.nextLine();

			switch (input) {
			case "stop":
				stopServer();
				break;
			case "start":
				startServer();
				break;
			case "exit":
				running = false;
				exit();
				break;
			case "help":
				showHelp();
				break;
			case "status":
				if(server.isRunning()) {
					System.out.println("Der Server l�uft.");
				} else {
					System.out.println("Der Server l�ugt nicht.");
				}
				break;
			default:
				System.out.println("Ung�ltiger Befehl! siehe auch help!");
				break;
			}
		}
		
		scanner.close();
	}

	/**
	 * Zeigt die Hilfe.
	 */
	private void showHelp() {
		System.out.println("M�gliche commands:");
		System.out.println("start : Startet den Server");
		System.out.println("stop : Stopt den Server");
		System.out.println("exit : Beendet das Programm");
		System.out.println("hilfe : zeigt diese Hilfe");
	}

	/**
	 * Stops the server and xits the programm.
	 */
	private void exit() {
		if (server.isRunning()) {
			stopServer();
		}

		System.exit(0);
	}

	/**
	 * Stops the server.
	 */
	private void stopServer() {
		if (server.isRunning()) {
			server.stopServer();
		} else {
			System.out.println("Der Server l�uft nicht.");
		}
	}

	/**
	 * Starts the server.
	 */
	private void startServer() {
		if (!server.isRunning()) {
			server.startServer();
		} else {
			System.out.println("Der Server l�uft bereits.");
		}
	}

}
