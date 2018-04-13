import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VanetMain {
	public static ArrayList<Entity> vehiclePlatoonList = new ArrayList<Entity>();
	public static ExecutorService pool=Executors.newFixedThreadPool(30);


	public static void main(String[] args) {

		Road r = new Road();
		r.addVehicle(40, (int)(Math.random() * 1000));
		r.addVehicle(40, (int)(Math.random() * 1000));
		r.addVehicle(40, (int)(Math.random() * 1000));
		r.join(0, 1);
		r.join(2, 1);
		int i=0;
		while (i < 10) {
			i++;
			r.tick();
			r.affiche();
		}

		/*
		Vehicle[] vehicles = new Vehicle[3];
		for(int i=0; i<vehicles.length; i++) {
			vehicles[i] = new Vehicle(40, (int)(Math.random() * 1000), UUID.randomUUID(),vehiclePlatoonList);
			vehiclePlatoonList.add(vehicles[i]);
			// pool.execute(vehicles[i]);
		}

		vehicles[0].join(vehicles[1]);
		vehicles[2].join(vehicles[1]);

		int i=0;
		while (i < 10) {
			i++;
			System.out.println("tick : " + i);
			for (Vehicle v : vehicles) {
				v.tick();
			}
			for (Vehicle v : vehicles) {
				if (v.myPlatoon != null && v.myPlatoon.leader == v) {
					v.myPlatoon.affiche();
					v.myPlatoon.tick();
				}
			}

		}
		// vehicles[0].quitPlatoon();

		/*
		int i=0;
		while (i < 100) {
			i++;
			System.out.println("tick : " + i);
			
			for (Vehicle v : vehicles) {
				v.tick();
				if (v.myPlatoon != null && v.myPlatoon.leader == v) {
					v.myPlatoon.affiche();
				}
			}
			if (Math.random() < 0.05) {
				int v1 = (int) (Math.random() * vehicles.length);
				int v2 = 0;
				do {
					v2 = (int) (Math.random() * vehicles.length);
				}
				while (v1 == v2);
				if (vehicles[v1].myPlatoon == null) {
					System.out.println("Vehicle " + v1 + " wants to join vehicle " + v2);
					vehicles[v1].join(vehicles[v2]);
				}
			}

		}
		*/
		
	}

}
