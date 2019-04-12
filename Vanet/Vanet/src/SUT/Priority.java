package SUT;

public enum Priority {
HIGH(3), MEDIUM(2), LOW(1);
	final int value;
	
	private Priority(int value) {
        this.value = value;
    }
}
