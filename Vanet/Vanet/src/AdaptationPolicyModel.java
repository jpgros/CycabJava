import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;

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
    boolean property =true;
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
    public void initiateMaps(Map<PolicyName, Integer> map, Map<PolicyName, Integer> map2) {
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	list.add(0);
    	list.add(0);
    	map.put(PolicyName.QUITPLATOON, 0);
    	map.put(PolicyName.QUITFORSTATION, 0);
    	map.put(PolicyName.QUITFAILURE, 0);
    	map.put(PolicyName.RELAY, 0);
    	map.put(PolicyName.UPGRADERELAY, 0);
    	//map.put(PolicyName.RUN, 0);
    	
    	map2.put(PolicyName.QUITPLATOON, 0);
    	map2.put(PolicyName.QUITFORSTATION, 0);
    	map2.put(PolicyName.QUITFAILURE, 0);
    	map2.put(PolicyName.RELAY, 0);
    	map2.put(PolicyName.UPGRADERELAY, 0);
    	//map2.put(PolicyName.RUN, 0);
    }
    public boolean containsElt(ArrayList<Element> array, Element elt) {
    	for(Element loopElt : array) {
    		if(loopElt.name == elt.name && loopElt.priority == elt.priority && loopElt.vehicle == elt.vehicle ) return true;
    	}
    	
    	return false;
    }
    public void stat() {
       	ArrayList<Element> prevElig = new ArrayList<Element>();
       	ArrayList<ArrayList<Element>> eligSorted = new ArrayList<ArrayList<Element>>() ;
       	ArrayList<Element> tmpList = new ArrayList<Element>();
       	for (Integer step : steps.keySet()) {
	        ArrayList<Element> elt = steps.get(step).getFirst();       
	        ArrayList<Element> actualElig = new ArrayList<Element>();
	        tmpList.clear();
	        //removing elts that are no longer eligible
	        System.out.println("prev Elig " + prevElig);
	        for(Element singleElt : prevElig) {
	        	if(!containsElt(elt, singleElt)) {
	        		tmpList.add(singleElt);
	        		System.out.println("elt adding : " + elt + "singleElt " +singleElt);
	        	}
	        	else {
	        		System.out.println("BINGO");
	        		System.out.println("elt " + elt + "singleElt " + singleElt);
	        	}
	        	
	        }
	        System.out.println("tmp list equals prevElig " + tmpList.equals(prevElig));
	        for(Element singleElt : tmpList) {
	        	prevElig.remove(singleElt);
	        }
	        
	        
	        // adding new eligible elts
	        for(Element singleElt : elt) {
	        	if(!containsElt(prevElig,singleElt)) {
	        		prevElig.add(singleElt);
	        		actualElig.add(singleElt);
	        	}
	        }
	        System.out.println("prev Elig " + prevElig);
	        eligSorted.add(actualElig);
	        
    	}
    	try {
			
			PrintWriter writerElig2 = new PrintWriter("./outputeligSorted.txt", "UTF-8"); 
			PrintWriter writerelig = new PrintWriter("./outputelig.txt", "UTF-8");
			writerelig.print("oyo");
			for (Integer step : steps.keySet()) {
	    		writerelig.println("elig brut :  " + steps.get(step).getFirst());
	    		
	    	}
			for(ArrayList<Element> eltelig : eligSorted) {
				writerElig2.println(("elig trie : " + eltelig));
			}
			writerelig.close();
			writerElig2.close();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	
    }
    
    public void dump() {
    	Map<PolicyName, Integer> eligibleMap = new HashMap<PolicyName, Integer>();
    	Map<PolicyName, Integer> actualMap = new HashMap<PolicyName, Integer>();
    	
    	//eligible map were successive eligible reconfigurations are suppressed
    	int eligLow=0;
    	int eligMed=0;
    	int eligHigh=0;
    	int actuLow=0;
    	int actuMed=0;
    	int actuHigh=0;
    	//Map<PolicyName, List<Integer>> eligibleActualCounter = new HashMap<PolicyName, List<Integer>>();
    	initiateMaps(eligibleMap,actualMap);
    	int lastStep =0;
    	String x ="";
    	for (Integer step : steps.keySet()) {
    		while (lastStep < step) {
    			x = "*** Step " + lastStep;
    			System.out.println(x);
    			//writerErr.println(x);
    			lastStep++;
    		}
    		x = "*** Step " + step;
            System.out.println(x);
            //writerErr.println(x);
            ArrayList<Element> elt = steps.get(step).getFirst();

            ArrayList<Element> elt2 = steps.get(step).getSecond();
            x ="Eligible reconfigurations: " + elt;
            System.out.println(x);
            //writerErr.println(x);
            for(Element elig : elt) {
            	eligibleMap.put(elig.name, eligibleMap.get(elig.name) + 1);
            	if(elig.name==PolicyName.RELAY) eligHigh ++;
            	
//            	switch(elig.priority) {
//            	case LOW :
//            		eligLow++;
//            		break;
//            	case MEDIUM:
//            		eligMed++;
//            		break;
//            	case HIGH: 
//            		eligHigh++;
//            		break;
//            	}
            }
            x=" --> Actual reconfiguration: " + steps.get(step).getSecond();
            System.out.println(x);
            //writerErr.println(x);
            for(Element elig : elt2) {
            	actualMap.put(elig.name, actualMap.get(elig.name) + 1);
            	if(elig.name==PolicyName.RELAY) eligHigh ++;

//            	switch(elig.priority) {
//            	case LOW :
//            		actuLow++;
//            		break;
//            	case MEDIUM:
//            		actuMed++;
//            		break;
//            	case HIGH: 
//            		actuHigh++;
//            		break;
//            	}
            }
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
            // when the eligible reconfiguration is empty and not actual
            if(steps.get(step).getSecond().size()>0 && steps.get(step).getFirst().size() ==0) {
            	writerErr.println("*** Step " + step + " encountered a problem : reconfiguration occured but was nout founded eligible");
                writerErr.println("Eligible reconfigurations: " + steps.get(step).getFirst());
                writerErr.println(" --> Actual reconfiguration: " + steps.get(step).getSecond());
            	  property=false;
            }
            //when actual is empty and not eligible
            else if(steps.get(step).getSecond().size()==0 && steps.get(step).getFirst().size() >0) {
            	writerErr.println("*** Step " + step + " encountered a problem : reconfiguration founded eligible but did not occured");
                writerErr.println("Eligible reconfigurations: " + steps.get(step).getFirst());
                writerErr.println(" --> Actual reconfiguration: " + steps.get(step).getSecond());
            	property=false;            
            }
            //when actual is not null but not contained in eligible
            else if(!containsReconf(steps.get(step).getSecond().get(0), steps.get(step).getFirst())) {
            	writerErr.println("*** Step " + step + " encountered a problem : actual reconfiguration is not in eligible list");
                writerErr.println("Eligible reconfigurations: " + steps.get(step).getFirst());
                writerErr.println(" --> Actual reconfiguration: " + steps.get(step).getSecond());
            	property=false;          
            }
            
            
//            }
            lastStep++;
        }
    	System.out.println("Last step with eligible reconfiguration is step " + --lastStep);
    	x = "Eligible versus actual reconfigurations :";
    	System.out.println(x);
    	writerErr.println(x);
    	Iterator it = eligibleMap.entrySet().iterator();
    	Iterator it2 = actualMap.entrySet().iterator();

    	    while (it.hasNext()) { 
    	        Map.Entry pair = (Map.Entry)it.next();
    	        System.out.print(pair.getKey() + " = " + pair.getValue());
    	        it.remove(); // avoids a ConcurrentModificationException
    	        
    	        Map.Entry pair2 = (Map.Entry)it2.next();
    	        System.out.println(" / " + pair2.getValue());
    	        it2.remove(); // avoids a ConcurrentModificationException
    	    }
//    	    x= "Low eligible vs actual : " + eligLow + "/" + actuLow;
//    	    System.out.println(x);
//    	    writerErr.println(x);
//    	    x= "Medium eligible vs actual : " + eligMed + "/" + actuMed;
//    	    System.out.println(x);
//    	    writerErr.println(x);
    	    x= "High eligible vs actual : " + eligHigh + "/" + actuHigh;
    	    System.out.println(x);
    	    writerErr.println(x);
    	    
    	    if(property) {
    	    	x ="Verdict : test passed successfully";
    	    	System.out.println(x);
    	    	writerErr.println(x);
    	    }
    	    else {
    	    	x ="Verdict : test failed";
    	    	System.out.println(x);
    	    	writerErr.println(x);
    	    }
    	    
//    	while(i<eligibleMap.size() ) {
//    		for(Element elt : eligibleMap)
//    		x= "actual/eligible : " + eligibleMap.+" / "+ eligibleActualCounter.get(i).get(0) ;
//    		System.out.println(x);
//    		writerErr.println(x);
//    		i++;
//    	}
    	    
    }
    public boolean containsReconf(Element e1, ArrayList<Element> array) {
    	for(int i =0; i< array.size(); i++) {
    		if(e1.name == array.get(i).name && e1.priority== array.get(i).priority && e1.vehicle==array.get(i).vehicle ) return true;
    	}
    	return false;
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