package task1;

import java.util.concurrent.RecursiveTask;

class RecurciveSum extends RecursiveTask<Integer> {
    int[] array;
    int start, end;

    public RecurciveSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= 1) {
            return array[start];
        } else {


            int mid = (start + end) / 2;

            RecurciveSum left = new RecurciveSum(array, start, mid);
            RecurciveSum right = new RecurciveSum(array, mid, end);

            left.fork();
            right.fork();


            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            return left.join() + right.join();
        }
    }
}
