package individual;

import java.util.*;

//import com.smartesting.standalone.engine.StandaloneEngine;
import engine.EvaluationEngine;
//import standalone.StandaloneEngineV2;
//import org.yecht.Data;

/**
 *
 */
public class Population  implements Iterable<Individual>
{


	private HashSet<Individual> individuals;
	private Set<String> tagsUnique;

	public HashMap<String,ArrayList<Individual>> tagsMap;

	private double sum_eval;
	private int nbIndividualValid;
	private int uniqueTagAmount;

	/**
	 *
	 */
	public Population (){
		this.sum_eval = 0;
		this.tagsMap = new HashMap<>();
	}
	
	public int getWeightAmount(){
		int weightAmount = 0;
		
		for(Individual indiv : this)
			weightAmount += indiv.getWeight();
		
		return weightAmount;
	}


	public int sizeHashMap(){
		return tagsMap.size();
	}

//	/**
//	 *	Insert un individu dans le HashMap
//	 * @param individual	Individu à insérer
//	 */
//	public void putIndividualInHashMap(Individual individual){
//
//		Set<String> tags = individual.getListUniqueActivatedTag();
//		ArrayList<Individual> listIndividuals;
//
//		for(String tag : tags){
//			if(this.tagsMap.containsKey(tag)) {
//
//				if(!(this.tagsMap.get(tag).contains(individual))) {
//
//					listIndividuals = tagsMap.get(tag);
//					listIndividuals.add(individual);
//
//					if (tagsMap.get(tag).size() == 1)
//						individual.incrWeight();
//
//					if (tagsMap.get(tag).size() == 2)
//						tagsMap.get(tag).get(0).decrWeight();
//				}
//			}
//			else {
//
//				listIndividuals = new ArrayList<>();
//				listIndividuals.add(individual);
//				tagsMap.put(tag,listIndividuals);
//				individual.incrWeight();
//			}
//		}
//	}

//	/**
//	 *	Retirer un individu du HashMap
//	 * @param individual	Individual à retirer
//	 */
//	public void removeIndividualInHashMap(Individual individual){
//
//		Set<String> tags = individual.getListUniqueActivatedTag();
//		ArrayList<Individual> listIndividuals ;
//
//		for(String tag : tags){
//			if(this.tagsMap.containsKey(tag)) {
//
//				listIndividuals = tagsMap.get(tag);
//				listIndividuals.remove(individual);
//
//				if(tagsMap.get(tag).size() == 0) {
//					individual.decrWeight();
//					tagsMap.remove(tag);
//				}
//
//				else if(tagsMap.get(tag).size() == 1)
//					tagsMap.get(tag).get(0).incrWeight();
//			}
//		}
//
//	}

	/**
	 *	Affiche le HashMap
	 */
	public void printIndividualInHashMap(){

		String st = "";

		ArrayList<String> tagss = new ArrayList<>();

		for (Map.Entry<String,ArrayList<Individual>> e : tagsMap.entrySet()){
			st += "\n"+e.getKey()+"";

			for(Individual i : e.getValue()){
				st += " "+i.size();
			}

		}
		System.out.println(st);
	}


	/**
	 *
	 * @return
	 */
	public int getNbIndividualValid(){
		return this.nbIndividualValid;
	}


	/**
	 * Attention(si remove ou add, individuals n'est pas modifié)
	 * @return
	 */
	public ArrayList<Individual> getIndividuals(){
		return new ArrayList<Individual>(this.individuals);
	}

	/**
	 *
	 * @param eval
	 */
	public void initialize (EvaluationEngine eval){
		this.individuals = new HashSet<>();
	}


	/**
	 *
	 * @return
	 */
	@Override
	public Iterator<Individual> iterator(){
		return this.individuals.iterator();
	}

//
//	/**
//	 *
//	 * @param engine
//	 * @return
//	 */
//	public double validationRate(StandaloneEngineV2 engine){
//
//		double rate;
//		this.nbIndividualValid = 0;
//
//		for (Individual i : this){
//			i.toValid(engine);
//			if( i.isValid())
//				nbIndividualValid++;
//		}
//
//		rate = (double)nbIndividualValid / (double)this.size();
//		return rate * 100;
//	}


	/**
	 *
	 * @param uniqueActivatedTagAmount
	 * @param maxTagAmount
	 * @return
	 */
	public double tagActivatedRate(int uniqueActivatedTagAmount, int maxTagAmount){
		double rate;
		rate = (double)uniqueActivatedTagAmount / (double)maxTagAmount;
		return rate * 100;
	}


//	/**
//	 *
//	 * @param evaluation
//	 * @param individual
//	 */
//	public void add(EvaluationEngine eval, Individual individual) {
//	
//		this.individuals.add(individual);
//		
//		startTime = System.nanoTime();
//		this.putIndividualInHashMap(individual);
//		endTime = System.nanoTime();
//
//		time += endTime - startTime;
//		this.sum_eval = eval.evaluate(this);
//		
//	}
 
	public long startTime = 0;
	public long endTime = 0;
	public long time = 0;

//	/**
//	 *
//	 * @param evaluation
//	 * @param individuals
//	 */
//	public void addAll(EvaluationEngine eval, Collection<Individual> individuals)
//	{
//		for (Individual individual : individuals) {
//			this.add(eval,individual);
//		}
//	}

//	/**
//	 *
//	 * @param evaluation
//	 * @param individual
//	 */
//	public void remove(EvaluationEngine eval, Individual individual)
//	{
//		if ( this.individuals.contains(individual) ) {
//			this.individuals.remove(individual);
//			this.removeIndividualInHashMap(individual);
//			this.sum_eval = eval.evaluate(this);
//			
//		}
//	}

	/**
	 *
	 * @param newEval
	 * @return
	 */
/*	public double diffEval(double newEval){
		return this.tagsMap.size() - newEval;
	}*/


	/**
	 * Compte le nombre d'individu qui ont un poids égal à zéro
	 * @return 		Le nombre d'individu qui ont un poids égal à zéro
	 */
	public int countNullWeight(){
		int count = 0;
		for(Individual individual : this){
			if(individual.getWeight() == 0)
				count++;
		}
		return count;
	}

	/**
	 *
	 * @return
	 */
	public double getEvaluationSum () {
		return this.sum_eval;
	}

	/**
	 *
	 * @return
	 */
	public int size () {
		return this.individuals.size();
	}

//	/**
//	 * Afficher le pourcentage d'individu valide dans la population
//	 * @param engine
//	 */
//	public void printIndividualValidRate(StandaloneEngineV2 engine){
//		System.out.println("\nINDIVIDUS VALIDES\n------------------------");
//		System.out.println("pourcentage : "+this.validationRate(engine)+"%");
//		System.out.println(this.getNbIndividualValid()+"/"+this.size());
//	}

//	/**
//	 * Afficher le pourcentage de tags activé dans la population
//	 * @param engine
//	 */
//	public void printActivatedTagRate(StandaloneEngineV2 engine){
//		System.out.println("\nTAGS ACTIVES\n---------------------");
//		int tagAmountMax = engine.getTagAmountMax();
//		double tagActivatedRate = this.tagActivatedRate((int)this.sum_eval, tagAmountMax);
//		System.out.println(" pourcentage : "+tagActivatedRate + "%");
//		System.out.println(this.sum_eval+"/"+tagAmountMax);
//	}

	/**
	 *
	 * @return
	 */
	public String toString(){
		String st = "";

		for (Individual i : this){
			st += i;
		}
		return st;
	}
}
