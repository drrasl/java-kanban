package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    int setTask(Task task);

    int setEpic(Epic epic);

    int setSubTask(SubTask subTask);

    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Optional<Task> getTask(int id);

    Optional<SubTask> getSubTask(int id);

    Optional<Epic> getEpic(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubTaskById(Integer id);

    void deleteEpicById(Integer id);

    List<SubTask> getSubTaskByEpic(int epicId);

    List<Task> getTasks();
}
