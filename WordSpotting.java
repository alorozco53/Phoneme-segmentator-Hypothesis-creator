
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.*;  

/*Esta clase leera un arhivo el cual contendra los predicados. Estos se guardaran en un mapa para que al momento 
 * de interpretar el programa pueda obtener un valor de lo interpretado
 ************************************************************************************************************/

public class WordSpotting {

/***************************** sobrecargando el metodo buscarIntencion ***************************************
     buscan la intencion en el mapa por medio de una llave la cual el usuario ingresara al preguntarle ¿cual es la 
     * expresion esperada?*/

                                /*########################
                                 * String expectativa : es la expectativa que introduce el usuario al momento de preguntarle
                                 * String[]argUsuario : son los argumentos obtenidos al momento que el usuario ingresa la expectativa ejemplo: nombre(x,y)-> se guarda [x,y] en el arreglo
                                 * HashMap<String,Object> PredArg : son los argumentos por cada predicado obtenidos en el archivo que el usuario requiera usar ejem: adivina.txt
                                 * String intencion : el la cadena que el usuario escribe ejemplo : me llamo abel
                                 * HashMap<String,Object> mapaDePredicados : este mapa contiene todos los predicados asi como sus valores del archivo con el cual inicia el programa
                                 */
	
			public static String buscarConExp(String expectativa,String[]argUsuario,HashMap<String,Object> PredArg,String intencion,HashMap<String,Object> mapaDePredicados){
				
				String llaveExp = null;
                                boolean activo = true;
                                boolean sinArg = true;
                                boolean entendi = false;
                                String[] nArg = {"1"} ;
                                int nArgPred;
                                String[] argumentos = null;
				String intencionResultado = null;
                                String resultado = "";
				HashMap<String,Object> mapaDeExp = new HashMap<String,Object>();
                                HashMap<String,Object> mapaDeArg = new HashMap<String,Object>();
				Pattern expReg;
				Matcher encontrado;

                                try {
                                        mapaDeArg = (HashMap<String,Object>)mapaDePredicados.get(expectativa);//obtenemos el mapa que corresponden a los argumentos que a su vez contienen las expresiones regulares
                                        argumentos = (String[])PredArg.get(expectativa);//obtenemos el arreglo de argumentos para la expectativa

                                        System.out.print("argumentos entendidos por el programa:");
                                        for(String arg: argumentos){
                                            System.out.print(arg+" ");
                                        }
                                        System.out.print("\n");
                                        if(argUsuario.length <= argumentos.length){ //evalua si los argumentos concuerdan con el archivo

                                                for(int i = 0; i<argUsuario.length;i++){ //controla la busqueda de argumentos

                                                    mapaDeExp = (HashMap<String,Object>)mapaDeArg.get(argumentos[i]);
                                                    Iterator recorreSegundo = mapaDeExp.entrySet().iterator();

                                                    while (recorreSegundo.hasNext()) {
                                                            Map.Entry j = (Map.Entry)recorreSegundo.next();
                                                            llaveExp = (String)j.getKey();
                                                            expReg = Pattern.compile(llaveExp);
                                                            encontrado = expReg.matcher(intencion);

                                                            if(encontrado.find()){
                                                                    //entendi = true;
                                                                    intencionResultado = (String)j.getValue();
                                                                    if(intencionResultado.equals("mismo"))
                                                                            intencionResultado = encontrado.group();
                                                                    if(activo){
                                                                        resultado=resultado+intencionResultado; //para que no empiece con ,
                                                                        activo=false;
                                                                    }
                                                                    else
                                                                        resultado = resultado+","+intencionResultado;

                                                                    break;
                                                            }
                                                    }
                                              //******* evalua si contiene todos los argumentos ******
                                                   nArg = resultado.split(",");
                                                   nArgPred = argumentos.length;
                                                   if(nArg.length == nArgPred) entendi = true;
                                              //********************************
                                              }
                                              if(entendi){
                                                      if(!argumentos[0].equals("0")) //evaluamos que tenga argumentos para modificar la variable
                                                            resultado = expectativa+"("+resultado+")";         //falta definir que busque que tenga todos los argu
                                               }
                                              else
                                                  resultado = "noEntendi";
                                        }
                                        else
                                            System.out.println("no coindicen los argumentos ingresados con los especificados en el archivo"); //si los argumentos no concuerda con el programa no se ejecuta lo demas
                            }catch(RuntimeException r){
                                    System.out.println("\nexpecativa no encotrada en el mapa: "+r);
                                    System.exit(1);
                            }
                                return resultado;
			}
/**********************************************************************************************************************
                         HashMap<String,Object> PredArg): mapa de cada de los arguementos obtenidos en el archivo****************/
                        
	                public static String buscarSinExp(String intencion,HashMap<String,Object> mapaDePredicados,HashMap<String,Object> PredArg){ //falta definir el nombre del metodo
                         
                             HashMap<String,Object> mapaDeArg= new HashMap<String,Object>();
                             HashMap<String,String> mapaDeValores= new HashMap<String,String>();
                             Pattern expReg;
                             Matcher encontrado;
                             String llaveRegExp;
                             String intencionResultado = "";
                             String predicado;
                             String resultados = "";
                             String[] tokens;
                             String argMapa;
                             String[] argsPred;
                             ArrayList<String> predEnc = new ArrayList(); //utilizado para los predicados encontrados
                             String[] tmp2;
                             String tmp1;
                             boolean bandera = false;
                             boolean completo;

                            Iterator i = mapaDePredicados.entrySet().iterator();
                             while (i.hasNext()){ //recorre el mapa de predicados, tenemos acceso a los predicados
                                      Map.Entry a = (Map.Entry)i.next();
                                      mapaDeArg = (HashMap<String,Object>)a.getValue(); //obtenemos el mapa de argumentos
                                      predicado = (String)a.getKey(); //obtenemos el predicado
                                      Iterator  j = mapaDeArg.entrySet().iterator(); //creamos el nuevo indice

                                      while (j.hasNext()){ //recorremos el mapa de argumentos, aqui tenemos acceso a los argumentos   de los predicado
                                                Map.Entry b = (Map.Entry)j.next();
                                                mapaDeValores = (HashMap<String,String>)b.getValue(); //obtenemos el mapa de valores asociadas a cada argumento
                                                
                                                Iterator  k = mapaDeValores.entrySet().iterator();//creamos un nuevo indice
                                                while (k.hasNext()){ //recorremos el mapa de valores, aqui tenemos acceso a las expresiones regulare
                                                        Map.Entry c = (Map.Entry)k.next();
                                                        llaveRegExp = (String)c.getKey();
                                                        expReg = Pattern.compile(llaveRegExp);
                                                        tokens=intencion.split(" "); //descomponemos la oracion por palabras
                                                        
                                                        for(String token:tokens){
                                                            encontrado = expReg.matcher(token);
                                                            if(encontrado.find()){ //encontramos el valor
                                                                intencionResultado = (String)c.getValue();
                                                                if(intencionResultado.equals("mismo")) //si contiene el valor de mismo extrae la cadena de la intencion
                                                                   intencionResultado = encontrado.group();
                                                                
                                                               //*******************seccion de prueba*****************
                                                                if(!predEnc.contains(predicado))// evalua si contiene el valor para no agregarlo nuevamente
                                                                   predEnc.add(predicado); //guardamos los predicados en los cuales se encontro el valor

                                                                argMapa = (String)b.getKey();  //obtenemos la llave como argumento
                                                                argsPred = (String[])PredArg.get(predicado); //obtenemos el arreglo de argumentos de ese predicado

                                                                for(int l = 0;l < argsPred.length;l++){ //buscamos coincidencias en el arreglo de argumentos con el argumento del valor encontrado
                                                                    if(argsPred[l].equals(argMapa)){ // si existe ese argumento del valor en el arreglo de arguementos                                                                    
                                                                       argsPred[l] = intencionResultado; //sustituimos el argumento con su valor                                  
                                                                    }
                                                                       PredArg.put(predicado,argsPred); //lo reeplazamos en el mapa de argumentos
                                                               } 
                                                               //*****************************************************                                                                            
                                                            }                                                                   
                                                        }

                                                }
                                                
                                       } //MAPA DE ARGUMENTOS                                      
                              }


                            if(!predEnc.isEmpty()){
                             //recorre el array list y asociar sus valores con el mapa de predicados,argumentos para posteriormente imprimirlos
                                     Iterator m = predEnc.iterator();
                                     System.out.println("predicados relacionados");
                                            while(m.hasNext()){
                                                 completo = true; //reestablecemos la bandera a su valor original
                                                 tmp1 = m.next().toString(); //obtenemos el valor de la lista de predicados encontrados
                                                 tmp2 = (String[])PredArg.get(tmp1); //obtenemos sus agrumentos asociados a ese predicado

                                                  //***********busqueda de argumentos completos*********
                                                    for(String arg: tmp2){

                                                        if(arg.length()== 1){
                                                            completo = false;
                                                            break;
                                                        }
                                                    }
                                                  //*****************************************************
                                                    if(completo){
                                                         System.out.print(tmp1+"("); //imprimimos ese predicado
                                                         for(String n: tmp2){ //recorremos los argumentos
                                                             if(bandera == false){//estos ifś sirven como formato de impresion de los argumentos
                                                                System.out.print(n);
                                                                bandera = true;
                                                             }
                                                             else
                                                                 System.out.print(","+n);
                                                         }
                                                         bandera = false; // para concatenar las comas a partir del segundo argumento
                                                         System.out.print(")\n");
                                                    }
                                            }
                            }
                            else{ System.out.println("noEntendi");}

                             return resultados;
                       }
                              
/*************************************Este metodo leer el archivo y lo convierte en una cadena*****************************************/
			public static String leer(String ruta){

					File archivo = new File(ruta);
					String linea=null;
					String texto="";
					int tamTexto;
					
					if(archivo.exists()){
							try{  
								FileReader caracter = new FileReader(archivo);
								BufferedReader lector = new BufferedReader(caracter);
								
								while((linea=lector.readLine())!= null)
									 texto += linea +"\n";	 	
					
							}catch(IOException e){
								System.out.println("ha ocurrido un error al leer el archivo");
                                                                System.exit(1);
							}
						
					}	
					else System.out.println("no se encontro el archivo");
					
					tamTexto = texto.length();
					texto = texto.substring(0,tamTexto - 1);//eliminamos el ultimo salto de linea generado por este metodo
					
					if(texto.endsWith(";")){
						texto = texto.substring(0,tamTexto - 2); //eliminamos el ultimo ; ya que esto nos provocaria error al procesarse posteriormente
					}
					
					return texto;
			 }
/************************este metodo convierte la cadena mandada en un conjunto de cadenas en un arreglo*********/	
			 public static String[] separadorDePredicadosIntenciones(String texto){
					
					 	String[] intenciones = texto.split(";");
					 	
					 	return intenciones;
			 }
/*************************Este metodo genera el primer mapa en donde contendra cada una de los predicandos la llave contendra
* los el predicado y el valos contendra los posibles valores para los argumentos de los predicados*****/
			 public static HashMap<String,String> mapaDeIntenciones(String[] intenciones,HashMap<String,Object> mapaDeArgumentos){
			
					   String[] predicado = null;
					   HashMap<String,String> mapaDeIntenciones= new HashMap<String,String>();
					   Pattern ExpReg=Pattern.compile("^\n"); //busca la intencion que comience con salto de linea
					   Matcher encontrado;
					   
						for(String intencion: intenciones){
							
							encontrado = ExpReg.matcher(intencion);
							
							if(encontrado.find()){
								intencion=intencion.replaceFirst("^\n+","");//eliminamos los saltos de linea que tiene al inicio las intenciones
								intencion=intencion.replaceFirst("\n$","");//eliminamos los saltos de linea que tiene al final las intenciones
							}
							predicado = intencion.split("\n"); //separamos por salto de linea y obtenemos el predicado
                                                        WordSpotting.posicionArg(predicado[0],mapaDeArgumentos);
                                                        if(predicado[0].contains("("))                                             
                                                           predicado=predicado[0].split("\\(");//eliminamos los argumentos
                                                        
							mapaDeIntenciones.put(predicado[0],intencion);	
					    }
						
					return mapaDeIntenciones;
			 }

/******************************Este metodo genera un nuevo mapa, en la llave contenemos el predicando y en el valor ponemos el mapa anterior 
                          * con todos los valores para las expresiones regulares******************************************************/
			public static HashMap<String,Object> mapaPorCampos(HashMap<String,String> mapaDeIntenciones){
			       
			    	HashMap<String,Object> mapaFinal = new HashMap<String,Object>();
			    	
			        String[] DivEnDos = null;
                                // String[] ValorPorArg = null;
			        String[] DivEnCampos = null;
			        String[] separador = null;
                                String[] pre_arg = null; //variable agregada
			        String expresiones = "";
			        String idExpresion=null;
			        
			        Iterator recorre = mapaDeIntenciones.entrySet().iterator();   
			               
			        while (recorre.hasNext()) {			        	
                                            HashMap<String,Object> mapaDeArgumentos = new HashMap<String,Object>(); //variable agregada

				            Map.Entry e = (Map.Entry)recorre.next(); 
				            expresiones = (String)e.getValue();//contiene las expresiones + el predicado
				            idExpresion = (String)e.getKey(); //solo contiene los argumentos sin predicado
                                            

                                            pre_arg = expresiones.split("\n"); //dividimos el predicado de lo demas

                                            for(int i=1;i<pre_arg.length;i++){ //comenzamos en 1 ya que no trataremos con los predicados

                                                HashMap<String,String> mapaDeExpReg = new HashMap<String,String>();

                                               if(pre_arg[i].contains("=")){
                                                       DivEnDos = pre_arg[i].split("=");
                                                       DivEnCampos = DivEnDos[1].split(":");
                                                       mapaDeArgumentos.put(DivEnDos[0],mapaDeExpReg); //mapa en donde cada argumento se le asigna un expresion regular
                                                }
                                                else{
                                                           DivEnCampos = pre_arg[i].split(":");
                                                           mapaDeArgumentos.put("0",mapaDeExpReg); //mapa en donde cada argumento se le asigna un expresion regular
                                                }

					       for(String campo: DivEnCampos){
					                separador = campo.split(",");

					                if(campo.contains(","))
					                    mapaDeExpReg.put(separador[0],separador[1]);
					                else
					                    mapaDeExpReg.put(campo,"mismo");
					        }

                                                    mapaFinal.put(idExpresion,mapaDeArgumentos);
                                                 
                                            }
			        }
			       
			        return mapaFinal;
			}
                        
                        public static void posicionArg(String llave,HashMap<String,Object> mapaDeArgumentos){
        
                                String argumentos =  null;  
                                String[] posArg = null; //posicion de los argumentos
                                String[] cero = {"0"}; //variable utilizada cuando no tengamos argumentos
                                String[] predicado = null;
                                //compilamos el patron
                                Pattern patron = Pattern.compile("\\((.*)\\)");//obtenemos todo lo que se encuentre dentro de ()
                                // creamos el Matcher a partir del patron, la cadena como parametro
                                Matcher encaja = patron.matcher(llave);
                                
                                if(encaja.find()){
                                    argumentos = encaja.group(1);
                                    posArg = argumentos.split(","); //separamos los argumentos
                                    predicado=llave.split("\\(");//eliminamos los argumentos
                                    mapaDeArgumentos.put(predicado[0],posArg);//para los predicados con argumentos
                                }
                                else
                                    mapaDeArgumentos.put(llave,cero); //para los predicados sin argumentos
                                   
                        }
                        public static String ObtenerPredicado(String llave){
                                String[] predicado = null;
                                predicado=llave.split("\\(");//eliminamos los argumentos 
                                return predicado[0]; 
                        }
                        
                        public static String[] ObtenerposicionArg(String llave){
                                String argumentos =  null;  
                                String[] cero = {"0"}; //variable utilizada cuando no tengamos argumentos
                                
                                String[] argumentos_usuario;
                                Pattern patron = Pattern.compile("\\((.*)\\)");//obtenemos
                                // creamos el Matcher a partir del patron, la cadena como parametro
                                Matcher encaja = patron.matcher(llave);

                                if(encaja.find()){
                                    argumentos = encaja.group(1);
                                    argumentos_usuario = argumentos.split(",");
                                }
                                else
                                    argumentos_usuario = cero;

                                return argumentos_usuario;
                        }

//##################################################seccion de impresion de mapas para la opcioh verbose###################################33

                        public static void imprimeMapa(HashMap mapa){

                            Iterator i = mapa.entrySet().iterator();

                            while (i.hasNext()){
                                    Map.Entry e = (Map.Entry)i.next();
                                    System.out.println("llave:"+e.getKey() + "     valor:" + e.getValue());
                                    System.out.println("##################################################");

                            }
                        }

                        public static void imprimeMapaArg(HashMap mapa){

                                String[] argumentos;
                                System.out.println("MAPA DE ARGUMETOS");

                                Iterator i = mapa.entrySet().iterator();

                                while (i.hasNext()) {
                                        Map.Entry a = (Map.Entry)i.next();
                                        System.out.print("llave:"+a.getKey() + " valor:");
                                        argumentos = (String[])a.getValue();
                                        for(String arg: argumentos)
                                        System.out.print(" "+arg+" ");

                                System.out.println("");
                                System.out.println("#########################################################################################################");
                                }
                        }
}
