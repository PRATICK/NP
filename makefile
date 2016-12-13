JCC = javac
JFLAGS = -g
default: Client.class Server.class
Client.class: Client.java
	$(JCC) -d bin -cp ./java-getopt.jar $(JFLAGS) Client.java

Server.class: Server.java
	$(JCC) -d bin $(JFLAGS) Server.java

clean: 
	$(RM) *.class

