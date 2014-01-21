package exceptions;

/**
 * This exception is used when the server is
 * crashing.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Exception
 */
public class FatalServerException extends Exception {

    /**
     * The empty constructor
     */
    public FatalServerException () {
	super();
    };


    /**
     * The commented constructor
     *
     * @param s the comment
     */
    public FatalServerException (String s) {
	super(s);
    }
}
