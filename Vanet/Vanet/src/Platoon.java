import java.util.ArrayList;
import java.util.UUID;

public class Platoon extends Entity implements Runnable {
	int consommationLeader = 2;
	int DistanceStation[] = new int[2]; //unused
	final static int NUMBER_VEHICLE_MAX = 5; //unused
    ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
    UUID id;
	UUID vehicleLeader = null;
	Vehicle leader;
	
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
		vehiclesList.get(findLeader()).setAutonomie(vehiclesList.get(findLeader()).getAutonomie()-2); //reduces leader energy
	}
	
	public void relay() {
		vehicleLeader = vehiclesList.get(containsVehicleOnBatteryLevel(vehiclesList,33)).getId(); //can be a problem
	}
	
	public void deleteVehicle(){
		
	}
	
	public void addVehicle() {
		
	}
	public int containsVehicleOnBatteryLevel(ArrayList<Vehicle> platoonList, int batteryExiged) {
		boolean notFounded = true;
		int index =0;
		while(notFounded) {
			notFounded = platoonList.get(index).getAutonomie() < batteryExiged;
			index++;
		}
		if(index>platoonList.size()) return -1;
		return index;
	}
	public int getConsommationLeader() {
		return consommationLeader;
	}
	public void setConsommationLeader(int consommationLeader) {
		this.consommationLeader = consommationLeader;
	}
	public int[] getDistanceStation() {
		return DistanceStation;
	}
	public void setDistanceStation(int[] distanceStation) {
		DistanceStation = distanceStation;
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
    }

}
