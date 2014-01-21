package exceptions;

/**
 * This exception is used when the size or type of
 * two images doesn't match.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 * @see java.lang.Exception
 */
public class IncompatibleImageException extends Exception {

    /**
     * The empty constructor
     */
    public IncompatibleImageException () {
	super();
    };
   
    /**
     * The commented constructor
     *
     * @param s the comment string
     */
    public IncompatibleImageException (String s) {
	super(s);
    }
}
