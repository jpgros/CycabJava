import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Platoon extends Entity implements Serializable{ //implements Runnable {
	int consommationLeader = 2;
	final static int NUMBER_VEHICLE_MAX = 5; //unused
	final static double MINLEADERVALUE = 33;
	ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
    ArrayList<Vehicle> nextLeaderList = new ArrayList<Vehicle>();
    UUID id;
	UUID vehicleLeader = null;
	Vehicle leader;
	Road road = null;
	AdaptationPolicy policies =new AdaptationPolicy();
	Element lastReconf =null;
	boolean created;
	//int tickCounter =0;
	public Platoon(int consommationLeader, int numberVehicleMax,UUID id, UUID vehicleLeader) {
		this.consommationLeader = consommationLeader;
		//this.NUMBER_VEHICLE_MAX = numberVehicleMax;
		this.id = id;
		this.vehicleLeader = vehicleLeader;
	}

	public Platoon() {   

    }

	public Platoon(Vehicle _leader, Road r, Vehicle... others) {
	    created =true;
		leader = _leader;
	    id = UUID.randomUUID();
		vehiclesList.add(leader);
		vehicleLeader = leader.id;
		leader.setPlatoon(this);
		road =r;
		for (Vehicle v : others) {
			vehiclesList.add(v);
			eligibleLeader(v);
			v.setPlatoon(this);
		}
		//System.out.println("Platoon created " + id);
		road.addStringWriter("Platoon created " + id);
	}

	public void addVehicle(Vehicle v){
		vehiclesList.add(v);
		eligibleLeader(v);
	}
//	public void removeVehicle(Vehicle v) {
//		v.myPlatoon=null;
//		if(v==leader) {
//			writer.println("ERROR should not remove a leader without verifying platoon deltion forced");
//			deletePlatoon();
//		}
//		vehiclesList.remove(v);
//	}
	public void eligibleLeader(Vehicle v) {
		double minValue = v.getMinValue();
		String x ="";
		for(int i =0; i<nextLeaderList.size();i++) {
			if(minValue>= nextLeaderList.get(i).getMinValue()) {
				nextLeaderList.add(i, v);
				x = "vehicle " + v.id+ " added to leader list";
				//System.out.println(x);
				road.addStringWriter(x);
				break;
			}
		}
		if (minValue > MINLEADERVALUE){
			nextLeaderList.add(nextLeaderList.size(),v);
			x = "vehicle " + v.id+ " added at the end of leader list \n";
			//System.out.println(x);
			road.addStringWriter(x);
		}
	}
	
	public int findLeader() {
		if(vehicleLeader==null) return -1;
		int index=0;
		boolean notFounded = true;
		while(notFounded) {
			notFounded = !(vehiclesList.get(index).getId()==vehicleLeader);
			index++;
		}
		return index;
	}
//	public void run() {
//		tick();
//	}
	
	public void tick(){
		//vehiclesList.get(findLeader()).setAutonomie(vehiclesList.get(findLeader()).getAutonomie()-2); //reduces leader energy
		//System.out.println("tick policy: " + tickCounter);
		//writer.println("tick policy: " + tickCounter);
		//if(tickCounter ==0) {
			lastReconf=null; // to remove , already done in ticktrigger
			if(policies.listPolicy.size()>0) {
				lastReconf = policies.listPolicy.get(0);
			}
				//verification that vehicle Leader wants to relay and quit platoon:
	//			if(lastReconf.vehicle.isLeader() && lastReconf.name == PolicyName.RELAY) {
	//				Element elt = new Element(PolicyName.QUITFAILURE,Priority.HIGH);
	//				elt.vehicle=lastReconf.vehicle;
	//				index = contains(elt, policies.listPolicy);
	//				if(index !=-1) {
	//					lastReconf = policies.listPolicy.remove(index);
	//				}
	//				else {
	//					elt.name=PolicyName.QUITFORSTATION;
	//					index = contains(elt, policies.listPolicy);
	//					if(index!=-1) {
	//						lastReconf = policies.listPolicy.remove(index);
	//					}
	//					else {
	//						elt.name=PolicyName.QUITPLATOON;
	//						index = contains(elt, policies.listPolicy);
	//						if(index!=-1) {
	//							lastReconf = policies.listPolicy.remove(index);
	//						}
	//					}
	//				}
	//				x = "Vehicle wanted to relay and " + lastReconf.name + " reconfiguration was applied";
	//				System.out.println(x);
	//				writer.println(x);
	//			}
				//System.out.println("policy list cleared");
				//writer.println("policy list cleared");
		//}
		
//		else {
//			lastReconf = null;
//		}
		//tickCounter -= (tickCounter== 0) ? 0 : 1;
	}
	
	public void tickTrigger() {
		String x = "";
		if(policies.listPolicy.size()>0) {
			lastReconf = policies.listPolicy.remove(0);
			policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
//				x = "Replaced by " + this.leader.getId();
//
//				System.out.println(x);
//				writer.println(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";// + " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITPLATOON || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x); // or distance reached
					road.addStringWriter(x);
					//tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly"+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					break;
				}
			}
        }
		created=false;
		lastReconf=null;
		String vlList="";
		if(vehiclesList==null) road.platoonLog+="0 \n";
		else {
			for(Vehicle v : this.vehiclesList) {
				vlList+=";"+ v.id;
			}
			road.platoonLog+="Platoon;"+ id+";" +vehiclesList.size() +vlList+  "\n";
		}
	}
	
	public void tickTriggerM1() { //always get lower priority
		String x = "";
		if(policies.listPolicy.size()>0) {
			System.out.println("list " + policies.listPolicy+ "taking " +policies.listPolicy.get(policies.listPolicy.size()-1));
			lastReconf = policies.listPolicy.remove(policies.listPolicy.size()-1);
			policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";// + " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITPLATOON || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x); // or distance reached
					road.addStringWriter(x);
					//tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly"+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					break;
				}
			}
        }
		created=false;
		lastReconf=null;
	}
	
	public void tickTriggerM2() { //get random priority should be harde rto detect than M1
		String x = "";
		if(policies.listPolicy.size()>0) {
			lastReconf = policies.listPolicy.remove(new Random().nextInt(policies.listPolicy.size()));
			policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";// + " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITPLATOON || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x); // or distance reached
					road.addStringWriter(x);
					//tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly"+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					break;
				}
			}
        }
		created=false;
		lastReconf=null;
	}
	
	public void tickTriggerM3() { //lastReconf not reinit at the end of tick trigger so if on the next step another action than tick is used, lastreconf will apear again into actual
		String x = "";
		if(policies.listPolicy.size()>0) {
			lastReconf = policies.listPolicy.remove(0);
			policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";// + " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITPLATOON || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x); // or distance reached
					road.addStringWriter(x);
					//tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly"+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					break;
				}
			}
        }
		created=false;
		//lastReconf=null; mutant removes this line
	}
	
	public void tickTriggerM4() { // randomly doest not do a reconf
		String x = "";
		double proba  = Math.random();
	    
		if(policies.listPolicy.size()>0 && proba >=0.95) { //here
			lastReconf = policies.listPolicy.remove(0);
			policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";// + " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
				//tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITPLATOON || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					//tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id+ "\n";
					//System.out.print(x); // or distance reached
					road.addStringWriter(x);
					//tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly"+ "\n";
					//System.out.print(x);
					road.addStringWriter(x);
					break;
				}
			}
        }
		created=false;
		//lastReconf=null; mutant removes this line
	}
	public void relayMutant() {
		this.leader=null;
	}
	public void relay() {
//		if(!nextLeaderList.isEmpty()){
//			if(leader.getId()==nextLeaderList.get(0).getId()) {
//				nextLeaderList.remove(0);
//			}
//		}
		if(!nextLeaderList.isEmpty()) {
			//System.out.print("Leader vehicle "+ leader.getId());
			if(nextLeaderList.get(0).autonomie >= leader.LOW_LEADER_BATTERY) {
				road.addStringWriter("actual leader id " +leader.getId() + "next leader " + nextLeaderList.get(0).getId());
				this.leader = nextLeaderList.remove(0);
			}
			else {
				//System.out.println("No better vehicle available, Platoon deleted");
				road.addStringWriter("No better vehicle available, Platoon deleted");
				deletePlatoon();
			}
		}
		
		else { // remove platoon
			//System.out.println("No better vehicle available, Platoon deleted");
			road.addStringWriter("No better vehicle available, Platoon deleted");
			deletePlatoon();
            /*
            if (vehiclesList.size()>0) {
				leader = vehiclesList.get((int)(Math.random() * vehiclesList.size()));
				System.out.println("Leader vehicle replaced by random " + leader.getId());
			}
			else {
				System.out.println("No more vehicle avaiblable");
			}
			*/
		}
	}
	
	public void upgradeRelay(Vehicle v) {
		this.leader=v;
	}
	
	public void deleteVehicle(Vehicle v){
		if(this.vehiclesList!=null) {
			v.idPlatoon = null;
			v.myPlatoon = null;
			this.vehiclesList.remove(v);
			if(v==leader) { // if deleted vehicle was leader, we elect randomly a new leader, the adaptation policies will elect a new one if he does not fits the requirements
				//Random random = new Random();
				//int index = Math.abs(random.nextInt(vehiclesList.size()));
				if(vehiclesList.size()>0) leader = vehiclesList.get(0);
			}
			policies.removeForVehicle(v);
			if (vehiclesList.size() <= 1) {
				deletePlatoon();
			}
		}

	}
	
	public void deletePlatoon() {
		if(vehiclesList.size()>=1) {
			//System.out.println("Deleting platoon ");
			road.addStringWriter("Deleting platoon" + this.vehiclesList+ "\n");
			//vehiclesList.get(0).removePlatoonFromList();
			//road.lastReconfList.add(this.lastReconf);
			for(int i =0; i<vehiclesList.size(); i++) {
				vehiclesList.get(i).deletePlatoon();
			}
			this.leader=null;
			this.vehiclesList=null;
			this.policies=null;
		}
		else {
			System.out.println("Case not taken care of");
		}
	}

//	public int containsVehicleOnBatteryLevel(ArrayList<Vehicle> platoonList, double batteryExiged) {
//		for (int i=0; i < platoonList.size(); i++) {
//		    if (platoonList.get(i).autonomie >= batteryExiged) {
//		        return i;
//            }
//        }
//        return -1;
		/*
	    boolean notFounded = true;
		int index =0;
		while(notFounded && index < platoonList.size()) {
			notFounded = platoonList.get(index).getAutonomie() < batteryExiged;
			index++;
		}
		if(index>=platoonList.size()) return -1;
		index --; //to cancel the last index++
		return index;
		*/
//	}
//	public int containsVehicleOnScore(ArrayList<Vehicle> platoonList, double batteryLeader, double distanceLeader) {
//		for (int i=0; i < platoonList.size(); i++) {
//		    if (Math.min(platoonList.get(i).autonomie,platoonList.get(i).distance) >= Math.min(batteryLeader,distanceLeader)) {
//		        return i;
//            }
//        }
//        return -1;
//	}
	public int contains(Element elt, ArrayList<Element> array) {
		int index = 0;
		boolean bool = true;
		if (array.size() >0) {
			do {
				bool = !array.get(index).equals(elt);
				index ++;
			}while(bool && index < array.size());
			index--;
			return array.get(index).equals(elt) ? index : -1;
			}
		return -1;
	}
		
	public void accept(Vehicle v) {
		// TODO -- adaptation policy
		if (vehiclesList.size() < NUMBER_VEHICLE_MAX) {
			vehiclesList.add(v);
			v.setPlatoon(this);
			//System.out.println("Vehicle " + v + " joined platoon" + this);
			road.addStringWriter("Vehicle " + v + " joined platoon" + this+ "\n");
		}
	}

	public String toString() {
		return id.toString();
	}
	
	public void affiche() {
	    //System.out.print("[");
	    road.addStringWriter("[");
	    for (int i=0; i < vehiclesList.size(); i++) {
	        if (i > 0) {
	            //System.out.print(" | ");
				road.addStringWriter(" | ");
            }
	        //System.out.print(vehiclesList.get(i).getDisplayString());
			road.addStringWriter(vehiclesList.get(i).getDisplayString());
        }
        //System.out.println("]");
	    //System.out.println("Policy: " + policies);
		road.addStringWriter("]+ \n");
		road.addStringWriter("Policy: " + policies +" nb "+ policies.listPolicy.size()+ "\n");
    }
	public int getConsommationLeader() {
		return consommationLeader;
	}
	public void setConsommationLeader(int consommationLeader) {
		this.consommationLeader = consommationLeader;
	}
	public int getNumberVehicleMax() {
		return NUMBER_VEHICLE_MAX;
	}
	/*public void setNumberVehicleMax(int numberVehicleMax) {
		this.NUMBER_VEHICLE_MAX = numberVehicleMax;
	} */
	public ArrayList<Vehicle> getVehiclesList() {
		return vehiclesList;
	}
	public void setVehiclesList(ArrayList<Vehicle> vehiclesList) {
		this.vehiclesList = vehiclesList;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getVehicleLeader() {
		return vehicleLeader;
	}
	public void setVehicleLeader(UUID vehicleLeader) {
		this.vehicleLeader = vehicleLeader;
	}
	
	public AdaptationPolicy getPolicies() {
		return policies;
	}

	public void setPolicies(AdaptationPolicy policies) {
		this.policies = policies;
	}
	
}
