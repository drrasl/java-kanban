package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private static final String FILE_NAME = "src//resources//backup.csv";
    Path backupFile;

    public FileBackedTaskManager() {
        super();
        checkBackupFile();
    }

    //В случае передачи файла, в том числе для теста
    public FileBackedTaskManager(Path path) {
        super();
        backupFile = path;
    }

    public void checkBackupFile() {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            try {
                backupFile = Files.createFile(Paths.get(FILE_NAME));
            } catch (IOException e) {
                System.out.println("Ошибка создания файла backup.csv");
            }
        }
    }

    public void save() throws ManagerSaveException {
        ArrayList<Task> tasks = getAllTasks();
        ArrayList<Epic> epics = getAllEpics();
        ArrayList<SubTask> subTasks = getAllSubTasks();

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(backupFile)))) {
            fileWriter.write("Id,type,name,status,description,epic_id(for_subtasks)" + "\n");
            if (tasks != null) {
                for (Task task : tasks) {
                    fileWriter.write(toString(task) + "\n");
                }
            }
            if (epics != null) {
                for (Epic epic : epics) {
                    fileWriter.write(toString(epic) + "\n");
                }
            }
            if (subTasks != null) {
                for (SubTask subTask : subTasks) {
                    fileWriter.write(toString(subTask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи задач в файл backup.csv");
        }
    }

    public String toString(Task task) {
        TypesOfTasks type;
        if (task.getClass() == Task.class) {
            type = TypesOfTasks.TASK;
        } else if (task.getClass() == Epic.class) {
            type = TypesOfTasks.EPIC;
        } else {
            type = TypesOfTasks.SUBTASK;
        }
        String info = task.getId() + "," + type + "," +
                task.getName() + "," + task.getStatus() + "," +
                task.getDescription();
        if (type != TypesOfTasks.SUBTASK) {
            return info;
        } else {
            return info + "," + ((SubTask) task).getEpicId();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        String backLog = "";
        try {
            backLog = Files.readString(file.toPath());
        } catch (IOException exception) {
            System.out.println("Ошибка чтения из файла backup.csv");
        }
        String[] tasks = backLog.split("\n");
        for (int i = 0; i < tasks.length; i++) {
            if (i > 0) {
                if (tasks[i].contains(TypesOfTasks.EPIC.toString())) {
                    try {
                        Epic epic = (Epic) fromString(tasks[i]);
                        fileBackedTaskManager.epicMap.put(epic.getId(), epic);
                    } catch (NullPointerException e) {
                        System.out.println("Эпик вернулся как null");
                    }
                } else if (tasks[i].contains(TypesOfTasks.SUBTASK.toString())) {
                    try {
                        SubTask subTask = (SubTask) fromString(tasks[i]);
                        fileBackedTaskManager.subTasksMap.put(subTask.getId(), subTask);
                        Epic epic;
                        epic = fileBackedTaskManager.epicMap.get(subTask.getEpicId());
                        epic.setSubTask(subTask);
                        epic.updateEpicStatus();
                    } catch (NullPointerException e) {
                        System.out.println("Подзадача вернулась как null");
                    }
                } else {
                    try {
                        Task task = fromString(tasks[i]);
                        fileBackedTaskManager.tasksMap.put(task.getId(), task);
                    } catch (NullPointerException e) {
                        System.out.println("Задача вернулась как null");
                    }
                }
            }
        }
        return fileBackedTaskManager;
    }

    public static Task fromString(String value) {
        StatusOfTask status = StatusOfTask.NEW;
        String[] taskDetails = value.split(",");

        if (taskDetails[3].equals(StatusOfTask.IN_PROGRESS.toString())) {
            status = StatusOfTask.IN_PROGRESS;
        } else if (taskDetails[3].equals(StatusOfTask.DONE.toString())) {
            status = StatusOfTask.DONE;
        }

        int id = Integer.parseInt(taskDetails[0]);

        if (value.contains(TypesOfTasks.EPIC.toString())) {
            return new Epic(taskDetails[2], taskDetails[4], status, id);
        } else if (value.contains(TypesOfTasks.SUBTASK.toString())) {
            int subId = Integer.parseInt(taskDetails[5]);
            return new SubTask(taskDetails[2], taskDetails[4], status, id, subId);
        } else if (value.contains(TypesOfTasks.TASK.toString())) {
            return new Task(taskDetails[2], taskDetails[4], status, id);
        }
        return null;
    }


    @Override
    public int setTask(Task task) {
        int set = super.setTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return set;
    }

    @Override
    public int setEpic(Epic epic) {
        int set = super.setEpic(epic);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return set;
    }

    @Override
    public int setSubTask(SubTask subTask) {
        int set = super.setSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return set;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        return super.getSubTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(int epicId) {
        return super.getSubTaskByEpic(epicId);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }
}
