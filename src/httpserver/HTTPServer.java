package httpserver;

// Standard imports:
import java.net.*;
import java.io.*;

// Project imports:
import exceptions.*;

/**
 * This is the main thread of the appletserver.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Thread
 */
public class HTTPServer extends Thread {
    
    /**
     * The listening socket.
     */
    private ServerSocket server;

    
    /**
     * The root directory of the filesystem.
     */
    private String root;


    /**
     * The error variable used to determine internal
     * errors in the server thread.
     */
    private Exception exception = null;


    /**
     * Initializes the server to listen to the specified port.
     *
     * @param port  the port to use.
     * @throws IOException  if initialization fails.
     */
    public HTTPServer(int httpport, String rootdir) throws IOException {
	server = new ServerSocket(httpport);
	root = rootdir;
    }
   
 
    /**
     * Initializes the server to run on default port 80.
     *
     * @throws IOException  if initialization fails.
     */
    public HTTPServer() throws IOException {
	server = new ServerSocket(80);
    }
  
  
    /**
     * Checks if any exception has been raised in the thread.
     *
     * @return last exception or null if not available
     */
   public Exception getLastException() {
	return exception;
    }


    /**
     * This method is called by the start()-method in the
     * Thread-class. It loops infinitely, giving HTTPThreads
     * to any incoming connection.
     */    
    public void run() {
	while(true) {
	    try {
		HTTPThread cthread = new HTTPThread(server.accept(), root);
		cthread.start();
	    } catch (Exception e) {
		exception = e;
	    }
        }
    } 
}

