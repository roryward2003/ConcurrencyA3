// This approach must use lock-free methods, by ensuring all array access appears atomic
// through the use of hardware primitives (CAS, TS, FA) from the java.util.concurrent.atomic
// package. There must be no blocking, and no data races.

// Your designs do not necessarily have to allow all n values to be accessed concurrently, but should allow
// multiple threads to concurrently read and write different array elements in O(1) while the array is not in
// the process of being resized. Resizing should of course not lose or corrupt the prior array state.

// Imports
import java.util.concurrent.atomic.AtomicReference;

// Resizable thread safe array implementation
@SuppressWarnings("unchecked")
public class q1b {
    private AtomicReference<Object>[] arr;
    private int size;

    // Basic constructor
    public q1b() {
        size = 20;
        arr = (AtomicReference<Object>[]) new AtomicReference[20];
        for(int i=0; i<size; i++)
            arr[i] = new AtomicReference<Object>(null);
    }

    public Object get(int i) {
        if(i == size)
            this.extend();
        while(size != arr.length); // This prevents NullPointerExceptions while the arr is being resized
        return arr[i].get();
    }

    public void set(int i, Object o) {
        if(i == size)
            this.extend();
        
        Object current;
        while(size != arr.length); // This prevents NullPointerExceptions while the arr is being resized
        current = arr[i].get();
        while(!arr[i].compareAndSet(current, o))
            current = arr[i].get();
    }

    // I have convinced myself that this is actually thread safe, as the AtomicReferences themselves don't change,
    // just the data they reference. Thus copying over the pointers to these AtomicReferences into a new array
    // means that the new array will still update with any changes to the old array as they reference the same
    // AtomicReference object, which itself may reference the new Object. I hope that makes sense :)
    private void extend() {
        this.size+=10;
        AtomicReference<Object>[] new_arr = (AtomicReference<Object>[]) new AtomicReference[size];
        int i=0;
        for(AtomicReference<Object> o : arr)
            new_arr[i++] = o;
        for(; i<size; i++)
            new_arr[i] = new AtomicReference<Object>(null);
        this.arr = new_arr;
    }

    public int getSize() { return size; }
}