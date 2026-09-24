package airportccpassignment;

public class Gate {
    private int gateID;
    private RefuelTruck refuelTruck;
    
    public Gate(int id){
        gateID = id;
    }
    
    public String toString(){
        return "Gate " + gateID;
    }
    
    public int getGateID(){
        return gateID;
    }
    
    public void gateProcess(Airplane airplane, Gate gate, Thread disembarkPassengersThread){
        // Create threads for refilling supplies, cleaning, and embarking passengers
        SupplyRefillCleaningCrew srcRunnable = new SupplyRefillCleaningCrew(airplane);
        Thread srcThread = new Thread(srcRunnable);
        Passengers embarkPassengersRunnable = new Passengers(airplane, gate);
        Thread embarkPassengersThread = new Thread(embarkPassengersRunnable);
        
        disembarkPassengersThread.start();
        srcThread.start();
        refuelTruck.requestRefuellingTruck(airplane); // Request refuelling truck
        
        // Wait for disembark to complete before starting embark
        try{
            disembarkPassengersThread.join();
        }catch(InterruptedException ex){}
        
        embarkPassengersThread.start();
        embarkPassengersThread.setName("Gate "+gate.gateID+" Embark Passengers");
        
        try{
            embarkPassengersThread.join();
            srcThread.join();
        }catch(InterruptedException ex){}
        
        // Wait until the refuelling status becomes full
        while(!airplane.fuelFull){
            try{
                Thread.sleep(100);
            }
            catch(InterruptedException ex){ex.printStackTrace();}
        }
    }
    
    public void setRefuelTruck(RefuelTruck refuelTruck){
        this.refuelTruck = refuelTruck;
    }
}
