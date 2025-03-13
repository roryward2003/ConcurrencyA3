// Synchronizing on the array as a whole would prevent concurrent access to the array,
// even when reading or writing to different array elements. This would be overkill.
// Overall approach is to prevent concurrent access to the same element by multiple
// threads, whilst using o(n) data. (number of pieces of data <= n-1).

// Your designs do not necessarily have to allow all n values to be accessed concurrently, but should allow
// multiple threads to concurrently read and write different array elements in O(1) while the array is not in
// the process of being resized. Resizing should of course not lose or corrupt the prior array state.

// Imports
import java.util.concurrent.locks.ReentrantLock;

// NB I don't actually think this is o(n)
// It is in fact not!
// Going to aim for O(log(n)) memory usage, by chunking in 2,4,8,16 etc.

// My current design uses n/2 locks for an n length array. Each lock prevents concurrent acces to
// a pair of elements. This ensures that each element can only be accessed by one thread at a time
// but has the side effect that any even index, and the element one above it, cannot be accessed at
// the same time. e.g. [0] and [1] cannot be accessed simultaneously.

// Resizable thread safe array implementation
public class q1a {
    private Object[] arr;
    private ReentrantLock[] locks;
    private int size;

    public q1a() {
        size = 20;
        arr = new Object[size];
        locks = new ReentrantLock[size/2];
        for(int i=0; i<size/2; i++)
            locks[i] = new ReentrantLock();
    }

    public Object get(int i) {
        Object o;
        if(i == size)
            this.extend();

        locks[i/2].lock();
        try {
            o = arr[i];
            return o;
        } finally {
            locks[i/2].unlock();
        }
    }

    public void set(int i, Object o) {
        if(i == size)
            this.extend();
        
        locks[i/2].lock();
        try {
            this.arr[i] = o;
        } finally {
            locks[i/2].unlock();
        }
    }

    private void extend() {
        // Acquire all locks in ascending order
        for(ReentrantLock l : locks) {
            l.lock();
        }
        this.size+=10;
        Object[] new_arr = new Object[size];
        ReentrantLock[] new_locks = new ReentrantLock[size/2];

        // Extend the array and lock array, copying all data over. Pointers to
        // the old locks are passed to the new lock array, so that waiting threads
        // don't get lost
        try {
            int i=0;
            for(Object o : arr) {
                new_arr[i] = o;
                if(i%2==0)
                    new_locks[i/2] = locks[i/2];
                i++;
            }
            for(;i<size;i+=2)
                new_locks[i/2] = new ReentrantLock();
            this.arr = new_arr;
            this.locks = new_locks;
        } 
        
        finally { // Release all old locks in descending order
            for(int i=(size/2)-6; i>=0; i--)
                locks[i].unlock();
        }
    }

    public int getSize() { return size; }
}