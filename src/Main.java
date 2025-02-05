import Task.*;
import Manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создаем две задачи
        Task firstTask = new Task("Первая задача", "Описание первой задачи", Status.NEW);
        Task secondTask = new Task("Вторая задача", "Описание второй задачи", Status.NEW);
        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);

        // Создаем эпик и две подзадачи
        Epic firstEpic = new Epic("Первый эпик",
                "Описание первого эпика", Status.NEW);
        taskManager.addEpic(firstEpic);

        Subtask firstSubtask = new Subtask("Первая подзадача первого эпика",
                "Описание первой подзадачи", Status.NEW, firstEpic.getTaskId());
        taskManager.addSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask("Вторая подзадача первого эпика",
                "Описание второй подзадачи", Status.NEW, firstEpic.getTaskId());
        taskManager.addSubtask(secondSubtask);
        // второй эпик и подзадача
        Epic secondEpic = new Epic("Второй эпик",
                "Описание второго эпика", Status.NEW);
        taskManager.addEpic(secondEpic);

        Subtask thirdSubtask = new Subtask("Подзадача второго эпика",
                "Описание подзадачи второго эпика", Status.NEW, secondEpic.getTaskId());
        taskManager.addSubtask(thirdSubtask);
       taskManager.printAllTasks();

        System.out.println("Меняем статусы у простой задачи и подзадачи первого эпика");
        firstTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(firstTask);
        secondSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(secondSubtask);
        taskManager.printAllTasks();

        System.out.println("Меняем статус у подзадачи второго эпика");
        thirdSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(thirdSubtask);
        taskManager.printAllTasks();
//
        System.out.println("Меняем статус эпика");
        firstEpic.setStatus(Status.DONE);
        taskManager.updateEpic(firstEpic);
        taskManager.printAllTasks();
//        System.out.println("Получаем задачу по ID");
//        Task task = taskManager.getTask(secondTask.getTaskId());
//        System.out.println(task);

//          System.out.println("вывод списка подклассов эпика");
//          System.out.println(taskManager.getEpicSubtasks(firstEpic.getTaskId()));
//
//        System.out.println("Удаляем подзадачу по ID");
//        taskManager.deleteTask(secondSubtask.getTaskId());
//        taskManager.printAllTasks();
//
//        System.out.println("Удаляем эпик по ID");
//        taskManager.deleteTask(firstEpic.getTaskId());
//        taskManager.printAllTasks();
//
//        System.out.println("Удаление всех задач...");
//        taskManager.clearTask();
//        taskManager.printAllTasks();

    }
}
