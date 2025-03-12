// Synchronizing on the array as a whole would prevent concurrent access to the array,
// even when reading or writing to different array elements. This would be overkill.
// Overall approach is to prevent concurrent access to the same element by multiple
// threads, whilst using o(n) data. (number of pieces of data <= n-1).

// Your designs do not necessarily have to allow all n values to be accessed concurrently, but should allow
// multiple threads to concurrently read and write different array elements in O(1) while the array is not in
// the process of being resized. Resizing should of course not lose or corrupt the prior array state.

// Resizable thread safe array implementation
public class q1a {
    private Object[] arr;
    private int size;

    // Basic constructor
    public q1a() {
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