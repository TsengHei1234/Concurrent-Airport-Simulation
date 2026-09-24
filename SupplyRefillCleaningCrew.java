    package airportccpassignment;

public class SupplyRefillCleaningCrew implements Runnable{
    
    private Airplane airplane;
    
    // Constructor to assign airplane to be serviced
    public SupplyRefillCleaningCrew (Airplane airplane){
        this.airplane = airplane;
    }
    
    public void run(){
        Thread.currentThread().setName("Supply Refill & Cleaning Crew");
        System.out.println("|" + Thread.currentThread().getName() + "|: Refilling Supply and cleaning for " + airplane.getName());
        try{
            Thread.sleep(1000);
        }
        catch(InterruptedException ex){ex.printStackTrace();}
        System.out.println("|" + Thread.currentThread().getName() + "|: Completed refilling Supply and cleaning for " + airplane.getName());
    }
}
