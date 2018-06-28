import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import javax.sql.PooledConnection;

import static sun.tools.java.Constants.DEC;


public class Vehicle extends Entity {
	double autonomie;
	double distance;
	UUID id;
	UUID idPlatoon = null;
	ArrayList<Entity> vehiclePlatoonList;
	Platoon myPlatoon = null;
	Road road = null;
	PrintWriter writer = null;
	FileReader read = null;
	BufferedReader reader = null;
	
	final double DEC_ENERGY = 1 + Math.random() / 5;
	final double DEC_LEADER = DEC_ENERGY * 1.2;
	final double DEC_DISTANCE = 10;
	final static double LOW_LEADER_DIST = 200;
	final static double LOW_DIST = 200;
	final static double VLOW_DIST = 100;
	final static double LOW_LEADER_BATTERY = 33;
	final static double LOW_BATTERY = 15; // should be > property3

//	final static double HIGH_PRIO = 100;
//	final static double MEDIUM_PRIO = 150;
//	final static double LOW_PRIO = 200;
//	final static double HIGH_PRIO_RELAY = 150;
//	final static double MEDIUM_PRIO_RELAY_TICK = 20;
//	final static double LOW_PRIO_RELAY_TICK = 25;
//	final static double VLOW_BATTERY = 5;
	
	
	public Vehicle (double autonomie, double distance, UUID id, ArrayList<Entity> vehiclePlatoonList, Road r, PrintWriter w, FileReader read) {
		this.autonomie = autonomie;
		this.distance = distance;
		this.id = id;
		this.road=r;
		this.writer=w;
		this.read=read;
		this.reader= new BufferedReader(this.read);
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
	public double getMinValue() { 
//		return Math.min(getAutonomieTick(), getDistanceTick());
//		return (isLeader()) ?
//		Math.min(this.autonomie * (this.DEC_DISTANCE / DEC_LEADER), this.getDistance()) :
//		Math.min(this.autonomie * (this.DEC_DISTANCE / DEC_ENERGY), this.getDistance());
		return Math.min(getAutonomieDistance(),this.getDistance());
}
	
	public double getMinValueLeader() {
		return Math.min((autonomie)*(DEC_DISTANCE/DEC_LEADER),this.getDistance());
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
			Platoon platoon = new Platoon(this, writer,this.road, v);
		}
	}
	public void removePlatoonFromList() {
		vehiclePlatoonList.remove(myPlatoon);
	}
	public void deletePlatoon() {
		if(myPlatoon.leader==this) myPlatoon.leader=null;
		myPlatoon=null;
		idPlatoon=null;
	}
	public void updateVehicleVariables(){ //put this method inside tick to have mutant
											// the vehicles may compare their battery with the leader but his the leader did not ticked, the value may change with the monitor
		String x ="";
		this.distance -= 10;
		this.autonomie -= (this.myPlatoon == null || this == this.myPlatoon.leader) ? DEC_LEADER : DEC_ENERGY;
		if(this.distance < 0 ) {
			x = "Error : Distance left negative !";
			System.out.println(x);
			writer.println(x);
		}
		else if( this.autonomie < 0){
			x = "Error : Battery left negative !";
			System.out.println(x);
			writer.println(x);
		}
	}
	public void tick() {
		String x ="";

		// TODO
		//Add each tick only new policies will be added
		if(myPlatoon!=null) {
			// distance < seuil --> quitte le peloton
			if(distance < VLOW_DIST) {
				Element elt = new Element(PolicyName.QUITPLATOON, Priority.HIGH, this);
				x = "Event : vehicle " + this.getId() + " is very close from destination [VLOW_DIST]";
				System.out.println(x);
				writer.println(x);
				myPlatoon.policies.addElement(elt); 
				writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
			}
			else if(distance < LOW_DIST) {
				Element elt = new Element(PolicyName.QUITPLATOON, Priority.LOW, this);
				x = "Event : vehicle " + this.getId() + " is close from destination [LOW_DIST]";
				System.out.println(x);
				writer.println(x);
				myPlatoon.policies.addElement(elt); 
				writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
			}
			if(autonomie< LOW_BATTERY) {
				Element elt = new Element(PolicyName.QUITFAILURE, Priority.HIGH, this);
				x = "Event : vehicle " + this.getId() + " is low on energy [LOW]";
				System.out.println(x);
				writer.println(x);
				myPlatoon.policies.addElement(elt);
				writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());

			}
			if (this == myPlatoon.leader && this.getMinValue() < LOW_LEADER_DIST) { // (autonomie < LOW_LEADER_BATTERY || distance < LOW_LEADER_DIST )) {
				x = "Event : vehicle " + this.getId() + " should relay soon [HIGH]";
				System.out.println(x);
				writer.println(x);
				Element elt = new Element(PolicyName.RELAY, Priority.HIGH, this);
				myPlatoon.policies.addElement(elt);	
				writer.println(" minvalue :" + this.getMinValue());

			}
			if((this.getAutonomieDistance() -10.0)< (road.distanceStation[0] +road.distanceStation[1])){ //keep a margin of 10 
				x = "Event : vehicle " + this.getId() + " is taking next station stop [NEXT_STATION]";
				System.out.println(x);
				writer.println(x);
				if(road.distanceStation[0] < 50) { //verifying adding priority does nto causes bugs
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [HIGH]";
					System.out.println(x);
					writer.println(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.HIGH, this);
					myPlatoon.policies.addElement(elt);
					writer.println(this.getAutonomieDistance() + " " + this.road.distanceStation[0]+ " "+ this.road.distanceStation[1]);
					writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
				}
				else if(road.distanceStation[0] < 70) { //road.distanceStation[0] >= 8 && 
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [MEDIUM]";
					System.out.println(x);
					writer.println(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.MEDIUM, this);
					myPlatoon.policies.addElement(elt);
					writer.println(this.getAutonomieDistance() + " " + this.road.distanceStation[0]+ " "+ this.road.distanceStation[1]);
					writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
				}
				else if(road.distanceStation[0] <= 100) {
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [LOW]";
					System.out.println(x);
					writer.println(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, Priority.LOW, this);
					myPlatoon.policies.addElement(elt);
					writer.println(this.getAutonomieDistance() + " " + this.road.distanceStation[0]+ " "+ this.road.distanceStation[1]);
					writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
				}
				else {
					x = "Error : Should not happen QuitToStation policy problem " + road.distanceStation[0];
					System.out.println(x);
					writer.println(x);
				}
			}
			writer.println("nb vehicles"+ myPlatoon.vehiclesList.size() + "minval " + this.getMinValue()/DEC_LEADER+ "leader min val " +myPlatoon.leader.getMinValue());
			if(!this.isLeader() && myPlatoon.vehiclesList.size()>=3 && ((this.getMinValue()/DEC_LEADER)> myPlatoon.leader.getMinValue())) {
				Element elt = new Element(PolicyName.UPGRADERELAY, Priority.MEDIUM, this);
				myPlatoon.policies.addElement(elt);
				writer.println(" nb policies :" + myPlatoon.policies.listPolicy.size());
			}
			
			//leader ask platoon to choose wich adaptation policy to choose
			if(this == myPlatoon.leader) {
			//	myPlatoon.tick();
			}
			// leader && [second plus de batterie] --> relai
		}
		else {
			if ((getAutonomieDistance() -10.0)< (road.distanceStation[0] +road.distanceStation[1]) && road.distanceStation[0] < 11) {
				x = "Event : vehicle " + this.getId() + " is refilling [REFILL] and getAutonomyvalue gives : " + getAutonomieDistance();
				writer.println(x);
				refill();	
			}
		}
		
	}
	
//	public void parsingFile() {
//		String arrayString[] = new String[3];
//		String arraySplit[];
//		boolean event = true;
//		while(!arrayString[0].contains("when") && !arrayString[0].contains("End of policies")) {
//			try {
//				arrayString[0]=reader.readLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if(arrayString[0].contains("when")) {
//			try {
//				arrayString[1]=reader.readLine();
//				arrayString[2]=reader.readLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			arrayString[0].replaceAll("when", "");
//			arraySplit=arrayString[0].split("=");
//			arraySplit[arraySplit.length-1].replaceAll(" ","");
//			event = (arraySplit[arraySplit.length-1] == "true") ? true : false; 
//		}
//	}
	public double getDistanceTick() {
		return distance/DEC_DISTANCE;
	}
	public double getAutonomieDistance() {
		if(isLeader()) {
			return autonomie*(DEC_DISTANCE/DEC_LEADER);
		}
		else {
			return autonomie*(DEC_DISTANCE/DEC_ENERGY);
		}
	}


	public boolean isLeader() {
		if(this.myPlatoon !=null) {
			return this.myPlatoon.leader ==this;
		}
		else {
			return false;
		}
	}
	public String getDisplayString() {
		String l = (myPlatoon != null && myPlatoon.leader == this) ? "*" : "";
		return "id: " + id.toString().split("-")[0] + l + ", auto: " + autonomie + ", distance: " + distance + " platoon: " + myPlatoon + " next station: "+ road.distanceStation[0];
	}

}
