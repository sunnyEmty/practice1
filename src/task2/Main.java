package task2;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main
{


    public static void main(String[] args) {
        ExecutorService exService = Executors.newFixedThreadPool(4);
        Scanner scan = new Scanner(System.in);
        int input1 = scan.nextInt();
        Future<?> feature = exService.submit(new RequestHandler(input1));

        while (true) {
            int input2 = scan.nextInt();
            if (!feature.isDone()) {
                exService.submit(new RequestHandler(input2));
            }
            break;
        }

        exService.shutdown();
        
    }
}

class RequestHandler implements Runnable {
    Integer value;
    public RequestHandler(int value) {
        this.value = value;
    }

    public void sleep(int min_time_msec, int max_time_msec) throws InterruptedException {
        long sleep_time = (long)(Math.random() * max_time_msec) + min_time_msec;
        Thread.sleep(sleep_time);
    }

    @Override
    public void run() {
        try {
            String threadName = Thread.currentThread().getName();

            System.out.println("Потоок " + threadName + " Ожидание ответа");

            sleep(1000, 5000);

            double result = Math.pow(value, 2);

            System.out.println("Потоок " + threadName + " " + Integer.toString(value) + "^2 = "
                    + Double.toString(result));


        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}