package engine;

import individual.Population;

public abstract class EvaluationEngine 
{
	public abstract double evaluate(Population population);
	public abstract String getName();
}
