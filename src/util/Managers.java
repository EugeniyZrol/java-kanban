package util;

import manager.*;

public class Managers {

    public static TaskManager getDefault(){
        return new inMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
