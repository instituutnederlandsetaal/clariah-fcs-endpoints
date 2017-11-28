package org.ivdnt.fcs.endpoint.common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivdnt.util.FileUtils;
import org.ivdnt.util.Utils;

public class ClarinCenterList extends HttpServlet {
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		String xmlFileContent = null;
		
		// https://stackoverflow.com/questions/10621580/how-to-get-the-servlet-context-from-servletrequest-in-servlet-2-5
		ServletContext context = request.getSession().getServletContext();
		
		// read the config file from the -config subfolder (outside the WAR)
		
		try {
			
			xmlFileContent = new FileUtils().readConfigFile(context, "clarin_center_ivdnt.xml");
			
			
		// if read that file fails, try to read it from the registry subfolder (inside the WAR)
			
		} catch (IOException e1) {
			
			try {

				StringBuilder sb = new StringBuilder();		
				
				FileInputStream fstream = 
						new FileInputStream("http://localhost:8080/blacklab-sru-server/registry/clarin_center_ivdnt.xml");
				
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				String strLine;
				while ((strLine = br.readLine()) != null) {				
					sb.append(strLine);
					sb.append("\n");
				}
				br.close();
				in.close();
				
				xmlFileContent = sb.toString();
				
			} catch (IOException e2) {
								
				Utils.printStackTrace(e2);
			}
			
		}
		
		// Set response content type
		response.setContentType("text/xml");
	
		
		// write the response
		try {
			
			PrintWriter out = response.getWriter();
			out.println(xmlFileContent);
			
		} catch (IOException e) {
			Utils.printStackTrace(e);
		}
	}
	
}
