
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

        AdaptationPolicyModel apm = new AdaptationPolicyModel();
        // Adaptation policy rules go here
        setRulesForAPM(apm);
        
        VanetConformanceMonitor vcm = new VanetConformanceMonitor(apm);
        st.setMonitor(vcm);

        ArrayList<MyTest> initial = st.generate(1, 1000);

        vcm.printReport();
    }

    public static void setRulesForAPM(AdaptationPolicyModel a) {

        // Rule: -- relai d'un vehicule qui vient d'entrer dans le peloton
        //  after join(v) until quit(v)
        //      if min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
        //          relay |--> medium
        Rule r1 = new Rule(new r1p1(), new r1p2(), PolicyName.RELAY, Priority.MEDIUM);
        a.addRule(r1);



        // Rule: -- relai du leader qui arrive à destination ou échéance
        //  after relay(v) until quit(v) | relay
        //      if min(v.distance, v.auto) < 100
        //          relay |--> low


        // Rule: -- relai du leader qui arrive à destination ou échéance
        //  after relay(v) until quit(v) | relay
        //      if min(v.distance, v.auto) < 50
        //          relay |--> high




        // Rule: -- départ du platoon du véhicule qui arrive à destination ou échéance
        //  after join until quit
        //      if (v.auto >= distance station[0] && v.auto < distance station[1])
        //          quit |--> high




    }

}



class r1p1 extends VanetProperty {
    //  after join(v) until quit(v)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.myPlatoon == null) {
            throw new PropertyFailedException(this, "Vehicle not in platoon");
        }
        return 0;
    }
}

class r1p2 extends VanetProperty {
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.myPlatoon.getVehiclesList().size() < 3 || currentVehicle.getMinValue(true) < currentVehicle.myPlatoon.leader.getMinValue(true))
            throw new PropertyFailedException(this, "Vehicle not ready to be leader");
        return 0;
    }
}
