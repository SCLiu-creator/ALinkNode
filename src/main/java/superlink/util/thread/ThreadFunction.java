package superlink.util.thread;

@FunctionalInterface
public interface ThreadFunction<E> {
//    public AtomicReference ref = new AtomicReference<>();

    E start();

}
