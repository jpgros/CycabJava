import com.sun.tools.javac.util.Pair;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 27/03/2018
 * Time: 13:35
 */
public interface PropertyAutomaton<SUT> {
    public int getState();
    public String toString();
    public void reset();
    public double match(SUT o) throws PropertyFailedException;
}

abstract class VanetProperty implements PropertyAutomaton<Road> {

    protected int state = 0;

    protected Vehicle currentVehicle = null;

    public void setCurrentVehicle(Vehicle v) {
        currentVehicle = v;
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

    PropertyAutomaton pa;

    public PropertyFailedException(PropertyAutomaton _pa, String msg) {
        super(msg);
        pa = _pa;
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

    // Toujours un leader dans le peloton

    public double match(Road sut) throws PropertyFailedException {
        for (Vehicle v : sut.allVehicles) {
            if (v.myPlatoon != null) {
                if (v.myPlatoon.leader == null) {
                    throw new PropertyFailedException(this, "Platoon " + v.myPlatoon + " does not have a leader.");
                }
            }
        }
        return 0;
    }
}


class Property2 extends VanetProperty {

    // Au moins 2 VL dans le peloton
    public double match(Road sut) throws PropertyFailedException {
        for (Vehicle v : sut.allVehicles) {
            if (v.myPlatoon != null) {
                if (v.myPlatoon.vehiclesList.size() < 2) {
                    throw new PropertyFailedException(this, "Platoon " + v.myPlatoon + " has less than 2 vehicles.");
                }
            }
        }
        return 0;
    }
}


class Property3 extends VanetProperty {

    // For each vehicle:  After joinPlatoon Always v.battery > 10 && v.distance > 0 until quitting platoon
    HashMap<Vehicle, Integer> forEachVehicle = new HashMap<Vehicle, Integer>();

    public double match(Road sut) throws PropertyFailedException {
        double ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicle.keySet().contains(v)) {
                forEachVehicle.put(v, 0);
            }
            switch (forEachVehicle.get(v)) {
                case 0:
                case 2:
                    if (v.myPlatoon != null) {
                        forEachVehicle.put(v, 1);
                    } else break;
                case 1:
                    if (v.myPlatoon == null) { 
                        // vehicle out of the platoon
                        forEachVehicle.put(v, 2);
                    } else if (v.autonomie < 10 || v.distance == 0) {
                        throw new PropertyFailedException(this, "Vehicle " + v.id + " has low autonomy or has reached destination.");
                    }
                    break;
            }
            // returns the minimal value of all 
            if (ret == -1) {
                ret = 2 - forEachVehicle.get(v);
            } else {
                int g = 2 - forEachVehicle.get(v);
                if (g < ret) {
                    ret = g;
                }
            }
        }
        return ret;
    }
}


class Property4 extends VanetProperty {

    // For each vehicle: After relay(v) always battery > 33 until vehicle downgraded
    HashMap<Vehicle, Integer> forEachVehicle = new HashMap<Vehicle, Integer>();

    public double match(Road sut) throws PropertyFailedException {
        double scoreV, ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicle.keySet().contains(v)) {
                forEachVehicle.put(v, 0);
            }
            switch (forEachVehicle.get(v)) {
                case 0: //how it initiates ? same behavior as case 2 ?
                case 2:
                    if (v.myPlatoon != null && v.myPlatoon.leader == v) {
                        forEachVehicle.put(v, 1);
                    } else break;
                case 1:
                    if (v.myPlatoon == null || v.myPlatoon.leader != v) {
                        // vehicle out of the platoon
                        forEachVehicle.put(v, 2);
                    } else if (v.autonomie < 33) {
                        throw new PropertyFailedException(this, "Vehicle " + v.id + " has a too low autonomy for being leader.");
                    }
                    break;
            }
            scoreV = 2 - forEachVehicle.get(v);
            // returns the minimal value of all
            ret = (ret == -1 || scoreV < ret) ? scoreV : ret;

        }
        return ret;
    }
}


class Property5 extends VanetProperty {

    // For each vehicle: Never refill when inPlatoon
    HashMap<Vehicle, Integer> forEachVehicle = new HashMap<Vehicle, Integer>();

    public double match(Road sut) throws PropertyFailedException {
        double scoreV, ret = -1;
        for (Vehicle v : sut) {
            if (!forEachVehicle.keySet().contains(v)) {
                forEachVehicle.put(v, 0);
            }
            switch (forEachVehicle.get(v)) {
                case 0: //not in platoon or just created
                    if (v.myPlatoon != null) {
                        forEachVehicle.put(v, 1);
                    }
                    break;
                case 1: //in platoon
                    if (v.myPlatoon == null) {
                        // vehicle out of the platoon
                        forEachVehicle.put(v, 2); //2 and not 0 because of the score ?
                    } else if (v.autonomie == 100) {
                        throw new PropertyFailedException(this, "Vehicle " + v.id + " should not refill while in platoon.");
                        // verify this property
                    }
                    break;
            }
            scoreV = 2 - forEachVehicle.get(v);
            // returns the minimal value of all
            ret = (ret == -1 || scoreV < ret) ? scoreV : ret;
        }
        return ret;
    }
}


//Property 6 no platoon with listNextLeader empty AND without leader
//property 7 distance > next station
