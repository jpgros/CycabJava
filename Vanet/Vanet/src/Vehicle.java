import java.util.UUID;

public class Vehicle {
	int autonomie;
	int distance;
	String id;
	
	public void tick() {
		this.autonomie -= 1;
		this.distance -=1;
	}
	
	public void refill() {
		this.autonomie =100;
	}
	//UUID uniqueKey = UUID.randomUUID();
}
