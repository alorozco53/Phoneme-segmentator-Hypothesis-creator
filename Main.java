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
public class Main extends AgentImpl{
    
    public static final int EOFERROR = -123456;
    public static final String AGENT_NAME = "PhonemeLearnAgent";
    public static int shyte = 98948;

    /** Default constructor */
    public Main() {
        super();
    }
    
    /**
     * Reads a stream of phonemes and returns an array of them
     * @param stream -- stream of phonemes
     * @param phdict -- phoneme dictionary
     * @return Phoneme[] -- array of all found phonemes
     */
    public static Phoneme[] readPhonemes(String stream, String phdict) {
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
	    } catch(NoSuchElementException e) {
		return EOFERROR;
	    }
	}
	return n;
    }
    
    /** Main method */
    public static void main(String[] args) {
	int reps = 1, reinforcement = 1;
	Scanner scan = new Scanner(System.in);
	String stream = "", hyp = ""; //input stream
	LinkedList<String> db = null;
	LinkedList<Vector> prevDB = null;
	Vector v = new Vector(2);
	Hypotheses h = null;
	try {
	    if(args[0] == null) {
 		System.out.println("Missing argument:\n-reps: amount of experiments performed.");
		System.exit(1);
	    }
	    reps = Integer.parseInt(args[1]);
	    db = new LinkedList<String>();
	} catch(ArrayIndexOutOfBoundsException e) {
	    System.err.println("Missing argument:\n-reps: amount of experiments performed.");
	    System.exit(1);	    
	} catch(NumberFormatException e) {
	    System.err.println("Wrong parameter for flag: "+args[0]);
	    System.exit(1);
	}
	try {
	    for(int i = 0; !(stream = scan.nextLine().trim()).equalsIgnoreCase("end") && i < reps; i++) {
		if(stream.equals("")) {continue;}
		h = new Hypotheses(readPhonemes(stream,args.length>2 ? args[3] : ""),prevDB,i+1);
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
		} while(reinforcement != 1 && reinforcement != EOFERROR);//the program only accepts a 1 as a "positive reinforcement"
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
	System.exit(0);
    }

    /*Métodos del OAA */

    public String getAgentCapabilities() {
        return "[learnPhonemes(phonemeStream,phonemeDict), learnPhonemes(phonemeStream)]";
    }
    
    public String getAgentName() {
        return AGENT_NAME;
    }
    
    public boolean oaaDoEventCallback(IclTerm goal, IclList params, IclList answers) {
        boolean result=false;
        String[] expectativas;
                
        if(goal.toIdentifyingString().equals("interpretaVoz")) {
            String Cadena1;// expectativa que se espera
            String Cadena2;// cadena que el usuario entiend
            String Cadena3;// el retorno del resultado
            String Id; //mamada de programacion

            if(goal.size()==3){
                Cadena1 = goal.iclNthTerm(1).iclStr();
                Cadena2 = goal.iclNthTerm(2).iclStr();
                Cadena3 = goal.iclNthTerm(3).iclStr();


                System.out.println("La Cadena1  recibida MD3: "+goal);
                System.out.println("La Cadena1  recibida MD3: "+Cadena1);
                System.out.println("Lo que viene del reconocedor de voz: "+Cadena2);
                Id = null;
            }else{
                Cadena1 = goal.iclNthTerm(1).iclStr();
                Id = goal.iclNthTerm(2).iclStr();
                Cadena2 = goal.iclNthTerm(3).iclStr();
                Cadena3 = goal.iclNthTerm(4).iclStr();
                System.out.println("La Cadena1  recibida MD4: "+goal);
                System.out.println("La Cadena1  recibida MD4: "+Cadena1);
                System.out.println("Id: "+Id);
                System.out.println("Lo que viene del reconocedor de voz: "+Cadena2);
            }

			String [] Cadena=Cadena1.split("\\[|\\]");
			String cadena1=Cadena[1];
			System.out.println("Predicates: "+cadena1);
                        System.out.println("Expresion: "+Cadena2);
			System.out.flush();
			try {
                                
                                texto = WordSpotting.leer(prop);//convierte el archivo lo crea en una sola cadena
                                predInt = WordSpotting.separadorDePredicadosIntenciones(texto);//separa la cadena texto en varias cadenas por predicado
                                mapaDePredicados = WordSpotting.mapaDeIntenciones(predInt,mapaDeArgumentos); //convertimos cada cadena del predicado en un mapa con la estructura (predicado,expresiones regulares), el mapa de argumentos es modificado y no es retornado en el metodo          
                                mapaDeValores=WordSpotting.mapaPorCampos(mapaDePredicados); //a partir del mapa de intenciones creamos un nuevo mapa en con la siguiente estructura (predicado,mapa) nota: el mapa seran todas las expresiones regulares con su valor correspondiente
                                args_usuario = WordSpotting.ObtenerposicionArg(cadena1);
                                expectativas = cadena1.split(",");
                                for(String exp: expectativas){
                                    res = WordSpotting.buscarConExp(WordSpotting.ObtenerPredicado(cadena1),args_usuario,mapaDeArgumentos,Cadena2,mapaDeValores);                                                                    
                                    
                                    if(!res.equals("noEntendi")){
                                            IclTerm answer;
                                            if(Id == null)
                                                 answer = new IclStruct("interpretaVoz", IclTerm.fromString(Cadena1), new IclStr(Cadena2),IclTerm.fromString(res));
                                            else
                                                 answer = new IclStruct("interpretaVoz", IclTerm.fromString(Cadena1), new IclStr(Id), new IclStr(Cadena2),IclTerm.fromString(res));
                                                                    //Add our answers to the list of answers
                                            answers.add(answer);
                                            System.out.println("INTENTION: "+res);
                                            MEMORIA = Cadena2; // Keep the last recognised phrase
                                            result = true;//return true to indicate success
                                    }
                                }
			}catch(Exception ex) {
				getLogger().error("Failed to add",ex);
			}
		}
		return result;
	}
}