
// Standard imports:
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

// Project imports:
import httpserver.*;
import imageconversion.*;
import rmiserver.*;
import exceptions.*;
import security.*;
import util.*;

/**
 * This is the main class of the server. It only
 * contains one (public) main method that handles
 * all the initialization of the server.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see rmiserver.RMIServer
 * @see httpserver.HTTPServer
 */
public class WinV {
    
    /**
     * The main method does the following initiations: 
     *  1 Parses the command line.
     *  2 Resolves the rmi object address.
     *  3 Sets up the registry if not disabled.
     *  4 Binds a server object to the registry.
     *  5 Inserts the rmiadress in the index.html file.
     *  6 Starts the http server.
     *  7 Resolves the server address and displays it.
     */
    public static void main(String [] args){
	
	int httpport = 8080;
	int registryport = 1099;
	String rmiaddress = null;
	String httpaddress = null;
	boolean rmiregistry = true;
	Registry reg;
	HTTPServer httpserver;
	
	System.out.println("WinV v1.0");

		
	// Parse the command line:	
	int i = 0;
	try {
	    for (; i < args.length; i++) {
		if (args[i].equals("-http") && i < args.length - 1)
		    httpport = Integer.parseInt(args[++i]);
		else if (args[i].equals("-rmi") && i < args.length - 1)
		    registryport = Integer.parseInt(args[++i]);
		else if (args[i].equals("-normi"))
		    rmiregistry = false;	
		else if (args[i].equals("-adduser")) {
		    addUser();
		}
		else throw new Exception();
	    }
	}	
        catch (Exception e) {
	    System.out.println("Command line error: " + args[i]);
	    System.exit(-1);
	}
	

	// Resolve our rmi and http address:

	try {

	String ip = InetAddress.getLocalHost().getHostAddress();

	rmiaddress = "//" + ip + ":" + String.valueOf(registryport) + "/Server";

	httpaddress = "http://" + ip;
    	if (httpport != 80) httpaddress = httpaddress + ":" + httpport;
	httpaddress = httpaddress + "/";

	} catch(Exception e) {
	    System.out.println("Please check your Internet connection.");	
	    System.exit(-1);
	}


	// Set up the rmi registry if not disabled:
			
	if ( rmiregistry ) {		
	    try {
		reg = LocateRegistry.createRegistry(registryport);        
	    } catch (RemoteException e) {
		System.out.println("Couldn't start the rmi registry.");
		System.exit(-1);
	    } 
	}


	// Bind a server object to the rmi registry:

        try {  
            RMILogin loginserver = new RMILogin();
            Naming.rebind(rmiaddress, loginserver);
        } catch(Exception e) {
	    System.out.println("Couldn't connect to the rmi registry.");
	    System.exit(-1);
        }

	
	// Parse the index.html file and insert our rmi address:
	
	try {
	    FileInputStream infile = new FileInputStream("../client/httptemplates/index.html");
	    FileOutputStream outfile = new FileOutputStream("../client/httproot/index.html");
	    
	    String keys[] = {"$rmiaddress", "$httpaddress"};
	    String values[] = {rmiaddress, httpaddress};
	    int counters[] = {0, 0};
	    int counter = 0;
	    
	    int length = infile.available();
	    byte buffer[] = new byte[length];
	    infile.read(buffer, 0, length);
	    
	    String str = new String(buffer);
	    
	    for (i = 0; i < counters.length; i++)
		counters[i] = str.indexOf(keys[i], counter);
	    
	    int min = 0;
	    for (i = 0; i < counters.length; i++)
		if ((counters[i] < counters[min] && counters[i] != -1) || 
		    counters[min] == -1) min = i;
	    
	    while (counters[min] != -1) {
		outfile.write(buffer, counter, counters[min] - counter);
		counter = counters[min] + keys[min].length();
		outfile.write(values[min].getBytes(), 0, values[min].length());
		
		counters[min] = str.indexOf(keys[min], counter);
		for (i = 0; i < counters.length; i++)
		    if ((counters[i] < counters[min] && counters[i] != -1) || 
			counters[min] == -1) min = i;
	    }
	    if (length - counter > 0) 
		outfile.write(buffer, counter, length - counter);
	    
	} catch(Exception e) {
	    System.out.println("Couldn't parse the template index file.");
	    System.exit(-1);
	}
	

	// Set up the http server:
	
	try {
	    httpserver = new HTTPServer(httpport, "../client/httproot");    
	    httpserver.start();
	} catch(Exception e) {
	    System.out.println("Couldn't start the http server.");
	    System.exit(-1);
	}
	

	// Display our http address:
	
	try {
	    System.out.println("Connect to the server using the following adress:");
	    System.out.println(httpaddress);
	} catch(Exception e) {}	
    }

    
    /**
     * Private method that asks for a username 
     * and a password and adds the user to the 
     * properties file.
     */
    private static void addUser() {

	System.out.print("Enter username: ");
	String username = "";
        String password = "";
	int c = 0;
	try {
	    c = System.in.read();
	    while (c != '\n' && c != '\r') {
		username = username + (char) c;
		c = System.in.read();
	    }
	    
            while (System.in.available() != 0) System.in.read();

	    System.out.print("Enter password: ");
            c = 0;
	    c = System.in.read();
	    while (c != '\n' && c != '\r') {
		password = password + (char) c;
		c = System.in.read();
	    }
	} catch(Exception e) {}
	
	try {
	    Settings.getInstance().addUser(username, password);
	} catch(Exception e) {}
	
	System.out.println("User " + username + " has been added.");
    }    
}	



