package superlink.util.asynhandle;

@FunctionalInterface
public interface AsynHandler<V,E> {
    V call(E... v) throws Exception;
}
