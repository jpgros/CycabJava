package SUT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import com.sun.tools.hat.internal.parser.Reader;

import static java.util.UUID.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 13/04/2018
 * Time: 14:33
 */
public class Road implements Serializable, Iterable<Vehicle> {
	boolean tick =false;
	int stepNb=0;
    final static int MAX_CAPACITY = 15;
	final static double FREQUENCYSTATION = 100;
    //double distanceStation[] = {FREQUENCYSTATION, FREQUENCYSTATION}; 
	ArrayList<Double> stationPositions = new ArrayList<Double>();
    ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
    ArrayList<StepElt> steps = new ArrayList<StepElt>();
    
    int numberPlatoon=0;
    double globalConso=0;
    int timePlatoon=0;
    int numberQuitPlatoon=0;
    Mutant mutant;
    String writer = "";
    //String vehicleReader = null;
    String writerLog ="";
    String component="";
    String binding="";
    String state="";
    String action="";
    String stepName="";
    String externalEvent="";
    String reconfigurationChoosen="";
    String reconfChoosenRead="";
    String reconfChoosenWrite="";
    transient BufferedReader reader;
    double k[];
    LogLevel logLevel;
    //LinkedList<Element> lastReconfList = new LinkedList<Element>();
    public Road(String w, String wl, String rc,String rcr,Mutant m, LogLevel lg) {
    	writer = w;
    	writerLog=wl;
    	reconfigurationChoosen=rc;
    	mutant=m;
    	reconfChoosenRead=rcr;
    	reader = new BufferedReader(new StringReader(rcr));
    	
    }
    public void reinit() { //reinit variables in case of a new test 
//        this.distanceStation[0] = FREQUENCYSTATION;
//        this.distanceStation[1] = FREQUENCYSTATION; 
        this.allVehicles = new ArrayList<Vehicle>();
        this.numberPlatoon=0;
        this.globalConso=0;
        this.timePlatoon=0;
        this.numberQuitPlatoon=0;
    }
    public void cleanRoad() {
//        this.distanceStation[0] = FREQUENCYSTATION;
//        this.distanceStation[1] = FREQUENCYSTATION; 
    	for(Vehicle v :allVehicles) {
    		v.cleanVl();
    	}
    	allVehicles.clear();
    	steps.clear();
    	timePlatoon=0;
    	this.numberQuitPlatoon=0;
    	globalConso=0;
    	numberPlatoon=0;
        writerLog ="";
        component="";
        binding="";
        state="";
        action="";
        stepName="";
        externalEvent="";
        reconfigurationChoosen="";
        reconfChoosenRead="";
        reconfChoosenWrite="";
    	
    }/** Say if a vehicle on road answers the requirement to merge
    @return index of vehicle adequate for merging
    */
    public int containsVehicleToJoin() {
    	int index =-1;
    	double maxBat=0;
    	for(int i=0; i<allVehicles.size();i++) {
    		if(allVehicles.get(i).autonomie>80&& allVehicles.get(i).autonomie>maxBat && allVehicles.get(i).autonomie<99) { // if autonomy is 100 means that vl is inside station
    			index=i;
    			maxBat= allVehicles.get(i).autonomie;
    		}
    	}
    	return index;
    }
    public void initCoeffRules(int nbRules) {
    	k= new double [nbRules];
    }
    public void setCoeffRules(ArrayList<Integer> list) {
    	double[] tab= {3,5,7};
    	for(int i =0; i<list.size(); i++) {
    		k[i]= 0;//list.get(i);
    	}
    }
    public String getLineReconfChoosenRead() throws IOException {
    	return reader.readLine();
    }
    public String getReconfChoosenRead() {
    	return reconfChoosenRead;
    }
    public void setStepName(String s) {
    	stepName=s;
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
    	return writerLog;
    }
    public void setStringWriterLog(String s) {
    	writerLog = s;
    }
    public void addStringWriterLog(String s) {
    	writerLog += s;
    }
    public void setReconfigurationChoosen(String s) {
    	reconfigurationChoosen = s;
    }
    public void addReconfigurationChoosen(String s) {
    	reconfigurationChoosen += s;
    }
    public String getReconfigurationChoosen() {
    	return reconfigurationChoosen;
    }
    public void reset() {
        allVehicles.clear();
    }
    public void addReconfChoosenWrite(String s){
    	reconfChoosenWrite+=s;    	
    }
    public String getReconfigurationChoosenWrite() {
    	return reconfChoosenWrite;
    }
    public double getGlobalConso() {
    	return globalConso;
    }
    public double getGlobalTimePLatooned() {
    	return timePlatoon;
    }
    public int getGlobalNumberPlatoonQuit(){
    	return numberQuitPlatoon;
    }
    public int addVehicle(UUID _id, double _auto, double _distance, double decAuto, double position, double speed) {
    	allVehicles.add(new Vehicle(_auto, _distance, _id, null,this, decAuto,position,speed));
    	//System.out.println("Vehicle created at index " + (allVehicles.size() - 1));
		writer+="Vehicle created at index " + (allVehicles.size() - 1);
        return allVehicles.size() - 1;
    }
//    public String join(int i, int j) {
////    	if (i != j && allVehicles.get(i).getPlatoon() == null) {	
////            return "aa"+String.valueOf(allVehicles.get(i).join(allVehicles.get(j)))+ String.valueOf(allVehicles.get(i).getPlatoon() != null);
////        }
//    	if (i != j && allVehicles.get(j).getPlatoon() == null) {	
//            return "bb"+String.valueOf(allVehicles.get(j).join(allVehicles.get(i))) +  String.valueOf(allVehicles.get(j).getPlatoon() != null);
//        }
//    	else if(allVehicles.get(j).getPlatoon() != null && allVehicles.get(i).getPlatoon() != null) {
//    		System.out.println("ok merge cond");
//    		allVehicles.get(i).getPlatoon().mergePlatoons(allVehicles.get(j).getPlatoon());
//    	}
//        return "falsetr";
//    }
    
    public boolean join(int i, int j) {
    	double number = Math.random();
//    	System.out.println("i = "+ i + "j= " + j);
//    	System.out.println(" vl i " + allVehicles.get(i).myPlatoon);
//    	System.out.println(" vl i auto" + allVehicles.get(i).autonomie);
        if (i != j ) {//&& number >0.8) { // && allVehicles.get(i).getPlatoon() == null) {  removed condition to make possible platoon merging
            return allVehicles.get(i).join(allVehicles.get(j))&& allVehicles.get(i).getPlatoon() != null;
        }
        return false;
    }

    public void forceQuitPlatoon(int i) {
        if (allVehicles.get(i).getPlatoon() != null) {
            allVehicles.get(i).quitPlatoon();
        }
    }
    public void forceQuitPlatoon(UUID id) {
    	//System.out.println("toto concerned " + id + " allvehicle size " + allVehicles.size());
//    	for(Vehicle v :allVehicles) {
//    		System.out.println(v.id);
//    	}
    	//System.out.println("vl concerned " + allVehicles.get(findIndexOfId(id)));
    	//System.out.println("pl concerned " + allVehicles.get(findIndexOfId(id)).getPlatoon());
        if (allVehicles.get(findIndexOfId(id)).getPlatoon() != null) {
            allVehicles.get(findIndexOfId(id)).quitPlatoon();
        }
    }
    public int findIndexOfId(UUID id) {
    	for(int i=0; i < allVehicles.size();i++) {
    		if(allVehicles.get(i).id == id) return i;
    	}
    	return -1;
    }

    public void tick() throws BehaviorException{
    	stepNb++;
    	tick=true;
    	component="Components(";
    	binding="Bindings(";
    	state="State(";
    	action="Actions(";
        for (int i=0; i < allVehicles.size(); i++) {
            Vehicle v = allVehicles.get(i);
            v.updateVehicleDistance();
            v.updateVehiclePosition();
            globalConso+=v.updatevehicleAutonomie();
            if(logLevel==LogLevel.VERBOSE) {
	            component +="Vehicle:"+v.getId();
	            state+= "Vehicle:"+v.getId() +"->remaining distance:"+ v.distance+";";
	            state+= "Vehicle:"+v.getId() +"->remaining automony:"+v.autonomie+";";
	            state+= "Vehicle:"+v.getId() +"->status:"+v.getStatus()+";";
            }
            v.tick();
            if(v.myPlatoon!=null && !v.isLeader()) { // is vehicle is in solo mode or leader it does not count has platooned time
            	timePlatoon++;
            	if(v.isLeader()) {
            		component +="Platoon:"+v.myPlatoon+"\n";
            	}
            	binding+= "Platoon:"+v.myPlatoon+"->Vehicle:"+v.getId()+"\n";
            	state +="\n"+"Platoon:"+v.myPlatoon+ "->number vehicles:" + v.myPlatoon.vehiclesList.size();
            }
            if(v.distance <=10) {
                if(v.myPlatoon!=null) { //not sure if this case should be taken care of or raise an eception
                    throw new BehaviorException("Vehicle " + v.id + " reached distance but is inside platoon");
                	//v.myPlatoon.deleteVehicle(v);
                    //removeVehicle(v, "Error : vehicle: "+ v.id+ " reached distance but is inside platoon, removed anyway ");
                }
                else{
                	action +="Vehicle:"+v.getId() +"quitRoad";
                    removeVehicle(v, "Event : vehicle: "+ v.id+ " reached distance and quitting successfully the road ");
                }
                i--;
            }
            state+="\n";
        }
        if(logLevel==LogLevel.VERBOSE) {
   	 		affiche();
        } 	
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
        		v.getPlatoon().tick();
        		if(v.myPlatoon.lastReconf!=null) action += v.myPlatoon.lastReconf;
            }
        }
		StepElt step = new StepElt();
        step.pair = new Pair(new ArrayList<Element>(), new ArrayList<Element>());
		step.step=stepNb;
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
        		for(Element elt : v.getPlatoon().step.pair.first) {
        			step.pair.first.add(elt);
        		}
        		if(v.getPlatoon().step.pair.second.size()>0) {
	        		step.pair.second.add(v.getPlatoon().step.pair.second.get(0));
	        		v.getPlatoon().clearStep();
        		}
            }
        }
        steps.add(step);
        if(logLevel==LogLevel.VERBOSE) {
	    	component+=")\n";
	    	binding+=")\n";
	    	state = state.substring(0, state.length()-1); 
	    	state+=")\n";
	    	action+=")\n";
	    	this.addStringWriterLog("Tick " + stepNb +"\n");
	    	this.addStringWriterLog("Vehicles on road " +allVehicles.size()+"\n");
	    	this.addStringWriterLog("Platoons on road " +numberPlatoon+"\n");
	    	this.addStringWriterLog(component);
	    	this.addStringWriterLog(binding);
	    	this.addStringWriterLog(state);
	    	this.addStringWriterLog(action);
	    	this.addStringWriterLog(externalEvent);
	    	this.addStringWriterLog("\n");
        }
//        roadLog += "Station;"+distanceStation[0]+";"+stepName+"\n";
//        addStringWriterLog(roadLog+ platoonLog+ vehicleLog );
//        vehicleLog="";
//        platoonLog="";
//        roadLog="";
    }
    public void tickTrigger() {
//    	if(!tick) {
//        	for (Vehicle v : allVehicles) {
//               v.updatevehicleAutonomie();
//            }
//    	}
    	for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
            	v.getPlatoon().tickTrigger();
            }
        }
    	if(tick) { 
    		//updateDistStas(); 	
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
                //System.out.print(x);
        		writer+=x;
            }
        }
        //System.out.println();
		writer+="\n \n";
    }
    /**
     * Remove vehicle from road and suppress it from lsit of all vehicle
     * @param v the vehicle to remove
     * @param x
     * @return the new size of vehicle list
     */
    public int removeVehicle(Vehicle v, String x) {
        allVehicles.remove(allVehicles.indexOf(v));
        //System.out.println(x);
        //System.out.println("removing vl " + v.id + " " + x + "distance " + v.distance);

		writer+=x;
        return allVehicles.size() -1;
    }
    public int getLowestVehicleBatteryInPlatoon(){
    	double min =100;
    	int index=0;
    	int i=0;
    	for(Vehicle v : allVehicles) {
    		if(v.autonomie<min&& v.myPlatoon!=null) {
    			min =v.autonomie;
    			index=i;
    		}
    		i++;
    	}
    	return index;
    }
    public int getHighestVehicleBattery(){
    	double max =0;
    	int index=0;
    	int i=0;
    	for(Vehicle v : allVehicles) {
    		if(v.autonomie>max) {
    			max =v.autonomie;
    			index=i;
    		}
    		i++;
    	}
    	return index;
    }

//    public void updateDistStas(){
//		distanceStation[0] = mutant==Mutant.M3 ? distanceStation[0]-1 : distanceStation[0]-10;
//		if(distanceStation[0]<=0) {
//			if(distanceStation[0]<0) System.out.println("distance station negative, should not happen");
//			distanceStation[0]=distanceStation[1];
//			distanceStation[1] = FREQUENCYSTATION;
//		}
//	}
    public Iterator<Vehicle> iterator() {
        return allVehicles.iterator();
    }

    public boolean isFull() {
        return !(allVehicles.size() < MAX_CAPACITY);
    }

    public int nbVehiclesOnRoad() {
        return allVehicles.size();
    }
    
    /**Get the next station based on a vehicle's position
     * @param int pos : position of the vehicle
     * @return double : the index of the next station in list of stations
     */
    public int getNextStation(double position) {
    	int i=0;
    	while(position >stationPositions.get(i)) {
    		i++;
    	}
    	return i;
    }
    public Vehicle getVehicle(int j) {
        return allVehicles.get(j);
    }
//	public double[] getDistanceStation() {
//		return distanceStation;
//	}
//	public void setDistanceStation(double[] distanceStas) {
//		distanceStation = distanceStas;
//	}
	public void consolePrint() {
		for (Vehicle v : allVehicles) {
			v.getDisplayString();
		}	        
	}

}
