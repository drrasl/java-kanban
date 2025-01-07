import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;
    private Task task;
    private SubTask subTask;
    private Epic epic;
    private HashMap<Integer, Task> tasksMap;
    private HashMap<Integer, SubTask> subTasksMap;
    private HashMap<Integer, Epic> epicMap;

    public TaskManager () {
        tasksMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTasksMap = new HashMap<>();
    }

    public void setTask (Task task) {
        this.task = task;
        id++;
        tasksMap.put(id, task);
    }

    public void setEpic (Epic epic) {
        this.epic = epic;
        id++;
        epicMap.put(id, epic);
    }

    public void setSubTask (SubTask subTask) {
        this.subTask = subTask;
        id++;
        subTasksMap.put(id, subTask);
    }

    public void getAllTasks () {
        for (Integer id : tasksMap.keySet()) {
            System.out.println("Задача - {id" + id + "}- " + tasksMap.get(id));
        }
    }

    public void getAllSubTasks () {
        for (Integer id : subTasksMap.keySet()) {
            System.out.println("Подзадача - {id" + id + "}- " + subTasksMap.get(id));
        }
    }

    public void getAllEpics () {
        for (Integer key : epicMap.keySet()) {
            epicMap.get(key).setStatus(epicMap.get(key).getStatus());
            System.out.println("Эпик - {id" + key + "}- " + epicMap.get(key));
        }
    }

    public void removeAllTasks () {
        this.tasksMap.clear();
    }

    public void removeAllSubTasks () {
        this.subTasksMap.clear();
    }

    public void removeAllEpics () {
        this.epicMap.clear();
        this.subTasksMap.clear();
    }

    public Task getTask(int id) {
        Task task = null;
        for (Integer key : tasksMap.keySet()) {
            if (key == id) {
                task = tasksMap.get(id);
            }
        }
        if (task == null) {
            System.out.println("Задача с id" + id + " не найдена");
            return task;
        }
        return task;
    }

    public SubTask getSubTask(int id) {
        SubTask subTask = null;
        for (Integer key : subTasksMap.keySet()) {
            if (key == id) {
                subTask = subTasksMap.get(id);
            }
        }
        if (subTask == null) {
            System.out.println("Подзадача с id" + id + " не найдена");
            return subTask;
        }
        return subTask;
    }

    public Epic getEpic(int id) {
        Epic epic = null;
        for (Integer key : epicMap.keySet()) {
            epicMap.get(key).setStatus(epicMap.get(key).getStatus());
            if (key == id) {
                epic = epicMap.get(id);
            }
        }
        if (epic == null) {
            System.out.println("Эпик с id" + id + " не найден");
            return epic;
        }
        return epic;
    }

    public void updateTask (Task task, Integer id) {
        tasksMap.put(id, task);
    }

    public void updateSubTask (SubTask subTask, Integer id) {
        subTasksMap.put(id, subTask);
    }

    public void updateEpic (Epic epic, Integer id) {
        epicMap.put(id, epic);
    }

    public void deleteTaskById (Integer id) {
        tasksMap.remove(id);
    }

    public void deleteSubTaskById (Integer id) {
        subTasksMap.remove(id);
    }

    public void deleteEpicById (Integer id) {
        epicMap.get(id).removeSubTasks();
        int epicId = 0;
        ArrayList<Integer> idForDelete = new ArrayList<>();

        for (Integer key : subTasksMap.keySet()) {
            epicId = subTasksMap.get(key).getEpicId();
                if (id == epicId) {
                    idForDelete.add(key);
                }
        }
        for (Integer key : idForDelete) {
            deleteSubTaskById(key);
        }
        epicMap.remove(id);
    }

    public HashMap<Integer, Task> getTaskAsMap () { // Метод добавлен по требованиям ТЗ для того, чтобы через
        //sout() вывести список задач. Изначально для целей печати я сделал void метод getAllTasks () (соответствующее
        // название для подзадачи и эпика), который сразу печатает как мне нужно.

        return tasksMap;
    }

    public HashMap<Integer, SubTask> getSubTaskAsMap () { // Метод добавлен по требованиям ТЗ для того, чтобы через
        //sout() вывести список задач. Изначально для целей печати я сделал void метод getAllTasks () (соответствующее
        // название для подзадачи и эпика), который сразу печатает как мне нужно.

        return subTasksMap;
    }

    public HashMap<Integer, Epic> getEpicAsMap () { // Метод добавлен по требованиям ТЗ для того, чтобы через
        //sout() вывести список задач. Изначально для целей печати я сделал void метод getAllTasks () (соответствующее
        // название для подзадачи и эпика), который сразу печатает как мне нужно.
        for (Integer key : epicMap.keySet()) {
            epicMap.get(key).setStatus(epicMap.get(key).getStatus());
        }
        return epicMap;
    }

    public int getEpicId(Epic epic){
        int id = 0;
        for (Integer key : epicMap.keySet()) {
            for (Epic epicValue : epicMap.values()) {
                if (epicValue != null && epicValue.equals(epic)) {
                    id = key;
                }
            }
        }
        return id;
    }
}