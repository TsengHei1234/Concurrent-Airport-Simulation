package airportccpassignment;

import java.util.ArrayList;

public class GateManager implements Runnable{
    // Shared static lists for gates and their availability status
    public static ArrayList<Gate> gateList = new ArrayList<>();
    public static ArrayList<Boolean> gateAvailability = new ArrayList<>();
    
    public volatile boolean operating = true;
    public Airplane airplane = null;
    public Gate gateTaken;
    private boolean assigningGate;
    
    // Static initializer block to create three gates with available status
    static{
        gateList.add(new Gate(1));
        gateList.add(new Gate(2));
        gateList.add(new Gate(3));
        gateAvailability.add(true);
        gateAvailability.add(true);
        gateAvailability.add(true);
    }
    
    public GateManager(){
        
    }
    
    // Finds and returns the first available gate, then sets it as unavailable
    private Gate availableGate(){
        while (true){
            for (int i = 0; i < gateList.size(); i++){
                if (gateAvailability.get(i) == true){
                    gateAvailability.set(i, false);
                    return gateList.get(i);
                }
            }
        }
    }
    
    // Allows airplane to request a gate and waits until it is assigned
    public Gate requestGate(){
        assigningGate = true;
        while (assigningGate){
            try{
                Thread.sleep(500);
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        return gateTaken;
    }
    
    // Releases the gate and marks it as available again after airplane take-off
    public void releaseGate(Gate gate){
        gateTaken = gate;
        for (int i = 0; i < gateList.size(); i++){
            if (gateList.get(i) == gateTaken){
                gateAvailability.set(i, true);
            }
        }
        gateTaken = null;
    }
    
    // Continously monitors for gate request from ATC
    public void run(){
        while (operating){
            while (!assigningGate){
                try {
                    if (!operating){
                        break;
                    }
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if (operating){
                this.gateTaken = availableGate();
                assigningGate = false;
                airplane = null;
            }
        }
    }
    
    public void terminateGateManager(){
        operating = false;
    }
    
    public ArrayList<Boolean> getGateAvailability(){
        return gateAvailability;
    }
    
    public ArrayList<Gate> getGateList(){
        return gateList;
    }
}
