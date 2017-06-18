package de.koetter.robin.awt;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import de.koetter.robin.server.Server;

public class MyTrayIcon implements Runnable {

	/** Logger */
	private static Logger logger = Logger.getLogger(MyTrayIcon.class);

	/** Der Server */
	private Server server;
	/** MenuItem um den Server zu Starten */
	private MenuItem startItem = new MenuItem("Server start");
	/** MenuItem um den Server zu stoppen */
	private MenuItem stopItem = new MenuItem("Stop server");

	/**
	 * Erstellt ein Trayicon.
	 */
	public MyTrayIcon(Server server) {
		this.server = server;

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
			logger.error(ex);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			logger.error(ex);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			logger.error(ex);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
	}

	/**
	 * Zeigt das TrayIcon An
	 */
	private void createAndShowGUI() {
		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(createImage("../images/url.png",
				"tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem("About");
		MenuItem exitItem = new MenuItem("Exit");

		// Add components to popup menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(startItem);
		popup.add(stopItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			logger.error(e);
			return;
		}

		aboutItem.addActionListener(action -> {
			JOptionPane.showMessageDialog(null,
					"Von Robin K�tter");
		});

		stopItem.addActionListener(action -> {
			stopServer();
		});

		startItem.addActionListener(action -> {
			startServer();
		});

		exitItem.addActionListener(action -> {
			tray.remove(trayIcon);

			if (server.isRunning()) {
				stopServer();
			}

			System.exit(0);
		});
	}

	/**
	 * Obtain the image URL
	 * 
	 * @param path
	 *            der Pfad zur Datei
	 * @param description
	 *            die Beschreibung
	 * @return das Image
	 */
	protected static Image createImage(String path, String description) {
		URL imageURL = MyTrayIcon.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	/**
	 * Stoppt den Server
	 */
	private void stopServer() {
		server.stopServer();
		startItem.setEnabled(true);
		stopItem.setEnabled(false);
	}

	/**
	 * Startet den Server.
	 */
	private void startServer() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(server);
		executor.shutdown();
		startItem.setEnabled(false);
		stopItem.setEnabled(true);
	}

	/**
	 * Zeigt die GUI.
	 */
	@Override
	public void run() {
		createAndShowGUI();
		while (true) {
			if (server != null) {
				synchronized (server) {
					if (server.isRunning()) {
						startItem.setEnabled(false);
						stopItem.setEnabled(true);
					} else {
						startItem.setEnabled(true);
						stopItem.setEnabled(false);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e);
					System.out
							.println("Es ist ein Fehler beim �berpr�fen des Serverstatus aufgetreten.");
				}
			}
		}
	}
}