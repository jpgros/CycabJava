/**
 * Created with IntelliJ IDEA.
 * User: Frederic Dadeau
 * Date: 07/03/2018
 * Time: 15:25
 */

import nz.ac.waikato.modeljunit.*;

import java.io.IOException;

public class CycabFSM implements FsmModel {

    /**
     * Automaton describing the FSM of a Cycab
     */
    boolean inWifi = false;
    boolean inGPS = false;

    Controller sut;
    
    
    public CycabFSM() {
    	sut = new Controller(100, SignalMode.GPS, BatteryLevel.HIGH, Zone.noWIFIGPS, ThresholdPolicy.NORMAL);
        inWifi = inGPS = false;
    }

    public String getState() {
        return "Wifi=" + inWifi + "-GPS=" + inGPS;
    }

    public Controller getSUT() {
        return sut;
    }
    
    public void reset(boolean testing) {
    	sut = new Controller(100, SignalMode.GPS, BatteryLevel.HIGH, Zone.noWIFIGPS, ThresholdPolicy.NORMAL);
        inWifi = inGPS = false;
    }

    public boolean tickGuard() {
        return true;
    }
    public double tickProba() { return 0.98; }
    @Action
    public void tick() {
        sut.updateBattery(sut.getSignal(), sut.getBattery());
        sut.updateBatteryLevel(sut.getBatteryLevel(), sut.getBattery());
        sut.updateSignal(sut.getSignal(), sut.getBatteryLevel(), sut.getZone());
    }


    /** Entrée WIFI **/
    public boolean enterWifiGuard() {
        return !inWifi;
    }
    public double enterWifiProba() { return 0.01; }
    @Action
    public void enterWifi() {
        inWifi = true;
        if(sut.getZone()== Zone.noWIFInoGPS) {
        	sut.setZone(Zone.WIFInoGPS);
        }
        else if(sut.getZone()== Zone.noWIFIGPS) {
            sut.setZone(Zone.WIFIGPS);
        }
        
    }

    /** Sortie WIFI **/
    public boolean exitWifiGuard() {
        return inWifi;
    }
    public double exitWifiProba() { return 0.01; }
    @Action
    public void exitWifi() {
        inWifi = false;
        if(sut.getZone()== Zone.WIFInoGPS) {
        	sut.setZone(Zone.noWIFInoGPS);
        }
        else if(sut.getZone()== Zone.WIFIGPS) {
            sut.setZone(Zone.noWIFIGPS);
        }
    }


    /** Entrée GPS **/
    public boolean enterGPSGuard() {
        return !inGPS;
    }
    public double enterGPSProba() { return 0.01; }
    @Action
    public void enterGPS() {
        inGPS = true;
        if(sut.getZone()== Zone.noWIFInoGPS) {
        	sut.setZone(Zone.noWIFIGPS);
        }
        else if(sut.getZone()== Zone.WIFInoGPS) {
            sut.setZone(Zone.WIFIGPS);
        }
    }

    /** Sortie GPS **/
    public boolean exitGPSGuard() {
        return inGPS;
    }
    public double exitGPSProba() { return 0.01; }
    @Action
    public void exitGPS() {
        inGPS = false;
        if(sut.getZone()== Zone.noWIFIGPS) {
        	sut.setZone(Zone.noWIFInoGPS);
        }
        else if(sut.getZone()== Zone.WIFIGPS) {
            sut.setZone(Zone.WIFInoGPS);
        }
    }
    
}

