/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 30/05/2018
 * Time: 10:43
 */
public class VanetConformanceMonitor {

    AdaptationAutomaton<Road> aa;

    AdaptationPolicyModel apm = null;

    ExecutionReport er = new ExecutionReport();
    

    public VanetConformanceMonitor(AdaptationPolicyModel _apm) {
        apm = _apm;
    }

    public void notify(MyStep newStep, Road sut) {
        apm.match(sut, er);
    }

    public void printReport() {
        er.dump();
    }

}
