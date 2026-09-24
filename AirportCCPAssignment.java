package airportccpassignment;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

//Main simulation class
public class AirportCCPAssignment {
    // Shared queues for handling plane requests
    public static Deque<Airplane> landingQueue = new ArrayDeque<>();
    public static Deque<Airplane> landingRequestQueue = new ArrayDeque<>();
    public static Deque<Airplane> takeOffQueue = new ArrayDeque<>();
    public static Deque<Airplane> takeoffRequestQueue = new ArrayDeque<>();
    public static Deque<Airplane> emergencyQueue = new ArrayDeque<>();
    public static Deque<Airplane> emergencyRequestQueue = new ArrayDeque<>();
    public static Runway runway = new Runway();
    
    // Statistics tracking
    public static List<Long> planesWaitingTime = new ArrayList<>();
    public static int planesServed = 0;
    public static int totalPassengersBoarded = 0;

    public static void main(String[] args) {
        // Create and start GateManager, RefuelTruck and ATC thread
        GateManager gcRunnable = new GateManager();
        Thread gcThread = new Thread(gcRunnable);
        gcThread.start();
        gcThread.setName("Gate Manager");
        
        RefuelTruck refuelTruckRunnable = new RefuelTruck();
        Thread refuelTruckThread = new Thread(refuelTruckRunnable);
        refuelTruckThread.start();
        
        for (Gate gate : GateManager.gateList){
            gate.setRefuelTruck(refuelTruckRunnable);
        }
        
        Thread mainThread = Thread.currentThread();
        AirTrafficControl atcRunnable = new AirTrafficControl(gcRunnable);
        Thread atcThread = new Thread(atcRunnable);
        atcThread.start();
        atcThread.setName("ATC");
        
        // Create airplanes threads (6 planes total)
        Airplane[] planes = new Airplane[6];
        Thread[] planesThread = new Thread[6];
        int planeID;
        
        for(int i = 0; i < planes.length; i++){
            planeID = i + 1;
            planesThread[i] = new Thread(new Airplane(atcRunnable, planeID, gcRunnable, i == 4)); // Set plane 5 as emergency
            planesThread[i].setName("|Plane " + planeID + "|");
            
            if (i == 4){
                planesThread[i].setPriority(Thread.MAX_PRIORITY); // Give higher thread priority to emergency plane
            }
            else{
                planesThread[i].setPriority(Thread.NORM_PRIORITY);
            }
        }

        // Start each plane thread with 0 to 2 seconds delay between them
        Random random = new Random();
        int j = 0;
        for (Thread planeThread : planesThread){
            planeThread.start();
            try{
                mainThread.sleep((int)(random.nextInt(3)*1000));
            }
            catch(InterruptedException ex){ex.printStackTrace();}
        }
        
        for (Thread planeThread : planesThread){
            try{
                planeThread.join();
            }
            catch(InterruptedException ex){ex.printStackTrace();}
        }
        
        // Shut down remaining threads
        gcRunnable.terminateGateManager();
        atcRunnable.terminateATC();
        refuelTruckRunnable.shutdown();
    }
}
