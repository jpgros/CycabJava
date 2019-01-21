import java.lang.*;
public enum LogLevel {
	VERBOSE(1), INFO(2), ERROR(3);
	int val;
	LogLevel(int p) {
	   val = p;
	}
	int showvalue() {
		return val;
	}
}