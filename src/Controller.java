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
	Writer writer;
	SignalMode signal = SignalMode.BOTH;
	BatteryMode batteryMode = BatteryMode.USING;
	Zone zone = Zone.NONE;
	public Controller(String basedir) {
			
	}
	public Controller( int battery, SignalMode signal, BatteryMode batteryMode,
			Zone zone) {
		super();
		this.battery = battery;
		this.signal = signal;
		this.batteryMode = batteryMode;
		this.zone = zone;
	}
	public void initFile(String baseDir) throws IOException {
	      	//File file = new File(baseDir+"/logs/logs.txt");
	      	//boolean b = file.createNewFile();
			this.writer = new FileWriter(baseDir+"/logs/logs.txt");
	}
	public  int updateBattery(BatteryMode batteryMode,SignalMode signal, int battery) throws IOException {
		switch(batteryMode) {
		case USING:
			switch(signal) {
				case BOTH: setBattery(battery - 5);
				System.out.println("Level of battery :"+battery);
				writer.write("Level of battery :"+battery); writer.write("\n");
				//shouldn't be <0
				break;
				case WIFI: setBattery(battery - 2);
				System.out.println("Level of battery :"+battery);
				writer.write("Level of battery :"+battery); writer.write("\n");
				//shouldn't be <0
				break;
				case GPS: setBattery(battery - 3);
				System.out.println("Level of battery :"+battery);
				writer.write("Level of battery :"+battery); writer.write("\n");
				//shouldn't be <0
				break;
			}
		break;
		case REFILING: setBattery(battery + 10);
		System.out.println("Level of battery :"+battery);
		writer.write("Level of battery :"+battery); writer.write("\n");
		//shouldn't be >100
		break;
		default: System.out.println("error");
		break;
		}
		return battery;
	}
	public BatteryMode updateBatteryMode(BatteryMode batteryMode, SignalMode signal, int battery) throws IOException{
		if(battery < 10 && batteryMode != BatteryMode.REFILING) {
			setBatteryMode(BatteryMode.REFILING);
			System.out.println("Battery mode in charge");
			writer.write("Battery mode in charge"); writer.write("\n");
		}
		else if(battery <20 && signal==SignalMode.BOTH && batteryMode != BatteryMode.REFILING) {
			setBatteryMode(BatteryMode.REFILING);
			System.out.println("Battery mode in charge");
			writer.write("Battery mode in charge"); writer.write("\n");
		}
		else if(battery > 80 && signal != signal.BOTH && batteryMode != BatteryMode.USING) {
			setBatteryMode(BatteryMode.USING);
			System.out.println("Battery mode in use");
			writer.write("Battery mode in use"); writer.write("\n");
		}
		else if(battery > 90 && batteryMode != BatteryMode.USING) {
			setBatteryMode(BatteryMode.USING);
			System.out.println("Battery mode in use");
			writer.write("Battery mode in use"); writer.write("\n");
		}
		return batteryMode;
	}
	public Zone updateZone(Zone zone) throws IOException {
		if (Math.random() +0.1 > 1.0) {
			if(zone == Zone.TUNNEL) {
				setZone(Zone.NONE);
				System.out.println("Exiting tunnel");
				writer.write("Exiting tunnel"); writer.write("\n");
			}
			else if (zone == Zone.WIFI){
				setZone(Zone.NONE);
				System.out.println("Exiting Wifi area");
				writer.write("Exiting Wifi area"); writer.write("\n");
			}
			else if(zone == Zone.NONE) {
				if(Math.random()>0.5) {
					setZone(Zone.TUNNEL);
					System.out.println("Entering tunnel");
					writer.write("Entering tunnel"); writer.write("\n");
				}
				else {
					setZone(Zone.WIFI);
					System.out.println("Entering Wifi area");
					writer.write("Entering Wifi area"); writer.write("\n");
				}
			}
		}
		return zone;
	}
	public SignalMode updateSignal(SignalMode signal, int battery, Zone zone) throws IOException {
		switch(zone) {
			case NONE:
				if(battery > 80 && signal != SignalMode.BOTH) {
					setSignal(SignalMode.BOTH);
					System.out.println("Signal mode activated : BOTH");
					writer.write("Signal mode activated : BOTH"); writer.write("\n");
				}
				else if (battery < 30 && signal != SignalMode.GPS) {
					setSignal(SignalMode.GPS);
					System.out.println("Signal mode activated : GPS");
					writer.write("Signal mode activated : GPS"); writer.write("\n");
				}
				break;
			case TUNNEL: 
				if(battery < 70 && signal != SignalMode.WIFI) {
					setSignal(SignalMode.WIFI);
					System.out.println("Signal mode activated : WIFI");
					writer.write("Signal mode activated : WIFI"); writer.write("\n");
				}
				break;
			case WIFI:
				if(battery < 30 && signal != SignalMode.WIFI) {
					setSignal(SignalMode.WIFI);
					System.out.println("Signal mode activated : WIFI");
					writer.write("Signal mode activated : WIFI"); writer.write("\n");
				}
				break;
		}
		return signal;
	}
	public void closeFile() throws IOException {
		writer.close();
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
	public BatteryMode getBatteryMode() {
		return batteryMode;
	}
	public void setBatteryMode(BatteryMode batteryMode) {
		this.batteryMode = batteryMode;
	}
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
}
