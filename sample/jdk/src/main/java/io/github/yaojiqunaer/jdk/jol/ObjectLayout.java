package io.github.yaojiqunaer.jdk.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Java Object Layout (JOL) 示例类
 * <p>
 * JOL是用于分析JVM中对象内存布局的工具，主要包括以下功能：
 * 1. ClassLayout - 查看对象内部信息，包括对象头和字段布局
 * 2. GraphLayout - 查看对象外部信息，包括对象引用的其他对象
 * <p>
 * 对象内存布局通常包括：
 * - 对象头 (Object Header)：包含Mark Word和Klass Word
 * - 实例数据 (Instance Data)：对象中定义的字段
 * - 对齐填充 (Padding)：确保对象大小为8字节的倍数
 */
public class ObjectLayout {

    private String name;
    private int age;

    public static void main(String[] args) {
        // 1. 基本对象布局分析 - ClassLayout
        System.out.println("=== 基本对象布局分析 ===");
        ObjectLayout obj = new ObjectLayout();
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        System.out.println("=== 加锁 ===");
        synchronized (obj) {
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        }

        // 2. 设置对象哈希码后查看对象头变化
        System.out.println("=== 设置哈希码后的对象布局 ===");
        obj.hashCode(); // 触发生成哈希码
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        // 3. 数组对象布局分析
        System.out.println("=== 数组对象布局分析 ===");
        String[] array = new String[]{"a", "b", "c"};
        System.out.println(ClassLayout.parseInstance(array).toPrintable());

        // 4. 集合对象布局分析
        System.out.println("=== 集合对象布局分析 ===");
        List<String> list = new ArrayList<>();
        list.add("element1");
        list.add("element2");
        System.out.println(ClassLayout.parseInstance(list).toPrintable());

        // 5. 对象图布局分析 - GraphLayout
        System.out.println("=== 对象图布局分析（包含引用对象）===");
        System.out.println(GraphLayout.parseInstance(obj).toPrintable());

        // 6. 集合对象图布局分析
        System.out.println("=== 集合对象图布局分析 ===");
        System.out.println(GraphLayout.parseInstance(list).toPrintable());

        // 7. 获取对象的总内存占用
        System.out.println("=== 对象内存占用统计 ===");
        GraphLayout graph = GraphLayout.parseInstance(list);
        System.out.println("对象自身大小: " + ClassLayout.parseInstance(list).instanceSize() + " bytes");
        System.out.println("对象及其引用的总大小: " + graph.totalSize() + " bytes");
        System.out.println("引用的对象数量: " + graph.totalCount());

        // 8. 详细内存布局信息
        System.out.println("=== 详细内存布局信息 ===");
        System.out.println(graph.toPrintable());
    }
}