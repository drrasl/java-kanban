import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasksMap;
    private HashMap<Integer, SubTask> subTasksMap;
    private HashMap<Integer, Epic> epicMap;

    public TaskManager () {
        tasksMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTasksMap = new HashMap<>();
    }

    public int setTask (Task task) {
        id++;
        task.setId(id);
        tasksMap.put(id, task);
        return task.getId();
    }

    public int setEpic (Epic epic) {
        id++;
        epic.setId(id);
        epicMap.put(id, epic);
        return epic.getId();
    }

    public int setSubTask (SubTask subTask) {
        if (epicMap.containsKey(subTask.getEpicId())) {
            id++;
            subTask.setId(id);
            subTasksMap.put(id, subTask);
            Epic epic;
            epic = epicMap.get(subTask.getEpicId());
            epic.setSubTask(subTask);
            epic.updateEpicStatus();
            return subTask.getId();
        } else {
            return -1; // Если вернется -1, то отсутствует нужный эпик.
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
        return epicMap.get(id);
    }

    public void updateTask (Task task) {
        if (tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        }
    }

    public void updateSubTask (SubTask subTask) {
        if (subTasksMap.containsKey(subTask.getId())) {
            int epicId = subTasksMap.get(subTask.getId()).getEpicId();
            Epic epic = epicMap.get(epicId);
            if (subTask.getEpicId() == epicId) {
                SubTask subTaskOld = subTasksMap.get(subTask.getId());
                epic.deleteSubTask(subTaskOld);
                epic.setSubTask(subTask);
                subTasksMap.put(subTask.getId(), subTask);
                epic.updateEpicStatus();
            }
        }
    }

    public void updateEpic (Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.get(epic.getId()).setName(epic.getName());
            epicMap.get(epic.getId()).setDescription(epic.getDescription());
        }
     }

    public void deleteTaskById (Integer id) {
        tasksMap.remove(id);
    }

    public void deleteSubTaskById (Integer id) {
        if (subTasksMap.containsKey(id)) {
            int epicId = subTasksMap.get(id).getEpicId();
            epicMap.get(epicId).deleteSubTask(subTasksMap.get(id));
            epicMap.get(epicId).updateEpicStatus();
            subTasksMap.remove(id);
        }
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
        if (epicMap.containsKey(epicId)) {
            return epicMap.get(epicId).getSubTasks();
        } else {
            return null;
        }
    }
}