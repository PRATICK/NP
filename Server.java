// File Name Server.java

import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Server extends Thread
{
	private ServerSocket serverSocket;
	private String DBPath;
	public Server(int port, String DBPath) throws IOException
	{
		serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(200000);
		this.DBPath=DBPath;
	}

	public void run() {
		while(true) {
			Connection c = null;
			Statement stmt = null;
			try
			{
				System.out.println("------");
				System.out.println("Waiting for client on port " +
						serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Server has connected!\n");
				PrintWriter out = new PrintWriter(server.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new
						InputStreamReader(server.getInputStream()));                   /*Read from Client*/
				String line = in.readLine();
				String[] a = line.split(";");
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:"+this.DBPath);
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					stmt = c.createStatement();

					String sql="SELECT * FROM EVENTS";
					ResultSet resultSet = stmt.executeQuery(sql);
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					Date date = new Date();
					//String currentTime = dateFormat.format(date);

					while(resultSet.next()){
						//System.out.println(resultSet.getString("TIME"));
						Date date1 = dateFormat.parse(resultSet.getString("TIME").replace(":", " ").replace("h", ":").replace("m", ":").replace("s", ".").replace("Z", ""));
						//System.out.println(date1);
						int check = date1.compareTo(date);
						if(check < 0 ){//&& a[2].equals(resultSet.getString("EVENT_DESCRIPTION"))
							stmt.executeUpdate("UPDATE EVENTS set IS_EXPIRED = 1 where ID = "+resultSet.getInt("ID"));

						}
					}


					String deleteSQL = "DELETE FROM EVENTS WHERE IS_EXPIRED =?";;
					PreparedStatement deletion = c.prepareStatement(deleteSQL);
					deletion.setInt(1, 1);
					//execute insert SQL statement
					if (deletion.executeUpdate()>= 1){
						StringBuffer b4 = new StringBuffer("Expired Event removed from event list...");
						out.write(b4.toString());
						out.flush();
					}

					if(a[0].equals("GET_NEXT_EVENTS") ){ 

						String sql1="SELECT * FROM EVENTS WHERE TIME ='"+a[2]+"'AND CLASS_NAME ='"+a[1]+"';";
						ResultSet rs1 = stmt.executeQuery(sql1);

						int count = 0;
						String ed = null,t=null,edesc=null,cn = null;
						String message="";
						while(rs1.next()){
							ed = rs1.getString("EVENT_DEFINITION");
							t=rs1.getString("TIME");
							edesc=rs1.getString("EVENT_DESCRIPTION");
							cn=rs1.getString("CLASS_NAME");
							message += ed+";"+t+";"+edesc+";"+cn+";";
							++count;
						}
						rs1.close();
						StringBuffer b1 = new StringBuffer("EVENTS;");
						b1.append(count+";"+message);
						out.write(b1.toString());
						out.flush();
					}else if (a[0].equals("EVENT_DEFINITION")){


						String insertTableSQL = "INSERT INTO EVENTS"
								+ "(EVENT_DEFINITION, TIME, EVENT_DESCRIPTION, CLASS_NAME) VALUES"
								+ "(?,?,?,?)";
						PreparedStatement insertion = c.prepareStatement(insertTableSQL);
						insertion.setString(1, a[0]);
						insertion.setString(2, a[1]);
						insertion.setString(3, a[2]);
						insertion.setString(4, a[3]);
						// execute insert SQL statement
						insertion.executeUpdate();

						String sql3="SELECT * FROM EVENTS WHERE EVENT_DESCRIPTION = '"+a[2]+"'";
						ResultSet rs3 = stmt.executeQuery(sql3);
						StringBuffer b3 = new StringBuffer("OK;");
						while(rs3.next()){
							b3.append(rs3.getString("EVENT_DEFINITION"));
							b3.append(";");
							b3.append(rs3.getString("TIME"));
							b3.append(";");
							b3.append(rs3.getString("EVENT_DESCRIPTION"));
							b3.append(";");
							b3.append(rs3.getString("CLASS_NAME"));
						}
						rs3.close();
						out.write(b3.toString());
						out.flush();


					}


					stmt.close();
					c.commit();
					c.close();

				} catch ( Exception e ) {
					e.printStackTrace();
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);
				}
				System.out.println("Records shown successfully");
				out.close();
				in.close();
				server.close();

			}catch(SocketTimeoutException s)
			{
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}
	public static void main(String [] args)
	{
		int port = Integer.parseInt(args[0]);
		String DBPath = args[1];
		try
		{
			Thread t = new Server(port,DBPath);
			t.start();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
