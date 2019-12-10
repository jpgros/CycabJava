package SUT;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import javax.sql.PooledConnection;

import sun.tools.tree.IncDecExpression;

import static sun.tools.java.Constants.DEC;


public class Vehicle extends Entity implements Serializable {
	double autonomie;
	double distance;
	double position;
	double speed;
	UUID id;
	UUID idPlatoon = null;
	ArrayList<Entity> vehiclePlatoonList= new ArrayList<Entity>();
	Platoon myPlatoon = null;
	Road road = null;
	//String read = null;
	BufferedReader reader = null;
	
	final double DEC_ENERGY; // = 1 + Math.random() / 5;
	final double DEC_LEADER;// = DEC_ENERGY * 1.2;
	final double DEC_DISTANCE = 10;
	final static double LOW_LEADER_DIST = 200;
	final static double LOW_DIST = 100.0;
	final static double VLOW_DIST = 50.0;
	final static double LOW_LEADER_BATTERY = 20;
	final static double LOW_BATTERY = 14; // should be > property3
	final static double HIGHPRIO = 7;
	final static double MEDIUMPRIO = 5;
	final static double LOWPRIO = 3;
//	final static double HIGH_PRIO = 100;
//	final static double MEDIUM_PRIO = 150;
//	final static double LOW_PRIO = 200;
//	final static double HIGH_PRIO_RELAY = 150;
//	final static double MEDIUM_PRIO_RELAY_TICK = 20;
//	final static double LOW_PRIO_RELAY_TICK = 25;
//	final static double VLOW_BATTERY = 5;
	

	public Vehicle (double autonomie, double distance, UUID id, ArrayList<Entity> vehiclePlatoonList, Road r, double decAuto, double position, double speed) {
		this.autonomie = autonomie;
		this.distance = distance;
		this.id = id;
		this.road=r;
		this.DEC_ENERGY=decAuto; //no /10 normally
		this.position= position;
		this.speed=10;
		double dc = decAuto/10.0;
		DEC_LEADER= DEC_ENERGY * 1.2; //1.2 normally
		//System.out.println("is " + decAuto + " and " + DEC_LEADER+" would have " + dc + " and " + dc*10.0);
//		this.reader= new BufferedReader(this.read);
	}
	public void cleanVl(){
		if(isLeader()) myPlatoon.cleanPl();
		vehiclePlatoonList.clear();
	}
	public void refill() {
		if(this.myPlatoon==null) {
			String x="Refill vehicle " + id;
			//System.out.println(x);
			road.addStringWriter(x);
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
			road.addStringWriter("vehicle " + this.getId() + " force-quitted platoon "+ this.getPlatoon().id  );
			myPlatoon.deleteVehicle(this);
		}
	}

	public Platoon getPlatoon() {
		return myPlatoon;
	}

	public boolean join(Vehicle v) {
		if (this.isTakingNextStation() || v.isTakingNextStation()) {
			System.out.println("taking");
			return false;
		}
		else if((this.myPlatoon != null && v.myPlatoon!=null)) { // if two vehicle are inside a platoon, we ask to merge platoons if possible
			//TODO add condition to see which vehicle add
			System.out.println("merging occuring");
			v.myPlatoon.mergePlatoons(this.myPlatoon);
			return true;
		}
		else if (v.myPlatoon != null) {
			v.myPlatoon.accept(this);
			return true;
		}
		else {
			return v.createPlatoon(this);
		}
	}
	
//	public String join(Vehicle v) {
//		if (this.myPlatoon != null || this.isTakingNextStation() || v.isTakingNextStation())
//			{
//			System.out.println("which one is penible "+String.valueOf(this.myPlatoon != null) + String.valueOf(this.isTakingNextStation()) + String.valueOf(v.isTakingNextStation()));
//			return "falseeee";
//			}
//		if (v.myPlatoon != null) {
//			v.myPlatoon.accept(v);
//			return "true";
//		}
//		else {
//			return String.valueOf(v.createPlatoon(this));
//		}
//	}

	public void setPlatoon(Platoon p) {
		myPlatoon = p;
	}

	public boolean createPlatoon(Vehicle v) {
		// TODO -- adaptation policy
		if (this.autonomie>LOW_LEADER_BATTERY+DEC_LEADER) {
			Platoon platoon = new Platoon(this,this.road, v);
			return true;
		}
		return false;
	}
	public void removePlatoonFromList() {
		vehiclePlatoonList.remove(myPlatoon);
	}
	
	public void deletePlatoon() {
		if(myPlatoon.leader==this) myPlatoon.leader=null;
		myPlatoon=null;
		idPlatoon=null;
	}
	
	/*public void deletePlatoon() {
		road.addStringWriter("Deleting platoon" + myPlatoon.vehiclesList+ "\n");
		//vehiclesList.get(0).removePlatoonFromList();
		//road.lastReconfList.add(this.lastReconf);
		for(int i =0; i<myPlatoon.vehiclesList.size(); i++) {
			myPlatoon.vehiclesList.get(i).idPlatoon=null;
		}
		myPlatoon.leader=null;
		myPlatoon.vehiclesList.clear();
		myPlatoon.policies=null;
		road.numberPlatoon--;
		myPlatoon=null;
		idPlatoon=null;
	}*/
	
	public void updateVehicleDistance(){ //put this method inside tick to have mutant
											// the vehicles may compare their battery with the leader but his the leader did not ticked, the value may change with the monitor
		String x ="";
		this.distance -= speed;
		if(this.distance < 0 ) {
			x = "Error : Distance left negative !" +"\n";
			System.out.print(x);
			road.addStringWriter(x);
		}
	}
	
	public double updatevehicleAutonomie() {
		String x="";
		double conso = this.autonomie;
		this.autonomie -= (this.myPlatoon == null || this == this.myPlatoon.leader) ? DEC_LEADER : DEC_ENERGY;
		//System.out.println("my auto "+ this.id + " "+ this.autonomie + " my platoon " + this.myPlatoon);
		if( this.autonomie < 0){
			x = "Error : new Battery left negative !"+"\n";
			System.out.print(x);
			System.out.println("id " +this.id + " position" + this.position + " next station " + road.stationPositions.get(road.getNextStation(position))+ " previous station " + road.stationPositions.get(road.getNextStation(position)-1));
			road.addStringWriter(x);
		}
		return conso -this.autonomie;
	}
	
	public void updateVehiclePosition() {
		this.position+=this.speed;
	}
	public void tick() {
		String x ="";
		//Add each tick only new policies will be added
		int indNextStation = road.getNextStation(this.position);		
		double distanceStations= road.stationPositions.get(indNextStation+1) - position;
		if(myPlatoon!=null) {
			// distance < seuil --> quitte le peloton
			if(distance < VLOW_DIST) {
				Element elt = new Element(PolicyName.QUITDISTANCE, HIGHPRIO+road.k[0], this);
				x = "Event : vehicle " + this.getId() + " is very close from destination [VLOW_DIST]"+"\n";
				System.out.print(x);
				road.addStringWriter(x);
				myPlatoon.policies.addElement(elt,road.mutant);
				road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				road.addStringWriter("platoon quitfail"+ myPlatoon + "distance " + distance);
			}
			if(distance < LOW_DIST) {
				Element elt = new Element(PolicyName.QUITDISTANCE, LOWPRIO+road.k[1], this);
				x = "Event : vehicle " + this.getId() + " is close from destination [LOW_DIST]"+"\n";
				System.out.print(x);
				road.addStringWriter(x);
				myPlatoon.policies.addElement(elt,road.mutant);
				road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
			}
			if(autonomie< LOW_BATTERY) {
				Element elt = new Element(PolicyName.QUITENERGY, HIGHPRIO+road.k[2], this);
				road.addStringWriter(x);
				System.out.println(" nb policies before :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				
				myPlatoon.policies.addElement(elt,road.mutant);
				//System.out.println("elt to be added " + elt);
				x = "Event : vehicle " + this.getId() + " is low on energy [LOW]"+"\n" + "platoon reconfs "+ myPlatoon + " "+ this.myPlatoon.policies + "size " + "\n";
				System.out.print(x);
				System.out.println("my platoon list vl ");
				for(Vehicle vl: myPlatoon.vehiclesList) {
					if(this.id==vl.id) System.out.println("vehicle is inside pl " + this.id);
				}
				System.out.println(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+"\n");

			}
			if (this == myPlatoon.leader && (this.autonomie <= LOW_LEADER_BATTERY)) {//  (this.getMinValueLeader() <= LOW_LEADER_DIST)){ 
				//this.getMinValue() <= LOW_LEADER_DIST || this.getAutonomie() < LOW_LEADER_BATTERY)) {
				x = "Event : vehicle " + this.getId() + " should relay soon [HIGH]"+"\n";
				//System.out.print(x);
				road.addStringWriter(x);
				Element elt = new Element(PolicyName.RELAY, HIGHPRIO+road.k[3], this);
				myPlatoon.policies.addElement(elt,road.mutant);
				road.addStringWriter(" minvalue :" + this.getMinValue()+"\n");
			}
			if(isTakingNextStation()){ //keep a margin of 10 
				//if(this.getAutonomieTick()-1.0 < this.getTwoNextStationsTicks()) { equivalent condition
				x = "Event : vehicle " + this.getId() + " is taking next station stop [NEXT_STATION]"+"\n";
				//System.out.print(x);
				road.addStringWriter(x);
				if(road.stationPositions.get(indNextStation) -position < 70) { //verifying adding priority does not causes bugs
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [HIGH]";
					//System.out.println(x);
					road.addStringWriter(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, HIGHPRIO+road.k[4], this);
					myPlatoon.policies.addElement(elt,road.mutant);
					road.addStringWriter(this.getAutonomieDistance() + " " + road.stationPositions.get(indNextStation)+ " "+ road.stationPositions.get(indNextStation+1)+"\n");
					road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				}
				else if(road.stationPositions.get(indNextStation)-position <= 85) { //road.distanceStation[0] >= 8 && 
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [MEDIUM]"+"\n";
					//System.out.print(x);
					road.addStringWriter(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, MEDIUMPRIO+road.k[5]+1.0, this);
					myPlatoon.policies.addElement(elt,road.mutant);
					road.addStringWriter(this.getAutonomieDistance() + " " + road.stationPositions.get(indNextStation)+ " "+ road.stationPositions.get(indNextStation+1)+"\n");
					road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				}
				else if(road.stationPositions.get(indNextStation)-position <= 100) {
					x = "Event : vehicle " + this.getId() + " QUITFORSTATION [LOW]"+"\n";
					//System.out.print(x);
					road.addStringWriter(x);
					Element elt = new Element(PolicyName.QUITFORSTATION, LOWPRIO+road.k[6], this);
					myPlatoon.policies.addElement(elt,road.mutant);
					road.addStringWriter(this.getAutonomieDistance() + " " + road.stationPositions.get(indNextStation)+ " "+ road.stationPositions.get(indNextStation+1)+"\n");
					road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+ "pl id" + myPlatoon.id+ "\n");
				}
				else {
					x = "Error : Should not happen QuitToStation policy problem " + road.stationPositions.get(indNextStation)+"\n";
					//System.out.print(x);
					road.addStringWriter(x);
				}
			}									
			
			if(!this.isLeader() && myPlatoon.vehiclesList.size()>=3 && ((this.getMinValue()/DEC_LEADER)> myPlatoon.leader.getMinValue()) && (autonomie > LOW_LEADER_BATTERY)) {
				Element elt = new Element(PolicyName.UPGRADERELAY, MEDIUMPRIO+road.k[7]+0.4, this);
				myPlatoon.policies.addElement(elt,road.mutant);
				road.addStringWriter(" nb policies :" + myPlatoon.policies.listPolicy.size()+"\n");
				road.addStringWriter("upgrade decleader = " + DEC_LEADER);
			}
		}
		else {
			if ((getAutonomieDistance() -3*speed)< (distanceStations) && road.stationPositions.get(indNextStation)-position < 11) {   //3*speed to keep a margin (gives [rarely] battery left negative either)
				x = "Event : vehicle " + this.getId() + " is refilling [REFILL] and getAutonomyvalue gives : " + getAutonomieDistance()+"\n";
				road.addStringWriter(x);
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

	/**
	 * Says if a vehicle is taking the next station on road
	 * @return boolean true if quitting false if not
	 */
	public boolean isTakingNextStation() {
		int indNextStation = road.getNextStation(this.position);
//		if(this.id.equals(UUID.fromString("05f5a6c3-4781-4ffd-b623-bb115e72f01d"))) {
//			System.out.println("tick vl " + id + " energy " +autonomie);
//			System.out.println("position " + position+ "nextStation "+ road.stationPositions.get(indNextStation) + " nextnext station " + road.stationPositions.get(indNextStation+1));
//		}
		//double distance=road.stationPositions.get(indNextStation) + road.stationPositions.get(indNextStation+1);
		//if(this.position + this.getMinValue()-10 <road.stationPositions.get(indNextStation+1))
		return this.position + this.getMinValue()-25 <road.stationPositions.get(indNextStation+1) ?true : false; //25 to keep a margin
		//return ((this.position+ this.getMinValue() -10.0)< (distance)) ? true :false;
	}
	/**
	 * gives the number of ticks associated to a distance
	 * @return the number of ticks
	 */
	public double getDistanceTick() {
		return distance/speed;
	}
	
	public double getTwoNextStationsTicks() {
		int indNextStation = road.getNextStation(this.position);	
		return (road.stationPositions.get(indNextStation) + road.stationPositions.get(indNextStation+1))/DEC_DISTANCE;
	}
	public double getAutonomieTick() {
		if(isLeader()) return autonomie/DEC_LEADER; 
		else return autonomie/DEC_ENERGY;
	}
	public double getAutonomieDistance() {
		if(isLeader()|| this.myPlatoon==null) {
			return autonomie*(speed/DEC_LEADER);
		}
		else {
			return autonomie*(speed/DEC_ENERGY);
		}
	}
	public String getStatus() {
		if(isLeader()) return "Leader";
		else if(myPlatoon==null) return "Solo";
		else return "Platooned";
	}

	public boolean isLeader() {
		if(this.myPlatoon !=null) {
			return this.myPlatoon.leader.id ==this.id;
		}
		else {
			return false;
		}
	}
	public String getDisplayString() {
		String l = (myPlatoon != null && myPlatoon.leader == this) ? "*" : "";
		return "id: " + id.toString().split("-")[0] + l + ", auto: " + autonomie + ", distance: " + distance + " platoon: " + myPlatoon + "next station ";
	}
	public String toString() {
		String s = id + " "+ autonomie + " "+ DEC_ENERGY + " "+ distance;
		if (myPlatoon!=null) s+=  " "+ myPlatoon.id;
		return s;
	}

}
