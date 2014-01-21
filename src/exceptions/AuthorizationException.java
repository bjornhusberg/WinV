package exceptions;

/**
 * This exception is used when an Authorization fails.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Exception
 */
public class AuthorizationException extends Exception {

    /**
     * The empty constructor
     */
    public AuthorizationException () {
	super();
    };

    /**
     * The commented constructor
     *
     * @param s the comment
     */
    public AuthorizationException (String s) {
	super(s);
    }
}
