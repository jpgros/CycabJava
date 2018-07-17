
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Writer;
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

        StochasticTester st = new StochasticTester(fsm,writer);

        AdaptationPolicyModel apm = new AdaptationPolicyModel();
        // Adaptation policy rules go here
        setRulesForAPM(apm,writer);
        VanetConformanceMonitor vcm = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm);
        ArrayList<MyTest> initial = st.generate(1, 4000);
        vcm.printReport();
        
        VanetConformanceMonitor vcm2 = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm2);
        ArrayList<MyTest> initial2 = st.generate(1, 400);
        vcm2.printReport();
        
        VanetConformanceMonitor vcm3 = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm3);
        ArrayList<MyTest> initial3 = st.generate(1, 400);
        vcm3.printReport();
        
        VanetConformanceMonitor vcm4 = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm4);
        ArrayList<MyTest> initial4 = st.generate(1, 400);
        vcm4.printReport();
        
        VanetConformanceMonitor vcm5 = new VanetConformanceMonitor(apm, writerErr);
        st.setMonitor(vcm5);
        ArrayList<MyTest> initial5 = st.generate(1, 400);
        vcm5.printReport();
        
        writerErr.close();
        writer.close();
        vehicleReader.close();
        platoonReader.close();
        roadReader.close();
    }

    public static void setRulesForAPM(AdaptationPolicyModel a, PrintWriter writer) {
    	
        // Rule: -- relai d'un vehicule qui vient d'entrer dans le peloton
        //  after join(v) until quit(v)
        //      if min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
        //          relay |--> medium
        Rule r1 = new Rule(new r1p1(writer), new r1p2(writer), PolicyName.UPGRADERELAY, Priority.MEDIUM);
        a.addRule(r1);

        Rule r2  = new Rule(new r2p1(writer), new r2p2(writer), PolicyName.RELAY, Priority.HIGH); //or quitstation
        a.addRule(r2);
        // Rule: -- relai du leader qui arrive à destination ou échéance
        //  after relay(v) until quit(v) | relay
        //      if min(v.distance, v.auto) < 100
        //          relay |--> low

//        Rule r3  = new Rule(new r3p1(), new r3p2(), PolicyName.RELAY, Priority.LOW); //or quitstation
//        a.addRule(r3);
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
//        Rule r6  = new Rule(new r6p1(), new r6p2(), PolicyName.QUITPLATOON, Priority.MEDIUM); //or quitstation
//        a.addRule(r6);
        
        
        Rule r6  = new Rule(new r6p1(writer), new r6p2(writer), PolicyName.QUITFAILURE, Priority.HIGH); //or quitstation
        a.addRule(r6);
        //Rule : -- depart du vehicule qui a une batterie faible
        // after join(v) until quit(v)
        // if v.autonomy <15
        // quitfailure |----> high
        
        Rule r7  = new Rule(new r7p1(writer), new r7p2(writer), PolicyName.QUITFORSTATION, Priority.HIGH); //or quitstation
        a.addRule(r7);
        //Rule : -- depart du vehicule ayant besoin de se recharger et proche d une station
        // after join(v) until quit(v)
        // if ((v.autonomie -10.0) > (v.distanceStation[0] + v.distanceStation[1])) && v.distanceStation[0] < 50
        // quitforstation |----> high
        
        Rule r8  = new Rule(new r8p1(writer), new r8p2(writer), PolicyName.QUITFORSTATION, Priority.MEDIUM); //or quitstation
        a.addRule(r8);
        //Rule : -- depart du vehicule ayant besoin de se recharger et proche d une station
        // after join(v) until quit(v)
        // if ((v.autonomie -10.0) > (v.distanceStation[0] + v.distanceStation[1])) && v.distanceStation[0] < 70
        // quitforstation |----> medium
        
        Rule r9  = new Rule(new r9p1(writer), new r9p2(writer), PolicyName.QUITFORSTATION, Priority.LOW); //or quitstation
        a.addRule(r9);
        //Rule : -- depart du vehicule ayant besoin de se recharger et proche d une station
        // after join(v) until quit(v)
        // if ((v.autonomie -10.0) > (v.distanceStation[0] + v.distanceStation[1])) && v.distanceStation[0] < 100
        // quitforstation |----> low

    }

}



class r1p1 extends VanetProperty {
	PrintWriter writer =null;
    //  after join(v) until quit(v)
    @Override
    public double match(Road sut) throws PropertyFailedException {
    	if(! sut.tick) { //remove to have mutant
    		throw new PropertyFailedException(this, "Road is using another action than tick");
    	}
    	else if (currentVehicle.myPlatoon == null) {
            throw new PropertyFailedException(this, "Vehicle not in platoon");
        }
        else if (currentVehicle.myPlatoon.leader == currentVehicle) {
            throw new PropertyFailedException(this, "Vehicle alredy leader");
        }
        else if(currentVehicle.myPlatoon.created) {
            throw new PropertyFailedException(this, "Cannot upgrade a vehicle just after a platoon creation");

        }
        else {
        	writer.println("Config upgraderelay OK for vehicle "+ currentVehicle.getId());
        }
        return 0;
    }
    public r1p1(PrintWriter w) {
    	writer = w;
    }
    public String toString(){
    	return "r1p1";
    }
}

class r1p2 extends VanetProperty {
	PrintWriter writer = null;
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        writer.println("nb vehicles " + currentVehicle.myPlatoon.getVehiclesList().size() +" min val"+ currentVehicle.getMinValue()/currentVehicle.DEC_LEADER + "min val leader "+ currentVehicle.myPlatoon.leader.getMinValue());

        if (currentVehicle.myPlatoon.getVehiclesList().size() < 3 || (currentVehicle.getMinValue()/currentVehicle.DEC_LEADER) <= currentVehicle.myPlatoon.leader.getMinValue())
            throw new PropertyFailedException(this, "Vehicle not ready to be leader");

    
    else {
    	writer.println("TP upgraderelay OK for vehicle min value "+ currentVehicle.getMinValue()/currentVehicle.DEC_LEADER + "leader minvalue "+ currentVehicle.myPlatoon.leader.getMinValue());

    }
    return 0;
    }
    public r1p2(PrintWriter w) {
    	writer = w;
    }
    public String toString(){
    	return "r1p2";
    }
}

class r2p1 extends VanetProperty {
	PrintWriter writer =null;
    //  after join(v) until quit(v)
	public r2p1(PrintWriter w) {
		writer = w;
	}
    @Override
    public double match(Road sut) throws PropertyFailedException {
    	if(! sut.tick) { //remove to have mutant
    		throw new PropertyFailedException(this, "Road is using another action than tick");
    	}
    	else if ((currentVehicle.myPlatoon == null) || (currentVehicle.myPlatoon.leader != currentVehicle)) {
            throw new PropertyFailedException(this, "Vehicle not in platoon");
        }
        else if(currentVehicle.myPlatoon.created) {
            throw new PropertyFailedException(this, "Cannot upgrade a vehicle just after a platoon creation");

        }
//        if () {
//        	throw new PropertyFailedException(this, "Vehicle not leader");
//        }
        else {
        	writer.println("Config relay OK for vehicle "+ currentVehicle.id);
        }
        return 0;
    }
    public String toString(){
    	return "r2p1";
    }
}

class r2p2 extends VanetProperty {
	PrintWriter writer =null;
    //  after join(v) until quit(v)
	public r2p2(PrintWriter w) {
		writer = w;
	}
    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
    @Override
    public double match(Road sut) throws PropertyFailedException {
        if (currentVehicle.getMinValueLeader() >200) { //currentVehicle.myPlatoon.getVehiclesList().size() < 3 ||
        	writer.println("TP relay KO for vehicle "+ currentVehicle.id + "minVal "+ currentVehicle.getMinValueLeader());
            throw new PropertyFailedException(this, "Vehicle not ready to downgrade");}
        
    
	   else {
	    	writer.println("TP relay OK for vehicle "+ currentVehicle.id + "minval " + currentVehicle.getMinValueLeader());
	    }
        return 0;
    }
    public String toString(){
    	return "r2p2";
    }
}

//class r3p1 extends VanetProperty {
//    //  after join(v) until quit(v)
//    @Override
//    public double match(Road sut) throws PropertyFailedException {
//        if (currentVehicle.myPlatoon == null ) {
//            throw new PropertyFailedException(this, "Vehicle not in platoon");
//        }
//        else if (currentVehicle.myPlatoon.leader == currentVehicle) {
//        	throw new PropertyFailedException(this, "Vehicle not leader");
//        }
//        return 0;
//    }
//    public String toString(){
//    	return "r3p1";
//    }
//}
//
//class r3p2 extends VanetProperty {
//    //      if platoon.size > 2 && min(v.distance, v.auto) > min(v.platoon.leader.distance, v.platoon.leader.auto)
//    @Override
//    public double match(Road sut) throws PropertyFailedException {
//        if (currentVehicle.getMinValue() >300) //currentVehicle.myPlatoon.getVehiclesList().size() < 3 ||
//            throw new PropertyFailedException(this, "Vehicle not ready to downgrade");
//        return 0;
//    }
//    public String toString(){
//    	return "r3p2";
//    }
//}

class r4p1 extends VanetProperty {
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
		    if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null) {
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
	        if ( currentVehicle.distance >= 100)
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        return 0;
	    }
	 	public String toString(){
	    	return "r4p2";
	    }
}

class r5p1 extends VanetProperty {
    //  after join(v) until quit(v)
	
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
		    if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null) {
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
	        if ( currentVehicle.distance >= 200)
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        return 0;
	    }
	 	public String toString(){
	    	return "r5p2";
	    }
}

class r6p1 extends VanetProperty {
	PrintWriter writer =null;

	public r6p1(PrintWriter w) {
		writer = w;
	}
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
		    if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null) {
	            throw new PropertyFailedException(this, "Vehicle not in platoon");
	        }
	    	else {
	    		writer.println("Config OK QUITFAIL");
	    	}
	        return 0;
	    }
	  public String toString(){
	    	return "r6p1";
	    }
}

class r6p2 extends VanetProperty {
	PrintWriter writer =null;
    //  after join(v) until quit(v)
	public r6p2(PrintWriter w) {
		writer = w;
	}
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
		 	if ( currentVehicle.autonomie >= 15)
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
		 	else writer.println("TP OK QUITFAIL");
	        
	        return 0;
	    }
	 	public String toString(){
	    	return "r6p2";
	    }
}

///////////////////

class r7p1 extends VanetProperty {
	  PrintWriter writer =null;
      //  after join(v) until quit(v)
	  public r7p1(PrintWriter w) {
		  writer = w;
	  }
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
		  	if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null || ((currentVehicle.getAutonomieDistance() - 10) > (currentVehicle.road.distanceStation[0] +currentVehicle.road.distanceStation[1]))) {
	        	writer.println("Config KO for quitStas HIGH" + " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	            throw new PropertyFailedException(this, "Vehicle not in platoon or do not need to quit for station");
	        }
	        else {
	        	writer.println("Config OK for quitStas HIGH");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r7p1";
	    }
}

class r7p2 extends VanetProperty {
	 PrintWriter writer =null;
     //  after join(v) until quit(v)
	 public r7p2(PrintWriter w) {
	 	 writer = w;
	 }
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.road.distanceStation[0] >= 50) {
	        	writer.println("TP KO for quitStas HIGH");
	        	throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        }
	        else {
	        	writer.println("TP OK for quitStas HIGH");
	        }
	        return 0;
	    }
	 	public String toString(){
	    	return "r7p2";
	    }
}
class r8p1 extends VanetProperty {
	 PrintWriter writer =null;
     //  after join(v) until quit(v)
	 public r8p1(PrintWriter w) {
		 writer = w;
	 }
	  @Override
	    public double match(Road sut) throws PropertyFailedException {
		    if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null || ((currentVehicle.getAutonomieDistance() - 10) > (currentVehicle.road.distanceStation[0] +currentVehicle.road.distanceStation[1]))) {
	        	writer.println("config KO for quitStas MEDIUM"+ " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	        	throw new PropertyFailedException(this, "Vehicle not in platoon or do not need to quit for station");
	        }
	        else {
	        	writer.println("config OK for quitStas MEDIUM"+ " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r8p1";
	    }
}

class r8p2 extends VanetProperty {
	 PrintWriter writer =null;
     //  after join(v) until quit(v)
	 public r8p2(PrintWriter w) {
		 writer = w;
	 }
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.road.distanceStation[0] >= 70) {
	        	writer.println("TP KO for quitStas MEDIUM");
	        	throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        }
	        else {
	        	writer.println("TP OK for quitStas MEDIUM");
	        }
	        return 0;
	    }
	 	public String toString(){
	    	return "r8p2";
	    }
}
class r9p1 extends VanetProperty {
	  PrintWriter writer =null;
      //  after join(v) until quit(v)
	  public r9p1(PrintWriter w) {
		  writer = w;
	  }
	  @Override
	    public double match(Road sut) throws PropertyFailedException { //currentVehicle.frequencystation causes problem
		  	if(! sut.tick) { //remove to have mutant
	    		throw new PropertyFailedException(this, "Road is using another action than tick");
	    	}
	    	else if (currentVehicle.myPlatoon == null || ((currentVehicle.getAutonomieDistance() - 10) >= (currentVehicle.road.distanceStation[0] +currentVehicle.road.distanceStation[1]))) {
	        	writer.println("Config KO for quitStas LOW"+ " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	        	throw new PropertyFailedException(this, "Vehicle not in platoon or do not need to qit for station");
	        }
	        else {
	        	writer.println("Config OK for quitStas LOW");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "r9p1";
	    }
}

class r9p2 extends VanetProperty {
	 PrintWriter writer =null;
     //  after join(v) until quit(v)
	 public r9p2(PrintWriter w) {
		 writer = w;
	 }
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.road.distanceStation[0] > 100) { //>= to have mutant
	        	writer.println("TP KO for quitStas LOW"+ " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
	        }
	        else {
	        	writer.println("TP OK for quitStas LOW"+ " " +currentVehicle.getAutonomieDistance() + " "+currentVehicle.road.distanceStation[0] + " " + currentVehicle.road.distanceStation[1]);
	        }
	        return 0;
	    }
	 	public String toString(){
	    	return "r9p2";
	    }
}


//class r6p1 extends VanetProperty {
//	  @Override
//	    public double match(Road sut) throws PropertyFailedException {
//	        if (currentVehicle.myPlatoon == null) {
//	            throw new PropertyFailedException(this, "Vehicle not in platoon");
//	        }
//	        return 0;
//	    }
//	  public String toString(){
//	    	return "r6p1";
//	    }
//}
//
//class r6p2 extends VanetProperty {
//	 @Override
//	    public double match(Road sut) throws PropertyFailedException {
//	        if ( currentVehicle.distance > 150) //getMinValue() 
//	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
//	        return 0;
//	    }
//	 	public String toString(){
//	    	return "r6p2";
//	    }
//}

