package io.github.yaojiqunaer.gc;


import java.util.ArrayList;
import java.util.List;

public class HeapOutOfMemoryTest {

    /**
     * VM Options:
     * -Xmx10m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/xiaodongzhang/Downloads
     */
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        while (true) {
            list.add("北京");
            Thread.sleep(0, 100000);
        }
    }

}
