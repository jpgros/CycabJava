package engine;

import java.util.ArrayList;

import individual.Individual;
import individual.Population;

public abstract class SelectionEngine 
{
	public abstract ArrayList<Individual> select (EvaluationEngine evaluation, Population population);
	public abstract String getName();
}
