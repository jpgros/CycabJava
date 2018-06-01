import java.io.PrintWriter;

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
    }

    public void printReport() {
        er.dump();
    }

}
