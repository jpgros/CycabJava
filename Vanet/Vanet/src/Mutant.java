
public enum Mutant {
NONE,// Normal execution
M1,// Property 3 : the minimum level his changed (from 0 to 20\%) the property is too strict and makes the system fail when it should not. DONE
M2,// This mutant update all the variables even if the external event is not quiescence (delta),
M3,// Distance station decreases 10 times less than in normal execution. DONE
M4,// This mutant modify the minimum required level of battery of a leader (+5\% .\\ required) it verifies that property 4 verify the system. DONE
M5,// This mutant does not do a reconfiguration with a 90\% probability. DONE
M6,// Property modified and requires at least 3 vehicles. DONE
M7,// The reconfiguration QuitForstation is modified and triggers with a level of battery lower than in reference implementation. In consequence the vehicles may the platoon too late and cannot refill because they must quit a platoon before refilling.\\
M8,// In order to verify the property 5, we let the possibility of vehicles to refill even if they are in platoon.
M9,// This mutant modifies the relay reconfiguration, it suppress the leader instead of replacing by another one.
M10,// Chooses randomly reconfiguration without taking utility value into account.
M11,// Chooses reconfiguration in inverted order of utility. DONE
M13// NOT A MUTANT This mutant changes the test generation probabilities, in some case we reduced the probability of event to occur, in other cases we increased the probability.
}