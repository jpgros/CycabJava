import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Element implements Serializable {
	PolicyName name;
	//Priority priority;
	double priority;
	Vehicle vehicle;
	
	public Element(PolicyName n, double p) {
		name =n;
		priority=p;
	}
	
	public Element(PolicyName n, double p, Vehicle v) {
		name =n;
		priority=p;
		vehicle = v;
	}
	public Element(PolicyName n) {
		name =n;
		priority=0;
		vehicle=null;
	}
    @Override
    public int hashCode() {
        return Objects.hash(name, priority, vehicle);
    }
   @Override
   public boolean equals(Object obj) {
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
   public boolean isContainedOnNameAndVehicle(ArrayList<Element> arrayElt) {
	   for(Element elt :arrayElt) {
		   if(this.name==elt.name && this.vehicle == elt.vehicle && this.priority<elt.priority) return true;
	   }
	   return false;   
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

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public String toString() {
		return name + "(" + vehicle.id + ") -> " + priority;
	}

}