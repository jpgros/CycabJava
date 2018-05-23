/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

public class VanetFSM implements FsmModel {

    /**
     * Automaton describing the FSM of a Cycab
     */
    Road sut;
    PrintWriter writer = null;
    FileReader vehicleReader =null;
    FileReader platoonReader =null;
    FileReader roadReader =null;
    
    public VanetFSM(PrintWriter w, FileReader vr, FileReader pr, FileReader rr) {
        sut = new Road(w,vr,pr,rr);
        writer = w;
        vehicleReader=vr;
        platoonReader=pr;
        roadReader=rr;
    }

    public String getState() {
        return sut.toString();
    }

    public Road getSUT() {
        return sut;
    }

    public void reset(boolean testing) {
        sut.reset();
    }

    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 : 0.87; }
    @Action
    public Object[] tick(PrintWriter writer) {
        sut.tick();
        return new Object[]{ sut };
    }

    public boolean addVehicleGuard() { return ! sut.isFull(); }
    public double addVehicleProba() { return sut.nbVehiclesOnRoad() == 0 ? 1 : 0.05; }
    @Action
    public Object[] addVehicle() {
        int auto = (int) (Math.random() * 10) + 20;
        int dist = (int)(Math.random() * 5000) + 1000;
        sut.addVehicle(auto, dist);
        return new Object[]{ sut, auto, dist };
    }


    public boolean requestJoinGuard() {
        if (sut.nbVehiclesOnRoad() < 2) {
            return false;
        }
        for (Vehicle v : sut) {
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
    public Object[] requestJoin() {
        int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
            int j = (i + start) % sut.nbVehiclesOnRoad();
            if (sut.getVehicle(j).getPlatoon() == null) {
                int k = 0;
                do {
                    k = (int) (Math.random() * sut.nbVehiclesOnRoad());
                }
                while (k == j);
                System.out.println("Join(" + j + ", " + k + ") -> " + sut.join(j, k));
                return new Object[]{ sut, j, k };
            }
        }
        return new Object[]{ sut }; // should not happen
    }



    public boolean forceQuitPlatoonGuard() {
        for (Vehicle v : sut) {
            if (v.getPlatoon() != null) {
                return true;
            }
        }
        return false;
    }
    public double forceQuitPlatoonProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 : 0.05; }
    @Action
    public Object[] forceQuitPlatoon() {
        int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
            int j = (i + start) % sut.nbVehiclesOnRoad();
            if (sut.getVehicle(j).getPlatoon() != null) {
                sut.forceQuitPlatoon(j);
                return new Object[]{sut, j};
            }
        }
        return new Object[]{ sut }; // should not happen
    }
}

