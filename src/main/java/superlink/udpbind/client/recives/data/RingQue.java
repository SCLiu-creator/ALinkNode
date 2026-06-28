package superlink.udpbind.client.recives.data;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RingQue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    Node<E>[] arrayList;
    AtomicInteger atomicInteger=new AtomicInteger(0);
    public int cap;

    public RingQue(int size){
        arrayList=new Node[size];
        Node node=new Node();

        Node hand=node;
        arrayList[size-1]=hand;
        size--;
        for (int i=0;size>0;size--,i++){

            Node node1=new Node();
            node.setTail(node1);
            node1.setHand(node);
            node=node1;
            arrayList[size-1]=node;
        }
        node.setTail(hand);
        hand.setHand(node);
        this.wnode=hand;
        this.rnode=hand;
        cap=size;
    }

    private Node<E> wnode;
    private Node<E> rnode;
    private ReentrantLock w=new ReentrantLock();
    private ReentrantLock r=new ReentrantLock();


    private void unWsignal(){
        ReentrantLock reentrantLock=wnode.inlock;
        Condition condition=wnode.tocondition;
        reentrantLock.lock();
        try {
            condition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }

        reentrantLock.unlock();
    }
    private void unRsignal(){
        ReentrantLock reentrantLock=rnode.tolock;
        Condition condition=rnode.tocondition;
        reentrantLock.lock();
        try {
            condition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }
        reentrantLock.unlock();
    }
    private  void setNextWonde(){
        w.lock();
        try {
            wnode=wnode.getTail();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            w.getQueueLength();
            w.unlock();
        }

    }
    private  void setNextRonde(){
        r.lock();
        try {
            rnode=rnode.getTail();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            r.getQueueLength();
            r.unlock();
        }
    }

    @Override
    public synchronized boolean add(E o) {
        wnode.setValue(o);
        wnode=wnode.getTail();
        return true;
    }

    @Override
    public boolean offer(E o) {
         ReentrantLock inlock=wnode.inlock;
//        final Condition condition=wnode.incondition;
        inlock.lock();
        try {

            while (wnode.getValue()!=null ){//||wnode.equals(rnode)
                unRsignal();
                wnode.incondition.await();
            }
            wnode.setValue(o);
            setNextWonde();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inlock.unlock();
        }
        unWsignal();
        return false;
    }

    @Override
    public E remove() {
        rnode.setValue(null);
        setNextRonde();
        atomicInteger.decrementAndGet();
        return null;
    }

    @Override
    public E poll() {
        Object o=rnode.getValue();
        if (o==null){
            return null;
        }
        rnode.value=null;
        atomicInteger.decrementAndGet();
        setNextRonde();
        return (E) o;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public void put(E o) throws InterruptedException {
        final ReentrantLock inlock=wnode.inlock;
        final Condition condition=wnode.incondition;
        inlock.lockInterruptibly();
        try {

            while (wnode.getValue()!=null ){//||wnode.equals(rnode)
                unRsignal();
                condition.await();
            }

            wnode.value=o;
            atomicInteger.getAndIncrement();
            setNextWonde();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inlock.unlock();
        }
        unWsignal();
    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        ReentrantLock tolock=rnode.tolock;
        Object o=null;
        tolock.lock();
        try {
             o=rnode.value;
             while (o==null){//&&rnode.equals(wnode)||rnode.equals(wnode)
                 if (rnode.tail.value!=null){

                 }
                 unWsignal();
                 rnode.tocondition.await();
                 o=rnode.value;
             }
            atomicInteger.decrementAndGet();
             rnode.value=null;
            setNextRonde();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            tolock.unlock();
        }
        if (rnode.hand==wnode){//
            if (wnode.inlock.getQueueLength()!=0){
                ReentrantLock wlock=wnode.inlock;
                Condition condition=wnode.incondition;
                wlock.lock();
                try {
                    condition.signal();
                }catch (Exception e){
                    e.printStackTrace();
                }
                wlock.unlock();
            }
        }
//        new LinkedBlockingDeque().take()
        unRsignal();
        return (E) o;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        Object o=null;
        rnode.inlock.lock();
        try {
            o=rnode.getValue();
            while (o==null||rnode.equals(wnode)){//&&rnode.equals(wnode)
                rnode.tocondition.await(timeout,unit);
            }
            rnode.value=null;
            atomicInteger.decrementAndGet();
            setNextRonde();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rnode.inlock.unlock();
        }

//        new LinkedBlockingDeque().take()
        return (E) o;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public synchronized boolean remove(Object o) {
        int z=size();
        List list=new ArrayList();
        Node node=null;
        Object o1;
        for (int i = 0; i <z ; i++) {
            o1=poll();
            if (o1!=null&& !o1.equals(o)){
                list.add(o1);
            }
        }

        for (int i = 0; i <list.size() ; i++) {
            add((E) list.get(i));
        }
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {
        for (Node node:arrayList){
            node.value=null;
        }
    }

    @Override
    @Deprecated
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    @Deprecated
    public boolean removeAll(Collection c) {

        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object n:c){
            E e=null;
            for (Node<E> node:arrayList){
                if (n.equals(node.value)){
                    e=node.value;
                }
                node.value=null;
            }
            if (e==null){
                return false;
            }
        }
        return true;
    }

    @Override
    public int size() {
        return atomicInteger.get();
    }

    @Override
    public boolean isEmpty() {
        if (atomicInteger.get()==0){
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        for (Node<E> node:arrayList){
            if (o.equals(node.value)){
                return true;
            }
//            node.value=null;
        }
        return false;
    }

    @Override
    @Deprecated
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        Object[] objects=new Object[arrayList.length];
        for (int i = 0; i <arrayList.length ; i++) {
            objects[i]=arrayList[i].getValue();
        }
        return objects;
    }

    public List<E> toList() {
        ArrayList<E> objects=new ArrayList(arrayList.length);
        Node<E> node = wnode;
        for (int i = 0; i <arrayList.length ; i++) {
            E data=node.value;
            if (data!=null){
                objects.add(data);
            }
            node=node.tail;
        }
        return objects;
    }
    @Override
    @Deprecated
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    @Override
    @Deprecated
    public int drainTo(Collection c) {
        return 0;
    }

    @Override
    @Deprecated
    public int drainTo(Collection c, int maxElements) {
        return 0;
    }

    public class Node<E>{

//        ReentrantLock inlock=new ReentrantLock();
//        Condition incondition=inlock.newCondition();
//        ReentrantLock tolock=new ReentrantLock();
//        Condition tocondition=tolock.newCondition();
        ReentrantLock inlock;
        Condition incondition;//=inlock.newCondition();
        ReentrantLock tolock;//=new ReentrantLock();
        Condition tocondition;//=tolock.newCondition();
        {
            inlock=new ReentrantLock();
            incondition=inlock.newCondition();
            tolock=inlock;
            tocondition=incondition;
        }
        Node<E> hand;
        E value;
        Node<E> tail;
        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            if (this.value==null){
                this.value = value;
                atomicInteger.getAndIncrement();
            }else {
                this.value = value;
            }

        }

        public Node getHand() {
            return hand;
        }

        public void setHand(Node hand) {
            this.hand = hand;
        }

        public Node<E> getTail() {
            return tail;
        }

        public void setTail(Node tail) {
            this.tail = tail;
        }

        @Override
        public int hashCode(){
            return value.hashCode();
        }
        @Override
        public boolean equals(Object o){
            return value.equals(o)?true:false;
        }

    }
}
