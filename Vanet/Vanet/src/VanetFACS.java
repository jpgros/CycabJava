
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
        PrintWriter writerErr = new PrintWriter("./outputError.txt", "UTF-8");
        FileReader vehicleReader = new FileReader("./vehiclePolicies.txt"); // /Vanet
        FileReader platoonReader = new FileReader("./platoonPolicies.txt");
        FileReader roadReader = new FileReader("./platoonPolicies.txt");

        FsmModel fsm = new VanetFSM(writer, vehicleReader, platoonReader, roadReader);

        StochasticTester st = new StochasticTester(fsm);

        AdaptationPolicyModel apm = new AdaptationPolicyModel();
        // Adaptation policy rules go here
        setRulesForAPM(apm);

        VanetConformanceMonitor vcm = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm);
        writer.println("goes here");

        ArrayList<MyTest> initial = st.generate(1, 100);
        System.out.println("here");
        writer.println("and here");
        vcm.printReport();
        writerErr.close();

    }

    public static void setRulesForAPM(AdaptationPolicyModel a) {
    	
        // Rule: -- relai d'un vehicule qui vient d'entrer dans le peloton
        //  after join(v) until quit(v)
        //      if min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
        //          relay |--> medium
        Rule r1 = new Rule(new r1p1(), new r1p2(), PolicyName.UPGRADERELAY, Priority.MEDIUM);
        a.addRule(r1);

        Rule r2  = new Rule(new r2p1(), new r2p2(), PolicyName.RELAY, Priority.HIGH); //or quitstation
        a.addRule(r2);
        // Rule: -- relai du leader qui arrive à destination ou échéance
        //  after relay(v) until quit(v) | relay
        //      if min(v.distance, v.auto) < 100
        //          relay |--> low

        Rule r3  = new Rule(new r3p1(), new r3p2(), PolicyName.RELAY, Priority.LOW); //or quitstation
        a.addRule(r3);
        // Rule: -- relai du leader qui arrive à destination ou échéance
        //  after relay(v) until quit(v) | relay
        //      if min(v.distance, v.auto) < 50
        //          relay |--> high



        Rule r4  = new Rule(new r4p1(), new r4p2(), PolicyName.QUITPLATOON, Priority.HIGH); //or quitstation
        a.addRule(r4);
        // Rule: -- départ du platoon du véhicule qui arrive à destination ou échéance
        //  after join until quit
        //      if (v.auto >= distance station[0] && v.auto < distance station[1])
        //          quit |--> high

        Rule r5  = new Rule(new r5p1(), new r5p2(), PolicyName.QUITPLATOON, Priority.LOW); //or quitstation
        a.addRule(r5);
        Rule r6  = new Rule(new r6p1(), new r6p2(), PolicyName.QUITPLATOON, Priority.MEDIUM); //or quitstation
        a.addRule(r6);

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
    public String toString(){
    	return "r1p1";
    }
}

class r1p2 extends VanetProperty {
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.myPlatoon.getVehiclesList().size() < 3 || currentVehicle.getMinValue() < currentVehicle.myPlatoon.leader.getMinValue())
            throw new PropertyFailedException(this, "Vehicle not ready to be leader");
        return 0;
    }
    public String toString(){
    	return "r1p2";
    }
}

class r2p1 extends VanetProperty {
    //  after join(v) until quit(v)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.myPlatoon == null ) {
            throw new PropertyFailedException(this, "Vehicle not in platoon");
        }
        else if (currentVehicle.myPlatoon.leader == currentVehicle) {
        	throw new PropertyFailedException(this, "Vehicle not leader");
        }
        return 0;
    }
    public String toString(){
    	return "r2p1";
    }
}

class r2p2 extends VanetProperty {
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.getMinValue() >200) //currentVehicle.myPlatoon.getVehiclesList().size() < 3 ||
            throw new PropertyFailedException(this, "Vehicle not ready to downgrade");
        return 0;
    }
    public String toString(){
    	return "r2p2";
    }
}

class r3p1 extends VanetProperty {
    //  after join(v) until quit(v)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.myPlatoon == null ) {
            throw new PropertyFailedException(this, "Vehicle not in platoon");
        }
        else if (currentVehicle.myPlatoon.leader == currentVehicle) {
        	throw new PropertyFailedException(this, "Vehicle not leader");
        }
        return 0;
    }
    public String toString(){
    	return "r3p1";
    }
}

class r3p2 extends VanetProperty {
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.getMinValue() >300) //currentVehicle.myPlatoon.getVehiclesList().size() < 3 ||
            throw new PropertyFailedException(this, "Vehicle not ready to downgrade");
        return 0;
    }
    public String toString(){
    	return "r3p2";
    }
}

class r4p1 extends VanetProperty {
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if (currentVehicle.myPlatoon == null) {
	            throw new PropertyFailedException(this, "Vehicle not in platoon");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r4p1";
	    }
}

class r4p2 extends VanetProperty {
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.getMinValue() > 100)
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        return 0;
	    }
	 	public String toString(){
	    	return "r4p2";
	    }
}

class r5p1 extends VanetProperty {
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if (currentVehicle.myPlatoon == null) {
	            throw new PropertyFailedException(this, "Vehicle not in platoon");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r5p1";
	    }
}

class r5p2 extends VanetProperty {
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.distance > 200)
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        return 0;
	    }
	 	public String toString(){
	    	return "r5p2";
	    }
}
class r6p1 extends VanetProperty {
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if (currentVehicle.myPlatoon == null) {
	            throw new PropertyFailedException(this, "Vehicle not in platoon");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r6p1";
	    }
}

class r6p2 extends VanetProperty {
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.distance > 150) //getMinValue() 
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        return 0;
	    }
	 	public String toString(){
	    	return "r6p2";
	    }
}

