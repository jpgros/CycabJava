import static java.util.UUID.randomUUID;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VanetMain {
	public static ArrayList<Entity> vehiclePlatoonList = new ArrayList<Entity>();
	public static ExecutorService pool=Executors.newFixedThreadPool(30);


	public static void main(String[] args) {
		PolicyName n1 = PolicyName.QUITFAILURE;
		Priority p1 = Priority.HIGH;
		Priority p3 = Priority.HIGH;
		Priority p2 =Priority.LOW;
		Vehicle v1 = new Vehicle(50.0, 400.0, randomUUID(), null, null, 1.0);
		Vehicle v2 = new Vehicle(50.0, 400.0, randomUUID(), null, null, 1.0);
		Vehicle v3 = new Vehicle(50.0, 400.0, randomUUID(), null, null, 1.0);
		Vehicle v4 = new Vehicle(50.0, 400.0, randomUUID(), null, null, 1.0);
		
		Element e1 = new Element(n1, 8, v1);
		//Element e2 = new Element(n1, p2, v2);
		Element e3 = new Element(n1, 2, v3);
		Element e4 = new Element(n1, 8, v4);
		Element e5 = new Element(n1, 1, v3);
		
		HashMap<Element, Integer> map = new HashMap<Element, Integer>();
		
		map.put(e1, 1);
		//map.put(e2, 1);
		map.put(e3,1);
		map.put(e4, 1);
		boolean bool =e5.equals(e3);
		System.out.println(bool);
		System.out.println(map.get(e3));
		map.put(e5, 4);
		for(Map.Entry<Element,Integer> elt : map.entrySet()) {
			System.out.println(elt);
		}
//    	String writer="";
//    	String vehicleReader="";
//    	String platoonReader="";
//    	String roadReader="";
//        String writerLog ="";
//		Road r = new Road(writer, vehicleReader,platoonReader,roadReader, writerLog);
//		r.addVehicle(40, (int)(Math.random() * 1000));
//		r.addVehicle(40, (int)(Math.random() * 1000));
//		r.addVehicle(40, (int)(Math.random() * 1000));
//		r.join(0, 1);
//		r.join(2, 1);
//		int i=0;
//		while (i < 10) {
//			i++;
//			r.tick();
//			r.affiche();
//		}
//		int low=0,medium=0,high =0;
//		FileReader reader=null;
//		try {
//			reader = new FileReader("output.txt");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		BufferedReader outputReader = new BufferedReader(reader);
//		String line =null;
//		String priority=null;
//		try {
//			while((line = outputReader.readLine()) != null) {
//				if(line.contains("Reconfiguration")){
//					priority=line.substring(line.indexOf("{")+1, line.indexOf("}"));
//					switch(priority) {
//					case "LOW" : low++;
//						break;
//					case "MEDIUM" : medium++;
//						break;
//					case "HIGH" : high++;
//						break;
//					default : System.out.println(priority); 
//					}
//				}
//			}
//		System.out.println("High reconfiguration occured " + high+ " times, medium reconfiguration occured " + medium + " times, low reconfiguration occured "+ low+ " times");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
