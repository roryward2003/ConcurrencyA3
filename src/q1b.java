import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// This approach must use lock-free methods, by ensuring all array access appears atomic
// through the use of hardware primitives (CAS, TS, FA) from the java.util.concurrent.atomic
// package. There must be no blocking, and no data races.

// Your designs do not necessarily have to allow all n values to be accessed concurrently, but should allow
// multiple threads to concurrently read and write different array elements in O(1) while the array is not in
// the process of being resized. Resizing should of course not lose or corrupt the prior array state.

// Resizable thread safe array implementation
@SuppressWarnings("unchecked")
public class q1b {
    private AtomicReference<Object>[] arr;
    private AtomicInteger size;

    // Basic constructor
    public q1b() {
        size = new AtomicInteger(20);
        arr = (AtomicReference<Object>[]) new AtomicReference[size.get()];
        for(int i=0; i<size.get(); i++)
            arr[i] = new AtomicReference<Object>(null);
    }

    public Object get(int i) {
        if(arr.length==i && size.compareAndSet(i, i+10))
            this.extend();

        return arr[i].get();
    }

    public void set(int i, Object o) {
        if(arr.length==i && size.compareAndSet(i, i+10))
            this.extend();

        AtomicReference<Object> ref = arr[i];
        Object current = ref.get();
        while(!arr[i].compareAndSet(current, o))
            current = ref.get();
    }

    // This is thread safe, as the AtomicReferences themselves don't change, just the
    // data they reference. Copying over the pointers to these AtomicReferences into a
    // new array means that the new array will still update with any changes to the old
    // array as they reference the same AtomicReference object, which itself can reference
    // the new Object. I hope that makes sense :)
    private void extend() {
        AtomicReference<Object>[] new_arr = (AtomicReference<Object>[]) new AtomicReference[size.get()];
        int i=0;
        for(AtomicReference<Object> o : arr)
            new_arr[i++] = o;
        for(; i<size.get(); i++)
            new_arr[i] = new AtomicReference<Object>(null);
        arr = new_arr;
    }

    public int getSize() {
        while(!size.compareAndSet(arr.length, size.get()));
        return size.get();
    }
}