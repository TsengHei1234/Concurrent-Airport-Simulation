package airportccpassignment;

import java.util.Deque;
import java.util.ArrayDeque;

public class RefuelTruck implements Runnable{
    
    // Queue for planes waiting to be refuelled
    private volatile Deque<Airplane> refuellingTruckRequest = new ArrayDeque<>(); 
    private Boolean operating = true;
    
    public void run(){
        Thread.currentThread().setName("|Refuelling Truck|");
        while(operating){   //will keep looping until it is called to shutdown
            if(!refuellingTruckRequest.isEmpty()){
                try{
                    Airplane currentAirplane = refuellingTruckRequest.pollFirst();
                    System.out.println(Thread.currentThread().getName() + ": Travelling to " + currentAirplane.assignedGate.toString() + ".");
                    Thread.sleep(1500);
                    System.out.println(Thread.currentThread().getName() + ": Refuelling " + currentAirplane.getName() + " on " + currentAirplane.assignedGate.toString() + ".");
                    Thread.sleep(1500);
                    System.out.println(Thread.currentThread().getName() + ": Refuel Complete for " + currentAirplane.getName()+ " on " + currentAirplane.assignedGate.toString() + ".");
                    currentAirplane.setfuelFull(true);
                }catch (InterruptedException ex){ex.printStackTrace();}
            }
        }
    }
    
    public void requestRefuellingTruck(Airplane airplane){
        refuellingTruckRequest.addLast(airplane);
    }
    
    public void shutdown(){
        operating = false;
    }
}

