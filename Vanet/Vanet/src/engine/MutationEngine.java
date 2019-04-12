package engine;

import individual.Individual;
//import standalone.StandaloneEngineV2;

public abstract class MutationEngine
{
	public abstract void mutate (Individual individual); //,StandaloneEngineV2 standaloneEngine);
	public abstract String getName();
}

