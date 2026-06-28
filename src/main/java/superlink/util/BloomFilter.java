package superlink.util;

import java.util.BitSet;
import java.util.Random;

public class BloomFilter {
    /**
     * 位数组，用于存储布隆过滤器的状态
     */
    private BitSet bitSet;
    /**
     * 位数组的长度
     */
    private int bitSetSize;
    /**
     * 预期元素数量
     */
    private int expectedNumberOfElements;
    /**
     * 哈希函数数量
     */
    private int numberOfHashFunctions;
    /**
     *  用于生成哈希种子的伪随机数生成器
     */
    private Random random = new Random();

    public BloomFilter(int bitSetSize, int expectedNumberOfElements) {
        this.bitSetSize = bitSetSize;
        this.expectedNumberOfElements = expectedNumberOfElements;

        // 根据公式计算哈希函数数量
        this.numberOfHashFunctions = (int) Math.round((bitSetSize / expectedNumberOfElements) * Math.log(2.0));

        // 创建位数组并初始化所有位为0
        this.bitSet = new BitSet(bitSetSize);
    }

    public void add(Object element) {
        // 对元素进行多次哈希，并将对应的位设置为1
        for (int i = 0; i < numberOfHashFunctions; i++) {
            long hash = computeHash(element.toString(), i);
            int index = getIndex(hash);
            bitSet.set(index, true);
        }
    }

    public boolean contains(Object element) {
        // 对元素进行多次哈希，并检查所有哈希值所对应的位是否都被设置为1
        for (int i = 0; i < numberOfHashFunctions; i++) {
            long hash = computeHash(element.toString(), i);
            int index = getIndex(hash);

            if (!bitSet.get(index)) {
                return false;
            }
        }

        return true;
    }

    private int getIndex(long hash) {
        // 将哈希值映射到位数组的下标（需要确保下标非负）
        return Math.abs((int) (hash % bitSetSize));
    }

    private long computeHash(String element, int seed) {
        // 使用伪随机数生成器生成不同的哈希种子
        random.setSeed(seed);

        // 将元素转换为字节数组，并计算其哈希值
        byte[] data = element.getBytes();
        long hash = 0x7f52bed27117b5efL;

        for (byte b : data) {
            hash ^= random.nextInt();
            hash *= 0xcbf29ce484222325L;
            hash ^= b;
        }

        return hash;
    }

}
