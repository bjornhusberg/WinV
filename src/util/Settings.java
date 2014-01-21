package util;

// Standard imports:
import java.io.*;
import java.util.*;

/**
 * This class is used to handle the settings
 * in the system. To prevent ambiguosity
 * the object is singleton. This means that
 * there is no way to create more than one
 * object of this type. To get a handle of the 
 * object the getInstance method is used.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class Settings {
    
    /**
     * Private variable to hold the actual object
     */
    private static Settings s = null;
    
    /**
     * Private variable to hold the property object
     */  
    private Properties p;
    
    /**
     * Private static variable to hold the filename
     * in which the settings are saved
     */
    private static String filename = "winv.ini";
    
    /**
     * The constructor is private to prevent
     * more than one object of this class.
     * If the settings file can't be read it 
     * will be overwritten.
     */
    private Settings() {
	p = new Properties();
	try {
	    FileInputStream in = new FileInputStream(filename);
	    p.load(in);
	    in.close();
	} catch(IOException e) {};
    }				
    
    
    /**
     * Private help method for getting properties
     */
    private String getProperty(String user, String key) throws IOException{
	
	return p.getProperty(user + "." + key);
    }
    
    
    /**
     * Private help method for setting properties. 
     * Always stores the settings on disc.
     */
    private void setProperty(String user, String key, String value) {
	
	p.setProperty(user + "." + key, value);
	
	try {
	    FileOutputStream out = new FileOutputStream(filename);
	    p.store(out, filename + " - Do not modify manually");
	    out.close();
	} catch (IOException e) {};
    }
    
    
    /**
     * This static method is used instead of the
     * constructor. It creates a new object if none
     * exists or returns the existing one.
     *
     * @return the Settings object
     */
    public static Settings getInstance() {
	if (s == null) s = new Settings();
	return s;
    }
    
    
    /** 
     * This method stores settings for a new user.
     *
     * @param user the username 
     * @param password the password
     */
    public void addUser(String user, String password) throws IOException {
	setPassword(user, password);
	setDefaults(user);
    }
    
    /**
     * This method is used to initiate the default settings
     * for a user.
     * 
     * @param user the username
     */
    public void setDefaults(String user) throws IOException {
	setJPEGQuality(user, 0.6f);
	setEncryptionStatus(user, false);
	setDeltaStatus(user, false);	 
    }
    
    /**
     * This method is used to set the password of a user
     *
     * @param user the username
     * @param password the password
     */
    public void setPassword(String user, String password)  throws IOException{	
	setProperty(user, "password", password);
    }
    
    
    /**
     * This method is used to get the password of a user
     *
     * @param user the username
     * @return the password
     */
    public String getPassword(String user) throws IOException {
	return getProperty(user, "password");
    }
    
    
    /**
     * This method is used to set the JPEGQuality for a user
     *
     * @param user the username
     * @param value the JPEGQuality value
     */
    public void setJPEGQuality(String user, float value) throws IOException {
	setProperty(user, "JPEGQuality", String.valueOf(value));
    }
    
    
    /**
     * This method is used to get the JPEGQuality for a user
     *
     * @param user the username
     * @return value the JPEGQuality value
     */
    public float getJPEGQuality(String user) throws IOException {
	return Float.valueOf(getProperty(user, "JPEGQuality")).floatValue();
    }
    
    
    /**
     * This method is used to set encryption status for a user
     *
     * @param user the username
     * @param value true if encryption is used
     */
    public void setEncryptionStatus(String user, boolean value) throws IOException {
	setProperty(user, "encryption", new Boolean(value).toString());
    }
    
    
    /**
     * This method is used to get the encryption status for a user
     *
     * @param user the username
     * @return value true if encryption is used
     */
    public boolean getEncryptionStatus(String user) throws IOException {
	return Boolean.valueOf(getProperty(user, "encryption")).booleanValue();
    }
    
    
    /**
     * This method is used to set delta precompression status for a user
     *
     * @param user the username
     * @param value true if delta precompression is used
     */
    public void setDeltaStatus(String user, boolean value) throws IOException {
	setProperty(user, "delta", new Boolean(value).toString());
    }
    
    
    /**
     * This method is used to get the delta precompression status for a user
     *
     * @param user the username
     * @return value true if delta precompression is used
     */
    public boolean getDeltaStatus(String user) throws IOException {
	return Boolean.valueOf(getProperty(user, "delta")).booleanValue();
    }
}
