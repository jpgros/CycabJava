import java.util.ArrayList;
import java.util.UUID;

public class Platoon implements Runnable{
	int consommationLeader;
	int DistanceStation[] = new int[2]; //unused
	int numberVehicleMax; //unused
    ArrayList<Vehicle> vehiclesList = new ArrayList<>();
    UUID id;
	UUID vehicleLeader = null;
	
	public Platoon(int consommationLeader, int numberVehicleMax,UUID id, UUID vehicleLeader) {
		this.consommationLeader = consommationLeader;
		this.numberVehicleMax = numberVehicleMax;
		this.id = id;
		this.vehicleLeader = vehicleLeader;
	}
	public Platoon() {
		
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
		return numberVehicleMax;
	}
	public void setNumberVehicleMax(int numberVehicleMax) {
		this.numberVehicleMax = numberVehicleMax;
	}
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
	
	
}
