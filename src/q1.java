import java.util.concurrent.ThreadLocalRandom;

// Driver class for testing my q1a and q1b implementations

public class q1 {

    public static void main(String[] args) {

        // Local vars for timing and input params
        long timeBefore, timeAfter;
        int k = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);

        // Initialise two four-thread arrays, to test q1a and q1b respectively
        Thread[] tA = new Thread[4];
        Thread[] tB = new Thread[4];
        q1aTester a = new q1aTester(new q1a(), k, m);
        q1bTester b = new q1bTester(new q1b(), k, m);
        for(int i=0; i<4; i++) {
            tA[i] = new Thread(a);
            tB[i] = new Thread(b);
        }

        // Time the execution of all threads in tA
        timeBefore = System.currentTimeMillis();
        for(Thread t : tA)
            t.run();
        timeAfter = System.currentTimeMillis();
        System.out.println("q1a execution time: "+(timeAfter-timeBefore)+"ms");
        
        // Time the execution of all threads in tB
        timeBefore = System.currentTimeMillis();
        for(Thread t : tB)
            t.run();
        timeAfter = System.currentTimeMillis();
        System.out.println("q1b execution time: "+(timeAfter-timeBefore)+"ms");
    }   
}

// This class tests a q1a resizable array implementation
class q1aTester implements Runnable {
    
    // Private variables
    private q1a arr;
    private ThreadLocalRandom rng;
    private int k;
    private int m;

    // Basic constructor with shared q1a reference
    public q1aTester(q1a arr, int k, int m) {
        this.arr = arr;
        this.rng = ThreadLocalRandom.current();
        this.k = k;
        this.m = m;
    }

    // Threads constructed using this runnable implementation will simulate q1a usage as below
    @Override
    public void run() {
        for(int i=0; i<m; i++) {
            if(rng.nextInt(100) >= k) {      // Access any normal part of the array
                if(rng.nextInt(2) == 0)
                    synchronized(this) { arr.set(rng.nextInt(arr.getSize()), new Object()); }
                else
                    synchronized(this) { arr.get(rng.nextInt(arr.getSize())); }
            } else {                               // Access one past the end of the array
                if(rng.nextInt(2) == 0)
                    synchronized(this) { arr.set(arr.getSize(), new Object()); }
                else
                    synchronized(this) { arr.get(arr.getSize()); }
            }
        }
    }
}

// This class tests a q1b resizable array implementation
class q1bTester implements Runnable {

    // Private variables
    private q1b arr;
    private ThreadLocalRandom rng;
    private int k;
    private int m;

    // Basic constructor with shared q1b reference
    public q1bTester(q1b arr, int k, int m) {
        this.arr = arr;
        this.rng = ThreadLocalRandom.current();
        this.k = k;
        this.m = m;
    }

    // Threads constructed using this runnable implementation will simulate q1b usage as below
    @Override
    public void run() {
        for(int i=0; i<m; i++) {
            if(rng.nextInt(100) >= k) {      // Access any normal part of the array
                if(rng.nextInt(2) == 0)
                    arr.set(rng.nextInt(arr.getSize()), new Object());
                else
                    arr.get(rng.nextInt(arr.getSize()));
            } else {                               // Access one past the end of the array
                if(rng.nextInt(2) == 0)
                    arr.set(arr.getSize(), new Object());
                else
                    arr.get(arr.getSize());
            }
        }
    }
}