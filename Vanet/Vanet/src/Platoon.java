import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Platoon extends Entity { //implements Runnable {
	int consommationLeader = 2;
	final static int NUMBER_VEHICLE_MAX = 5; //unused
	final static double MINLEADERVALUE = 30;
	ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
    ArrayList<Vehicle> nextLeaderList = new ArrayList<Vehicle>();
    UUID id;
	UUID vehicleLeader = null;
	Vehicle leader;
	Road road = null;
	AdaptationPolicy policies =new AdaptationPolicy();
	PrintWriter writer =null;
	Element lastReconf =null;
	int tickCounter =0;
	public Platoon(int consommationLeader, int numberVehicleMax,UUID id, UUID vehicleLeader) {
		this.consommationLeader = consommationLeader;
		//this.NUMBER_VEHICLE_MAX = numberVehicleMax;
		this.id = id;
		this.vehicleLeader = vehicleLeader;
	}

	public Platoon() {   

    }

	public Platoon(Vehicle _leader, PrintWriter w, Road r, Vehicle... others) {
	    leader = _leader;
	    id = UUID.randomUUID();
		vehiclesList.add(leader);
		vehicleLeader = leader.id;
		leader.setPlatoon(this);
		writer=w;
		road =r;
		for (Vehicle v : others) {
			vehiclesList.add(v);
			eligibleLeader(v);
			v.setPlatoon(this);
		}
		System.out.println("Platoon created " + id);
		writer.println("Platoon created " + id);
	}

	public void addVehicle(Vehicle v){
		vehiclesList.add(v);
		eligibleLeader(v);
	}
	public void removeVehicle(Vehicle v) {
		vehiclesList.remove(v);
	}
	public void eligibleLeader(Vehicle v) {
		double minValue = v.getMinValue();
		for(int i =0; i<nextLeaderList.size();i++) {
			if(minValue>= nextLeaderList.get(i).getMinValue()) {
				nextLeaderList.add(i, v);
				break;
			}
		}
		if (minValue > MINLEADERVALUE){
			nextLeaderList.add(nextLeaderList.size(),v);
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
		String x = "";
		int index=0;
		//vehiclesList.get(findLeader()).setAutonomie(vehiclesList.get(findLeader()).getAutonomie()-2); //reduces leader energy
		System.out.println("tick policy: " + tickCounter);
		if(policies.listPolicy.size()>0 && tickCounter==0) {
			lastReconf = policies.listPolicy.remove(0);
			//verification that vehicle Leader wants to relay and quit platoon:
			if(lastReconf.vehicle.isLeader() && lastReconf.name == PolicyName.RELAY) {
				Element elt = new Element(PolicyName.QUITFAILURE,Priority.HIGH);
				elt.vehicle=lastReconf.vehicle;
				index = contains(elt, policies.listPolicy);
				if(index !=-1) {
					lastReconf = policies.listPolicy.remove(index);
				}
				else {
					elt.name=PolicyName.QUITFORSTATION;
					index = contains(elt, policies.listPolicy);
					if(index!=-1) {
						lastReconf = policies.listPolicy.remove(index);
					}
					else {
						elt.name=PolicyName.QUITPLATOON;
						index = contains(elt, policies.listPolicy);
						if(index!=-1) {
							lastReconf = policies.listPolicy.remove(index);
						}
					}
				}
			}
			policies.listPolicy.clear();
			System.out.println("policy list cleared");
			writer.println("policy list cleared");
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + " tick : " + tickCounter;
				System.out.println(x);
				writer.println(x);
				tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.UPGRADERELAY) {
				this.upgradeRelay(lastReconf.vehicle);
				x = "Reconfiguration : normal vehicle get better leader" + lastReconf.vehicle.getId() + " : [UPGRADERELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + " tick : " + tickCounter;
				System.out.println(x);
				writer.println(x);
				tickCounter=6;
			}
			else if(lastReconf.name == PolicyName.QUITFAILURE || lastReconf.name == PolicyName.QUITFORSTATION || lastReconf.name == PolicyName.QUITFORSTATION) {
				if(lastReconf.vehicle == leader) {
					relay();
					x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded before quitting : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id;
					System.out.println(x);
					writer.println(x);
					tickCounter=6;
				}
				deleteVehicle(lastReconf.vehicle);
				switch (lastReconf.name) {
				case QUITFAILURE:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to failure : [QUITFAILURE] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id;
					System.out.println(x);
					writer.println(x);
					tickCounter+=3;
					break;
				case QUITFORSTATION:
					x = "Reconfiguration : vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to station : [QUITFORSTATION] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id;
					System.out.println(x);
					writer.println(x);
					tickCounter+=5;
					break;
				case QUITPLATOON:
					x = "Reconfiguration :vehicle " + lastReconf.vehicle.getId() + " quitted platoon due to user : [QUITPLATOON]  ; priority : {" + lastReconf.getPriority()+ "} "+ this.id;
					System.out.println(x); // or distance reached
					writer.println(x);
					tickCounter+=4;
					break;
				default:
					x= "Error, policy name not verified properly";
					System.out.println(x);
					writer.println(x);
					break;
				}
                
            }
		}
//		else {
//			lastReconf = null;
//		}
		tickCounter -= (tickCounter== 0) ? 0 : 1;
	}
	
	
	public void relay() {
		if(!nextLeaderList.isEmpty()) {
			System.out.print("Leader vehicle "+ leader.getId());
			this.leader = nextLeaderList.remove(0);			
			System.out.println(" replaced by elected " + leader.getId());
		}
		
		else { // remove platoon
			deletePlatoon();
            System.out.println("No better vehicle available, Platoon deleted");
            writer.println("No better vehicle available, Platoon deleted");
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
			if (vehiclesList.size() == 1) {
				deletePlatoon();
			}
		}

	}
	public void deletePlatoon() {
		if(vehiclesList.size()>=1) {
			System.out.println("Deleting platoon ");
			writer.println("Deleting platoon" + this.vehiclesList);
			//vehiclesList.get(0).removePlatoonFromList();
			for(int i =0; i<vehiclesList.size(); i++) {
				vehiclesList.get(i).deletePlatoon();
			}
			road.lastReconfList.add(this.lastReconf);
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
			System.out.println("Vehicle " + v + " joined platoon" + this);
			writer.println("Vehicle " + v + " joined platoon" + this);
		}
	}

	public String toString() {
		return id.toString();
	}
	
	public void affiche() {
	    System.out.print("[");
	    for (int i=0; i < vehiclesList.size(); i++) {
	        if (i > 0) {
	            System.out.print(" | ");
	            writer.print(" | ");
            }
	        System.out.print(vehiclesList.get(i).getDisplayString());
	        writer.print(vehiclesList.get(i).getDisplayString());
        }
        System.out.println("]");
	    System.out.println("Policy: " + policies);
	    writer.println("]");
	    writer.println("Policy: " + policies);
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
