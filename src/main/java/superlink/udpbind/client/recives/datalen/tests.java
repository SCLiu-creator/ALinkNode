package superlink.udpbind.client.recives.datalen;

import java.util.Random;
import java.util.zip.CRC32;

public class tests {

    public static void main(String[] args) {
        int dataSize = 1024 * 100; // 1MB 数据
        byte[] data = generateRandomData(dataSize);

        // 测试 CRC32 计算性能
        long crc32Time = testCrc32Performance(data);
        System.out.println("CRC32 计算时间: " + crc32Time + " ms");

        // 测试奇偶校验计算性能
        long parityTime = testParityPerformance(data);
        System.out.println("奇偶校验计算时间: " + parityTime + " ms");

        // 测试错误检测能力
        testErrorDetection(data);
    }

    // 生成随机数据
    private static byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        new Random().nextBytes(data);
        return data;
    }

    // 测试 CRC32 计算性能
    private static long testCrc32Performance(byte[] data) {
        long startTime = System.nanoTime();
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        long checksum = crc32.getValue(); // 计算 CRC32 值
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    // 测试奇偶校验计算性能
    private static long testParityPerformance(byte[] data) {
        long startTime = System.nanoTime();
        int parity = 0;
        for (byte b : data) {
            parity ^= b; // 计算奇偶校验（异或所有字节）
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    // 测试错误检测能力
    private static void testErrorDetection(byte[] originalData) {
        // 计算原始数据的 CRC32 和奇偶校验值
        CRC32 crc32 = new CRC32();
        crc32.update(originalData);
        long originalCrc32 = crc32.getValue();

        int originalParity = 0;
        for (byte b : originalData) {
            originalParity ^= b;
        }

        // 模拟数据错误（翻转一个比特）
        byte[] corruptedData = originalData.clone();
        corruptedData[corruptedData.length / 2] ^= 0x01; // 翻转中间字节的最低位

        // 检测 CRC32
        crc32.reset();
        crc32.update(corruptedData);
        long corruptedCrc32 = crc32.getValue();
        boolean crc32Detected = (corruptedCrc32 != originalCrc32);

        // 检测奇偶校验
        int corruptedParity = 0;
        for (byte b : corruptedData) {
            corruptedParity ^= b;
        }
        boolean parityDetected = (corruptedParity != originalParity);

        System.out.println("\n错误检测测试（翻转 1 比特）：");
        System.out.println("CRC32 是否检测到错误: " + crc32Detected);
        System.out.println("奇偶校验是否检测到错误: " + parityDetected);

        // 模拟更严重的错误（翻转多个比特）
        corruptedData = originalData.clone();
        corruptedData[corruptedData.length / 4] ^= 0xFF; // 翻转一个字节
        corruptedData[corruptedData.length / 3] ^= 0x0F; // 翻转另一个字节的部分比特

        // 检测 CRC32
        crc32.reset();
        crc32.update(corruptedData);
        corruptedCrc32 = crc32.getValue();
        crc32Detected = (corruptedCrc32 != originalCrc32);

        // 检测奇偶校验
        corruptedParity = 0;
        for (byte b : corruptedData) {
            corruptedParity ^= b;
        }
        parityDetected = (corruptedParity != originalParity);

        System.out.println("\n错误检测测试（翻转多个比特）：");
        System.out.println("CRC32 是否检测到错误: " + crc32Detected);
        System.out.println("奇偶校验是否检测到错误: " + parityDetected);
    }
}