package SUT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

import javax.print.StreamPrintServiceFactory;

import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.tools.internal.xjc.model.SymbolSpace;

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
    public ArrayList<Rule> getRules() {
    	return rules;
    }
    public double match(Road sut, ExecutionReport er) {
    	   for (Rule r : rules) {
    		   //System.out.println("r cov "+ r.coverage +" "+ r.reconf);
               for (Vehicle v : sut.allVehicles) {
                   if (r.matches(sut, v, er)) {
                	   r.coverage = r.coverage>0.5 ? r.coverage : 0.5;
                       er.notifyStepBefore(compteur, r, v);
                   }
               }
           }
        // check reconfig effective du SUT
        if (compteur > 0) {
            for (Vehicle v : sut.allVehicles) {
                if (v.myPlatoon != null && v.myPlatoon.leader == v && v.myPlatoon.lastReconf != null) {
                	for(Rule r : rules) {
                		if(v.myPlatoon.lastReconf.name == r.reconf) r.coverage=1.0;
                	}
                	er.notifyStepAfter(compteur, v.myPlatoon.lastReconf,lastTriggeredReconf);
                }
            }
        }
        double cpt=0.0;
		for(Rule r : rules) {
			cpt+=r.coverage;
		}
		cpt = (cpt/(double)(rules.size()))*100.0;
        compteur++;
        return cpt;
    }
}

/**
 * @author jpgros
 *
 */
class Rule {

    VanetProperty TP;
    int state=0;
    double coverage=0.0;
//    public boolean [][] transitionsConfig = new boolean[][]{
//  	  { false,false,false,false},
//  	  { false,false,false,false},
//  	  { false,false,false,false},
//	  { false,false,false,false},
//      };    
//	  public boolean [][] transitionsTP = new boolean[][]{
//		  { false, false},
//		  { false,false},
//		  };
    VanetProperty config;
    PolicyName reconf;
    double prio;
    
    public Rule(VanetProperty TP, VanetProperty config, PolicyName reconf, double prio) {
        this.TP = TP;
        this.config = config;
        this.reconf = reconf;
        this.prio = prio;
    }

    public PropertyAutomaton<?> getConfig() {
        return config;
    }

    public PolicyName getReconf() {
        return reconf;
    }

    public double getPrio() {
        return prio;
    }

    public PropertyAutomaton<?> getTP() {
        return TP;
    }


    public boolean matches(Road sut, Vehicle v, ExecutionReport er) {
        try {
            TP.setCurrentVehicle(v);
            TP.match(sut);
            TP.setPriority(prio);
            TP.setName(reconf);
            Element e = new Element(this.reconf, this.prio, v);
            er.notifyTP(e);
            config.setCurrentVehicle(v);
            config.match(sut);
            er.notifyConfig(this, v, TP);
            return true;
        }
        catch (PropertyFailedException pfe) {
            
        }
        return false;
    }
    
    public boolean matchTP(Road sut) {
        try {
        		TP.match(sut);
        		return true;
        		        }
        catch (PropertyFailedException pfe) {
            
        }
        return false;
    }

}
class RuleCoveredException extends Exception {
    public RuleCoveredException(String msg) {
        super(msg);
    }
}
class ExecutionReport {
	class FreqSteps{
		HashSet<FreqStep> freqSet = new HashSet<FreqStep>();
		
		/**
		 * return the corresponding element based on string parameter and suppress it from set
		 * @param str
		 * @return FreqStep hashmap
		 */
		FreqStep containsRule(String str) {
			for(FreqStep elt : freqSet) {
				
				if(elt.NamePrio.equals(str)) {
					freqSet.remove(elt);
					return elt;
				}
			}
			return null;
		}
		void printConcernedRules() {
			System.out.println("printing freqset ");
			for(FreqStep elt : freqSet) {
				System.out.println(elt.NamePrio);
			}
		}
	}
	class FreqStep{
		String NamePrio="";
		LinkedHashMap<Integer,Double> freqMap = new LinkedHashMap<Integer,Double>();
		//public int index;
		//public Double freq;
		public FreqStep(String name) {
			NamePrio=name;
			//index=i;
			//freq=d;
		}
	}
	ArrayList<ArrayList<Element>> eligSorted = new ArrayList<ArrayList<Element>>() ;
	HashMap<Element,Integer> actualCountedMap = new HashMap<Element,Integer>();
	HashMap<MiniElement,Integer> actualCountedMiniMap = new HashMap<MiniElement,Integer>();
	HashMap<Element,Integer> eligibleSortedCountedMap = new HashMap<Element,Integer>();
	HashMap<Element,Integer> eligibleNotSortedCountedMap = new HashMap<Element,Integer>();
	HashMap<MiniElement,Integer> eligibleNotSortedCountedMiniMap = new HashMap<MiniElement,Integer>();
    HashMap<Integer, Pair<ArrayList<Element>, ArrayList<Element>>> steps = new LinkedHashMap<Integer, Pair<ArrayList<Element>, ArrayList<Element>>>();
    HashMap<Element,Integer> occurrencesTP = new HashMap<Element, Integer>();
    HashSet<MiniElement> miniOccurrencesTP = new HashSet<MiniElement>();
    HashSet<MiniElement> possibleMiniOccurrencesTP = new HashSet<MiniElement>();
    HashSet<MiniElement> untriggeredMiniOccurrencesTP = new HashSet<MiniElement>();
    HashMap<PropertyAutomaton,Integer> occurrencesConfig = new HashMap<PropertyAutomaton, Integer>();
    HashMap<Element, Integer> eligNoRepeat = new LinkedHashMap<Element,Integer>();
    FreqSteps freqSteps = new FreqSteps();    
	int nb=0;
    LogPrinter writerErr =null;
    boolean property =true;
    public ExecutionReport(LogPrinter w) {
    	writerErr = w;
    	
    }
    
    public void notifyConfig(Rule rule, Vehicle v, PropertyAutomaton<?> tp) {
        if (occurrencesConfig.get(tp) == null) {
            occurrencesConfig.put(tp, 1);
        }
        else {
            occurrencesConfig.put(tp, occurrencesConfig.get(tp) + 1);
        }
    }

    public void notifyTP(Element e) { //Rule rule, Vehicle v, VanetProperty tp) {
    	Integer value;
		value = (occurrencesTP.get(e));
		MiniElement miniElt=null;
		miniElt=new MiniElement(e.name,e.getPriority());
        miniOccurrencesTP.add(miniElt);
		if (value == null) {
            occurrencesTP.put(e, 1);
        }
        else {
            occurrencesTP.put(e, value + 1);
        }
        nb++;
    }
    //eligible reconf
    public void notifyStepBefore(int i, Rule r, Vehicle v) {
        if (steps.get(i) == null) {
            steps.put(i, new Pair<ArrayList<Element>, ArrayList<Element>>(new ArrayList<Element>(), new ArrayList<Element>()));
        }
        steps.get(i).getFirst().add(new Element(r.reconf, r.prio, v));
    }
    //actual reconf
    public void notifyStepAfter(int i, Element lastReconf, Element lastTriggeredReconf) {
    	//if(lastReconf.toStringShort().contains("QUITFORSTATION")) System.out.println("quitstas in steps "+ lastReconf.toStringShort());
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
    	map.put(PolicyName.QUITENERGY, 0);
    	map.put(PolicyName.QUITFORSTATION, 0);
    	map.put(PolicyName.QUITDISTANCE, 0);
    	map.put(PolicyName.RELAY, 0);
    	map.put(PolicyName.UPGRADERELAY, 0);
    	//map.put(PolicyName.RUN, 0);
    	
    	map2.put(PolicyName.QUITENERGY, 0);
    	map2.put(PolicyName.QUITFORSTATION, 0);
    	map2.put(PolicyName.QUITDISTANCE, 0);
    	map2.put(PolicyName.RELAY, 0);
    	map2.put(PolicyName.UPGRADERELAY, 0);
    	//map2.put(PolicyName.RUN, 0);
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
    			//System.out.println(x);
    			//writerErr.println(x);
    			lastStep++;
    		}
    		x = "*** Step " + step;
            //System.out.println(x);
            //writerErr.println(x);
            ArrayList<Element> elt = steps.get(step).getFirst();
            ArrayList<Element> elt2 = steps.get(step).getSecond();
            x ="Eligible reconfigurations: " + elt;
            //System.out.println(x);
            //writerErr.println(x);
            for(Element elig : elt) {
            	eligibleMap.put(elig.name, eligibleMap.get(elig.name) + 1);
            	if(elig.name==PolicyName.RELAY) eligHigh ++;
            	
            }
            x=" --> Actual reconfiguration: " + steps.get(step).getSecond();
            //System.out.println(x);
            //writerErr.println(x);
            for(Element elig : elt2) {
            	actualMap.put(elig.name, actualMap.get(elig.name) + 1);
            	if(elig.name==PolicyName.RELAY) eligHigh ++;

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
    	//System.out.println("Last step with eligible reconfiguration is step " + --lastStep);
    	x = "Eligible versus actual reconfigurations :";
    	//System.out.println(x);
    	writerErr.println(x);
    	Iterator it = eligibleMap.entrySet().iterator();
    	Iterator it2 = actualMap.entrySet().iterator();
    	
    	    while (it.hasNext()) { 
    	        Map.Entry pair = (Map.Entry)it.next();
    	        //System.out.print(pair.getKey() + " = " + pair.getValue());
    	        it.remove(); // avoids a ConcurrentModificationException
    	        
    	        Map.Entry pair2 = (Map.Entry)it2.next();
    	        //System.out.println(" / " + pair2.getValue());
    	        it2.remove(); // avoids a ConcurrentModificationException
    	    }
//    	    x= "Low eligible vs actual : " + eligLow + "/" + actuLow;
//    	    System.out.println(x);
//    	    writerErr.println(x);
//    	    x= "Medium eligible vs actual : " + eligMed + "/" + actuMed;
//    	    System.out.println(x);
//    	    writerErr.println(x);
    	    x= "High eligible vs actual : " + eligHigh + "/" + actuHigh;
    	    //System.out.println(x);
    	    writerErr.println(x);
    	    
    	    if(property) {
    	    	x ="Verdict : test passed successfully";
    	    	//System.out.println(x);
    	    	writerErr.println(x);
    	    }
    	    else {
    	    	x ="Verdict : test failed";
    	    	//System.out.println(x);
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
//    public void fillEligNoRepeat(){
//    	for (Element step :steps.keySet()) {
//    		
//    	}
//    }
    
    public void sortEligibleSteps() {
       	ArrayList<Element> prevElig = new ArrayList<Element>();
		
       	ArrayList<Element> tmpList = new ArrayList<Element>();
       	for (Integer step : steps.keySet()) {
	        ArrayList<Element> elt = steps.get(step).getFirst();       
	        ArrayList<Element> actualElig = new ArrayList<Element>();
	        tmpList.clear();
	        
	        for(Element singleElt : prevElig) {
	        	if(! elt.contains(singleElt)) {
	        		tmpList.add(singleElt);
	        	}	        	
	        }
	        for(Element singleElt : tmpList) {
	        	prevElig.remove(singleElt);
	        }
	        // adding new eligible elts
	        for(Element singleElt : elt) {
	        	if(!elt.contains(singleElt)) {
	        		prevElig.add(singleElt);
	        		actualElig.add(singleElt);
	        	}
	        }
	        eligSorted.add(actualElig);	        
    	}      
    	
    }
    public void printReconfNB(int j) {
    	Integer value;
    	for(Pair<ArrayList<Element>,ArrayList<Element>> pair : steps.values()) {
			//filling actual map
    		
    		if(pair.second.size()>0) {
				MiniElement elt = new MiniElement(pair.second.get(0).name, pair.second.get(0).priority);
				value = actualCountedMiniMap.get(elt);
				if(value==null) {
					actualCountedMiniMap.put(elt, 1);
				}
				else {
					actualCountedMiniMap.put(elt, ++value);
				}
			}
    		//filling eligible map
				for(Element eligElt : pair.first) {
					MiniElement miniElt = new MiniElement(eligElt.getName(), eligElt.priority);
					value = eligibleNotSortedCountedMiniMap.get(miniElt);
					if(value==null) {
						eligibleNotSortedCountedMiniMap.put(miniElt, 1);
					}
					else {
						eligibleNotSortedCountedMiniMap.put(miniElt, ++value);
					}
				}
    	}
    	Integer val=0;
    	for(Map.Entry<MiniElement, Integer> elt: eligibleNotSortedCountedMiniMap.entrySet()) {
    		//System.out.println("elig not sorted " +elt.getKey());
    		val= actualCountedMiniMap.get(elt.getKey());
    		if(val!=null) {
    			System.out.println("val = " +val+ " value = "  + elt.getValue() + " elt= " +elt.toString());
    		}
    	}
    }
    public void populateGraph(int j) {
    	
    	Integer value;
    	for(Pair<ArrayList<Element>,ArrayList<Element>> pair : steps.values()) {
			//filling actual map    		
    		if(pair.second.size()>0) {
				MiniElement elt = new MiniElement(pair.second.get(0).name, pair.second.get(0).priority);
//				if(elt.toString().contains("QUITFORSTATION")) {
//					System.out.println("quitstas populate " + elt.toString());
//				}
				value = actualCountedMiniMap.get(elt);
				if(value==null) {
					actualCountedMiniMap.put(elt, 1);
				}
				else {
					actualCountedMiniMap.put(elt, ++value);
				}
			}
    		//filling eligible map
				for(Element eligElt : pair.first) {
					MiniElement miniElt = new MiniElement(eligElt.getName(), eligElt.priority);
//					if(eligElt.toString().contains("QUITFORSTATION")) {
//					System.out.println("quitstas populate " + eligElt.toString());
//					}
					value = eligibleNotSortedCountedMiniMap.get(miniElt);
//					if(miniElt.toString().contains("QUITFORSTATION")) {
//					System.out.println("quitstas populate " + miniElt.toString());
//				}
					if(value==null) {
						eligibleNotSortedCountedMiniMap.put(miniElt, 1);
					}
					else {
						eligibleNotSortedCountedMiniMap.put(miniElt, ++value);
					}
				}
    	}
    	
    	//creating frequencies array (name + prio) based on actual reconf versus elig (TP+guard) reconf (not sorted ones)
    	HashMap<MiniElement,Double> frequenciesActuEligMiniMap = new HashMap<MiniElement,Double>();
    	Integer val=0;
    	Double freq=0.0;
    	for(Map.Entry<MiniElement, Integer> elt: eligibleNotSortedCountedMiniMap.entrySet()) {
    		//System.out.println("elig not sorted " +elt.getKey());
    		val= actualCountedMiniMap.get(elt.getKey());
    		if(val!=null) {
    			freq = (((Double)(double)(val))/((Double)(double)(elt.getValue())))*100.0;
    			if(freq<0) System.out.println("freq neg " + "actual nb" + val + "elig nb "+elt.getValue() + " " +elt.getKey());

    			if(freq>100) System.out.println("freq too high " + "actual nb" + val + "elig nb "+elt.getValue() + " " +elt.getKey());
    			//System.out.println("freq "+ freq + " " + (Double)(double)(val) );
    			frequenciesActuEligMiniMap.put(elt.getKey(), freq);
    		}
    		else {
    			frequenciesActuEligMiniMap.put(elt.getKey(), 0.0);
    		}
    	}
    	eligibleNotSortedCountedMap.clear();
    	Double d=0.0;
    	for(Map.Entry<MiniElement,Double> map : frequenciesActuEligMiniMap.entrySet()){
    		String nameKey = map.getKey().toString();
    		FreqStep elt = freqSteps.containsRule(nameKey);
    		d=map.getValue();
    		if(elt !=null) {
    			elt.freqMap.put(j, d);
    			freqSteps.freqSet.add(elt);  
    		}
    		else {
    	    	FreqStep fs = new FreqStep(nameKey);
    			fs.freqMap.put(j, map.getValue());
    			freqSteps.freqSet.add(fs);
    			
    			for(FreqStep eltSteps : freqSteps.freqSet) {
    				System.out.println("eltSteps "+ eltSteps.NamePrio);
    				for(Map.Entry <Integer,Double> eltLoop :eltSteps.freqMap.entrySet()) {
    					System.out.println("elt loop" +eltLoop);
    				}
    			}System.out.println("end");
    		}    			
		}
    	eligibleNotSortedCountedMiniMap.clear();
    	actualCountedMiniMap.clear();
    	
	}
    public void cleanGraph() {
    	steps.clear();
    	freqSteps.freqSet.clear();
    }
    public void printGraph(int ind) throws FileNotFoundException, UnsupportedEncodingException {
        int i=0;
        for(ExecutionReport.FreqStep elt:freqSteps.freqSet) {
        	File theDir = new File("test" +ind);
        	// if the directory does not exist, create it
        	if (!theDir.exists()) {
        		System.out.println("creating file");
        	    try{
        	        theDir.mkdir();
        	    } 
        	    catch(SecurityException se){
        	        System.out.println("problem creating directory");
        	    }        
        	}
        	i++;
        	PrintWriter writerGraphFile = new PrintWriter("./test" +ind+"/statsGraph"+i+".tex", "UTF-8");
            LogPrinter writeGraph = new LogPrinter(writerGraphFile, LogLevel.ERROR, LogLevel.ERROR);
            freqSteps.printConcernedRules();
            String splits[] = elt.NamePrio.split(" ");
            writeGraph.println("!"+splits[1] + ";" + elt.NamePrio);
            for(int key: elt.freqMap.keySet()) {
                	if(key ==0 || key%100==0) writeGraph.println("("+ key + ","+ elt.freqMap.get(key)+")");
            }
            elt.freqMap.clear();
            writerGraphFile.close();
            writeGraph.close();
        }
        freqSteps.freqSet.clear();  
    }
    	
    public void statsFreq() throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writerStatsFile = new PrintWriter("./statsShort.txt", "UTF-8");
        LogPrinter writeStats = new LogPrinter(writerStatsFile, LogLevel.ERROR, LogLevel.ERROR);
    	Integer value;
    	String x="";
    	for(Pair<ArrayList<Element>,ArrayList<Element>> pair : steps.values()) {
			//filling actual map
    		
    		if(pair.second.size()>0) {
				MiniElement elt = new MiniElement(pair.second.get(0).name, pair.second.get(0).priority);
				value = actualCountedMiniMap.get(elt);
				if(value==null) {
					actualCountedMiniMap.put(elt, 1);
				}
				else {
					actualCountedMiniMap.put(elt, ++value);
				}
			}
    		//filling eligible map
				for(Element eligElt : pair.first) {
					//System.out.println("elig elt "+ eligElt);
					MiniElement miniElt = new MiniElement(eligElt.getName(), eligElt.priority);
					value = eligibleNotSortedCountedMiniMap.get(miniElt);
					if(value==null) {
						eligibleNotSortedCountedMiniMap.put(miniElt, 1);
					}
					else {
						eligibleNotSortedCountedMiniMap.put(miniElt, ++value);
					}
				}
    	}
    	
    	//creating frequencies array (name + prio) based on actual reconf versus elig (TP+guard) reconf (not sorted ones)
    	HashMap<MiniElement,Double> frequenciesActuEligMiniMap = new HashMap<MiniElement,Double>();
    	Integer val=0;
    	Double freq=0.0;
    	for(Map.Entry<MiniElement, Integer> elt: eligibleNotSortedCountedMiniMap.entrySet()) {
    		//System.out.println("elig not sorted " +elt.getKey());
    		val= actualCountedMiniMap.get(elt.getKey()); 
    		if(val!=null) {
    			freq = (((Double)(double)(val))/((Double)(double)(elt.getValue())))*100.0;
    			//System.out.println("freq "+ freq + " " + (Double)(double)(val) );
    			frequenciesActuEligMiniMap.put(elt.getKey(), freq);
    		}
    		else {
    			frequenciesActuEligMiniMap.put(elt.getKey(), 0.0);
    		}
    	}
    	
    	writeStats.println("-----------------Frequencies actual vs elig ---------------------------------------");
    	for(Map.Entry<MiniElement,Double> map : frequenciesActuEligMiniMap.entrySet()){
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	writeStats.println("--------------------------------------------------------");
    	for(Map.Entry<MiniElement, Integer> elt: eligibleNotSortedCountedMiniMap.entrySet()) {
    		writerStatsFile.println("elt " + elt);
    	}
    	writeStats.println("--------------------------------------------------------");
    	for(Map.Entry<MiniElement, Integer> elt: actualCountedMiniMap.entrySet()) {
    		writerStatsFile.println("eltact " + elt);
    	}
    	
    	writeStats.close();
    }
    public void stats() throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writerStatsFile = new PrintWriter("./stats.txt", "UTF-8");
        LogPrinter writeStats = new LogPrinter(writerStatsFile, LogLevel.ERROR, LogLevel.ERROR);
    	Integer value;
    	String x="";
    	for(Pair<ArrayList<Element>,ArrayList<Element>> pair : steps.values()) {
			//filling actual map
    		
    		if(pair.second.size()>0) {
				Element elt = pair.second.get(0);
				value = actualCountedMap.get(elt);
				if(value==null) {
					actualCountedMap.put(elt, 1);
				}
				else {
					actualCountedMap.put(elt, ++value);
				}
			}
    		//filling eligible map
				for(Element eligElt : pair.first) {
					//System.out.println("elig elt "+ eligElt);
					value = eligibleNotSortedCountedMap.get(eligElt);
					if(value==null) {
						eligibleNotSortedCountedMap.put(eligElt, 1);
					}
					else {
						eligibleNotSortedCountedMap.put(eligElt, ++value);
					}
				}
    	}

    	
    	for(ArrayList<Element> arrayElt : eligSorted) {
    		for(Element elt : arrayElt) {
    			value =(Integer)(eligibleSortedCountedMap.get(elt));
    			if(value ==null) {
    				eligibleSortedCountedMap.put(elt, 1);
    			}
    			else {
    				eligibleSortedCountedMap.put(elt, ++value);
    			}
    		}
    	}
    	
    	
    	//creating frequencies array based on actual reconf versus TP conditions
    	HashMap<Element,Double> frequenciesOccTPMap = new HashMap<Element,Double>();
    	Integer val=0;
    	Double freq=0.0;
    	for(Map.Entry<Element, Integer> elt: occurrencesTP.entrySet()) {
    		val=actualCountedMap.get(elt.getKey()); 
    		if(val!=null) {
    			freq = (((Double)(double)(val))/((Double)(double)(elt.getValue())))*100.0;
    			//System.out.println("freq "+ freq + " " + (Double)(double)(val) );
    			frequenciesOccTPMap.put(elt.getKey(), freq);
    		}
    		else {
    			frequenciesOccTPMap.put(elt.getKey(), 0.0);
    		}
    	}
    	
    	//creating frequencies array based on actual reconf versus elig (TP+guard) reconf (not sorted ones)
    	HashMap<Element,Double> frequenciesActuEligMap = new HashMap<Element,Double>();
    	val=0;
    	freq=0.0;
    	for(Map.Entry<Element, Integer> elt: eligibleNotSortedCountedMap.entrySet()) {
    		//System.out.println("elig not sorted " +elt.getKey());
    		val= actualCountedMap.get(elt.getKey()); 
    		if(val!=null) {
    			freq = (((Double)(double)(val))/((Double)(double)(elt.getValue())))*100.0;
    			//System.out.println("freq "+ freq + " " + (Double)(double)(val) );
    			frequenciesActuEligMap.put(elt.getKey(), freq);
    		}
    		else {
    			frequenciesActuEligMap.put(elt.getKey(), 0.0);
    		}
    	}
    	
    	//creating frequencies array (name + prio) based on actual reconf versus elig (TP+guard) reconf (not sorted ones)
    	HashMap<Element,Double> frequenciesActuEligMapShort = new HashMap<Element,Double>();
    	val=0;
    	freq=0.0;
    	for(Map.Entry<Element, Integer> elt: eligibleNotSortedCountedMap.entrySet()) {
    		//System.out.println("elig not sorted " +elt.getKey());
    		val= actualCountedMap.get(elt.getKey()); 
    		if(val!=null) {
    			freq = (((Double)(double)(val))/((Double)(double)(elt.getValue())))*100.0;
    			//System.out.println("freq "+ freq + " " + (Double)(double)(val) );
    			frequenciesActuEligMapShort.put(elt.getKey(), freq);
    		}
    		else {
    			frequenciesActuEligMapShort.put(elt.getKey(), 0.0);
    		}
    	}
    	
    	writeStats.println("-----------------Frequencies actual vs elig ---------------------------------------");
    	for(Map.Entry<Element,Double> map : frequenciesActuEligMap.entrySet()){
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	writeStats.println("--------------------------------------------------------");
    	    	
    	//System.out.println("Counted actual map :");
    	for(Map.Entry<Element,Integer> map : actualCountedMap.entrySet()) {
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	//System.out.println("Counted eligible map :");
    	for(Map.Entry<Element,Integer> map : eligibleSortedCountedMap.entrySet()) {
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	
    	//System.out.println("Counted unsorted eligible map :");
    	for(Map.Entry<Element,Integer> map : eligibleNotSortedCountedMap.entrySet()) {
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	
    	//System.out.println("occurrencesTP :");
    	for(Map.Entry<Element,Integer> rule :occurrencesTP.entrySet()) {
    		x= "name " + rule.getKey().getName() + " vehicle "+ rule.getKey().getVehicle()+ " priority "+ rule.getKey().getPriority()+" value " + rule.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}
    	//System.out.println("size : " + nb);
    	
    	for(Map.Entry<Element,Double> map : frequenciesOccTPMap.entrySet()){
    		x= " key " + map.getKey() + " value "+ map.getValue();
    		//System.out.println(x);
    		writeStats.println(x);
    	}    	
    	//detecting which TP was not triggered
//    	for (PolicyName pol : PolicyName.values()) {
//    		  for(Priority prio : Priority.values()) {// replace enum with range
//    			  MiniElement miniElt = new MiniElement(pol,prio);
//    			  possibleMiniOccurrencesTP.add(miniElt);
//    		  }
//    		}
    	for(MiniElement miniElt : possibleMiniOccurrencesTP) {
    		if(!miniOccurrencesTP.contains(miniElt)) {
    			untriggeredMiniOccurrencesTP.add(miniElt);
    		}
    	}
    	writeStats.println("untrigerred TP");
    	for(MiniElement miniElt : untriggeredMiniOccurrencesTP) {
    		writeStats.println(miniElt.name + " " + miniElt.priority);
    	}
    	writeStats.close();
    }
    public boolean containsReconf(Element e1, ArrayList<Element> array) {
    	for(Element elt : array) {
    		if(e1.name == elt.name && e1.priority== elt.priority && e1.vehicle==elt.vehicle ) return true;
    	}
    	return false;
    }
}

class Pair<K, V> implements Serializable{
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