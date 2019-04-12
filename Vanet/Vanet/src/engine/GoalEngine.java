package engine;

import individual.Population;

public abstract class GoalEngine
{
	public abstract boolean searchShouldStop (GeneticEngine genetic, Population population);
	public abstract String getName();
}
