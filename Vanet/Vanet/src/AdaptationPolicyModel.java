import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 30/05/2018
 * Time: 11:09
 */
public class AdaptationPolicyModel<SUT> {

    // Set of rules <TP --> PropertyAutomaton, config --> PropertyAutomaton, Reconf --> PolicyName (ENUM), Priority --> Priority (ENUM) >

    ArrayList<Rule> rules = new ArrayList<Rule>();

    ArrayList<Rule> candidateReconfigurations = new ArrayList<Rule>();

    public void addRule(Rule r) {
        rules.add(r);
    }

    public void match(SUT sut, ExecutionReport er) {
        // check reconfig effective du SUT
        // TODO  er.notifyNonExecutedPolicy
        // vide liste reconf et calcul pour le tour suivant
        candidateReconfigurations.clear();
        for (Rule r : rules) {
            if (r.matches(sut, er)) {
                candidateReconfigurations.add(r);
            }
        }
    }
}

class Rule {

    PropertyAutomaton TP;
    PropertyAutomaton config;
    PolicyName reconf;
    Priority prio;
    
    public Rule(PropertyAutomaton TP, PropertyAutomaton config, PolicyName reconf, Priority prio) {
        this.TP = TP;
        this.config = config;
        this.reconf = reconf;
        this.prio = prio;
    }

    public PropertyAutomaton getConfig() {
        return config;
    }

    public PolicyName getReconf() {
        return reconf;
    }

    public Priority getPrio() {
        return prio;
    }

    public PropertyAutomaton getTP() {
        return TP;
    }


    public <SUT> boolean matches(SUT sut, ExecutionReport er) {
        try {
            TP.match(sut);
            er.notifyTP(this, TP);

            config.match(sut);
            er.notifyConfig(this, TP);

            return true;
        }
        catch (PropertyFailedException pfe) {
            
        }
        return false;
    }
}

class ExecutionReport {
    
    public void notifyConfig(Rule rule, PropertyAutomaton tp) {
        // TODO
    }

    public void notifyTP(Rule rule, PropertyAutomaton tp) {
        // TODO
    }
}