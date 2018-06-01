import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class AdaptationPolicy {
	ArrayList<Element> listPolicy= new ArrayList<Element>();
	
	public void addElement(Element elt) {
		int indexSup =0;
		int indexInf =0;
		boolean looping = true;
		int indexElt= this.containsName(elt.name); 
		if(listPolicy.size()==0) {
			listPolicy.add(0, elt);
		}
		//if the element is already present with a higher or equal priority, no need to add the selected elt
		else {
			if( (indexElt !=-1 && elt.priority.value > listPolicy.get(indexElt).getPriority().value) || indexElt ==-1 ) {
			//if element already exists but with a lesser priority we remove it
				if (indexElt !=-1 && elt.priority.value > listPolicy.get(indexElt).getPriority().value ) {
					listPolicy.remove(indexElt);
				}
				if(elt.getPriority()==Priority.HIGH) {
					while(looping && indexSup<listPolicy.size() ) {
						looping = listPolicy.get(indexSup).getPriority()==Priority.HIGH;
						indexSup++;
					}
					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
					listPolicy.add(randomNum, elt);
				}
				if(elt.getPriority()==Priority.MEDIUM) {
					while(looping && indexInf<listPolicy.size() ) {
						looping = listPolicy.get(indexSup).getPriority()==Priority.HIGH;
						indexInf++;
					}
					looping=true;
					indexSup=indexInf;
					while(looping && indexSup<listPolicy.size() ) {
						looping =listPolicy.get(indexSup).getPriority()==Priority.MEDIUM;
						indexSup++;
					}
					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
					listPolicy.add(randomNum, elt);
				}
				if(elt.getPriority()==Priority.LOW) {
					indexSup = listPolicy.size()-1;
					indexInf=indexSup;
					while(looping && indexInf <0 ) {
						looping =listPolicy.get(indexSup).getPriority()==Priority.LOW;
						indexInf--;
					}
					int randomNum = ThreadLocalRandom.current().nextInt(indexInf, indexSup + 1);
					listPolicy.add(randomNum, elt);
				}
			}
		}
	}
	
	public int containsName(PolicyName n) {
		boolean notFounded = true;
		int index =0;
		while(notFounded && index < listPolicy.size()) {
			notFounded= n!=listPolicy.get(index).getName();
			index++;
		}
		index--;
		if (!listPolicy.isEmpty())	index = n==listPolicy.get(index).getName() ?  index : -1;
		return index;
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
