/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class VanetFSM implements FsmModel {

    /**
     * Automaton describing the FSM of a Cycab
     */
    ArrayList<Vehicle> vanet = new ArrayList<Vehicle>();
    
    public VanetFSM() {

    }

    public String getState() {
        return vanet.toString();
    }


    public void reset(boolean testing) {
        vanet.clear();
    }

    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return 0.95; }
    @Action
    public void tick() {
        for (Vehicle v : vanet) {
            v.tick();
        }
    }

    public boolean addVehicleGuard() { return vanet.size() < 5; }
    public double addVehicleProba() { return 0.02; }
    @Action
    public void addVehicle() {
        Vehicle v = new Vehicle(100, (int)(Math.random() * 5000) + 1000, UUID.randomUUID(), null);
        vanet.add(v);
    }


    public boolean requestJoinGuard() {
        if (vanet.size() < 2) {
            return false;
        }
        for (Vehicle v : vanet) {
            if (v.getPlatoon() == null) {
                return true;
            }
        }
        return false;
    }
    public double requestJoinProba() {
        return 0.03;
    }
    @Action
    public void requestJoin() {
        int start = (int)(Math.random() * vanet.size());
        for (int i=0; i < vanet.size(); i++) {
            int j = (i + start) % vanet.size();
            if (vanet.get(j).getPlatoon() == null) {
                int k = 0;
                do {
                    k = (int) (Math.random() * vanet.size());
                }
                while (k != j);
                vanet.get(j).join(vanet.get(k));
            }
        }
    }
    

}

