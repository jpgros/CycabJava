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
		else if (!(indexElt !=-1 && elt.priority.value <= listPolicy.get(indexElt).getPriority().value)) {
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
				while(looping && indexSup<listPolicy.size() ) {
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
	
	public int containsName(String n) {
		boolean notFounded = true;
		int index =0;
		while(notFounded && index < listPolicy.size()) {
			notFounded= n==listPolicy.get(index).getName();
			index++;
		}
		if (index >=listPolicy.size()) return -1;
		return index;
	}
	
}

