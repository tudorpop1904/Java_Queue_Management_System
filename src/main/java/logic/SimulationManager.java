package logic;

import GUI.SimulationFrame;
import model.Server;
import model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable {
    private int maxSimulationTime, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, nrServers, nrClients;
    private Scheduler scheduler;
    private Thread mainThread;
    private SelectionPolicy selectionPolicy;
    private List<Task> tasks;
    private FileWriter file;

    public SimulationManager(int N, int Q, int maxSimulationTime, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy policy) {
        nrClients = N;
        nrServers = Q;
        this.maxSimulationTime = maxSimulationTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        selectionPolicy = policy;
    }
    public void simulationSetUp() {
        try {
            file = new FileWriter("log.txt");
        }
        catch (IOException e) {
            System.out.println("Upon initializing the File Writer, the program encountered an IO Exception!");
        }
        scheduler = new Scheduler(nrServers, nrClients);
        scheduler.changeStrategy(selectionPolicy);
        generateRandomTasks();
        mainThread = new Thread(this);
        Server.resetSimulation();
    }

    public void startSim(){
//        System.out.println("simulation started");
        mainThread.start();
    }

    public void generateRandomTasks() {
        Random r = new Random();
        tasks = new ArrayList<>(nrClients);
        for (int i = 0; i < nrClients; i++) {
            int arrTime = r.nextInt(minArrivalTime, maxArrivalTime);
            int servTime = r.nextInt(minServiceTime, maxServiceTime);
            tasks.add(new Task(i + 1, arrTime, servTime));
        }
        Collections.sort(tasks);
    }

    public void log(String s) {
        if (file == null) return;
        try {
            file.write(s + "\n");
        }
        catch (IOException e) {
            System.out.println("Failed to append to log.txt!");
        }
    }
    @Override
    public void run() {
        double avgWait = 0.0, avgService = 0.0;
        int peakHour = 0, peakTasks = 0;
        while (Server.whatTimeIsIt() < maxSimulationTime && (!tasks.isEmpty() || scheduler.serversAreWorking())) {
            while (!tasks.isEmpty() && tasks.getFirst().getArrivalTime() <= Server.whatTimeIsIt()) {
                scheduler.dispatchTask(tasks.removeFirst());
            }

            log(toString());

            try {
                file.write("Time " + Server.whatTimeIsIt() + "\n");
                file.write("Waiting clients: " + tasks + "\n");
                file.write(scheduler.toString() + "\n");
            }
            catch (IOException e) {
                System.out.println("Failed to append to log.txt!");
            }
            if (scheduler.howManyTasks() > peakTasks) {
                peakHour = Server.whatTimeIsIt();
                peakTasks = scheduler.howManyTasks();
            }

            for (Server server : scheduler.getServers()) {
                avgWait += server.getWaitingPeriod().get();
            }
            //Server.currentTime.incrementAndGet();
            if (tasks.isEmpty())
                scheduler.clockOutEarly();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Server.newTime();
        }
        if (scheduler.serversAreWorking()) {
            log("We're Closed!");
            scheduler.clockOut();
        }
        avgWait /= nrClients;
        log("Average Waiting Time: " + avgWait);
        avgService = (double)scheduler.getCurrentServiceTime() / nrClients;
        log("Average Service Time: " + avgService);
        log("Peak Hour: " + peakHour);
        log("Number of processed tasks during the peak hour: " + peakTasks);
        try {
            file.close();
        }
        catch (IOException e) {
            System.out.println("An issue occurred while attempting to close log.txt!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time ").append(Server.whatTimeIsIt()).append('\n');
        synchronized (tasks){
            sb.append("Waiting clients: ").append(tasks).append('\n');
        }
        sb.append(scheduler);

        return sb.toString();
    }

    public void setMaxSimulationTime(int maxSimulationTime) {
        this.maxSimulationTime = maxSimulationTime;
    }

    public void setMinArrivalTime(int minArrivalTime) {
        this.minArrivalTime = minArrivalTime;
    }

    public void setMaxArrivalTime(int maxArrivalTime) {
        this.maxArrivalTime = maxArrivalTime;
    }

    public void setMinServiceTime(int minServiceTime) {
        this.minServiceTime = minServiceTime;
    }

    public void setMaxServiceTime(int maxServiceTime) {
        this.maxServiceTime = maxServiceTime;
    }

    public void setNrServers(int nrServers) {
        this.nrServers = nrServers;
    }

    public void setNrClients(int nrClients) {
        this.nrClients = nrClients;
    }

    public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
        this.selectionPolicy = selectionPolicy;
    }

    public static void main(String[] args) {
//        SimulationManager manager = new SimulationManager(10, 4, 30, 2, 4, 1, 5, SelectionPolicy.TIMESTRATEGY);
        new SimulationFrame();

    }
}
