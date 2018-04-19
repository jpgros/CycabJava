public class Element{
	PolicyName name;
	Priority priority;
	Vehicle vehicle;
	
	public Element(PolicyName n, Priority p) {
		name =n;
		priority=p;
	}
	
	public Element(PolicyName n, Priority p, Vehicle v) {
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


	public PolicyName getName() {
		return name;
	}

	public void setName(PolicyName name) {
		this.name = name;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String toString() {
		return name + "(" + vehicle.id + ") -> " + priority;
	}

}