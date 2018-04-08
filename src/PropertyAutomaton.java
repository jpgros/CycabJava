/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 27/03/2018
 * Time: 13:35
 */
public interface PropertyAutomaton<SUT> {
    public int getState();
    public void reset();
    public double match(SUT o);
}

abstract class CyCabProperty implements PropertyAutomaton<Controller> {

    protected int state = 0;

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void reset() {
        state = 0;
    }

    @Override
    public abstract double match(Controller sut);
}


class Property1 extends CyCabProperty {

    // after addwifi terminates, before removewifi terminates, eventually power < 33

    public double match(Controller sut) {
        switch (state) {
            case 0:
                if (!sut.getCycabComponents().getComponentList().contains("GPS")) {
                    state = (sut.battery >= 33) ? 1 : 2;
                }
                break;
            case 1:
                if (!sut.getCycabComponents().getComponentList().contains("GPS") && sut.battery < 33) {
                    state = 2;
                }
                else if (sut.getCycabComponents().getComponentList().contains("GPS")) {
                    state = 0;
                }
                break;
            case 2:
                if (sut.getCycabComponents().getComponentList().contains("GPS")) {
                    state = 3;
                }
                break;
        }
        switch (state) {
            case 0:
                return 3;
            case 1:
                return 2 + (sut.battery - 33) / 100.0;
            case 2:
                return 1;
        }
        return 0;
    }
}


class Property2 extends CyCabProperty {

    // after addgps terminates, before removegps terminates, eventually power < 33

    public double match(Controller sut) {
        switch (state) {
            case 0:
                if (!sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = (sut.battery >= 33) ? 1 : 2;
                }
                break;
            case 1:
                if (!sut.getCycabComponents().getComponentList().contains("Wifi") && sut.battery < 33) {
                    state = 2;
                }
                else if (sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = 0;
                }
                break;
            case 2:
                if (sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = 3;
                }
                break;
        }
        switch (state) {
            case 0:
                return 3;
            case 1:
                return 2 + (sut.battery - 33) / 100.0;
            case 2:
                return 1;
        }
        return 0;
    }
}


class Property3 extends CyCabProperty {

    // after removegps terminates, before addgps terminates, eventually power > 33

    public double match(Controller sut) {
        switch (state) {
            case 0:
                if (sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = (sut.battery <= 33) ? 1 : 2;
                }
                break;
            case 1:
                if (sut.getCycabComponents().getComponentList().contains("Wifi") && sut.battery > 33) {
                    state = 2;
                }
                else if (!sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = 0;
                }
                break;
            case 2:
                if (!sut.getCycabComponents().getComponentList().contains("Wifi")) {
                    state = 3;
                }
                break;
        }
        switch (state) {
            case 0:
                return 3;
            case 1:
                return 2 + (33 - sut.battery) / 100.0;
            case 2:
                return 1;
        }
        return 0;
    }
}
