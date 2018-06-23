import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 30/05/2018
 * Time: 11:09
 */
public class AdaptationPolicyModel {
	Element lastTriggeredReconf=null;
    // Set of rules <TP --> PropertyAutomaton, config --> PropertyAutomaton, Reconf --> PolicyName (ENUM), Priority --> Priority (ENUM) >

    ArrayList<Rule> rules = new ArrayList<Rule>();
    ArrayList<Rule> candidateReconfigurations = new ArrayList<Rule>();

    public void addRule(Rule r) {
        rules.add(r);
    }

    int compteur = 0;

    public void match(Road sut, ExecutionReport er) {
    	   for (Rule r : rules) {
               for (Vehicle v : sut.allVehicles) {
                   if (r.matches(sut, v, er)) {
                       er.notifyStepBefore(compteur, r, v);
                   }
               }
           }
        // check reconfig effective du SUT
        if (compteur > 0) {
            for (Vehicle v : sut.allVehicles) {
                if (v.myPlatoon != null && v.myPlatoon.leader == v && v.myPlatoon.lastReconf != null) {
                    er.notifyStepAfter(compteur, v.myPlatoon.lastReconf,lastTriggeredReconf);
                }
            }
            
//            while(sut.lastReconfList.size()>0) { //if a platoon get deleted, the last reconf is retrieved here
//            	Element elt = sut.lastReconfList.remove(0);
//
//            	if(elt!=null) {
//            		System.out.println("lastrecfonfounded");
//            		if(lastTriggeredReconf!=null) {
//            			System.out.println("lastreconftriggered founded");
//            		if(!lastTriggeredReconf.equals(elt)) {
//            			System.out.println("same last reconfs");
//		            	lastTriggeredReconf=null;
//		            	System.out.println("elt = " +elt.priority);
//		            	sut.writer.println("elt = " +elt.priority);
//		            	er.notifyStepAfter(compteur, elt,lastTriggeredReconf);
//		            	System.out.println("Retrieved last reconf of platoon before deletion");
//		            	sut.writer.println("Retrieved last reconf of platoon before deletion");
//		            	compteur++; 
//	            	}
//            		}
//            	}
//           }
        }
        compteur++;
    }
}

class Rule {

    VanetProperty TP;
    VanetProperty config;
    PolicyName reconf;
    Priority prio;
    
    public Rule(VanetProperty TP, VanetProperty config, PolicyName reconf, Priority prio) {
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


    public boolean matches(Road sut, Vehicle v, ExecutionReport er) {

        try {

            TP.setCurrentVehicle(v);
            TP.match(sut);
            er.notifyTP(this, v, TP);
            config.setCurrentVehicle(v);
            config.match(sut);
            er.notifyConfig(this, v, TP);
            return true;
        }
        catch (PropertyFailedException pfe) {
            
        }
        return false;
    }
}

class ExecutionReport {

    HashMap<Integer, Pair<ArrayList<Element>, ArrayList<Element>>> steps = new LinkedHashMap<Integer, Pair<ArrayList<Element>, ArrayList<Element>>>();
    
    HashMap<PropertyAutomaton,Integer> occurrences = new HashMap<PropertyAutomaton, Integer>();
    PrintWriter writerErr =null;
    
    public ExecutionReport(PrintWriter w) {
    	writerErr = w;
    }
    
    public void notifyConfig(Rule rule, Vehicle v, PropertyAutomaton tp) {
        if (occurrences.get(tp) == null) {
            occurrences.put(tp, 1);
        }
        else {
            occurrences.put(tp, occurrences.get(tp) + 1);
        }
    }

    public void notifyTP(Rule rule, Vehicle v, PropertyAutomaton tp) {
        if (occurrences.get(tp) == null) {
            occurrences.put(tp, 1);
        }
        else {
            occurrences.put(tp, occurrences.get(tp) + 1);
        }
    }
    //eligible reconf
    public void notifyStepBefore(int i, Rule r, Vehicle v) {
        if (steps.get(i) == null) {
            steps.put(i, new Pair(new ArrayList<Element>(), new ArrayList<Element>()));
        }
        steps.get(i).getFirst().add(new Element(r.reconf, r.prio, v));
    }
    //actual reconf
    public void notifyStepAfter(int i, Element lastReconf, Element lastTriggeredReconf) {
    	if (steps.get(i) == null) {
            // when no reconfiguration was expected 
            steps.put(i,new Pair(new ArrayList<Element>(), new ArrayList<Element>()));
        }
        steps.get(i).getSecond().add(new Element(lastReconf.name, lastReconf.priority, lastReconf.vehicle));
        lastTriggeredReconf=lastReconf;
    }

    public void dump() {
    	int lastStep =0;
    	String x ="";
    	for (Integer step : steps.keySet()) {
    		while (lastStep < step) {
    			x = "*** Step " + lastStep;
    			System.out.println(x);
    			writerErr.println(x);
    			lastStep++;
    		}
    		x = "*** Step " + step;
            System.out.println(x);
            writerErr.println(x);
            x ="Eligible reconfigurations: " + steps.get(step).getFirst();
            System.out.println(x);
            writerErr.println(x);
            x=" --> Actual reconfiguration: " + steps.get(step).getSecond();
            System.out.println(x);
            writerErr.println(x);
           // if(steps.get(step).getSecond().size()>0 && (steps.get(step).getFirst().contains(steps.get(step).getSecond()))) {
            //if priority eligible > actual 
            // if eligible empty and not actual
            if(steps.get(step).getSecond().size()>0 && steps.get(step).getFirst().size()==0) {
            	writerErr.println("*** Step " + step + " encountered a problem : reconfiguration occured but was nout founded eligible");
                writerErr.println("Eligible reconfigurations: " + steps.get(step).getFirst());
                writerErr.println(" --> Actual reconfiguration: " + steps.get(step).getSecond());
            }
//            if(steps.get(step).getSecond().size()>0) {
//            	Element elt = steps.get(step).getSecond().get(0);
//            	ArrayList<Element> eltArray = steps.get(step).getSecond();
//            	writerErr.print("element = " +elt);
//            	if(!(eltArray.size()>0 && eltArray.contains)){
//            		
//            	}
//            }
            
//            if(steps.get(step).getSecond().size()>0 && steps.get(step).getFirst(). )==0) {
//            	writerErr.println("*** Step " + step + " encountered a problem : reconfiguration occured but was nout founded eligible");
//                writerErr.println("Eligible reconfigurations: " + steps.get(step).getFirst());
//                writerErr.println(" --> Actual reconfiguration: " + steps.get(step).getSecond());
//            }
            //}
            lastStep++;
        }
    	System.out.println("Last step with eligible reconfiguration is step " + --lastStep);
    }
}


class Pair<K, V> {
    K first;
    V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public void setFirst(K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}