package SUT;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 30/05/2018
 * Time: 10:43
 */
public class VanetConformanceMonitor {

    AdaptationAutomaton<Road> aa;

    AdaptationPolicyModel apm = null;
    LogPrinter writerErr = null;
    ExecutionReport er = null;    //shallow copy  

    public VanetConformanceMonitor(AdaptationPolicyModel _apm, LogPrinter w) {
        apm = _apm;
        writerErr = w;
        er = new ExecutionReport(writerErr);
    }

    public double notify(MyStep newStep, Road sut)  {
		double cov =apm.match(sut, er);
        sut.setStepName(newStep.meth.getName());
        return cov;
    }

    public void printReport() {
        er.dump();
        er.sortEligibleSteps();
        try {
			//er.stats();
			er.statsFreq();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
