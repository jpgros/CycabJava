package SUT;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.security.auth.callback.TextInputCallback;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 20/03/2018
 * Time: 09:26
 */
public class StochasticTester implements Serializable{
	//public PrintWriter writer = null;
	//public PrintWriter writerLog = null;
	public FileReader readerStep = null;
	public PrintWriter writerErr = null;
	public FileOutputStream outser = null;   
	public ObjectOutputStream objOutStr = null;
	boolean reinitCov  ; // do we want to reinit different coverages afeter each test
	boolean interruptCovered ; // do we want to stop execution when everything is covered
	Mutant mutant;
	
	//public ArrayList<SerializableTest> serializableTest;
	public LinkedList<VanetProperty> properties = new LinkedList<VanetProperty>();
	private LinkedList<VanetProperty> props;
    /** FSM model that describes a probabilistic usage automaton */
    private FsmModel fsm;
    /** Actions declared in that FSM with their probabilities */
    private HashMap<Method, Double> actionsAndProbabilities;

    private VanetConformanceMonitor vcm = null;
    String propertiesOutput="";
    final ArrayList<UUID> iD = new ArrayList<UUID>();
    final ArrayList<Double> battery = new ArrayList<Double>();
    final ArrayList<Double> decBattery = new ArrayList<Double>();
    final ArrayList<Double> distance = new ArrayList<Double>();
    /**
     * Constructor. Initializes the FSM and computes associated actionsAndProbabilities.
     * @param _fsm
     * @param writerlog four output trace generation
     */
    public StochasticTester(FsmModel _fsm, LogPrinter we,boolean rc, boolean ic, Mutant m) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
       // System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        //writer=w;
        //writerLog=wl;
        reinitCov =rc;
        interruptCovered = ic;
        mutant =m;
        try {
			getTestValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        init();
    }
    
    /**
     * Constructor. Initializes the FSM and computes associated actionsAndProbabilities.
     * @param _fsm
     */
    public StochasticTester(FsmModel _fsm) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
       // System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        try {
			getTestValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        init();
    }
    
    public void setMonitor(VanetConformanceMonitor _vcm) {
        vcm = _vcm;
    }
    /**
     * Adds the properties to keep correct of the SUT
     */
    public void init() {
        properties.add(new Property1(mutant));
        properties.add(new Property2(mutant));
        properties.add(new Property3(mutant));
        properties.add(new Property4(mutant));
        properties.add(new Property5(mutant));
    }
    
    /**
     * Generates a set of test cases (object MyTest).
    * @return a double with the coverage information of all properties
     */
    public double coverageProperties() {
    	double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int ki = 0; ki<3; ki++)
        	{
        	    for(int kj = 0; kj<2; kj++)
        	    {
        	    	//System.out.print(vProp.transitionsMade[ki][kj]);
        	     if(vProp.transitionsMade[ki][kj]) 	 coverage += 1.0;
        	    // vProp.transitionsMade[i][j]=false;
        	    }
        	}vProp.transitionsMade[0][1]=true;

        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        coverage = (coverage/(30))*100;
        return coverage;
    }
    public String checkCoverageProperties(int testNb, int stepNb) {
    	double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int i = 0; i<3; i++)
        	{
        	    for(int j = 0; j<2; j++)
        	    {
        	    	//System.out.print(vProp.transitionsMade[ki][kj]);
        	     if(vProp.transitionsMade[i][j]) 	 coverage += 1.0;
        	    // vProp.transitionsMade[i][j]=false;
        	    }
        	}vProp.transitionsMade[0][1]=true;

        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        System.out.println("nb prop " +properties.size());
        coverage = (coverage/(30))*100; //magic numbers /!\
        return " Test "+ testNb+" finished with " +coverage +"% of properties covered "+ stepNb;
       // propertiesWriter.close();
    }
    
    public boolean checkCoverageProperties() {//  throws PropertyCoveredException{
        double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int i = 0; i<3; i++){
        	    for(int j = 0; j<2; j++){
        	    	if(vProp.transitionsMade[i][j]) 	 coverage += 1.0;
        	    }
        	}
        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        coverage = (coverage/(30))*100;
    	if(coverage == 100.0) {
    		return true;
    	}
    	return false;
    }
    
    public void checkCoverageRules(PrintWriter propertiesWriter,int nbSteps, int stepNumber,AdaptationPolicyModel apm)throws RuleCoveredException {
    	double cpt=0;
		for(Rule r : apm.rules) {
			cpt+=r.coverage;
			propertiesWriter.println("cov "+ r.coverage);
			r.coverage=0.0;
		}
		cpt = (cpt/apm.rules.size())*100.0;
		propertiesWriter.println("cpt " + cpt);
		if(cpt == 100.0) {
    		depopList(nbSteps,stepNumber);
    		propertiesWriter.println("coverage rules :" + cpt + "%" + "at step:" + stepNumber);
    		throw new RuleCoveredException("Rules covered, step  " + stepNumber+" terminated\n");
    	}
		cpt = 0;
    }
    
    /**
     * When a test is terminated, we need to be syncronized between reference test and input file giving the attributes of vehicles
     * So we remove from the list the vehicle than have not been created
     * @param nbSteps number of steps already done
     * @param stepNumber number of steps done by the reference execution 
     */
    public void depopList(int nbSteps, int stepNumber) {
//        for(int i = ((VanetFSM) fsm).addedVehicles; i<nbSteps; i++) { // in order to be even for the next executions when the attributes of vehicles are read in file
//        	((VanetFSM) fsm).battery.remove(0);
//        	((VanetFSM) fsm).decBattery.remove(0);
//        	((VanetFSM) fsm).distance.remove(0);
//    	}
    }
    /**
     * Reinitializes property and rules coverage to 0%
     */
    public void resetCov(AdaptationPolicyModel apm){ // /!\ magic numbers 
    	// /!\ maybe property coverage was written here
        double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int i = 0; i<3; i++){
        	    for(int j = 0; j<2; j++){
        	    	vProp.transitionsMade[i][j]=false;
        	    }
        	}vProp.transitionsMade[0][1]=true;
        }
        coverage = (coverage/(30))*100;
        for(Rule r :apm.rules) {
				r.coverage=0.0;
		}
        //propertiesWriter.println("coverage props :" + coverage + "%");
        //propertiesWriter.close();
    }
    /**
     * Generates a set of test cases (object MyTest).
     * @param nb number of test cases to generate
     * @param length maximal size of the test cases
     * @return the set of test cases
     * @throws IOException 
     * @throws NumberFormatException 
     */
    
    public ArrayList<MyTest> generate(int nb, int length,AdaptationPolicyModel apm) throws NumberFormatException, IOException {
    	//BufferedReader inStream = new BufferedReader(readerStep);
		int nbCatchedError=0;
		String conso="";
		String time="";
    	boolean propCov=false;
    	boolean catched = false;
    	boolean interruptCovered = false; // do we want to stop execution when everything is covered
    	boolean reinitCov = true; //do we want rules coverage to be reinitialized
    	boolean checkCoverage =false; // do we want to check coverage of rules
    	double ruleCov=0.0;
    	int indRules=0;
    	int j=0;
    	int indProp=0;
    	String x="";
        PrintWriter propertiesWriterFile = new PrintWriter("./propertiesErr.txt", "UTF-8");
        PrintWriter writerConso = new PrintWriter("./conso.csv", "UTF-8");
        LogPrinter propertiesWriter = new LogPrinter(propertiesWriterFile, LogLevel.ERROR, LogLevel.ERROR);
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        // for each of the resulting test cases
        long startTime=System.currentTimeMillis();
        long estimTime=0;
        Double[] array = {-3.0,-1.0, 0.0, 1.0, 3.0};
        ArrayList<Double> coeffList = new ArrayList<Double>(Arrays.asList(array));
        for(int cptwriter=0 ; cptwriter<1; cptwriter++) {
	        for(int iii=0; iii<1; iii++) {
        	//for(Double coeff : coeffList) {
	        	//conso+= "k="+coeff+";";
	        	//time+= "k="+coeff+";";
	        	//System.out.println("coeff "+ coeff);
		        for (int i=0; i < nb; i++) {
		        	ruleCov=0.0;
		        	propCov=false;
		        	((VanetFSM) fsm).getValues(this.iD,this.battery, this.decBattery,this.distance, length);
		            ((VanetFSM) fsm).getSUT().reinit();
		        	x="== Generating test #" + i + " == rule cov ";
		        	((VanetFSM) fsm).getSUT().addStringWriter(x);
		            System.out.println(x);
		            x="";
		            // initialize step counter
		            j=0;
		            // reset FSM exploration
		            fsm.reset(true);
		            // create new test case 
		            MyTest currentTest = new MyTest();
		            boolean b = true;
		            // while limit has not been reached and there exists a next step
		            MyStep newStep;
	        		estimTime= System.currentTimeMillis()-startTime;
	        		//System.out.println("loop time 1 " + estimTime);
		            for(int cptK=0;cptK<((VanetFSM) fsm).getSUT().k.length;cptK++) {
		        		((VanetFSM) fsm).getSUT().k[cptK] = 0.0;//i== cptK ? coeff :0.0;
		        	}
		        	do {	
		        		//System.out.println("tick");
		        		x="step " +j;
		            	((VanetFSM) fsm).getSUT().addStringWriter(x);
			            newStep = computeNextStep();
			            estimTime= System.currentTimeMillis()-startTime;
		        		//System.out.println("loop time 2 " + estimTime);         
		            	b = (newStep != null);
		            	try {
			                if (b) {
			                    currentTest.append(newStep);
			                    j++;
			                    if (vcm != null) { 
			                        ruleCov =vcm.notify(newStep, ((VanetFSM) fsm).getSUT());
			                        if(ruleCov==100.0) {
			                    		indRules= indRules==0? j : indRules;
			        					if(propCov) {
				        					depopList(((VanetFSM) fsm).addedVehicles, j);
				        					propertiesWriter.println("coverage rules :"  + " 100%" + "at step:" + j+ "and properties at index " + indProp) ;
				        					if(interruptCovered) {
				        						break; //break the actual test when everything is covered
				        					}
			        					}
			        				}
			                    }
			                    // TODO :if newstep !=tick
			                }
			                if(newStep.meth.getName() == "tick") {//if not tick we do not do reconfiguration
				                //ticktrigger here
				                ((VanetFSM) fsm).getSUT().tickTrigger();
				                for(VanetProperty prop : properties) {
					                try {
										prop.match(((VanetFSM) fsm).getSUT());
										propCov=checkCoverageProperties();
										//checkRules(propertiesWriter,((VanetFSM) fsm).addedVehicles, j,apm);
									} catch (PropertyFailedException e) {
										propertiesOutput += "Failed;" + prop.toString()+ ";" +j+"\n";
										System.out.println("Failed;" + prop.toString()+ ";" +j+"\n");
										e.printStackTrace();
										depopList(((VanetFSM) fsm).addedVehicles, j);
										//ret.add(currentTest);
										conso+="0;";
										time+="0";
										catched=true;
										nbCatchedError++;
										if(interruptCovered) return ret;
									}// catch (PropertyCoveredException e) {
										// TODO Auto-generated catch block
									if(propCov) {	
										//e.printStackTrace();
										
										propCov=true;
										indProp= indProp==0? j: indProp;
										//System.out.println("rules cov : "+ruleCov);
										if(ruleCov==100.0) {
											//System.out.println("props and rules covered");
											depopList(((VanetFSM) fsm).addedVehicles, j);
											propertiesWriter.print(" catched errors "+ nbCatchedError +" properties covered at step " + j+ " and rules at index "+ indRules);
											if(interruptCovered) {
												break; //break the actual test when everything is covered
											}
										}
									}
				                }
			                }
		            	}
		                catch (Exception e) {
		                	System.out.println("step FAIL value "+ newStep );
		                	System.out.println(" methods possible "+ actionsAndProbabilities);
		                	((VanetFSM) fsm).getSUT().consolePrint();
		                	
		                	System.exit(1);
							// TODO: handle exception
						}
		                estimTime= System.currentTimeMillis()-startTime;
		        		//System.out.println("loop time 4 " + estimTime);
		                //checkRules(propertiesWriter,((VanetFSM) fsm).addedVehicles, j,apm);
		            }while (j < length && b);       		
		            // add computed test case to the result
		        	//if(ruleCov==100.0 && propCov) {
		        	currentTest.score = nbCatchedError > 0 ? nbCatchedError*-1 : coverageProperties()+ ruleCov;
		        	System.out.println("\n consummed energy : "+((VanetFSM) fsm).getSUT().getGlobalConso());
		        	if(!catched) ret.add(currentTest); // do not add test if contains property error
		        	//}
		            propertiesWriter.print(propertiesOutput);
		            propertiesWriter.print(checkCoverageProperties(i,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
		            System.out.println(checkCoverageProperties(i,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
		            System.out.println("test score " +currentTest.score);
		            if(reinitCov) {
		            	System.out.println("here reinit");
		            	resetCov(apm);
		            }
		            if(!catched)time+=((VanetFSM) fsm).getSUT().getGlobalTimePLatooned()+";"; //conso+=((VanetFSM) fsm).getSUT().getGlobalConso()+";";
		            catched=false;		            
		        }
		        conso += "\n";
		        time +="\n";
	        }
	        writerConso.println();
        }
		vcm.printReport();
		propertiesWriter.close();
		writerConso.print(time);
		writerConso.close();
		System.out.println("quitted");
        return ret;
    }
    
    //retrieve the list of input steps and notify them to the SUT
    public String retrieve(AdaptationPolicyModel apm,SerializableTest test,int sourcePA,int sourceCoeff) throws NumberFormatException, IOException {
    	long startTime = System.currentTimeMillis();
    	long estimatedTime=0;
		int nbCatchedError=0;
    	int cpt=0;
    	boolean propCov=false;
    	double ruleCov=0.0;
    	int indRules=0;
    	int indProp=0;
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        //((VanetFSM) fsm).getValues(this.iD, this.battery, this.decBattery,this.distance, serializableTest.get(0).size());
        //System.out.println("serialisable test size " + serializableTest.get(0).size());
        int j=0,k=0;
        boolean b;
        String conso="";
        MyStep newStep; 
		PrintWriter propertiesWriterFile = new PrintWriter("./propertiesErr.txt", "UTF-8");
        LogPrinter propertiesWriter = new LogPrinter(propertiesWriterFile, LogLevel.VERBOSE, LogLevel.ERROR);
//        estimatedTime = (System.currentTimeMillis() - estimatedTime - startTime);
//        System.out.println("elapsed time header" + estimatedTime + " miliseconds " );
         System.out.println("retrieve fsm ");
		 // for each of the resulting test cases
        ((VanetFSM) fsm).getSUT().reinit();
       
        //for(SerializableTest test : serializableTest) {
        	System.out.println("new Test ");
        	for(int cptK=0;cptK<((VanetFSM) fsm).getSUT().k.length;cptK++) {
        		((VanetFSM) fsm).getSUT().k[cptK] = 0; // ((VanetFSM) fsm).getSUT().k[cptK]== sourcePA ? sourceCoeff :0;
        	}
			k++;
	        // reset FSM exploration
            fsm.reset(true);
	        MyTest currentTest = new MyTest();
			for(SerializableStep step : test.steps) {				
				j++;
				//if(!step.meth.equals("tick()"))System.out.println("step :"+step.meth);
				newStep = computeInputTest(step);
				b = (newStep != null);
                if (b) {
                    currentTest.append(newStep);
                    if (vcm != null) { 
                    	 ruleCov =vcm.notify(newStep, ((VanetFSM) fsm).getSUT());
                         if(ruleCov==100.0) {
                    		indRules= indRules==0? j : indRules;
                    		if(propCov) {
	        					depopList(((VanetFSM) fsm).addedVehicles, j);
	        		    		propertiesWriter.println("coverage rules :"  + " 100%" + "at step:" + j+ "and properties at index " + indProp) ;
	        					if(interruptCovered) break;
                    		}
        				}
                    }
                }
                
                try {
	                if(newStep.meth.getName() == "tick") {//if not tick we do not do reconfiguration
	                	for(VanetProperty prop : properties) {
//	                		estimatedTime = (System.currentTimeMillis()  - startTime);
//	                		System.out.println("elapsed time 1 " + estimatedTime + "miliseconds properties length " + properties.size());
			                try {
								prop.match(((VanetFSM) fsm).getSUT());
								propCov=checkCoverageProperties();
							} catch (PropertyFailedException e) {
								nbCatchedError++;
								propertiesOutput += "Failed;" + prop.toString()+ ";" +j+"\n";
								System.out.println("Failed;" + prop.toString()+ ";" +j+"\n");
								// TODO Auto-generated catch block
								e.printStackTrace();
								ret.add(currentTest);
								if(interruptCovered) break;
							}
							if(propCov) {	
								//e.printStackTrace();
								propCov=true;
								indProp= indProp==0? j: indProp;
								if(ruleCov==100.0) {
									propertiesWriter.print("properties covered at step " + j+ " and rules at index "+ indRules);
									depopList(((VanetFSM) fsm).addedVehicles, j);
									if(interruptCovered) break;
								}
							}
							
	                }
	                	//estimatedTime = (System.currentTimeMillis() - startTime);
//	        	        System.out.println("elapsed time 2 " + estimatedTime + " miliseconds " );
	                	
		                ((VanetFSM) fsm).getSUT().tickTrigger();
	                }
                }
                catch (Exception e) {
                	System.out.println("newstep null "+ newStep + " step " + step );
                	System.out.println("step number "+ cpt);
                	System.out.println("test size "+ test.size());
                	System.out.println(" methods possible "+ actionsAndProbabilities);
                	((VanetFSM) fsm).getSUT().consolePrint();
                	
                	System.exit(1);
					// TODO: handle exception
				}	
                cpt++;
			}
			currentTest.score = nbCatchedError > 0 ? nbCatchedError*-1 : coverageProperties()+ ruleCov;
			j=0;
			propertiesWriter.print(propertiesOutput);
	        propertiesWriter.print(checkCoverageProperties(k,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
	        System.out.println(checkCoverageProperties(k,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
	        System.out.println("test score " +currentTest.score);
	        if(reinitCov) {
            	resetCov(apm);
            }
	        System.out.println("\n consummed energy : "+((VanetFSM) fsm).getSUT().getGlobalConso());
	        conso+=((VanetFSM) fsm).getSUT().getGlobalConso()+";";
			ret.add(currentTest);
//			estimatedTime = (System.currentTimeMillis() - estimatedTime- startTime);
//	        System.out.println("elapsed time end " + estimatedTime + " miliseconds " );
			
		//} //end for serializable test
        
       // writerLog.print(strLog); 
		propertiesWriter.print(propertiesOutput);
		propertiesWriter.close();
        vcm.printReport();
        System.out.println("size test "+ cpt);
		return conso;
    }
    private MyStep prepareInvoke(Method act) {//SerializableStep step, 
    	ArrayList<Object> paramList = new ArrayList<Object>(); 
    	try {
				//Object[] tabObj=step.getParams();
//				ArrayList<Object> paramList = new ArrayList<Object>();
//				System.out.println("begin obj "+ act.getName());
//				for(Object obj : tabObj) {
//					paramList.add(obj);
//					System.out.println("obj " + obj);
//				}
				act.invoke(fsm, paramList);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 Method meth = act;
			 MyStep myStep = new MyStep(meth, fsm, null);
			 return myStep;
    }
    public MyStep computeInputTest(SerializableStep step){
 //   	System.out.println("step before "+ step.getMethNameWithParams());
    	 HashMap<Method, Double> actionsReady = getActivableActions(fsm);
//    	 System.out.println("into");
    	for (Method act : actionsReady.keySet()) {
			 if(step.getMethNameWithParams().contains(act.getName()) ) {
				 try {
						Object[] tabObj=step.getParams();
						ArrayList<Object> paramList = new ArrayList<Object>();
						for(Object obj : tabObj) {
							//System.out.print(obj+" ");
							paramList.add(obj);
						}
						
						act.invoke(fsm, paramList);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
//						System.out.println("act " + act.getName());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 Method meth = act;
//					 System.out.println("meth params inside "+ step.getMethNameWithParams());
					 MyStep myStep = new MyStep(meth, step.getInstance(),step.getParams());
					 return myStep;
				 //return prepareInvoke(step, act);
			 }			 
    	 }
		 //for (Method act : actionsReady.keySet()) {
			 //System.out.println("meth name "+ act.getName());
		 //}
//		 System.out.println("vehicles ");
//		 System.out.println("all vehicle on road " + ((VanetFSM) fsm).getSUT().nbVehiclesOnRoad());
//		 for(Vehicle vl : ((VanetFSM) fsm).getSUT().allVehicles) {
//			 System.out.println("my platoon " +vl.myPlatoon);
//		 }
    	
    	//for genetic child if event was not available we replace by tick or another in case of mutation
    	if( Math.random() <= 0.5 ) {
    		Object[] meths = actionsReady.keySet().toArray();
    		Method act = (Method)meths[new Random().nextInt(meths.length)];
//    		System.out.println("mutation " + act.getName() +" selected");
//    		System.out.println("meth params "+ step.getMethNameWithParams());
    		return prepareInvoke(act);
    	}
    	else {
//    		System.out.println("external event not available taking tick instead");
    		for (Method act : actionsReady.keySet()) {
    			if(act.getName()=="tick") {
    				return prepareInvoke(act);
    			}
    		}
    		return null; //should not happen
    	}    		 
    }
    
    /**
     * Computes the next step, by considering the activable actionsAndProbabilities and their associated probabilities.
     *  First probability to match is used.
     *  Side-effect: modifies the FSM. 
     * @return a String providing the name of the action that was invoked. 
     */
    private MyStep computeNextStep() {
        String ret = null;
        double sum = 0;
        double rand = 0;
        long startTime = System.currentTimeMillis();
        HashMap<Method, Double> actionsReady = getActivableActions(fsm);       
        if (actionsReady.isEmpty()) {
            return null;
        }
        do {
        	sum = 0;
            rand = Math.random();
            for (Method act : actionsReady.keySet()) {
                sum += actionsReady.get(act);
                if (rand <= sum) {
                    try {
                    	ArrayList<Object> emptylist= new ArrayList<Object>();                    	
                        Object[] tab = (Object[]) act.invoke(fsm, emptylist);
                        //long estimatedTime = (System.currentTimeMillis() - startTime);
                        //System.out.println("compute elapsed time 2  " + estimatedTime + "miliseconds" + "method name " + act.getName());
                        Object[] params = new Object[tab.length - 1];
                        for (int i=1; i < tab.length; i++) {
                            params[i-1] = tab[i];
                        }   
                        //System.out.println(act.getName()+ " " + tab.length);
                        return new MyStep(act, tab[0], params);
                    } catch (IllegalAccessException e) {
                        System.err.println("Illegal access to " + act.getName());
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        System.err.println("Exception on target invocation on " + act.getName());
                        System.err.println("Shouldn't have happened");
                        e.getCause().printStackTrace(System.err);
                    }
                }
            }
        }
        while (rand > sum);
        return null;
    }
    
    /**
     * Utility function: inspects the FSM and retrieves the Methods representing the actions with their associated probabilities.
     * @param _fsm  the FSM to explore.
     * @return a map of actions contained in the FSM associated to their probabilities.
     */
    private HashMap<Method, Double> getActionTaggedMethods(FsmModel _fsm) {
        HashMap<Method, Double> ret = new HashMap<Method, Double>();
        for (Method m : _fsm.getClass().getDeclaredMethods()) {
        	//System.out.println("method " + m);
            Annotation[] ta = m.getDeclaredAnnotations();
            
            for (int i=0; i < ta.length; i++) {
            	//System.out.println(ta[i] instanceof Action);
                if (ta[i] instanceof Action) {
                    Method actProba = null;
                    double proba = 0;
                    try {
                        actProba = fsm.getClass().getDeclaredMethod(m.getName() + "Proba");
                        proba = (Double) actProba.invoke(fsm);
                    } catch (NoSuchMethodException e) {
                        System.err.println("Warning: method " + m.getName() + " has no probability. Considered 0.");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.err.println("Illegal access to " + m.getName() + "Proba");
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        System.err.println("Exception on target invocation on " + m.getName() + "Proba");
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
                    }
                    ret.put(m, proba);
                }
            }
        }
        //System.out.println(ret);
    	//System.exit(1);

        return ret;
    }

    /**
     * Filters, among all possible actionsAndProbabilities, those activable from the current FSM state.
     * @param _fsm  the FSM
     * @return a set of activable action Methods;
     */
    private HashMap<Method, Double> getActivableActions(FsmModel _fsm) {
        // computation of activable methods
        HashMap<Method, Double> activableOnes = new HashMap<Method, Double>();
        double sum = 0;
        for (Method act : this.actionsAndProbabilities.keySet()) {
            try {
                Method actGuard = fsm.getClass().getDeclaredMethod(act.getName() + "Guard");
                //System.out.println("actguard " + actGuard);
                if ((Boolean) actGuard.invoke(fsm)) {
                    Method actProba = fsm.getClass().getDeclaredMethod(act.getName() + "Proba");
                    double proba = (Double) actProba.invoke(fsm);
                    activableOnes.put(act, proba);
                    sum += proba; // checksum
                }
            } catch (NoSuchMethodException e) {
                System.err.println("Warning: method " + act.getName() + " is not guarded.");
                System.err.println("Will be ignored. ");
            } catch (IllegalAccessException e) {
                System.err.println("Illegal access to " + act.getName());
                System.err.println("Shouldn't have happened");
                // e.printStackTrace(System.err);
            } catch (InvocationTargetException e) {
                System.err.println("Exception on target invocation on " + act.getName());
                System.err.println("Shouldn't have happened");
                // e.printStackTrace(System.err);
            }
            if (sum > 1) {
                System.err.println("Warning: sum of probabilities of activable actionsAndProbabilities is > 1 !\n" + activableOnes);
            }
        }
        //System.out.println("activable ones "+ activableOnes);
        return activableOnes;
    }
    public void getTestValues() throws IOException {
    	FileReader vehicleReader = new FileReader("./outputVals.txt");
        BufferedReader br = new BufferedReader(vehicleReader);
        String sCurrentLine;
        sCurrentLine = br.readLine();
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Battery")) {
    		iD.add(UUID.fromString(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("DecAuto")) {
    		battery.add(Double.parseDouble(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Distance")) {
    		decBattery.add(Double.parseDouble(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        while ((sCurrentLine = br.readLine()) != null){
        	distance.add(Double.parseDouble(sCurrentLine));
        }
        br.close();
    }
}
