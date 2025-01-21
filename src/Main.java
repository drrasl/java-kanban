import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();



/*        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();




        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);

        taskManager.setTask(task1);
        taskManager.setTask(task2);

        Epic epic1 = new Epic ("epic1", "descr3");
        taskManager.setEpic(epic1);

        SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId());

        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);

        Epic epic2 = new Epic ("epic2", "descr6");
        taskManager.setEpic(epic2);
        SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());

        taskManager.setSubTask(subTask3);

        //Распечатываем списки
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("---------------Меняем статусы");
        //Меняем статусы

        //task1.setStatus(StatusOfTask.IN_PROGRESS); обращаемся через таск-менеджер.
        taskManager.getTask(task1.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        taskManager.updateTask(task1);

        taskManager.getTask(task2.getId()).setStatus(StatusOfTask.DONE);
        taskManager.updateTask(task2);

        taskManager.getSubTask(subTask1.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);

        taskManager.getSubTask(subTask2.getId()).setStatus(StatusOfTask.DONE);
        taskManager.updateSubTask(subTask2);

        taskManager.getSubTask(subTask3.getId()).setStatus(StatusOfTask.DONE);
        taskManager.updateSubTask(subTask3);
        //Проверяем статусы
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());


        System.out.println("----------Обновим подзадачу3 через конструктор 2");
        SubTask subtask33 = new SubTask("subtask333 instead 3", "descr777 instead 7",
                StatusOfTask.NEW, subTask3.getId(), epic2.getId());
        taskManager.updateSubTask(subtask33);
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("----------Обновим подзадачу1 через конструктор 2, но неверный айди");
        SubTask subtask11 = new SubTask("subtask111 instead 1", "descr111 instead 4",
                StatusOfTask.NEW, 100, epic1.getId());
        taskManager.updateSubTask(subtask11);
        System.out.println(taskManager.getSubTaskByEpic(epic1.getId()));
        System.out.println(taskManager.getAllEpics());

        System.out.println("HISTORY------------------------");
        System.out.println(taskManager.getHistory());

        System.out.println("-------Удаление");
        //Удаляем одну из задач и один из эпиков
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubTaskById(subTask3.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("----");
        System.out.println(taskManager.getSubTaskByEpic(epic2.getId()));
        taskManager.deleteEpicById(epic2.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());

        Task task3 = new Task("task3", "descr8", StatusOfTask.NEW);
        taskManager.setTask(task3);
        taskManager.getTask(task3.getId());

        Task task4 = new Task("task4", "descr9", StatusOfTask.NEW);
        taskManager.setTask(task4);
        taskManager.getTask(task4.getId());

        Task task5 = new Task("task5", "descr10", StatusOfTask.NEW);
        taskManager.setTask(task5);
        taskManager.getTask(task5.getId());

        Task task6 = new Task("task6", "descr11", StatusOfTask.NEW);
        taskManager.setTask(task6);
        taskManager.getTask(task6.getId());

        Task task7 = new Task("task7", "descr12", StatusOfTask.NEW);
        taskManager.setTask(task7);
        taskManager.getTask(task7.getId());

        Task task8 = new Task("task8", "descr13", StatusOfTask.NEW);
        taskManager.setTask(task8);
        taskManager.getTask(task8.getId());

        Task task9 = new Task("task9", "descr14", StatusOfTask.NEW);
        taskManager.setTask(task9);
        taskManager.getTask(task9.getId());

        System.out.println("HISTORY------------------------");
        System.out.println(taskManager.getHistory());

        Task task10 = new Task("task10", "descr15", StatusOfTask.NEW);
        taskManager.setTask(task10);
        taskManager.getTask(task10.getId());

        System.out.println("HISTORY------------------------");
        System.out.println(taskManager.getHistory());
*/
        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);

        taskManager.setTask(task1);
        taskManager.setTask(task2);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        Epic epic1 = new Epic ("epic1", "descr3");
        taskManager.setEpic(epic1);
        taskManager.getEpic(epic1.getId());

        SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId());

        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);

        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());


        Epic epic2 = new Epic ("epic2", "descr6");
        taskManager.setEpic(epic2);
        taskManager.getEpic(epic2.getId());
        SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());

        taskManager.setSubTask(subTask3);
        taskManager.getSubTask(subTask3.getId());

//        Task task3 = new Task("task3", "descr8", StatusOfTask.NEW);
//        taskManager.setTask(task3);
//        taskManager.getTask(task3.getId());
//
//        Task task4 = new Task("task4", "descr9", StatusOfTask.NEW);
//        taskManager.setTask(task4);
//        taskManager.getTask(task4.getId());
//
//        Task task5 = new Task("task5", "descr10", StatusOfTask.NEW);
//        taskManager.setTask(task5);
//        taskManager.getTask(task5.getId());
//
//        Task task6 = new Task("task6", "descr11", StatusOfTask.NEW);
//        taskManager.setTask(task6);
//        taskManager.getTask(task6.getId());

        Task task7 = new Task("task7", "descr12", StatusOfTask.NEW);
        taskManager.setTask(task7);
        taskManager.getTask(task7.getId());

        Task task8 = new Task("task8", "descr13", StatusOfTask.NEW);
        taskManager.setTask(task8);
        taskManager.getTask(task8.getId());

        Task task9 = new Task("task9", "descr14", StatusOfTask.NEW);
        taskManager.setTask(task9);
        taskManager.getTask(task9.getId());



        taskManager.getTask(task7.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        taskManager.updateTask(task7);
        taskManager.getTask(task7.getId());


        printAllTasks(taskManager);

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

            for (Task task : manager.getHistory()) {
                System.out.println(task);
            }
    }


}
