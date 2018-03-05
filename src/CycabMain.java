import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CycabMain {

	public static void main(String[] args) throws IOException {
	
		String baseDir=System.getProperty("user.dir");
		Controller controller= new Controller(100, SignalMode.BOTH, BatteryMode.USING, Zone.NONE);
		controller.initFile(baseDir);
		int cpt=0;
		while(controller.getBattery()> 0 && controller.getBattery()<= 100 && cpt<15) {
			controller.updateBattery(controller.getBatteryMode(), controller.getSignal(), controller.getBattery());
			controller.updateBatteryMode(controller.getBatteryMode(), controller.getSignal(), controller.getBattery());
			controller.updateZone(controller.getZone());
			controller.updateSignal(controller.getSignal(), controller.getBattery(), controller.getZone());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cpt++;
		}
		controller.closeFile();
	
	}
	
}
