/*--------------------------------------------------------

1. Name / Date: Chadwick Rivera-Crum / 4/19/20

2. Java version used, if not the official version for the class: Java 1.8



3. Precise command-line compilation examples / instructions:


> javac JokeServer.java


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

5. Notes:
I implemented two distinct startup instructions whether the client is using a secondary server OR not. (s) command can be used for both of these, but one will 
print that there is no secondary server as the assignment conventions outlines.


----------------------------------------------------------*/

import java.io.*;//import of java library for read/write 
import java.net.*;//gives us networking library
import java.util.*;//imports librarys for Arrays, collections, shuffling. 
/* This JokeClient program will retrieve username from the user. Randomization of the Jokes/Proverbs is handled here as well. The Client then retrieves
a Joke OR Proverb (client is unaware of mode.) It retrieves by sending userName, jokeIndex and proverbIndex. The indexes are stored here and simply 
incremented
Two Server Implementation: Flags are used to state whether a second server is in use, if so, two other flags signal which server is currently in use that the alternate
can be switched to.
*/
public class JokeClient{
	public static boolean secondaryServerFlag=false;//flags if the client is accessing two servers
	public static boolean usingSecondServer=false;//sets a flag if the client is currently using second server
	public static boolean usingFirstServer=false;//sets a flag that client is currently usuing first server
	public static void main (String args[]){
		String serverName=null;//store the name of server1 
		String serverName2=null;//stores the name of the second server
		if (args.length < 1) {
			/*
			if there are 0 arguments given, this allows the Client to access the normal port for the server and sets the name to localhost. 
			*/
			serverName= "localhost";
			System.out.println("Connecting with " + serverName + " at port: 4545.");
			
		}
		
		else if (args.length >1) {

			serverName=args[0];//this assumes the first argument is going to be for server1
			serverName2=args[1];//takes second argument and sets the name of server2
			secondaryServerFlag=true;//this signals to other functions that there is more than one server being connected to 
			usingFirstServer=true;//sets first server as being used to allow for later logic to work such as switching from server1 to server2
			System.out.println("Server one: " + serverName + "at port: 4545. ");
			System.out.println("Server two: " + serverName2 + "at port: 4546. ");
		}
		else {//this "else" block is if an IP address is given but not a second argument (aka a second server Name)
		serverName = args[0];
		System.out.println("Connecting with " + serverName + "at port: 4545. ");
		}
		

		System.out.println("Chad's JokeClient, 1.8.\n");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));//creates input stream to get information from USER
		try{
			String userEmail;//stores userEmail, this is only updated once as to not annoy user with multiple userName inputs. Email is used due to
			//being unique enough for duplicates. Additionally, duplicates do not to be handled as state is maintained on client
			String response;//stores user response, updated continuously in while loop
			System.out.print
				("Please enter your email address and press (enter) ");//prompts user for email address
			System.out.flush();//flushes any elements from the stream. 
			userEmail=in.readLine();//gets userEmail from input
			randomizeJokeOrder1();//first round of randomizations for Joke order
			randomizeProverbOrder1();//first randomization for Proverb order
			if (secondaryServerFlag==true){
				/*
				Using an "if" statement isn't extremely critical, however, might as well save the time so that this can be scaled up and
				time isn't wasted randomizing 50,000 clients that aren't using a second server
				*/
			randomizeJokeOrder2();//first round of randomization for second server's Joke Order
			randomizeProverbOrder2();//first randomization for second server's Proverb order
		}

			do {
				if (secondaryServerFlag==true)//ensures instructions are accurate for "mode" of client
					System.out.print("Press (enter) for Joke/Proverb,(s) to switch servers, otherwise (quit) to end: \n");
				else//if client isn't using second server, no need for (s) command
					System.out.print("Press (enter) for Joke/Proverb, otherwise (quit) to end: \n");//prompts user for input pertaining to what theyt want
				
				System.out.flush();
				response = in.readLine ();//saves response 
				if (secondaryServerFlag==false && response.equals("s")) System.out.println("No secondary server being used.\n");
				else if (response.indexOf("quit") < 0){//LOGIC: if response does not contain QUIT, then provide a joke/proverb
					//to indicate no second server
					if(secondaryServerFlag==true){//LOGIC: if there is the presence of a second server, then there are additional checks needed
						if(response.equals("s")){//LOGIC: if user input is equal to switch command ("s") then enter'

							if (usingFirstServer==true){//LOGIC: if first server is in use, then switch to second
								usingFirstServer=false;
								usingSecondServer=true;
								System.out.print("Now communicating with:"+ serverName2	+ " at port: 4546.\n");

								
							}
							else if(usingFirstServer==false){//LOGIC: if not using first server, then switch to first server and remove flag from second
								usingFirstServer=true;
								usingSecondServer=false;
								System.out.print("Now communicating with:"+ serverName	+ " at port: 4545.\n");
							}
						}
						else{//if user does not signal for a switch or quit, then they want a Joke/Proverb
							if(usingFirstServer==true){//if using server1, then get Joke/Proverb using that server's name
							getJoke(serverName, userEmail);
						}
							else if (usingSecondServer==true){//if using server2, then retrieve content with that server's name
								getJoke(serverName2, userEmail);
							}
							else getJoke(serverName, userEmail);//not likely needed, but added just incase logic is flawed. 
						}//this sends just the servername to the method, this will need to be updated 
					//to accept further commands such as "turn off"
					}
					else {//this keeps original functionality of client IF there is no additional arguments for a second server
					usingFirstServer=true;
					getJoke(serverName, userEmail);
					
					}//sends userEmail and the serverName to the getJoke() method

				}

			} while (response.indexOf("quit") < 0);//this structure was simply kept from the INET server. However, it will exit if user gives (quit) response 
			System.out.println("Canceled by user request.");
		} catch(IOException x) { x.printStackTrace();}//tracks any IO exceptions from try block.
	}
	/*I made the choice to separate the methods for randomizations pertaining to Joke/Proverb because I only wanted to randomize whichever
	set needed to be randomized. This maintains the state if the Server Mode were to be switched without going through either of the two sets. 
	However, the mechanics are simple. The arrays for Joke/Proverb simply stores an index of which joke/proverb to get back. The count provides an 
	index of the array so that the joke/proverbs are NOT repeated. The arrays are randomized so it may be {0, 1, 2, 3} OR it could be {2, 1, 0, 3}. 
	The count variable will always return a sequence of 0, 1, 2, 3 so that the cycle is completed and each joke/proverb is provided. 
	Second Server Implementation:
	I simply added a second set of arrays for the second server. This is the only way that I could think of randomizing order without affecting the other array. 
	In regards to the additional two methods, I wanted to prevent the flags from possible affecting which array gets shuffled, so I simply made two methods 
	specifically for the second server. 
	*/
	static int jokeCount1=0;//keeps count of jokes and will be index for array when sent to Server
	static int proverbCount1=0;//keeps count for proverbs and will be index of proverb array when sent
	static Integer[] jokeArray1= new Integer[]{0, 1, 2, 3};//simply an array with numbers that will be an index for the JokeServer of which joke to tell
	static Integer[] proverbArray1= new Integer[]{0, 1, 2, 3};
	static int jokeCount2=0;//keeps count of jokes and will be index for array when sent to Server
	static int proverbCount2=0;//keeps count for proverbs and will be index of proverb array when sent
	static Integer[] jokeArray2= new Integer[]{0, 1, 2, 3};//simply an array with numbers that will be an index for the JokeServer of which joke to tell
	static Integer[] proverbArray2= new Integer[]{0, 1, 2, 3};

	static void randomizeJokeOrder1(){
		//this is the workhorse of randomizations for Jokes. It takes the JokeArray and uses the Collections library to shuffle. However, this 
		//shuffling cannot be done on an array, so the asList() method converts to an array List. 

		Collections.shuffle(Arrays.asList(jokeArray1));
		//System.out.println(jokeArray[0]);//used as verification that the first index was changed
		
	}

	static void randomizeProverbOrder1(){
		//this is identical in functionality to the randomizeJokeOrder() but for proverbs.
		Collections.shuffle(Arrays.asList(proverbArray1));
		
	}
	static void randomizeJokeOrder2(){
		//identical functionality to the other randomization methods, but for Joke order of server2
		Collections.shuffle(Arrays.asList(jokeArray2));
	}

	static void randomizeProverbOrder2(){
		//same functionality as other randomization methods, but server2 proverb order
		Collections.shuffle(Arrays.asList(proverbArray2));
	}

	static void getJoke(String serverName, String userEmail){
		/* Everything being sent to the server is handled here. This method will create the connection with the server. Then it will get input/output streams. 
		The output stream will be used to send the userEmail and current indexes for Jokes and Proverbs. Both indexes are sent as the Client does not
		know the Mode state for the Server
		Second Server Implementation:
		Simply using the flags to know whether or not the socket should be set to 4546 OR 4545. 
		*/
		Socket sock;//this creates a socket for use when connecting to server
		BufferedReader fromServer;//catching input from server
		PrintStream toServer;//used to send output to server
		String textFromServer;//used to store whatever text comes from server (aka, joke or proverb)
		try{

			if (secondaryServerFlag==true){//if using a second server, then need to see which port to use
				if (usingSecondServer==true){//if using the second server, then connect to 4546
					sock = new Socket(serverName, 4546);//this simply creates the socket at 4546. NOTE: serverName is used both times as whichever is passed is used.
				}
				else
					sock = new Socket(serverName, 4545);//sets sockets to 4545 if using first server 
			}
			else//this retains original functionality of a single server Client
				sock = new Socket(serverName, 4545);//establishes a connection with the appropriate servername at 4545
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));//creates a bufferedReader instance for getting server output to be printed
			
			toServer= new PrintStream(sock.getOutputStream());
			//this is for the data being sent to the Server
			/* I had issues with sending the data was one large chunk so I instead sent each 
			piece of data separately. The flush() statements may be extraneous but they ensure no elements are in stream
			*/
			toServer.println(userEmail); toServer.flush(); //send userEmail then flush stream

			if (usingSecondServer==true){
				//if using the second server, then the order arrays for server2 have to be used and sent. 
				toServer.println(jokeArray2[jokeCount2]); toServer.flush();//send the index of the array.
				toServer.println(proverbArray2[proverbCount2]); toServer.flush();
			}
			else{
				//if using the first array, then send order arrays for first server
			toServer.println(jokeArray1[jokeCount1]); toServer.flush();//send the index of the array.
			toServer.println(proverbArray1[proverbCount1]); toServer.flush(); //send the index of the proverbArray
		}

			textFromServer = fromServer.readLine();//this reads the text from the server.
			if (textFromServer != null) System.out.println(textFromServer+ "\n");//ensures there is actually text from the server, then prints it
			if ((textFromServer.indexOf("J")<0) && usingFirstServer==true)//LOGIC: IF the textFromServer is NOT A Joke AND client is using the first server, then count must be updated.
			{
				if (proverbCount1<3) proverbCount1++;//if proverbCount will go out of bounds on the array, then increment
				else {//if at the end of possible proverbs without repeating, then reset the count to 0
					proverbCount1=0;
					System.out.println("PROVERB CYCLE COMPLETED\n");//just announces that the cycle is completed
					randomizeProverbOrder1();//randomizes the order of the array
				}
			}
			else if (usingFirstServer==true){//if textFromServer IS a Joke and using first server, then update server1 JokeCount
				if(jokeCount1<3) jokeCount1++;//ensures the bounds of JokeArray is not overextended
				else {
					jokeCount1=0;//reset jokeCount if going out of bounds for array
					System.out.println("JOKE CYCLE COMPLETED\n");//announces the cycle is complete
					randomizeJokeOrder1();//randomizes order of array for server1 Jokes
				}
			}	
			else if (usingSecondServer==true){//if currently using server2, then those counts need to be updated here
				if((textFromServer.indexOf("J")<0)){//checks if textFromServer is NOT A Joke, so that proverb counts can be update
				if (proverbCount2<3) proverbCount2++;//if proverbCount will go out of bounds on the array, then increment
				else {//if at the end of possible proverbs without repeating, then reset the count to 0
					proverbCount2=0;
					System.out.println("PROVERB CYCLE COMPLETED\n");//just announces that the cycle is completed
					randomizeProverbOrder2();//randomizes the order of the array for server2 proverb
				}

			}
				else {//Logic: So, if server1 isn't being used AND the text IS a Joke, then update the joke count for server2.
					if(jokeCount2<3) jokeCount2++;//ensures the bounds of JokeArray is not overextended
				else {
					jokeCount2=0;//reset jokeCount if going out of bounds for array
					System.out.println("JOKE CYCLE COMPLETED\n");//announces the cycle is complete
					randomizeJokeOrder2();//randomizes order of array

				}
			}
		}


			sock.close();//closes connection with whichever server is being used. 
		} catch(IOException x){//simply our exception catch if there's an IO issue. 
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
}