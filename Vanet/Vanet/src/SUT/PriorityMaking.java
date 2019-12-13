package SUT;

import java.util.ArrayList;

public class PriorityMaking {
	private ArrayList<Integer> listIndexes=new ArrayList<Integer>();
	private ArrayList<ArrayList<Double>> listPrioValues= new ArrayList<ArrayList<Double>>();
	public double[][] tabPrio;
	public PriorityMaking() {
		
	}
	public PriorityMaking(ArrayList<Integer>  lI, ArrayList<ArrayList<Double>> lP) {
		listIndexes=lI;
		listPrioValues=lP;
	}
	public void arrayFilling() {
		int nbRules=8;
		for(int i=0; i<nbRules; i++) {
			listIndexes.add(0);
		}
		 //QUITDISTANCE, HIGHPRIO 0; QUITDISTANCE, LOWPRIO 1; QUITENERGY, HIGHPRIO 2; RELAY, HIGHPRIO 3; 
        //QUITFORSTATION, HIGHPRIO 4; QUITFORSTATION, MEDIUMPRIO 5; QUITFORSTATION, LOWPRIO 6; UPGRADERELAY, MEDIUMPRIO 7
		ArrayList<Double> tmp = new ArrayList<Double>();
		tmp.add(7.0);
		//=(ArrayList<Double>) Arrays.asList(7.0, 5.0, 3.0);
		listPrioValues.add(tmp);
		ArrayList<Double> tmp2 = new ArrayList<Double>();
		tmp2.add(5.0);
		tmp2.add(3.0);
		listPrioValues.add(tmp2);
		
		ArrayList<Double> tmp3 = new ArrayList<Double>();
		tmp3.add(7.0);
		listPrioValues.add(tmp3);
		
		ArrayList<Double> tmp4 = new ArrayList<Double>();
		tmp4.add(7.0);
		tmp4.add(5.0);
		tmp4.add(3.0);
		listPrioValues.add(tmp4);
		
		ArrayList<Double> tmp5 = new ArrayList<Double>();
		tmp5.add(7.0);
		listPrioValues.add(tmp5);
		
		ArrayList<Double> tmp6 = new ArrayList<Double>();
		tmp6.add(6.0);
		tmp6.add(5.0);
		listPrioValues.add(tmp6);
		
		ArrayList<Double> tmp7 = new ArrayList<Double>();
		tmp7.add(3.0);
		listPrioValues.add(tmp7);
		
		ArrayList<Double> tmp8 = new ArrayList<Double>();
		tmp8.add(5.4);
		tmp8.add(5.0);
		listPrioValues.add(tmp8);
		
		int dimension =1;
		for(ArrayList<Double> list :listPrioValues) {
			dimension*=list.size();
		}
		tabPrio = new double[dimension][nbRules];
		boolean bool = true;
		int lineNb=0;
		while(bool) {
			for(int i =0; i<nbRules; i++) {
				double toto=listPrioValues.get(i).get(listIndexes.get(i));
				tabPrio[lineNb][i]=toto;
			}
			bool = majIndexes(0);
			lineNb++;
			
		}
		
	}
	public  boolean majIndexes(int i) {

		if(listIndexes.get(i)>=listPrioValues.get(i).size()-1) {
			listIndexes.remove(i);
			listIndexes.add(i, 0);
			if(i<listIndexes.size()-1) {
				i++;
				return true && majIndexes(i);
			}
			else {
				return false;
			}
		}
		else {
			Integer val=listIndexes.remove(i);
			listIndexes.add(i, ++val);
			return true;
		}
	}
	
}
