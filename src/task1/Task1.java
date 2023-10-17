package task1;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class Task1 {
    public static void printStats(long startTime, long startFreeMem) {
        System.out.println("Время " + Long.toString(System.currentTimeMillis() - startTime));
        System.out.println("Занимаемая память " + Long.toString(startFreeMem - Runtime.getRuntime().freeMemory()));
    }

    public static int continuouslySum(int[] arr, int size) throws InterruptedException {
        int sum = 0;
        long startTime = System.currentTimeMillis();
        long startFreeMem = Runtime.getRuntime().freeMemory();


        for (int i = 0; i < size; i++) {

            sum += arr[i];
            Thread.sleep(1);

        }

        System.out.println("Последовательная сумма " + Integer.toString(sum));
        printStats(startTime, startFreeMem);

        return sum;
    }

    public static int threadSum(int[] arr, int size) throws InterruptedException {
        int threadCount = 20;
        int sum = 0;
        long startTime = System.currentTimeMillis();
        long startFreeMem = Runtime.getRuntime().freeMemory();

        SumThread[] threads = new SumThread[threadCount];
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++){
            threads[i] = new SumThread(countDownLatch, Arrays.copyOfRange(arr, i * size / threadCount,
                    (i + 1) * size / threadCount));


        }
        for (int i = 0; i < threadCount; i++){
            threads[i].start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        for (int i = 0; i < threadCount; i++){
            sum += threads[i].getSum();


            Thread.sleep(1);
        }

        System.out.println("Cумма, полученная при помощи библиотеки Thread " + Integer.toString(sum));
        printStats(startTime, startFreeMem);

        return sum;
    }


    public static int forkSum (int[] arr, int size) {

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        long startFreeMem = Runtime.getRuntime().freeMemory();

        RecurciveSum recSum  = new RecurciveSum(arr, 0, arr.length);


        int sum = pool.invoke(recSum);

        System.out.println("Cумма, полученная при помощи ForkJoin " + Integer.toString(sum));
        printStats(startTime, startFreeMem);

        return sum;
    }


    public static void runTests() {
        int size = 20;
        int[] arr = new int[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt();

        }

        try {
            continuouslySum(arr, size);
            threadSum(arr, size);
            forkSum(arr, size);
        } catch (InterruptedException e){
            e.printStackTrace();
        }


    }

}



