Student name: Rishi Dhar and Pratick Bakhtiani

STEPS TO FOLLOW WHILE IMPLEMENTING THE CODE:

The folder "Project_Milestone2" contains 7 files, namely,
1. run.sh
2. Server.java
3. Client.java
4. test.db
5. makefile
6. README.txt
7. Documentation.pdf

STEP 1: ./run.sh <Desired port number>
	Here the shell script invokes the makefile and compiles the two classes i.e. Server.java and Client.java with their respective jar files i.e. java-getopt.jar 
	and sqlite-jdbc-3.7.2.jar in their classpath, creating a Server.class and Client.class respectively in the bin folder. 
	The server is created on this particular terminal on port 3333 and listens for an incoming client connection request. 
STEP 2: Start a client on a new terminal.
	There are 2 scenarios to demonstrate the client- server connection.
	First is, EVENT_DEFINITION, wherein the string passed by the client is accepted by the server to check if the event passed exists in the local sqlite database 
	i.e. test.db, if it contains it returns the arguments to the client with an "OK" appended in front of the arguments. 
	Second is, GET_NEXT_EVENTS, wherein the string passed by the client is accepted by the server to check if there are multiple events happening at the same time, 
	in the same class name which is again compared to the data in the sqlite database i.e. test.db, if it contains that particular event, it will return EVENTS 
	followed by the number of times and the arguments appended to it.

The following demonstrates an example to start a client and primarily three scenarios being taken care in our Client-Server architecture:
1. java -cp ./bin:./java-getopt.jar:./sqlite-jdbc.jar Client -s localhost -p 1234 "EVENT_DEFINITION;2015-03-15:18h30m00s001Z;Meeting with the user.;CSE5232"
where -s: serverName, the server name can be passed through this argument.
	 -p: port, the port number can be specified on which the client wants to send a request to the server to connect.
	 -e: event, the string event contains 4 parts i.e. EVENT_DEFINITION, EVENT_DESCRIPTION, TIME & CLASS_NAME respectively, separated by ";".

In this case the server inserts the event into test.db and returns with following to acknowledge it’s successful insertion and retrieval from the database like,
OK;EVENT_DEFINITION;2015-03-12:18h30m00s001Z;Meeting with the user.;CSE5232

2. java -cp ./bin:./java-getopt.jar:./sqlite-jdbc.jar Client -s localhost -p 1234 -e "GET_NEXT_EVENTS;CSE5232;2015-03-12:18h30m00s000Z"
In this case the server checks for events happening at this particular time and under this particular class name, if more than one events occur at same time, 
it will return the following statement:
EVENTS;2;EVENT_DEFINITION;2015-03-12 18:30:00:001;Meeting with the user.;CSE5232;EVENT_DEFINITION;2015-03-12 18:30:00:001;Grandma visits Uncle Tom.;CSE5232

3. If the event time has passed, a field named as IS_EXPIRED is updted to ‘1’ and then every row which has this field vale (IS_EXPIRED) as ‘1’ is deleted from 
the database and a message is appended to the output stream as ” Expired Event removed from event list...”.
