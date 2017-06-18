package de.koetter.robin.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public class RequestHandler implements Runnable {

	/** Logger */
	private static Logger logger = Logger.getLogger(RequestHandler.class);
	/** Der client-Socket */
	private Socket clientSocket;
	/** Server folder */
	private String serverFolder;

	/**
	 * Der RequestHandler, der dem Client die geforderte Ressource liefert.
	 * 
	 * @param clientSocket
	 *            der ClientSocket
	 * @param serverFolder
	 *            der Pfad des Server-Ordners
	 */
	public RequestHandler(Socket clientSocket, String serverFolder) {
		this.clientSocket = clientSocket;
		this.serverFolder = serverFolder;
	}

	/**
	 * Started ein RequestHandling.
	 */
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String request = in.readLine();
			
			String requestedFile = "";
			if(request != null &&  !request.isEmpty()) {
				requestedFile = request.split(" ")[1];
			}

			generateBody(serverFolder + requestedFile);

			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Fehler beim handeln des Inputs.");
			logger.error(e);
		}

	}

	/**
	 * Pr�ft den MimeType.
	 * 
	 * @param mime
	 *            der vom Client gesendete Mime.
	 * @return den Mimetype als String
	 */
	private String checkMimeType(String mime) {
		Path filename = Paths.get(mime);
		String type = "";
		try {
			type = Files.probeContentType(filename);
			if (type == null) {
				System.err.format("'%s' has an" + " unknown filetype.%n",
						filename);
			}
		} catch (IOException x) {
			System.err.println("Fehler beim Pr�fen des Mimetype");
			logger.error(x);
		}

		return type;
	}

	/**
	 * Zeigt eine statische 404 Seite an.
	 * 
	 * @param out
	 *            der OutputStream
	 * @throws IOException
	 *             wirft eine IOException wenn die Seite nicht angezeigt werden
	 *             konnte.
	 */
	private void showStatic404(OutputStream out) throws IOException {
		out.write("HTTP/1.0 200 OK\n\r".getBytes());
		out.write("Content-type: text/html\n\r".getBytes());
		out.write("\r\n".getBytes());
		out.write("<html><head><title>404 not found</title></head><body><h1>Error 404</h1>Die Seite konnte nicht gefunden werden</body></html>".getBytes());

		out.flush();
		out.close();
	}

	/**
	 * Sendet die Daten an den Client.
	 * @param requestedFileBytes die inputBytes
	 * 
	 * @throws IOException
	 *             Wirft IOException wenn kein flush durchgef�hrt werden konnte.
	 */
	private void send(OutputStream out, byte[] requestedFileBytes)
			throws IOException {
		out.write(requestedFileBytes);
		out.flush();
		out.close();
	}

	/**
	 * Packt den Body in den OutputStream.
	 * 
	 * @param filePath
	 *            der Pfad zur Datei
	 */
	private void generateBody(String filePath) {
		try {
			OutputStream out = clientSocket.getOutputStream();
			
			if (fileExistingChecker(filePath)) {
				printHead(out, filePath);
				File reqFile = new File(filePath);
				FileInputStream fin = new FileInputStream(reqFile);
				byte[] requestedFileBytes = new byte[(int) reqFile.length()];
				BufferedInputStream bin = new BufferedInputStream(fin);
				bin.read(requestedFileBytes, 0, requestedFileBytes.length);
				
				send(out, requestedFileBytes);
				bin.close();
			} else {
				showStatic404(out);
			}
		} catch (Exception e) {
			System.err.println("Fehler bei der Ausgabe des Bodys");
			logger.error(e);
		}
	}

	/**
	 * Gibt den Head in den OutputStream.
	 * 
	 * @param out
	 *            der OutputStream
	 * @param requestedFile
	 *            die vom Client angeforderte Datei
	 * @throws IOException
	 *             wirft IOException, wenn es Fehler beim Schreiben in den
	 *             OutputStream gibt.
	 */
	private void printHead(OutputStream out, String requestedFile)
			throws IOException {
		out.write("HTTP/1.1 200 OK\r\n".getBytes());
		out.write(("Content-Type: " + checkMimeType(requestedFile) + "\r\n").getBytes());
		out.write("\r\n".getBytes());
	}

	/**
	 * Pr�ft ob unter dem angegebenen Pfad eine Datei liegt.
	 * 
	 * @param filePath
	 *            der Pfad zu der Datei
	 * @return true, wenn eine Datei gefunden wurde
	 */
	private boolean fileExistingChecker(String filePath) {
		boolean fileExist = true;
		File reqFile = new File(filePath);
		try {
			new FileInputStream(reqFile);
		} catch (Exception ex) {
			fileExist = false;
		}
		return fileExist;
	}
}
