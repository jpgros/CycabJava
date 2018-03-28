
import nz.ac.waikato.modeljunit.FsmModel;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 14/03/2018
 * Time: 08:46
 */
public class CycabGenetic {

    public static void main(String[] args) throws IOException {

        Controller controller = new Controller(100, SignalMode.BOTH, BatteryMode.USING, Zone.NONE);

        CycabFSM fsm = new CycabFSM();

        // initialize population using biased-random exploration of a FSM.
        StochasticTester st = new StochasticTester(fsm);
        ArrayList<MyTest> initial = st.generate(10, 20);
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

    public static double replayAndEvaluateTest(CycabFSM fsm, MyTest t, double[] fitnesses) {
        double min = -1;
        fsm.reset(true);
        CyCabProperty prop = new Property2();
        for (int i=0; i < t.size(); i++) {
            String step = t.getStepAt(i);
            try {
                Method m = fsm.getClass().getDeclaredMethod(step);
                m.invoke(fsm);
                double fitness = prop.match(fsm.getSUT());
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
