package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    protected Map<Integer, Task> tasksMap;
    protected Map<Integer, SubTask> subTasksMap;
    protected Map<Integer, Epic> epicMap;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTasksMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int setTask(Task task) {
        if (task != null && task.getClass() == Task.class) {
            id++;
            task.setId(id);
            tasksMap.put(id, task);
            return task.getId();
        }
        return -1;
    }

    @Override
    public int setEpic(Epic epic) {
        id++;
        epic.setId(id);
        epicMap.put(id, epic);
        return epic.getId();
    }

    @Override
    public int setSubTask(SubTask subTask) {
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

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(tasksMap.values());
        return tasks;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.addAll(subTasksMap.values());
        return subTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.addAll(epicMap.values());
        return epics;
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasksMap.keySet()) {
            historyManager.removeHistory(id);
        }
        this.tasksMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer id : subTasksMap.keySet()) {
            historyManager.removeHistory(id);
        }
        this.subTasksMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.removeSubTasks();
            epic.updateEpicStatus();
        }
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epicMap.keySet()) {
            historyManager.removeHistory(id);
        }
        for (Integer id : subTasksMap.keySet()) {
            historyManager.removeHistory(id);
        }
        this.epicMap.clear();
        this.subTasksMap.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.setHistory(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.setHistory(subTasksMap.get(id));
        return subTasksMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.setHistory(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
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

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.get(epic.getId()).setName(epic.getName());
            epicMap.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        historyManager.removeHistory(id);
        tasksMap.remove(id);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (subTasksMap.containsKey(id)) {
            historyManager.removeHistory(id);
            int epicId = subTasksMap.get(id).getEpicId();
            epicMap.get(epicId).deleteSubTask(subTasksMap.get(id));
            epicMap.get(epicId).updateEpicStatus();
            subTasksMap.remove(id);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epicMap.containsKey(id)) {
            for (SubTask subTask : getSubTaskByEpic(id)) {
                historyManager.removeHistory(subTask.getId());
                subTasksMap.remove(subTask.getId());
            }
            historyManager.removeHistory(id);
            epicMap.remove(id);
        }
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(int epicId) {
        if (epicMap.containsKey(epicId)) {
            return epicMap.get(epicId).getSubTasks();
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return (ArrayList<Task>) historyManager.getTasks();
    }
}