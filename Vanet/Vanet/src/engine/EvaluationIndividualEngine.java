package engine;

import individual.Individual;
import individual.Population;

public abstract class EvaluationIndividualEngine
{
    public abstract double evaluateIndividual(EvaluationEngine evaluationEngine, Population population, Individual individual);
    public abstract String getName();
}
