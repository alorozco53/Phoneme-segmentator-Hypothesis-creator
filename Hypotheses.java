import java.util.LinkedList;
import java.util.Vector;
import java.lang.Math;
/**
 * Class that creates, manages, emits, and reinforces hypotheses from a stream of phonemes.
 * A reinforcement learning approach is used here by scoring each candidate hypothesis.
 * Hypothesis are only of the form CVCV
 * @author alorozco53
 * @version 09.0.23.13
 * @see Phoneme
 */
public class Hypotheses {

    protected Phoneme[] stream;
    protected String[] hyps; //array with the found hypotheses
    protected int[] scores;
    protected LinkedList<Vector> prevDatabase; //this list saves all the non-utilized hypotheses of a single experiment

    /**
     * Constructor with two parameters
     * @param str -- new stream of phonemes
     * @param pd -- list of phonemes identified in prior experiments
     * @param sumcoeff -- determines the initialized 'standard' score for scores[]
     */
    public Hypotheses(Phoneme[] str, LinkedList<Vector> pd, int sumcoeff) {
	LinkedList<Phoneme> aux = new LinkedList();
	this.prevDatabase = pd == null ? new LinkedList() : pd;
	for(int i = 0; i < str.length; i++) {
	    if(str[i] != null)
		aux.add(str[i]);
	}
	this.stream = new Phoneme[aux.size()];
	for(int j = 0; j < stream.length; j++)
	    this.stream[j] = (Phoneme)aux.get(j);
	createHyps(sumcoeff);
    }

    /**
     * This method creates all the hypotheses from the stream and stores them in hyps.
     * It intitializes the scores array, as well.
     * @param sumcoeff -- determines the initialized 'standard' score for scores[]
     */
    public void createHyps(int sumcoeff) {
	int j,k,l,m;
	boolean flag = false;
	String temp1 = "", temp2 = "", temp3 = "";
	LinkedList<String> list = new LinkedList(); //temporal hyps linked list
	for(int i = 0; i < stream.length;  i++) {
	    if(stream[i]==null || stream[i].isVowell() || i+3>=stream.length) {continue;}
	    for(j = i; stream[j].isConsonant() && j<stream.length; j++)
		if(j+1 >= stream.length) {flag = true; break;}
	    if(!stream[j].isVowell() || flag || j+2>=stream.length) {continue;}
	    for(k = j; stream[k].isVowell() && k<stream.length; k++)
		if(k+1 >= stream.length) {flag = true; break;}
 	    if(!stream[k].isConsonant() || flag || k+1>=stream.length) {continue;}
	    for(l = k; stream[l].isConsonant() && l<stream.length; l++)
		if(l+1 >= stream.length) {flag = true; break;}
	    if(!stream[l].isVowell() || flag || l>=stream.length) {continue;}
	    for(m = l; stream[m].isVowell() && m<stream.length; m++)
		if(m+1 >= stream.length) {flag = true; break;}
	    for(int p = i; p < j; p++) {
		temp1 = stream[p] + " ";
		for(int x = j; x < k; x++) {
		    temp2 = temp1 + stream[x] + " ";
		    for(int y = k; y < l; y++) {
			temp3 = temp2 + stream[y] + " ";
			for(int z = l; z <= m; z++) {
			    if(z==list.size()) {break;}
			    list.add(temp3+stream[z]);
			}
			temp3 = "";
		    }
		    temp2 = "";
		}
		temp1 = "";
	    }
	}
	this.hyps = new String[list.size()];
	this.scores = new int[list.size()];
	System.out.println("Identified hypotheses:\t\tScores:");
	for(int u = 0; u < list.size(); u++) {
	    this.hyps[u] = list.get(u);
	    this.scores[u] = sumcoeff;
	    for(int t = 0; t < prevDatabase.size(); t++) { //checks if the identified hypotheses appeared earlier
		if(hyps[u].equals((String)prevDatabase.get(t).get(0)))
		    scores[u] = (Integer)prevDatabase.get(t).get(1);
	    }
	    System.out.println((u+1)+". "+hyps[u]+"\t\t\t"+scores[u]);
	}
	System.out.println("--------------------------------------------");
    }

    /**
     * This method receives as parameter a negatively reinforced hypothesis and increases everyone else's score.
     * Then it 'randomly' calculates and returns another hypothesis using the scores and taking the maximum score as an upper bound.
     * @param h -- negatively reinforced hypothesis (the first time this method is called, h equals "")
     * @return String -- new hypothesis
     */
    public String emitHyp(String h) {
	int maxScore = 1;
	double random1, random2; //random1 will determine a new set of hypotheses while random2 will chose a specific one
	for(int i = 0; i < hyps.length; i++) {
	    if(hyps[i].equals(h)) {
		scores[i]+=5;
		maxScore = scores[i] > maxScore ? scores[i] : maxScore;
	    }
	    System.out.println((i+1)+". "+hyps[i]+"\t\t\t"+scores[i]);
	}
	System.out.println("-----------------------------------------------");
	random1 = (((double)hyps.length)-0.001)*Math.random();
	random2 = (1.0/(double)maxScore)*Math.random();
	System.out.println("random1: "+random1+"\trandom2: "+random2);
	System.out.println("1.0/(double)scores[(int)random1]: "+1.0/(double)scores[(int)random1]);
	while(1.0/(double)scores[(int)random1]<=random2 && hyps[(int)random1]==null) {
	    random1 = ((double)hyps.length-0.001)*Math.random();
	    System.out.println("random1: "+random1+"\trandom2: "+random2);
	}
	System.out.println("hyps[random1]: "+hyps[(int)random1]);
	return hyps[(int)random1];
    }

    /**
     * Given a string (hypothesis) as a parameter, returns its current score or -1, if not found
     * @param hyp -- hypothesis
     * @return int -- hyp's current score
     */
    public int getScore(String hyp) {
	for(int i = 0; i < hyps.length; i++) {
	    if(hyp.equals(hyps[i]))
		return scores[i];
	}
	return -1;
    }

    /**
     * This method reinforces the given word by increasing the other one's scores.
     * It also appends the given hypothesis to the prevDatabase and saves its current score
     * @param hyp -- hypotheses to reinforce
     */
    public void reinforce(String hyp) {
	Vector v = new Vector(2);
	v.add(0,hyp);
	v.add(1,getScore(hyp));
	prevDatabase.add(v);
	for(int i = 0; i < prevDatabase.size(); i++) {
	    if(!hyp.equals((String)prevDatabase.get(i).elementAt(0)))
		prevDatabase.get(i).set(1,((Integer)prevDatabase.get(i).elementAt(1))+5);
	}
    }
}
