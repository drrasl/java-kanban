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
        SubTask subTask4 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());

        taskManager.setSubTask(subTask4);

        //Распечатываем списки
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());


        System.out.println();
        //Меняем статусы
        task1.setStatus(StatusOfTask.IN_PROGRESS);
        task2.setStatus(StatusOfTask.DONE);

        subTask1.setStatus(StatusOfTask.IN_PROGRESS);
        subTask2.setStatus(StatusOfTask.DONE);

        subTask4.setStatus(StatusOfTask.DONE);

        //Проверяем статусы
        System.out.println("В этот момент статус эпика не меняется, так как методы вызова не работали:");
        System.out.println(epic1.getStatus());
        System.out.println(epic2.getStatus());
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println("Далее статусы должны поменяться:"); //Так и должно быть или нужно переопределять метод
        //setStatus для класса SubTask? Но как связать объект из TaskManager, если он уровнем выше?
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());


        System.out.println(epic1.getStatus());
        System.out.println(epic2.getStatus());
        System.out.println();

        System.out.println("Обновим подзадачу4");
        SubTask subtask55 = new SubTask("subtask555 instead 3", "descr888 instead 7",
                StatusOfTask.NEW, 7, epic2.getId());
        taskManager.updateSubTask(subtask55);

        System.out.println("Обновим подзадачу1, но неверный айди");
        SubTask subtask11 = new SubTask("subtask111 instead 1", "descr111 instead 4",
                StatusOfTask.NEW, 100, epic1.getId());
        taskManager.updateSubTask(subtask11);

        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getSubTaskByEpic(epic1.getId()));
                System.out.println("-------");

        //Удаляем одну из задач и один из эпиков
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubTaskById(subTask4.getId());

        //Проверяем удаление 2мя методами
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
    }
}
