import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 30/04/2018
 * Time: 08:46
 */
public class VanetMonitoring {


    public static void main(String[] args) {
//    	PrintWriter writer = new PrintWriter("outputGenetic.txt", "UTF-8");
//    	FileReader vehicleReader = new FileReader("vehiclePolicies.txt");
//    	FileReader platoonReader = new FileReader("platoonPolicies.txt");
//    	FileReader roadReader = new FileReader("platoonPolicies.txt"); 
    	String writer="";
    	String vehicleReader="";
    	String platoonReader="";
    	String roadReader="";
        String writerLog ="";

        VanetFSM fsm = new VanetFSM(writer, writerLog,null,null,null,null);        
        ArrayList<VanetProperty> props = new ArrayList<VanetProperty>();
        props.add(new Property1());
        props.add(new Property2());
        props.add(new Property3());
        props.add(new Property4());
        props.add(new Property5());
        executeAndMonitor(fsm, props);
    }


    /**
     * Runs the simulation and monitors the properties. 
     * @param fsm
     * @param properties
     */
    public static void executeAndMonitor(VanetFSM fsm, ArrayList<VanetProperty> properties) {

        HashMap<Method, Double> actionsAndProbabilities = getActionTaggedMethods(fsm);
        System.out.println("Actions & Probabilities :\n" + actionsAndProbabilities);

        // reset FSM exploration
        fsm.reset(true);
        for (VanetProperty p : properties) {
            p.reset();
        }

        // make new simulation and monitor the properties
         boolean b;
         do {
            // compute next step
            b = computeNextStep(fsm, actionsAndProbabilities);
            if (b) {
                monitorProperties(fsm, properties);
            }
         }
         while (b);
    }


    /**
     * Computes the next step, by considering the activable actionsAndProbabilities and their associated probabilities.
     *  First probability to match is used.
     *  Side-effect: modifies the FSM. 
     * @return a String providing the name of the action that was invoked. 
     */
    private static boolean computeNextStep(FsmModel fsm,  HashMap<Method, Double> actionsAndProbabilities) {
        double sum = 0;
        double rand = 0;
        HashMap<Method, Double> actionsReady = getActivableActions(fsm, actionsAndProbabilities);
        if (actionsReady.isEmpty()) {
            return false;
        }
        do {
            sum = 0;
            rand = Math.random();
            for (Method act : actionsReady.keySet()) {
                sum += actionsReady.get(act);
                if (rand <= sum) {
                    try {
                        System.out.println("\nInvoking: " + act.getName()) ;
                        act.invoke(fsm);
                        return true;
                    } catch (IllegalAccessException e) {
                        System.err.println("Illegal access to " + act.getName());
                        System.err.println("Shouldn't have happened");
                        // e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        System.err.println("Exception on target invocation on " + act.getName());
                        System.err.println("Shouldn't have happened");
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        while (rand > sum);

        return false;
    }

    
    private static void monitorProperties(VanetFSM fsm, ArrayList<VanetProperty> properties) {
        for (VanetProperty vp : properties) {
            try {
                vp.match(fsm.getSUT());
            }
            catch (PropertyFailedException e) {
                System.err.println("Property violated : " + e.getMessage());
                System.exit(-1);
            }
        }
    }




    /**
     * Utility function: inspects the FSM and retrieves the Methods representing the actions with their associated probabilities.
     * @param fsm  the FSM to explore.
     * @return a map of actions contained in the FSM associated to their probabilities.
     */
    private static HashMap<Method, Double> getActionTaggedMethods(FsmModel fsm) {
        HashMap<Method, Double> ret = new HashMap<Method, Double>();
        for (Method m : fsm.getClass().getDeclaredMethods()) {
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
     * @param fsm  the FSM
     * @return a set of activable action Methods;
     */
    private static HashMap<Method, Double> getActivableActions(FsmModel fsm, HashMap<Method, Double> actionsAndProbabilities) {
        // computation of activable methods
        HashMap<Method, Double> activableOnes = new HashMap<Method, Double>();
        double sum = 0;
        for (Method act : actionsAndProbabilities.keySet()) {
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
