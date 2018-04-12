public class Element{
	String name;
	Priority priority;
	Vehicle vehicle;
	
	public Element(String n, Priority p) {
		name =n;
		priority=p;
	}
	
	public Element(String n, Priority p, Vehicle v) {
		name =n;
		priority=p;
		vehicle = v;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	

}