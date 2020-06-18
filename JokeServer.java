/*--------------------------------------------------------

1.  Chadwick Rivera-Crum 


----------------------------------------------------------*/

import java.io.*; //import of java library for input/output of data
import java.net.*;//imports useful library for networking such as Socket
import java.lang.Integer;//for parseInt() method which is used to get state of Joke/Proverbs from JokeClient 
import java.util.*;//imports libraries for Arrays 


class Worker extends Thread { 
	/*
	Utilizing an INET backbone, this worker retrieves input from the JokeClient, then uses JokeOrProverb() to decide which mode the server should be in.
	JokeOrProverb() then passes the arguments to the correct method for Jokes or Proverbs. 
	Second Server Implementation:
	Simply using flags from the JokeServer class, other classes and methods will access the flag to either add <S2> or not. 
	*/
	
	Socket sock;//creates an instance of socket to form a connection with JokeClient
	Worker (Socket s) { sock = s; } 

	public void run(){
		/* this performs the work of building a connection, input and output stream with the JokeClient. It then gets information such as
		username, jokeCount, and proverbCount to later be passed to the correct method. This method assumes that randomization will occur on the client end
		as well as state of Jokes/Proverbs will be given (however, does not assume mode)
		*/
		PrintStream out = null; 
		BufferedReader in = null; 
		int jokeCount;
		int proverbCount;
		try {
			in = new BufferedReader
				(new InputStreamReader(sock.getInputStream()));
				//this is the input stream from the JokeClient to be used for getting information such as userName
			out = new PrintStream(sock.getOutputStream());
			//this is the stream used to send the Joke/Proverbs to the Client
			try {
				String userName;//this username is simply used for in the Joke/Proverb string. This does nothing with maintaining state.
				boolean mode;//this is probably not necessary and can be removed. Required in Alternate implementation 
				userName = in.readLine();//this gets the userName from the JokeClient. This assumes the JokeClient will send it each time
				jokeCount =  Integer.parseInt(in.readLine());//this gets the JokeCount state from the JokeClient to know which joke to print
				proverbCount =  Integer.parseInt(in.readLine());//this gets the ProverbCoount state from the JokeClient

				
				jokeOrProverb(userName, out, jokeCount, proverbCount);//send pertinent information to the method which determines current mode of server
				
			} catch (IOException x){
				System.out.println("Server read error");
				x.printStackTrace(); //this provides useful information about any possible IO errors that may occur such as connecting to client
			}
			sock.close(); //this closes the connection with client. Assumes a NEW connection each time.
		} catch (IOException ioe) { System.out.println(ioe);}//Catches any IO exceptions from the outer "try" block
	}
	static void jokeOrProverb(String user, PrintStream out, int jokeCount, int proverbCount){
	//decides if user gets joke or proverb based on mode from JokeServer class
	if (JokeServer.JokeMode)//logic: if JokeMode is TRUE then give user a joke
		printJoke(user, out, jokeCount);
	else//if JokeMode is FALSE ,then give user a proverb 
		printProverb(user, out, proverbCount);
	}

	static void printJoke(String user, PrintStream out, int jokeCount){
		/* This is a somewhat simplistic implementation of storing jokes, jokeName, etc. Each Joke is stored in its own string. JokeNames are stored
		separately as I could not correctly implement a way for the print statement to simply print the variable name.
		*/
		/*this is the storage of each piece of joke content*/
		String[] jokeArray= new String[4];//this creates an array so that the jokeCount can be used as an index for accessing whichever particular joke
		String JA = " I invested a new word! Plagiarism\n";
		String JB = " Why do Java programmers wear glasses? They can't C!\n";
		String JC = " Couldn't afford a FitBit, so I got the off-brand: FatButt\n";
		String JD =	" How many computer scientists does it take to change a lightbulb? None, that's a job for the hardware group\n";
		/*this is the storage of each piece of joke name*/
		String[] jokeNameArray=new String[4];//this is similar to jokeArray but for the joke Names. 
		String JA1 = "JA";
		String JB1 = "JB";
		String JC1 = "JC";
		String JD1 = "JD";
		
		/*assigns the strings of the Jokes to an index within the array*/
		jokeArray[0]=JA; 
		jokeArray[1]=JB;
		jokeArray[2]=JC;
		jokeArray[3]=JD;
		/*assigns the strings of the JokeNames to an index within the array*/
		jokeNameArray[0]=JA1;
		jokeNameArray[1]=JB1;
		jokeNameArray[2]=JC1;
		jokeNameArray[3]=JD1;


		/*depending on the count given by user, the array index decides  the joke and sends it to the output stream */
		if (JokeServer.secondaryServerFlag==true)//if the output is being sent from the second server, then must add "<S2>"
			out.println("<S2>"+ jokeNameArray[jokeCount] + " " + user + ": " + jokeArray[jokeCount]);
		else{//if simply being sent from first server, send as normal
		out.println(jokeNameArray[jokeCount] + " " + user + ": " + jokeArray[jokeCount]);}

		
		
	}
	static void printProverb(String user, PrintStream out, int proverbCount){
		/* Similar to printJoke, this is a simple method which builds the proverb array and fills it with proverb strings. 
		*/
		String[] proverbArray= new String[4];//builds the array for storing proverb strings
		String PA = "Better a diamond with a flaw than a pebble without\n";
		String PB = "Too many cooks will spoil the broth\n";
		String PC = "Do unto others as you'd have them do unto you\n";
		String PD =	"The keyboard is mightier than the sword\n";
		String[] proverbNameArray=new String[4];//builds the array for storing proverbName strings
		String PA1 = "PA";
		String PB1 = "PB";
		String PC1 = "PC";
		String PD1 = "PD";
	
		/*assigns the proverbs to a particular index in the array*/
		proverbArray[0]=PA;
		proverbArray[1]=PB;
		proverbArray[2]=PC;
		proverbArray[3]=PD;
		/*assigns the name of the proverb to an index as this is a necessary convention*/
		proverbNameArray[0]=PA1;
		proverbNameArray[1]=PB1;
		proverbNameArray[2]=PC1;
		proverbNameArray[3]=PD1;

		/*takes the proverbCount index and uses that to print the correct joke to the output stream
		*/
		if (JokeServer.secondaryServerFlag==true)//Again, if sending from second server, need to add <S2> identifier
			out.println("<S2>" +proverbNameArray[proverbCount]+ " " + user + ": " +proverbArray[proverbCount]);
		else
			out.println(proverbNameArray[proverbCount]+ " " + user + ": " +proverbArray[proverbCount]);
		
		
	}
}
class ModeWorker extends Thread {
	/*This is the workhorse for the JokeClientAdmin connection. It forms a connections and retrieves the request from the ClientAdmin. 
	If the request is NOT "quit," then the JokeMode will be changed through the method mode().
	*/
	Socket sock;//creates a socket instance to be used for forming a connection with the JokeAdminClient
	ModeWorker (Socket s){ sock = s;}


	public void run(){
		/* 
		This method creates the connection to the JokeClientAdmin. It then retrieves the request which is simply implemented as "quit" or a mode change
		*/
		PrintStream out = null; //this will be the output stream to the JokeClientAdmin
		BufferedReader in = null;//this is the input stream for getting the request from the JokeClientAdmin 
		
		try {
			in = new BufferedReader
				(new InputStreamReader(sock.getInputStream()));//creates an input stream from socket which establishes input stream
			out = new PrintStream(sock.getOutputStream());//creates an output stream to send back information such as confirmed mode change
			try {
				String request;//string to store the request. Right now this should only store "quit"
				request = in.readLine();//gets input from the JokeClientAdmin. This should be the "request"
				//System.out.println(request);//prints request to console. This isn't necessary, but good to know it occurred  
				if (request.indexOf("quit")<0){//LOGIC: if request does NOT contain quit, then change mode. Flaw: anything with the word quit would work
					mode(request);//sends request to mode. This is prepping for additional implementions such as shutting down server
				}
				
				
			} catch (IOException x){
				System.out.println("Server read error");
				x.printStackTrace();} // this catches any IO exceptions from the inner "Try" block and gives useful information
			sock.close(); //closes the stream with the JokeClientAdmin
	} catch (IOException ioe) { System.out.println(ioe);}//catches any IO issues from outer "try" block such as setting up in/out stream 
	}

	static void mode(String request){
		/* this method is what will change the mode of the server by simply changing the variable in JokeServer from TRUE (JOKE) to FALSE (PROVERB)
		*/
		if ((request.indexOf("change") >= 0)){//this outer "IF" can be used to implement additional commands
				if (JokeServer.JokeMode==true ){//LOGIC: If JokeMODE==True (Jokes), then switch the proverb mode
					//change mode to Proverb
					JokeServer.JokeMode=false;
					System.out.println("Changed to Proverb Mode");
				}
				else {
					//change back to Joke mode if in proverb mode.
					JokeServer.JokeMode=true;
					System.out.println("Changed to Joke Mode");
				}
		 
			}
		}
	
}

public class JokeServer{
	/*
	This Server utilizes some code from INET and some provided by Professor Elliott. However, the main method accepts an argument of secondary if the second server is 
	to be used. This sets the secondaryServerFlag to TRUE which notifies areas of the code that this is pertinent to. 
	*/
	static boolean JokeMode=true;//this flag is to let Worker class know that we are in JOKEMODE, if false, then proverb mode
	static boolean secondaryServerFlag=false;//this flag is to lets areas know that we are using the SECONDARY server. If false, then primary server is used


	public static void main(String a[]) throws IOException{
		int q_len = 6;//number of queued simultaneous connections.
		int port = 4545; //primary server port
		int portSecondServer = 4546;//secondary server port
		int usedPort;//this will be whichever port is going to be used .
		Socket sock; 
		if (a.length < 1) usedPort=port;//if there is no secondary argument, then port remains the same
		else {
			if (a[0].equals("secondary")){//ensures secondary argument is requesting the secondary server
				usedPort=portSecondServer;
				secondaryServerFlag=true;//flags that second server is in use (AKA This is the second server.)
			}
			else
				usedPort=port;//just in case it was a mistake in secondary argument


		}

		AdminLooper AL= new AdminLooper();//creates a new instance of AdminLooper which is what will look for connections from ClientAdmin
		Thread t = new Thread(AL);//creates a new thread 
		t.start();//starts thread

		ServerSocket servsock = new ServerSocket(usedPort, q_len);//creates a connection to port
		//passes the port number to ServerSocket to bind specific port as well as set max number of queued connections
		if (usedPort==port) //if usedPort matches original port, then this is the primary server
			System.out.println("Chad Rivera's JokeServer 1 starting up and listening at port "+ usedPort);
		else if (usedPort==portSecondServer)//if usedPort matches port of server2, then this is the secondary server
			System.out.println("Chad Rivera's JokeServer 2 starting up and listening at port " + usedPort);
		while (true){//simply loops an acceptance of client requests
			sock = servsock.accept();//accepts incoming request found at servsock and stores in sock
			new Worker(sock).start();//starts new thread of worker with information from accept()
		}
	}
	public static boolean getJokeMode(){
		/*right now, this method is not used, however, I hope to implement an additional method for accessing the variable contents JokeMode.*/
		return JokeMode;
	}
	public static void changeMode(){
		/*right now, this method is unused. I hope to implement this method for accessing/changing variable from True/False*/
		JokeMode=!JokeMode;
	}
}



class AdminLooper implements Runnable{
//this AdminLooper was given code. However, it listens for connections from the JokeClientAdmin.
	public static boolean adminControlSwitch= true;//this switch is a boolean that is used to control the "while" loop of looking for connections

	public void run(){
		int q_len=6;//number of simultaneous connections to queue
		int port = 5050;//what port to listen to 
		int portSecondServer=5051;//this gives us the port to use for the secondary server connection to ClientAdmin
		Socket sock;//creates socket so that it can be passed to the ModeWorker
		if (JokeServer.secondaryServerFlag==true) port=portSecondServer;//this sets the port to 5051 if the secondary server is in use here.
		try{
			/*this "try" block creates a server socket and then tries to accept connections at the specified port number*/
			ServerSocket servsock= new ServerSocket(port, q_len);
			while (adminControlSwitch){
				sock = servsock.accept();
				new ModeWorker(sock).start();
			}
		} catch (IOException ioe){ System.out.println(ioe);}
	}
}
















