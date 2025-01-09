import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 1;
    private int id;
    private HashMap<Integer, Task> tasksMap;
    private HashMap<Integer, SubTask> subTasksMap;
    private HashMap<Integer, Epic> epicMap;

    public TaskManager () {
        tasksMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTasksMap = new HashMap<>();
    }

    public int setTask (Task task) {
        id = setId();
        task.setId(id);
        tasksMap.put(id, task);
        if (task.getId() == id) {
            return id;
        } else {
            return -1; // Если вернется -1, то ошибка добавления Задачи.
        }
    }

    public int setEpic (Epic epic) {
        id = setId();
        epic.setId(id);
        epicMap.put(id, epic);
        if (epic.getId() == id) {
            return id;
        } else {
            return -1; // Если вернется -1, то ошибка добавления Эпика.
        }
    }

    public int setSubTask (SubTask subTask) {
        if (epicMap.containsKey(subTask.getEpicId())) {
            id = setId();
            subTask.setId(id);
            subTasksMap.put(id, subTask);
            Epic epic;
            epic = epicMap.get(subTask.getEpicId());
            epic.setSubTask(subTask);
            epic.updateEpicStatus();
            if (subTask.getId() == id) {
                return id;
            } else {
                return -1; // Если вернется -1, то ошибка добавления ПодЗадачи.
            }
        } else {
            return -1; // Если вернется -1, то ошибка добавления ПодЗадачи.
        }
    }

    public ArrayList<Task> getAllTasks () {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(tasksMap.values());
        return tasks;
    }

    public ArrayList<SubTask> getAllSubTasks () {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.addAll(subTasksMap.values());
        return subTasks;
    }

    public ArrayList<Epic> getAllEpics () {
        for (Integer key: epicMap.keySet()) {
            epicMap.get(key).updateEpicStatus();
        }
        ArrayList<Epic> epics = new ArrayList<>();
        epics.addAll(epicMap.values());
        return epics;
    }

    public void removeAllTasks () {
        this.tasksMap.clear();
    }

    public void removeAllSubTasks () {
        this.subTasksMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.removeSubTasks();
            epic.updateEpicStatus();
        }
    }

    public void removeAllEpics () {
        this.epicMap.clear();
        this.subTasksMap.clear();
    }

    public Task getTask(int id) {
       return tasksMap.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasksMap.get(id);
    }

    public Epic getEpic(int id) {
        epicMap.get(id).updateEpicStatus();
        return epicMap.get(id);
    }

    public void updateTask (Task task) {
        int checkId = task.getId();
        for (Integer id : tasksMap.keySet()) {
            if (checkId == id) {
                tasksMap.put(id, task);
            }
        }
    }

    public void updateSubTask (SubTask subTask) {
        int checkId = subTask.getId();
        for (Integer id : subTasksMap.keySet()) {
            if (checkId == id) {
                int epicId = subTasksMap.get(id).getEpicId();
                if (subTask.getEpicId() == epicId) {
                    SubTask subTaskOld = subTasksMap.get(id);
                    epicMap.get(epicId).deleteSubTask(subTaskOld);
                    epicMap.get(epicId).setSubTask(subTask);
                    subTasksMap.put(id, subTask);
                    epicMap.get(epicId).updateEpicStatus();
                }
            }
        }
    }

    public void updateEpic (Epic epic) {

        for (Integer id : epicMap.keySet()) {
            if (epic.getId() == id) {
                epicMap.get(id).setName(epic.getName());
                epicMap.get(id).setDescription(epic.getDescription());
            }
        }
    }

    public void deleteTaskById (Integer id) {
        tasksMap.remove(id);
    }

    public void deleteSubTaskById (Integer id) {
        epicMap.get(subTasksMap.get(id).getEpicId()).deleteSubTask(subTasksMap.get(id));
        epicMap.get(subTasksMap.get(id).getEpicId()).updateEpicStatus();
        subTasksMap.remove(id);
    }

    public void deleteEpicById (Integer id) {
        if (epicMap.containsKey(id)) {
            for (SubTask subTask : getSubTaskByEpic(id)) {
                subTasksMap.remove(subTask.getId());
            }
            epicMap.remove(id);
        }
    }

    public ArrayList<SubTask> getSubTaskByEpic (int epicId) {
        return epicMap.get(epicId).getSubTasks();
    }

    private Integer setId () {
        return idCounter++;
    }
}