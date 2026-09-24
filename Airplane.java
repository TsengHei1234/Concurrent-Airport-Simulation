package airportccpassignment;

import static airportccpassignment.AirTrafficControl.airportGroundSemaphore;
import static airportccpassignment.AirportCCPAssignment.runway;
import java.util.Random;

public class Airplane implements Runnable{
    Random random = new Random();
    private AirTrafficControl atc;
    private int planeID;
    private GateManager gm;
    public int totalPassengers;
    public boolean landingAvailability;
    public boolean takeOffAvailability;
    public Gate assignedGate;
    public boolean fuelFull = false;
    public boolean isEmergency;
    private long permissionTime;
    private long requestToLandTime;
    
    public Airplane(AirTrafficControl atc, int id, GateManager gm, boolean isEmergency){
        this.atc = atc;
        this.planeID = id;
        this.gm = gm;
        totalPassengers = random.nextInt(50) + 1; // Random passenger count between 1 to 50
        this.isEmergency = isEmergency;
    }
    
    public String getName(){
        return "Plane " + planeID;
    }
    
    public void run(){
        // Create thread for disembarking passengers where they come from airplane, not gate itself
        Passengers disembarkPassengersRunnable = new Passengers(this, totalPassengers);
        Thread disembarkPassengersThread = new Thread(disembarkPassengersRunnable);
        
        // Mark landing request time
        requestToLandTime = System.currentTimeMillis(); 
        
        if (isEmergency){
            planeEmergencyLandingRequest(this);
        }
        else{
            planeLandingRequest(this);
        }
        
        // Wait until ATC grants landing permission
        while (!landingAvailability){
            try{
                Thread.sleep(500);
            }
            catch(InterruptedException ex){ex.printStackTrace();}
        }
        
        // Record landing permission time and calculate wait duration
        permissionTime = System.currentTimeMillis();
        long waitDuration = permissionTime - requestToLandTime;
        synchronized(AirportCCPAssignment.planesWaitingTime){
            AirportCCPAssignment.planesWaitingTime.add(waitDuration);
        }
        
        planeLand();
        
        planeCoastToAssignedGate();
        
        assignedGate.gateProcess(this, assignedGate, disembarkPassengersThread);
        
        planeTakeOffRequest(this);
        
        while (!takeOffAvailability){
            try{
                Thread.sleep(500);
            }
            catch(InterruptedException ex){ex.printStackTrace();}
        }
        
        planeTakeOff();
    }
    
    public synchronized void planeEmergencyLandingRequest(Airplane airplane){
        System.out.println(Thread.currentThread().getName() + ": Requesting for Emergency Landing!");
        AirportCCPAssignment.emergencyRequestQueue.addLast(this);
    }
    
    public synchronized void planeLandingRequest(Airplane airplane){
        System.out.println(Thread.currentThread().getName() + ": Requesting for Landing!");
        AirportCCPAssignment.landingRequestQueue.addLast(this);
    }
    
    public synchronized void planeLand(){
        System.out.println(Thread.currentThread().getName() + ": Landing Airplane...");
        try{
            Thread.sleep(2000);
        }
        catch(InterruptedException ex){};
        System.out.println(Thread.currentThread().getName() + ": Landed Successfully!");
    }
    
    public synchronized void planeTakeOffRequest(Airplane airplane){
        System.out.println(Thread.currentThread().getName() + ": Requesting for Take Off!");
        AirportCCPAssignment.takeoffRequestQueue.addLast(this);
    }
    
    public synchronized void planeTakeOff(){     
        try{
            System.out.println(Thread.currentThread().getName() + ": Undocked from Gate.");
            gm.releaseGate(assignedGate);
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + ": Coasting to runway...");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + ": Taking off Airplane...");
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + ": Take off Successfully!");
            runway.emptyRunway();
            takeOffAvailability = false;
            airportGroundSemaphore.release();
            AirportCCPAssignment.planesServed++;
        }
        catch(InterruptedException ex){};
    }
    
    public synchronized void planeCoastToAssignedGate(){
        System.out.println(Thread.currentThread().getName() + ": Coasting to Assigned " + assignedGate.toString());
        try{
            Thread.sleep(1000);
        }
        catch(InterruptedException ex){ex.printStackTrace();}
        System.out.println(Thread.currentThread().getName() + ": Docked to Assigned " + assignedGate.toString() + " Successfully!");
        atc.airplaneDocked = true;
        runway.emptyRunway();
        landingAvailability = false;
    }
    
    public void setfuelFull(boolean status){
        this.fuelFull = status;
    }
}
