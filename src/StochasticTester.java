import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 20/03/2018
 * Time: 09:26
 */
public class StochasticTester {

    /** FSM model that describes a probabilistic usage automaton */
    private FsmModel fsm;
    /** Actions declared in that FSM with their probabilities */
    private HashMap<Method, Double> actionsAndProbabilities;

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
     * Generates a set of test cases (object MyTest).
     * @param nb number of test cases to generate
     * @param length maximal size of the test cases
     * @return the set of test cases
     */
    public ArrayList<MyTest> generate(int nb, int length) {
        ArrayList<MyTest> ret = new ArrayList<MyTest>();
        // for each of the resulting test cases
        for (int i=0; i < nb; i++) {
            // initialize step counter
            int j=0;
            // reset FSM exploration
            fsm.reset(true);
            // create new test case 
            MyTest currentTest = new MyTest();
            boolean b = true;
            // while limit has not been reached and there exists a next step
            do {
                // compute next step
                String newStep = computeNextStep();
                b = (newStep != null);
                if (b) {
                    currentTest.append(newStep);
                    j++;
                }
            }
            while (j < length && b);
            // add computed test case to the result
            ret.add(currentTest);
        }
        return ret;
    }

    
    /**
     * Computes the next step, by considering the activable actionsAndProbabilities and their associated probabilities.
     *  First probability to match is used.
     *  Side-effect: modifies the FSM. 
     * @return a String providing the name of the action that was invoked. 
     */
    private String computeNextStep() {
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
                    String name = act.getName();
                    try {
                        act.invoke(fsm);
                        return act.getName();
                    } catch (IllegalAccessException e) {
                        System.err.println("Illegal access to " + act.getName());
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        System.err.println("Exception on target invocation on " + act.getName());
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
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
class MyTest implements Iterable<String> {

    ArrayList<String> steps;
    public MyTest() {
        steps = new ArrayList<String>();
    }

    public void append(String step) {
        steps.add(step);
    }

    public int size() {
        return steps.size();
    }

    public String getStepAt(int i) {
        return steps.get(i);
    }

    public String toString() {
        return steps.toString();
    }

    public Iterator<String> iterator() {
        return steps.iterator();
    }

}