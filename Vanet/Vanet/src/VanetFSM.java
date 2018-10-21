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
    String writer = null;
    String vehicleReader =null;
    String platoonReader =null;
    String roadReader =null;
    String writerLog=null;
    ArrayList<Double> battery = new ArrayList<Double>();
    ArrayList<Double> distance = new ArrayList<Double>();
    ArrayList<Integer> indexjoined = new ArrayList<Integer>();
    ArrayList<Integer> indexKicked = new ArrayList<Integer>();
    public VanetFSM(String w, String vr, String pr, String rr, String wl) {
        writer = w;
        vehicleReader=vr;
        platoonReader=pr;
        roadReader=rr;
        writerLog=wl;
        sut = new Road(w,vr,pr,rr,wl);
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
    public void getValues() throws NumberFormatException, IOException {
    	FileReader vehicleReader = new FileReader("./outputVals.txt");
        BufferedReader br = new BufferedReader(vehicleReader);
        String sCurrentLine;
        String[] splits;
        sCurrentLine = br.readLine();
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Distance")) {
    		battery.add(Double.parseDouble(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("indexJoined")) {
        	System.out.println(sCurrentLine);
        	distance.add(Double.parseDouble(sCurrentLine));
        	sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("indexKicked")){
        	indexjoined.add((int)(Double.parseDouble(sCurrentLine)));
        	sCurrentLine = br.readLine();
        }
    	while ((sCurrentLine = br.readLine()) != null){
    		indexKicked.add((int)(Double.parseDouble(sCurrentLine)));
    	}       
    	vehicleReader.close();
//    	for(Double elt: distance) {
//    		System.out.println("dist : " +elt);
//    	}
    	for(Double elt: battery) {
    		System.out.println("bat : " +elt);
    	}
//    	for(Integer elt: indexjoined) {
//    		System.out.println("indexjoin : " +elt);
//    	}
//    	for(Integer elt: indexKicked) {
//    		System.out.println("indexKick : " +elt);
//    	}
    }
    
    public void printValues() throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writer = new PrintWriter("./outputVals.txt", "UTF-8");
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
    public double tickProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 : 0.87; }
    @Action
    public Object[] tick() {
        sut.tick();
        return new Object[]{ sut };
    }

    public boolean addVehicleGuard() { return ! sut.isFull(); }
    public double addVehicleProba() { return sut.nbVehiclesOnRoad() == 0 ? 1 : 0.05; }
    @Action
    public Object[] addVehicle() {
        double auto = battery.remove(0); 
        		//(int) (Math.random() * 10) + 20;
        //battery.add(auto);
        double dist = distance.remove(0); 
        		//(int)(Math.random() * 5000) + 1000;
        //distance.add(dist);
        sut.addVehicle(auto, dist);
        return new Object[]{ sut, auto, dist };
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
        while(( !(sut.getVehicle(k).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)) || k==j) &&k >=0) {
        	k--;
        }
        if(k>=0 ){
        	System.out.println("Join(" + j + ", " + k + ") -> " + sut.join(j, k));
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

