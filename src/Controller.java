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
	SignalMode signal = SignalMode.GPS;
	BatteryLevel batteryLevel = BatteryLevel.HIGH;
	Zone zone = Zone.WIFIGPS;
	public Controller(String basedir) {
			
	}
	public Controller( int battery, SignalMode signal, BatteryLevel batteryLevel,
			Zone zone, ThresholdPolicy policy) {
		super();
		this.battery = battery;
		this.signal = signal;
		this.batteryLevel = batteryLevel;
		this.zone = zone;
		this.threshold = policy;
		this.initThresholdPolicy(this.threshold, this.policyArray);
	}
	
	public void initThresholdPolicy(ThresholdPolicy threshold, double[] policyArray) {
		switch(threshold) {
		case LIGHT :
			policyArray[0] = 2.0*Math.random()/3.0; //1 * 2/3 = 0.6 
			policyArray[1] = 2.0*Math.random()/3.0 +0.4; // between 0.4 and 1.0
			policyArray[2] = Math.random()/5.0 +0.8; //between 0.8 and 1.0
			break;
		case NORMAL :
			policyArray[0] = 2.0*Math.random()/5.0; //1 * 2/5 = 0.4 
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
	public  int updateBattery(BatteryLevel batteryMode,SignalMode signal, int battery) /* throws IOException */ {
//		switch(batteryMode) {
//		case USING:
		switch(signal) {
			case ALL: setBattery(battery - 3); // total should be -2 but -3 is better for the tests
			System.out.println("Level of battery :"+battery);
			//writer.write("Level of battery :"+battery); writer.write("\n");
			//shouldn't be <0
			break;
			case WIFI: setBattery(battery + 1);
			System.out.println("Level of battery :"+battery);
			//writer.write("Level of battery :"+battery); writer.write("\n");
			//shouldn't be <0
			break;
			case GPS: setBattery(battery + 0);
			System.out.println("Level of battery :"+battery);
			//writer.write("Level of battery :"+battery); writer.write("\n");
			//shouldn't be <0
			break;
			case RADIO: setBattery(battery + 2);
			System.out.println("Level of battery :"+battery);
			break;
			case WIFInGPS: setBattery(battery - 2);
			System.out.println("Level of battery :"+battery);
			break;
			case WIFInRADIO: setBattery(battery - 1);
			System.out.println("Level of battery :"+battery);
			break;
			case GPSnRADIO: setBattery(battery - 1);
			System.out.println("Level of battery :"+battery);
			break;
			case NONE: setBattery(battery +3);
			break;
		}
//		break;
//		case REFILING: setBattery(battery + 10);
//		System.out.println("Level of battery :"+battery);
//		//writer.write("Level of battery :"+battery); writer.write("\n");
//		//shouldn't be >100
//		break;
//		default: System.out.println("error");
//		break;
//		}
		return battery;
	}
	public BatteryLevel updateBatteryLevel(BatteryLevel batteryLevel, SignalMode signal, int battery) /*throws IOException*/ {
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
	public SignalMode updateSignal(SignalMode signal, BatteryLevel batteryLevel, Zone zone) /*throws IOException*/ {
		switch(zone) {
			case noWIFIGPS:
				if(batteryLevel == BatteryLevel.HIGH && (!isGPSActivated(signal))) {
					addGPS(signal, policyArray[2]);
					if( !isRadioActivated(signal)) addRadio(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (isWifiActivated(signal))){
					removeWifi(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.LOW && isWifiActivated(signal) ) {
					removeWifi(signal,policyArray[2]);
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && signal != SignalMode.GPS) {
					setSignal(SignalMode.GPS); //CAREFUL
					System.out.println("Signal mode activated : GPS");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case WIFInoGPS: 
				if(batteryLevel == BatteryLevel.HIGH && (!isWifiActivated(signal))) {
					addWifi(signal,policyArray[2]);
					if( !isRadioActivated(signal)) addRadio(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (isGPSActivated(signal))){
					removeGPS(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.LOW && isGPSActivated(signal) ) {
					removeGPS(signal,policyArray[2]);
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && signal != SignalMode.WIFI) {
					setSignal(SignalMode.WIFI); //CAREFUL
					System.out.println("Signal mode activated : WIFI");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case WIFIGPS:
				if(batteryLevel == BatteryLevel.HIGH && (!isGPSActivated(signal))) {
					addGPS(signal,policyArray[2]);
					if( !isRadioActivated(signal)) addRadio(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				if(batteryLevel == BatteryLevel.HIGH && (!isWifiActivated(signal))) {
					addWifi(signal,policyArray[2]);
					if( !isRadioActivated(signal)) addRadio(signal,policyArray[1]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (!isGPSActivated(signal))) {
					addGPS(signal,policyArray[1]);
				}
				if(batteryLevel == BatteryLevel.MEDIUM && (!isWifiActivated(signal))) {
					addWifi(signal,policyArray[1]);
				}
				else if(batteryLevel == BatteryLevel.LOW && isGPSActivated(signal) ) { 
					removeGPS(signal,policyArray[1]); //GPS consumes more energy than wifi
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && signal != SignalMode.WIFI) {
					setSignal(SignalMode.WIFI); //CAREFUL
					System.out.println("Signal mode activated : WIFI");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case noWIFInoGPS:
				if(batteryLevel == BatteryLevel.HIGH || batteryLevel == BatteryLevel.MEDIUM && (!isRadioActivated(signal))) {
					addRadio(signal,policyArray[2]);
				}
				else if(batteryLevel == BatteryLevel.MEDIUM && (isWifiActivated(signal))){
					removeWifi(signal,policyArray[1]);
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[0]);
					//writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				if(batteryLevel == BatteryLevel.LOW && isGPSActivated(signal) ) { 
					removeGPS(signal,policyArray[2]);
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[1]);
				}
				if(batteryLevel == BatteryLevel.LOW && isWifiActivated(signal) ) { 
					removeWifi(signal,policyArray[2]);
					if(isRadioActivated(signal)) removeRadio(signal,policyArray[2]);
				}
				else if (batteryLevel==BatteryLevel.VERYLOW && signal != SignalMode.NONE) {
					setSignal(SignalMode.NONE); //CAREFUL
					System.out.println("Signal mode activated : None");
					//writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				
		}
		return signal;
	}
	
	public void removeWifi(SignalMode signal, double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.ALL) {
				setSignal(SignalMode.GPSnRADIO);
				System.out.println("Signal mode desactivated : WIFI");
			}
			else if(signal == SignalMode.WIFInGPS) {
				setSignal(SignalMode.GPS);
				System.out.println("Signal mode desactivated : WIFI");
			}
			else if(signal == SignalMode.WIFInRADIO) {
				setSignal(SignalMode.RADIO);
				System.out.println("Signal mode desactivated : WIFI");
			}
			else if(signal == SignalMode.WIFI) { 
	 			setSignal(SignalMode.NONE);
				System.out.println("Signal mode desactivated : WIFI");
			}
		}
	}
//	public void removeWifi(SignalMode signal, String priority) {
//
//		if(signal == SignalMode.ALL) {
//			setSignal(SignalMode.GPSnRADIO);
//			System.out.println("Signal mode desactivated : WIFI");
//		}
//		else if(signal == SignalMode.WIFInGPS) {
//			setSignal(SignalMode.GPS);
//			System.out.println("Signal mode desactivated : WIFI");
//		}
//		else if(signal == SignalMode.WIFInRADIO) {
//			setSignal(SignalMode.RADIO);
//			System.out.println("Signal mode desactivated : WIFI");
//		}
//		else if(signal == SignalMode.WIFI) { 
// 			setSignal(SignalMode.NONE);
//			System.out.println("Signal mode desactivated : WIFI");
//		}
//	}
	public void removeGPS(SignalMode signal, double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.ALL) {
				setSignal(SignalMode.WIFInRADIO);
				System.out.println("Signal mode desactivated : GPS");
			}
			else if(signal == SignalMode.WIFInGPS) {
				setSignal(SignalMode.WIFI);
				System.out.println("Signal mode desactivated : GPS");
			}
			else if(signal == SignalMode.GPSnRADIO) {
				setSignal(SignalMode.RADIO);
				System.out.println("Signal mode desactivated : GPS");
			}
			else if(signal == SignalMode.GPS) { 
	 			setSignal(SignalMode.NONE);
				System.out.println("Signal mode desactivated : GPS");
			}
		}
	}
	
	public void removeRadio(SignalMode signal, double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.ALL) {
				setSignal(SignalMode.WIFInGPS);
				System.out.println("Signal mode desactivated : Radio");
			}
			else if(signal == SignalMode.GPSnRADIO) {
				setSignal(SignalMode.GPS);
				System.out.println("Signal mode desactivated : Radio");
			}
			else if(signal == SignalMode.WIFInRADIO) {
				setSignal(SignalMode.WIFI);
				System.out.println("Signal mode desactivated : Radio");
			}
			else if(signal == SignalMode.RADIO) {
	 			setSignal(SignalMode.NONE);
				System.out.println("Signal mode desactivated : Radio");
			}
		}
	}
	
	public void addWifi(SignalMode signal, double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.RADIO) {
				setSignal(SignalMode.WIFInRADIO);
				System.out.println("Signal mode activated : WIFI");
			}
			if(signal == SignalMode.GPS) {
				setSignal(SignalMode.WIFInGPS);
				System.out.println("Signal mode activated : WIFI");
			}
			if(signal == SignalMode.GPSnRADIO) {
				setSignal(SignalMode.ALL);
				System.out.println("Signal mode activated : WIFI");
			}
			if(signal == SignalMode.NONE) {
				setSignal(SignalMode.WIFI);
				System.out.println("Signal mode activated : WIFI");
			}
		}
	}
	
	public void addGPS(SignalMode signal, double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.RADIO) {
				setSignal(SignalMode.GPSnRADIO);
				System.out.println("Signal mode activated : GPS");
			}
			if(signal == SignalMode.WIFI) {
				setSignal(SignalMode.WIFInGPS);
				System.out.println("Signal mode activated : GPS");
			}
			if(signal == SignalMode.WIFInRADIO) {
				setSignal(SignalMode.ALL);
				System.out.println("Signal mode activated : GPS");
			}
			if(signal == SignalMode.NONE) {
				setSignal(SignalMode.GPS);
				System.out.println("Signal mode activated : GPS");
			}
		}
	}
	
	public void addRadio(SignalMode signal,double proba) {
		double rand = Math.random();
		System.out.println("proba = " +proba + "and rand = " + rand);
		if(rand < proba) {
			if(signal == SignalMode.WIFI) {
				setSignal(SignalMode.WIFInRADIO);
				System.out.println("Signal mode activated : Radio");
			}
			if(signal == SignalMode.GPS) {
				setSignal(SignalMode.GPSnRADIO);
				System.out.println("Signal mode activated : Radio");
			}
			if(signal == SignalMode.WIFInGPS) {
				setSignal(SignalMode.ALL);
				System.out.println("Signal mode activated : Radio");
			}
			if(signal == SignalMode.NONE) {
				setSignal(SignalMode.RADIO);
				System.out.println("Signal mode activated : Radio");
			}
		}
	}
	
//	public boolean probabilistic(double proba) { // given a probability between 0 and 1
//		return Math.random() < proba;
//	}
	
	public boolean isRadioActivated(SignalMode signal) {
		return (signal == SignalMode.ALL || signal == SignalMode.GPSnRADIO || signal == SignalMode.WIFInRADIO || signal == SignalMode.RADIO);
	}
	public boolean isWifiActivated(SignalMode signal) {
		return (signal == SignalMode.ALL || signal == SignalMode.WIFInRADIO || signal == SignalMode.WIFInGPS || signal == SignalMode.WIFI);
	}
	public boolean isGPSActivated(SignalMode signal) {
		return (signal == SignalMode.ALL || signal == SignalMode.GPSnRADIO || signal == SignalMode.WIFInGPS || signal == SignalMode.GPS);
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
	public SignalMode getSignal() {
		return signal;
	}
	public void setSignal(SignalMode signal) {
		this.signal = signal;
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
}
