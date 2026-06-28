package superlink.util.thread;

@FunctionalInterface
public interface ThreadInterface<E,V,T> {

//    public ThreadFunction f = null;

    ThreadFunction create(ThreadFunction<? super T> function);

    default E start(ThreadFunction<E> function) {
        return function.start();
    }

}
