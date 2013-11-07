/* Java packages */
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Vector;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.lang.NumberFormatException;
import java.io.*;
/* OAA packages */
import com.sri.oaa2.com.*;
import com.sri.oaa2.lib.*;
import com.sri.oaa2.icl.*;
import com.sri.oaa2.agentlib.*;
/**
 * Main class for this program, it receives streams of phonemes, emits hypotheses, and saves the positively reinforced ones in a string array.
 * @author alorozco53
 * @version 09.0.23.13
 * @see PhonemeNotFoundException, Phoneme, Hypotheses
 */
public class PhonemeLearnAgent extends AgentImpl {
    
    public static int reps;
    public static String phdict;
    public static Hypotheses h = null;
    public static String[] arguments;
    public static LinkedList<String> db = null;
    public static LinkedList<Vector> prevDB = null;
    public static boolean interactive = false;
    public static final int EOFERROR = -123456;
    public static final String AGENT_NAME = "PhonemeLearnAgent";

    /** Default constructor */
    public PhonemeLearnAgent() {
        super();
    }
    
    /**
     * Reads a stream of phonemes and returns an array of them
     * @param stream -- stream of phonemes
     * @param phdict -- phoneme dictionary
     * @return Phoneme[] -- array of all found phonemes
     */
    public static Phoneme[] readPhonemes(String stream) {
	Phoneme[] array = null;
	int counter = 0;
	// the following loop avoids the use of a list for this method
	for(int i = 0; i < stream.length(); i++) {
	    if(stream.charAt(i)==' ' && stream.charAt(i-1)!=' ')
		counter++;
	}
	array = new Phoneme[counter+2];
	for(int i = 0,j = 0,k = 0; k < array.length && j < stream.length(); j++) {
	    try {
		if(stream.charAt(j)==' ' && stream.charAt(j-1)!=' ') {
		    array[k] = new Phoneme(stream.substring(i,j),phdict);
		    i = j+1;
		    k++;
		}
		if(j+1 == stream.length())
		    array[k] = new Phoneme(stream.substring(i,stream.length()),phdict);
	    } catch (FileNotFoundException e) {
		System.err.println("Error: " + e.getMessage());
		System.exit(1);
	    } catch (SecurityException e) {
		System.err.println("Error: " + e.getMessage());
		System.exit(1);
	    } catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
		System.exit(1);
	    } catch (PhonemeNotFoundException e) {
		System.err.println("Error: " + e.getMessage());
		i = j+1 < stream.length() ? j+1 : i;
		k++;
	    } catch (IndexOutOfBoundsException e) {
		System.err.println("Error: " + e.getMessage());
		System.exit(1);
	    } catch (Exception e) {
		System.err.println("Error: " + e.getMessage());
		System.exit(1);
	    }
	}
	return array;
    }

    /*
     * This private method only reads input until it recognizes an integer and returns it
     * @return int -- integer read
     */
    private static int validateInput() {
	Scanner in = new Scanner(System.in);
	int n = -1;
	boolean ok = true;
	while(ok) {
	    try {
		n = in.nextInt();
		ok = false;
	    } catch(InputMismatchException e) {
		while (System.in.read() != -1)
		    continue;
	    } catch(NoSuchElementException e) {
		return EOFERROR;
	    }
	}
	return n;
    }
    
    private static Vector readParams(String[] args, LinkedList<String> db) {
	reps = -1;
	phdict = "";
	Vector v = new Vector(2);
	try {
	    for(int i = 0; i < args.length; i++) {
		switch(args[i]) {
		case "-reps":
		    reps = Integer.parseInt(args[i+1]);
		    db = new LinkedList<String>();
		    break;
		case "-i":
		    interactive = true;
		    break;
		case "-phone":
		    phone = i+1!=args.length ? args[i+1] : "";
		    break;
		default: break;
		}
	    }
	    if(reps == -1) {
		System.out.println("Missing argument:\n-reps: amount of experiments to perform.");
		System.exit(1);
	    }
	} catch(ArrayIndexOutOfBoundsException e) {
	    System.err.println("Missing argument:\n-reps: amount of experiments performed.");
	    System.exit(1);	    
	} catch(NumberFormatException e) {
	    System.err.println("Wrong parameter for flag: "+args[i]);
	    System.exit(1);
	}
	v.add(0,reps);
	v.add(1,phone);
	return v;
    }
    
    public static void work() {
	int reinforcement = 1;
	Scanner scan = new Scanner(System.in);
	String stream = "", hyp = ""; //input stream
	Vector v = new Vector(2);
	try {
	    for(int i = 0; !(stream = scan.nextLine().trim()).equalsIgnoreCase("end") && i < reps; i++) {
		if(stream.equals("")) {continue;}
		h = new Hypotheses(readPhonemes(stream),prevDB,i+1);
		do {
		    System.out.println("Golem says: "+(hyp = h.emitHyp(hyp)));
		    reinforcement = validateInput();
		    if(reinforcement == EOFERROR) {break;}
		    if(reinforcement != 1 && reinforcement != EOFERROR) {
			if(h.prevDatabase.isEmpty()) {
			    v.add(0,hyp);
			    v.add(1,h.getScore(hyp));
			    h.prevDatabase.add(v);
			    v = new Vector(2);
			} else {
			    for(int a = 0; a < h.prevDatabase.size(); a++) {
				if(((String)h.prevDatabase.get(a).get(0)).equals(hyp))
				    break;
				if(a+1 == h.prevDatabase.size()) {
				    v.add(0,hyp);
				    v.add(1,h.getScore(hyp));
				    h.prevDatabase.add(v);
				    v = new Vector(2);
				    break;
				}
			    }
			}
		    }
		} while(reinforcement!=1 && reinforcement!=EOFERROR);//the program only accepts a 1 as a "positive reinforcement"
		if(reinforcement == EOFERROR) {break;}
		h.reinforce(hyp);
		System.out.println("Golem has reinforced the word '"+hyp+"'\n---------------------------------------------------------------------");
		db.add(hyp);
		prevDB = h.prevDatabase;
		if(i+1 == reps) {break;}
		hyp = "";
	    }
	} catch(Exception e) {}
	System.out.println("Learned words:");
	for(int y = 0; y < db.size(); y++) {
	    if(db.get(y) != null)
		System.out.println((String)db.get(y));
	}
    }

    /** Main method */
    public static void main(String[] args) {
	Vector v = new Vector(2);
	arguments = args;
	v = readParams(args,db);
	if(interactive)
	    work(db,v.elementAt(0),v.elementAt(1));
	else {
	    try {
		Agent agent = new PhonemeLearnAgent();
		agent.facilitatorConnect(args);
		agent.start();
	    } catch(AgentException ex) {
		System.err.println("Failed to start PhonemeLearnAgent");
		ex.printStackTrace();
		System.exit(1);
	    }
	}
	System.exit(0);
    }

    /* OAA Methods */

    public String getAgentCapabilities() {
        return "[identifyPhonemes(phonemeStream, CANDIDATE), reinforce(reinforcement, RESULT)]";
    }
    
    public String getAgentName() {
        return AGENT_NAME;
    }
    
    public String oaaIdentify(String stream) throws Exception {
	String hyp;
	if(stream.equals("")) {throw new Exception("Invalid stream of phonemes.");}
	h = new Hypotheses(readPhonemes(stream),prevDB,i+1);
	System.out.println("Golem says: "+(hyp = h.emitHyp(hyp)));
	return hyp;
    }
    
    public String oaaReinforce(int reinforcement) {
	Vector v = new Vector(2);
	String hyp = ""; //input stream
	try {
	    if(reinforcement == EOFERROR) {break;}
	    if(reinforcement != 1 && reinforcement != EOFERROR) {
		if(h.prevDatabase.isEmpty()) {
		    v.add(0,hyp);
		    v.add(1,h.getScore(hyp));
		    h.prevDatabase.add(v);
		} else {
		    for(int a = 0; a < h.prevDatabase.size(); a++) {
			if(((String)h.prevDatabase.get(a).get(0)).equals(hyp))
			    break;
			if(a+1 == h.prevDatabase.size()) {
			    v.add(0,hyp);
			    v.add(1,h.getScore(hyp));
			    h.prevDatabase.add(v);
			    break;
			}
		    }
		}

	    }
	    h.reinforce(hyp);
	    System.out.println("Golem has reinforced the word '"+hyp+"'\n---------------------------------------------------------------------");
	    db.add(hyp);
	    prevDB = h.prevDatabase;
	} catch(Exception e) {}
	System.out.println("Golem says: "+(hyp = h.emitHyp(hyp)));
	return hyp;
    }

    public static boolean oaaDoEventCallback(IclTerm goal, IclList params, IclList answers) {
	public static int reps;
	public static String phdict;
	public static Hypotheses h = null;
	public static String[] arguments;
	public static LinkedList<String> db = null;
	public static LinkedList<Vector> prevDB = null;
	public static boolean interactive = false;
	public static final int EOFERROR = -123456;
	public static final String AGENT_NAME = "PhonemeLearnAgent";
	if(goal.toItentifyingString().equals("identifyPhonemes")) {
	    IclTerm phonemeStream = goal.getTerm(0);
	    try {
		String stream = phonemeStream.toString(), candidate = oaaIdentify(stream);
		IclTerm answer = new IclStruct("identifyPhonemes",(IclTerm)stream.clone(),new IclTerm(candidate));
		answers.add(answer);
	    } catch(Exception ex) {
                getLogger().error("Failed to add", ex);
	    }
	}
	if(goal.toItentifyingString().equals("reinforce")) {
	    IclTerm reinforcement = goal.getTerm(0);
	    try {
		int reinf = ToInt.getInstance().from(reinforcement);
		String result = oaaReinforce(reinf);
		IclTerm answer = new IclStruct("reinforce",(IclTerm)reinforcement.clone(),new IclTerm(result));
		answers.add(answer);
	    }  catch(EOFException ex) {
		System.out.println("Learned words:");
		for(int y = 0; y < db.size(); y++) {
		    if(db.get(y) != null)
			System.out.println((String)db.get(y));
		}
	    } catch(Exception ex) {
                getLogger().error("Failed to add", ex);
	    }		
	    return true;
	}
    }
}