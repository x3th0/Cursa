package club.eridani.cursa.concurrent;

import club.eridani.cursa.concurrent.repeat.RepeatManager;
import club.eridani.cursa.concurrent.repeat.RepeatUnit;
import club.eridani.cursa.concurrent.task.*;
import club.eridani.cursa.concurrent.thread.BackgroundMainThread;
import club.eridani.cursa.concurrent.utils.Syncer;
import club.eridani.cursa.tasks.Tasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntSupplier;

/**
 * Created by B_312 on 05/01/2021
 */
public class TaskManager {

    //---- Instance Stuff ----//
    public static TaskManager instance;

    public static Logger logger = LogManager.getLogger("Concurrent Task Manager");

    public static int workingThreads = 8;

    public BackgroundMainThread backgroundMainThread = new BackgroundMainThread();

    public final ThreadPoolExecutor executor = new ThreadPoolExecutor(workingThreads, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public static void init() {
        if (instance == null) instance = new TaskManager();
    }

    TaskManager() {
        RepeatManager.init();
        backgroundMainThread.start();
    }

    //---- TaskPool Runner ----//
    public static void runBlocking(List<VoidTask> tasks) {
        Syncer syncer = new Syncer(tasks.size());
        tasks.forEach(it -> instance.executor.execute(new VoidRunnable(it, syncer)));
        syncer.await();
    }

    public static <T> void runParameterBlocking(List<Task<T>> tasks, T[] parameters) {
        Syncer syncer = new Syncer(tasks.size());
        tasks.forEach(it -> instance.executor.execute(new TaskRunnable<>(it, syncer, parameters)));
        syncer.await();
    }

    public static void launch(Tasks tasks) {
        instance.executor.execute(new VoidRunnable(tasks.task));
    }

    public static void launch(VoidTask task) {
        instance.executor.execute(new VoidRunnable(task));
    }

    public static <T> void launch(Syncer syncer, T parameters, EventTask<T> task) {
        instance.executor.execute(new TaskRunnable<>(task, syncer, parameters));
    }

    public static <T> void launch(T parameter, EventTask<T> task) {
        instance.executor.execute(new TaskRunnable<>(parameter, task));
    }

    public static <T> void launch(Syncer syncer, T[] parameters, Task<T> task) {
        instance.executor.execute(new TaskRunnable<>(task, syncer, parameters));
    }

    public static <T> void launch(T[] parameters, Task<T> task) {
        instance.executor.execute(new TaskRunnable<>(parameters, task));
    }

    public static void launch(Syncer syncer, VoidTask task) {
        instance.executor.execute(new VoidRunnable(task, syncer));
    }

    //---- Delay Runner ----//
    public static <T> void addDelayTask(int delay, T parameter, EventTask<T> task) {
        runRepeat(delay, 1, true, () -> launch(parameter, task));
    }

    public static <T> void addDelayTask(int delay, T[] parameters, Task<T> task) {
        runRepeat(delay, 1, true, () -> launch(parameters, task));
    }

    public static void addDelayTask(int delay, VoidTask task) {
        runRepeat(delay, 1, true, () -> launch(task));
    }

    //---- Repeat Runner ----//
    public static void runRepeat(RepeatUnit unit) {
        registerRepeatUnit(unit);
    }

    public static void runRepeat(int delay, VoidTask task) {
        RepeatUnit unit = new RepeatUnit(delay, task);
        registerRepeatUnit(unit);
    }

    public static void runRepeat(int delay, int times, VoidTask task) {
        RepeatUnit unit = new RepeatUnit(delay, times, task);
        registerRepeatUnit(unit);
    }

    public static void runRepeat(int delay, int times, boolean isDelayTask, VoidTask task) {
        RepeatUnit unit = new RepeatUnit(delay, times, isDelayTask, task);
        registerRepeatUnit(unit);
    }

    public static void runRepeat(IntSupplier delay, VoidTask task) {
        RepeatUnit unit = new RepeatUnit(delay, task);
        registerRepeatUnit(unit);
    }

    public static void runRepeat(IntSupplier delay, int times, VoidTask task) {
        RepeatUnit unit = new RepeatUnit(delay, times, task);
        registerRepeatUnit(unit);
    }

    public static void registerRepeatUnit(RepeatUnit repeatUnit) {
        RepeatManager.instance.repeatUnits.add(repeatUnit);
    }

    public static void unregisterRepeatUnit(RepeatUnit repeatUnit) {
        RepeatManager.instance.repeatUnits.remove(repeatUnit);
    }

    //---- Background Stuff ----//
    public static void updateBackground() {
        RepeatManager.update();
    }

}
