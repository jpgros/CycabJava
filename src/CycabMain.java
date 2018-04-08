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
		int cpt=0, cycle=0;
		controller.testList();
		double flipCoin = 0.5, changeZone=0.1;
		double[] policyArray = new double[3];
		while(controller.getBattery()> 0 && controller.getBattery()<= 100 && cpt<250) {
			for(int i=0; i<10; i++) {
				controller.updateBattery(controller.getBattery());
				controller.updateBatteryLevel(controller.getBatteryLevel(), controller.getBattery());
				controller.updateZone(controller.getZone(),flipCoin, changeZone);
				controller.updateSignal(controller.getBatteryLevel(), controller.getZone());
				try {
					Thread.sleep(750);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cpt++;
			}
			if(!controller.getCycabComponents().getComponentList().contains("Communication")) {
				controller.addCommunication();
			}
			else {
				System.out.println("removing");
				controller.removeCommunication();
			}
		}
		controller.closeFile();
	
	}
	
}
