package logic;

import model.Server;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {
    private boolean open;
    private ArrayList<Server> servers;
    private int maxNoServers, maxTasksPerServer, currentServiceTime;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        open = true;
        this.maxNoServers = maxNoServers;
        servers = new ArrayList<>(maxNoServers);
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            servers.add(server);
            Thread th = new Thread(server);
            th.start();
        }
    }
    public int getCurrentServiceTime() {
        return currentServiceTime;
    }
    public int howManyTasks() {
        int count = 0;
        for (Server server : servers) {
            count += server.getTasks().size();
        }
        return count;
    }
    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if (selectionPolicy == SelectionPolicy.TIMESTRATEGY)
            strategy = new TimeStrategy();
        else if (selectionPolicy == SelectionPolicy.SHORTESTQUEUESTRATEGY)
            strategy = new ShortestQueueStrategy();
    }
    public List<Server> getServers() {
        return servers;
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
        currentServiceTime += task.getServiceTime();
    }

    public void clockOutEarly() {
        for (Server server : servers)
            server.setEarlyClosingTime();
        open = false;
    }

    public void clockOut() {
        for (Server server : servers)
            server.setClosingTime();
        open = false;
    }

    public boolean serversAreWorking() {
        for (Server s : servers) {
            if(!s.getTasks().isEmpty())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        synchronized (servers) {
            for (int i = 0; i < servers.size(); i++) {
                sb.append("Queue ").append(i + 1).append(": ");
                sb.append(servers.get(i)).append('\n');
            }
        }
        return sb.toString();
    }
}
