/*--------------------------------------------------------

1. Name / Date: Chadwick Rivera-Crum / 4/17/20

2. Java version used, if not the official version for the class: Java 1.8



3. Precise command-line compilation examples / instructions:


> javac JokeClientAdmin.java


4. Precise examples / instructions to run this program:


For using single server:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

For using TWO servers:

> java JokeServer [secondary] (do not use brackets, this is just simply optional)
> java JokeClient localhost localhost
> java JokeClientAdmin localhost localhost

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.


 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java
 e. JokeLog.txt

5. Notes:



----------------------------------------------------------*/
import java.io.*;//imports java library for reading and writing
import java.net.*;//imports important library for networking


public class JokeClientAdmin {
	/* implements INET code to get hostname argument, pass argument to sendModeChange() and communicate with the user
	Secondary Server Implentation:
	Using flags to set whether a second server is in use, then two additional flags that give which server is currently being connected to. 
	*/
	public static boolean secondaryServerFlag=false;
	public static boolean usingSecondServer=false;
	public static boolean usingFirstServer=false;
	public static void main(String args[]) throws IOException{
		String serverName=null;//stores the serverName to be passs to sendModeChange()
		String serverName2=null;
		if (args.length < 1) {
			//if there were no arguments given, then simply use localhost and connect to primary port. 
			serverName= "localhost";
			System.out.println("Connecting with " + serverName + "at port: 5050.");
			
		}
		
		else if (args.length >1) {
			//if two arguments were given, then use the first for the primary server, the second for the secondary server and set appropriate flags. 

			serverName=args[0];
			serverName2=args[1];
			secondaryServerFlag=true;//sets flag that we are in a two server situation.
			usingFirstServer=true;//sets logic for later use that we are using first server.
			System.out.println("Server one: " + serverName + "at port: 5050. ");
			System.out.println("Server two: " + serverName2 + "at port: 5051. ");
		}
		else {//if a single argument was given, then use that as serverName.
		serverName = args[0];
		System.out.println("Connecting with " + serverName + "at port: 5050.");}//communicating to user the port it is connecting to
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));//establishes a buffered input to get input from the user

		try{
			String response;//will store the users input (expecting this to be empty as they should just be using (enter))
			
			
			do{
				/* This Do-While loop accepts the response from the user and if the String response does not contain the word "quit," then it checks to see if 
				command matches switch server command, if not, it signals to change modes of whichever server is in use.
				*/
			if (secondaryServerFlag==true)//shows correct commands for a two server situation.
				System.out.print("Press (enter) to connect to server and toggle between Joke/Proverb Modes, press (s) to toggle between Server 1 and 2,(quit) to cancel ");
			else//if we are only using a single server, then you can either toggle OR quit. 
				System.out.print("Press (enter) to connect to server and toggle between Joke/Proverb Modes, (quit) to cancel ");
				System.out.flush();//ensuring there are no elements present in the stream. This is most likely not necessary
				response = in.readLine();//reads user response
				if (secondaryServerFlag==false && response.equals("s")) System.out.println("No secondary server being used.\n");
				else if (response.indexOf("quit") < 0){//if command is NOT quit, then enter loop
					
					if(secondaryServerFlag==true){//this checks for the presence of a two server situation.
						if(response.equals("s")){//if command is equal to switch command then enter block
							if (usingFirstServer==true){//if the command is to switch and we are using first server, then switch to second.
								usingFirstServer=false;//sets flag that we are no longer using first server
								usingSecondServer=true;//sets flag that we are now using second server
								System.out.print("Now communicating with:"+ serverName2	+ " at port: 5051.\n");

								
							}
							else if(usingFirstServer==false){//if the command is to switch and we are NOT using first server, then switch to first.
								usingFirstServer=true;
								usingSecondServer=false;
							System.out.print("Now communicating with:"+ serverName	+ " at port: 5050.\n");
							}
						}
						else{//this else block is if the command is not to QUIT or switch then simply change modes of whichever server is in use.
							if(usingFirstServer==true){//if first server is in use, send server1 serverName.
							sendModeChange(serverName);
						}
							else if (usingSecondServer==true){//if second server is in use, then send server2 name. 
								sendModeChange(serverName2);
							}
							else sendModeChange(serverName);//sends server1 name to change mode. 
						}
					
					}
					else sendModeChange(serverName);//retains original functionality of single server situation. 

						
					}
					
				
			} while (response.indexOf("quit") < 0);//awaiting the user input of "quit"
			

      } catch(IOException x) { x.printStackTrace();}//catches an exception for IO errors and prints where the issue occurs
}

static void sendModeChange(String serverName){
	/* This method uses the serverName to establish a connection and receive/send information. It then accepts a response from the server, which may 
	not be necessary, but this will be changed if I determine it to be useless 
	Second Server Implentation:
	The flags are used to know whether or not to check for which server to send a command to.
	*/
	Socket sock;//uses the implementation of socket to create an endpoint between JokeClientAdmin and JokeServer stored in sock 
	BufferedReader getResponse;//this is to store a response, may use this to confirm a change has been made by server
	PrintStream sendChangeCommand;//this stores a command
	String textFromServer;//
	String request="change";//this is simple implementation that only sends a  "change" command

	try{
		
		if (secondaryServerFlag==true){//if there is the presence of a secondary server, check which to use

		if (usingSecondServer==true){ //if using secondary server, then using port 5051.
			sock = new Socket(serverName, 5051);
			
		}
		else   {
			sock=new Socket(serverName, 5050);
			
		}
		}//creates stream at port 5050 which is different than JokeClient
		else {//this retains original functionality of a single server situation. 
			sock = new Socket(serverName, 5050);
		}
		//getResponse = new BufferedReader(new InputStreamReader(sock.getInputStream()));//this retrieves a response to be stored as the "in" from JokeServer

		sendChangeCommand = new PrintStream(sock.getOutputStream());//this is the  outgoing stream for sending commands to JokeServer

		sendChangeCommand.println(request);//this may be used to further implement a shutdown command, but just sends a generic request for changing modes 
		sendChangeCommand.flush();//flushes stream 
		System.out.println("Sending Change Mode Command");//sends information to console about change 

		sock.close();//closes connection between JokeClientAdmin and JokeServer

	}catch(IOException x){
			System.out.println("Socket error.");
			x.printStackTrace();//catches an error with IO and prints error to console
		}

	}
}




