package engine;

import java.util.ArrayList;

import SUT.MyStep;
import SUT.MyTest;
import SUT.SerializableStep;
import individual.Individual;

public class CrossOverImplem extends CrossoverEngine{



	@Override
	public ArrayList<LocusPair> findLocuses(Individual individual_1, Individual individual_2) {
		ArrayList<LocusPair> arrayLocus = new ArrayList<LocusPair>();
		LocusPair locus = new LocusPair();
		locus.ind1= individual_1.size()/2;
		locus.ind2= individual_2.size()/2;
		arrayLocus.add(locus);
		return arrayLocus;
	}

	@Override
	public ArrayList<Individual> crossover(GeneticEngine geneticEngine, Individual individual_1, Individual individual_2) {
		
		ArrayList<LocusPair> locuses = findLocuses(individual_1, individual_2);
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		ArrayList<Individual> individuals = new ArrayList<Individual>();

		//construct child1
		for(int i=0; i< locuses.get(0).ind1;i++) {
			child1.getCalls().append(individual_1.getCalls().getStepAt(i));
		}
		for(int i= locuses.get(0).ind1; i<individual_2.size();i++) {
			child1.getCalls().append(individual_2.getCalls().getStepAt(i));			
		}
		
		//construct child2
		for(int i=0; i< locuses.get(0).ind2;i++) {
			child2.getCalls().append(individual_2.getCalls().getStepAt(i));
		}
		for(int i= locuses.get(0).ind2; i<individual_1.size();i++) {
			child2.getCalls().append(individual_1.getCalls().getStepAt(i));			
		}
		
		individuals.add(child1);
		individuals.add(child2);
		return individuals;
	}

	@Override
	public ArrayList<Individual> crossover(Individual individual_1, Individual individual_2, Individual individual_3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


}
