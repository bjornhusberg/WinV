package httpserver;

// Standard imports:
import java.net.*;
import java.io.*;
import java.util.*;

// Project imports:
import exceptions.*;


/**
 * This is the communication-threads of the HTTPserver.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Thread
 */
class HTTPThread extends Thread {


/**
 * Constant for determing the buffer size.
 */
    private static final int buffersize = 4096; // 4kb


/**
 * Constant for determining the timeout in milliseconds.
 */
    private static final long timeout = 60000; // 1 min timeout


/**
 * Root directory of the filesystem.
 */
    private String root;


/** 
 * The buffer. Created only once.
 */
    private byte[] buffer;

     
/**
 * The communication socket.
 */
    private Socket socket;                   


/**
 * The InputStream associated to the communication socket.
 */
    private InputStream in;                  


/** 
 * The OutputStream associated to the communication socket.
 */
    private OutputStream out;                

    
/**
 * Initializes the associated streams
 * 
 * @throws IOException
 */
   public HTTPThread(Socket s, String rootdir) throws IOException {
	socket = s;
	in = socket.getInputStream();  
	out = socket.getOutputStream();
	buffer = new byte[buffersize];
	root = rootdir;
    }
    
    
/**
 * This method is called by the start()-method in the 
 * Thread-class. It gets a request, parses it and sends 
 * the result. Relative paths are ignored.
 *
 * @see java.lang.Thread
 */
    public void run() {

	try {

  	    String hreq = getHTTPRequest(in);

	    StringTokenizer st = new StringTokenizer(hreq, " \t");
	    if (!st.nextToken().equals("GET")) throw new Exception();
	
	    String filename = st.nextToken();
	
	    if (filename.equals("/")) filename = "/index.html";
	    if (filename.indexOf("..") != -1) filename = "/index.html";

	    String mimetype = "text/html";
	    if (filename.endsWith(".jar")) mimetype = "binary/octet-stream";
	    else if (filename.endsWith(".gif")) mimetype = "image/gif";
	    else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) mimetype = "image/jpeg";

	    sendFile(filename, mimetype);

 	}catch (Exception e){}

	try {
	socket.close();
	} catch (Exception e){}

   }

    
/**
 * Sends a file to an output stream.
 *
 * @param filename  the name of the file to be sent.
 * @param out  the OutputStream to write to.
 * @throws IOException
 */
    private void sendFile(String filename, String mimetype) throws IOException {

	FileInputStream file = new FileInputStream(root + filename);
	int length = file.available();

	String header = "HTTP/1.0 200 OK\r\n";
	header = header + "Content-type: " + mimetype + "\r\n";
	header = header + "Content-Length: " + length + "\r\n\r\n";

        for (; length >= buffersize; length -= buffersize) {
		file.read(buffer);
        	out.write(buffer); 
	}
	file.read(buffer, 0, length);
	out.write(buffer, 0, length);

	file.close();
	out.flush();

    }
      
    
/**
 * Gets a HTTP request from an InputStream.
 * 
 * @param in  the InputStream to read from.
 * @throws IOException
 * @return  the HTTP request as a string.
 */
    private String getHTTPRequest(InputStream in) throws IOException, RequestTimeoutException {

	String request = new String();    

	long t = new Date().getTime();

	while (new Date().getTime() - t < timeout && !request.endsWith("\r\n\r\n"))
		while (in.available() > 0)
			 request = request + (char) in.read();

	if (new Date().getTime() - t > timeout) throw new RequestTimeoutException();

	return request;               
    }		    
}     


