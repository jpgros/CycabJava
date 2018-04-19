import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Platoon extends Entity implements Runnable {
	int consommationLeader = 2;
	final static int NUMBER_VEHICLE_MAX = 5; //unused
    ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
    UUID id;
	UUID vehicleLeader = null;
	Vehicle leader;
	AdaptationPolicy policies =new AdaptationPolicy();
	public Platoon(int consommationLeader, int numberVehicleMax,UUID id, UUID vehicleLeader) {
		this.consommationLeader = consommationLeader;
		//this.NUMBER_VEHICLE_MAX = numberVehicleMax;
		this.id = id;
		this.vehicleLeader = vehicleLeader;
	}

	public Platoon() {   

    }

	public Platoon(Vehicle _leader, Vehicle... others) {
	    leader = _leader;
	    id = UUID.randomUUID();
		vehiclesList.add(leader);
		vehicleLeader = leader.id;
		leader.setPlatoon(this);
		for (Vehicle v : others) {
			vehiclesList.add(v);
			v.setPlatoon(this);
		}
		System.out.println("Platoon created " + id);
	}

	public void addVehicle(Vehicle v){
		vehiclesList.add(v);
	}
	public void removeVehicle(Vehicle v) {
		vehiclesList.remove(v);
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
	public void run() {
		tick();
	}
	
	public void tick() {
		//vehiclesList.get(findLeader()).setAutonomie(vehiclesList.get(findLeader()).getAutonomie()-2); //reduces leader energy
		if(policies.listPolicy.size()>0) {
			Element elt = policies.listPolicy.remove(0);
			if(elt.name == PolicyName.RELAY) this.relay();
			else if(elt.name == PolicyName.QUITFAILURE || elt.name == PolicyName.QUITFORSTATION || elt.name == PolicyName.QUITFORSTATION) {
				if(elt.vehicle == leader) {
					relay();
				}
				deleteVehicle(elt.vehicle);
				switch (elt.name) {
				case QUITFAILURE:
					System.out.println("vehicle " + elt.vehicle.getId() + " quitted platoon due to failure"+ this.id  );
					break;
				case QUITFORSTATION:
					System.out.println("vehicle " + elt.vehicle.getId() + " quitted platoon due to station"+ this.id  );
					break;
				case QUITPLATOON:
					System.out.println("vehicle " + elt.vehicle.getId() + " quitted platoon due to user"+ this.id  ); // or distance reached
					break;
				default:
					System.out.println("Error, policy name not verified properly");
					break;
				}
                
            }
		}
	}
	
	
	public void relay() {
		int index = containsVehicleOnScore(vehiclesList,/*33*/ leader.autonomie + 1, leader.distance);
		if (index !=-1) {
			System.out.print("Leader vehicle "+ leader.getId());
			leader = vehiclesList.get(index);
			 System.out.println(" replaced by elected " + leader.getId());
		}
	
		else { // remove platoon
			deletePlatoon();
            System.out.println("No better vehicle available, Platoon deleted");
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
		}

	}
	public void deletePlatoon() {
		if(vehiclesList.size()>=1) {
			System.out.println("Deleting platoon ");
			//vehiclesList.get(0).removePlatoonFromList();
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

	public int containsVehicleOnBatteryLevel(ArrayList<Vehicle> platoonList, double batteryExiged) {
		for (int i=0; i < platoonList.size(); i++) {
		    if (platoonList.get(i).autonomie >= batteryExiged) {
		        return i;
            }
        }
        return -1;
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
	}
	public int containsVehicleOnScore(ArrayList<Vehicle> platoonList, double batteryLeader, double distanceLeader) {
		for (int i=0; i < platoonList.size(); i++) {
		    if (Math.min(platoonList.get(i).autonomie,platoonList.get(i).distance) >= Math.min(batteryLeader,distanceLeader)) {
		        return i;
            }
        }
        return -1;
	}
	
	public void accept(Vehicle v) {
		// TODO -- adaptation policy
		if (vehiclesList.size() < NUMBER_VEHICLE_MAX) {
			vehiclesList.add(v);
			v.setPlatoon(this);
			System.out.println("Vehicle " + v + " joined platoon" + this);
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
            }
	        System.out.print(vehiclesList.get(i).getDisplayString());
        }
        System.out.println("]");
	    System.out.println("Policy: " + policies);
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
