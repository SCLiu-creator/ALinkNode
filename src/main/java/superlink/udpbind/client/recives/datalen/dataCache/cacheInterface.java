package superlink.udpbind.client.recives.datalen.dataCache;

public interface cacheInterface<E,T,V> {

    V get(String s) ;

    void set(E e,T s) ;

    String set(byte[] b) ;

}
