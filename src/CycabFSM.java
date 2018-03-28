/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

public class CycabFSM implements FsmModel {

    /**
     * Automaton describing the FSM of a Cycab
     */
    boolean inWifi = false;
    boolean inTunnel = false;

    Controller sut;
    
    
    public CycabFSM() {
        sut = new Controller(100, SignalMode.GPS, BatteryMode.USING, Zone.NONE);
        inWifi = inTunnel = false;
    }

    public String getState() {
        return "Wifi=" + inWifi + "-Tunnel=" + inTunnel;
    }

    public Controller getSUT() {
        return sut;
    }
    
    public void reset(boolean testing) {
        sut = new Controller(100, SignalMode.GPS, BatteryMode.USING, Zone.NONE);
        inWifi = inTunnel = false;
    }

    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return 0.98; }
    @Action
    public void tick() {
        sut.updateBattery(sut.getBatteryMode(), sut.getSignal(), sut.getBattery());
        sut.updateBatteryMode(sut.getBatteryMode(), sut.getSignal(), sut.getBattery());
        sut.updateSignal(sut.getSignal(), sut.getBattery(), sut.getZone());
    }


    /** Entrée WIFI **/
    public boolean enterWifiGuard() {
        return !inWifi;
    }
    public double enterWifiProba() { return 0.01; }
    @Action
    public void enterWifi() {
        inWifi = true;
        sut.setZone(Zone.WIFI);
    }

    /** Sortie WIFI **/
    public boolean exitWifiGuard() {
        return inWifi;
    }
    public double exitWifiProba() { return 0.01; }
    @Action
    public void exitWifi() {
        inWifi = false;
        sut.setZone(Zone.NONE);
    }


    /** Entrée TUNNEL **/
    public boolean enterTunnelGuard() {
        return !inTunnel;
    }
    public double enterTunnelProba() { return 0.01; }
    @Action
    public void enterTunnel() {
        inTunnel = true;
        sut.setZone(Zone.TUNNEL);
    }

    /** Sortie WIFI **/
    public boolean exitTunnelGuard() {
        return inTunnel;
    }
    public double exitTunnelProba() { return 0.01; }
    @Action
    public void exitTunnel() {
        inTunnel = false;
        sut.setZone(Zone.NONE);
    }
    
}

