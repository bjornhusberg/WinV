
  1 How to compile WinV
 =======================

 Change directory to winv/src/ and compile the client:
 
  javac -d ../client/classes/ Client.java
 
 Compile the server:
 
  javac -d ../bin/ WinV.java   

 Compile the rmi stubs and skeletons: 

  rmic -d ../bin/ rmiserver.RMIServer
  rmic -d ../bin/ rmiserver.RMILogin
  
 Copy the files RMIServer_Stub.class and RMILogin_Stub.class
 from winv/bin/rmiserver/ to winv/client/classes/

 Then change directory to winv/client/classes/
 and put all client files in a jar archive: 

  jar cf ../httproot/Client.jar *


  2 How to add a new user
 =========================
 
 Initially WinV contains no user data. Therefor a user has
 to be added before the system can be used.

 Change directory to winv/bin/ and start the server
 using the following command line options:

  java WinV -adduser

 Enter user name and password when requested. The new
 user is added or updated in the system.

 There is currently no neat way to remove a user
 from the system. Either the file winv/bin/winv.ini
 can be modified manually or deleted completely. 


  3 How to start the server
 ===========================

 The easiest way is to execute the file winv/winv.bat
 (The batch file works on Windows and Unix systems)

 Another way is to change directory to winv/bin/
 and start the server using:

  java WinV [-http <portnr>] [-rmi <portnr> | -normi] [-adduser]

 Command line options:

  -http <portnr>   

 Specifies which port to run the http server on. (default = 8080)

  -rmi <portnr>   
 
 Specifies which port to run the rmiregistry on. (default = 1099)
  
  -normi

 Tells winv to use an external rmiregistry that is
 already running on the system.


  4 How to connect to the server
 ================================

 The http address of the server is displayed when the 
 server is started. This address is simply entered in
 the address field of the browser. If the browser is
 unable to run winv the appletviewer can be used
 instead:

  appletviewer <http address>

 The applet consumes a lot of memory. This can cause the
 appletviewer to crash. If that happends simply increase
 the maximum memory heap size by passing the command line 
 option -J-Xmx<number of megabytes>m to the appletviewer.
 For example:
 
  appletviewer -J-Xmx100m <http address>


  5 How to update the javadoc documentation
 ===========================================

 Change directory to winv/src/
 Run javadoc with the following command line options:	
  
  javadoc -private -author -version -d ../docs/javadoc *

