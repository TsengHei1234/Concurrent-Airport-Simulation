package airportccpassignment;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import static airportccpassignment.AirportCCPAssignment.runway;

public class AirTrafficControl implements Runnable {
    private GateManager gateManager;
    public static Semaphore airportGroundSemaphore = new Semaphore(3); // Controls max 3 planes on the ground
    public volatile boolean operating = true;
    public boolean airplaneDocked = false;
    public Gate gateForPlane = null;
    
    
    public AirTrafficControl(GateManager gm){
        this.gateManager = gm;
    }
    
    public void run(){
        while (operating){
            while(!AirportCCPAssignment.emergencyRequestQueue.isEmpty()){ // Handle emergency landing requests
                Airplane requestEmergencyPlane = AirportCCPAssignment.emergencyRequestQueue.poll();
                if (requestEmergencyPlane != null){
                    AirportCCPAssignment.emergencyQueue.add(requestEmergencyPlane);
                    System.out.println("|" + Thread.currentThread().getName() + "|: Emergency landing request accepted for " + requestEmergencyPlane.getName());
                }
            }
            while(!AirportCCPAssignment.landingRequestQueue.isEmpty()){ // Handle normal landing requests
                Airplane requestLandingPlane = AirportCCPAssignment.landingRequestQueue.poll();
                if (requestLandingPlane != null){
                    // Check if permits are available after considering waiting queues
                    if (airportGroundSemaphore.availablePermits() - AirportCCPAssignment.emergencyQueue.size() - AirportCCPAssignment.landingQueue.size() > 0){
                        System.out.println("|" + Thread.currentThread().getName() + "|: Landing request accepted for " + requestLandingPlane.getName());
                    }
                    else{
                        System.out.println("|" + Thread.currentThread().getName() + "|: Airport full. Landing request rejected for " + requestLandingPlane.getName());
                    }
                    AirportCCPAssignment.landingQueue.add(requestLandingPlane);
                }
            }
            while(!AirportCCPAssignment.takeoffRequestQueue.isEmpty()){ // Handle take-off requests
                Airplane requestTakeOffPlane = AirportCCPAssignment.takeoffRequestQueue.poll();
                if (requestTakeOffPlane != null){
                    AirportCCPAssignment.takeOffQueue.add(requestTakeOffPlane);
                    System.out.println("|" + Thread.currentThread().getName() + "|: Take off request accepted for " + requestTakeOffPlane.getName());
                }
            }
            // Synchronize access to the runway before processing any landing or take-off
            synchronized(runway){
                while (!runway.getRunwayAvailability()){
                    try{
                        runway.wait();
                    }
                    catch(InterruptedException ex){ex.printStackTrace();}
                }
            }
            Airplane landingEmergencyAirplane;
            Airplane landingAirplane;
            Airplane takeOffAirplane;
            Gate availableGate;
            // Priorities Emergency landing if airport not over capacity and other queues are empty
            if (airportGroundSemaphore.availablePermits() >= 1 && !AirportCCPAssignment.emergencyQueue.isEmpty() && AirportCCPAssignment.emergencyRequestQueue.isEmpty() && AirportCCPAssignment.landingRequestQueue.isEmpty() && AirportCCPAssignment.takeoffRequestQueue.isEmpty()){
                try{
                    airportGroundSemaphore.acquire();
                    landingEmergencyAirplane = AirportCCPAssignment.emergencyQueue.poll();
                    System.out.println("|" + Thread.currentThread().getName() + "|: Emergency Landing permission granted to " + landingEmergencyAirplane.getName());
                    availableGate = gateManager.requestGate();
                    landingEmergencyAirplane.assignedGate = availableGate;
                    System.out.println("|" + Thread.currentThread().getName() + "|: " + availableGate.toString() + " Emergency Landing assigned for " + landingEmergencyAirplane.getName() + "!");
                    runway.occupiedRunway();
                    landingEmergencyAirplane.landingAvailability = true; // give the particular airplane to land!!
                }
                catch(InterruptedException ex){ex.printStackTrace();}
            }
            // Normal landing
            else if (!AirportCCPAssignment.landingQueue.isEmpty() && airportGroundSemaphore.availablePermits() >= 1 && AirportCCPAssignment.emergencyRequestQueue.isEmpty() && AirportCCPAssignment.landingRequestQueue.isEmpty() && AirportCCPAssignment.takeoffRequestQueue.isEmpty()){
                try{
                    airportGroundSemaphore.acquire();
                    landingAirplane = AirportCCPAssignment.landingQueue.poll();
                    System.out.println("|" + Thread.currentThread().getName() + "|: Landing permission granted to " + landingAirplane.getName());
                    availableGate = gateManager.requestGate();
                    landingAirplane.assignedGate = availableGate;
                    System.out.println("|" + Thread.currentThread().getName() + "|: " + availableGate.toString() + " assigned for " + landingAirplane.getName() + "!");
                    runway.occupiedRunway();
                    landingAirplane.landingAvailability = true; // give the particular airplane to land!!
                }
                catch(InterruptedException ex){ex.printStackTrace();}
            }
            // Take off
            else if (!AirportCCPAssignment.takeOffQueue.isEmpty() && AirportCCPAssignment.emergencyRequestQueue.isEmpty() && AirportCCPAssignment.landingRequestQueue.isEmpty() && AirportCCPAssignment.takeoffRequestQueue.isEmpty()){
                try{
                    runway.occupiedRunway();
                    takeOffAirplane = AirportCCPAssignment.takeOffQueue.poll();
                    System.out.println("|" + Thread.currentThread().getName() + "|: Take Off permmission granted to " + takeOffAirplane.getName());
                    takeOffAirplane.takeOffAvailability = true;
                }
                catch(InterruptedException ex){ex.printStackTrace();}
            }
        }      
    }
    
     // Stop ATC thread and print final statistics
    public void terminateATC(){
        System.out.println("\n-----Sanity Check & Statistics-----");
        boolean gatesEmpty = true;
        ArrayList<Boolean> gateAvailability = gateManager.getGateAvailability();
        ArrayList<Gate> gateList = gateManager.getGateList();
        for (int i = 0; i < gateAvailability.size(); i++){
            if (!gateAvailability.get(i)){
                System.out.println(Thread.currentThread().getName() + ": " + gateList.get(i).toString() + " is still being used!");
                gatesEmpty = false;
            }
        }
        if (gatesEmpty){
            System.out.println(Thread.currentThread().getName() + ": All gates are empty.");
        }
        
        if (!AirportCCPAssignment.planesWaitingTime.isEmpty()){
            long shortestWaitingTime = AirportCCPAssignment.planesWaitingTime.stream().mapToLong(Long::longValue).min().getAsLong();
            long longestWaitingTime = AirportCCPAssignment.planesWaitingTime.stream().mapToLong(Long::longValue).max().getAsLong();
            double averageWaitingTime = AirportCCPAssignment.planesWaitingTime.stream().mapToLong(Long::longValue).average().getAsDouble();
            
            System.out.println("Total Planes served: " + AirportCCPAssignment.planesServed);
            System.out.println("Total passengers boarded: " + AirportCCPAssignment.totalPassengersBoarded);
            System.out.println("Longest waiting time (seconds): " + ((double)longestWaitingTime/1000));
            System.out.println("Shortest waiting time (seconds): " + ((double)shortestWaitingTime/1000));
            System.out.println("Average waiting time (seconds): " + String.format("%.3f", averageWaitingTime / 1000));
        }
        operating = false;
    }
}
