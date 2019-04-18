package SUT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SerializableTest implements Serializable{
	 ArrayList<SerializableStep> steps = new ArrayList<SerializableStep>();
	    public SerializableTest(ArrayList<SerializableStep> s) {
	    	for(SerializableStep step : s) {
	    		steps.add(step);
	    	}
		}

	    public void append(SerializableStep step) {
	        steps.add(step);
	    }

	    public int size() {
	        return steps.size();
	    }

	    public SerializableStep getStepAt(int i) {
	        return steps.get(i);
	    }

	    public String toString() {
	        return steps.toString();
	    }

	    public Iterator<SerializableStep> iterator() {
	        return steps.iterator();
	    }
}