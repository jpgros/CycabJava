package SUT;

import java.io.Serializable;
import java.util.ArrayList;

class StepElt implements Serializable{
	public int step=0;
	public Pair<ArrayList<Element>, ArrayList<Element>> pair;
	public StepElt() {
		pair = new Pair(new ArrayList<Element>(), new ArrayList<Element>()); //first = elig second = actual
	}
    
}