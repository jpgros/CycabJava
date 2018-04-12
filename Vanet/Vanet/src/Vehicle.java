import java.util.ArrayList;
import java.util.UUID;

import javax.sql.PooledConnection;

public class Vehicle implements Runnable{
	int autonomie;
	int distance;
	UUID id;
	UUID idPlatoon = null;
	ArrayList<Object> vehiclePlatoonList;
	
	public Vehicle (int autonomie, int distance, UUID id, ArrayList<Object> vehiclePlatoonList) {
		this.autonomie = autonomie;
		this.distance = distance;
		this.id = id;
		this.vehiclePlatoonList = vehiclePlatoonList;
	}
	public void tick() {
		if(idPlatoon!=null)this.autonomie -= 10;
		else this.autonomie -=12;
		this.distance -=10;
	}
	
	public void refill() {
		this.autonomie =100;
	}
	
	public void run() {
		int cpt =0;
		System.out.println("Vehicle id : " + id + " started");
		while(distance >0 && autonomie>10) {
//			cpt++;
//			if (cpt==9) {
//				refill();
//				cpt=0;
//			}
			tick();	
			System.out.println("autonomy " + autonomie);
			if(idPlatoon==null) {
				int index = containsEntityOnBatteryLevel(33);
				//System.out.println("this class = " + this.getClass() + ", list class = " + vehiclePlatoonList.get(index).getClass());
				if(vehiclePlatoonList.get(index).getClass() == this.getClass()) { // not forget mutex on lists and attributes
					Vehicle v = (Vehicle) vehiclePlatoonList.get(index);
					UUID idCreated =UUID.randomUUID();
					Platoon platoon = new Platoon(2,10,idCreated,id);
					//add vehicles in new platoon
					platoon.getVehiclesList().add(this);
					platoon.getVehiclesList().add(v);
					//add platoon to global list
					vehiclePlatoonList.add(platoon);
					//remove 2 vehicles of globals list
					vehiclePlatoonList.remove(v);
					vehiclePlatoonList.remove(this);
					//set idPLatoon for two vehicles
					idPlatoon = idCreated;
					v.setIdPlatoon(idCreated);
					
					//should launch a new platoon thread
					//pool.execute(platoon);
					System.out.println("two vehicules created a platoon");
				}
				else {
					Platoon p = (Platoon) vehiclePlatoonList.get(index);
					p.getVehiclesList().add(this);
					vehiclePlatoonList.remove(this);
					idPlatoon = p.getId(); 
					System.out.println("a vehicule " + this.getId()+ " rejoined a platoon" + p.getId());
				}
			}
		}
		System.out.println("Vehicle id : " + id + " stopped");
		quitPlatoon(idPlatoon);
		if(distance == 0) System.out.println("destination reached");
		else if (autonomie > 0 && autonomie < 10 ) { 
			System.out.println("No energy left");
			//remove vehicle of platoon list
		}
		else System.out.println("error " + autonomie + " "  + distance);
	}
	//UUID uniqueKey = UUID.randomUUID();
	public int getAutonomie() {
		return autonomie;
	}
	public void setAutonomie(int autonomie) {
		this.autonomie = autonomie;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getIdPlatoon() {
		return idPlatoon;
	}
	public void setIdPlatoon(UUID idPlatoon) {
		this.idPlatoon = idPlatoon;
	}
	
	public int containsEntityOnBatteryLevel(int batteryExiged) {
		int index=0;
		boolean notFounded = true;
		while(notFounded) {
			if(vehiclePlatoonList.get(index).getClass() == this.getClass()) {
				Vehicle v = (Vehicle) vehiclePlatoonList.get(index);
				if( v.getAutonomie() > batteryExiged ) {
					notFounded= false;
				}
			}
			else { //if it is a platoon we join directly
				notFounded= false;
			}
			//else {
				//Platoon p = (Platoon) vehiclePlatoonList.get(index);
				//if(p.containsVehicleOnBatteryLevel(p.getVehiclesList(), 33) !=-1 ) {
					
			//}
		}
		if(index > vehiclePlatoonList.size()) return -1;
		return index;
	}
	
	public void quitPlatoon(UUID idPlatoon) {
		if(idPlatoon != null) {
			int index =0;
			boolean notFounded = true;
			Platoon p = new Platoon();
			while(notFounded) {
				if(vehiclePlatoonList.get(index).getClass() == p.getClass()) {
					p = (Platoon) vehiclePlatoonList.get(index);
					if(idPlatoon == p.getId()) {
						p.getVehiclesList().remove(this);
						idPlatoon=null;
						System.out.println("vehicule quitted platoon");
					}
				}
			}
		}
	}

}
