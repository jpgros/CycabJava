package SUT;
import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 27/03/2018
 * Time: 13:35
 */
public interface PropertyAutomaton<SUT> {
	public String name=null;
    public int getState();
    public String toString();
    public void reset();
    public double match(SUT o) throws PropertyFailedException;
    public PolicyName getName();
    public void setName(PolicyName s);


}

abstract class VanetProperty implements PropertyAutomaton<Road> {
	protected PolicyName name=null;
    protected int state = 0;
    protected double priority=0;
    protected Vehicle currentVehicle = null;
    protected HashMap<Vehicle, ArrayList<Triple>> forEachVehicleProp = new HashMap<Vehicle, ArrayList<Triple>>();
    protected boolean [][] transitionsMade = new boolean[][]{
    	  { false, true},
    	  { false,false},
    	  { false,false},
    	  };
    public void setCurrentVehicle(Vehicle v) {
        currentVehicle = v;
    }
    public void setPriority(double p) {
    	priority=p;
    }

    public void setName(PolicyName s) {
    	name =s;
    }

    public PolicyName getName() {
        return name;
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, currentVehicle, priority);
    }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VanetProperty elt = (VanetProperty) obj;
      if (this.name == elt.name && this.currentVehicle== elt.currentVehicle && this.priority == elt.priority) { //
    	  return true;
      }
      else {
    	  return false;
      }
   }
   public Vehicle getVehicle(){
	   return currentVehicle;
   }
   public double getPriority() {
	   return priority;
   }
   
    @Override
    public int getState() {
        return state;
    }

    @Override
    public void reset() {
        state = 0;
    }

    @Override
    public abstract double match(Road sut) throws PropertyFailedException;
}


class PropertyFailedException extends Exception {

    PropertyAutomaton<Road> pa;

    public PropertyFailedException(PropertyAutomaton<Road> _pa, String msg) {
        super(msg);
        pa = _pa;
    }

}

class PropertyCoveredException extends Exception {
    public PropertyCoveredException( String msg) {
        super(msg);
    }
}

/**
 X  Toujours un leader dans le peloton
 X  Au moins 2 VL dans le peloton
 X  After joinPlatoon Always v.battery > 10 && v.distance > 0 until quitting platoon
 *  After relay(v) always battery > 33 until vehicle downgraded
 *  Never refill when inPlatoon
 *  After relay(v) eventually relay(v') before critical battery
 *  --
 *  trouver des propriétés
 *  --> si plus de véhicule pour prendre le relai --> plus de platoon
 *  --> quand 2 véhicule et 1 part --> plus de platoon
 *  -- TODO JP
 *  --> intégrer la notion de prochaine station
 *  --> propriétés liées au prochaines stations
 *  --> heuristique sur le choix du véhicule lié à un score (min distance, autonomie)
 */

class Property1 extends VanetProperty {
	Mutant mutant;
	public Property1() {
		
	}
	public Property1(Mutant m) {
		mutant=m;
	}
    // Toujours un leader dans le peloton

    public double match(Road sut) throws PropertyFailedException {
    	
    	double scoreV, ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicleProp.keySet().contains(v)) {
            	Triple t = new Triple(0, "init",sut.stepNb);
            	ArrayList<Triple> alp = new ArrayList<Triple>();
            	alp.add(t);
                forEachVehicleProp.put(v, alp);
            }
            ArrayList<Triple> alpTmp =forEachVehicleProp.get(v);
            switch ((Integer)(alpTmp.get(alpTmp.size()-1).state)) {
                case 0:
                    if (v.myPlatoon != null) {
                    	Triple t = new Triple(1, "joinPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[0][0]=true;
                    }
                    else {
                     	transitionsMade[0][1]=true; // not 0->2 but 0->0 : ysed to take less space
                	}
                    break;
                case 2://not in platoon or just created
                    if (v.myPlatoon != null) {
                    	Triple t = new Triple(1, "joinPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[2][0]=true;
                    }
                    else {
                    	transitionsMade[2][1]=true;
                    }
                    break;
                case 1: //in platoon


                     if (v.myPlatoon == null) {
                    	Triple t = new Triple(2, "quitPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[1][0]=true;
                        // vehicle out of the platoon
                    }
                    else if (v.myPlatoon !=null) {
                    	transitionsMade[1][1]=true;
                        if (v.myPlatoon.leader == null || v.myPlatoon.noLeaderDetected()) {
                            throw new PropertyFailedException(this, "Platoon " + v.myPlatoon + " does not have a leader.");
                        }
                    }
                    break;
            }
            alpTmp =forEachVehicleProp.get(v);
            scoreV = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
            // returns the minimal value of all
            ret = (ret == -1 || scoreV < ret) ? scoreV : ret;
        }
        return ret;
    }
    @Override 
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Property1";
    }
}


class Property2 extends VanetProperty {
	Mutant mutant;
	public Property2() {
		
	}
	public Property2(Mutant m) {
		mutant=m;
	}
    // Au moins 2 VL dans le peloton
    public double match(Road sut) throws PropertyFailedException {
    	 int numberVl = mutant== Mutant.M6 ? 3 : 2;
    	 double scoreV, ret = -1;
         for (Vehicle v : sut) {
             if (!forEachVehicleProp.keySet().contains(v)) {
             	Triple t = new Triple(0, "init",sut.stepNb);
             	ArrayList<Triple> alp = new ArrayList<Triple>();
             	alp.add(t);
                 forEachVehicleProp.put(v, alp);
             }
             ArrayList<Triple> alpTmp =forEachVehicleProp.get(v);
             switch ((Integer)(alpTmp.get(alpTmp.size()-1).state)) {
                 case 0:
                     if (v.myPlatoon != null) {
                      	Triple t = new Triple(1, "joinPl", sut.stepNb);
                      	alpTmp.add(t);
                          forEachVehicleProp.put(v, alpTmp);
                          transitionsMade[0][0]=true;
                      }
                     else {
                      	transitionsMade[0][1]=true; // not 0->2 but 0->0 : ysed to take less space
                 	 }
                      break;
                 case 2://not in platoon or just created
                     if (v.myPlatoon != null) {
                     	Triple t = new Triple(1, "joinPl", sut.stepNb);
                     	alpTmp.add(t);
                         forEachVehicleProp.put(v, alpTmp);
                         transitionsMade[2][0]=true;
                     }
                     else {
                    	 transitionsMade[2][1]=true;
                     }
                     break;
                 case 1: //in platoon


                    if (v.myPlatoon == null) {
                     	Triple t = new Triple(2, "quitPl", sut.stepNb);
                     	alpTmp.add(t);
                         forEachVehicleProp.put(v, alpTmp);
                         transitionsMade[1][1]=true;
                         // vehicle out of the platoon
                     }
                     else if(v.myPlatoon !=null){
                    	 transitionsMade[1][0]=true;
                         if (v.myPlatoon.vehiclesList.size() < numberVl) {
                             throw new PropertyFailedException(this, "Platoon " + v.myPlatoon + " has less than 2 vehicles.");
                         }
                     }
                     break;
             }
             alpTmp =forEachVehicleProp.get(v);
             scoreV = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
             // returns the minimal value of all
             ret = (ret == -1 || scoreV < ret) ? scoreV : ret;
         }
         return ret;
    }
    @Override 
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Property2";
    }
}

class Property3 extends VanetProperty {
	Mutant mutant;
	public static int MIN_LEVEL_BATTERY;
	public Property3() {
		
	}
	public Property3(Mutant m) {
		mutant=m;
		MIN_LEVEL_BATTERY = mutant==Mutant.M1 ? 20 : 0;
	}
    // For each vehicle:  After joinPlatoon Always v.battery > 0 && v.distance > 0 until quitting platoon


    public double match(Road sut) throws PropertyFailedException {
        double ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicleProp.keySet().contains(v)) {
            	Triple t = new Triple(0, "init",sut.stepNb);
            	ArrayList<Triple> alp = new ArrayList<Triple>();
            	alp.add(t);
                forEachVehicleProp.put(v, alp);
            }
        	ArrayList<Triple> alpTmp =forEachVehicleProp.get(v);
            switch ((Integer)(alpTmp.get(alpTmp.size()-1).state)) {
                case 0:
                    if (v.myPlatoon != null) {
                    	Triple t = new Triple(1, "JoinPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[0][0]=true;
                    }
                    else {
                     	transitionsMade[0][1]=true; // not 0->2 but 0->0 : ysed to take less space
                	 }
                    break;
                case 2:
                    if (v.myPlatoon != null) {
                    	Triple t = new Triple(1, "JoinPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[2][0]=true;

                    } else {
                    	transitionsMade[2][1]=true;
                    }break;
                case 1:

                    if (v.myPlatoon == null) { 
                        // vehicle out of the platoon
                    	Triple t = new Triple(2, "QuitPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                        transitionsMade[1][1]=true;
                    }
                    else if(v.myPlatoon!=null) {
                    	transitionsMade[1][0]=true;
                        if (v.autonomie < MIN_LEVEL_BATTERY || v.distance == 0) {
                            throw new PropertyFailedException(this, "Vehicle " + v.id + " has low autonomy or has reached destination. \n vehicle's platoon reconf list " +v.myPlatoon.policies.listPolicy);
                        }
                    }
                    break;
            }
            // returns the minimal value of all 
            if (ret == -1) {
                ret = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
            } else {
                int g = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
                if (g < ret) {
                    ret = g;
                }
            }
        }
        return ret;
    }
    @Override 
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Property3";
    }
}


class Property4 extends VanetProperty {
	Mutant mutant;
	public Property4() {
		
	}
	public Property4(Mutant m) {
		mutant=m;
	}
	
    // For each vehicle: After relay(v) always battery > 33 until vehicle downgraded
    public double match(Road sut) throws PropertyFailedException {
        double scoreV, ret = -1;
        double minLeaderBat;
        for (Vehicle v : sut) {
            if (!forEachVehicleProp.keySet().contains(v)) {
            	Triple t = new Triple(0, "init",sut.stepNb);
            	ArrayList<Triple> alp = new ArrayList<Triple>();
            	alp.add(t);
                forEachVehicleProp.put(v, alp);
            }
            ArrayList<Triple> alpTmp =forEachVehicleProp.get(v);
            switch ((Integer)(alpTmp.get(alpTmp.size()-1).state)) {
                case 0: 
                	if (v.myPlatoon != null && v.myPlatoon.leader == v) {
                    	Triple t = new Triple(1, "Elected", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                    	transitionsMade[0][0]=true;
                    }
                	else {
                     	transitionsMade[0][1]=true; // not 0->2 but 0->0 : ysed to take less space
                	}
                    break;
                case 2:
                    if (v.myPlatoon != null && v.myPlatoon.leader == v) {
                    	Triple t = new Triple(1, "Elected", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                    	transitionsMade[2][0]=true;

                    } else {
                    	transitionsMade[2][1]=true;
                    }
                    break;
                case 1:

                    if (v.myPlatoon == null || v.myPlatoon.leader != v) {
                    	Triple t = new Triple(2, "Downgraded", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                    	transitionsMade[1][1]=true;
                        // vehicle out of the platoon
                    }
                    else if(v.myPlatoon!=null && v.myPlatoon.leader==v) {
                    	transitionsMade[1][0]=true;
                    	minLeaderBat = mutant==Mutant.M4 ? v.LOW_LEADER_BATTERY+3.0 : v.LOW_LEADER_BATTERY-3.0;
                        if (v.autonomie < minLeaderBat) {
                        	System.out.println("auto" +v.autonomie + "le" + v.myPlatoon.leader + "me" + v);
                            throw new PropertyFailedException(this, "Vehicle " + v.id + " has a too low autonomy for being leader, min leader bat " + minLeaderBat);
                        }
                    }

                    break;
            }
            alpTmp =forEachVehicleProp.get(v);
            scoreV = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
            // returns the minimal value of all
            ret = (ret == -1 || scoreV < ret) ? scoreV : ret;

        }
        return ret;
    }
    @Override 
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Property4";
    }
}


class Property5 extends VanetProperty {
	Mutant mutant;
	public Property5() {
		
	}
	public Property5(Mutant m) {
		mutant=m;
	}
    // For each vehicle: Never refill when inPlatoon

    public double match(Road sut) throws PropertyFailedException {
        double scoreV, ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicleProp.keySet().contains(v)) {
            	Triple t = new Triple(0, "init",sut.stepNb);
            	ArrayList<Triple> alp = new ArrayList<Triple>();
            	alp.add(t);
                forEachVehicleProp.put(v, alp);
            }
            ArrayList<Triple> alpTmp =forEachVehicleProp.get(v);
            switch ((Integer)(alpTmp.get(alpTmp.size()-1).state)) {
                case 0:
                	 if (v.myPlatoon != null) {
                     	Triple t = new Triple(2, "joinPl", sut.stepNb);
                     	alpTmp.add(t);
                         forEachVehicleProp.put(v, alpTmp);
                       	transitionsMade[0][0]=true;

                     }
                	 else {
                     	transitionsMade[0][1]=true; // not 0->2 but 0->0 : ysed to take less space
                	 }
                     break;
                case 2://not in platoon
                    if (v.myPlatoon != null) {
                    	Triple t = new Triple(1, "joinPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                    	transitionsMade[2][0]=true;
                    }
                    else {
                    	transitionsMade[2][1]=true;
                    }
                    break;
                case 1: //in platoon

                	 if (v.myPlatoon == null) {
                    	Triple t = new Triple(1, "quitPl", sut.stepNb);
                    	alpTmp.add(t);
                        forEachVehicleProp.put(v, alpTmp);
                    	transitionsMade[1][1]=true;
                        // vehicle out of the platoon
                    }
                    else if(v.myPlatoon!=null) {
                    	transitionsMade[1][0]=true;
                   	 if (v.autonomie == 100) {
                         throw new PropertyFailedException(this, "Vehicle " + v.id + " should not refill while in platoon. \n");
                     }
                    }
                    break;
            }
            alpTmp =forEachVehicleProp.get(v);
            scoreV = 2 - (Integer)(alpTmp.get(alpTmp.size()-1).state);
            // returns the minimal value of all
            ret = (ret == -1 || scoreV < ret) ? scoreV : ret;
        }
        return ret;
    }
    @Override 
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Property5";
    }
}


class Triple {
	Integer state;
	String transition;
	Integer step;
	public Triple(Integer sta, String tr, Integer ste) {
		state=sta;
		transition=tr;
		step=ste;
	}
}
//Property 6 no platoon with listNextLeader empty AND without leader
//property 7 distance > next station
