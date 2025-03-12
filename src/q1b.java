// This approach must use lock-free methods, by ensuring all array access appears atomic
// through the use of hardware primitives (CAS, TS, FA) from the java.util.concurrent.atomic
// package. There must be no blocking, and no data races.

// Your designs do not necessarily have to allow all n values to be accessed concurrently, but should allow
// multiple threads to concurrently read and write different array elements in O(1) while the array is not in
// the process of being resized. Resizing should of course not lose or corrupt the prior array state.

// Resizable thread safe array implementation
public class q1b {
    private Object[] arr;
    private int size;

    // Basic constructor
    public q1b() {
        size = 20;
        arr = new Object[size];
    }

    // Not a concurrent safe implementation
    public Object get(int i) {
        if(i == size)
            this.extend();
        return arr[i];
    }

    // Not a concurrent safe implementation
    public void set(int i, Object o) {
        if(i == size)
            this.extend();
        this.arr[i] = o;
    }

    // Not a concurrent safe implementation
    private void extend() {
        this.size+=10;
        Object[] new_arr = new Object[size];
        int i=0;
        for(Object o : arr)
            new_arr[i++] = o;
        this.arr = new_arr;
    }

    // Not a concurrent safe implementation
    public int getSize() { return size; }
}