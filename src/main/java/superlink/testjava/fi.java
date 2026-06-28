package superlink.testjava;

import java.util.function.Function;

@FunctionalInterface
public interface fi<T, U, R> {

    R apply(T t, U u);


    default <V> fi<T, U, V> andThen(Function<? super R, ? extends V> after) {
        System.out.println("fi");
        return (T t, U u) -> {
            System.out.println("t");
            System.out.println(t);
            System.out.println(t);
            System.out.println("u");
            return after.apply(apply(t, u));
        };
    }
}
