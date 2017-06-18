package de.koetter.robin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.*;

import de.koetter.robin.awt.MyTrayIcon;
import de.koetter.robin.server.Server;

public class Main {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(Main.class);
	/** Der Server */
	private static Server server;
	/** Der Threadpool */
	private static ExecutorService executor;

	/**
	 * Programmstart.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		startLogger();	
		executor = Executors.newFixedThreadPool(2);
		server = new Server();
		
		argsCheck(args);
		
		executor.execute(server);
		executor.shutdown();
	}
	
	/**
	 * Startet das Logging.
	 */
	private static void startLogger() {
		try {
			PatternLayout layout = new PatternLayout(
					"%d{ISO8601} %-5p [%t] %c: %m%n");
			DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
					layout, "server.log", //TODO anpassen!!!!!
					"'.'yyyy-MM-dd_HH-mm");
			logger.addAppender(fileAppender);
			logger.setLevel(Level.ALL);
		} catch (Exception ex) {
			logger.error("Unerwarteter Fehler bei Programmstart", ex);
		}
	}
	
	/**
	 * Pr�ft die �bergabeparameter.
	 */
	private static void argsCheck(String[] args) {
		boolean useTray = false;
		
		if (args.length > 0) {
			for (String arg : args) {
				if(arg.equals("-tray")) {
					useTray = true;
					MyTrayIcon myTrayIcon = new MyTrayIcon(server);
					executor.execute(myTrayIcon);
				} else {
					int port;
					try {
						port= Integer.parseInt(arg);
						server = new Server(port);
					}  catch (Exception e) {
						System.out.println("Es wurde kein g�ltiger Port eingegeben!");
					}
				}
			}
		}
		
		if(!useTray) {
			CMDLineReader cmdLineReader = new CMDLineReader(server);
			executor.execute(cmdLineReader);
		}
	}

}
