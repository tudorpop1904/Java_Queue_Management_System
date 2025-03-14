package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private boolean closingTime = false, earlyClosingTime = false;
    public static final AtomicInteger currentTime = new AtomicInteger(0);
    private int selfCurrentTime = -1;
    public Server(int maxTasksPerServer) {
        tasks = new LinkedBlockingQueue<>(maxTasksPerServer);
        waitingPeriod = new AtomicInteger(0);
        selfCurrentTime = currentTime.get();
    }

    public void setEarlyClosingTime() {
        this.earlyClosingTime = true;
    }

    public boolean isClosingTime() {
        return closingTime;
    }
    public static synchronized void resetSimulation() { currentTime.set(0); }
    public static synchronized int whatTimeIsIt() { return currentTime.get(); }
    public static synchronized void newTime() { currentTime.incrementAndGet(); }
    public void setClosingTime() {
        this.closingTime = true;
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void newTask(Task task) {
        synchronized (tasks) {
            tasks.add(task);
            waitingPeriod.addAndGet(task.getServiceTime());
            if (selfCurrentTime < 0)
                selfCurrentTime = task.getArrivalTime();
        }
    }

    @Override
    public void run() { // got my eye on you
        while (!closingTime && (!earlyClosingTime || !tasks.isEmpty())) {
            if(selfCurrentTime >= currentTime.get()){
                Thread.yield();
                continue;
            }
            selfCurrentTime = currentTime.get();
            if (tasks.isEmpty()) {
                Thread.yield();
                continue;
            }
            if (tasks.peek() != null && tasks.peek().getServiceTime() >= 2)
                tasks.peek().decrementServiceTime();
            else tasks.remove();
            selfCurrentTime = currentTime.get();
            waitingPeriod.decrementAndGet();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        if (tasks.isEmpty())
            return "closed";
        StringBuilder builder = new StringBuilder();
        for (Task task : tasks)
            builder.append(task).append(' ');
        return builder.toString();
    }
}