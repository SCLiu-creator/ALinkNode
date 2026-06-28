package superlink.util.datastack;

public class LinkQue<T> {
    // 定义单向链表节点
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head; // 队列头节点（用于poll）
    private Node<T> tail; // 队列尾节点（用于add）
    private int size;     // 队列大小

    // 构造函数
    public LinkQue() {
        head = null;
        tail = null;
        size = 0;
    }

    // 在队列尾部插入元素
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // 从队列头部移除并返回元素
    public T poll() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null; // 队列为空时，尾节点也置为null
        }
        size--;
        return data;
    }

    // 获取队列大小
    public int size() {
        return size;
    }

    // 判断队列是否为空
    public boolean isEmpty() {
        return size == 0;
    }

}
