import tasks.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

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

    }
}
