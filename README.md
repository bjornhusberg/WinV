Compile WinV
============

 Change directory to winv/src/ and compile the client:
 
<code>javac -d ../client/classes/ Client.java</code>
 
 Compile the server:
 
<code>javac -d ../bin/ WinV.java</code>

 Compile the rmi stubs and skeletons: 

<code>rmic -d ../bin/ rmiserver.RMIServer</code>
<code>rmic -d ../bin/ rmiserver.RMILogin</code>
  
 Copy the files <code>RMIServer_Stub.class</code> and <code>RMILogin_Stub.class</code>
 from <code>winv/bin/rmiserver/</code> to <code>winv/client/classes/</code>

 Then change directory to <code>winv/client/classes/</code>
 and put all client files in a jar archive: 

<code>jar cf ../httproot/Client.jar *</code>

Add a new user
==============
 
 Initially WinV contains no user data. Therefor a user has
 to be added before the system can be used.

 Change directory to winv/bin/ and start the server
 using the following command line options:

<code>java WinV -adduser</code>

 Enter user name and password when requested. The new
 user is added or updated in the system.

 There is currently no neat way to remove a user
 from the system. Either the file winv/bin/winv.ini
 can be modified manually or deleted completely. 


Start the server
================

 Change directory to <code>winv/bin/</code>
 and start the server using:

<code>java WinV [-http &lt;portnr&gt;] [-rmi &lt;portnr&gt; | -normi] [-adduser]</code>

 Command line options:

<code>-http &lt;portnr&gt;</code>

 Specifies which port to run the http server on. (default = 8080)

<code>-rmi &lt;portnr&gt;</code>   
 
 Specifies which port to run the rmiregistry on. (default = 1099)
  
<code>-normi</code>

 Tells winv to use an external rmiregistry that is
 already running on the system.


Connect to the server
=====================

 The http address of the server is displayed when the 
 server is started. This address is simply entered in
 the address field of the browser. If the browser is
 unable to run winv the appletviewer can be used
 instead:

<code>appletviewer &lt;http address&gt;</code>

 The applet consumes a lot of memory. This can cause the
 appletviewer to crash. If that happends simply increase
 the maximum memory heap size by passing the command line 
 option <code>-J-Xmx&lt;number of megabytes&gt;m</code> to the appletviewer.
 For example:
 
<code>appletviewer -J-Xmx100m &lt;http address&gt;</code>


Update the javadoc documentation
================================

 Change directory to <code>winv/src/</code>.
 Run javadoc with the following command line options:	
  
<code>javadoc -private -author -version -d ../docs/javadoc *</code>

