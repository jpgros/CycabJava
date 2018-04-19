import java.util.ArrayList;
import java.util.UUID;

import javax.sql.PooledConnection;


public class Vehicle extends Entity {
	double autonomie;
	double distance;
	UUID id;
	UUID idPlatoon = null;
	ArrayList<Entity> vehiclePlatoonList;
	Platoon myPlatoon = null;
	Road road = null;
	final static double DEC_ENERGY = 1;
	final static double DEC_LEADER = DEC_ENERGY * 1.2;
	final static double LOW_LEADER_DIST = 40;
	final static double LOW_DIST = 30;
	final static double VLOW_DIST = 20;
	final static double LOW_LEADER_BATTERY = 33;
	final static double LOW_BATTERY = 10;
//	final static double VLOW_BATTERY = 5;
	
	public Vehicle (double autonomie, double distance, UUID id, ArrayList<Entity> vehiclePlatoonList, Road r) {
		this.autonomie = autonomie;
		this.distance = distance;
		this.id = id;
		this.vehiclePlatoonList = vehiclePlatoonList;
		this.road=r;
	}

	public void refill() {
		if(this.myPlatoon==null) {
			System.out.println("Refill vehicle " + id);
			this.autonomie =100;
		}
		else {
			System.out.println("Tried to refill but still in Platoon");
		}
	}
	
//	public void run() {
//		System.out.println("Vehicle id : " + id + " started");
//		while(distance >0 && autonomie>10) {
//			tick();	
//			System.out.println("autonomy " + autonomie);
//			if(idPlatoon==null) {
//				int index = containsEntityOnBatteryLevel(33);
//				//System.out.println("this class = " + this.getClass() + ", list class = " + vehiclePlatoonList.get(index).getClass());
//				if(vehiclePlatoonList.get(index).getClass() == this.getClass()) { // not forget mutex on lists and attributes
//					Vehicle v = (Vehicle) vehiclePlatoonList.get(index);
//					UUID idCreated =UUID.randomUUID();
//					Platoon platoon = new Platoon(2,10,idCreated,id);
//					//add vehicles in new platoon
//					platoon.getVehiclesList().add(this);
//					platoon.getVehiclesList().add(v);
//					//add platoon to global list
//					vehiclePlatoonList.add(platoon);
//					//remove 2 vehicles of globals list
//					vehiclePlatoonList.remove(v);
//					vehiclePlatoonList.remove(this);
//					//set idPLatoon for two vehicles
//					idPlatoon = idCreated;
//					v.setIdPlatoon(idCreated);
//					
//					//should launch a new platoon thread
//					//pool.execute(platoon);
//					System.out.println("two vehicules created a platoon");
//				}
//				else {
//					Platoon p = (Platoon) vehiclePlatoonList.get(index);
//					p.getVehiclesList().add(this);
//					vehiclePlatoonList.remove(this);
//					idPlatoon = p.getId(); 
//					System.out.println("a vehicule " + this.getId()+ " rejoined a platoon" + p.getId());
//				}
//			}
//		}
//		System.out.println("Vehicle id : " + id + " stopped");
//		quitPlatoon(/*idPlatoon*/);
//		if(distance == 0) System.out.println("destination reached");
//		else if (autonomie > 0 && autonomie < 10 ) { 
//			System.out.println("No energy left");
//			//remove vehicle of platoon list
//		}
//		else System.out.println("error " + autonomie + " "  + distance);
//	}
	public double getAutonomie() {
		return autonomie;
	}
	public void setAutonomie(double autonomie) {
		this.autonomie = autonomie;
	}
	public double getDistance() {
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
	
	public void quitPlatoon(/*UUID idPlatoon*/) {
		if (myPlatoon != null) {
			System.out.println("vehicle " + this.getId() + " force-quitted platoon "+ this.getPlatoon().id  );
			myPlatoon.deleteVehicle(this);
		}
	}

	public Platoon getPlatoon() {
		return myPlatoon;
	}

	public void join(Vehicle v) {
		if (this.myPlatoon != null) return;
		if (v.myPlatoon != null) {
			v.myPlatoon.accept(this);
		}
		else {
			v.createPlatoon(this);
		}
	}

	public void setPlatoon(Platoon p) {
		myPlatoon = p;
	}

	public void createPlatoon(Vehicle v) {
		// TODO -- adaptation policy
		if (true) {
			Platoon platoon = new Platoon(this, v);
		}
	}
	public void removePlatoonFromList() {
		vehiclePlatoonList.remove(myPlatoon);
	}
	public void deletePlatoon() {
		myPlatoon=null;
		idPlatoon=null;
	}
	public void tick() {
		this.autonomie -= (this.myPlatoon == null || this == this.myPlatoon.leader) ? DEC_LEADER : DEC_ENERGY;
		this.distance -= 10;

		// TODO
		//Add each tick only new policies will be added
		if(myPlatoon!=null) {
			// distance < seuil --> quitte le peloton
			if(distance < VLOW_DIST) {
				Element elt = new Element(PolicyName.QUITPLATOON, Priority.HIGH, this);
				myPlatoon.policies.addElement(elt); 
			}
			else if(distance < LOW_DIST) {
				Element elt = new Element(PolicyName.QUITPLATOON, Priority.MEDIUM, this);
				myPlatoon.policies.addElement(elt); 
			}
			if(autonomie < LOW_BATTERY) {
				Element elt = new Element(PolicyName.QUITFAILURE, Priority.HIGH, this);
				myPlatoon.policies.addElement(elt);
			}
			if(this == myPlatoon.leader && (autonomie < LOW_LEADER_BATTERY || distance < LOW_LEADER_DIST)) {
				Element elt = new Element(PolicyName.RELAY, Priority.HIGH, this);
				myPlatoon.policies.addElement(elt);
			}
			if((autonomie -10.0)< (road.distanceStation[0] +road.distanceStation[1])){ //keep a margin of 10 
				if(road.distanceStation[0] < 8) {
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.HIGH, this);
					myPlatoon.policies.addElement(elt);
				}
				else if(road.distanceStation[0] >= 8 && road.distanceStation[0] < 15) {
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.MEDIUM, this);
					myPlatoon.policies.addElement(elt);
				}
				else if(road.distanceStation[0] >= 15) {
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.LOW, this);
					myPlatoon.policies.addElement(elt);
				}
				else {
					System.out.println("Should not happen QuitToStation policy problem");
				}
			}
			
			//leader ask platoon to choose wich adaptation policy to choose
			if(this == myPlatoon.leader) {
			//	myPlatoon.tick();
			}
			// leader && [second plus de batterie] --> relai
		}
		else {
			if (autonomie < LOW_BATTERY) {
				refill();	// TODO: improve
			}
		}
	}

	public String getDisplayString() {
		String l = (myPlatoon != null && myPlatoon.leader == this) ? "*" : "";
		return "id: " + id.toString().split("-")[0] + l + ", auto: " + autonomie + ", distance: " + distance;
	}

}
