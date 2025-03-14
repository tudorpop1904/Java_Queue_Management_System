package logic;

import model.Server;
import model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {
    public void addTask(List<Server> servers, Task task) {
        int index = getBest(servers);
        servers.get(index).newTask(task);
    }
    public int getBest(List<Server> servers) {
        int minPeriod = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < servers.size(); i++) {
            if (!servers.get(i).isClosingTime() && servers.get(i).getWaitingPeriod().get() < minPeriod) {
                minPeriod = servers.get(i).getWaitingPeriod().get();
                index = i;
            }
        }
        return index;
    }
}
