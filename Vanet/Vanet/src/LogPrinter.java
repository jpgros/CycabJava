import java.io.FileWriter;
import java.io.PrintWriter;

public class LogPrinter {
	LogLevel logLevel;
	LogLevel globalLogLevel;
	PrintWriter file;
	public LogPrinter(PrintWriter fw,LogLevel lvl, LogLevel globalLevel) {
		file = fw;
		logLevel=lvl;
		globalLogLevel= globalLevel;
	}
	
	public void print(Object s) {
		if(logLevel.showvalue()>= globalLogLevel.showvalue()) {
			file.print(s);
		}
	}
	public void println(Object s) {
		if(logLevel.showvalue()>= globalLogLevel.showvalue()) {
			file.print(s+"\n");
		}
	}
	public void close() {
		file.close();
	}
}
