import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Controller {
	
	
	//FileWriter outputLog = new FileWriter(baseDir+"/logs/logs.txt");
	
//	ArrayList components = new ArrayList();
//	components.add("Wifi");
//	components.add("GPS");
	int battery = 100;
	ThresholdPolicy threshold = ThresholdPolicy.NORMAL;
	//Writer writer;
	double[] policyArray=new double[3];
	//SignalMode signal = SignalMode.GPS;
	BatteryLevel batteryLevel = BatteryLevel.HIGH;
	Zone zone = Zone.WIFIGPS;
	CycabComponents cycabComponents = new CycabComponents();

	public Controller(String basedir) {
			
	}
	public Controller( int battery, SignalMode signal, BatteryLevel batteryLevel,
			Zone zone, ThresholdPolicy policy) {
		super();
		this.battery = battery;
		this.cycabComponents=cycabComponents;
		//this.signal = signal;
		this.batteryLevel = batteryLevel;
		this.zone = zone;
		this.threshold = policy;
		this.initThresholdPolicy(this.threshold, this.policyArray);
	}
	
	public void testList() {
		cycabComponents.getComponentList().add("Wifi");
		if(cycabComponents.getComponentList().equals("[Wifi]")) {
		System.out.println("the list now : "+cycabComponents.getComponentList());
		}
		cycabComponents.getComponentList().add("GPS");
		if(cycabComponents.getComponentList().equals("Wifi")) {
			System.out.println("error");
		}
		System.out.println("the list now : "+cycabComponents.getComponentList());
		cycabComponents.getComponentList().remove("Wifi");
		System.out.println("the list now : "+cycabComponents.getComponentList());
	}
	
    /**
     * Initialises the ThresholdPolicy according to the given parameters.
     * @param threshold The threshold policy choosen can be HARD, NORMAL or LIGHT
     * @param policyArray the array containing the probability to trigger an event or can be seen as a priority level
     * case 0 of the array indicates a low priority while case 2 indicates a high priority 
     */
	public void initThresholdPolicy(ThresholdPolicy threshold, double[] policyArray) {
		switch(threshold) {
		case LIGHT :
			policyArray[0] = 2.0*Math.random()/3.0; //1 * 2/3 = 0.6 
			policyArray[1] = 2.0*Math.random()/3.0 +0.4; // between 0.4 and 1.0
			policyArray[2] = Math.random()/5.0 +0.8; //between 0.8 and 1.0
			break;
		case NORMAL :
			policyArray[0] = 2.0*Math.random()/5.0; //1 * 2/5 = 0.4 
			System.out.println("policy ="+ policyArray[0]);
			policyArray[1] = 2.0*Math.random()/3.0 +0.2; // between 0.2 and 0.8
			policyArray[2] = 2.0*Math.random()/5.0 +0.6; //between 0.6 and 1.0
			break;
		case HARD:
			policyArray[0] = 1.0*Math.random()/5.0; //1 * 1/5 = 0.2 
			policyArray[1] = 2.0*Math.random()/3.0 ; // between 0.0 and 0.6
			policyArray[2] = 2.0*Math.random()/3.0 +0.4; //between 0.4 and 1.0
			break;
		}
	}
	public void initFile(String baseDir) /*throws IOException*/ {
	      	//File file = new File(baseDir+"/logs/logs.txt");
	      	//boolean b = file.createNewFile();
			// this.writer = new FileWriter(baseDir+"/logs/logs.txt");
	}
	
    /**
     * Update the level of battery according to the components in use
     * @param signal components in use
     * @param battery level of battery
     * @return the new level of battery
     */
	public  int updateBattery(int battery) /* throws IOException */ {
//		switch(batteryMode) {
//		case USING:
		int batteryChange =3;
		ArrayList<String> componentList = cycabComponents.getComponentList();
		
		if(componentList.contains("Wifi")) batteryChange-=2;
		if(componentList.contains("GPS")) batteryChange-=3;
		if(componentList.contains("Radio")) batteryChange-=1;
		if(componentList.contains("Communication")) batteryChange-=3;
		setBattery(battery+batteryChange);
		System.out.println("battery: "+ battery);
		return battery;
	}
	
    /**
     * Updates the battery level (HIGH, MEDIUM, LOW, VERY LOW) according to the battery value
     * @param batteryLevel is the battery level before the update
     * @param battery is the battery value used to decide in which level battery should be
     * @return the updated batteryLevel (can be unchanged) 
     */
	public BatteryLevel updateBatteryLevel(BatteryLevel batteryLevel, int battery) /*throws IOException*/ {
		//somewhere between low and verylow changes too often
		if(battery < 20 && batteryLevel != BatteryLevel.VERYLOW) {
			setBatteryLevel(BatteryLevel.VERYLOW);
			//writer.write("Battery mode in charge"); writer.write("\n");
		}
		else if (battery <30 && battery >20) {
			if(Math.random()>(1.2-(30.0-battery)/10.0) && batteryLevel== BatteryLevel.VERYLOW) {
				setBatteryLevel(BatteryLevel.LOW);
				System.out.println("Battery level : LOW");
			}
			else if (Math.random()<(1.2-(30.0-battery)/10.0) && batteryLevel== BatteryLevel.LOW) {
				setBatteryLevel(BatteryLevel.VERYLOW);
				System.out.println("Battery level : VERYLOW");				
			}
		}
		else if(battery <45 && battery>30 && batteryLevel != BatteryLevel.LOW) {
			setBatteryLevel(BatteryLevel.LOW);
			System.out.println("Battery level : LOW");
			//writer.write("Battery mode in charge"); writer.write("\n");
		}
		else if (battery <55 && battery >45) {
			if(Math.random()>(1.2-(55.0-battery)/10.0) && batteryLevel== BatteryLevel.LOW) {
				setBatteryLevel(BatteryLevel.MEDIUM);
				System.out.println("Battery level : MEDIUM");
			}
			else if (Math.random()<(1.2-(55.0-battery)/10.0) && batteryLevel== BatteryLevel.MEDIUM) {
				setBatteryLevel(BatteryLevel.LOW);
				System.out.println("Battery level : LOW");	
			}
		}
		else if(battery > 55 && battery < 70 && batteryLevel != BatteryLevel.MEDIUM) {
			setBatteryLevel(BatteryLevel.MEDIUM);
			System.out.println("Battery level : MEDIUM");
			//writer.write("Battery mode in use"); writer.write("\n");
		}
		else if (battery <80 && battery >70) {
			if(Math.random()>(1.2-(70.0-battery)/10.0) && batteryLevel== BatteryLevel.MEDIUM) {
				setBatteryLevel(BatteryLevel.HIGH);
				System.out.println("Battery level : HIGH");
			}
			else if (Math.random()<(1.2-(70.0-battery)/10.0) && batteryLevel== BatteryLevel.HIGH) {
				setBatteryLevel(BatteryLevel.MEDIUM);
				System.out.println("Battery level : MEDIUM");			
			}
		}
		else if(battery > 80 && batteryLevel != BatteryLevel.HIGH) {
			setBatteryLevel(BatteryLevel.HIGH);
			System.out.println("Battery level : HIGH");
			//writer.write("Battery mode in use"); writer.write("\n");
		}
		return batteryLevel;
	}
	
	public Zone updateZone(Zone zone, double flipCoin, double changeZone) /*throws IOException*/ { 
		if (Math.random() +changeZone > 1.0) {
			if(zone == Zone.WIFIGPS) {
				if(Math.random()>flipCoin) {
					setZone(Zone.noWIFIGPS);
					System.out.println("Receiving GPS but no WIFI");
					//writer.write("Entering tunnel"); writer.write("\n");
				}
				else {
					setZone(Zone.WIFInoGPS);
					System.out.println("Receiving Wifi but no GPS");
					//writer.write("Entering Wifi area"); writer.write("\n");
				}
			}
			else if (zone == Zone.noWIFIGPS){
				if(Math.random()>flipCoin) {
					setZone(Zone.WIFIGPS);
					System.out.println("Receiving WIFI and GPS");
					//writer.write("Entering tunnel"); writer.write("\n");
				}
				else {
					setZone(Zone.noWIFInoGPS);
					System.out.println("Receiving neither WIFI nor GPS");
					//writer.write("Entering Wifi area"); writer.write("\n");
				}
			}
			else if(zone == Zone.WIFInoGPS) {
				if(Math.random()>flipCoin) {
					setZone(Zone.noWIFInoGPS);
					System.out.println("Receiving neither WIFI nor GPS");
					//writer.write("Entering tunnel"); writer.write("\n");
				}
				else {
					setZone(Zone.WIFIGPS);
					System.out.println("Receiving both WIFI and GPS");
					//writer.write("Entering Wifi area"); writer.write("\n");
				}
			}
			else if(zone == Zone.noWIFInoGPS) {
				if(Math.random()>flipCoin) {
					setZone(Zone.WIFInoGPS);
					System.out.println("Receiving GPS but no WIFI");
					//writer.write("Entering tunnel"); writer.write("\n");
				}
				else {
					setZone(Zone.WIFInoGPS);
					System.out.println("Receiving WIFI but no GPS");
					//writer.write("Entering Wifi area"); writer.write("\n");
				}
			}
		}
		return zone;
	}
	public void updateSignal(BatteryLevel batteryLevel, Zone zone) /*throws IOException*/ {
		ArrayList<String> componentList = cycabComponents.getComponentList();
		ArrayList<String> tempList = new ArrayList<>();
		switch(zone) {
			case noWIFIGPS:
				tempList.add("GPS");
				if(batteryLevel == BatteryLevel.HIGH && (!componentList.contains("GPS"))) {
					addGPS(policyArray[2]);
					if( !componentList.contains("Radio")) addRadio(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (componentList.contains("Wifi"))){
					removeWifi(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.LOW && componentList.contains("Wifi")) {
					removeWifi(policyArray[2]);
					if(componentList.contains("Radio")) removeRadio(policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW &&  !componentList.equals(tempList)) { //tempList == "GPS"
					setSignal("GPS"); //CAREFUL IT CUTS COMMUNICATION
					System.out.println("Signal mode activated : GPS");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case WIFInoGPS:
				tempList.add("Wifi");
				if(batteryLevel == BatteryLevel.HIGH && (!componentList.contains("Wifi"))) {
					addWifi(policyArray[2]);
					if( !componentList.contains("Radio")) addRadio(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (componentList.contains("GPS"))){
					removeGPS(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.LOW && componentList.contains("GPS")) {
					removeGPS(policyArray[2]);
					if(componentList.contains("Radio")) removeRadio(policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && !componentList.equals(tempList) ) { //tempList == "Wifi"
					setSignal("Wifi"); //CAREFUL IT CUTS COMMUNICATION
					System.out.println("Signal mode activated : WIFI");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case WIFIGPS:
				tempList.add("Wifi");
				if(batteryLevel == BatteryLevel.HIGH && (!componentList.contains("GPS"))) {
					addGPS(policyArray[2]);
					if( !componentList.contains("Radio")) addRadio(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				if(batteryLevel == BatteryLevel.HIGH && (!componentList.contains("Wifi"))) {
					addWifi(policyArray[2]);
					if( !componentList.contains("Radio")) addRadio(policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (!componentList.contains("GPS"))) {
					addGPS(policyArray[1]);
				}
				if(batteryLevel == BatteryLevel.MEDIUM && (!componentList.contains("Wifi"))) {
					addWifi(policyArray[1]);
				}
				else if(batteryLevel == BatteryLevel.LOW && componentList.contains("GPS") ) { 
					removeGPS(policyArray[1]); //GPS consumes more energy than wifi
					if(componentList.contains("Radio")) removeRadio(policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && !componentList.equals(tempList)) { //tempList == "Wifi"
					setSignal("Wifi"); //CAREFUL IT CUTS COMMUNICATION
					System.out.println("Signal mode activated : WIFI");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case noWIFInoGPS:
				tempList.add("");
				if(batteryLevel == BatteryLevel.HIGH || batteryLevel == BatteryLevel.MEDIUM && (!componentList.contains("Radio"))) {
					addRadio(policyArray[2]);
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (componentList.contains("Wifi"))){
					removeWifi(policyArray[1]);
					if(componentList.contains("Radio")) removeRadio(policyArray[0]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				if(batteryLevel == BatteryLevel.LOW && componentList.contains("GPS")) { 
					removeGPS(policyArray[2]);
					if(componentList.contains("Radio")) removeRadio(policyArray[1]);
				}
				if(batteryLevel == BatteryLevel.LOW && componentList.contains("Wifi")) { 
					removeWifi(policyArray[2]);
					if(componentList.contains("Radio")) removeRadio(policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && componentList.equals(tempList) ) { //tempList == ""
					setSignal(""); //CAREFUL IT CUTS COMMUNICATION
					System.out.println("Signal mode activated : None");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				tempList.clear();
		}
	}
	
	public void removeWifi(double proba) {
		double rand = Math.random();
		if(rand < proba) {
			cycabComponents.getComponentList().remove("Wifi");
			System.out.println(cycabComponents.getComponentList());
		}
	}
	
	public void removeGPS(double proba) {
		double rand = Math.random();
		if(rand < proba) {
			cycabComponents.getComponentList().remove("GPS");
			System.out.println(cycabComponents.getComponentList());
		}
	}
	
	public void removeRadio(double proba) {
		double rand = Math.random();
		if(rand < proba) {
			cycabComponents.getComponentList().remove("Radio");
			System.out.println(cycabComponents.getComponentList());
		}
	}
	
	public void removeCommunication() {
		cycabComponents.getComponentList().remove("Communication");
		System.out.println(cycabComponents.getComponentList());
	}
	public void addWifi(double proba) {
		double rand = Math.random();
		if(rand < proba) {
			if(!cycabComponents.getComponentList().contains("Wifi")) {
				cycabComponents.getComponentList().add("Wifi");
				System.out.println(cycabComponents.getComponentList());
			}
		}
	}
	
	public void addGPS(double proba) {

		double rand = Math.random();
		if(rand < proba) {
			if(!cycabComponents.getComponentList().contains("GPS")) {
				cycabComponents.getComponentList().add("GPS");
				System.out.println(cycabComponents.getComponentList());
			}
		}
	}
	
	public void addCommunication() {
		if(!cycabComponents.getComponentList().contains("Communication")) {
			cycabComponents.getComponentList().add("Communication");
			System.out.println(cycabComponents.getComponentList());
		}
	}
	
	public void addRadio(double proba) {
		double rand = Math.random();
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add("Radio");
		if(rand < proba) {
			if(!cycabComponents.getComponentList().contains("Radio")) {
				cycabComponents.getComponentList().add("Radio");
				System.out.println(cycabComponents.getComponentList());
			}
		}
	}
		
	public void closeFile() throws IOException {
		// writer.close();
	}
	public int getBattery() {
		return battery;
	}
	public void setBattery(int battery) {
		this.battery = battery;
	}
//	public SignalMode getSignal() {
//		return SignalMode.ALL;
//	}
	public void setSignal(String component) {
		cycabComponents.getComponentList().clear();
		cycabComponents.getComponentList().add(component);
	}
	public BatteryLevel getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(BatteryLevel batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	public CycabComponents getCycabComponents() {
		return cycabComponents;
	}
	public void setCycabComponents(CycabComponents cycabComponents) {
		this.cycabComponents = cycabComponents;
	}

}
