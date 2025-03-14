package logic;

import model.Server;
import model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    public void addTask(List<Server> servers, Task task) {
        int index = getBest(servers);
        servers.get(index).newTask(task);
    }
    public int getBest(List<Server> servers) {
        int index = 0;
        Server bestServer = servers.getFirst();
        for (int i = 1; i < servers.size(); i++) {
            if (!servers.get(i).isClosingTime() && servers.get(i).getTasks().size() < bestServer.getTasks().size()) {
                bestServer = servers.get(i);
                index = i;
            }
        }
        return index;
    }
}
