
import nz.ac.waikato.modeljunit.FsmModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections15.iterators.EntrySetMapIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 14/03/2018
 * Time: 08:46
 */

public class VanetFACS implements Serializable{
    public static void main(String[] args) throws Exception {    	
    	Date date = new Date() ;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
    	PrintWriter writerLog = new PrintWriter("./logs/log"+ dateFormat.format(date) +".txt", "UTF-8");    	    	
    	PrintWriter writer = new PrintWriter("./outputGenetic.txt", "UTF-8");
        PrintWriter writerErr = new PrintWriter("./outputError.txt", "UTF-8"); 
        FileReader vehicleReader = new FileReader("./vehiclePolicies.txt"); // /Vanet
        FileReader platoonReader = new FileReader("./platoonPolicies.txt");
        FileReader roadReader = new FileReader("./platoonPolicies.txt");   
        boolean reinitCov  =false; // do we want to reinit different coverages afeter each test
    	boolean interruptCovered =false; // do we want to stop execution when everything is covered		
        String strWriter ="";
        String vhReader="";
        String plReader="";
        String rdReader="";
        String strLog="";
        FsmModel fsm = new VanetFSM(strWriter, vhReader, plReader, rdReader, strLog);       
        StochasticTester st   = new StochasticTester(fsm,writerLog,reinitCov, interruptCovered);
        AdaptationPolicyModel apm = new AdaptationPolicyModel();
        // Adaptation policy rules go here
        setRulesForAPM(apm,writer);       
        VanetConformanceMonitor vcm = new VanetConformanceMonitor(apm, writerErr);         
        //choice between generation and retrieving
        //ArrayList<MyTest> initial=retrieveTest(st,vcm,apm);
        generatetest(st, vcm, writerErr,apm);
        strWriter=((VanetFSM) fsm).getSUT().getStringWriter();
        writer.println(" strLog Begins : "); 
        writer.print(strWriter);
        writerErr.close();
        writer.close();
        vehicleReader.close();
        platoonReader.close();
        roadReader.close();
        writerLog.close();
        //objectInputStream.close();
        //writerLog.close();
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



        Rule r4  = new Rule(new r4p1(), new r4p2(), PolicyName.QUITPLATOON, Priority.LOW); //or quitstation
        a.addRule(r4);
        // Rule: -- départ du platoon du véhicule qui arrive à destination ou échéance
        //  after join until quit
        //      if (v.auto >= distance station[0] && v.auto < distance station[1])
        //          quit |--> high

        Rule r5  = new Rule(new r5p1(), new r5p2(), PolicyName.QUITPLATOON, Priority.HIGH); //or quitstation
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
    
    public static ArrayList<MyTest> retrieveTest(StochasticTester st, VanetConformanceMonitor vcm, AdaptationPolicyModel apm){
    	ArrayList<MyTest> initial=null;
    	FileInputStream inser;
		try {  
			inser = new FileInputStream("output.ser");
			ObjectInputStream objectInputStream = new ObjectInputStream(inser);
	        ArrayList<SerializableTest> testInput = (ArrayList<SerializableTest>)objectInputStream.readObject();
	        objectInputStream.close();
			st.setMonitor(vcm); 
	        initial = st.retrieve(apm,testInput);	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//reading for input test
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return initial;
    }
    
    public static void generatetest(StochasticTester st, VanetConformanceMonitor vcm, PrintWriter writerLog,AdaptationPolicyModel apm) {
    	//attention risque d incomptatibilite nbstep 
        ArrayList<SerializableStep> serializableArray = new ArrayList<SerializableStep>();
        ArrayList<SerializableTest> testArraySer = new ArrayList<SerializableTest>();   
		try {
			FileOutputStream outser = new FileOutputStream("output.ser");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outser);
			ArrayList<MyTest> testsList=null;
			st.setMonitor(vcm);
			testsList=st.generate(1,1000,apm);
			//stats should be verified : may be done globaly		
			//convert initial in a serializable list and writing it
	        for(MyTest curTest : testsList) {
	        	for(MyStep curStep : curTest ) {
	        		SerializableStep step = new SerializableStep(curStep.toString(), curStep.instance, curStep.params);
	        		serializableArray.add(step);
	        	}
	        	SerializableTest test = new SerializableTest(serializableArray);
	        	testArraySer.add(test);
	        	serializableArray.clear();
	        }    
	        //((VanetFSM) fsm).printValues();
	        objectOutputStream.writeObject(testArraySer);
	        objectOutputStream.close();    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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
            throw new PropertyFailedException(this, "Vehicle already leader");
        }
        else if(currentVehicle.myPlatoon.created) {
            throw new PropertyFailedException(this, "Cannot upgrade a vehicle just after a platoon creation");

        }
        else {
        	String x ="Config upgraderelay OK for vehicle "+ currentVehicle.getId();
        	//System.out.println(x);
        	writer.println(x);
        }
        return 0;
    }
    public r1p1(PrintWriter w) {
    	writer = w;
    }
    public String toString(){
    	return "TP upgrade Relay";
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
    	String x="TP upgraderelay OK for vehicle min value "+ currentVehicle.getMinValue()/currentVehicle.DEC_LEADER + "leader minvalue "+ currentVehicle.myPlatoon.leader.getMinValue();
    	//System.out.println(x);
    	writer.println(x);

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
        	String x ="Config relay OK for vehicle "+ currentVehicle.id;
        	//System.out.println(x);
        	writer.println(x);
        }
        return 0;
    }
    public String toString(){
    	return "TP relay";
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
        if (currentVehicle.autonomie >currentVehicle.LOW_LEADER_BATTERY){// getMinValueLeader() >200) { //currentVehicle.myPlatoon.getVehiclesList().size() < 3 ||
        	writer.println("TP relay KO for vehicle "+ currentVehicle.id + "minVal "+ currentVehicle.getMinValueLeader());
            throw new PropertyFailedException(this, "Vehicle not ready to downgrade");}
        
    
	   else {
		   String x="TP relay OK for vehicle "+ currentVehicle.id + "minval " + currentVehicle.getMinValueLeader();
		   //System.out.println(x);
		   writer.println(x);
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
	    	return "TP Quit platoon HIGH";
	    }
}

class r4p2 extends VanetProperty {
	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.distance >= currentVehicle.LOW_DIST)
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
	    	return "TP QuitPlatoon LOW";
	    }
}

class r5p2 extends VanetProperty {

	 @Override
	    public double match(Road sut) throws PropertyFailedException {
	        if ( currentVehicle.autonomie >= currentVehicle.LOW_BATTERY)
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
	    	return "TP QuitFailure HIGH";
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
		 	if ( currentVehicle.distance >= currentVehicle.VLOW_DIST )
	            throw new PropertyFailedException(this, "Vehicle not ready to quit platoon");
		 	
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
	    	return "QuitForStation HIGH";
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
	    	return "TP QuitForStation MEDIUM";
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
	        	throw new PropertyFailedException(this, "Vehicle not in platoon or do not need to quit for station");
	        }
	        else {
	        	writer.println("Config OK for quitStas LOW");
	        }
	        return 0;
	    }
	  public String toString(){
	    	return "TP QuitForStation LOW";
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
class SerializableStep implements Serializable{
	String meth;
	Object instance;
    Object[] params;

    public SerializableStep(String _m, Object _i, Object[] _p) {
        meth = _m;
        instance = _i;
        params = _p;
    }
    public String getMethName() {
    	return meth;
    }
    public Object getInstance() {
    	return instance;
    }
    public Object[] getParams() {
    	return params;
    }

    public String toString() {
        String ret = /*instance + "." +*/ meth + "(";
        for (int i=0; i < params.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += params[i].toString();
        }
        return ret + ")";
    }
}
 
 class SerializableTest implements Serializable{
	 ArrayList<SerializableStep> steps = new ArrayList<SerializableStep>();
	    public SerializableTest(ArrayList<SerializableStep> s) {
	    	for(SerializableStep step : s) {
	    		steps.add(step);
	    	}
		}

	    public void append(SerializableStep step) {
	        steps.add(step);
	    }

	    public int size() {
	        return steps.size();
	    }

	    public SerializableStep getStepAt(int i) {
	        return steps.get(i);
	    }

	    public String toString() {
	        return steps.toString();
	    }

	    public Iterator<SerializableStep> iterator() {
	        return steps.iterator();
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

