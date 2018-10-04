import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import static java.util.UUID.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 13/04/2018
 * Time: 14:33
 */
public class Road implements Serializable, Iterable<Vehicle> {
	boolean tick =false;
    final static int MAX_CAPACITY = 5;
	final static double FREQUENCYSTATION = 100;
    double distanceStation[] = {FREQUENCYSTATION, FREQUENCYSTATION}; 
    ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
    String writer = null;
    //String vehicleReader = null;
    String platoonReader = null;
    String roadReader = null;
    String writerLog ="";
    String vehicleLog="";
    String platoonLog="";
    //LinkedList<Element> lastReconfList = new LinkedList<Element>();
    public Road(String w, String vr, String pr, String rr, String wl) {
    	writer = w;
    	//vehicleReader=vr;
    	platoonReader=pr;
    	roadReader=rr;
    	writerLog=wl;


    }
    public String getStringWriter() {
    	return writer;
    }
    public void setStringWriter(String s) {
    	writer = s;
    }
    public void addStringWriter(String s) {
    	writer += s;
    }
    
    public String getStringWriterLog() {
    	return writer;
    }
    public void setStringWriterLog(String s) {
    	writer = s;
    }
    public void addStringWriterLog(String s) {
    	writerLog += s;
    }
    
    public void reset() {
        allVehicles.clear();
    }

    public int addVehicle(double _auto, double _distance) {
    	allVehicles.add(new Vehicle(_auto, _distance, randomUUID(), null,this));
    	System.out.println("Vehicle created at index " + (allVehicles.size() - 1));
		writer+="Vehicle created at index " + (allVehicles.size() - 1);
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
//why ?
//    public boolean refill(int i) {
//        if (allVehicles.get(i).getPlatoon() == null) {
//            allVehicles.get(i).refill();
//            return true;
//        }
//        return false;
//    }


    public void tick() {
    	tick=true;
        for (int i=0; i < allVehicles.size(); i++) {
            Vehicle v = allVehicles.get(i);
            v.updateVehicleVariables();
            vehicleLog += v.autonomie + ";"+ v.distance+";"+v.getStatus() + "\n";
            v.tick();
            if(v.distance <=10) {
                if(v.myPlatoon!=null) {
                    removeVehicle(v, "Error : vehicle: "+ v.id+ " reached distance but is inside platoon, removed anyway ");
                }
                else {
                    removeVehicle(v, "Event : vehicle: "+ v.id+ " reached distance and quitting successfully the road ");
                }
                i--;
            }
        }
        affiche();
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
                v.getPlatoon().tick();
            }
        }
        addStringWriterLog(vehicleLog + platoonLog);
        vehicleLog="";
        platoonLog="";
    }
    public void tickTrigger() {
    	for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
            	v.getPlatoon().tickTrigger();
            }
        }
    	if(tick) { 
    		updateDistStas(); 	
    		tick =false;
    	}
    }


    public void affiche() {
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
                v.getPlatoon().affiche();
            }
        }
        String x="";
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() == null) {
            	x=v.getDisplayString() + " | ";
                System.out.print(x);
        		writer+=x;
            }
        }
        System.out.println();
		writer+="\n \n";
    }

    public int removeVehicle(Vehicle v, String x) {
        allVehicles.remove(allVehicles.indexOf(v));
        System.out.println(x);
		writer+=x;
        return allVehicles.size() -1;
    }



    public void updateDistStas(){
		distanceStation[0]-=10;
		if(distanceStation[0]<=0) {
			if(distanceStation[0]<0) System.out.println("distance station negative, should not happen");
			distanceStation[0]=distanceStation[1];
			distanceStation[1] = FREQUENCYSTATION;
		}
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
	public double[] getDistanceStation() {
		return distanceStation;
	}
	public void setDistanceStation(double[] distanceStas) {
		distanceStation = distanceStas;
	}
}
