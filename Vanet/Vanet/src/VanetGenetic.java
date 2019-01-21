
import nz.ac.waikato.modeljunit.FsmModel;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 14/03/2018
 * Time: 08:46
 */
public class VanetGenetic {

    public static void main(String[] args) throws Exception {

//    	PrintWriter writer = new PrintWriter("outputGenetic.txt", "UTF-8");
//    	FileReader vehicleReader = new FileReader("vehiclePolicies.txt");
//    	FileReader platoonReader = new FileReader("platoonPolicies.txt");
//    	FileReader roadReader = new FileReader("platoonPolicies.txt"); 
    	String writer="";
    	String vehicleReader="";
    	String platoonReader="";
    	String roadReader="";
        String writerLog ="";
        FsmModel fsm = new VanetFSM(writer, writerLog,null,null,null,null);
        // initialize population using biased-random exploration of a FSM.
        StochasticTester st = new StochasticTester(fsm);
        ArrayList<MyTest> initial=null;// = st.generate(10, 50);
        for (MyTest mt : initial) {
            double[] fits = new double[mt.size()];
            double min = replayAndEvaluateTest(fsm, mt, fits);
            System.out.print("(" + min + ") [");
            for (int i=0; i < mt.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(mt.getStepAt(i) + " (" + fits[i] + ")");
            }
            System.out.println("]");
        }

        // calculer la fitness de chaque individu
        double fitness = 0;

        while (fitness > 0) {
            // sélection

            // reprodcution

            // remplacement

            // mutation
        	
        }
    }

    public static double replayAndEvaluateTest(FsmModel fsm, MyTest t, double[] fitnesses) throws PropertyFailedException {
        double min = -1;
        fsm.reset(true);
        VanetProperty prop = new Property3();
        for (int i=0; i < t.size(); i++) {
            MyStep step = t.getStepAt(i);
            try {
                Method m = fsm.getClass().getDeclaredMethod(step.meth.getName());
                m.invoke(fsm);
                double fitness = prop.match(((VanetFSM)fsm).getSUT());
                fitnesses[i] = fitness;
                min = (i==0 || fitness < min) ? fitness : min;
            }
            catch (NoSuchMethodException e) {
                System.err.println("No such method: " + step); // should not happen
            }
            catch (IllegalAccessException e) {
                System.err.println("Illegal access to " + step); // should not happen
            }
            catch (InvocationTargetException e) {
                System.err.println("Invocation target exception to " + step); // should not happen
            }
        }
        return min;
    }   
}
