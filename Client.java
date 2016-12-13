// File Name Client.java

import java.net.*;
import java.io.*;
import gnu.getopt.Getopt;

public class Client {
	public static void main(String [] args) {
		if(args.length == 0) {
			printUsage();
			System.exit(1);
		} 	      

		Getopt g = new Getopt("Client", args, "s:p:e:");
		int c;
		String serverName = "";
		String ports = "";
		// retrieving port from command line
		int port =  0;
		String event = "";
		while((c = g.getopt()) != -1) {
			switch (c) {
			case 'e': event = g.getOptarg();
			break;
			case 's' :serverName = g.getOptarg();
			break;
			case 'p' : ports = g.getOptarg();
			port = Integer.parseInt(ports);
			break;
			case '?':printUsage();
			break;
			default : printUsage();
			break;

			}
		}
		try
		{
			Socket skt = new Socket(serverName, port);        /*Connects to server*/
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.println(event);       /*Writes to server*/

			BufferedReader in = new BufferedReader(new
					InputStreamReader(skt.getInputStream()));      /*Reads from server*/
			System.out.println(in.readLine());

			out.close();    /*Closes all*/
			in.close();
			skt.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}

	}


	private static void printUsage() {
		String usage = "Usage: [-s] [-p] [-e] ";
		System.err.println(usage);
	}
}
