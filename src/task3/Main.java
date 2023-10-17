package task3;



import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main
{
    public static void main(String[] args) {
        FileQueue queue = new FileQueue(5);
        String[] types = new String[] {"XML", "JSON", "XLS"};

        FileHandler XmlHandler = new FileHandler(queue, "XML", queue.lock, queue.notEmpty);
        FileHandler JsonHandler = new FileHandler(queue, "JSON", queue.lock, queue.notEmpty);
        FileHandler XlsHandler = new FileHandler(queue, "XLS", queue.lock, queue.notEmpty);

        Thread XmlThread = new Thread(XmlHandler);
        Thread JsonThread = new Thread(JsonHandler);
        Thread XlsThread = new Thread(XlsHandler);

        XmlThread.start();
        JsonThread.start();
        XlsThread.start();



        FileGenerator fileGenerator = new FileGenerator(queue, queue.lock, queue.notFull, types);
        Thread generatorThread = new Thread(fileGenerator);
        generatorThread.start();
        
    }
}



class File_ {
    String type;
    Integer size;

    public File_(String type, Integer size) {
        this.type = type;
        this.size = size;
    }
}


class FileQueue {

    public ReentrantLock lock = new ReentrantLock();
    public Condition notEmpty = lock.newCondition();
    public Condition notFull = lock.newCondition();

    public Queue<File_> queue = new LinkedList<>();
    public int max_size;

    public FileQueue(int max_size) {
        this.max_size = max_size;
    }

    public void add(File_ file) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == max_size) {
                notFull.await();
            }
            queue.add(file);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public void remove() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            queue.remove();
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return queue.size();
    }

    public File_ peek() {
        return queue.peek();
    }
}



class FileHandler implements Runnable {
    private final FileQueue queue;
    private final String handlerType;
    private final Lock lock;
    private final Condition signalCondition;

    public FileHandler(FileQueue queue, String type, Lock lock, Condition condition) {
        this.queue = queue;
        this.handlerType = type;
        this.lock = lock;
        this.signalCondition = condition;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                while (queue.size() == 0) {
                    signalCondition.await();
                }
                File_ file = queue.peek();

                if (file.type.equals(handlerType)) {
                    int time = file.size * 7;
                    queue.remove();
                    Thread.sleep(time);
                    System.out.println("Поток " + Thread.currentThread().getName() + " Обработал файл размером " + file.size + " типа " + file.type + " за " + time + "мс");
                }
                signalCondition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}


class FileGenerator implements Runnable {
    private final String[] fileTypes;
    private final Random rand = new Random();
    private final FileQueue queue;
    private final Condition condition;

    private final Lock lock;


    public FileGenerator(FileQueue queue, Lock lock, Condition condition, String[] types) {
        this.queue = queue;
        this.lock = lock;
        this.condition = condition;
        this.fileTypes = types;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                while (queue.size() == 5) {
                    condition.await();
                }

                String type = fileTypes[rand.nextInt(3)];
                Integer size = rand.nextInt(10, 100);
                int time = rand.nextInt(100, 1000);

                File_ file = new File_(type,size);

                Thread.sleep(time);

                System.out.println("Поток " + Thread.currentThread().getName() + "Сгенерирован файл  " + file.type + " размером " + file.size + "за " + time + " секунд");


                queue.add(file);
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}