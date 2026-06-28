package superlink.util.datastack;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayDeque;

public class ArrayQue <T>{
//    public ArrayQue(int capacity) {
//        this.capacity = capacity + 1;
//        this.queue = newArray(capacity + 1);
//        this.head = 0;
//        this.tail = 0;
//    }
    public ArrayQue(int capacity) {
        this.capacity = capacity ;
        this.queue = (T[]) new Object[capacity];
        this.head = 0;
        this.tail = 0;
    }

    public boolean add(T o) {
        queue[tail] = o;
        int newtail = (tail + 1) % capacity;
        if (newtail == head){
           head = (head + 1) % capacity;
        }
        tail = newtail;
        return true; // we did add something
    }
    public boolean adde(T o) {
        queue[tail] = o;
        int newtail = (tail + 1) % capacity;
        if (newtail == head){
            return false;
        }
        tail = newtail;
        return true; // we did add something
    }

    public T poll() {
        if (head == tail) {
            return null;
        }
        T removed = queue[head];
        queue[head] = null;
        head = (head + 1) % capacity;
        return removed;
    }

    public T get(int i) {
        int size = size();
        if (i < 0 || i >= size) {
            final String msg = "Index " + i + ", queue size " + size;
            throw new IndexOutOfBoundsException(msg);
        }
        int index = (head + i) % capacity;
        return queue[index];
    }

    public int size() {
        int diff = tail - head;
        if (diff < 0)
            diff += capacity;
        return diff;
    }

    public int capacity;
    public T[] queue;
    public int head;
    public int tail;
}
