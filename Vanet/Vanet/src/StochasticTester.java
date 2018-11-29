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
import java.util.Set;

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
	public PrintWriter writerStep = null;
	public PrintWriter writerLog = null;
	public FileReader readerStep = null;
	public FileOutputStream outser = null;   
	public ObjectOutputStream objOutStr = null;
	public PrintWriter writerTest = null;
	boolean reinitCov  ; // do we want to reinit different coverages afeter each test
	boolean interruptCovered ; // do we want to stop execution when everything is covered
	
	//public ArrayList<SerializableTest> serializableTest;
	public LinkedList<VanetProperty> properties = new LinkedList<VanetProperty>();
	private LinkedList<VanetProperty> props;
    /** FSM model that describes a probabilistic usage automaton */
    private FsmModel fsm;
    /** Actions declared in that FSM with their probabilities */
    private HashMap<Method, Double> actionsAndProbabilities;

    private VanetConformanceMonitor vcm = null;
    String propertiesOutput="";
    /**
     * Constructor. Initializes the FSM and computes associated actionsAndProbabilities.
     * @param _fsm
     * @param writerlog four output trace generation
     */
    public StochasticTester(FsmModel _fsm,PrintWriter wl, boolean rc, boolean ic) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        writerLog=wl;
        reinitCov =rc;
        interruptCovered = ic;
    }
    
    /**
     * Constructor. Initializes the FSM and computes associated actionsAndProbabilities.
     * @param _fsm
     */
    public StochasticTester(FsmModel _fsm) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
    }
    
    public void setMonitor(VanetConformanceMonitor _vcm) {
        vcm = _vcm;
    }
    public void init() {
        properties.add(new Property1());
        properties.add(new Property2());
        properties.add(new Property3());
        properties.add(new Property4());
        properties.add(new Property5());
    }
    public String checkCoverageProperties(int stepNb, int indProp) {
    	double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int ki = 0; ki<3; ki++)
        	{
        	    for(int kj = 0; kj<2; kj++)
        	    {
        	    	System.out.print(vProp.transitionsMade[ki][kj]);
        	     if(vProp.transitionsMade[ki][kj]) 	 coverage += 1.0;
        	    // vProp.transitionsMade[i][j]=false;
        	    }
        	}vProp.transitionsMade[0][1]=true;

        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        coverage = (coverage/(30))*100;
        return " Test "+ stepNb+" finished with " +coverage +"% of properties covered "+ indProp;
       // propertiesWriter.close();
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
    
    public boolean checkProperties(PrintWriter propertiesWriter,int nbSteps, int stepNumber) {//  throws PropertyCoveredException{
        HashMap<Vehicle, ArrayList<Triple>> p1Log =properties.get(0).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p2Log =properties.get(1).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p3Log =properties.get(2).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p4Log =properties.get(3).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p5Log =properties.get(4).forEachVehicleProp;
        //propertiesWriter.println("prop1");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p1Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop2");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p2Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop3");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p3Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop4");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p4Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop5");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p4Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int i = 0; i<3; i++)
        	{
        	    for(int j = 0; j<2; j++)
        	    {
        	     if(vProp.transitionsMade[i][j]) 	 coverage += 1.0;
        	    }
        	}
        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        coverage = (coverage/(30))*100;

    	if(coverage == 100.0) {
    		//depopList(nbSteps,stepNumber);
    		//propertiesWriter.println("coverage props :" + coverage + "%" + "at step:" + stepNumber);
    		//throw new PropertyCoveredException("Properties covered, step  " + stepNumber+" terminated\n");
    		return true;
    	}
    	return false;
        //propertiesWriter.close();
    }
    
    public void depopList(int nbSteps, int stepNumber) {
        for(int i = ((VanetFSM) fsm).addedVehicles; i<nbSteps; i++) { // in order to be even for the next executions when the attributes of vehicles are read in file
        	((VanetFSM) fsm).battery.remove(0);
        	((VanetFSM) fsm).decBattery.remove(0);
        	((VanetFSM) fsm).distance.remove(0);
    	}
    }
    public void deinit(PrintWriter propertiesWriter){
        HashMap<Vehicle, ArrayList<Triple>> p1Log =properties.get(0).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p2Log =properties.get(1).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p3Log =properties.get(2).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p4Log =properties.get(3).forEachVehicleProp;
        HashMap<Vehicle, ArrayList<Triple>> p5Log =properties.get(4).forEachVehicleProp;
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p1Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop2");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p2Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop3");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p3Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop4");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p4Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        //propertiesWriter.println("prop5");
        for(Map.Entry<Vehicle,ArrayList<Triple>> vlList : p4Log.entrySet()) {
        	for(Triple line : vlList.getValue()) {
        		//propertiesWriter.println(line.state + ";"+ line.transition+";"+ line.step);
        	}
        }
        double coverage=0.0;
        for(VanetProperty vProp : properties) {
        	for(int i = 0; i<3; i++)
        	{
        	    for(int j = 0; j<2; j++)
        	    {
        	     if(vProp.transitionsMade[i][j]) 	 coverage += 1.0;
        	     //vProp.transitionsMade[i][j]=false;
        	    }
        	}vProp.transitionsMade[0][1]=true;

        	//propertiesWriter.println(Arrays.stream(vProp.transitionsMade).allMatch(s -> s.equals(vProp.transitionsMade[0])));
        }
        coverage = (coverage/(30))*100;
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
		init(); 
    	boolean propCov=false;
    	boolean interruptCovered = false; // do we want to stop execution when everything is covered
    	boolean reinitCov = false; //do we want rules coverage to be reinitialized
    	double ruleCov=0.0;
    	int indRules=0;
    	int indProp=0;
    	String strLog="";
        PrintWriter propertiesWriter = new PrintWriter("./propertiesErr.txt", "UTF-8"); 
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        ((VanetFSM) fsm).getValues();
        // for each of the resulting test cases
        for (int i=0; i < nb; i++) {
            System.out.println("== Generating test #" + i + " ==");
            strLog+= "== Generating test #" + i + " ==";
            // initialize step counter
            int j=0;
            // reset FSM exploration
            fsm.reset(true);
            // create new test case 
            MyTest currentTest = new MyTest();
            boolean b = true;
            // while limit has not been reached and there exists a next step
            MyStep newStep;

        	do {
            	strLog += "step " +j; 
	            newStep = computeNextStep();
            	b = (newStep != null);
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
							propCov=checkProperties(propertiesWriter,((VanetFSM) fsm).addedVehicles, j);
							//checkRules(propertiesWriter,((VanetFSM) fsm).addedVehicles, j,apm);
						} catch (PropertyFailedException e) {
							propertiesOutput += "Failed;" + prop.toString()+ ";" +j+"\n";
							System.out.println("Failed;" + prop.toString()+ ";" +j+"\n");
							e.printStackTrace();
							depopList(((VanetFSM) fsm).addedVehicles, j);
							ret.add(currentTest);
							return ret;
						}// catch (PropertyCoveredException e) {
							// TODO Auto-generated catch block
						if(propCov) {	
							//e.printStackTrace();
							propCov=true;
							indProp= indProp==0? j: indProp;
							if(ruleCov==100.0) {
								depopList(((VanetFSM) fsm).addedVehicles, j);
								propertiesWriter.print("properties covered at step " + j+ " and rules at index "+ indRules);
								if(interruptCovered) {
									break; //break the actual test when everything is covered
								}
								if(interruptCovered) {
	        						break; //break the actual test when everything is covered
	        					}
							}
						}
	                }
                }
                //checkRules(propertiesWriter,((VanetFSM) fsm).addedVehicles, j,apm);
            }while (j < length && b);       		
            // add computed test case to the result
            ret.add(currentTest);
            propertiesWriter.print(propertiesOutput);
            propertiesWriter.print(checkCoverageProperties(i,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
            strLog+=((VanetFSM) fsm).getSUT().writerLog;
            ((VanetFSM) fsm).getSUT().setStringWriterLog("");
            if(reinitCov) {
            	deinit(propertiesWriter);
	            for(Rule r :apm.rules) {
						r.coverage=0.0;
				}
            }
        }
		vcm.printReport();
		strLog+=((VanetFSM) fsm).getSUT().writerLog;
		writerLog.print(strLog);
        writerLog.print(strLog); 
		propertiesWriter.close();
        return ret;
    }
    
    //retrieve the list of input steps and notify them to the SUT
    public ArrayList<MyTest> retrieve(AdaptationPolicyModel apm, ArrayList<SerializableTest> serializableTest) throws NumberFormatException, IOException {
    	boolean propCov=false;
    	double ruleCov=0.0;
    	int indRules=0;
    	int indProp=0;
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        int j=0,k=0;
        boolean b;
        MyStep newStep;
		PrintWriter propertiesWriter = new PrintWriter("./propertiesErr.txt", "UTF-8"); 
        ((VanetFSM) fsm).getValues();
        // for each of the resulting test cases
        init();
        for(SerializableTest test : serializableTest) {
			k++;
	        // reset FSM exploration
            fsm.reset(true);
	        MyTest currentTest = new MyTest();
			for(SerializableStep step : test.steps) {
				j++;
				System.out.println("step :"+step.toString());
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
                if(newStep.meth.getName() == "tick") {//if not tick we do not do reconfiguration
	                for(VanetProperty prop : properties) {
		                try {
							prop.match(((VanetFSM) fsm).getSUT());
							propCov=checkProperties(propertiesWriter,((VanetFSM) fsm).addedVehicles, j);
						} catch (PropertyFailedException e) {
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
	                //ticktrigger here
	                ((VanetFSM) fsm).getSUT().tickTrigger();
                }
			}
			j=0;
			propertiesWriter.print(propertiesOutput);
	        propertiesWriter.print(checkCoverageProperties(k,indProp) + "and " + ruleCov + "% of rules " + indRules +"\n");
	        if(reinitCov) {
            	deinit(propertiesWriter);
	            for(Rule r :apm.rules) {
						r.coverage=0.0;
				}
            }
			ret.add(currentTest);
		}
        String strLog=((VanetFSM) fsm).getSUT().writerLog;
        writerLog.print(strLog); 
		propertiesWriter.print(propertiesOutput);
		propertiesWriter.close();
        vcm.printReport();
		return ret;
    }

    public void printInputTest(ArrayList<SerializableTest> testList) {
    	writerTest.println("begin");
    	for(SerializableTest test : testList) {
    		for(SerializableStep step : test.steps) {
    			writerTest.println(" step : "+step);
    		}
    	}
    }
   
    public MyStep computeInputTest(SerializableStep step){
    	 HashMap<Method, Double> actionsReady = getActivableActions(fsm);
		 for (Method act : actionsReady.keySet()) {
			 if(step.getMethName().contains(act.getName()) ) {
				 try {
					act.invoke(fsm);
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
				 MyStep myStep = new MyStep(meth, step.getInstance(),step.getParams());
				 return myStep;
			 }
			 
    	 }
    	 return null;
    		 
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
                        Object[] tab = (Object[]) act.invoke(fsm);
                        Object[] params = new Object[tab.length - 1];
                        for (int i=1; i < tab.length; i++) {
                            params[i-1] = tab[i];
                        }
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
            Annotation[] ta = m.getDeclaredAnnotations();
            for (int i=0; i < ta.length; i++) {
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
        return activableOnes;
    }
}

/**
 * Simple class encapsulating a test as a sequence (ArrayList) of String representing action names. 
 */
class MyTest implements Iterable<MyStep> ,Serializable{

    ArrayList<MyStep> steps;
    public MyTest() {
        steps = new ArrayList<MyStep>();
    }

    public void append(MyStep step) {
        steps.add(step);
    }

    public int size() {
        return steps.size();
    }

    public MyStep getStepAt(int i) {
        return steps.get(i);
    }

    public String toString() {
        return steps.toString();
    }

    public Iterator<MyStep> iterator() {
        return steps.iterator();
    }

}

class MyStep implements Serializable{
    Method meth;
    Object instance;
    Object[] params;

    public MyStep(Method _m, Object _i, Object[] _p) {
        meth = _m;
        instance = _i;
        params = _p;
    }
    public Method getMeth() {
    	return meth;
    }
    public Object getInstance() {
    	return instance;
    }
    public Object[] getParams() {
    	return params;
    }
   
    public void execute() throws InvocationTargetException, IllegalAccessException {
        meth.invoke(instance, params);
    }

    public String toString() {
        String ret = /*instance + "." +*/ meth.getName() + "(";
        for (int i=0; i < params.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += params[i].toString();
        }
        return ret + ")";
    }
}