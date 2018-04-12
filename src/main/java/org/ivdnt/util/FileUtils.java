package org.ivdnt.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Several methods to read file content
 * 
 * @author fannee, peter
 *
 */
public class FileUtils {

	private String filePath;
	private String userFilePath;
	private String warFilePath;
	ServletContext context;

	// -------------------------------------------------------------
	// constructor

	public FileUtils(String filepath) {

		this.filePath = filepath;
	}

	public FileUtils(ServletContext context, String filepath) {

		this.context = context;
		this.filePath = filepath;
		// translate path into the path to the config file
		String contextpath = this.context.getRealPath(this.filePath);
		this.userFilePath = contextpath.replace("blacklab-sru-server", "blacklab-sru-server-config");
		this.warFilePath = File.separator + "WEB-INF" + File.separator + this.filePath;

	}

	private DataInputStream createInputStream(String filepath) throws FileNotFoundException {
		FileInputStream fstream = new FileInputStream(filepath);
		return new DataInputStream(fstream);
	}

	// -------------------------------------------------------------
	// write to files

	public void writeStringToFile(String content) {

		FileOutputStream fos;
		OutputStreamWriter out;

		try {
			fos = new FileOutputStream(filePath);
			out = new OutputStreamWriter(fos, "UTF-8");

			out.write(content);

			out.close();
			fos.close();

		} catch (IOException e) {
			throw new RuntimeException("Not able to write string to file: " + filePath, e);
		}

	}

	// -------------------------------------------------------------
	// read files

	// get string from file (moved from nederlab.stuff.IO.java)

	public String readStringFromFile() {
		try {
			FileReader r = new FileReader(new File(this.filePath));
			BufferedReader b = new BufferedReader(r);
			String l;
			StringBuilder sb = new StringBuilder();

			while ((l = b.readLine()) != null) {
				sb.append(l);
				sb.append("\n");
			}
			b.close();
			return sb.toString();

		} catch (Exception e) {
			throw new RuntimeException("Not able to read string from file", e);
		}
	}


	private String streamToString(InputStream stream) {
		StringBuilder result = new StringBuilder("");
		Scanner scanner = new Scanner(stream);

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.append(line).append("\n");
		}

		scanner.close();

		return result.toString();
	}

	// Unused
	/*
	 * // https://stackoverflow.com/questions/14089146/file-loading-by-getclass-
	 * getresource public File getResourceAsFile() { try { InputStream in =
	 * ClassLoader.getSystemClassLoader().getResourceAsStream(this.filepath); if (in
	 * == null) { return null; }
	 * 
	 * File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
	 * tempFile.deleteOnExit();
	 * 
	 * try (FileOutputStream out = new FileOutputStream(tempFile)) { // copy stream
	 * byte[] buffer = new byte[1024]; int bytesRead; while ((bytesRead =
	 * in.read(buffer)) != -1) { out.write(buffer, 0, bytesRead); } }
	 * 
	 * return tempFile; } catch (IOException e) {
	 * 
	 * throw new RuntimeException("Error while reading resource " + this.filepath,
	 * e); } }
	 
	// get list from file (moved from util.StringUtils.java)

	public List<String> readListFromFile() throws IOException {
		String l;
		BufferedReader b = new BufferedReader(new FileReader(this.filePath));

		List<String> L = new ArrayList<String>();
		while ((l = b.readLine()) != null) {
			L.add(l);
		}
		b.close();
		return L;
	}

	// -------------------------------------------------------------
	// get file from resource folder in different formats:
	// as string or as file

	public String getResourceAsString(String filepath) throws FileNotFoundException {
		URL resource = null;
		try {
			resource = this.context.getResource(filepath);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		if (resource == null) {
			throw new FileNotFoundException("File not found: " + filepath);
		}

		InputStream s = null;
		try {
			s = resource.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return streamToString(s);
	}*/

	public URL readConfigFileAsURL() {
		// return path as URL
		// https://stackoverflow.com/questions/6098472/pass-a-local-file-in-to-url-in-java
		URL url = null;
		File file = new File(userFilePath);
		if (file.exists()) {
			// Try user-defined config file in blacklab-sru-server-config directory
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException("Could not get URL from path: " + userFilePath, e);
			}
			System.err.println("[config] " + filePath + " read from blacklab-sru-server-config/");
		} else {
			// If that fails, read config file from WEB-INF directory in war
			try {
				url = context.getResource(warFilePath);
				if (url == null) {
					throw new FileNotFoundException();
				}
			} catch (IOException e) {
				throw new RuntimeException("Not able to get resource from war file: " + warFilePath, e);
			}
			System.err.println("[config] " + filePath + " read from WEB-INF");
		}

		return url;
	}

	public String readConfigFileAsString() {
		InputStream in = readConfigFileAsStream();

		return streamToString(in);
	}

	public Document readConfigFileAsDoc() {
		InputStream in = readConfigFileAsStream();

		Document doc = null;

		// Convert stream to document object
		try {
			doc = streamToDoc(in);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException("Not able to convert stream to doc: " + in, e);
		}

		return doc;
	}

	// General method, which tries to read config file from user config directory,
	// then defaults to WEB-INF directory packaged with war
	// Returns InputStream
	private InputStream readConfigFileAsStream() {
		InputStream in = null;
		URL url = null;
		try {
			// Try user-defined config file in blacklab-sru-server-config directory
			in = createInputStream(userFilePath);
			System.err.println("[config] " + filePath + " read from blacklab-sru-server-config/");
		} catch (FileNotFoundException e) {
			// If that fails, read config file from WEB-INF directory in war
			try {
				url = context.getResource(warFilePath);
				if (url == null) {
					throw new FileNotFoundException();
				}
				in = url.openStream();
			} catch (IOException e1) {
				throw new RuntimeException("Not able to get resource from war file: " + warFilePath, e1);
			}
			System.err.println("[config] " + filePath + " read from WEB-INF");
		}
		return in;
	}

	public static Document streamToDoc(InputStream istr)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);

		Document doc = null;
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(istr);
		istr.close();
		return doc;

	}

}
