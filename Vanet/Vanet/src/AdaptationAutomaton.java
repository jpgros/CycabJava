

	import com.sun.tools.javac.util.Pair;

	import java.util.HashMap;

	public interface AdaptationAutomaton<SUT> {
	    public int getState();
	    public String toString();
	    public void reset();
	    public double match(SUT o) throws AdaptationFailedException;
	}

	abstract class VanetAdaptation implements AdaptationAutomaton<Road> {

	    protected int state = 0;

	    @Override
	    public int getState() {
	        return state;
	    }

	    @Override
	    public void reset() {
	        state = 0;
	    }

	    @Override
	    public abstract double match(Road sut) throws AdaptationFailedException;
	}


	class AdaptationFailedException extends Exception {

	    AdaptationAutomaton pa;

	    public AdaptationFailedException(AdaptationAutomaton _pa, String msg) {
	        super(msg);
	        pa = _pa;
	    }

	}
	//when after low battery if vehicle is in platoon then quitPlatoon is high
	class Adaptation1 extends VanetAdaptation {
		HashMap<Vehicle, Integer> forEachVehicle = new HashMap<Vehicle, Integer>();
		
		public double match(Road sut) throws AdaptationFailedException {
			for (Vehicle v : sut) {
	            if (!forEachVehicle.keySet().contains(v)) {
	                forEachVehicle.put(v, 0);
	            }
	            switch (forEachVehicle.get(v)) {
	            // We consider that when and if cannot happen at the same tick because the system will not join with a vehicle that is at one tick to be low battery
	            // consideration to do
	            case 0: 
	            	if(v.getAutonomie()< v.LOW_BATTERY) {
	            		forEachVehicle.put(v, forEachVehicle.get(v)+1);
	            		if(v.myPlatoon !=null) {
		            		forEachVehicle.put(v, forEachVehicle.get(v)+20);
		            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
		            			forEachVehicle.put(v, 3);
		            		}
	            		}
	            	}
	            	else if(v.myPlatoon != null) {
	            		forEachVehicle.put(v, forEachVehicle.get(v)+2);
	            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            			 throw new AdaptationFailedException(this, "When clause : vehicle.autonomy > LOW_ENERGY not respected");
	            		}
	            	}
	            	if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            		if(v.myPlatoon!=null) {
	            			throw new AdaptationFailedException(this, "When clause : vehicle.autonomy > LOW_ENERGY not respected");
	            		}
	            		else if(v.getAutonomie()<v.LOW_BATTERY) {
	            			throw new AdaptationFailedException(this, "If clause : vehicle.platoon!=null not respected");
	            		}
	            		else {
	            			throw new AdaptationFailedException(this, "When clause : vehicle.autonomy > LOW_ENERGY AND If clause : vehicle.platoon!=null not respected");
	            		} 
           		}
	            	
	            	//if reconf triggered (means that the system is using is now with priority indicated in the PA) put v + 4
	            	break;
	            case 1:
	            	if(v.getAutonomie()> v.LOW_BATTERY) {
	            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            			throw new AdaptationFailedException(this, "When clause : vehicle.autonomy > LOW_ENERGY not respected");
	            		}
	            		forEachVehicle.put(v, 3); //or 0 ?
	            		//cannot be in platoon
	            	}
	            	else if(v.myPlatoon!=null) {
	            		forEachVehicle.put(v, 21);
	            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            			forEachVehicle.put(v, 3); //or 1 ?
	            		}
	            	}
	            	break;
	            case 2: // already in platoon
	            	if(v.getAutonomie()< v.LOW_BATTERY) {
	            		forEachVehicle.put(v,21);
	            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            			forEachVehicle.put(v,3);
	            		}
	            		forEachVehicle.put(v, 3); //or 0 ?
	            	}
	            	else if(v.myPlatoon==null) {
	            		forEachVehicle.put(v, 3);
	            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	              			throw new AdaptationFailedException(this, "When clause : vehicle.autonomy > LOW_ENERGY AND If clause : vehicle.platoon!=null not respected");
	            		}
	            	}       	
	            	break;
	            case 21:
	            	//low battery and in platoon, 'when' cannot change until quitted platoon
	            	if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
	            		forEachVehicle.put(v, 3); // or 1 ?
            		}
	            	else if(v.myPlatoon==null){ //not sure it is mandatory
	            		forEachVehicle.put(v, 1);
	            	}
	            	break;
	            case 3: // reconf triggered but when and guard not validated
	            	// but can be triggered because of other PA => need to retrieve QUITFORSTATION event
	            	if(v.getAutonomie()< v.LOW_BATTERY) {
	            		forEachVehicle.put(v, forEachVehicle.get(v)+1);
	            		if(v.myPlatoon !=null) {
		            		forEachVehicle.put(v, forEachVehicle.get(v)+20);
		            		if(v.myPlatoon.lastReconf.name == PolicyName.QUITFORSTATION && v.myPlatoon.lastReconf.vehicle ==v) {
		            			forEachVehicle.put(v, 3); // or 1 ?
		            		}
	            		}
	            	}
	            		            		           
	            default :
	            	System.out.println("Problem putting values in adaptation automaton number one");
	            	break;
	            }
	            
	            //when and if verified here, conformance priority verified when test stops
//			case 0: nothing triggered
//			case 1: when triggered
//			case 2: guard triggered
//			case 3: reconf triggered
//			case 21: when and guard triggered
//			...
			
			return 0;
			}
			return -1;
		}
	}
	
	// adaptation 2 : when after high energy ; if distance > x and vehicle is solo then utility of join is high

	// adaptation 3 : when after high energy ; if distance < y and vehicle is solo then utility of join is low
	
	// adaptation 4 : when after nbVehiclesMax always ejectTrue until !nbVehiclesMax; if distance <y or energy < x then utility of quit platoon is high 

	// adaptation 5 : when after nbVehiclesMax ; if distance > y and energy > x then utility of quit platoon is low

	// adaptation 6 : when after nextLeaderList is empty ; if vehicle is in platoon and minValue > 20 then utility of addVehicleInLeaderList is medium
	
	// adaptation 7 : when after autonomie < X; if vehicle is leader and nextleaderList is not empty ; then utility of relay is high
	
	// adaptation 8 : when after nextLeaderList is empty ; if NbVehiclePlatoon(i) + nbVehiclePlatoon(j) <nbMax; then utility og mergePlatoons(i,j) is high 
	