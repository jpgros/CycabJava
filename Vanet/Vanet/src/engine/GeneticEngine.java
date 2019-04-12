package engine;

//import standalone.StandaloneEngineV2;
import individual.Population;

/**
 * Uses and composes with the different other engines.
 *
 */
public abstract class GeneticEngine 
{
	public abstract Population run ();
	public abstract int getCurrentIteration ();
	//public abstract StandaloneEngineV2 getStandaloneEngine ();
	
	/* engines */
	public abstract PopulationEngine getPopulationEngine ();
	public abstract EvaluationEngine getEvaluationEngine ();
	public abstract EvaluationIndividualEngine getEvaluationIndividualEngine();
	public abstract SelectionEngine getSelectionEngine ();
	public abstract CrossoverEngine getCrossoverEngine ();
	public abstract ReplacementPolicyEngine getReplacementPolicyEngine ();
	public abstract MutationEngine getMutationEngine ();
	public abstract GoalEngine getGoalEngine ();


}
