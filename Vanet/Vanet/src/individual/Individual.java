package individual;
import SUT.MyStep;
//import com.smartesting.artifacts.tests.TestRepository;
//import com.smartesting.standalone.engine.InitialState;
//import com.smartesting.standalone.engine.ModelState;
//import com.smartesting.standalone.engine.StandaloneEngine;

import java.util.*;


/**
 * An individual represent a more or less relevant test case.
 */
public class Individual implements Cloneable
{
	private ArrayList<MyStep> calls;
	private double weight;
	private boolean valid;
	private int generation;



	/**
	 *
	 */
	public Individual(){
		this.calls = new ArrayList<>();
		this.generation = 1;
		this.weight = 0;
	}

	/**
	 *
	 */
	public void incrWeight(){
		int w = (int)this.weight;
		w++;
		this.weight = w;
	}

	/**
	 *
	 */
	public void decrWeight(){
		int w = (int)this.weight;
		w--;
		this.weight = w;
	}

	/**
	 *
	 */
	public void incrGeneration(){
		this.generation++;
	}

	/**
	 *
	 * @return
	 */
	public int getGeneration(){
		return this.generation;
	}

	public void setGeneration(int generation){
		this.generation = generation;
	}

	/**
	 *
	 * @param call
	 */
	public void addCall(MyStep call) //TestStep 
	{
		this.calls.add(call);
	}

	/**
	 *
	 * @return
	 */
	public String getTitle()
	{
		return "Individual (" + this.calls.size() + "-" + this.getWeight() + ")";
	}

//	/**
//	 *
//	 * @return
//	 */
//	public String[] getPrintCalls(){
//
//		String [] res = new String[this.calls.size()];
//
//		int i=0;
//
//		for(MyStep step : this.calls) {
//			//res[i] = step.getCall() + "\n";
//			i++;
//		}
//
//		return res;
//	}

	/**
	 *
	 * @return
	 */
	public double getWeight(){
		return this.weight;
	}

	/**
	 *
	 * @param weight
	 */
	public void setWeight(double weight){
		this.weight = weight;
	}


	/**
	 *
	 * @return
	 */
	public boolean isValid(){
		return this.valid;
	}

	/**
	 *
	 * @param valid
	 */
	public void setValid(boolean valid){
		this.valid = valid;
	}

//	/**
//	 *
//	 * @param engine
//	 */
//	public void toValid(StandaloneEngine engine){
//
//		InitialState init = engine.createInitialState();
//		ModelState next = init;
//		boolean valid = true;
//
//		for(int i = 0 ; i < this.calls.size(); i++) {
//
//			next = next.animateWithoutObservations(this.calls.get(i).getCall());
//
//			if(!next.isPreviousAnimationValid())
//				valid = false;
//		}
//
//		this.setValid(valid);
//	}




	/**
	 *
	 * @return
	 */
/*	@Override
	public int hashCode() {
		return Objects.hash(calls, weight, valid, generation);
	}*/

	/**
	 *
	 * @param engine
	 */
//	public void reevalueTag(StandaloneEngine engine, int index){
//
//		InitialState init = engine.createInitialState();
//		ModelState next = init;
//
//		for(int i=index; i < this.getCalls().size(); i++){
//
//			this.getCalls().get(i).getTags().clear();
//
//			next = next.animateWithoutObservations(this.getCalls().get(i).getCall());
//
//			for (Map.Entry entry : next.getActivatedTags().entries())
//				this.getCalls().get(i).addTag((String)entry.getValue());
//		}
//	}

//
//	/**
//	 * @return
//	 */
//	public String toString()
//	{
//		String res = getTitle() + "\n \t";
//		
//		for(TestStep step : this.calls) {
//			res += step + "\n \t";
//		}
//
//		return res;
//	}


	/**
	 *
	 * @return
	 */
	//public ArrayList<TestStep> getCalls () { return this.calls; }




//	/**
//	 *
//	 * @return
//	 */
//	public Set<String> getListUniqueActivatedTag(){
//
//		Set<String> tags = new HashSet<String>();
//
//		for(TestStep step : this.calls)
//			tags.addAll(step.getTags());
//
//		return tags;
//	}

	

	/**
	 *
	 * @return
	 */
	public int size(){
		return this.calls.size();
	}

}
