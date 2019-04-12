package engine;


import individual.Population;

public abstract class PopulationEngine
{
	public abstract void createInitialPopulation(GeneticEngine genetic, Population population);
	public abstract String getName();
}
