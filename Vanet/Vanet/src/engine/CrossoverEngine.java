package engine;

import java.util.ArrayList;

//import LocusPair;
//import individual.Individual;

public abstract class CrossoverEngine
{
	public abstract ArrayList<LocusPair> findLocuses (Individual individual_1, Individual individual_2);
	public abstract ArrayList<Individual> crossover (GeneticEngine geneticEngine, Individual individual_1, Individual individual_2);
	public abstract ArrayList<Individual> crossover (Individual individual_1, Individual individual_2, Individual individual_3);
	public abstract String getName();
}
