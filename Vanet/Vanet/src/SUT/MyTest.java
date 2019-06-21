package SUT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple class encapsulating a test as a sequence (ArrayList) of String representing action names. 
 */
public class MyTest implements Iterable<MyStep> ,Serializable{
	double score=0;
    ArrayList<MyStep> steps;
    public MyTest() {
        steps = new ArrayList<MyStep>();
    }

    public void append(MyStep step) {
        steps.add(step);
    }

    public int size() {
        return steps.size();
    }

    public MyStep getStepAt(int i) {
        return steps.get(i);
    }

    public String toString() {
        return steps.toString();
    }

    public Iterator<MyStep> iterator() {
        return steps.iterator();
    }

	public void add(MyStep step) {
		steps.add(step);
	}

}