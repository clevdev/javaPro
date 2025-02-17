package task3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/*
   Попробуйте реализовать собственный пул потоков. В качестве аргументов конструктора пулу передается
   его емкость (количество рабочих потоков). Как только пул создан, он сразу инициализирует и
   запускает потоки. Внутри пула очередь задач на исполнение организуется через LinkedList<Runnable>.
   При выполнении у пула потоков метода execute(Runnable), указанная задача должна попасть в очередь исполнения,
   и как только появится свободный поток – должна быть выполнена. Также необходимо реализовать метод shutdown(),
   после выполнения которого, новые задачи больше не принимаются пулом (при попытке добавить задачу
   можно бросать IllegalStateException), и все потоки для которых больше нет задач завершают свою работу.
   Дополнительно можно добавить метод awaitTermination() без таймаута, работающий аналогично стандартным пулам потоков
 */
public class SimpleThreadPool {
    //private final int capacity;
    private final WorkerThread[] workerThreads;
    private final Queue<Runnable> taskQueue;
    private final AtomicBoolean isShutdown;

    public SimpleThreadPool(int capacity) {

        this.workerThreads = new WorkerThread[capacity];
        this.taskQueue = new LinkedList<>();
        this.isShutdown = new AtomicBoolean(false);

        for (int i = 0; i < capacity; i++) {
            workerThreads[i] = new WorkerThread(i);
            workerThreads[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized (taskQueue) {
            if (isShutdown.get()) {
                throw new IllegalStateException("ThreadPool is shut down and can't accept new tasks");
            }
            taskQueue.offer(task);
            taskQueue.notifyAll();
        }
    }

    public void shutdown() {
        synchronized (taskQueue) {
            isShutdown.getAndSet(true);
            taskQueue.notifyAll(); // пробуждаем всех ожидающих потоков
        }
    }

    public void awaitTermination() throws InterruptedException {
        synchronized (taskQueue) {
            while (!isAllTasksCompleted()) {
                taskQueue.wait();
            }
        }
    }

    private boolean isAllTasksCompleted() {
        return taskQueue.isEmpty() && allWorkersIdle();
    }

    private boolean allWorkersIdle() {
        for (WorkerThread worker : workerThreads) {
            if (worker.isAlive() && worker.isWorking()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool threadPool = new SimpleThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            threadPool.execute(() -> {
                System.out.println("Executing task " + taskId);
                try {
                    Thread.sleep(1000); // имитация работы
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        System.out.println("All tasks added to queue.");
        Thread.sleep(1000);
        threadPool.shutdown();
        System.out.println("threadPool.shutdown()");
        try {
            System.out.println("threadPool awaitTermination.");
            threadPool.awaitTermination();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All tasks completed.");
    }


    private class WorkerThread extends Thread {
        private boolean working;
        private final int workerNumber;

        public WorkerThread(int workerNumber) {
            this.workerNumber = workerNumber;
        }

        @Override
        public void run() {
            while (true) {
                Runnable task;

                synchronized (SimpleThreadPool.this.taskQueue) {
                    if (isShutdown.get() && taskQueue.isEmpty()) {
                        System.out.println("Worker while (true)" + workerNumber + " isShutdown ");
                        working = false;
                        SimpleThreadPool.this.taskQueue.notify();
                        return; // Завершаем поток, если пул завершен
                    }
                    while (taskQueue.isEmpty()) {
                        working = false;
                        try {
                            SimpleThreadPool.this.taskQueue.wait(); // ждем новую задачу
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // восстанавливаем прерывание
                            return;
                        }
                    }
                    working = true;
                    task = taskQueue.poll();
                }

                try {
                    if (task != null) {
                        System.out.println("Worker " + workerNumber + " Executing task ");
                        task.run(); // выполняем задачу
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // обработка ошибок задач
                }
            }

        }

        public boolean isWorking() {
            return working;
        }
    }

}
