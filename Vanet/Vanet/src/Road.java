import java.util.ArrayList;
import java.util.Iterator;
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
    double distanceStation[] = {FREQUENCYSTATION, FREQUENCYSTATION}; 
    ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
    int numberPlatoon=0;
    double globalConso=0;
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
    	k = new double[8];
    	logLevel=lg;
    	for(int i=0;i<k.length;i++) k[i]=0;
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
    public int addVehicle(double _auto, double _distance, double decAuto) {
    	allVehicles.add(new Vehicle(_auto, _distance, randomUUID(), null,this, decAuto));
    	//System.out.println("Vehicle created at index " + (allVehicles.size() - 1));
		writer+="Vehicle created at index " + (allVehicles.size() - 1);
        return allVehicles.size() - 1;
    }
    public boolean join(int i, int j) {
        if (i != j && allVehicles.get(i).getPlatoon() == null) {
            return allVehicles.get(i).join(allVehicles.get(j))&& allVehicles.get(i).getPlatoon() != null;
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

    public void reinit() { //reinit variables in case of a new test 
        this.distanceStation[0] = FREQUENCYSTATION;
        this.distanceStation[1] = FREQUENCYSTATION; 
        this.allVehicles = new ArrayList<Vehicle>();
        this.numberPlatoon=0;
        this.globalConso=0;
    }
    public void tick() {
    	stepNb++;
    	tick=true;
    	component="Components(";
    	binding="Bindings(";
    	state="State(";
    	action="Actions(";
        for (int i=0; i < allVehicles.size(); i++) {
            Vehicle v = allVehicles.get(i);
            v.updateVehicleDistance();
            globalConso+=v.updatevehicleAutonomie();
            if(logLevel!=LogLevel.ERROR) {
	            component +="Vehicle:"+v.getId();
	            state+= "Vehicle:"+v.getId() +"->remaining distance:"+ v.distance+";";
	            state+= "Vehicle:"+v.getId() +"->remaining automony:"+v.autonomie+";";
	            state+= "Vehicle:"+v.getId() +"->status:"+v.getStatus()+";";
            }
            v.tick();
            if(v.myPlatoon!=null) { 
            	if(v.isLeader()) {
            		component +="Platoon:"+v.myPlatoon+"\n";
            	}
            	binding+= "Platoon:"+v.myPlatoon+"->Vehicle:"+v.getId()+"\n";
            	state +="\n"+"Platoon:"+v.myPlatoon+ "->number vehicles:" + v.myPlatoon.vehiclesList.size();
            }
            if(v.distance <=10) {
                if(v.myPlatoon!=null) { 
                    removeVehicle(v, "Error : vehicle: "+ v.id+ " reached distance but is inside platoon, removed anyway ");
                }
                else{
                	action +="Vehicle:"+v.getId() +"quitRoad";
                    removeVehicle(v, "Event : vehicle: "+ v.id+ " reached distance and quitting successfully the road ");
                }
                i--;
            }
            state+="\n";
        }
        if(logLevel!=LogLevel.ERROR) {
   	 		affiche();
        } 	
        for (Vehicle v : allVehicles) {
            if (v.getPlatoon() != null && v.getPlatoon().leader == v) {
                v.getPlatoon().tick();
                if(v.myPlatoon.lastReconf!=null) action += v.myPlatoon.lastReconf;
            }
        }
        if(logLevel!=LogLevel.ERROR) {
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
                //System.out.print(x);
        		writer+=x;
            }
        }
        //System.out.println();
		writer+="\n \n";
    }

    public int removeVehicle(Vehicle v, String x) {
        allVehicles.remove(allVehicles.indexOf(v));
        //System.out.println(x);
		writer+=x;
        return allVehicles.size() -1;
    }
    public int getLowestVehicleBattery(){
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

    public void updateDistStas(){
		distanceStation[0] = mutant==Mutant.M3 ? distanceStation[0]-1 : distanceStation[0]-10;
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
