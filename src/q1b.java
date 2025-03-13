import java.util.concurrent.atomic.AtomicReference;

// Resizable thread safe array implementation, using only lock-free methods.

// For clarity: arrRef is an atomic reference to an array of atomic references,
// each of which references an Object. This allows reads and writes of
// individual elements, and also allows the array resizing, to appear atomic.

@SuppressWarnings("unchecked")
public class q1b {

    // Private data
    private AtomicReference<AtomicReference<Object>[]> arrRef;

    // Constructor creates an array of atomic references to objects, and
    // stores an atomic reference to this array in the arrRef variable.
    public q1b() {

        arrRef = new AtomicReference<AtomicReference<Object>[]>();
        arrRef.set(new AtomicReference[20]);
        for(int i=0; i<20; i++)
            arrRef.get()[i] = new AtomicReference<Object>(null);
    }

    // Get object from index i, extending if i is one beyond the array limit
    public Object get(int i) {

        // Extend the array in a way that appears atomic. If another thread has extended
        // in between then this overlapped and unnecessary extension is abandoned.
        if(arrRef.get().length == i) {
            AtomicReference<Object>[] expected = arrRef.get();
            arrRef.compareAndSet(expected, extend(expected));
        }
        return arrRef.get()[i].get();
    }

    // Set an object at index i, extending if i is one beyond the array limit
    public void set(int i, Object o) {

        // Extend the array in a way that appears atomic. If another thread has extended
        // in between then this overlapped and unnecessary extension is abandoned.
        if(arrRef.get().length == i) {
            AtomicReference<Object>[] expected = arrRef.get();
            arrRef.compareAndSet(expected, extend(expected));
        }

        // Set o at index i using a CAS operation
        AtomicReference<Object> objRef = arrRef.get()[i];
        Object current = objRef.get();
        while(!objRef.compareAndSet(current, o))
            current = objRef.get();
    }

    // Return an extended copy of the old array. This extended copy is a new array, containing
    // pointers to the same object references as the old array. Because the references themselves
    // are the same, updates that occur during resizing will still be reflected in the new array.
    //
    // Importantly, the caller will decide if this extension is valid or not using a CAS operation,
    // which is what allows this entire array resizing process to appear atomic to all other threads.
    private AtomicReference<Object>[] extend(AtomicReference<Object>[] expected) {

        // Create the new array
        AtomicReference<Object>[] new_arr = (AtomicReference<Object>[]) new AtomicReference[expected.length+10];
        int i=0;
        for(AtomicReference<Object> o : expected) // Copy over the old array's contents
            new_arr[i++] = o;
        for(; i<expected.length+10; i++)          // Pad the rest with new references to null objects
            new_arr[i] = new AtomicReference<Object>(null);
        return new_arr;                           // Return the new extended array
    }

    // Get the current array size (for the q1.java driver program to use)
    public int getSize() { return arrRef.get().length; }
}