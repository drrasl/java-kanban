package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.*;

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
        if (task != null && task.getClass() == Task.class && isNoTaskIntersection(task)) {
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
        if (epicMap.containsKey(subTask.getEpicId()) && isNoTaskIntersection(subTask)) {
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
        tasksMap.keySet().forEach(historyManager::removeHistory);
        this.tasksMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasksMap.keySet().forEach(historyManager::removeHistory);
        this.subTasksMap.clear();
        epicMap.values().forEach(epic -> {
            epic.removeSubTasks();
            epic.updateEpicStatus(); // Метод updateEpicStatus() в конце содержит команду на метод updateEpicTime()
        });
    }

    @Override
    public void removeAllEpics() {
        epicMap.keySet().forEach(historyManager::removeHistory);
        subTasksMap.keySet().forEach(historyManager::removeHistory);
        this.epicMap.clear();
        this.subTasksMap.clear();
    }

    @Override
    public Optional<Task> getTask(int id) {
        historyManager.setHistory(tasksMap.get(id));
        return Optional.ofNullable(tasksMap.get(id));
    }

    @Override
    public Optional<SubTask> getSubTask(int id) {
        historyManager.setHistory(subTasksMap.get(id));
        return Optional.ofNullable(subTasksMap.get(id));
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        historyManager.setHistory(epicMap.get(id));
        return Optional.ofNullable(epicMap.get(id));
    }

    @Override
    public void updateTask(Task task) {
        if (tasksMap.containsKey(task.getId()) && isNoTaskIntersection(task)) {
            tasksMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasksMap.containsKey(subTask.getId()) && isNoTaskIntersection(subTask)) {
            int epicId = subTasksMap.get(subTask.getId()).getEpicId();
            Epic epic = epicMap.get(epicId);
            if (subTask.getEpicId() == epicId) {
                SubTask subTaskOld = subTasksMap.get(subTask.getId());
                epic.deleteSubTask(subTaskOld);
                epic.setSubTask(subTask);
                subTasksMap.put(subTask.getId(), subTask);
                epic.updateEpicStatus(); // Метод updateEpicStatus() в конце содержит команду на метод updateEpicTime()
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
            epicMap.get(epicId).updateEpicStatus(); // Метод updateEpicStatus() в конце содержит команду на метод updateEpicTime()
            subTasksMap.remove(id);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epicMap.containsKey(id)) {
            getSubTaskByEpic(id).forEach(subTask -> {
                historyManager.removeHistory(subTask.getId());
                subTasksMap.remove(subTask.getId());
            });
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

    protected void setId(int id) {
        this.id = id;
    }

    public Set<Task> getPrioritizedTasks() {
        Set<Task> priorityTasksList = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId)
        );

        List<Task> tasksMapFilteredToList = tasksMap.values().stream()
                .filter(task -> task.getStartTime() != null)
                .toList();
        List<SubTask> subTasksMapFilteredToList = subTasksMap.values().stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .toList();

        priorityTasksList.addAll(tasksMapFilteredToList);
        priorityTasksList.addAll(subTasksMapFilteredToList);

        return priorityTasksList;
    }

    protected boolean isNoTaskIntersection(Task task) {
        if (getPrioritizedTasks().isEmpty() | task.getStartTime() == null) {
            return true;
        } else {
            /* Проверяем с каждым (искомый таск - отрезок AB, таски в дереве ab):
              а) a-A-b,
              б) A-a-b-B
              в) a-B-b
              г) A = a || A = b || B = a || B = b
              */
            return getPrioritizedTasks().stream()
                    .filter(item ->
                            task.getId() != item.getId()
                    )
                    .noneMatch(item -> {
                        return (task.getStartTime().isAfter(item.getStartTime()) &&
                                task.getStartTime().isBefore(item.getEndTime())) ||
                                (task.getStartTime().isBefore(item.getStartTime()) &&
                                        task.getEndTime().isAfter(item.getEndTime())) ||
                                (task.getEndTime().isAfter(item.getStartTime()) &&
                                        task.getEndTime().isBefore(item.getEndTime())) ||
                                (task.getStartTime().isEqual(item.getStartTime()) ||
                                        task.getStartTime().isEqual(item.getEndTime()) ||
                                        task.getEndTime().isEqual(item.getStartTime()) ||
                                        task.getEndTime().isEqual(item.getEndTime()));
                    });
        }
    }
}