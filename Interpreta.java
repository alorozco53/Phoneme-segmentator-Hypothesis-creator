/* $Id: Valida.java 03/2009 */
/* 
 * Copyright (C) 2009 Elia Pavon 
 *                    Myriam Reys
 *                    Nasheilly Vasques
 *                    Ivan V. Meza-Ruiz (http://turing.iimas.unam.mx/~ivanvladimir)
 *
 * Recibe una lista de posibles intenciones cadena1 para que se haga valida alguna de ellas buscando en el texto que manda el reconocedor de voz en
 * cadena2 produciendo como salida una intencion para mandarlo al Manejador de Dialogo en el tercer parametro
 *
 */

import com.sri.oaa2.com.*;
import com.sri.oaa2.lib.*;
import com.sri.oaa2.icl.*;
import com.sri.oaa2.agentlib.*;
import java.io.*;
import java.io.Console;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;





/*
 *
 */
public class Interpreta extends AgentImpl
{
	/* The name of this agent */
	public final static String AGENT_NAME = "InterpretaVoz";
	
	public static String INTENCION = "nada";

	public static String MEMORIA = "algo";
	public static String IdPrev;

    public static HashMap intentions;
    public static int mode = 1; //0 test, 1 agent
    public static String prop = "prop.txt"; //0 test, 1 agent

	
	/* Starts the Interpreta agent. */
	public static void main(String[] args)
	{
        String s;
        for (int i = 0; i < args.length; i++){
            s = args[i];
            System.err.println("Prop :"+s);

            if(s.equals("--test") || s.equals("-t"))
            {
                mode=0;
            }
            if(s.equals("--prop") || s.equals("-p"))
            {
                prop=args[i+1];
                i++;
            }
        }
        System.err.println("Modo :"+mode);
        System.err.println("Prop :"+prop);
        
        if(mode == 1){
            try {
                  Agent agent = new Interpreta();
                  agent.facilitatorConnect(args);
                  agent.start();
            } catch(AgentException ex) {
                  System.err.println("Failed to start InterpretaVoz agent");
                  ex.printStackTrace();
                  System.exit(1);
            }
        }else{
            Agent agent = new Interpreta();

            Console console = System.console();
            if (console == null) {
                System.err.println("No console.");
                System.exit(1);
            }
            String exp;
            String inten;
            String id;
            do{
                inten=console.readLine("%nEntra las expresiones esperadas ");
                id=console.readLine("%nEntra el id de la expresion ");
                exp=console.readLine("%nEntra la intension experada: ");
                ArrayList<String> res=interpreto(id,inten,exp);
                String intention;
                if(res.size()>1)
                {
                    intention=restostring(res);
                }
                else{
                    intention=restostring(res);
                }
                System.out.println(intention);
            }while(exp.length() > 0);
        }
	}

    public static String restostring(ArrayList<String> res){
                String intention=res.get(0);
                if(res.get(0).equals("nada"))
                        {
                            return "nada";
                        }
                System.out.println(res);
                if(res.size()>1){
                    if(res.size()==3 && res.get(2).equals("SAME")){
                        return res.get(1);
                        
                    }else{
                        intention+="(";
                    }
                }
                for(int c=1;c<res.size()-1;c++)
                {
                    intention+=res.get(1)+",";
                }
                if(res.size()>1){
                    String arg=res.get(res.size()-1);
                    if(arg.contains(" ")){
                        arg="'"+arg+"'";
                    }
                    intention+=arg;
                    intention+=")";
                }
                return intention;
    }

	public Interpreta(){
		super();
        intentions = new HashMap();
        int state=0;

        // Lee archivo
        try{
	        BufferedReader rd;
			rd = new BufferedReader(new FileReader(prop));
            // Line 1: pred (state 0)
            // Lines : parameter per line (state 1)
            // Line n: ; (state 2)
            ArrayList argsvals= new ArrayList();

            String  pred="NONE";
            String texto;
            HashMap args=new HashMap();
            HashMap<String,String> pas=new HashMap();
		    if(rd.ready()){
                while( (texto = rd.readLine()) != null ){					
					if(state == 0)//En caso de encontrar una intencion en el archivo
					{
                        pred = fix_predicate(texto);
		        System.out.println(pred);
                        args = new HashMap();
                        String [] pa  = getPredArgs(texto);
                        String [] opa = getPredArgs(pred);
                        for(int c=1; c<pa.length;c++){
                            pas.put(pa[c],opa[c]);
                        }
                        state=1;
                    }
					else if(state==1 && texto.equals(";"))//indica fin de un bloque en el archivo
					{
                        if(!pred.equals("NONE")){
                            intentions.put(pred,args);
                            state=0;
                        }
                           					}
					else if(state==1)//Verificando los parametros de esa intencion
					{
                        String [] bits = texto.split("=");
                        String [] res;
                        String val;
                        if(bits.length>1){
                            res = bits[1].split(":");
                            val = pas.get(bits[0]);
                        }else{
                            res = bits[0].split(":");
                            val = "SAME";
                        }
                        // Stores the regular expresion
                        ArrayList regs = new ArrayList();
                        // Stores the labels to subsitutite the label
                        ArrayList labels = new ArrayList();
                        // Puts together bot regs and labels
                        ArrayList reg_label = new ArrayList();
                        reg_label.add(regs);
                        reg_label.add(labels);
                        Pattern pat;
                        String label;
                        for(String rl: res)                            
                        {   
                            String [] bits2 = rl.split(",");
                            if(bits2.length > 1){
                                pat = Pattern.compile(bits2[0]);
                                regs.add(pat);
                                labels.add(bits2[1]);
                            }else{
                                pat = Pattern.compile(bits2[0]);
                                regs.add(pat);
                                labels.add("VAL");

                            }
                        }
                        args.put(val,reg_label);
				    }
				}
			} 
        }catch(Exception ex) {ex.printStackTrace();}
        IdPrev = "__";
	}

	public String getAgentCapabilities() {
		return "[interpretaVoz(Cadena1,Cadena2,Cadena3),interpretaVoz(Cadena1,Id,Cadena2,Cadena3)]";
	}

	public String getAgentName() {
		return AGENT_NAME;
	}

	public boolean oaaDoEventCallback(IclTerm goal, IclList params, IclList answers) {
		boolean result=false;

        if(goal.toIdentifyingString().equals("interpretaVoz")) {
            String Cadena1;
            String Cadena2;
            String Cadena3;
            String Id;
          

     
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
					    ArrayList<String> res=interpreto(Id,cadena1,Cadena2);
                        String intention=restostring(res); 
			System.out.println("INTENTION: "+ intention);
                        System.out.println("Res "+res);
                        IclTerm answer;
                        if(Id==null){
                            
						    answer = new IclStruct("interpretaVoz", IclTerm.fromString(Cadena1), new IclStr(Cadena2),IclTerm.fromString(intention));
                        }else{
                            
						    answer = new IclStruct("interpretaVoz", IclTerm.fromString(Cadena1), new IclStr(Id), new IclStr(Cadena2),IclTerm.fromString(intention));
                        } 
						//Add our answers to the list of answers
						answers.add(answer);
						System.out.println("INTENTION: "+ intention);
						MEMORIA = Cadena2; // Keep the last recognised phrase
					//return true to indicate success
					result = true;
				}catch(Exception ex) {
					getLogger().error("Failed to add",ex);
				}
		}
		return result;
	}


  public static String fix_predicate(String pred)
  {
 			//Obtiene campos y parametros
			String [] campos = pred.split("[\\(,]");
			//Estandarizando las intenciones
			String nada=campos[0];
			String tipoA = campos[0] + "_";
			String [] param_answer;
			boolean simple=false;
			
			if(campos.length==1)
				{param_answer = new String[1]; simple=true;}
			else
             	{param_answer = new String[campos.length-1];}
                    
			//Estandarizando la cadena 1
			for(int k=0;k<campos.length-1;k++)
				tipoA = tipoA + k +"_";
		
    
            return tipoA;
  }

  public static String[] getPredArgs(String pred)
  {
    String [] all = pred.split("(\\(|_|,|\\))");
    return all;
  }
       	
  public static ArrayList<String> interpreto(String Id, String cadena1_, String cadena2_)
        {

	String cadena2 = cadena2_.toLowerCase();
	String cadena1 = cadena1_.toLowerCase();
		System.out.println("La cadena1 Recibiendo+funcion(): "+cadena1);
        System.out.println("cadena2 Recibiendofuncion(): "+cadena2);
	    String intencion = "";
        ArrayList res=new ArrayList();
    
        int j=0;


        System.out.println(Id);
        System.out.println(IdPrev);

        if(Id!=null){
            if(Id.equals(IdPrev)){
                System.out.println("No escuche nada");
                res.clear();
                res.add("nada");
                return res;
            }
            IdPrev=Id;
        }

	    boolean band1=false,intigual=false,resp=false;
	 ArrayList<String> intenciones = new ArrayList();

	    int ii=0;
            int bk=0;
	    int inside=0;

	    while(ii<cadena1.length()){
		System.out.print(cadena1.charAt(ii));
		System.out.print(bk);
		System.out.println(ii);
		if(inside>0){
			if(cadena1.charAt(ii)=='(')
                           inside++;
                        else if(cadena1.charAt(ii)==')'){
			   inside--;
			}
		}else{
		  if(cadena1.charAt(ii)==','){
			intenciones.add(cadena1.substring(bk,ii));
		        System.out.println(cadena1.substring(bk,ii));
                        bk=ii+1;
		  } else if(cadena1.charAt(ii)=='(')
                           inside++;

		}
                ii++;
	    }
	    intenciones.add(cadena1.substring(bk,ii));




	    boolean finded=false;
	    //Recorre intencion por intencion de la cadena1
	    for(String inten: intenciones)
		{	
                  
		            System.out.println("Intention "+inten);
		            System.out.println("Intention "+intentions);
                    if(res.size()>1){
                        break;
                    }

                    String tipoA = fix_predicate(inten);
		            System.out.println("De la cadena 1 tipo A estandarizada "+tipoA);

                   
                    if(intentions.containsKey(tipoA))
                    {
                        HashMap args = (HashMap)intentions.get(tipoA);
                        String [] pa = getPredArgs(tipoA);
                        String [] opa = getPredArgs(inten);

                        res = new ArrayList();
                        res.add(pa[0]);

		                System.out.println("args"+args);



                        if(args.containsKey("SAME")){
                                ArrayList reg_label        = (ArrayList)args.get("SAME");
                                ArrayList<Pattern> regs    = (ArrayList)reg_label.get(0);
                                ArrayList<String> labels   = (ArrayList)reg_label.get(1);

                                boolean found = false;
                                int cc = 0;
                                Matcher match; 
                                while(!found && cc < regs.size()){
                                        Pattern pat = regs.get(cc);
                                        match = pat.matcher(cadena2);
		                                System.out.println(match);
                                        if(match.find()){
                                           found=true;
                                           finded=true;
                                           if(labels.get(cc).equals("VAL")){
                                                res.add(match.group());
                                                res.add("SAME");
                                           }else{
                                                res.add(labels.get(cc));
                                                res.add("SAME");
                                           }
                                        }
                                        cc++;
                                }
                        }else{
                            

		                System.out.println("pa"+pa);
		                System.out.println(pa.length);

                        for(int c=1; c< pa.length; c++)
                        {
                            if(args.containsKey(pa[c])){

                                ArrayList reg_label = (ArrayList)args.get(pa[c]);
                                ArrayList<Pattern> regs      = (ArrayList)reg_label.get(0);
                                ArrayList<String> labels    = (ArrayList)reg_label.get(1);

                                boolean found = false;
                                int cc = 0;
                                Matcher match; 
                                while(!found && cc < regs.size()){
                                        Pattern pat = regs.get(cc);
                                        match = pat.matcher(cadena2);
		                                System.out.println(match);
                                        if(match.find()){
                                           found=true;
                                           finded=true;
                                           String label = labels.get(cc);
                                           if(label.equals("VAL")){
                                                res.add(match.group());
                                           }else{
                                                res.add(label);
                                           }
                                        }
                                        cc++;
                                }
                            }else{
                               res.add(opa[c]);
                            }   
                        }
                        } 
                       

                    }
				
		}//fin if


        if(!finded){
            res.clear();
            res.add("noEntendi");
        }

        System.out.println("SALE>>>>>>>");
        System.out.println(res);
        return res;
        }


}

