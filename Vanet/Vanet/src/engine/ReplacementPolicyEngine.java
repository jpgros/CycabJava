package engine;

import individual.Individual;
import individual.Population;

import java.util.ArrayList;

public abstract class ReplacementPolicyEngine {

    public abstract void replace(EvaluationIndividualEngine evaluation_indiv_engine, EvaluationEngine evaluation_engine, Population population, ArrayList<Individual> children);
    public abstract String getName();
}
