import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        //Теперь работаем через FileBackedTaskManager
        FileBackedTaskManager fileManager = Managers.getDefaultWithBackup();


        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);

        System.out.println("--------------------Проверяем, что задача удаляет свою версию перед добавлением себя");
        fileManager.setTask(task1);
        fileManager.setTask(task2);
        fileManager.getTask(task1.getId());
        fileManager.getTask(task1.getId());

        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Добавляем таск 2");
        fileManager.getTask(task2.getId());
        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Меняем статус у таск 1");
        fileManager.getTask(task1.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        fileManager.updateTask(task1);
        fileManager.getTask(task1.getId());
        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Добавляем и выводим в историю остальные таски по очереди");

        Epic epic1 = new Epic("epic1", "descr3");
        fileManager.setEpic(epic1);
        fileManager.getEpic(epic1.getId());

        SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId());
        SubTask subTask10 = new SubTask("subtask10", "descr10", StatusOfTask.NEW, epic1.getId());

        fileManager.setSubTask(subTask1);
        fileManager.setSubTask(subTask2);
        fileManager.setSubTask(subTask10);

        fileManager.getSubTask(subTask1.getId());
        fileManager.getSubTask(subTask2.getId());
        fileManager.getSubTask(subTask10.getId());

        Epic epic2 = new Epic("epic2", "descr6");
        fileManager.setEpic(epic2);
        fileManager.getEpic(epic2.getId());
        SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());

        fileManager.setSubTask(subTask3);
        fileManager.getSubTask(subTask3.getId());

        Task task7 = new Task("task7", "descr12", StatusOfTask.NEW);
        fileManager.setTask(task7);
        fileManager.getTask(task7.getId());

        Task task8 = new Task("task8", "descr13", StatusOfTask.NEW);
        fileManager.setTask(task8);
        fileManager.getTask(task8.getId());

        Task task9 = new Task("task9", "descr14", StatusOfTask.NEW);
        fileManager.setTask(task9);
        fileManager.getTask(task9.getId());

        System.out.println("---------------------------------------------------------История №1:");

        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("--------------------Обновляем таск 7 (id=9) [должен переместиться в конец]");
        fileManager.getTask(task7.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        fileManager.updateTask(task7);
        fileManager.getTask(task7.getId());
        System.out.println("---------------------------------------------------------История №2:");

        for (Task task : fileManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("---------------------------------------------------------Распечатываем в стиле Яндекс-Практикума:");
        printAllTasks(fileManager);

        System.out.println("-------------------Удаляем 1 эпик в том числе в истории. Удалится и Субтаск:");
        fileManager.deleteEpicById(epic1.getId());
        printAllTasks(fileManager);

        System.out.println("----------------------");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Часть 2 >>>> Выгрузим новую версию");
        System.out.println("----------------------");

        Path file = Paths.get("src//resources//backup.csv");
        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file.toFile());

        System.out.println("-------------------Добавим новый таск (id должен быть = 12, а не 1) и выведем его в историю");

        Task task11 = new Task("task11", "descr15", StatusOfTask.NEW);
        fileManager2.setTask(task11);
        fileManager2.getTask(task11.getId());

        printAllTasks(fileManager2);

    }

    private static void printAllTasks(TaskManager manager) {
        //HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubTaskByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");

        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
    }
}
