//****** paquetes API JAVA *******
import java.util.HashMap; //codigo de gollem iimas
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//******** paquetes OAA **********
import com.sri.oaa2.com.*;
import com.sri.oaa2.lib.*;
import com.sri.oaa2.icl.*;
import com.sri.oaa2.agentlib.*;

public class AgenteInterpreta extends AgentImpl{

    private final static  String AGENT_NAME = "InterpretaVoz";
    public static String MEMORIA = "algo";
    public static String texto = "";
    public static String prop = "";
    public static String res = "";
    public static String[] predInt = null;
    public static String[] args_usuario = null;    //variables de usuario
    public static String pred_usuario = "";
    public static HashMap<String,String> mapaDePredicados = new HashMap<String,String>();
    public static HashMap<String,Object> mapaDeValores = new HashMap<String,Object>();
    public static HashMap<String,Object> mapaDeArgumentos = new HashMap<String,Object>();//contiene el predicado y un arreglo de sus argumentos encontrados en el archivo


    public static void main(String[] args) throws IOException{
                
        boolean modo = true; //varible para que el programa se ejecute en modo prueba
        boolean verbose = false;
        boolean actArchivo = false;
        boolean expectation = true;
        String archInt = ""; 
        //#########################
        String entExpEsp = "";
        String entIntencion = "";
        //##########################
        BufferedReader lectura = new BufferedReader(new InputStreamReader(System.in));
        //######################### activacion de banderas para los argumentos ##############################
        
        for(int i = 0; i < args.length; i++){
            
            System.out.println("Prop :"+args[i]);

            if(args[i].equals("-t") || args[i].equals("--test"))
                modo = false;
                
            if(args[i].equals("-v") || args[i].equals("--verbose"))
                verbose = true;

            if(args[i].equals("-ne") || args[i].equals("--noexpectation"))
                expectation = false;

            if(args[i].equals("-a") || args[i].equals("--archivo")){
                actArchivo = true;
                modo = false;
                archInt = args[i+1]; //archivo de intenciones
                }
            if(args[i].equals("-p") || args[i].equals("--prop")){
                prop = args[i+1];   //archivo de predicados
               }
        }

        if(modo){
            System.out.println("modo agente");

            try {
                  Agent agent = new AgenteInterpreta();
                  agent.facilitatorConnect(args);
                  agent.start();
            } catch(AgentException ex) {
                  System.err.println("Failed to start InterpretaVoz agent");
                  ex.printStackTrace();
                  System.exit(1);
            }

        }
        else{ //################################### creacion del mapa del programa #################################

            System.out.println("*** modo test ***");
            texto = WordSpotting.leer(prop);//convierte el archivo lo crea en una sola cadena
            predInt = WordSpotting.separadorDePredicadosIntenciones(texto);//separa la cadena texto en varias cadenas por predicado
            mapaDePredicados = WordSpotting.mapaDeIntenciones(predInt,mapaDeArgumentos); //convertimos cada cadena del predicado en un mapa con la estructura (predicado,expresiones regulares), el mapa de argumentos es modificado y no es retornado en el metodo
            mapaDeValores=WordSpotting.mapaPorCampos(mapaDePredicados); //a partir del mapa de intenciones creamos un nuevo mapa en con la siguiente estructura (predicado,mapa) nota: el mapa seran todas las expresiones regulares con su valor correspondiente
            
            if(verbose){

                System.out.println("*** modo verbose ***");
                WordSpotting.imprimeMapa(mapaDeValores);
                WordSpotting.imprimeMapaArg(mapaDeArgumentos);
            }

            //######################################seccion de entrada de datos #####################################

            if(expectation){
                    try{
                        System.out.println("Ingresa las expresiones esperadas");
                        entExpEsp = lectura.readLine();
                        args_usuario = WordSpotting.ObtenerposicionArg(entExpEsp);
                        pred_usuario = WordSpotting.ObtenerPredicado(entExpEsp);
                        System.out.println("numero de argumentos:"+args_usuario.length);
                        System.out.print("argumentos ingresados:");
                        for(String argumento: args_usuario)
                            System.out.print(argumento+" ");
                        System.out.println("\npredicado ingresado:"+pred_usuario);
                    }
                    catch(IOException e){
                        System.err.println("error al iniciar la lectura de datos");
                        System.exit(1);
                    }
            }

            if(!actArchivo){
                    try{
                        System.out.println("Ingresa la intencion");
                        entIntencion = lectura.readLine();
                    }
                    catch(IOException e){
                        System.err.println("error al iniciar la lectura de datos");
                        System.exit(1);
                    }
            }
            //##################################### seccion de busqueda ##################################################

            if(actArchivo){
                    texto = WordSpotting.leer(archInt);//convierte el archivo lo crea en una sola cadena
                    predInt = WordSpotting.separadorDePredicadosIntenciones(texto);//separa la cadena texto en varias cadenas por predicado
                    System.out.println("intenciones en el archivo: "+archInt);

                    if(expectation){
                            System.out.println("busqueda con expectativa:");

                            for(String intencion: predInt){
                                System.out.print(intencion);
                                res = WordSpotting.buscarConExp(pred_usuario,args_usuario,mapaDeArgumentos,intencion,mapaDeValores);
                                System.out.println(res);
                            }
                    }
                    else{
                            System.out.println("busqueda sin expectativa:");
                            for(String intencion: predInt){
                                System.out.println(intencion+": ");
                                res = WordSpotting.buscarSinExp(intencion,mapaDeValores,mapaDeArgumentos); //falta modificar el valor de retorno
                            }
                    }

            }
           else{
                    if(expectation){
                            System.out.println("*** busqueda con expectativa ***\n");
                            res = WordSpotting.buscarConExp(pred_usuario,args_usuario,mapaDeArgumentos,entIntencion,mapaDeValores);
                            System.out.println(res);
                    }
                    else{
                            System.out.println("*** busqueda sin expectativa ***\n");
                            res = WordSpotting.buscarSinExp(entIntencion,mapaDeValores,mapaDeArgumentos);
                    }
            }
        }    
     } //fin del metodo main


//#################################metodos del OAA #################################################


    public String getAgentCapabilities() {
		return "[interpretaVoz(Cadena1,Cadena2,Cadena3),interpretaVoz(Cadena1,Id,Cadena2,Cadena3)]";
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