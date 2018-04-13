import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import static java.util.UUID.*;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 13/04/2018
 * Time: 14:33
 */
public class Road implements Iterable<Vehicle> {

    final static int MAX_CAPACITY = 5;

    ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();


    public void reset() {
        allVehicles.clear();
    }

    public int addVehicle(double _auto, double _distance) {
        allVehicles.add(new Vehicle(_auto, _distance, randomUUID(), null));
        System.out.println("Vehicle created at index " + (allVehicles.size() - 1));
        return allVehicles.size() - 1;
    }

    public boolean join(int i, int j) {
        if (i != j && allVehicles.get(i).getPlatoon() == null) {
            allVehicles.get(i).join(allVehicles.get(j));
            return allVehicles.get(i).getPlatoon() != null;
        }
        return false;
    }

    public void forceQuitPlatoon(int i) {
        if (allVehicles.get(i).getPlatoon() != null) {
            allVehicles.get(i).quitPlatoon();
        }
    }

    public boolean refill(int i) {
        if (allVehicles.get(i).getPlatoon() == null) {
            allVehicles.get(i).refill();
            return true;
        }
        return false;
    }


    public void tick() {
        for (Vehicle v : allVehicles) {
            v.tick();
        }
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
                v.getPlatoon().tick();
            }
        }
        affiche();
    }

    public void affiche() {
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
                v.getPlatoon().affiche();
            }
        }
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() == null) {
                System.out.print(v.getDisplayString() + " | ");
            }
        }
        System.out.println();
    }

    public Iterator<Vehicle> iterator() {
        return allVehicles.iterator();
    }

    public boolean isFull() {
        return !(allVehicles.size() < MAX_CAPACITY);
    }

    public int nbVehiclesOnRoad() {
        return allVehicles.size();
    }

    public Vehicle getVehicle(int j) {
        return allVehicles.get(j);
    }
}
