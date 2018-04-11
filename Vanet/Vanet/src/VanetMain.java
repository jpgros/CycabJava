import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VanetMain {
	public static ArrayList<Object> vehiclePlatoonList = new ArrayList<Object>();
	public static ExecutorService pool=Executors.newFixedThreadPool(30);
	public static void main(String[] args) {
		Vehicle[] vehicles = new Vehicle[20];
		
		for(int i=0; i<vehicles.length; i++) {
			vehicles[i] = new Vehicle(100, 250,UUID.randomUUID(),vehiclePlatoonList);
			vehiclePlatoonList.add(vehicles[i]);
			pool.execute(vehicles[i]);
		}
		
	}

}
