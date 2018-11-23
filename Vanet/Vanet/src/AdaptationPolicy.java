import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections15.functors.SwitchClosure;

public class AdaptationPolicy implements Serializable{
	ArrayList<Element> listPolicy= new ArrayList<Element>();
	
	public void addElement(Element elt) {
		if(listPolicy.size()==0) {
			listPolicy.add(0, elt);
			System.out.println("elt added " + elt);
		}
		//if the element is already present with a higher or equal priority, no need to add the selected elt
		else {
			int index=0;
			boolean looping = true;
			//int indexElt= this.containsName(elt.name);
			
			//if(indexElt !=-1) {
				switch(elt.priority) {
				case HIGH :
					listPolicy.add(0, elt);
					System.out.println("elt added high " + index + "size list" + listPolicy.size()+ " " + elt);
				break;
				case MEDIUM :
					index=0;
					do{
					looping = listPolicy.get(index).getPriority()==Priority.HIGH;
					index++;
				}while(looping && index<listPolicy.size());
				listPolicy.add(--index, elt);
				System.out.println("elt added med " + elt + " " + index + "size list" + listPolicy.size());
				index=0;
				break;
				
				case LOW :	
//					while(looping && index<listPolicy.size()) {
//					index--;
//					looping = listPolicy.get(index).getPriority()==Priority.LOW;
//				}
				index= listPolicy.size();
				listPolicy.add(index, elt);
				System.out.println("elt added low " + elt+ " " + index + "size list" + listPolicy.size());
				break;
				
				default :
					
					break;
				}
			//}
			//if( (indexElt !=-1 && elt.priority.value > listPolicy.get(indexElt).getPriority().value) || indexElt ==-1 ) {
			//if element already exists but with a lesser priority we remove it
				
//				if (indexElt !=-1 && elt.priority.value > listPolicy.get(indexElt).getPriority().value ) {
//					listPolicy.remove(indexElt);
//				}
//				if(elt.getPriority()==Priority.HIGH) {
//					while(looping && indexSup<listPolicy.size() ) {
//						looping = listPolicy.get(indexSup).getPriority()==Priority.HIGH;
//						indexSup++;
//					}
//					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
//					listPolicy.add(randomNum, elt);
//				}
//				if(elt.getPriority()==Priority.MEDIUM) {
//					while(looping && indexInf<listPolicy.size() ) {
//						looping = listPolicy.get(indexSup).getPriority()==Priority.HIGH;
//						indexInf++;
//					}
//					looping=true;
//					indexSup=indexInf;
//					while(looping && indexSup<listPolicy.size() ) {
//						looping =listPolicy.get(indexSup).getPriority()==Priority.MEDIUM;
//						indexSup++;
//					}
//					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
//					listPolicy.add(randomNum, elt);
//				}
//				if(elt.getPriority()==Priority.LOW) {
//					indexSup = listPolicy.size()-1;
//					indexInf=indexSup;
//					while(looping && indexInf <0 ) {
//						looping =listPolicy.get(indexSup).getPriority()==Priority.LOW;
//						indexInf--;
//					}
//					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
//					listPolicy.add(randomNum, elt);
//				}
//			}
		}
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


	public String toString() {
		return listPolicy.toString();
	}
}

