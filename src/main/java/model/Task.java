package model;
import java.util.concurrent.atomic.AtomicInteger;
public class Task implements Comparable<Task> {
    private int ID, arrivalTime;
    private AtomicInteger serviceTime;
    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = new AtomicInteger(serviceTime);
    }

    public void decrementServiceTime(){
        serviceTime.decrementAndGet();
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getServiceTime() {
        return serviceTime.get();
    }
    @Override
    public int compareTo(Task task) {
        return Integer.compare(this.arrivalTime, task.arrivalTime);
    }
    @Override
    public String toString() {
        return "(" + ID + ", " + arrivalTime + ", " + serviceTime.get() + ")";
    }
}