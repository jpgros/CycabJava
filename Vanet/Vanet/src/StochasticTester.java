import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 20/03/2018
 * Time: 09:26
 */
public class StochasticTester implements Serializable{
	public PrintWriter writer = null;
	public PrintWriter writerStep = null;
	public FileReader readerStep = null;
	public FileOutputStream outser = null;   
	public ObjectOutputStream objOutStr = null;
	public PrintWriter writerTest = null;
	public ArrayList<SerializableTest> serializableTest;
    /** FSM model that describes a probabilistic usage automaton */
    private FsmModel fsm;
    /** Actions declared in that FSM with their probabilities */
    private HashMap<Method, Double> actionsAndProbabilities;

    private VanetConformanceMonitor vcm = null;

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
    
    /**
     * Constructor. Initializes the FSM  and writer, computes associated actionsAndProbabilities.
     * @param _fsm
     */
    
    public StochasticTester(FsmModel _fsm, PrintWriter w) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        writer=w;
        //writerStep.println("Actions & Probabilities :\n" + actionsAndProbabilities);

    }
    
    
    /**
     * Constructor. Initializes the FSM  and writer, computes associated actionsAndProbabilities.
     * @param _fsm
     */
    public StochasticTester(FsmModel _fsm, PrintWriter w, ArrayList<SerializableTest> st) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        writer=w;
        serializableTest=st;
        writer.println("Actions & Probabilities :\n" + actionsAndProbabilities);

    }
        
    /**
     * Constructor. Initializes the FSM  and writer, computes according to the input event file.
     * @param _fsm
     */
    public StochasticTester(FsmModel _fsm, PrintWriter w, FileReader rs) {
        fsm = _fsm;
        actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);
        fsm.reset(true);
        writer=w;
        readerStep = rs;
    }
    public void setMonitor(VanetConformanceMonitor _vcm) {
        vcm = _vcm;
    }

    /**
     * Generates a set of test cases (object MyTest).
     * @param nb number of test cases to generate
     * @param length maximal size of the test cases
     * @return the set of test cases
     */
    public ArrayList<MyTest> generate(int nb, int length) {
    	//BufferedReader inStream = new BufferedReader(readerStep);
        String inString;
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        // for each of the resulting test cases
        for (int i=0; i < nb; i++) {
            System.out.println("== Generating test #" + i + " ==");
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
            	System.out.println("Step : " +j);
            	writer.println("Step : "+j);
	            newStep = computeNextStep();
            	b = (newStep != null);
                //writerStep.println("My step : " + newStep.instance + ";" + newStep.meth + ";" + newStep.params);
                if (b) {
                    currentTest.append(newStep);
                    j++;
                    if (vcm != null) { 
                        vcm.notify(newStep, ((VanetFSM) fsm).getSUT());
                    }
                }
                //ticktrigger here
                ((VanetFSM) fsm).getSUT().tickTrigger();
            }
            while (j < length && b);       		 
            // add computed test case to the result
            ret.add(currentTest);
        }
        return ret;
    }
    
    public ArrayList<MyTest> retrieve() {
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        String inString;
        int j=0,k=0;
        boolean b;
        MyStep newStep;
        // for each of the resulting test cases
		for(SerializableTest test : serializableTest) {
			System.out.println("test:" +test);
			k++;
	        // reset FSM exploration
            fsm.reset(true);
	        MyTest currentTest = new MyTest();
			for(SerializableStep step : test.steps) {
				System.out.print("step :" +step);
				j++;
				
				newStep = computeInputTest(step); 
				System.out.println("Test : " + k +" Step : " +j + " " + newStep);
            	writer.println("Step : "+j + " " + newStep);
				b = (newStep != null);
                //writerStep.println("My step : " + newStep.instance + ";" + newStep.meth + ";" + newStep.params);
                if (b) {
                    currentTest.append(newStep);
                    if (vcm != null) { 
                        vcm.notify(newStep, ((VanetFSM) fsm).getSUT());
                    }
                }
                //ticktrigger here
                ((VanetFSM) fsm).getSUT().tickTrigger();
			}
			j=0;
			System.out.println("");
			ret.add(currentTest);
		}
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
    
//    private MyStep remakeNextStep(Method methKey) {
//            String ret = null;
//            double sum = 0;
//            double rand = 0;
//            HashMap<Method, Double> actionsReady = getActivableActions(fsm);
//            Method act = actionsReady.g(methKey);
//        for(Map.Entry<Method, Double>  keySet : actionsReady.entrySet()) {
//        	double tot=keySet.getValue();
//        	Method toto=keySet.getKey();
//        	Method act1 = actionsReady.get(keySet.getKey());
//        	double act2 = actionsReady.get(keySet.getValue());
//        }
//        
//    	return null;
//    }

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