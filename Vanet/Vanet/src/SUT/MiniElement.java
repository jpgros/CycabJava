package SUT;
import java.util.Objects;

public class MiniElement {
	PolicyName name;
	Priority priority;
	
	public MiniElement(PolicyName n, Priority p) {
		name =n;
		priority=p;
	}
	

	public MiniElement(PolicyName n) {
		name =n;
		priority=null;
	}
    @Override
    public int hashCode() {
        return Objects.hash(name, priority);
    }
   @Override
   public boolean equals(Object obj) {

      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MiniElement elt = (MiniElement) obj;
      if (this.name == elt.name && this.priority == elt.priority ) { //
    	  return true;
      }
      else {
    	  return false;
      }
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
		return name + " " + priority;
	}

}
