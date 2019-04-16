package SUT;
/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

import static java.util.UUID.randomUUID;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.server.UID;
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
    ArrayList<UUID> iD = new ArrayList<UUID>();
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
    public void getValues(ArrayList<UUID> _id, ArrayList<Double> bat,  ArrayList<Double> decBat, ArrayList<Double> dist, int nbSteps) throws NumberFormatException, IOException {//does not work as expected
    	int cpt=0;
    	while(cpt<nbSteps) {
    		iD.add(cpt,_id.get(cpt));
    		cpt++;
    	}
    	cpt=0;
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
    	System.out.println("print test values ");
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
    public Object[] tick(ArrayList<Object> empty) {

    	sut.tick();
        return new Object[]{ sut };
    }

    public boolean addVehicleGuard() { return ! sut.isFull(); }
    public double addVehicleProba() { return sut.nbVehiclesOnRoad() == 0 ? 1 : 0.05; } //0.05
    @Action
    public Object[] addVehicle(ArrayList<Object> params) {
    	addedVehicles++;
    	UUID id = randomUUID();//iD.remove(0);
    	double auto=0, dist=0, decAuto=0;
    	if(params==null) {
	    	auto = (Math.random() * 10) + 20;// battery.remove(0); 
	        		//(int) (Math.random() * 10) + 20;
	        //battery.add(auto);
	        dist = (Math.random() * 5000) + 1000;// distance.remove(0); 
	        		//(int)(Math.random() * 5000) + 1000;
	        //distance.add(dist);
	        decAuto = 1 + Math.random() / 5;//decBattery.remove(0);
	        
    	}
    	else {
    		id = (UUID) params.get(0);
    		auto = (double) params.get(1);
    		dist=(double) params.get(2);
    		decAuto=(double) params.get(3);
    	}
        sut.addVehicle(id,auto, dist, decAuto);
        System.out.println("AddVehicle("+id+","+auto+","+dist+","+decAuto+")");
        return new Object[]{sut, id, auto, dist, decAuto };
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
        return 0.07;
    }
    @Action
    public Object[] requestJoin(ArrayList<Object>vl) {// takes vehicle with most battery and last created vehicle
    	UUID tmpId= UUID.fromString( "00000000-0000-0000-0000-000000000000" );

    	if(vl==null) {
	        int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
	        for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
		        int j = (i + start) % sut.nbVehiclesOnRoad();
		        //int j = sut.getHighestVehicleBattery();
		//        int k=sut.allVehicles.size()-1;
		//        while(( !(sut.getVehicle(k).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)) || k==j) &&k >0) {
		//        	k--;
		//        }
		//        if(k>=0 ){
		//        	System.out.println("Join(" + j + ", " + k + ") -> " + sut.join(j, k));
		//        	//indexjoined.add(k);
		//        	return new Object[]{ sut, j, k };
		//        }
		        
		      if ((sut.getVehicle(j).getPlatoon() == null) && (sut.getVehicle(j).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION) )) {
		    	int k = 0;
		        do {
		            k = (int) (Math.random() * sut.nbVehiclesOnRoad());
		        }while (k == j);
		        
		    	//k= indexjoined.remove(0);
		        if(sut.getVehicle(k).getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)){
		        	
		        	System.out.println("Join(" + sut.getVehicle(j).id + ", " + sut.getVehicle(k).id + ") -> " + sut.join(j, k));
		        	//indexjoined.add(k);
		        	return new Object[]{ sut, sut.getVehicle(j).id, sut.getVehicle(k).id };
		        }
		      	}
	        }
	        System.out.println("Join( ) -> false");
	        	return new Object[]{sut ,UUID.fromString( "00000000-0000-0000-0000-000000000000" ) };// should not happen : except if vehicle did not found another vehicle
    	}  	
    	
    	//else if((UUID)vl.get(0)!=(UUID)tmpId ){
    	else if (vl.size() >1) {
    		if(vl.get(0)!= vl.get(1)) { //guard to improve with getminvalue
    			System.out.println("Join(" + vl.get(0) + ", " + vl.get(1) + ") -> " + sut.join(sut.findIndexOfId((UUID)vl.get(0)), sut.findIndexOfId((UUID)vl.get(1))));
	    		return new Object[]{ sut, vl.get(0), vl.get(1) };
    		}
    		else {
    			System.out.println("Join( ) -> false");
    	    	return new Object[]{sut, UUID.fromString( "00000000-0000-0000-0000-000000000000" )};
    		}
    	}
    	System.out.println("Join( ) -> false");
    	return new Object[]{sut, UUID.fromString( "00000000-0000-0000-0000-000000000000" )};// should not happen : except if vehicle did not found another vehicle
    }
    public boolean forceQuitPlatoonGuard() {
        for (Vehicle v : sut) {
            if (v.getPlatoon() != null) {
                return true;
            }
        }
        return false;
    }
    public double forceQuitPlatoonProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 :0.01;} //0.05; }
    @Action
    public Object[] forceQuitPlatoon(ArrayList<Object> vl) {
    	System.out.println("force quit done");
    	if(vl==null) {
        //int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        //for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
        	int j = sut.getLowestVehicleBattery();
            //int j = (i + start) % sut.nbVehiclesOnRoad();
            //int j = indexKicked.remove(0);
        	//if (sut.getVehicle(j).getPlatoon() != null) {
                sut.forceQuitPlatoon(j);
                //indexKicked.add(j);
                System.out.println("ForceQuit("+sut.getVehicle(j).id+")");
                return new Object[]{sut, sut.getVehicle(j).id};
            //}
        //}
       // return new Object[]{ sut }; // should not happen
    	}
    	else {
    		System.out.println("ForceQuit("+vl.get(0)+")");
    		sut.forceQuitPlatoon((UUID)vl.get(0));
    		return new Object[]{sut, vl};
    	}
    }

}

