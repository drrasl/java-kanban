package service;

import model.StatusOfTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
    Создан отдельный класс для поиска пересечений
    Может быть встроенным в InMemoryTaskManager.class или даже методом в нем.
    Так как на данный момент не планируется его использовать в приложении, то
    вынес его в отдельный метод. Цель - поиск пересечений за О(1).
    Х-Т это таблица ключей по 15 минут и значений - занято или нет.
    -----
    Предполагаем, что класс будет инициализироваться на 1 год, то есть Х-М на 365 дней.
*/

public class TaskIntersections {
    protected Map<Integer, Boolean> timeTaskMap;
    int year;
    int daysOfYear = Year.of(year).length();
    int intervalsQty = daysOfYear * 24 * 60 / 15; // Кол-во ключей в Х-Т для обычного года = 35040, для високосного = 35136

    public TaskIntersections(int year) {
        this.year = year;
        timeTaskMap = new HashMap<>(intervalsQty);
    }

    protected int fillInMap(Task task) {
        if (isNoTaskIntersectionThroughHashMap(task)) {
            int[] keyAndDuration = keyAndKeyDurationReceiver(task);
            for (int i = 0; i < keyAndDuration[1]; i++) {
                timeTaskMap.put(keyAndDuration[0] - 1 + i, false);
            }
            return 1;
        } else {
            return -1;
        }
    }

    protected int removeTaskFromMap(Task task) {
        if (!isNoTaskIntersectionThroughHashMap(task)) {
            int[] keyAndDuration = keyAndKeyDurationReceiver(task);
            for (int i = 0; i < keyAndDuration[1]; i++) {
                timeTaskMap.put(keyAndDuration[0] - 1 + i, true);
            }
            return 2;
        } else {
            return -2;
        }
    }

    //true - пересечений нет, false - пересечений есть
    protected boolean isNoTaskIntersectionThroughHashMap(Task task) {
        int[] keyAndDuration = keyAndKeyDurationReceiver(task);
        if (keyAndDuration[0] > intervalsQty || (keyAndDuration[0] + keyAndDuration[1] > intervalsQty)) {
            return false;
        }
        if (timeTaskMap.isEmpty()) {
            return true;
        } else {
            int countOfTrue = 0;
            for (int i = 0; i < keyAndDuration[1]; i++) {
                if (timeTaskMap.get(keyAndDuration[0] - 1 + i) == null || timeTaskMap.get(keyAndDuration[0] - 1 + i)) {
                    countOfTrue++;
                }
            }
            return countOfTrue == keyAndDuration[1];
        }
    }

    protected int[] keyAndKeyDurationReceiver(Task task) {
        int[] keyAndDuration = new int[2];
        int durationInKeys;
        int startKey;
        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        Duration duration = task.getDuration();

        if (duration.toMinutes() % 15 != 0) {
            durationInKeys = (int) duration.toMinutes() / 15 + 1; //если не кратно 15 мин, то округляем в большую сторону
        } else {
            durationInKeys = (int) duration.toMinutes() / 15;
        }
        Duration startPeriod = Duration.between(yearStart, taskStartTime); // период между началом года и началом таска

        if (startPeriod.toMinutes() % 15 == 0) {
            startKey = (int) startPeriod.toMinutes() / 15;
        } else {
            startKey = (int) startPeriod.toMinutes() / 15 + 1; //если не кратно 15 мин, то округляем в большую сторону
        }
        keyAndDuration[0] = startKey;
        keyAndDuration[1] = durationInKeys;
        return keyAndDuration;
    }

    //Ниже проводим тест работы метода
    public static void main(String[] args) {
        TaskIntersections inter = new TaskIntersections(2025);

        int days = Year.of(2025).length();
        System.out.println("Кол-во дней в 2025 году = " + days);

        //Задаем 2 таска
        System.out.println("Задаем 2 таска и выведем [ключ, кол-во ключей]");
        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE, LocalDateTime.of(2025, 01, 01, 00, 1), Duration.ofMinutes(45));
        System.out.println(task1);
        System.out.println(Arrays.toString(inter.keyAndKeyDurationReceiver(task1)));
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW, LocalDateTime.of(2025, 02, 01, 10, 30), Duration.ofMinutes(30));
        System.out.println(task2);
        System.out.println(Arrays.toString(inter.keyAndKeyDurationReceiver(task2)));

        //Проверяем пересечение и записываем в мапу
        System.out.println("Проверяем, что пересечений нет и записываем в мапу: 1 = записано");
        System.out.println(inter.isNoTaskIntersectionThroughHashMap(task1));
        System.out.println(inter.fillInMap(task1));

        System.out.println(inter.isNoTaskIntersectionThroughHashMap(task2));
        System.out.println(inter.fillInMap(task2));

        // Проверим мапу
        System.out.println("Проверим мапу");
        System.out.println(inter.timeTaskMap.get(0) + " / " + inter.timeTaskMap.get(1) + " / " + inter.timeTaskMap.get(2) + " / " + inter.timeTaskMap.get(3));
        System.out.println(inter.timeTaskMap.get(3016) + " / " + inter.timeTaskMap.get(3017) + " / " + inter.timeTaskMap.get(3018) + " / " + inter.timeTaskMap.get(3019));

        // Добавим таск3, пересекающий таск 1
        System.out.println("Добавим таск3, пересекающий таск 1");
        Task task3 = new Task("task3", "descr3", StatusOfTask.DONE, LocalDateTime.of(2025, 01, 01, 00, 16), Duration.ofMinutes(60));
        System.out.println(task3);
        System.out.println(Arrays.toString(inter.keyAndKeyDurationReceiver(task3)));

        //Проверим пересечение
        System.out.println("Проверим пересечение: false - есть пересечение");
        System.out.println(inter.isNoTaskIntersectionThroughHashMap(task3));
        //Пересечение есть, попробуем записать
        System.out.println("Пересечение есть, попробуем записать: -1 не записалось");
        System.out.println(inter.fillInMap(task3)); // -1 не записалось

        //Удалим таск 1
        System.out.println("Удалим таск 1: Удалилось успешно = 2, не удалилось = -2");
        System.out.println(inter.removeTaskFromMap(task1));
        // Проверим мапу
        System.out.println("Проверим мапу");
        System.out.println(inter.timeTaskMap.get(0) + " / " + inter.timeTaskMap.get(1) + " / " + inter.timeTaskMap.get(2) + " / " + inter.timeTaskMap.get(3));

        //Проверим пересечение и запишем таск 1
        System.out.println("Проверим пересечение: true - нет пересечение");
        System.out.println(inter.isNoTaskIntersectionThroughHashMap(task3));
        //Пересечение есть, попробуем записать
        System.out.println("Пересечения нет, попробуем записать: -1 не записалось / 1 - записалось");
        System.out.println(inter.fillInMap(task3));
        // Проверим мапу
        System.out.println("Проверим мапу");
        System.out.println(inter.timeTaskMap.get(0) + " / " + inter.timeTaskMap.get(1) + " / " + inter.timeTaskMap.get(2) + " / " + inter.timeTaskMap.get(3) + " / " + inter.timeTaskMap.get(4)
                + " / " + inter.timeTaskMap.get(5));
    }
}
