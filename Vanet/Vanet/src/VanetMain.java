import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VanetMain {
	public static ArrayList<Entity> vehiclePlatoonList = new ArrayList<Entity>();
	public static ExecutorService pool=Executors.newFixedThreadPool(30);


	public static void main(String[] args) {
//    	PrintWriter writer = new PrintWriter("outputGenetic.txt", "UTF-8");
//    	FileReader vehicleReader = new FileReader("vehiclePolicies.txt");
//    	FileReader platoonReader = new FileReader("platoonPolicies.txt");
//    	FileReader roadReader = new FileReader("platoonPolicies.txt"); 
    	String writer="";
    	String vehicleReader="";
    	String platoonReader="";
    	String roadReader="";
		Road r = new Road(writer, vehicleReader,platoonReader,roadReader);
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
//		writer.close();
//		try {
//			vehicleReader.close();
//			platoonReader.close();
//			roadReader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		int low=0,medium=0,high =0;
		FileReader reader=null;
		try {
			reader = new FileReader("output.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader outputReader = new BufferedReader(reader);
		String line =null;
		String priority=null;
		try {
			while((line = outputReader.readLine()) != null) {
				if(line.contains("Reconfiguration")){
					priority=line.substring(line.indexOf("{")+1, line.indexOf("}"));
					switch(priority) {
					case "LOW" : low++;
						break;
					case "MEDIUM" : medium++;
						break;
					case "HIGH" : high++;
						break;
					default : System.out.println(priority); 
					}
				}
			}
		System.out.println("High reconfiguration occured " + high+ " times, medium reconfiguration occured " + medium + " times, low reconfiguration occured "+ low+ " times");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
