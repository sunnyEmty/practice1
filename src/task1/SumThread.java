package task1;

import java.util.concurrent.CountDownLatch;

class SumThread extends Thread {
    public int[] arr;
    public int sum;
    CountDownLatch latch;
    public SumThread(CountDownLatch latch, int[] arr) {
        this.arr = arr;
        this.sum = 0;
        this.latch = latch;
    }

    public int getSum() {
        return sum;
    }
    @Override
    public void run() {
        int z = 0;
        for (int num : arr) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sum += num;

        }
        latch.countDown();
    }
}
