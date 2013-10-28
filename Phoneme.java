import java.io.*;
import java.util.Scanner;
/**
 * Class that simulates a phoneme according to the Mexican Spanish phoneme dictionary created at Golem IIMAS,UNAM.
 * A string will be considered a phoneme if and only if it is included in the 'phoneme.phone' file
 * NOTE: The user can change the current phoneme list just by modifying the previous file.
 * @author alorozco53
 * @version 09.0.23.13
 * @see PhonemeNotFoundException
 */
public class Phoneme {

    protected String phoneme; //string representation of the phoneme
    protected String phFile; //phoneme dictionary

    /** 
     * Constructor with two parameters
     * @param p -- new phoneme
     * @param phdict -- new phoneme dictionary
     * @throws PhonemeNotFoundException -- if the initialized parameter is not a valid string representation of a phoneme
     */
    public Phoneme(String p, String phdict) throws Exception {
       	try {
	    FileInputStream fstream = new FileInputStream(phdict.equals("") ? (this.phFile="phonemes.phone") : (this.phFile=phdict.trim()));
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    while((strLine = br.readLine()) != null) {
		if(strLine.trim().substring(0,strLine.indexOf(' ')).equals(p)) {
 		    this.phoneme = p;
		    break;
		}
	    }
	    in.close();
	    if(this.phoneme == null)
	   	throw new PhonemeNotFoundException("The input string '"+p+"' is not a phoneme, please refer to the file 'phonemes.phone'");
	} catch (FileNotFoundException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (SecurityException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IndexOutOfBoundsException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	}
    }
    
    /**
     * ToString Method
     * @return String -- the string representatation of the phoneme
     */
    public String toString() {
	return phoneme;
    }
        
    /**
     * Returns true if the phoneme is a consonant; else, it returns false
     * @return boolean -- true if the phoneme is a consonant
     */
    public boolean isConsonant() {
	try {
	    FileInputStream fstream = new FileInputStream(phFile);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    while((strLine = br.readLine()) != null) {
		if(strLine.trim().substring(0,strLine.indexOf(' ')).equals(phoneme))
		    return strLine.trim().charAt(strLine.length()-1) == 'c';
	    }
	    in.close();
	} catch (FileNotFoundException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (SecurityException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IndexOutOfBoundsException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	}
	return false;
    }
    
    /**
     * Returns true if the phoneme is a vowell; else, it returns false
     * @return boolean -- true if the phoneme is a vowell
     */ 
    public boolean isVowell() {
	try {
	    FileInputStream fstream = new FileInputStream(phFile);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    while((strLine = br.readLine()) != null) {
		if(strLine.trim().substring(0,strLine.indexOf(' ')).equals(phoneme))
		    return strLine.trim().charAt(strLine.length()-1) == 'v';
	    }
	    in.close();
	} catch (FileNotFoundException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (SecurityException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	} catch (IndexOutOfBoundsException e) {
	    System.err.println("Error: " + e.getMessage());
	    System.exit(1);
	}
	return false;
    }

    /** This main only tests the current class */
    public static void main(String[] args) {
	for(int i = 0; i < args.length; i++)
	    System.out.println("i: "+i+"\t"+args[i]);
	Phoneme[] array = new Phoneme[10];
	Scanner in = new Scanner(System.in);
	for(int i = 0; i < array.length; i++) {
	    try {
		array[i] = new Phoneme(in.nextLine().trim(),"");
		System.out.println("i: "+i+"\t"+array[i] + (!array[i].isConsonant() ? "\tvowell" : "\tconsonant"));
	    } catch(PhonemeNotFoundException e) {
		System.out.println("oidjfisdjfisdjfi");
		i--;
	    } catch(Exception e) {
		System.err.println("Error: " + e.getMessage());
		i--;
	    }
	}
	System.exit(0);
    }
}