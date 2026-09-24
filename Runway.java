package airportccpassignment;

public class Runway {
    private boolean runwayAvailability = true;
    
    public synchronized void occupiedRunway() throws InterruptedException{
        while (!getRunwayAvailability()){
            wait(); //wait until runway is available
        }
        runwayAvailability = false;
    }
    
    public synchronized void emptyRunway(){
        runwayAvailability = true; 
        notifyAll();
    }
    
    public boolean getRunwayAvailability(){
        return runwayAvailability;
    }
}
