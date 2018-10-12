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
    PrintWriter writerErr = null;
    ExecutionReport er = null;    //shallow copy  

    public VanetConformanceMonitor(AdaptationPolicyModel _apm, PrintWriter w) {
        apm = _apm;
        writerErr = w;
        er = new ExecutionReport(writerErr);
    }

    public void notify(MyStep newStep, Road sut) {
        apm.match(sut, er);
        sut.setStepName(newStep.meth.getName());
    }

    public void printReport() {
        er.dump();
        er.sortEligibleSteps();
        try {
			er.stats();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
