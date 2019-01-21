/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import com.sun.tools.internal.ws.resources.GeneratorMessages;

public class VanetFSM implements FsmModel {

    /**
     * Automaton describing the FSM of a Cycab
     */
    Road sut;
    int addedVehicles=0;
    String writer = null;
    String vehicleReader =null;
    String platoonReader =null;
    String roadReader =null;
    String writerLog=null;
    String reconfChoosen="";
    Mutant mutant;
    String reconfChoosenReader;
    ArrayList<Double> battery = new ArrayList<Double>();
    ArrayList<Double> decBattery = new ArrayList<Double>();
    ArrayList<Double> distance = new ArrayList<Double>();
    ArrayList<Integer> indexjoined = new ArrayList<Integer>();
    ArrayList<Integer> indexKicked = new ArrayList<Integer>();
    public VanetFSM(String w, String wl, String rc,String rcr ,Mutant m, LogLevel logL) {
        writer = w;
        writerLog=wl;
        reconfChoosen=rc;
        mutant=m;
        reconfChoosenReader=rcr;
        sut = new Road(w,wl,rc,rcr,m,logL);
    }
    public String getStringWriter() {
    	return sut.getStringWriter();
    }
    public void addStringWriter(String s) {
    	sut.setStringWriter(sut.getStringWriter()+ s);
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
    public void getValues(ArrayList<Double> bat,  ArrayList<Double> decBat, ArrayList<Double> dist, int nbSteps) throws NumberFormatException, IOException {//does not work as expected
    	int cpt=0;
    	while(cpt<nbSteps) {
    		battery.add(cpt,bat.get(cpt));
    		cpt++;
    	}
//    	for(int i=0; i< 100; i++) {
//    		System.out.println("added battery "+ battery + "\n existing battery " +bat.get(i));
//    	}
    	cpt=0;
    	while(cpt<nbSteps) {
    		decBattery.add(cpt,decBat.get(cpt));
    		cpt++;
    	}
    	cpt=0;
    	while(cpt<nbSteps) {
    		distance.add(cpt,dist.get(cpt));
    		cpt++;
    	}
    }
    public void afficheTestValues() {
    	System.out.println(battery);
    	System.out.println(decBattery);
    	System.out.println(distance);
    }
    public void printValues() throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writerFile = new PrintWriter("./outputVals.txt", "UTF-8");
        LogPrinter writer = new LogPrinter(writerFile, LogLevel.INFO, LogLevel.ERROR);
    	writer.println("Battery");
    	for(double bat : battery) {
    		writer.println(bat);
    	}
    	writer.println("Distance");
    	for(double i : distance) {
    		writer.println(i);
    	}
    	writer.println("indexJoined");
    	for(double i : indexjoined) {
    		writer.println(i);
    	}
    	writer.println("indexKicked");
    	for(double i : indexKicked) {
    		writer.println(i);
    	}
    	writer.close();
    }
    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 : 0.87; } //0.87
    @Action
    public Object[] tick() {
    	//long startTime = System.currentTimeMillis();
        sut.tick();
        //long estimatedTime = (System.currentTimeMillis() - startTime);
        //System.out.println("tick elapsed time  " + estimatedTime + "miliseconds");
        return new Object[]{ sut };
    }

    public boolean addVehicleGuard() { return ! sut.isFull(); }
    public double addVehicleProba() { return sut.nbVehiclesOnRoad() == 0 ? 1 : 0.05; } //0.05
    @Action
    public Object[] addVehicle() {
    	addedVehicles++;
        double auto = battery.remove(0); 
        		//(int) (Math.random() * 10) + 20;
        //battery.add(auto);
        double dist = distance.remove(0); 
        		//(int)(Math.random() * 5000) + 1000;
        //distance.add(dist);
        double decAuto = decBattery.remove(0);
        sut.addVehicle(auto, dist, decAuto);
        return new Object[]{ sut, auto, dist, decAuto };
    }

    public void tickTrigger(){
    	sut.tickTrigger();
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
    public Object[] requestJoin() {// takes vehicle with most battery and last created vehicle
        //int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        //for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
          //  int j = (i + start) % sut.nbVehiclesOnRoad();
        int j = sut.getHighestVehicleBattery();
        int k=sut.allVehicles.size()-1;
        while(( !(sut.getVehicle(k).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)) || k==j) &&k >0) {
        	k--;
        }
        if(k>=0 ){
        	System.out.println("Join(" + j + ", " + k + ") -> " + sut.join(j, k)+ " vehicle one auto"+ sut.allVehicles.get(j).autonomie);
        	//indexjoined.add(k);
        	return new Object[]{ sut, j, k };
        }
        else {
        	return new Object[]{ sut };
        }
//            if ((sut.getVehicle(j).getPlatoon() == null) && (sut.getVehicle(j).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION) )) {
//            	int k = 0;
//               /* do {
//                    k = (int) (Math.random() * sut.nbVehiclesOnRoad());
//                }*/
//            	k= indexjoined.remove(0);
//                while (k == j);
//                if(sut.getVehicle(k).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)){
//                	System.out.println("Join(" + j + ", " + k + ") -> " + sut.join(j, k));
//                	//indexjoined.add(k);
//                	return new Object[]{ sut, j, k };
//                }
//            }
        //}
//        return new Object[]{ sut }; // should not happen : except if vehicle did not found another vehicle
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
        //int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        //for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
        	int j = sut.getLowestVehicleBattery();
            //int j = (i + start) % sut.nbVehiclesOnRoad();
            //int j = indexKicked.remove(0);
        	//if (sut.getVehicle(j).getPlatoon() != null) {
                sut.forceQuitPlatoon(j);
                //indexKicked.add(j);
                return new Object[]{sut, j};
            //}
        //}
       // return new Object[]{ sut }; // should not happen
    }

}

