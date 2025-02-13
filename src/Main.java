import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);

        System.out.println("--------------------Проверяем, что задача удаляет свою версию перед добавлением себя");
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());

        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Добавляем таск 2");
        taskManager.getTask(task2.getId());
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Меняем статус у таск 1");
        taskManager.getTask(task1.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        taskManager.updateTask(task1);
        taskManager.getTask(task1.getId());
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("--------------------Добавляем и выводим в историю остальные таски по очереди");

        Epic epic1 = new Epic ("epic1", "descr3");
        taskManager.setEpic(epic1);
        taskManager.getEpic(epic1.getId());

        SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId());
        SubTask subTask10 = new SubTask("subtask10", "descr10", StatusOfTask.NEW, epic1.getId());

        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);
        taskManager.setSubTask(subTask10);

        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask10.getId());

        Epic epic2 = new Epic ("epic2", "descr6");
        taskManager.setEpic(epic2);
        taskManager.getEpic(epic2.getId());
        SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());

        taskManager.setSubTask(subTask3);
        taskManager.getSubTask(subTask3.getId());

        Task task7 = new Task("task7", "descr12", StatusOfTask.NEW);
        taskManager.setTask(task7);
        taskManager.getTask(task7.getId());

        Task task8 = new Task("task8", "descr13", StatusOfTask.NEW);
        taskManager.setTask(task8);
        taskManager.getTask(task8.getId());

        Task task9 = new Task("task9", "descr14", StatusOfTask.NEW);
        taskManager.setTask(task9);
        taskManager.getTask(task9.getId());

        System.out.println("---------------------------------------------------------История №1:");

        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("--------------------Обновляем таск 7 (id=8) [должен переместиться в конец]");
        taskManager.getTask(task7.getId()).setStatus(StatusOfTask.IN_PROGRESS);
        //после смены статуса требуется обновить таск-менеджер
        taskManager.updateTask(task7);
        taskManager.getTask(task7.getId());
        System.out.println("---------------------------------------------------------История №2:");

        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("---------------------------------------------------------Распечатываем в стиле Яндекс-Практикума:");
        printAllTasks(taskManager);

        System.out.println("-------------------Удаляем все таски в том числе в истории:");
        taskManager.removeAllTasks();
        printAllTasks(taskManager);

        System.out.println("-------------------Удаляем 1 эпик в том числе в истории. Удалится и Субтаск:");
        taskManager.deleteEpicById(epic1.getId());
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

            for (Task task : manager.getTasks()) {
                System.out.println(task);
            }
    }
}
