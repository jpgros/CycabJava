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
    ArrayList<UUID> iDPl = new ArrayList<UUID>();
    ArrayList<Double> roadStations = new ArrayList<Double>();
    ArrayList<Double> speed = new ArrayList<Double>();
    ArrayList<Double> position = new ArrayList<Double>();
    ArrayList<Double> battery = new ArrayList<Double>();
    ArrayList<Double> decBattery = new ArrayList<Double>();
    ArrayList<Double> distance = new ArrayList<Double>();
    ArrayList<Integer> indexjoined = new ArrayList<Integer>();
    ArrayList<Integer> indexKicked = new ArrayList<Integer>();
    int nbVehicleOnRoad;
    int nbPlatoonOnRoad;
    ArrayList<Integer> nbVehiclesInPlatoonsList = new ArrayList<Integer>();
    
    public VanetFSM(String w, String wl, String rc,String rcr ,Mutant m, LogLevel logL) {
        writer = w;
        writerLog=wl;
        reconfChoosen=rc;
        mutant=m;
        reconfChoosenReader=rcr;
        sut = new Road(w,wl,rc,rcr,m,logL);
        try {
			getTestValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//    public void getValues(ArrayList<UUID> _id, ArrayList<Double> bat,  ArrayList<Double> decBat, ArrayList<Double> dist,ArrayList<UUID> idpl, int nbSteps) throws NumberFormatException, IOException {//does not work as expected
//    	int cpt=0;
//    	while(cpt<nbSteps) {
//    		iD.add(cpt,_id.get(cpt));
//    		cpt++;
//    	}
//    	cpt=0;
//    	while(cpt<nbSteps) {
//    		battery.add(cpt,bat.get(cpt));
//    		cpt++;
//    	}
////    	for(int i=0; i< 100; i++) {
////    		System.out.println("added battery "+ battery + "\n existing battery " +bat.get(i));
////    	}
//    	cpt=0;
//    	while(cpt<nbSteps) {
//    		decBattery.add(cpt,decBat.get(cpt));
//    		cpt++;
//    	}
//    	cpt=0;
//    	while(cpt<nbSteps) {
//    		distance.add(cpt,dist.get(cpt));
//    		cpt++;
//    	}
//    	cpt=0;
//    	while(cpt<nbSteps) {
//    		iDPl.add(cpt,idpl.get(cpt));
//    		cpt++;
//    	}
//    	System.out.println("sizes fsm "+ iD.size() + " "+ distance.size());
//    }
    public void afficheTestValues() {
    	System.out.println("print test values ");
    	System.out.println(battery);
    	System.out.println(decBattery);
    	System.out.println(distance);
    }
//    public void printValues() throws FileNotFoundException, UnsupportedEncodingException {
//    	PrintWriter writerFile = new PrintWriter("./outputVals.txt", "UTF-8");
//        LogPrinter writer = new LogPrinter(writerFile, LogLevel.INFO, LogLevel.ERROR);
//    	writer.println("Battery");
//    	for(double bat : battery) {
//    		writer.println(bat);
//    	}
//    	writer.println("Distance");
//    	for(double i : distance) {
//    		writer.println(i);
//    	}
//    	writer.println("indexJoined");
//    	for(double i : indexjoined) {
//    		writer.println(i);
//    	}
//    	writer.println("indexKicked");
//    	for(double i : indexKicked) {
//    		writer.println(i);
//    	}
//    	writer.close();
//    }
    
    public void initSystem(){
    	//int =100; //1600 vl on a 50k step test
    	//int =8;
    	int nbVehicleInPlatoon=4;
    	//System.out.println("nbvl "+ nbVl + "nbpl  " + nbPl + " nbvlinpl " + nbVehiclesInPlatoonsList);
		for(double posStas:roadStations) {
			sut.stationPositions.add(posStas);
		}
    	for(int i=0; i<nbVehicleOnRoad;i++) {
    		sut.addVehicle(iD.remove(0),this.battery.remove(0), this.distance.remove(0), this.decBattery.remove(0),this.position.remove(0),this.speed.remove(0));
    	}
    	int cpt=0;
    	int i=0;
    	while(cpt<nbPlatoonOnRoad) {
    		for(int j=i+1; j<(i+nbVehiclesInPlatoonsList.get(cpt)); j++) {	//platoon of 3 vls
	    		if(i==j) {
	    			j++;
	    		}
	    		System.out.println("Join(" + sut.allVehicles.get(i).id + ", " + sut.allVehicles.get(j).id + ") -> " + sut.join(j, i));
    		}
    		i=i+nbVehiclesInPlatoonsList.get(cpt);
    		cpt++;
    		//if(cpt>=nbVehiclesInPlatoonsList.size()) break; //sale
    	}
//		for(int i=0;i<(nbPlatoonOnRoad*nbVehicleInPlatoon);i=i+nbVehicleInPlatoon) { //forming 3 plts
//    		for(int j=i+1; j<(i+nbVehicleInPlatoon); j++) {	//platoon of 3 vls
//    			if(i==j)j++;
//    	       	System.out.println("Join(" + sut.allVehicles.get(i).id + ", " + sut.allVehicles.get(j).id + ") -> " + sut.join(j, i));
//    		}
//    	}
    }
    
    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 : 0.995; } //0.87
    @Action
    public Object[] tick(ArrayList<Object> empty) {
    	//replace tick by request join if a vehicule is available to join
    	int index = sut.containsVehicleToJoin();
    	if(index!=-1 && sut.stepNb%20==0) {

    		ArrayList<Object> paramList = new ArrayList<Object>();
    		paramList.add(sut.allVehicles.get(index));
    		if ((sut.getVehicle(index).getPlatoon() == null) && !(sut.getVehicle(index).isTakingNextStation())) { //
    			int k;
    			do {
		            k = (int) (Math.random() * sut.nbVehiclesOnRoad());
		        }while (k == index);
		        
		    	//k= indexjoined.remove(0);
		        if(!sut.getVehicle(k).isTakingNextStation() && sut.getVehicle(k).autonomie<99 && sut.getVehicle(index).autonomie<99){
		        	//if autonomie equals 100 its means vl is inside pl
		        	System.out.println("Join(" + sut.getVehicle(index).id + ", " + sut.getVehicle(k).id + ") -> " + sut.join(index, k) +" -> true");
		        	//indexjoined.add(k);
		    		sut.tick=false;
		        	return new Object[]{ sut};//, index, k };
		        }
		        try {
		        	sut.tick();
		        }catch (BehaviorException e) {
		        	System.out.println("FAIL");
				}
		        return new Object[]{ sut };
        		
    		}
    		try {
	        	sut.tick();
	        }catch (BehaviorException e) {
	        	System.out.println("FAIL");
			}
    		//requestJoin(paramList); //TODO maybe specify the two vehicles to join ie best bat of solo vl and a random pl
    	}
    	else {
    		try {
	        	sut.tick();
	        }catch (BehaviorException e) {
	        	System.out.println("FAIL");
			}
    	}
    	//sut.tick();
        return new Object[]{ sut };
    }

    public boolean addVehicleGuard() { return ! sut.isFull(); }
   // public double addVehicleProba() { return sut.nbVehiclesOnRoad() == 0 ? 1 : 0.05; } //0.05
    public double addVehicleProba() { return 0;}
    @Action
    public Object[] addVehicle(ArrayList<Object> params) {
    	addedVehicles++;
    	UUID id = randomUUID();//iD.remove(0);
    	double auto=0, dist=0, decAuto=0;
    	int position=0, speed=0;
    	if(params.size()<=0) {
	    	auto = (Math.random() * 10) + 20;// battery.remove(0); 
	        		//(int) (Math.random() * 10) + 20;
	        //battery.add(auto);
	        dist = (Math.random() * 5000) + 1000;// distance.remove(0); 
	        		//(int)(Math.random() * 5000) + 1000;
	        //distance.add(dist);
	        decAuto = 1 + Math.random() / 5;//decBattery.remove(0);
	        position=(int) ((Math.random() * 40)+5);
	        speed=(int) ((Math.random() * 10)+5);
    	}
    	else if (params.size()==1) {
    		auto = (Math.random() * 10) + 20;// battery.remove(0); 
    		//(int) (Math.random() * 10) + 20;
		    //battery.add(auto);
		    dist = (Math.random() * 5000) + 1000;// distance.remove(0); 
		    		//(int)(Math.random() * 5000) + 1000;
		    //distance.add(dist);
		    decAuto = 1 + Math.random() / 5;//decBattery.remove(0);
		    position=(int) ((Math.random() * 40)+5);
	        speed=(int) ((Math.random() * 10)+5);
    	}
    	else {
    		id = (UUID) params.get(0);
    		auto = (double) params.get(1);
    		dist=(double) params.get(2);
    		decAuto=(double) params.get(3);
    		position=(int)params.get(4);
 	        speed=position=(int)params.get(5);
    	}
        sut.addVehicle(id,auto, dist, decAuto, position,speed);
        System.out.println("AddVehicle("+id+","+auto+","+dist+","+decAuto+")");
        return new Object[]{sut, id, auto, dist, decAuto, position, speed };
    }

    public void tickTrigger(){
    	sut.tickTrigger();
    }
    public boolean requestJoinGuard() {
        if (sut.nbVehiclesOnRoad() < 2) {
            return false;
        }
        return true; //remove last condition to let merging of platoons
        /*for (Vehicle v : sut) {
            if (v.getPlatoon() == null) {
                return true;
            }
        }
        return false;*/
    }
    public void getTestValues() throws IOException {
    	FileReader vehicleReader = new FileReader("./outputVals.txt");
        BufferedReader br = new BufferedReader(vehicleReader);
        String sCurrentLine;
        
        sCurrentLine = br.readLine();
        nbVehicleOnRoad=Integer.parseInt(sCurrentLine);
        sCurrentLine = br.readLine();
        nbPlatoonOnRoad=Integer.parseInt(sCurrentLine);
        sCurrentLine = br.readLine();
        for(int i=0;i<nbPlatoonOnRoad;i++) {
        	sCurrentLine = br.readLine();
            nbVehiclesInPlatoonsList.add(Integer.parseInt(sCurrentLine));
        }
        sCurrentLine = br.readLine();
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Battery")) {
    		iD.add(UUID.fromString(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("DecAuto")) {
    		battery.add(Double.parseDouble(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Distance")) {
    		decBattery.add(Double.parseDouble(sCurrentLine));
    		sCurrentLine = br.readLine();
    	}
        sCurrentLine = br.readLine();
        while (!sCurrentLine.contains("IdPl")){
        	distance.add(Double.parseDouble(sCurrentLine));
        	sCurrentLine = br.readLine();
        }
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Position")){ 
        	iDPl.add(UUID.fromString(sCurrentLine));
        	sCurrentLine = br.readLine();
        }
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("Speed")){ 
        	position.add(Double.parseDouble(sCurrentLine));
        	sCurrentLine = br.readLine();
        }
        sCurrentLine = br.readLine();
        while(!sCurrentLine.contains("StationPositions")){ 
        	speed.add(Double.parseDouble(sCurrentLine));
        	sCurrentLine = br.readLine();
        } 
        sCurrentLine = br.readLine();
        while((sCurrentLine = br.readLine()) != null){
        	roadStations.add(Double.parseDouble(sCurrentLine));
        	sCurrentLine = br.readLine();
        }
        System.out.println("sizes "+ iD.size() + " "+ battery.size()+" "+ decBattery.size()+ " " + distance.size());
        br.close();
    }
    public double requestJoinProba() {
        return 0.0; //0.075 0.12
    }
    @Action
    public Object[] requestJoin(ArrayList<Object>vl) {// takes vehicle with most battery and last created vehicle
    	   	if(vl.size()<=0) {
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
		        
		      if ((sut.getVehicle(j).getPlatoon() == null) && (!sut.getVehicle(j).isTakingNextStation())) {// getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION) )) {
		    	int k = 0;
		        do {
		            k = (int) (Math.random() * sut.nbVehiclesOnRoad());
		        }while (k == j);
		        
		    	//k= indexjoined.remove(0);
		        if(!sut.getVehicle(k).isTakingNextStation()) {// getMinValue() > (sut.distanceStation[0] + sut.distanceStation[1]+sut.FREQUENCYSTATION)){
		        	System.out.println("Join(" + sut.getVehicle(j).id + ", " + sut.getVehicle(k).id + ") -> " + sut.join(j, k) +" -> true");
		        	//indexjoined.add(k);
		        	return new Object[]{ sut, j, k };
		        }
		      	}
	        }
	        System.out.println("did not found another vehicle Join( ) -> false");
        	return new Object[]{sut ,-1 };// should not happen : except if vehicle did not found another vehicle
    	}  	
    	   	else if(vl.size() ==1) {
    	   		System.out.println(" size 1 Join( ) -> false" + vl.toString());
    	    	return new Object[]{sut, -1 };// should not happen : except if vehicle did not found another vehicle
    	   	}
    	//else if((UUID)vl.get(0)!=(UUID)tmpId ){
    	else {
    		if(vl.get(0)!= vl.get(1)) { //guard to improve with getminvalue
//    			
	    		return new Object[]{ sut, vl.get(0), vl.get(1) };
    		}
    		else {
    			System.out.println("retrieve join not possible Join( ) -> false");
    	    	return new Object[]{sut,-1};
    		}
    	}
    	
    }
    public boolean forceQuitPlatoonGuard() {
        for (Vehicle v : sut) {
            if (v.getPlatoon() != null) {
                return true;
            }
        }
        return false;
    }
    public double forceQuitPlatoonProba() { return sut.nbVehiclesOnRoad() == 0 ? 0 :0.005;} //0.05; }
    @Action
    public Object[] forceQuitPlatoon(ArrayList<Object> vl) {
    	if(vl.size()<=0) {
        //int start = (int)(Math.random() * sut.nbVehiclesOnRoad());
        //for (int i=0; i < sut.nbVehiclesOnRoad(); i++) {
        	int j = sut.getLowestVehicleBatteryInPlatoon();
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
    		try {
    			sut.forceQuitPlatoon((UUID)vl.get(0));
    		}
    		catch (ArrayIndexOutOfBoundsException e) {
    			int j = sut.getLowestVehicleBatteryInPlatoon();                
                sut.forceQuitPlatoon(j);                    
                System.out.println("ForceQuit("+sut.getVehicle(j).id+")");
                return new Object[]{sut, sut.getVehicle(j).id};
    		}
    		return new Object[]{sut, vl};
    	}
    }

}

