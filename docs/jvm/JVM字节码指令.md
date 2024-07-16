
# JVM 字节码

## 字节码

### 什么是字节码

Java之所以可以“一次编译，到处运行”，一是因为JVM针对各种操作系统、平台都进行了定制，二是因为无论在什么平台，都可以编译生成固定格式的字节码（.class文件）供JVM使用。因此，也可以看出字节码对于Java生态的重要性。之所以被称之为字节码，是因为字节码文件由十六进制值组成，而JVM以两个十六进制值为一组，即以字节为单位进行读取。在Java中一般是用javac命令编译源代码为字节码文件，一个.java文件从编译到运行的示例如图所示。

### Class文件结构

字节码（.class）文件由一组十六进制数组成，JVM规定一个class文件需要按照固定的十个部分顺序排列组成。这十个部分按顺序为：魔数、版本号、常量池、访问标志、当前类索引、父类索引、接口表、字段表、方法表、附加属性。

```java
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
```

#### 魔数（Magic Number）

class文件的开头为4个字节的十六进制数 `CAFEBABE` ，这是class文件的标识，由Java之父制定

#### 版本号（Version）

版本号接着魔术，由4个字节组成，前、后各2个字节分别代表次版本号（Minor Version）、主版本号（Major Version），如`00 00 00 3D` 表示的主版本为61次版本为0，可查询官网知道其对应的JDK版本为17

参考主版本映射关系：[**`class` file format major versionsz'z**](https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1)

#### 常量池（Constant Pool）

常量池主要存储两类常量：字面量和符号引用；其中，字面量`final`修饰或者字符串的常量值，符号引用包括了类的全限定名、字段名称和描述符、方法名称和描述符等。

常量池由常量池计数器和常量池数据组成：

- constant_pool_count: 常量池计数器，占两个字节，表示常量池数据的个数
- constant pool[constant_pool_count-1]: 常量池数据区，由(constant_pool_count-1)个cp_info组成

cp_info的一共有14种类型，每种类型的结构固定，这里仅作举例说明

`final String DEFAULT_NAME = "zhangshan";`

```tex
CONSTANT_String_info {
    u1 tag; // 表示类型，tag = 8，占用一个字节
    u2 string_index; // 表示字面量"zhangshan"的引用，占用两个字节
}
```

#### 访问标志（Access Flag）

常量池后接着访问标志，共占用两个字节，用于描述class是接口还是类，以及是否被public、final、abstract等修饰。JVM并没有穷举所有的访问标志，而是使用按位或操作来进行描述的，比如某个类的修饰符为Public Final，则对应的访问修饰符的值为`ACC_PUBLIC` | `ACC_FINAL`，即0x0001 | 0x0010=0x0011。

| Flag Name        | Value  | Description                                                  |
| ---------------- | ------ | ------------------------------------------------------------ |
| `ACC_PUBLIC`     | 0x0001 | Declared `public`; may be accessed from outside its package. |
| `ACC_FINAL`      | 0x0010 | Declared `final`; no subclasses allowed.                     |
| `ACC_SUPER`      | 0x0020 | Treat superclass methods specially when invoked by the *invokespecial* instruction. |
| `ACC_INTERFACE`  | 0x0200 | Is an interface, not a class.                                |
| `ACC_ABSTRACT`   | 0x0400 | Declared `abstract`; must not be instantiated.               |
| `ACC_SYNTHETIC`  | 0x1000 | Declared synthetic; not present in the source code.          |
| `ACC_ANNOTATION` | 0x2000 | Declared as an annotation interface.                         |
| `ACC_ENUM`       | 0x4000 | Declared as an `enum` class.                                 |
| `ACC_MODULE`     | 0x8000 | Is a module, not a class or interface.                       |

#### 当前类名（This Class）

占用两个字节，描述当前类的全限定名。保存的值为常量池中的索引值，根据索引值能在常量池中找到这个类的全限定名。

#### 超类类名（Super Class）

占用两个字节，描述父类的全限定名。保存的值为常量池中的索引值，根据索引值能在常量池中找到父类的全限定名。

#### 接口表（Interface）

包含接口计数器和接口表两个部分，描述类及父类的接口信息。

- interfaces_count：共两个字节，描述class及父类实现的接口数量；
- interfaces[interfaces_count]：保存了每一个Interface在常量池中的索引值。

#### 字段表（Fields）

包含字段计数器和字段表两个部分，描述类变量和成员变量，但不包括局部变量。

- fields_count：共两个字节，描述字段的数量；
- fields[fields_count]：保存每个字段的元数据，field_info=权限修饰符（2 字节）+字段名索引（2 字节）+描述符索引（2 字节）+属性个数（2字节）+属性列表（n 字节）。

#### 方法表（Methods）

包含方法计数器和方法表两个部分，描述每个方法的详细信息。

- methods_count：共两个字节，描述方法的数量；
- methods[methods_count]：保存每个方法的详细信息，method_info=权限修饰符（2 字节）+方法名索引（2 字节）+描述符索引（2 字节）+属性个数（2字节）+属性列表（n 字节）。

上述的属性列表包括：

1. "Code区"：源代码对应的**JVM指令操作码**
2. "LineNumberTable"：行号表，将Code区的操作码和源代码中的行号对应，Debug时会起到作用（源代码走一行，需要走多少个JVM指令操作码）。
3. "LocalVariableTable"：本地变量表，包含This和局部变量，之所以可以在每一个方法内部都可以调用This，是因为JVM将This作为每一个方法的第一个参数隐式进行传入。当然，这是针对非Static方法而言。

#### 属性表（Attributes）

包含属性计数器和属性表两个部分，描述类或接口定义的属性信息。

attributes_count：共两个字节，描述属性的数量；

attributes[attributes_count]：每个attribute的结构固定

```tex
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
```



## 字节码指令

## 源码案例

### 对象的创建

## 参考文献

1. https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html