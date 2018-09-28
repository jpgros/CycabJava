import java.util.Objects;

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
	public Element(PolicyName n) {
		name =n;
		priority=null;
		vehicle=null;
	}
    @Override
    public int hashCode() {
        return Objects.hash(name, priority, vehicle);
    }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Element elt = (Element) obj;
      if (this.name == elt.name && this.priority == elt.priority && this.vehicle == elt.vehicle) { //
    	  return true;
      }
      else {
    	  return false;
      }
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