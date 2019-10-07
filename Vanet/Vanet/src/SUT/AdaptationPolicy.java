package SUT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections15.functors.SwitchClosure;

public class AdaptationPolicy implements Serializable{
	ArrayList<Element> listPolicy= new ArrayList<Element>();
	ArrayList<Element> tmpListPolicy = new ArrayList<Element>();
	final static double COEFF_WAITING_RULE=0.3;
	
	// add element according to the given priority and the time rule has already waited
	public void addElement(Element elt, Mutant mutant) {
		if(mutant==Mutant.M18) {
			switch(elt.toStringShort()) {
	    		case "RELAY 7.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[0];
	    			break;
	    		case "QUITENERGY 7.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[1];
	    			break;
	    		case "QUITDISTANCE 7.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[2];
	    			break;
	    		case "QUITFORSTATION 7.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[3];
	    			break;
	    		case "QUITFORSTATION 6.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[4];
	    			break;
	    		case "UPGRADERELAY 5.4":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[5];
	    			break;
	    		case "QUITFORSTATION 3.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[6];
	    			break;
	    		case "QUITDISTANCE 3.0":
	    			elt.fairnessPoint=elt.vehicle.myPlatoon.arrayPoints[7];
	    			break;
	        		default:
	        			System.out.println("shouldn't happen adaptationPolicy class " );
	            	break;	
			}
		}
			if (listPolicy.size() == 0 || mutant== Mutant.M14) {
				listPolicy.add(elt);
	        } else if ((listPolicy.get(0).priority+ COEFF_WAITING_RULE*listPolicy.get(0).timeWaiting) > (elt.priority+ COEFF_WAITING_RULE*elt.timeWaiting)) {
	        	listPolicy.add(0, elt);
	        } else if ((listPolicy.get(listPolicy.size() - 1).priority + COEFF_WAITING_RULE*listPolicy.get(listPolicy.size()-1).timeWaiting)< (elt.priority + COEFF_WAITING_RULE*elt.timeWaiting)) {
	        	listPolicy.add(listPolicy.size(), elt);
	        } else {
	            int i = 0;
	            while ((listPolicy.get(i).priority + listPolicy.get(i).timeWaiting) < (elt.priority +COEFF_WAITING_RULE*elt.timeWaiting) ) {
	                i++;
	            }
	            listPolicy.add(i, elt);
		}
	}
	
	public void mergeLists(Mutant mutant) {
		if(mutant!=Mutant.M18) {
			ArrayList<Element> listTmp = new ArrayList<Element>(); 
			if(this.tmpListPolicy.size()!=0) {
				for(Iterator<Element> itr= listPolicy.iterator(); itr.hasNext();) {
					Element elt=itr.next();
					for(Iterator<Element> itr2=tmpListPolicy.iterator(); itr.hasNext();) {
						Element eltLast = itr.next();
						Element newElt = new Element(eltLast.getName(), eltLast.getPriority(), eltLast.getVehicle());
						if(elt.equals(eltLast) && mutant != Mutant.M17) {
							newElt.timeWaiting=++eltLast.timeWaiting;
						}
						listTmp.add(newElt);
						itr.remove();
					}
				}
				for(Element elt : listTmp) {
					addElement(elt, mutant);
				}
				listTmp.clear();
			}
			tmpListPolicy.clear();
			for(Element elt : this.listPolicy) {
				tmpListPolicy.add(elt);
			}
		}
	}
	
	public double averageValuePolicies() {
		if (listPolicy.size()==0) return 0;
		double totalPolicies=0;
		for(Element elt : listPolicy) {
			totalPolicies+= elt.priority;
		}
		return totalPolicies/listPolicy.size();
	}
	
	public int containsName(PolicyName n) {
		for(int i=0; i< listPolicy.size(); i++) {
			if(listPolicy.get(i).name==n) return i;
		}
		return -1;
	}

	public void removeForVehicle(Vehicle v) {
		int i = 0;
		while (i < listPolicy.size()) {
			if (listPolicy.get(i).vehicle == v) {
				listPolicy.remove(i);
			}
			else {
				i++;
			}
		}
	}
	/**
	 * clears policy and copy it in tmp list
	 */
	public void clearPolicy() {
		for(int i=0; i<this.listPolicy.size();i++) { //Element elt : this.listPolicy) {
			this.tmpListPolicy.add(this.listPolicy.remove(this.listPolicy.size()-1));
		}
		this.listPolicy.clear();
	}

	public String toString() {
		return listPolicy.toString();
	}
}

