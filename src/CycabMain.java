import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CycabMain {

	public static void main(String[] args) throws IOException {
	
		String baseDir=System.getProperty("user.dir");
		Controller controller= new Controller(100, SignalMode.ALL, BatteryLevel.HIGH, Zone.noWIFInoGPS, ThresholdPolicy.NORMAL);
		controller.initFile(baseDir);
		int cpt=0;
		double flipCoin = 0.5, changeZone=0.1;
		double[] policyArray = new double[3];
		while(controller.getBattery()> 0 && controller.getBattery()<= 100 && cpt<250) {
			controller.updateBattery(controller.getBatteryLevel(), controller.getSignal(), controller.getBattery());
			controller.updateBatteryLevel(controller.getBatteryLevel(), controller.getSignal(), controller.getBattery());
			controller.updateZone(controller.getZone(),flipCoin, changeZone);
			controller.updateSignal(controller.getSignal(), controller.getBatteryLevel(), controller.getZone());
			try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cpt++;
		}
		controller.closeFile();
	
	}
	
}
