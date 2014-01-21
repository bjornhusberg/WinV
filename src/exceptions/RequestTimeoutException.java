package exceptions;

/**
 * This exception is used when there is a request timeout
 * occuring inside the http-server.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Exception
 */
public class RequestTimeoutException extends Exception {

    /**
     * The empty constructor
     */
    public RequestTimeoutException () {
	super();
    };
   
    /**
     * The commented constructor
     *
     * @param s the comment string
     */
    public RequestTimeoutException (String s) {
	super(s);
    }
}
