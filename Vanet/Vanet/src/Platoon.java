import java.io.FileNotFoundException;
import java.io.IOException;
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
	final static double THRESHOLDRULESVALUE = 7;
	ArrayList<Vehicle> vehiclesList = new ArrayList<Vehicle>();
    ArrayList<Vehicle> nextLeaderList = new ArrayList<Vehicle>();
    UUID id;
	Vehicle leader=null;
	Road road = null;
	AdaptationPolicy policies =new AdaptationPolicy();
	Element lastReconf =null;
	boolean created;
	boolean review = true;
	String line;
	//int tickCounter =0;
	public Platoon(int consommationLeader, int numberVehicleMax,UUID id, Vehicle vehicleLeader) {
		this.consommationLeader = consommationLeader;
		//this.NUMBER_VEHICLE_MAX = numberVehicleMax;
		this.id = id;
		leader = vehicleLeader;
	}
	public Platoon() {   

    }
	public Platoon(Vehicle _leader, Road r, Vehicle... others) {
	    created =true;
		leader = _leader;
	    id = UUID.randomUUID();
		vehiclesList.add(leader);
		leader.setPlatoon(this);
		road =r;
		r.numberPlatoon++;
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
		if(leader==null) return -1;
		int index=0;
		boolean notFounded = true;
		while(notFounded) {
			notFounded = !(vehiclesList.get(index).getId()==leader.id);
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
		if(true) {//M3 lastReconf not reinit at the end of tick trigger so if on the next step another action than tick is used, lastreconf will apear again into actual
			lastReconf=null;
		}
			if(policies.listPolicy.size()>0) {
				if(policies.listPolicy.size()>1 && !review) {
					road.addReconfigurationChoosen("Tick " +road.stepNb);
					road.addReconfChoosenWrite("Tick " +road.stepNb );
				}
				switch(road.mutant) {
				case M5:
					if((int) Math.floor(Math.random() * 101)> 100) {// M2 randomly does not do a reconf
					if(policies.listPolicy.size()>1) {
						for(int i=0; i<policies.listPolicy.size();i++) {
							road.addReconfigurationChoosen("|"+i +";" +policies.listPolicy.get(i).getName()+";"+policies.listPolicy.get(i).getPriority()+ ";OK");
						}
						road.addReconfigurationChoosen("\n");
					}
					lastReconf = policies.listPolicy.get(0);
				}
				break;
				case M11:
					if(policies.listPolicy.size()>1) {
						for(int i=0; i<policies.listPolicy.size();i++) {
							road.addReconfigurationChoosen("|"+i +";" +policies.listPolicy.get(i).getName()+";"+policies.listPolicy.get(i).getPriority()+ ";OK");
						}
						road.addReconfigurationChoosen("\n");
					}
					lastReconf=policies.listPolicy.get(policies.listPolicy.size()-1);   //M1 replaces tickTriggerM1
				break;
				case M10:// chooses randomly a reconf
					//TODO 
				break;
				case M12:
					if(policies.listPolicy.size()>1 ) {
						if(review) {
							try {
								line = road.getLineReconfChoosenRead();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							String[] reconf;
							try {
							reconf= line.split("/");
							}catch (NullPointerException e) {
					            System.out.print("Caught the NullPointerException line " + line);
					            break;
					        }
	//						System.out.println("line "+ line);
	//						System.out.println("reconfs " +reconf[0] + " "+ reconf[1] + "  "+reconf[2] + "size" + reconf.length);
							int i=0;
							boolean looping=true;
							do {
								String splits[]=reconf[i+1].split(";");
	//							for(int k=0; k<splits.length;k++) {
	//								System.out.println("split "+ splits[k]);
	//							}	
								if(splits[splits.length-1].equals("OK")) {
									looping=false;
									//System.out.println("ok ko " +splits[splits.length-1]);
									lastReconf = policies.listPolicy.get(i);
									reconf[i+1] =reconf[i+1].replaceAll("OK", "KO");
									line="";
									policies.listPolicy.clear();
									for(String vector : reconf){
							             line += vector+"/";
							       }
									//System.out.println("adding line "+ line);
									road.addReconfChoosenWrite(line+"\n");
									road.addReconfigurationChoosen(line+"\n");
								}
								else {
									
								}
							i++;
							}while(looping && i<policies.listPolicy.size());
						}//list>1
						else {
							lastReconf = policies.listPolicy.get(0);
							for(Element elt : policies.listPolicy) {
								road.addReconfChoosenWrite("/" +elt.getName()+";"+elt.getPriority()+ ";OK");
								road.addReconfigurationChoosen("/" +elt.getName()+";"+elt.getPriority()+ ";OK");
							}
							road.addReconfChoosenWrite("\n");
							road.addReconfigurationChoosen("\n");
							policies.listPolicy.clear();
						}
					break;
					
					}
					else {
						lastReconf = policies.listPolicy.get(0);
						policies.listPolicy.clear();
					}
					break;
				default:
					if(policies.averageValuePolicies()*policies.listPolicy.size() + policies.COEFF_WAITING_RULE*policies.listPolicy.get(0).timeWaiting > THRESHOLDRULESVALUE ) {
						lastReconf = policies.listPolicy.get(0);
						policies.listPolicy.clear();
					}
				break;
				}	
				
			}//list >0
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
		if(lastReconf!=null) {// policies.listPolicy.size()>0) {			
			//lastReconf = policies.listPolicy.remove(0);
			//policies.listPolicy.clear();
		
			if(lastReconf.name == PolicyName.RELAY) {
				this.relay();
				x = "Reconfiguration : vehicle leader" + lastReconf.vehicle.getId() + " downgraded and stays : [RELAY] ; priority : {" + lastReconf.getPriority()+ "} "+ this.id + "minvalue "+ lastReconf.getVehicle().getMinValue()+ "\n"; //+ " tick : " + tickCounter;
				//System.out.print(x);
				road.addStringWriter(x);
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
		//if(vehiclesList==null) road.platoonLog+="0 \n";
//		else {
//			for(Vehicle v : this.vehiclesList) {
//				vlList+=";"+ v.id;
//			}
			//road.platoonLog+="Platoon;"+ id+";" +vehiclesList.size() +vlList+  "\n";
//		}
	}
	//returns true is there is no leader inside platoon
	public boolean noLeaderDetected() {
		for(Vehicle vl : vehiclesList) {
			if(vl.isLeader()== true) return false;
		}
		return true;
	}
	public void relayMutant() {
		this.leader=null;
	}
	
	public void relay() {
		if(!nextLeaderList.isEmpty()) {
			//System.out.print("Leader vehicle "+ leader.getId());
			if(nextLeaderList.get(0).autonomie >= leader.LOW_LEADER_BATTERY) {
				leader.
				road.addStringWriter("actual leader id " +leader.getId() + "next leader " + nextLeaderList.get(0).getId());
				this.leader = nextLeaderList.remove(0);
			}
			else{
				road.addStringWriter("No better vehicle available, Platoon deleted");
				deletePlatoon();
			}
		}
		else { // remove platoon
			road.addStringWriter("No better vehicle available, Platoon deleted");
			deletePlatoon();
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
			nextLeaderList.remove(v);
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
			road.numberPlatoon--;
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
	
	public AdaptationPolicy getPolicies() {
		return policies;
	}

	public void setPolicies(AdaptationPolicy policies) {
		this.policies = policies;
	}
	
}
