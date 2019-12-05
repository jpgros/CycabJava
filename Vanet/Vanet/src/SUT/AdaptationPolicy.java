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
	
	/**
	 *  add element according to the given priority and the time rule has already waited
	 *  low priority are classed in low indexes and high priorities in high indexes
	 * @param elt element to add in the list
	 * @param mutant is evaluated to true indicates we want a mutational approach or a normal one either
	 */
	public void addElement(Element elt, Mutant mutant) {
		if(mutant==Mutant.M18) {
			Integer val =elt.vehicle.myPlatoon.hashPoints.get(elt.toStringShort());
			if(val!=null) elt.fairnessPoint=val;
			else elt.fairnessPoint=0; // if quitforstation high should be 1
		}
//		System.out.println("element to add " +elt.toString()+ "into ");
//		for(Element e : listPolicy) {
//			System.out.println(e.toString());
//		}
		if(!containsBetterElement(elt)) {
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
						else {
//							System.out.println("test equals elt name " + elt.name + "prio "+ elt.priority +" id vl "+ elt.vehicle.id );
//
//							System.out.println("elt name " + eltLast.name + "prio "+ eltLast.priority +" id vl "+ eltLast.vehicle.id );
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
	/**
	 * Says if an element is already contained a list or with a higher priority (not taking waiting time into account)
	 * @param elt to be compared
	 * @return boolean
	 */
	public boolean containsBetterElement(Element elt) {
		for(Element listElt:listPolicy) {
			if(elt.name.equals(listElt.name)&& elt.vehicle.id== listElt.vehicle.id && elt.priority<= listElt.priority) {
				return true;
			}
		}
		return false;
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
	public void clearPolicy(Mutant mutant) {
		for(int i=0; i<this.listPolicy.size();i++) { //Element elt : this.listPolicy) {
			this.tmpListPolicy.add(this.listPolicy.remove(this.listPolicy.size()-1));
		}
		this.listPolicy.clear();
//		for(Element elt : tmpListPolicy) {
//			addElement(elt, mutant);
//		}
	}

	public String toString() {
		return listPolicy.toString();
	}
}

