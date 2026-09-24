package airportccpassignment;

import java.util.Random;

public class Passengers implements Runnable{
    
    Random random = new Random();
    private Airplane airplane;
    private Gate gate;
    private Integer totalPassengers;
    private boolean isEmbark;
    
    // Constructor for disembarkation process
    public Passengers(Airplane airplane, int totalPassengers){
        this.airplane = airplane;
        this.totalPassengers = totalPassengers;
        isEmbark = false;
    }
    
    // Constructor for embarkation process
    public Passengers(Airplane airplane, Gate gate){
        this.gate = gate;
        this.airplane = airplane;
        totalPassengers = random.nextInt(50) + 1;
        isEmbark = true;
    }
    
    public void run(){
        if (!isEmbark){
            // Handle disembark passengers from airplane
            Thread.currentThread().setName("|"+airplane.getName() + "'s Disembark Passengers|");
            System.out.println(Thread.currentThread().getName() + ": " + airplane.totalPassengers + " Passengers disembarking out of " + airplane.getName());
            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException ex){ex.printStackTrace();}
            System.out.println(Thread.currentThread().getName() + ": " + airplane.totalPassengers + " Passengers successfully disembarked out of " + airplane.getName());
        }
        else{
            // Handle embark passengers from gate
            Thread.currentThread().setName("|"+airplane.getName() + "'s Embark Passengers|");
            System.out.println(Thread.currentThread().getName() + ": " + totalPassengers + " Passengers embarking into " + airplane.getName());
            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException ex){ex.printStackTrace();}
            System.out.println(Thread.currentThread().getName() + ": " + totalPassengers + " Passengers successfully embarking into " + airplane.getName());
            airplane.totalPassengers = totalPassengers;
            AirportCCPAssignment.totalPassengersBoarded += totalPassengers; // Update totalPassengersBoarded for final statistics
        }
        
    }
}

