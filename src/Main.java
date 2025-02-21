
import manager.TaskManager;
import manager.InMemoryTaskManager;
import task.*;
import util.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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


        //taskManager.getTaskId(firstTask.getTaskId());
        Task newFirstTask = new Task("Новая первая задача", "Новое описание первой задачи", Status.DONE);

        taskManager.updateTask(newFirstTask);
        newFirstTask.setTaskId(1);
        taskManager.getTaskId(newFirstTask.getTaskId());
        taskManager.getEpicId(firstEpic.getTaskId());
        firstEpic.setName("Очень важная задача");
        taskManager.updateEpic(firstEpic);
        taskManager.getEpicId(firstEpic.getTaskId());
        taskManager.getEpicId(secondEpic.getTaskId());
        taskManager.getEpicId(firstEpic.getTaskId());
        firstSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(firstSubtask);
        printAllTasks((InMemoryTaskManager) taskManager);

//        System.out.println("Меняем статусы у простой задачи и подзадачи первого эпика");
//        firstTask.setStatus(Status.IN_PROGRESS);
//        taskManager.updateTask(firstTask);
//        secondSubtask.setStatus(Status.IN_PROGRESS);
//        taskManager.updateSubtask(secondSubtask);
//        printAllTasks((inMemoryTaskManager) taskManager);
//
//
//        System.out.println("Меняем статус у подзадачи второго эпика");
//        thirdSubtask.setStatus(Status.DONE);
//        taskManager.updateSubtask(thirdSubtask);
//        printAllTasks((inMemoryTaskManager) taskManager);
//
//        System.out.println("Меняем статус эпика");
//        firstEpic.setStatus(Status.DONE);
//        taskManager.updateEpic(firstEpic);
//        printAllTasks((inMemoryTaskManager) taskManager);
//
//        System.out.println("Получаем задачу по ID");
//        Task task = taskManager.getTaskId(secondTask.getTaskId());
//        System.out.println(task);
//
//        System.out.println("вывод списка подклассов эпика");
//        System.out.println(taskManager.getEpicSubtasks(firstEpic.getTaskId()));
//
//        System.out.println("Удаляем подзадачу по ID");
//        taskManager.deleteSubtaskId(secondEpic.getTaskId());
//        printAllTasks((inMemoryTaskManager) taskManager);
//
//        System.out.println("Удаляем эпик по ID");
//        taskManager.deleteEpicId(firstEpic.getTaskId());
//        printAllTasks((inMemoryTaskManager) taskManager);
//
//        System.out.println("Список вызванных задач: ");
//        System.out.println(taskManager.getHistory());
//        System.out.println("убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.");
//        secondTask.setStatus(Status.IN_PROGRESS);
//        secondTask.setName("Третья задача");
//        taskManager.updateTask(firstTask);
//        System.out.println(taskManager.getHistory());
//
//        System.out.println("Удаление всех задач...");
//        taskManager.clearTask();
//        taskManager.clearEpic();
//        printAllTasks((inMemoryTaskManager) taskManager);


    }


    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpic()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}

