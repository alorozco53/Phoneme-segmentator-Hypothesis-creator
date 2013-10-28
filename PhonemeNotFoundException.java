/**
 * This class creates an exception that will help to handle situations
 * where the program cannot match a string with any phoneme included in the 'phoneme.phone' file.
 * (i.e. When constructing on object of the Phoneme class)
 * @author Albert Manuel Orozco Camacho
 * @version 09.2.25.13
 */
public class PhonemeNotFoundException extends Exception {

    /** Default constructor */    
    public PhonemeNotFoundException() {
	super();
    }

    /**
     * Constructor with one parameter
     * @param message -- the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     */
    public PhonemeNotFoundException(String message) {
	super(message);
    }

    /**
     * Constructor with two parameters
     * @param message -- the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     * @param cause -- the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public PhonemeNotFoundException(String message, Throwable cause) { 
	super(message, cause);
    }

    /**
     * Constructor with one parameter
     * @param cause -- the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public PhonemeNotFoundException(Throwable cause) { 
	super(cause);
    }
}