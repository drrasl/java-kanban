import tasks.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        taskManager.setTask(new Task("task1", "descr1"));
        taskManager.setTask(new Task("task2", "descr2"));

        Epic epic1 = new Epic ("epic1", "descr3");
        taskManager.setEpic(epic1);

        SubTask subTask1 = new SubTask("subtask1", "descr4", taskManager.getEpicId(epic1));
        SubTask subTask2 = new SubTask("subtask2", "descr5", taskManager.getEpicId(epic1));

        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);
        epic1.setSubTask(subTask1);
        epic1.setSubTask(subTask2);

        Epic epic2 = new Epic ("epic2", "descr6");
        taskManager.setEpic(epic2);
        SubTask subTask4 = new SubTask("subtask3", "descr7", taskManager.getEpicId(epic2));

        taskManager.setSubTask(subTask4);
        epic2.setSubTask(subTask4);

        //Распечатываем списки
        // Это как void метод
        taskManager.getAllTasks();
        taskManager.getAllSubTasks();
        taskManager.getAllEpics();

        // Это как return метод
        System.out.println(taskManager.getTaskAsMap());
        System.out.println(taskManager.getSubTaskAsMap());
        System.out.println(taskManager.getEpicAsMap());

        System.out.println();
        //Меняем статусы
        taskManager.getTask(1).setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.getTask(2).setStatus(StatusOfTask.DONE);

        subTask1.setStatus(StatusOfTask.IN_PROGRESS);
        subTask2.setStatus(StatusOfTask.DONE);

        subTask4.setStatus(StatusOfTask.DONE);

        //Проверяем статусы 2мя методами

        taskManager.getAllTasks();
        taskManager.getAllSubTasks();
        taskManager.getAllEpics();

        System.out.println(taskManager.getTaskAsMap());
        System.out.println(taskManager.getSubTaskAsMap());
        System.out.println(taskManager.getEpicAsMap());

        System.out.println();
        //Удаляем одну из задач и один из эпиков

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(3);

        //Проверяем удаление 2мя методами

        taskManager.getAllTasks();
        taskManager.getAllSubTasks();
        taskManager.getAllEpics();

        System.out.println(taskManager.getTaskAsMap());
        System.out.println(taskManager.getSubTaskAsMap());
        System.out.println(taskManager.getEpicAsMap());
    }
}
