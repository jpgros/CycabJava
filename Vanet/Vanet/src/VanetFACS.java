
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.FileReader;
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
public class VanetFACS {

    public static void main(String[] args) throws Exception {

        PrintWriter writer = new PrintWriter("./outputGenetic.txt", "UTF-8");
        FileReader vehicleReader = new FileReader("./Vanet/vehiclePolicies.txt");
        FileReader platoonReader = new FileReader("./Vanet/platoonPolicies.txt");
        FileReader roadReader = new FileReader("./Vanet/platoonPolicies.txt");
        FsmModel fsm = new VanetFSM(writer, vehicleReader, platoonReader, roadReader);

        StochasticTester st = new StochasticTester(fsm);

        AdaptationPolicyModel<Road> apm = new AdaptationPolicyModel<Road>();
        // Adaptation policy rules go here
        // TODO apm.addRule();
        
        VanetConformanceMonitor vcm = new VanetConformanceMonitor(apm);
        st.setMonitor(vcm);

        ArrayList<MyTest> initial = st.generate(1, 1000);
        
    }
}
