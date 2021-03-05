一：JVM

## 1：什么是JVM

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\JVM.png)

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\jvm-paramselect.jpg)

## 2：JRE/JDK/JVM是什么关系

JRE(JavaRuntimeEnvironment，Java运行环境)，也就是Java平台。所有的Java 程序都要在JRE下才能运行。普通用户只需要运行已开发好的java程序，安装JRE即可。

JDK(Java Development Kit)是程序开发者用来来编译、调试java程序用的开发工具包。JDK的工具也是Java程序，也需要JRE才能运行。为了保持JDK的独立性和完整性，在JDK的安装过程中，JRE也是 安装的一部分。所以，在JDK的安装目录下有一个名为jre的目录，用于存放JRE文件。

JVM(JavaVirtualMachine，Java虚拟机)是JRE的一部分。它是一个虚构出来的计算机，是通过在实际的计算机上仿真模拟各种计算机功能来实现的。JVM有自己完善的硬件架构，如处理器、堆栈、寄存器等，还具有相应的指令系统。Java语言最重要的特点就是跨平台运行。使用JVM就是为了支持与操作系统无关，实现跨平台。

## 3：JVM原理

JVM是java的核心和基础，在java编译器和os平台之间的虚拟处理器。它是一种利用软件方法实现的抽象的计算机基于下层的操作系统和硬件平台，可以在上面执行java的字节码程序。

java编译器只要面向JVM，生成JVM能理解的代码或字节码文件。Java源文件经编译成字节码程序，通过JVM将每一条指令翻译成不同平台机器码，通过特定平台运行。

#### 3.1类加载

#####    3.1.1 类加载的条件

-  通过new，克隆，反序列化 ，发射创建对象实例时
-  调用类的静态变量时
-  使用类或者接口的静态字段
-  初始化子类时先初始化父类
-  作为启动虚拟机作为main方法的那个类

#####   3.1.2 虚拟机加载的过程

-    通过类的全限定名，
-    解析类的二进制流为方法区的数据结构
-    创建java.lang.Class类的实例，表示改类型

#####   3.1.3 一切Class 都从ClassLoad开始

ClassLoad负责通过各种方式将Class信息的二进制数据流读入系统，

##### 补充：Class.forName()和ClassLoad()区别？

ClassLoad()：就是遵循双亲委派模式，最终调用的是启动类加载器的类加载器，通过类的全限定名来获取此类的二进制流，然后放到JVM中。只干一件事情，就是将.class文件加载到jvm中，不会执行static中的内容，只有在newInstance才会去执行static块。
Class.forName()：除了将类的.class文件加载到jvm中之外，还会对类进行解释，执行类中的static块。实际上是调用ClassLoad来实现的，

```java
    @CallerSensitive
    public static Class<?> forName(String className)
                throws ClassNotFoundException {
        Class<?> caller = Reflection.getCallerClass();
        return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
    }


 /** Called after security check for system loader access checks have been made.
 *   forName0 方法
 */
    private static native Class<?> forName0(String name, boolean initialize,
                                            ClassLoader loader,
                                            Class<?> caller)
        throws ClassNotFoundException;

//注意：第二个参数表示是否对加载的类进行初始化，若设置为true,表示在是初始化执行的时候，会执行类中的静态的代码块，以及对静态变量的赋值操作。
```

Class.forName(className)方法，内部实际调用的方法是  Class.forName(className,true,classloader);

第2个boolean参数表示类是否需要初始化， Class.forName(className)默认是需要初始化。

一旦初始化，就会触发目标对象的 static块代码执行，static参数也也会被再次初始化。

ClassLoader.loadClass(className)方法，内部实际调用的方法是 ClassLoader.loadClass(className,false);

第2个 boolean参数，表示目标对象是否进行链接，false表示不进行链接，由上面介绍可以，

不进行链接意味着不进行包括初始化等一些列步骤，那么静态块和静态对象就不会得到执行

**应用**：JDBC使用时通常是使用Class.forName()方法来加载数据库链接驱动，因为要求Drive(数据库驱动类)必须向DrierManager注册自己，而Driver注册到DriverManager中的操作写在了静态代码块中，这就是为什么在写JDBC时使用Class.forName()的原因了。

### 4：JVM的体系结构

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\class文件.jpg)

类装载器（ClassLoader）（用来装载.class文件）

执行引擎（执行字节码，或者执行本地方法）

运行时数据区（方法区、堆、java栈、PC寄存器、本地方法栈）

### 5：JVM运行时数据区

第一块：PC寄存器

PC寄存器是用于存储每个线程下一步将执行的JVM指令，如该方法为native的，则PC寄存器中不存储任何信息。

第二块：JVM栈

JVM栈是线程私有的，每个线程创建的同时都会创建JVM栈，JVM栈中存放的为当前线程中局部基本类型的变量（java中定义的八种基本类型：boolean、char、byte、short、int、long、float、double）、部分的返回结果以及Stack Frame，非基本类型的对象在JVM栈上仅存放一个指向堆上的地址。

第三块：堆（Heap）

它是JVM用来存储对象实例以及数组值的区域，可以认为Java中所有通过new创建的对象的内存都在此分配，Heap中的对象的内存需要等待GC进行回收。

（1） 堆是JVM中所有线程共享的，因此在其上进行对象内存的分配均需要进行加锁，这也导致了new对象的开销是比较大的

（2） Sun Hotspot JVM为了提升对象内存分配的效率，对于所创建的线程都会分配一块独立的空间TLAB（Thread Local Allocation Buffer），其大小由JVM根据运行的情况计算而得，在TLAB上分配对象时不需要加锁，因此JVM在给线程的对象分配内存时会尽量的在TLAB上分配，在这种情况下JVM中分配对象内存的性能和C基本是一样高效的，但如果对象过大的话则仍然是直接使用堆空间分配

（3） TLAB仅作用于新生代的Eden Space，因此在编写Java程序时，通常多个小的对象比大的对象分配起来更加高效。

（4） 所有新创建的Object 都将会存储在新生代Yong Generation中。如果Young Generation的数据在一次或多次GC后存活下来，那么将被转移到OldGeneration。新的Object总是创建在Eden Space。

第四块：方法区域（Method Area）

（1）在Sun JDK中这块区域对应的为PermanetGeneration，又称为持久代。

（2）方法区域存放了所加载的类的信息（名称、修饰符等）、类中的静态变量、类中定义为final类型的常量、类中的Field信息、类中的方法信息，当开发人员在程序中通过Class对象中的getName、isInterface等方法来获取信息时，这些数据都来源于方法区域，同时方法区域也是全局共享的，在一定的条件下它也会被GC，当方法区域需要使用的内存超过其允许的大小时，会抛出OutOfMemory的错误信息。

第五块：运行时常量池（Runtime Constant Pool）

存放的为类中的固定的常量信息、方法和Field的引用信息等，其空间从方法区域中分配。

第六块：本地方法堆栈（Native Method Stacks）

JVM采用本地方法堆栈来支持native方法的执行，此区域用于存储每个native方法调用的状态。

------

### 6：对象“已死”的判定算法

由于程序计数器、Java虚拟机栈、本地方法栈都是线程独享，其占用的内存也是随线程生而生、随线程结束而回收。而Java堆和方法区则不同，线程共享，是GC的所关注的部分。

在堆中几乎存在着所有对象，GC之前需要考虑哪些对象还活着不能回收，哪些对象已经死去可以回收。

有两种算法可以判定对象是否存活：

1.）引用计数算法：给对象中添加一个引用计数器，每当一个地方应用了对象，计数器加1；当引用失效，计数器减1；当计数器为0表示该对象已死、可回收。但是它很难解决两个对象之间相互循环引用的情况。

2.）可达性分析算法：通过一系列称为“GC Roots”的对象作为起点，从这些节点开始向下搜索，搜索所走过的路径称为引用链，当一个对象到GC Roots没有任何引用链相连（即对象到GC Roots不可达），则证明此对象已死、可回收。Java中可以作为GC Roots的对象包括：虚拟机栈中引用的对象、本地方法栈中Native方法引用的对象、方法区静态属性引用的对象、方法区常量引用的对象。

在主流的商用程序语言（如我们的Java）的主流实现中，都是通过可达性分析算法来判定对象是否存活的。

------

### 7：JVM垃圾回收

GC (Garbage Collection)的基本原理：将内存中不再被使用的对象进行回收，GC中用于回收的方法称为收集器，由于GC需要消耗一些资源和时间，Java在对对象的生命周期特征进行分析后，按照新生代、旧生代的方式来对对象进行收集，以尽可能的缩短GC对应用造成的暂停

（1）对新生代的对象的收集称为youngGC；

（2）对旧生代的对象的收集称为Full GC；

（3）程序中主动调用System.gc()强制执行的GC为Full GC。

#### 1:gc对象

#####  (1).不使用的对象。

  分析：相当于没有回答，问题就是在问什么对象才是“不使用的对象”。大约占30%。

#####  (2).超出作用域的对象/引用计数为空的对象。

  分析：这2个回答站了60%，相当高的比例，估计学校教java的时候老师就是这样教的。第一个回答没有解决我的疑问，gc到底怎么判断哪些对象在不在作用域的？至于引用计数来判断对象是否可收集的，我可以会补充一个下面这个例子让面试者分析一下obj1、obj2是否会被GC掉？
  class C{
     public Object x;
  }
  C obj1、obj2 = new C();
  obj1.x = obj2;
  obj2.x = obj1;
  obj1、obj2 = null;

#####  (3).从gc root开始搜索，搜索不到的对象。

  分析：根对象查找、标记已经算是不错了，小于5%的人可以回答道这步，估计是引用计数的方式太“深入民心”了。基本可以得到这个问题全部分数。
  PS：有面试者在这个问补充强引用、弱引用、软引用、幻影引用区别等，不是我想问的答案，但可以加分。

#####   (4).从root搜索不到，而且经过第一次标记、清理后，仍然没有复活的对象。

  分析：我期待的答案。但是的确很少面试者会回答到这一点，所以在我心中回答道第3点我就给全部分数。 

#### 2:JVM对象的引用分为了四种类型(不同的对象引用类型， GC会采用不同的方法进行回收)

##### （1）强引用：

默认情况下，对象采用的均为强引用（这个对象的实例没有其他对象引用，GC时才会被回收）

##### （2）软引用：

软引用是Java中提供的一种比较适合于缓存场景的应用（只有在内存不够用的情况下才会被GC）

##### 经典面试题:ThreadLocal与内存泄漏

ThreadLocalMap类是ThreadLocal类的静态内部类，而Entry是ThreadLocalMap的静态内部类，key是ThreadLocal（声明为弱引用），value是Object，也就是我们要存的值。

ThreadLocal自身并不储存值，而是作为一个key来让线程从ThreadLocal获取value。Entry是中的key是弱引用，所以jvm在垃圾回收时如果外部没有强引用来引用它，ThreadLocal必然会被回收。但是，作为ThreadLocalMap的key，ThreadLocal被回收后，ThreadLocalMap就会存在null，但value不为null的Entry。若当前线程一直不结束，可能是作为线程池中的一员，线程结束后不被销毁，或者分配（当前线程又创建了ThreadLocal对象）使用了又不再调用get/set方法，就可能引发内存泄漏。其次，就算线程结束了，操作系统在回收线程或进程的时候不是一定杀死线程或进程的，在繁忙的时候，只会清除线程或进程数据的操作，重复使用线程或进程（线程id可能不变导致内存泄漏）。因此，**key弱引用并不是导致内存泄漏的原因，而是因为ThreadLocalMap的生命周期与当前线程一样长，并且没有手动删除对应key。**

那么，为什么要将Entry中的key设为弱引用？相反，设置为弱引用的key能预防大多数内存泄漏的情况。如果key 使用强引用，引用的ThreadLocal的对象被回收了，但是ThreadLocalMap还持有ThreadLocal的强引用，如果没有手动删除，ThreadLocal不会被回收，导致Entry内存泄漏。如果key为弱引用，引用的ThreadLocal的对象被回收了，由于ThreadLocalMap持有ThreadLocal的弱引用，即使没有手动删除，ThreadLocal也会被回收。value在下一次ThreadLocalMap调用set,get，remove的时候会被清除。



##### ThreadLocal在预防内存泄漏方面，做了哪些努力？

Thread中维护了ThreadLocalMap，所以ThreadLocalMap的生命周期和Thread（当前线程）一样长。使用不当就可能会导致内存泄漏问题。**但是，在ThreadLocal中，进行get，set操作的时候会清除Map里所有key为null的value。在使用完ThreadLocal时，及时调用它的的remove方法清除数据,可以避免弱引用引发的内存泄漏

##### （3）弱引用

在GC时一定会被GC回收

##### （4）虚引用

由于虚引用只是用来得知对象是否被GC



### 8：为什么大对象直接进入老年代

1：大对象就是需要大量连续内存空间的对象（比如：字符串、数组）。为了避免为大对象分配内存时由于**分配担保机制**带来的**复制而降低效率**。

2：长期存活的对象将进入老年代：既然虚拟机采用了分代收集的思想来管理内存，那么内存回收时就必须能识别哪些对象应放在新生代，哪些对象应放在老年代中。为了做到这一点，虚拟机给每个对象一个对象年龄（Age）计数器。如果对象在 Eden 出生并经过第一次 Minor GC 后仍然能够存活，并且能被 Survivor 容纳的话，将被移动到 Survivor 空间中，并将对象年龄设为 1.对象在 Survivor 中每熬过一次 MinorGC,年龄就增加 1 岁，当它的年龄增加到一定程度（默认为 15 岁），就会被晋升到老年代中。对象晋升到老年代的年龄阈值，可以通过参数 `-XX:MaxTenuringThreshold` 来设置。

3：动态对象年龄判定：虚拟机并不是永远要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升老年代，如果在 Survivor 中相同年龄所有对象大小的总和大于 Survivor 空间的一半，则年龄大于或等于该年龄的对象可以直接进入老年代，无需等到 MaxTenuringThreshold 中要求的年龄。

4：空间分配担保：在发生 Minor GC 之前，虚拟机先检查老年代最大可用的连续空间是否大于新生代所有对象总空间，如果条件成立的话，那么 Minor GC 可以确认是安全的。如果不成立的话虚拟机会查看 HandlePromotionFailure 的值是否允许担保失败，如果允许那么就会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小，如果大于，将尝试着进行一次 Minor GC；如果小于，或者 HandlePromotionFailure 的值不允许冒险，那么就要进行一次 Full GC。

5：Full GC 的触发条件：

- #### 调用 System.gc()

  只是建议虚拟机执行 Full GC，但是虚拟机不一定真正去执行。不建议使用这种方式，而是让虚拟机管理内存。

- ####  老年代空间不足

  老年代空间不足的常见场景为前文所讲的大对象直接进入老年代、长期存活的对象进入老年代等。

  为了避免以上原因引起的 Full GC，应当尽量不要创建过大的对象以及数组。除此之外，可以通过 -Xmn 虚拟机参数调大新生代的大小，让对象尽量在新生代被回收掉，不进入老年代。还可以通过 -XX:MaxTenuringThreshold 调大对象进入老年代的年龄，让对象在新生代多存活一段时间。

- #### 空间分配担保失败

  使用复制算法的 Minor GC 需要老年代的内存空间作担保，如果担保失败会执行一次 Full GC。具体内容请参考上面的内容。

- #### JDK 1.7 及以前的永久代空间不足

  在 JDK 1.7 及以前，HotSpot 虚拟机中的方法区是用永久代实现的，永久代中存放的为一些 Class 的信息、常量、静态变量等数据。

  当系统中要加载的类、反射的类和调用的方法较多时，永久代可能会被占满，在未配置为采用 CMS GC 的情况下也会执行 Full GC。如果经过 Full GC 仍然回收不了，那么虚拟机会抛出 java.lang.OutOfMemoryError。为避免以上原因引起的 Full GC，可采用的方法为增大永久代空间或转为使用 CMS GC。

- #### Concurrent Mode Failure

  执行 CMS GC 的过程中同时有对象要放入老年代，而此时老年代空间不足（可能是 GC 过程中浮动垃圾过多导致暂时性的空间不足），便会报 Concurrent Mode Failure 错误，并触发 Full GC。

  

### 8：垃圾收集算法

#### 1、标记-清除算法

最基础的算法，分标记和清除两个阶段：首先标记处所需要回收的对象，在标记完成后统一回收所有被标记的对象。

它有两点不足：一个效率问题，标记和清除过程都效率不高；一个是空间问题，标记清除之后会产生大量不连续的内存碎片（类似于我们电脑的磁盘碎片），空间碎片太多导致需要分配大对象时无法找到足够的连续内存而不得不提前触发另一次垃圾回收动作。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\标记-清除.jpg)

#### 2、复制算法

为了解决效率问题，出现了“复制”算法，他将可用内存按容量划分为大小相等的两块，每次只需要使用其中一块。当一块内存用完了，将还存活的对象复制到另一块上面，然后再把刚刚用完的内存空间一次清理掉。这样就解决了内存碎片问题，但是代价就是可以用内容就缩小为原来的一半。

年轻代一般选择复制算法

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\复制-算法.jpg)

#### 3、标记-整理算法

复制算法在对象存活率较高时就会进行频繁的复制操作，效率将降低。因此又有了标记-整理算法，标记过程同标记-清除算法，但是在后续步骤不是直接对对象进行清理，而是让所有存活的对象都向一侧移动，然后直接清理掉端边界以外的内存。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\标记-整理.jpg)

#### 4、分代收集算法

当前商业虚拟机的GC都是采用分代收集算法，这种算法并没有什么新的思想，而是根据对象存活周期的不同将堆分为：新生代和老年代，方法区称为永久代（在新的版本中已经将永久代废弃，引入了元空间的概念，永久代使用的是JVM内存而元空间直接使用物理内存）。

这样就可以根据各个年代的特点采用不同的收集算法。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\分代-收集.jpg)

新生代中的对象“朝生夕死”，每次GC时都会有大量对象死去，少量存活，使用复制算法。新生代又分为Eden区和Survivor区（Survivor from、Survivor to），大小比例默认为8:1:1。

老年代中的对象因为对象存活率高、没有额外空间进行分配担保，就使**用标记-清除或标记-整理算法**。

新产生的对象优先进去Eden区，当Eden区满了之后再使用Survivor from，当Survivor from 也满了之后就进行Minor GC（新生代GC），将Eden和Survivor from中存活的对象copy进入Survivor to，然后清空Eden和Survivor from，这个时候原来的Survivor from成了新的Survivor to，原来的Survivor to成了新的Survivor from。复制的时候，如果Survivor to 无法容纳全部存活的对象，则根据老年代的分配担保（类似于银行的贷款担保）将对象copy进去老年代，如果老年代也无法容纳，则进行Full GC（老年代GC）。

------

大对象直接进入老年代：JVM中有个参数配置**-XX:PretenureSizeThreshold**，令大于这个设置值的对象直接进入老年代，目的是为了避免在Eden和Survivor区之间发生大量的内存复制。

长期存活的对象进入老年代：JVM给每个对象定义一个对象年龄计数器，如果对象在Eden出生并经过第一次Minor GC后仍然存活，并且能被Survivor容纳，将被移入Survivor并且年龄设定为1。没熬过一次Minor GC，年龄就加1，当他的年龄到一定程度（默认为15岁，可以通过XX:MaxTenuringThreshold来设定），就会移入老年代。但是JVM并不是永远要求年龄必须达到最大年龄才会晋升老年代，如果Survivor 空间中相同年龄（如年龄为x）所有对象大小的总和大于Survivor的一半，年龄大于等于x的所有对象直接进入老年代，无需等到最大年龄要求。

**什么时候会触发YGC,什么时候触发FGC?**

**YGC的时机:**

 虚拟机在进行minorGC之前会判断老年代最大的可用连续空间是否大于新生代的所有对象总空间

  1、如果大于的话，直接执行minorGC

  2、如果小于，判断是否开启HandlerPromotionFailure，没有开启直接FullGC

  3、如果开启了HanlerPromotionFailure, JVM会判断老年代的最大连续内存空间是否大于历次晋升的大小，如果小于直接执行FullGC

  4、如果大于的话，执行minorGC

**FGC的时机：**

1.old空间不足；

2.perm空间不足；

3.显示调用System.gc() ，包括RMI等的定时触发;

4.YGC时的悲观策略；

5.dump live的内存信息时(jmap –dump:live)。

对YGC的 触发时机，相当的显而易见，就是eden空间不足， 这时候就肯定会触发ygc

对于FGC的触发时机， old空间不足， 和perm的空间不足， 调用system.gc()这几个都比较显而易见，就是在这种情况下， 一般都会触发GC。

最复杂的是所谓的悲观策略，它触发的机制是在首先会计算之前晋升的平均大小，也就是从新生代，通过ygc变成新生代的平均大小，然后如果旧生代剩余的空间小于晋升大小，那么就会触发一次FullGC。sdk考虑的策略是， 从平均和长远的情况来看，下次晋升空间不够的可能性非常大， 与其等到那时候在fullGC 不如悲观的认为下次肯定会触发FullGC， 直接先执行一次FullGC。而且从实际使用过程中来看， 也达到了比较稳定的效果。

------

### 9：垃圾收集器

垃圾收集算法是方法论，垃圾收集器是具体实现。JVM规范对于垃圾收集器的应该如何实现没有任何规定，因此不同的厂商、不同版本的虚拟机所提供的垃圾收集器差别较大，这里只看HotSpot虚拟机。

JDK7/8后，HotSpot虚拟机所有收集器及组合（连线）如下：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\收集器.jpg)

jdk1.7 默认垃圾收集器Parallel Scavenge（新生代）+Parallel Old（老年代）

jdk1.8 默认垃圾收集器Parallel Scavenge（新生代）+Parallel Old（老年代）

jdk1.9 默认垃圾收集器G1

##### 1.Serial收集器（新生代采用复制算法，老年代采用标记-整理算法。）

Serial收集器是最基本、历史最久的收集器，曾是新生代手机的唯一选择。他是单线程的，只会使用一个CPU或一条收集线程去完成垃圾收集工作，并且它在收集的时候，必须暂停其他所有的工作线程，直到它结束，即“Stop the World”。停掉所有的用户线程，对很多应用来说难以接受。比如你在做一件事情，被别人强制停掉，你心里奔腾而过的“羊驼”还数的过来吗？

尽管如此，它仍然是虚拟机运行在client模式下的默认新生代收集器：简单而高效（与其他收集器的单个线程相比，因为没有线程切换的开销等）。

工作示意图：

​	![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\serial收集器.jpg)

它的优点是简单高效，在单个 CPU 环境下，由于没有线程交互的开销，因此拥有最高的单线程收集效率。

它是 Client 场景下的默认新生代收集器，因为在该场景下内存一般来说不会很大。它收集一两百兆垃圾的停顿时间可以控制在一百多毫秒以内，只要不是太频繁，这点停顿时间是可以接受的。

##### 2.ParNew收集器

ParNew收集器是Serial收集器的多线程版本，除了使用了多线程之外，其他的行为（收集算法、stop the world、对象分配规则、回收策略等）同Serial收集器一样。

是许多运行在Server模式下的JVM中首选的新生代收集器，其中一个很重还要的原因就是除了Serial之外，只有他能和老年代的CMS收集器配合工作。

工作示意图：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\ParNew收集器.jpg)

##### 3.Parallel Scavenge收集器

新生代收集器，并行的多线程收集器。它的目标是达到一个可控的吞吐量（就是CPU运行用户代码的时间与CPU总消耗时间的比值，即 吞吐量=行用户代码的时间/[行用户代码的时间+垃圾收集时间]），这样可以高效率的利用CPU时间，尽快完成程序的运算任务，适合在后台运算而不需要太多交互的任务。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\Parallel Scavenge收集器.jpg)

##### 4.Serial Old收集器（**标记整理**）

Serial 收集器的老年代版本，单线程，“**标记整理**”算法，主要是给Client模式下的虚拟机使用。

另外还可以在Server模式下：

JDK 1.5之前的版本中雨Parallel Scavenge 收集器搭配使用

可以作为CMS的后背方案，在CMS发生Concurrent Mode Failure是使用

工作示意图：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\Parallel Old收集器.jpg)

##### 6.CMS收集器(标记-清除)

CMS(Concurrent Mark Sweep)收集器是一种以获取最短回收停顿时间为目标的收集器，停顿时间短，用户体验就好。

基于“**标记清除**”算法，并发收集、低停顿，运作过程复杂，分4步：

1)初始标记：仅仅标记GC Roots能直接关联到的对象，速度快，但是需要“Stop The World”

2)并发标记：就是进行追踪引用链的过程，可以和用户线程并发执行。

3)重新标记：修正并发标记阶段因用户线程继续运行而导致标记发生变化的那部分对象的标记记录，比初始标记时间长但远比并发标记时间短，需要“Stop The World”

4)并发清除：清除标记为可以回收对象，可以和用户线程并发执行

由于整个过程耗时最长的并发标记和并发清除都可以和用户线程一起工作，所以总体上来看，CMS收集器的内存回收过程和用户线程是并发执行的。

工作示意图：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\CMS收集器.jpg)

CSM收集器有3个缺点：

1)对CPU资源非常敏感

并发收集虽然不会暂停用户线程，但因为占用一部分CPU资源，还是会导致应用程序变慢，总吞吐量降低。

CMS的默认收集线程数量是=(CPU数量+3)/4；当CPU数量多于4个，收集线程占用的CPU资源多于25%，对用户程序影响可能较大；不足4个时，影响更大，可能无法接受。

2)无法处理浮动垃圾（在并发清除时，用户线程新产生的垃圾叫浮动垃圾）,可能出现"Concurrent Mode Failure"失败。

并发清除时需要预留一定的内存空间，不能像其他收集器在老年代几乎填满再进行收集；如果CMS预留内存空间无法满足程序需要，就会出现一次"Concurrent Mode Failure"失败；这时JVM启用后备预案：临时启用Serail Old收集器，而导致另一次Full GC的产生；

3)产生大量内存碎片：CMS基于"标记-清除"算法，清除后不进行压缩操作产生大量不连续的内存碎片，这样会导致分配大内存对象时，无法找到足够的连续内存，从而需要提前触发另一次Full GC动作。

##### 7.Parallel Old收集器（标记-整理）

Parallel Scavenge 收集器的老年代版本。使用多线程和“标记-整理”算法。在注重吞吐量以及 CPU 资源的场合，都可以优先考虑 Parallel Scavenge 收集器和 Parallel Old 收集器。

##### 8.G1收集器（jdk1.9之后默认收集器）

G1（Garbage-First）是JDK7-u4才正式推出商用的收集器。G1是面向服务端应用的垃圾收集器。它的使命是未来可以替换掉CMS收集器。

G1收集器特性：

并行与并发：能充分利用多CPU、多核环境的硬件优势，缩短停顿时间；能和用户线程并发执行。

分代收集：G1可以不需要其他GC收集器的配合就能独立管理整个堆，采用不同的方式处理新生对象和已经存活一段时间的对象。

空间整合：整体上看采用标记整理算法，局部看采用复制算法（两个Region之间），不会有内存碎片，不会因为大对象找不到足够的连续空间而提前触发GC，这点优于CMS收集器。

可预测的停顿：除了追求低停顿还能建立可以预测的停顿时间模型，能让使用者明确指定在一个长度为M毫秒的时间片段内，消耗在垃圾收集上的时间不超N毫秒，这点优于CMS收集器。

为什么能做到可预测的停顿？

是因为可以有计划的避免在整个Java堆中进行全区域的垃圾收集。

G1收集器将内存分大小相等的独立区域（Region），新生代和老年代概念保留，但是已经不再物理隔离。

G1跟踪各个Region获得其收集价值大小，在后台维护一个优先列表；

每次根据允许的收集时间，优先回收价值最大的Region（名称Garbage-First的由来）；

这就保证了在有限的时间内可以获取尽可能高的收集效率。

对象被其他Region的对象引用了怎么办？

> 判断对象存活时，是否需要扫描整个Java堆才能保证准确？在其他的分代收集器，也存在这样的问题（而G1更突出）：新生代回收的时候不得不扫描老年代？
>
> 无论G1还是其他分代收集器，JVM都是使用Remembered Set来避免全局扫描：
>
> 每个Region都有一个对应的Remembered Set；
>
> 每次Reference类型数据写操作时，都会产生一个Write Barrier 暂时中断操作；
>
> 然后检查将要写入的引用指向的对象是否和该Reference类型数据在不同的 Region（其他收集器：检查老年代对象是否引用了新生代对象）；
>
> 如果不同，通过CardTable把相关引用信息记录到引用指向对象的所在Region对应的Remembered Set中；
>
> 进行垃圾收集时，在GC根节点的枚举范围加入 Remembered Set ，就可以保证不进行全局扫描，也不会有遗漏。

------

不计算维护Remembered Set的操作，回收过程可以分为4个步骤（与CMS较为相似）：

1)初始标记：仅仅标记GC Roots能直接关联到的对象，并修改TAMS(Next Top at Mark Start)的值，让下一阶段用户程序并发运行时能在正确可用的Region中创建新对象，需要“Stop The World”

2)并发标记：从GC Roots开始进行可达性分析，找出存活对象，耗时长，可与用户线程并发执行

3)最终标记：修正并发标记阶段因用户线程继续运行而导致标记发生变化的那部分对象的标记记录。并发标记时虚拟机将对象变化记录在线程Remember Set Logs里面，最终标记阶段将Remember Set Logs整合到Remember Set中，比初始标记时间长但远比并发标记时间短，需要“Stop The World”

4)筛选回收：首先对各个Region的回收价值和成本进行排序，然后根据用户期望的GC停顿时间来定制回收计划，最后按计划回收一些价值高的Region中垃圾对象。回收时采用复制算法，从一个或多个Region复制存活对象到堆上的另一个空的Region，并且在此过程中压缩和释放内存；可以并发进行，降低停顿时间，并增加吞吐量。

工作示意图：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\G1收集器.jpg)

### 10：基本结构

从Java平台的逻辑结构上来看，我们可以从下图来了解JVM：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\JVM-基本结构.jpg)



# 二：Spring、SpringBoot、SpringCloud

## （一）：sping-AOP自定义注解

### （1）：几个注解  了解一下

```java
//作用：用于描述注解的使用范围（即：被描述的注解可以用在什么地方） 
@Target({ElementType.TYPE})
public enum ElementType {
    /** Class, interface (including annotation type), or enum declaration */
    TYPE,//接口、类、枚举、注解
    /** Field declaration (includes enum constants) */
    FIELD,//字段、枚举的常量
    /** Method declaration */
    METHOD,//方法
    //方法参数
    PARAMETER,
     //构造函数
    CONSTRUCTOR,
    //局部变量
    LOCAL_VARIABLE,
  	//注解
    ANNOTATION_TYPE,
    //包   
    PACKAGE,
    /**
     * Type parameter declaration
     * @since 1.8
     */
    TYPE_PARAMETER,
    /**
     * Use of a type
     * @since 1.8
     */
    TYPE_USE
}
/*
@Retention ：表示需要在什么级别保存该注释信息，用于描述注解的生命周期（即：被描述的注解在什么范围内有效） 定义了该Annotation被保留的时间长短，取值（RetentionPoicy）有：
1.RetentionPolicy.SOURCE —— 这种类型的Annotations只在源代码级别保留,编译时就会被忽略
2.RetentionPolicy.CLASS —— 这种类型的Annotations编译时被保留,在class文件中存在,但JVM将会忽略
3.RetentionPolicy.RUNTIME —— 这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用. */

/**
*@Documented用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的*	工具文档化。Documented是一个标记注解，没有成员。
*/
```

注：需要通过切面来实现注解或通过反射来处理

@Aspect:定义一个切面         AspectJ提供了五种注解来定义    通知

```java
@Aspect
@Component
public class TestAspect {}
```

@Pointcut:声明一个通用的切点(其实就是对那个包下面的什么方法进行切入)

```java
@Pointcut("execution( *  com.pingan.chengke.saas.worksite.web.dashboard.controller.HighFormworkController.getMachineCurrentInfo(..)))")
public void authPointCut(){}
```

@After
通知方法会在目标方法返回或抛出异常后调用

@AfterRetruening
通常方法会在目标方法返回后调用

@AfterThrowing
通知方法会在目标方法抛出异常后调用

@Around
通知方法将目标方法封装起来

@Before

JoinPoint 对象：JoinPoint对象封装了	SpringAop中切面方法的信息,在切面方法中添加JoinPoint参数,就可以获取到封装了该方法信息的JoinPoint对象. 常用api:

Signature getSignature()：获取封装了署名信息的对象,在该对象中可以获取到目标方法名,所属类的Class等信息
Object[] getArgs()：获取传入目标方法的参数对象
Object getTarget()：获取被代理的对象
Object getThis()：获取代理对象

ProceedingJoinPoint对象：ProceedingJoinPoint对象是JoinPoint的子接口,该对象只用在@Around的切面方法中, 

添加了两个方法： 
`Object proceed() throws Throwable //执行目标方法` 
`Object proceed(Object[] var1) throws Throwable //传入的新的参数去执行目标方法` 

```java
/**
 * 功能权限校验切面
 */
@Aspect
@Component
public class AuthAspect implements ApplicationContextAware {
    private final static Logger LOGGER = getLogger(AuthAspect.class);
    private static final String PROJECT_ID = "projectId";
    private ApplicationContext applicationContext;

    @Autowired
    private ProjectFacade projectFacade;
// @Pointcut("execution(* aopdemo.*.*(..))") 用切点表达式
   // 指定类上包含注解的作为切点，然后可以在切入点开始处(如@Before @after)做具体的处理，
    @Pointcut("@annotation(com.pingan.chengke.field.common.annotation.FunctionAuth)")
    public void authPointCut(){}

    @Before("authPointCut()")
    public void before(JoinPoint joinPoint){
        if (UserThreadContext.getSessionVisitor() == null){
            throw new BizException("account.login.loginState.fail");
        }
        //userId:19L projectId:158L
        Long userId = UserThreadContext.getSessionVisitor().getAccountId();
        Long projectId = 0L;
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FunctionAuth functionAuth = method.getAnnotation(FunctionAuth.class);
        if (functionAuth != null) {
            try{
                //第X个参数
                int paramOrder = functionAuth.paramOrder();
                //是否需要查询sql
                boolean needQuery = functionAuth.needQuery();
                //是否直接使用第x个参数
                boolean useDirectParam = functionAuth.useDirectParam();
                Object object = args[paramOrder - 1];
                //直接使用参数，不需要查询获取
                if (useDirectParam && !needQuery) {
                    projectId = (Long) object;
                } else if (!useDirectParam && !needQuery) {
                    String objectStr = JSON.toJSONString(object);
                    JSONObject jsonObject = JSONObject.parseObject(objectStr);
                    projectId = jsonObject.getLong(functionAuth.paramName());
                }
                //需要查询得到projectId
                else if (useDirectParam && needQuery) {
                    projectId = getProjectId(functionAuth.beanName(), functionAuth.methodName(), (Long) object);
                } else if (!useDirectParam && needQuery) {
                    String objectStr = JSON.toJSONString(object);
                    JSONObject jsonObject = JSONObject.parseObject(objectStr);
                    Long searchId = jsonObject.getLong(functionAuth.paramName());
                    projectId = getProjectId(functionAuth.beanName(), functionAuth.methodName(), searchId);
                }
            }catch (Exception e){
                LOGGER.error("功能权限校验失败", e);
            }
            if (LongUtil.isNotNullAndZero(projectId)){
                checkPermission(projectId, userId);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取projectId
     * @param beanName
     * @param methodName
     * @param searchId
     * @return
     */
    private Long getProjectId(String beanName, String methodName, Long searchId){
        Long projectId = 0L;
        try{
            Object contextBean = applicationContext.getBean(beanName);
            Method methodInvoke = contextBean.getClass().getMethod(methodName, Long.class, Integer.class);
            Object result = methodInvoke.invoke(contextBean, searchId, DeletedEnum.NOT_DELETED.getValue());
            if (result instanceof Collection){
                String objectStr = JSON.toJSONString(result);
                JSONArray jsonArray = JSONArray.parseArray(objectStr);
                if(CollectionUtils.isEmpty(jsonArray)){
                    Object first = jsonArray.get(0);
                    String firstStr = JSON.toJSONString(first);
                    JSONObject jsonObject = JSONObject.parseObject(firstStr);
                    projectId = jsonObject.getLong(PROJECT_ID);
                }
            }else{
                String objectStr = JSON.toJSONString(result);
                JSONObject jsonObject = JSONObject.parseObject(objectStr);
                projectId = jsonObject.getLong(PROJECT_ID);
            }
        }catch (Exception e){
            LOGGER.error("功能权限校验，获取项目编号【失败】", e);
        }
        return projectId;
    }

    /**
     * 权限校验
     * @param projectId
     */
    private void checkPermission(Long projectId, Long userId){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        List<ProjectPermissionVO> projectPermissionVOList = projectFacade.getProjectPermissionVO(userId, projectId);
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Long finalProjectId = projectId;
        AtomicBoolean includeUri = new AtomicBoolean(false);
        projectPermissionVOList.forEach(projectPermissionVO -> {
            if (finalProjectId.equals(projectPermissionVO.getId())){
                List<PermissionDto> permissionDtoList = projectPermissionVO.getPermissionDto();
                permissionDtoList.forEach(permissionDto -> {
                    if (antPathMatcher.match(permissionDto.getRequestUrl(), request.getRequestURI())){
                        includeUri.set(true);
                    }
                });
            }
        });
        if (!includeUri.get()){
            throw new BizException("system.have.no.permission.to.access");
        }
    }

}

```

### （2）和spring的关系 

BeforeAdvice：前置通知， 对应spring的接口 MethodBeforeAdvice， 其对应切点为指定方法调用前
 AfterAdvice： 后置通知， 对应spring接口AfterReturningAdvice， 对应切点为指定方法调用后
 AroundAdvice：环绕通知， 对应spring接口MethodInterceptor， 对应的切点方法调用前，调用后，调用中，整个方法调用前后均可用，而且，还可以改变方法的行为

 ThrowsAdvice：  异常通知， 对应spring接口ThrowsAdvice，切点为目标方法抛出异常时调用

## (二):自定义异常

  在实际的开发中，有时需要我们自定义一些异常，springBoot开发中自定义异常步骤

### （1）：自定义一个异常类，继承RunTimeException 

```java
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1095242212086237834L;
    protected Object errorCode;
    protected Object[] args;
    public BizException() {
        super();
    }
    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
    public BizException(String message) {
        super(message);
    }
    public BizException(String message, Object[] args, Throwable cause) {
        super(message, cause);
        this.args = args;
    }
    public BizException(String message, Object[] args) {
        super(message);
        this.args = args;
    }
    public BizException(Object errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public BizException(Object errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public BizException(Object errorCode, String message, Object[] args, Throwable cause) {
        super(message, cause);
        this.args = args;
        this.errorCode = errorCode;
    }
    public BizException(Object errorCode, String message, Object[] args) {
        super(message);
        this.args = args;
        this.errorCode = errorCode;
    }
    public BizException(Throwable cause) {
        super(cause);
    }
    public Object[] getArgs() {
        return args;
    }
    public Object getErrorCode() {
        return errorCode;
    }
}
```

### （2）.需要构建异常处理器

####     几个注解了解一下：

@ControllerAdvice拦截异常 ： 用在类上

@ExceptionHandler指定处理哪种异常（可指定多个）：用在方法上

@ResponseStatus指定返回的http状态码（具体可查看HttpStatus这个类）：用在方法上

@ControllerAdvice+@ResponseBody可换成@RestControllerAdvice.

```java
/**
 * WEB异常处理器
 */
@ControllerAdvice("com.pingan.chengke.field")
public class WebExceptionHandler {
    static Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);
    private static final String SYSTEM_ERROR = "系统错误";
    @Autowired
    private MessageSource messageSource;  
	@ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseVO<Object> handleBizException(BizException e) {
        LOG.error(e.getMessage());
        return fillExceptionResult(e.getErrorCode(), e.getMessage(), e.getArgs());
    }
  }
 /**
     * 封装 ResponseVO
     * @return
     */
    private ResponseVO fillExceptionResult(Object errorCode, String errorMessage, Object[] args) {
        String message = errorMessage;
        if (errorMessage != null) {
            try {
                message = messageSource.getMessage(errorMessage, args, LocaleContextHolder.getLocale());
            } catch (NoSuchMessageException ex) {
                LOG.warn("no such message key:" + errorMessage);
            }
        }
        String errorCodeStr = errorCode == null ? null : errorCode.toString();
        return ResponseVO.fail(errorCodeStr, message);
    }
}
```

## (三)：Session共享

### （1）第一种方式：Spring Session解决分布式会话共享

问题：HttpSession是通过Servlet容器创建和管理的，像Tomcat/Jetty都是保存在内存中的。如果我们将Web应用横向扩展搭建成分布式的集群，然后利用LVS或Nginx做负载均衡，那么来自同一用户的Http请求将有可能被负载分发到两个不同的实例中去，如何保证不同实例间Session共享成为一个不得不解决的问题。

我能想到的办法就是把session放到一个共同的地方，然后每次请求的时候就去查找看看是不是存在请求的session ，例如Memcached/Redis中。那么问题又来了，如何替换掉Servlet容器创建和管理HttpSession的实现呢？

- 利用Servlet容器提供的插件功能，自定义HttpSession的创建和管理策略，并通过配置的方式替换掉默认的策略。不过这种方式有个缺点，就是需要耦合Tomcat/Jetty等Servlet容器的代码。这方面其实早就有开源项目了，例如[memcached-session-manager](https://link.jianshu.com?t=https://github.com/magro/memcached-session-manager)，以及[tomcat-redis-session-manager](https://link.jianshu.com?t=https://github.com/jcoleman/tomcat-redis-session-manager)。暂时都只支持Tomcat6/Tomcat7。


- 配置Nginx的负载均衡算法为ip_hash，这样每个请求按访问IP的hash结果分配，这样来自同一个IP的访客固定访问一个后端服务器，有效解决了动态网页存在的Session共享问题


- 如果你使用Shiro管理Session，可以用Redis来实现Shiro 的SessionDao接口，这样Session便归Redis保管。


- 设计一个Filter，利用HttpServletRequestWrapper，实现自己的 getSession()方法，接管创建和管理Session数据的工作。Spring-Session就是通过这样的思路实现的。

  下面看看springBoot 管理session 的方案

  #### 1）添加依赖

  ```xml
  <dependency>  
          <groupId>org.springframework.boot</groupId>  
          <artifactId>spring-boot-starter-redis</artifactId>  
  </dependency>  
  <dependency>  
          <groupId>org.springframework.session</groupId>  
          <artifactId>spring-session-data-redis</artifactId>  
  </dependency>  
  ```

  2) 配置类 和 redis 配置

  ```java
  @Configuration  
  @EnableRedisHttpSession  //@EnableRedisHttpSession这个注解是由spring-session-data-redis提供的
  public class RedisSessionConfig {  
      
  }  
  ```

  3）类使用

  ```java
  @RestController  
  @RequestMapping(value = "/admin/v1")  
  public class testController {  
      @RequestMapping(value = "/first", method = RequestMethod.GET)  
      public Map<String, Object> firstResp (HttpServletRequest request){  
          Map<String, Object> map = new HashMap<>();  
          request.getSession().setAttribute("request Url", request.getRequestURL());  
          map.put("request Url", request.getRequestURL());  
          return map;  
      }  
  ```


## 5：（四）使用



## 6：定时任务



# 二:数据结构与算法

（一）数据结构：数据结构是指逻辑意义上的数据组织方式以及相应的处理方式。逻辑意义上的组合方式有很多，比如树，图，队列，哈希；树可以是二叉树，三叉树，B+树等，队列是先进先出的线性结构，哈希是根据某种算法直接定位的数据组织方式；

1：集合框架

其实像那些基础的框架图我本来不想放在这的，因为我觉的这很基础，要像长在脑子里一样





# 						三：Java8特性

## （一）：

## （二）：流概念

流：从支持数据处理操作的源生成的元素序列

数据处理：流的数据处理中间：filter，map，reduce，find，match，sort，limit（n），distinct，skip(n)                         		      所有其他操作都会返回一个流

​                  终端操作： collect(把流归约成一个集合)，count（返回流中元素的个数）,forEach（消费流中每个元素并对应的其应用Lambda）

源：流会使用一个提供数据的源；如集合，数组或输入/输出资源(****流只能用一次****)  

元素序列：就像集合一样，流也提供了一个接口，可以访问特定元素类型的一组有序值

### 1：数据处理中的方法

filter(一个返回Boolean的函数参数) 

map(接受一个函数参数，这个函数会被运用到每个元素上，并将其映射成一个新的元素，)

Arrays.stream()：接受一个数组并产生一个流

flatMap:将生成的多个流扁平化成单个流

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\PASOScreenShot20190115175253841.PNG)

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\PASOScreenShot20190115175121263.PNG)

anyMatch:流中是否有一个元素能匹配给定的谓词，返回一个Boolean,是终端操作

allMatch:流中的元素是否都能匹配给定的谓词，返回一个Boolean,是终端操作

noneMatch:确保流中没有任何元素与给定谓词匹配，返回一个Boolean,是终端操作

findAny:返回流中的所有元素，是终端操作

findFirst:返回流中的第一个元素

注解：Optional<T>类是一个容器类，代表值存在或者不存在，不用考虑返回NUL的问题，

# 						四：中间件

## （一） redis

安装和启动redis:步骤：wget—>make—>make install —>make test —> 程序会复制到/usr/local/bin目录下

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\redis.PNG)

启动：redis-cli -h localhost -p 6379

测试链接：redis-cli ping

### 1：redis命令（常见的数据结构）

#### (1)：String（value值最大512M） 

#### (2):List 列表

#### (3):set集合(一个key中可以有多个值)

#### (4):hash  散列，存储多个键值对之间的映射(同一个key可以有多个key-value)

#### (5):	zset   和散列一样，用于存储键值对，有序结合的键成为成员，每个成员各不相同；有序集合的值是浮点数。有序集合是redis里面唯一可以根据成员访问i元素，又可以根据值以及值得排列顺序来访问元素结构(理解:可有多个有顺序的key-value )。

#### （6）HyperLogLog ：这种数据式结构是redis的高级数据结构，可以用来解决去重统计问题，他和set数据结构差不多都可以用来去重。

指令：pfadd（相当于sadd）:增加计数     

PFADD key element [element …]：将任意数量的元素添加到指定的 HyperLogLog 里面。整数回复： 如果 HyperLogLog 的内部储存被修改了， 那么返回 1 ， 否则返回 0 。

时间复杂度： 每添加一个元素的复杂度为 O(1) 。

作为这个命令的副作用， HyperLogLog 内部可能会被更新， 以便反映一个不同的唯一元素估计数量（也即是集合的基数）。如果 HyperLogLog 估计的近似基数（approximated cardinality）在命令执行之后出现了变化， 那么命令返回 `1` ， 否则返回 `0` 。 如果命令执行时给定的键不存在， 那么程序将先创建一个空的 HyperLogLog 结构， 然后再执行命令。

PFCOUNT key [key …]：当 [PFCOUNT key [key …\]](http://redisdoc.com/hyperloglog/pfcount.html#pfcount) 命令作用于单个键时， 返回储存在给定键的 HyperLogLog 的近似基数， 如果键不存在， 那么返回 `0` 。当 [PFCOUNT key [key …\]](http://redisdoc.com/hyperloglog/pfcount.html#pfcount) 命令作用于多个键时， 返回所有给定 HyperLogLog 的并集的近似基数， 这个近似基数是通过将所有给定 HyperLogLog 合并至一个临时 HyperLogLog 来计算得出的。

时间复杂度： 当命令作用于单个 HyperLogLog 时， 复杂度为 O(1) ， 并且具有非常低的平均常数时间。 当命令作用于 N 个 HyperLogLog 时， 复杂度为 O(N) ， 常数时间也比处理单个 HyperLogLog 时要大得多。

通过 HyperLogLog 数据结构， 用户可以使用少量固定大小的内存， 来储存集合中的唯一元素 （每个 HyperLogLog 只需使用 12k 字节内存，以及几个字节的内存来储存键本身）。

命令返回的可见集合（observed set）基数并不是精确值， 而是一个带有 0.81% 标准错误（standard error）的近似值。

PFMERGE destkey sourcekey [sourcekey …]：将多个 HyperLogLog 合并（merge）为一个 HyperLogLog ， 合并后的 HyperLogLog 的基数接近于所有输入 HyperLogLog 的可见集合（observed set）的并集。合并得出的 HyperLogLog 会被储存在 `destkey` 键里面， 如果该键并不存在， 那么命令在执行之前， 会先为该键创建一个空的 HyperLogLog 。

返回值：返回 `OK` 。

时间复杂度： O(N) ， 其中 N 为被合并的 HyperLogLog 数量， 不过这个命令的常数复杂度比较高。

#### (7)布隆过滤器

讲个使用场景，比如我们在使用新闻客户端看新闻时，它会给我们不停地推荐新的内容，它每次推荐时要去重，去掉那些已经看过的内容。问题来了，新闻客户端推荐系统如何实现推送去重的？

布隆过滤器可以理解为一个不怎么精确的 set 结构，当你使用它的 contains 方法判断某个对象是否存在时，它可能会误判。但是布隆过滤器也不是特别不精确，只要参数设置的合理，它的精确度可以控制的相对足够精确，只会有小小的误判概率。

布隆过滤器有二个基本指令，bf.add 添加元素，bf.exists 查询元素是否存在，它的用法和 set 集合的 sadd 和 sismember 差不多。注意 bf.add 只能一次添加一个元素，如果想要一次添加多个，就需要用到 bf.madd 指令。同样如果需要一次查询多个元素是否存在，就需要用到 bf.mexists 指令。

## 2：reids应用

### 1：发布与订阅：

subscribe:订阅

publish:发布

### 2文章投票：

### 3：分布式锁

起初考虑到分布式锁考虑了只有一个线程能拿到锁，想到了setnx命令，然后用del命令来释放锁，考虑到中途会出故障导致死锁，所以给锁加一个过期时间，又有一个问题，在给锁设置过期时间之前出故障，所以要保证加锁和设置过期时间是原子操作，所以以前既要使用reids,还要引入分布式锁 的library，redis 引入setnx和expire的原子性操作命令，

### 4：redis 的  list   数据结构可以作为异步消息队列使用

使用lpush / rpush  入队列  使用 	lpop / rpop出队列

通常我们让线程睡眠1s 来解决redis空队列时redis 的QPS会被拉高，慢查询会显著；但是睡眠会使消息的延迟增大，解决方法是 使用 blpop / brpop 命令 b 代表阻塞读，阻塞读可以使队列在没有数据的时候立刻进入休眠状态，有数据的时候就能立即苏醒，有个问题就是空闲链接的问题，闲置过久服务器一般会断开连接，这个时候   blpop / brpop 会抛出异常。

解决在第一次加锁失败的情况：直接抛异常，通知客户稍后再试；sleep 一会然后再重试；将请求转移至延时队列（zset这个数据结构实现）。

### 5：事务

**MULTI**：标记一个事务块的开始。

事务块内的多条命令会按照先后顺序被放进一个队列当中，最后由[EXEC](http://redisdoc.com/transaction/exec.html#exec) 命令原子性(atomic)地执行。

**EXEC**：执行所有事务块内的命令。

假如某个(或某些) key 正处于 [WATCH](http://redisdoc.com/transaction/watch.html#watch) 命令的监视之下，且事务块中有和这个(或这些) key 相关的命令，那么 [EXEC](http://redisdoc.com/transaction/exec.html#exec) 命令只在这个(或这些) key 没有被其他命令所改动的情况下执行并生效，否则该事务被打断(abort)。

DISCARD：取消事务，放弃执行事务块内的所有命令。

时间复杂度： O(1)。

如果正在使用 `WATCH` 命令监视某个(或某些) key，那么取消所有监视，等同于执行命令 `UNWATCH` 。

**WATCH key [key …]**：监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。

**UNWATCH**：取消 [WATCH](http://redisdoc.com/transaction/watch.html#watch) 命令对所有 key 的监视。

如果在执行 [WATCH](http://redisdoc.com/transaction/watch.html#watch) 命令之后， [EXEC](http://redisdoc.com/transaction/exec.html#exec) 命令或 [DISCARD](http://redisdoc.com/transaction/discard.html#discard) 命令先被执行了的话，那么就不需要再执行 [UNWATCH](http://redisdoc.com/transaction/unwatch.html#unwatch) 了。

因为 [EXEC](http://redisdoc.com/transaction/exec.html#exec) 命令会执行事务，因此 [WATCH](http://redisdoc.com/transaction/watch.html#watch) 命令的效果已经产生了；而 [DISCARD](http://redisdoc.com/transaction/discard.html#discard) 命令在取消事务的同时也会取消所有对 key 的监视，因此这两个命令执行之后，就没有必要执行 [UNWATCH](http://redisdoc.com/transaction/unwatch.html#unwatch) 了。

### 6:限流

### 7:redis的线程模型

[Redis](http://lib.csdn.net/base/redis) 基于 [Reactor 模式](http://en.wikipedia.org/wiki/Reactor_pattern)开发了自己的网络事件处理器： 这个处理器被称为文件事件处理器（file event handler）：

- 文件事件处理器使用 [I/O 多路复用（multiplexing）](http://en.wikipedia.org/wiki/Multiplexing)程序来同时监听多个套接字， 并根据套接字目前执行的任务来为套接字关联不同的事件处理器。
- 当被监听的套接字准备好执行连接应答（accept）、读取（read）、写入（write）、关闭（close）等操作时， 与操作相对应的文件事件就会产生， 这时文件事件处理器就会调用套接字之前关联好的事件处理器来处理这些事件。

虽然文件事件处理器以单线程方式运行， 但通过使用 I/O 多路复用程序来监听多个套接字， 文件事件处理器既实现了高性能的网络通信模型， 又可以很好地与 [redis](http://lib.csdn.net/base/redis) 服务器中其他同样以单线程方式运行的模块进行对接， 这保持了 Redis 内部单线程设计的简单性。

### 文件事件处理器的构成

图展示了文件事件处理器的四个组成部分， 它们分别是套接字、 I/O 多路复用程序、 文件事件分派器（dispatcher）、 以及文件事件处理器。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\文件处理器.jpg)

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\redis进程.png)

文件事件是对套接字操作的抽象， 每当一个套接字准备好执行连接应答（accept）、写入、读取、关闭等操作时， 就会产生一个文件事件。 因为一个服务器通常会连接多个套接字， 所以多个文件事件有可能会并发地出现。

I/O 多路复用程序负责监听多个套接字， 并向文件事件分派器传送那些产生了事件的套接字。

尽管多个文件事件可能会并发地出现， 但 I/O 多路复用程序总是会将所有产生事件的套接字都入队到一个队列里面， 然后通过这个队列， 以有序（sequentially）、同步（synchronously）、每次一个套接字的方式向文件事件分派器传送套接字： 当上一个套接字产生的事件被处理完毕之后（该套接字为事件所关联的事件处理器执行完毕）， I/O 多路复用程序才会继续向文件事件分派器传送下一个套接字

### 文件事件的处理器

Redis 为文件事件编写了多个处理器， 这些事件处理器分别用于实现不同的网络通讯需求， 比如说：

- 为了对连接服务器的各个客户端进行应答， 服务器要为监听套接字关联连接应答处理器。
- 为了接收客户端传来的命令请求， 服务器要为客户端套接字关联命令请求处理器。
- 为了向客户端返回命令的执行结果， 服务器要为客户端套接字关联命令回复处理器。
- 当主服务器和从服务器进行复制操作时， 主从服务器都需要关联特别为复制功能编写的复制处理器。

### 连接应答处理器

`networking.c/acceptTcpHandler` 函数是 Redis 的连接应答处理器， 这个处理器用于对连接服务器监听套接字的客户端进行应答， 具体实现为`sys/socket.h/accept` 函数的包装。

当 Redis 服务器进行初始化的时候， 程序会将这个连接应答处理器和服务器监听套接字的 `AE_READABLE` 事件关联起来， 当有客户端用`sys/socket.h/connect` 函数连接服务器监听套接字的时候， 套接字就会产生 `AE_READABLE` 事件， 引发连接应答处理器执行， 并执行相应的套接字应答操作， 如图 IMAGE_SERVER_ACCEPT_CONNECT 所示。

![digraph {    label = "\n图 IMAGE_SERVER_ACCEPT_CONNECT    服务器对客户端的连接请求进行应答";    rankdir = LR;    client [label = "客户端", shape = circle];    server [label = "服务器\n\n\n服务器监听套接字产生\nAE_READABLE 事件\n执行连接应答处理器", shape = box, height = 2];    client -> server [label = "连接监听套接字"];}](http://redisbook.com/_images/graphviz-3b7d7746ab1f951de394d8e7fb2c8fb7c16ae466.png)

### 命令请求处理器

`networking.c/readQueryFromClient` 函数是 Redis 的命令请求处理器， 这个处理器负责从套接字中读入客户端发送的命令请求内容， 具体实现为 `unistd.h/read` 函数的包装。

当一个客户端通过连接应答处理器成功连接到服务器之后， 服务器会将客户端套接字的 `AE_READABLE` 事件和命令请求处理器关联起来， 当客户端向服务器发送命令请求的时候， 套接字就会产生 `AE_READABLE` 事件， 引发命令请求处理器执行， 并执行相应的套接字读入操作， 如图 IMAGE_SERVER_RECIVE_COMMAND_REQUEST 所示。

![digraph {    label = "\n图 IMAGE_SERVER_RECIVE_COMMAND_REQUEST    服务器接收客户端发来的命令请求";    rankdir = LR;    client [label = "客户端", shape = circle];    server [label = "服务器\n\n\n客户端套接字产生\nAE_READABLE 事件\n执行命令请求处理器", shape = box, height = 2];    client -> server [label = "发送命令请求"];}](http://redisbook.com/_images/graphviz-d17b048af34f2b53583f75205f86cfad1cda6491.png)

在客户端连接服务器的整个过程中， 服务器都会一直为客户端套接字的 `AE_READABLE` 事件关联命令请求处理器。

### 命令回复处理器

`networking.c/sendReplyToClient` 函数是 Redis 的命令回复处理器， 这个处理器负责将服务器执行命令后得到的命令回复通过套接字返回给客户端， 具体实现为 `unistd.h/write` 函数的包装。

当服务器有命令回复需要传送给客户端的时候， 服务器会将客户端套接字的 `AE_WRITABLE` 事件和命令回复处理器关联起来， 当客户端准备好接收服务器传回的命令回复时， 就会产生 `AE_WRITABLE` 事件， 引发命令回复处理器执行， 并执行相应的套接字写入操作， 如图 IMAGE_SERVER_SEND_REPLY 所示。

![digraph {    label = "\n图 IMAGE_SERVER_SEND_REPLY    服务器向客户端发送命令回复";    rankdir = LR;    client [label = "客户端", shape = circle];    server [label = "服务器\n\n\n客户端套接字产生\nAE_WRITABLE 事件\n执行命令回复处理器", shape = box, height = 2];    client -> server [dir = back, label = "发送命令回复"];}](http://redisbook.com/_images/graphviz-afa1aff0499dc8685a7bb6a87284c75e3fcb753d.png)

当命令回复发送完毕之后， 服务器就会解除命令回复处理器与客户端套接字的 `AE_WRITABLE` 事件之间的关联。

### 一次完整的客户端与服务器连接事件示例

让我们来追踪一次 Redis 客户端与服务器进行连接并发送命令的整个过程， 看看在过程中会产生什么事件， 而这些事件又是如何被处理的。

假设一个 Redis 服务器正在运作， 那么这个服务器的监听套接字的 `AE_READABLE` 事件应该正处于监听状态之下， 而该事件所对应的处理器为连接应答处理器。

如果这时有一个 Redis 客户端向服务器发起连接， 那么监听套接字将产生 `AE_READABLE` 事件， 触发连接应答处理器执行： 处理器会对客户端的连接请求进行应答， 然后创建客户端套接字， 以及客户端状态， 并将客户端套接字的 `AE_READABLE` 事件与命令请求处理器进行关联， 使得客户端可以向主服务器发送命令请求。

之后， 假设客户端向主服务器发送一个命令请求， 那么客户端套接字将产生 `AE_READABLE` 事件， 引发命令请求处理器执行， 处理器读取客户端的命令内容， 然后传给相关程序去执行。

执行命令将产生相应的命令回复， 为了将这些命令回复传送回客户端， 服务器会将客户端套接字的 `AE_WRITABLE` 事件与命令回复处理器进行关联： 当客户端尝试读取命令回复的时候， 客户端套接字将产生 `AE_WRITABLE` 事件， 触发命令回复处理器执行， 当命令回复处理器将命令回复全部写入到套接字之后， 服务器就会解除客户端套接字的 `AE_WRITABLE` 事件与命令回复处理器之间的关联。

![digraph {    label = "\n图 IMAGE_COMMAND_PROGRESS    客户端和服务器的通讯过程";    splines = ortho;    rankdir = LR;    node [shape = box, height = 3.0];    client [label = "客\n户\n端"];    server [label = "服\n务\n器"];    client -> server [label = "客户端向服务器发送连接请求\n服务器执行连接应答处理器"];    client -> server [label = "\n\n客户端向服务器发送命令请求\n服务器执行命令请求处理器"];    server -> client [label = "\n\n服务器向客户端发送命令回复\n服务器执行命令回复处理器"];}](http://redisbook.com/_images/graphviz-ecd9b3ff243e2f34204c0f9f2a590058bf262d84.png)

### 8：redis 的数据淘汰机制

在 redis 中，允许用户设置最大使用内存大小通过配置redis.conf中的maxmemory这个值来开启内存淘汰功能，在内存限定的情况下是很有用的。设置最大内存大小可以保证redis对外提供稳健服务。

redis 提供 6种数据淘汰策略通过maxmemory-policy设置策略：

volatile-lru：从已设置**过期**时间的数据集（server.db[i].expires）中挑选最近最**少使用**的数据淘汰
volatile-ttl：从已设置**过期**时间的数据集（server.db[i].expires）中挑选**将要过期**的数据淘汰
volatile-random：从已设置**过期**时间的数据集（server.db[i].expires）中**任意**选择数据淘汰
allkeys-lru：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
allkeys-random：从数据集（server.db[i].dict）中任意选择数据淘汰
no-enviction（驱逐）：禁止驱逐数据
redis 确定驱逐某个键值对后，会删除这个数据并将这个数据变更消息发布到本地（AOF 持久化）和从机（主从连接）

### 9:  redis和mongoDB对比

| 区别           | redis                                                        | mongoDB                                                      |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 内存管理机制   | 数据存在内存，定期写入磁盘，内存不够时选择指定的lru算法删除数据 | 数据存在内存，由linux系统毛毛实现，内存不够，值存热点数据，其他放磁盘 |
| 支持的数据结构 | hash，String，list，set，zset                                | 文本，二进制类型                                             |
| 附加功能       | 发布/订阅，主从分区，序列化支持，脚本支持                    | 多线程服务支持                                               |
| 网络I/O模型    | 单进程                                                       | 多进程，非阻塞IO模式                                         |
| 持久化         | RDB,AOF                                                      | 不支持                                                       |

内存管理机制

Memcached与Redis都是C语言实现，但是不约而同的都抛弃了传统C语言中的malloc/free函数，而选择了自主实现内存模型。
Memcached的内存模式，官方定义为 ”Slab Allocation“，大致流程图如下：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\memcatch内存管理机制.png)

上图为我们描述了如下几点：
1、chunk是Memcached用来存储数据的最小单位，就好像一个盒子，每个盒子里装的是我们的午饭一样。Memcache这样设计的初衷是为了尽量减少内存碎片的问题，熟练掌握内存变成的童鞋，相信对内存碎片都有 ”刻骨铭心“ 的感觉，这里就不赘述了
2、slab和page是更大一些的盒子，用于承装不同尺寸的Chunk，Chunk的大小是通过Factor【自增长因子决定】
3、不同尺寸的Chunk最终会交给一个”目录“进行管理，以便于访问，而这个目录叫slab_class.
数据访问流程： 客户端会现在slab_class里找到尺寸合适的Slab，并且通过一定的方式找到Chunk，最终保证数据会进入一个更合适的”盒子“，从而减少内存的浪费。

Redis在这一方面的处理相对简单，大致的形式如下：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\redis内存管理机制.png)

Redis每一个数据块都是根据数据类型和大小进行分配的，这一块数据的元数据（比如数据块大小）会存入内存块的头部，real_ptr是redis调用malloc后返回的指针。redis将内存块的大小size存入头部，size所占据的内存大小是已知的，为size_t类型的长度，然后返回ret_ptr。当需要释放内存的时候，ret_ptr被传给内存管理程序。通过ret_ptr，程序可以很容易的算出real_ptr的值，然后将real_ptr传给free释放内存。

总的来讲：
Memcached使用预分配的内存池的方式，使用slab和大小不同的chunk来管理内存，Item根据大小选择合适的chunk存储，内存池的方式可以省去申请/释放内存的开销，并且能减小内存碎片产生，但这种方式也会带来一定程度上的空间浪费

Redis使用现场申请内存的方式来存储数据，并且很少使用free-list等方式来优化内存分配，会在一定程度上存在内存碎片，Redis跟据存储命令参数，会把带过期时间的数据单独存放在一起，并把它们称为临时数据，非临时数据是永远不会被剔除的，即便物理内存不够，导致swap也不会剔除任何非临时数据（但会尝试剔除部分临时数据），这点上Redis更适合作为存储而不是cache。

基于以上两方面可以总结如下：
1、Redis内存空间的利用比Memcahced更精细，引入Memcached是用一个“盒子”对数据进行承载，哪怕这个盒子的尺寸再合适，也不可避免的会有空置；2、Memcached完美的解决了内存碎片的问题；3、Memcached内部还存在一个slot的机制，对内存的使用优先使用废弃内存，在内存的重复利用上也具有一定的优势；4、Redis并不是将所有内存数据都存放在内存中，只会将所有的key存放在内存，在读取的时候会有一定几率存在一次IO操作，在这一点上，Redis是使用时间换取了空间的策略；



9：Redis中采用两种算法进行内存回收，[引用计数算法](http://www.cnblogs.com/WJ5888/p/4359783.html)以及LRU算法

### 10：数据一致性保障

Redis提供了一个“事务”的概念，虽然这是一个假的事务，由于Redis是单进程操作，所以Redis的事务仅仅只是将一组操作按顺序进行操作，在这之间不会插入任何其他命令，从而保证数据的一致性，但是这种方式很容易造成操作阻塞。
Memcached提供了类似于乐观锁一样的cas操作，会快速的返回处理成功或失败，不会对其他数据操作产生影响。
**在这一点上，Memcached的速度要比Redis更快也更安全。**

### 11：Redis 大量数据插入

有些时候，Redis实例需要装载大量用户在短时间内产生的数据，数以百万计的keys需要被快速的创建。

我们称之为*大量数据插入(mass insertion)*,本文档的目标就是提供如下信息：Redis如何尽可能快的处理数据。

### 使用Luke协议

使用正常模式的Redis 客户端执行大量数据插入不是一个好主意：因为一个个的插入会有大量的时间浪费在每一个命令往返时间上。使用管道（pipelining）是一种可行的办法，但是在大量插入数据的同时又需要执行其他新命令时，这时读取数据的同时需要确保请可能快的的写入数据。

只有一小部分的客户端支持非阻塞输入/输出(non-blocking I/O),并且并不是所有客户端能以最大限度的提高吞吐量的高效的方式来分析答复。五：中间件

### 12：redis单线程是如何支持高并发的呢？

1.redis**是**基于内存的，内存的读写速度非常快；

2.redis**是**单线程的，省去了很多上下文切换线程的时间；

3：对于一个内存数据库而言，单纯的内存操作非常的快，其真正的性能瓶颈是在处理网络I/O上。redis采用I/O多路复用的方式来监听多个socket，从而提高其自身的性能。redis使用多路复用技术，可以处理**并发的**连接。非阻塞IO 内部实现采用epoll，采用了epoll+自己实现的简单的事件框架。epoll中的读、写、关闭、连接都转化成了事件，然后利用epoll的多路复用特性，绝不在io上浪费一点时间。

下面重点介绍单线程设计和IO多路复用核心设计快的原因。

### 13：为什么Redis是单线程的

#### **1.官方答案**

因为Redis**是**基于内存的操作，CPU不**是**Redis的瓶颈，Redis的瓶颈最有可能**是**机器内存的大小或者网络带宽。既然单线程容易实现，而且CPU不会成为瓶颈，那就顺理成章地采用单线程的方案了。

#### **2.性能指标**

关于redis的性能，官方网站也有，普通笔记本轻松处理每秒几十万的请求。

#### **3.详细原因**

##### **1）不需要各种锁的性能消耗**

Redis的数据结构并不全**是**简单的Key-Value，还有list，hash等复杂的结构，这些结构有可能会进行很细粒度的操作，比如在很长的列表后面添加一个元素，在hash当中添加或者删除
一个对象。这些操作可能就需要加非常多的锁，导致的结果**是**同步开销大大增加。
总之，在单线程的情况下，就不用去考虑各种锁的问题，不存在加锁释放锁操作，没有因为可能出现死锁而导致的性能消耗。

##### **2）单线程多进程集群方案**

单线程的威力实际上非常强大，每核心效率也非常高，多线程自然**是**可以比单线程有更高的性能上限，但**是**在今天的计算环境中，即使**是**单机多线程的上限也往往不能满足需要了，需要进一步摸索的**是**多服务器集群化的方案，这些方案中多线程的技术照样**是**用不上的。

所以单线程、多进程的集群不失为一个时髦的解决方案。

##### **3）CPU消耗**

采用单线程，避免了不必要的上下文切换和竞争条件，也不存在多进程或者多线程导致的切换而消耗 CPU。

但**是**如果CPU成为Redis瓶颈，或者不想让服务器其他CUP核闲置，那怎么办？

可以考虑多起几个Redis进程，Redis**是**key-value数据库，不**是**关系数据库，数据之间没有约束。只要客户端分清哪些key放在哪个Redis进程上就可以了。

### 14：主从复制，读写分离，主从切换，哨兵模式

Redis 集群是一个可以**在多个 Redis 节点之间进行数据共享**的设施（installation）。

Redis 集群**通过分区（partition）来提供一定程度的可用性**（availability）： 即使集群中有一部分节点失效或者无法进行通讯， 集群也可以继续处理命令请求。

Redis 集群提供了以下两个好处：

- 将数据自动切分（split）到多个节点的能力。
- 当集群中的一部分节点失效或者无法进行通讯时， 仍然可以继续处理命令请求的能力。

### 15：优势

- 读的速度是110000 次/s  写的速度是81000 次/ 秒
- 丰富的数据类型
- 原子性：redis的所有操作都是原子性，

### 3：springboot集成redis

#### (1)：添加依赖

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
</dependencies>
```

#### (2)：添加application.properties配置

```yaml
redis:
    database: 4 #Redis数据库索引（默认为0）
    host: 10.59.78.21 #Redis服务器地址
    port: 6379 #Redis服务器连接端口
    password: # Redis服务器连接密码（默认为空）
    pool:
      lettuce:
        max-active: 8 #连接池最大连接数（使用负值表示没有限制）
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
        max-idle: 8 #连接池中的最大空闲连接
        min-idle: 0 # 连接池中的最小空闲连接
    timeout: 5000  #连接超时时间（毫秒）
```

#### (3) RedisConfig配置类

```java
@Configuration //纳入spring 管理
@EnableCaching //开启缓存
public class CacheConfiguration extends CachingConfigurerSupport {
    private Logger log = LoggerFactory.getLogger(CacheConfiguration.class);
    @Bean
    CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        FastJsonRedisSerializer<Object> jackson2JsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60*60))
.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)).disableCachingNullValues();
        RedisCacheManager cacheManager =

                RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();

        return cacheManager;
    }
    
 //这个配置也可以单独 提取一个类进行配置   
 @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        //Helper类简化了Redis数据访问代码
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        FastJsonRedisSerializer<Object> jackson2JsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }
}
```

## 

## （二） RabbitMQ ：（RabbitMQ 是典型的内存式堆积，支持事务）

​	采用 Erlang 语言实现的 AMQP 协议的消息中间件，最初起源于金融系统，用于在分布式系统中存储转发消息。RabbitMQ 发展到今天，被越来越多的人认可，这和它在可靠性、可用性、扩展性、功能丰富等方面的卓越表现是分不开的。

介绍：

- broker：每个节点运行的服务程序，功能为维护该节点的队列的增删以及转发队列操作请求。

- master queue：每个队列都分为一个主队列和若干个镜像队列。

- mirror queue：镜像队列，作为master queue的备份。在master queue所在节点挂掉之后，系统把mirror queue提升为master queue，负责处理客户端队列操作请求。注意，mirror queue只做镜像，设计目的不是为了承担客户端读写压力。

  ![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rabbit-mq.png)

​        如上图有两个consumer消费队列A，这两个consumer连在了集群的不同机器上。RabbitMQ集群中的任何一个节点都拥有集群上所有队列的元信息，所以连接到集群中的任何一个节点都可以，主要区别在于有的consumer连在master queue所在节点，有的连接在非master queue节点上。

​	因为mirror queue要和master queue保持一致，故需要同步机制，正因为一致性的限制，导致**所有的读写操作都必须都操作在master queue上**（想想，为啥读也要从master queue中读？和数据库读写分离是不一样的。），然后由master节点同步操作到mirror queue所在的节点。**即使consumer连接到了非master queue节点，该consumer的操作也会被路由到master queue所在的节点上，这样才能进行消费**。

#### 比较重要的概念有 4 个，分别为：虚拟主机，交换机，队列，和绑定。*

- 虚拟主机：一个虚拟主机持有一组交换机、队列和绑定。为什么需要多个虚拟主机呢？很简单，RabbitMQ当中，*用户只能在虚拟主机的粒度进行权限控制。* 因此，如果需要禁止A组访问B组的交换机/队列/绑定，必须为A和B分别创建一个虚拟主机。每一个RabbitMQ服务器都有一个默认的虚拟主机“/”。

- 交换机：Exchange 用于转发消息，**但是它不会做存储*** ，**如果没有 Queue bind 到 Exchange 的话，它会直接丢弃掉 Producer 发送过来的消息**。 这里有一个比较重要的概念：路由键 。消息到交换机的时候，交互机会转发到对应的队列中，那么究竟转发到哪个队列，就要根据该路由键。
- 绑定：也就是交换机需要和队列相绑定，这其中如上图所示，是多对多的关系。

- 交换机(Exchange)：交换机的功能主要是接收消息并且转发到绑定的队列，交换机不存储消息，在启用ack模式后，交换机找不到队列会返回错误。交换机有四种类型：Direct, topic, Headers and Fanout
  - Direct：direct 类型的行为是”先匹配, 再投送”. 即在绑定时设定一个 **routing_key**, 消息的 **routing_key** 匹配时, 才会被交换器投送到绑定的队列中去.
  - Topic：按规则转发消息（最灵活）
  - Headers：设置header attribute参数类型的交换机
  - Fanout：转发消息到所有绑定队列
  - Direct Exchange：Direct Exchange是RabbitMQ默认的交换机模式，也是最简单的模式，根据key全文匹配去寻找队列。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rabbitmq_dirctExchange.jpg)

第一个 X - Q1 就有一个 binding key，名字为 orange； X - Q2 就有 2 个 binding key，名字为 black 和 green。*当消息中的 路由键 和 这个 binding key 对应上的时候，那么就知道了该消息去到哪一个队列中。*

Ps：为什么 X 到 Q2 要有 black，green，2个 binding key呢，一个不就行了吗？ - 这个主要是因为可能又有 Q3，而Q3只接受 black 的信息，而Q2不仅接受black 的信息，还接受 green 的信息。

- **Topic Exchange**

*Topic Exchange 转发消息主要是根据通配符。* 在这种交换机下，队列和交换机的绑定会定义一种路由模式，那么，通配符就要在这种路由模式和路由键之间匹配后交换机才能转发消息。

在这种交换机模式下：

- 路由键必须是一串字符，用句号（.） 隔开，比如说 agreements.us，或者 agreements.eu.stockholm 等。

- 路由模式必须包含一个 星号（* )，主要用于匹配路由键指定位置的一个单词，比如说，一个路由模式是这样子：agreements..b.*，那么就只能匹配路由键是这样子的：第一个单词是 agreements，第四个单词是 b。 井号（#）就表示相当于一个或者多个单词，例如一个匹配模式是agreements.eu.berlin.#，那么，以agreements.eu.berlin开头的路由键都是可以的。

具体代码发送的时候还是一样，第一个参数表示交换机，第二个参数表示routing key，第三个参数即消息。如下：

```Java
rabbitTemplate.convertAndSend("testTopicExchange","key1.a.c.key2", " this is  RabbitMQ!");
```

topic 和 direct 类似, 只是匹配上支持了”模式”, 在”点分”的 routing_key 形式中, 可以使用两个通配符:

- - *** **表示一个词
  - **# **表示零个或多个词
- **Headers Exchange**

headers 也是根据规则匹配, 相较于 direct 和 topic 固定地使用 routing_key , headers 则是一个自定义匹配规则的类型. 在队列与交换器绑定时, 会设定一组键值对规则, 消息中也包括一组键值对( headers 属性), 当这些键值对有一对, 或全部匹配时, 消息被投送到对应队列.

- **Fanout Exchange**

Fanout Exchange 消息广播的模式，不管路由键或者是路由模式，会把消息发给绑定给交换机的全部队列，如果配置了routing_key会被忽略。

#### springboot集成RabbitMQ（https://netfilx.github.io/spring-boot/8.springboot-rabbitmq/springboot-rabbitmq）

springboot集成RabbitMQ非常简单，如果只是简单的使用配置非常少，springboot提供了spring-boot-starter-amqp项目对消息各种支持。

#### 生产者

##### 1、配置pom包，主要是添加spring-boot-starter-amqp的支持

```Xml
<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

##### 2、配置文件

配置rabbitmq的安装地址、端口以及账户信息

```properties
#默认rabbitmq
#rabbitmq配置
spring.rabbitmq.addresses = 10.131.110.58
spring.rabbitmq.username = bmapplOrderMq
spring.rabbitmq.password = FuKbpFNiwpDSDp1W
spring.rabbitmq.port = 5672
spring.rabbitmq.virtualHost = /
spring.rabbitmq.default.prefetch.count = 1
spring.rabbitmq.listener.concurrency = 100
spring.rabbitmq.listener.max-concurrency = 20000
spring.rabbitmq.executor.corePoolSize = 100
spring.rabbitmq.executor.maxPoolSize = 25500
spring.rabbitmq.executor.queueCapacity = 500
spring.rabbitmq.executor.keepAliveSeconds = 60
spring.rabbitmq.container.concurrentConsumers = 1
#channel缓存空间，must be 1 or higher
spring.rabbitmq.channelCacheSize = 20
#是否批量否定应答true为批量，false单条
spring.rabbitmq.container.basicNack.multiple = false
#否定确认响应是否删除消息，fale删除，true重新入队列
spring.rabbitmq.container.basicNack.requeue = false
#模拟生产者测试
spring.rabbitmq.test.producer.switchStatus = on
spring.rabbitmq.test.producer.sendCount = 1

#第二个
spring.rabbitmq.app.host = 10.19.160.171
spring.rabbitmq.app.password = Odgc1u29wT01OEav
spring.rabbitmq.app.port = 5672
spring.rabbitmq.app.username = bmapplOrderMq
```

##### 3、队列配置（多rabbitmq）

```java
@Data
public abstract class AbstractRabbitConfiguration {
    private String host;
    private int port;
    private String username;
    private String password;
    protected ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

}

@Configuration
@ConfigurationProperties("spring.rabbitmq.app")
public class AppRabbitConfiguration extends AbstractRabbitConfiguration {
    
@Bean(name = "AppConnectionFactory")
@Primary
    public ConnectionFactory appConnectionFactory() {   
        return super.connectionFactory();
    }
    
  @Bean(name = "hatchConnectionFactory")
    public ConnectionFactory hatchConnectionFactory(@Value("${spring.rabbitmq.hatch.address}") String address, @Value("${spring.rabbitmq.hatch.username}") String username,
@Value("${spring.rabbitmq.hatch.password}") String password){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //集群用这个属性，例如192.168.1.105:6372,192.168.1.106:6572
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }   
    @Bean(name = "appRabbitTemplate")
    @Primary
    public RabbitTemplate appRabbitTemplate(@Qualifier("AppConnectionFactory") ConnectionFactory connectionFactory,@Autowired RabbitMqCallBack hatchCallBack) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setEncoding("utf-8");
        //Set the mandatory flag when sending messages; only applies if a
	// {@link #setReturnCallback(ReturnCallback) returnCallback} had been provided.
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setRetryTemplate(retryTemplate());
        rabbitTemplate.setReturnCallback(hatchCallBack);
        rabbitTemplate.setConfirmCallback(hatchCallBack);
        return new RabbitTemplate(connectionFactory);
    }
    private RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy());
        retryTemplate.setRetryPolicy(retryPolicy());
        return retryTemplate;
    }
    private SimpleRetryPolicy retryPolicy() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        return retryPolicy;
    }
    private ExponentialBackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMaxInterval(10000);
        backOffPolicy.setMultiplier(1.5);
        return backOffPolicy;
    }

    @Bean(name = "appFactory")
    public SimpleRabbitListenerContainerFactory firstFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,                                                         @Qualifier("AppConnectionFactory") ConnectionFactory connectionFactory) {    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();    configurer.configure(factory, connectionFactory);    
    return factory;
    }
    
    @Bean(name = "appRabbitAdmin")
    public RabbitAdmin appRabbitAdmin(@Qualifier("AppConnectionFactory") ConnectionFactory connectionFactory) {   
        return new RabbitAdmin(connectionFactory);
    }
}
```

##### 4、发送消息（多rabbitmq）

```java
@Component
public class RabbitMqProducer {
    private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMqProducer.class);
    @Autowired
    @Resource(name= "appRabbitTemplate")
     private RabbitTemplate appRabbitTemplate;
    @Autowired
    @Resource(name= "guoRabbitTemplate")
    private RabbitTemplate guoRabbitTemplate;
    @Autowired
    @Resource(name = "hatchRabbitTemplate")
    private RabbitTemplate hatchRabbitTemplate;
    @Autowired
    private DingDingService dingDingService;


    /**
     * rabbitmq发送消息
     * 发送消息实体 必须实现 Serializable
     * @param message
     */
     public void sendRabbitMqMessage(String message,String exchange,String routeKey){
         appRabbitTemplate.convertAndSend(exchange, routeKey,message,new CorrelationData(UUID.randomUUID().toString()));
     }
     /**
     * rabbitmq发送孵化中心消息
     * 发送消息实体 必须实现 Serializable
     * @param message
     */
     public void sendHatchRabbitMqMessage(String message,String exchange,String routeKey){
         hatchRabbitTemplate.convertAndSend(exchange, routeKey,message,new CorrelationData(UUID.randomUUID().toString()));
     }

     /**
     * rabbitmq发送裹裹消息
     * 发送消息实体 必须实现 Serializable
      *
     * @param message
     */
     public Object sendGuoRabbitMqMessage(String message,String exchange,String routeKey){
         try {
             return  guoRabbitTemplate.convertSendAndReceive(exchange, routeKey, message,new CorrelationData(UUID.randomUUID().toString()));
         } catch (Exception e) {
             LOGGER.error("发送到rabbiitMq的消息失败");
             System.err.println(e);
             return null;
         }
     }
     /**
     * rabbitmq发送揽收消息
     * 发送消息实体 必须实现 Serializable
     * @param message
     */
     public void sendAppRabbitMqMessages(String message,String exchange,String routeKey,Channel channel){
         LOGGER.info("发送到揽收rabbiitMq的消息：{}", message);
         try {
             channel.basicPublish(exchange,routeKey,getBasicProperties(),message.getBytes());
         } catch (IOException e) {
             LOGGER.error("发送到揽收rabbiitMq的消息失败");
             dingDingService.sendMessage(dingDingService.buildDingDingMessage("发送到揽收rabbiitMq的消息失败", JSON.toJSONString(message), e));
             System.err.println(e);
         }finally{
             try {
                 if(channel != null){
                     channel.close();
                 }
             }catch (Exception e) {
                 LOGGER.error("揽收channel关闭异常");
             }
         }
     }
     /**
     * rabbitmq发送裹裹消息
     * 发送消息实体 必须实现 Serializable
     * @param message
     */
     public void sendGuoRabbitMqMessages(String message,String exchange,String routeKey,Channel channel){
         try {
             channel.basicPublish(exchange,routeKey,getBasicProperties(),message.getBytes());
         } catch (IOException e) {
             LOGGER.error("发送到rabbiitMq的消息失败");
             dingDingService.sendMessage(dingDingService.buildDingDingMessage("发送到rabbiitMq的消息失败", JSON.toJSONString(message), e));
             System.err.println(e);
         }finally{
             try {
                 if(channel != null){
                     channel.close();
                 }
             }catch (Exception e) {
                 LOGGER.error("裹裹channel关闭异常");
             }
         }
     }

         private AMQP.BasicProperties getBasicProperties(){
             return new AMQP.BasicProperties.Builder().
                     contentType(ContentTypeEnum.JSON.getDec())
                     .contentEncoding("UTF-8")
                     .deliveryMode(2)//消息持久化
                     .type(ExchangeTypes.DIRECT)
                     .priority(0)
                     .build();
         }
}
```

```java
   /**
     * 获取channel
     * Connection 可以用来创建多个 Channel 实例，但是 Channel 实例不能在线程问共享，应用程序应该为每一个线程开		辟一个 Channel 。某些情况下 Channel 的操作可以并发运行，但是在其他情况下会导致在网络上出现错误的通信帧交		错，同时也会影响友送方确认（publisherconfirm ）机制的运行，所以多线程问共享 Channel 实例是非线程安全的
  *
  *channel.exchangeDeclare各个参数详细说明如下所述。
   exchange 交换器的名称。
   type 交换器的类型，常见的如 fanout direct topic 详情参见 2.1.4 节。
   durable ：设置是否持久 durab 设置为 true 表示持久化， 反之是非持久化。持可以将交换器存盘，在服务器重启 的时			候不会丢失 关信息。
   autoDelete 设置是否自动删除。 autoDelete 设置为 true 表示自动删除。自动删除的前提是至少有 1个队列或者交换器			 与这个交换器绑定 之后所有与这个交换器绑 定的队列或者交换器都与 解绑。注意不能错误地 这个参数理解为 “当			与此交换器 连接的客户端都断开时 RabbitMQ 会自动 除本交换器
   internal 设置是否是内置的。如果设置为 true ，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，		  只能通过交换器路由到交换器这种方式。
   argument 其他一些结构化参数，比如 ternate exchange
*
*queueDeclare方法的参数详细说明如下所述。
 queue ：队列的名称。
 durable ：设置是否持久化。为 true 则设置队列为持久化。持久化的队列会存盘，在服务器重启的时候可以保证不丢失相关			信息。
 exclusive ：设置是否排他。为 true 则设置队列为排他的。如果一个队列被声明为排
        他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除。这里需要注意
        三点：排他队列是基于连接（ Connection ）可见的，同 个连接的不同信道（ Channel)
        是可以同时访问同一连接创建的排他队列：“首次”是指如果 个连接己经声明了
        排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同：即使该队
        列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除，这种队列
        适用于一个客户端同时发送和读取消息的应用场景。
 autoDelete ：设置是否自动删除。为 true 则设置队列为自动删除。自动删除的前提是：
至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会自动删除。不能把这个参数错误地理解为：“当连接到此队列的所有客户端断开时，这个队列自动删除”，因为生产者客户端创建这个队列，或者没有消费者客户端与这个队列连接时，都不会自动删除这个队列。
 arguments ：设置队列的其他一些参数，如 x-rnessage-ttl x-expires-rnax-length x-rnax-length-bytes x-dead-letter-exchange x-deadletter-routing-key, x-rnax-priority 等。
*
* queueBind方法中涉及的参数详解。
* queue ：队列名称：
  exchange ：交换器的名称：
  routingKey 用来绑定队列和交换器的路由键；
  argument ：定义绑定的一些参数。
*/
    private Channel getGuoChannel(){
        Channel channel = null;
        try {
            channel = appRabbitTemplate.getConnectionFactory().createConnection().createChannel(false);
            channel.exchangeDeclare(RabbitConfig.EXCHANGE_ORDERS, ExchangeTypes.DIRECT, true);
            channel.queueDeclare(RabbitConfig.ORDER_QUEUE, true, false, false, null);
            channel.queueBind(RabbitConfig.ORDER_QUEUE, RabbitConfig.EXCHANGE_ORDERS,  RabbitConfig.ORDER_ROUTING_KEY);
        } catch (IOException e) {
            LOGGER.error("获取揽收channel失败");
            dingDingService.sendMessage(dingDingService.buildDingDingMessage("获取揽收channel失败", null, e));
        }
        return channel;
    }
//confirm模式
 //开启confirm模式
  private  ReturnT sendRabbitMq(List<Map<String, Object>> appSendMapList){
    guoChannel.confirmSelect();
      //rabbitmq确认发送
    guoChannel.waitForConfirms()

```

```java
/** 
* 确认机制 
* @author 
* 如果消息没有到exchange,则confirm回调,ack=false,
  如果消息到达exchange,则confirm回调,ack=true
  exchange到queue成功,则不回调return
  exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
*/
@Component
public class RabbitMqCallBack implements RabbitTemplate.ConfirmCallback , RabbitTemplate.ReturnCallback {   
    private Logger LOGGER = LoggerFactory.getLogger(RabbitMqCallBack.class);    
    @Autowired    
    private DingDingService dingDingService;    
    /**     
    * 消息到达exchange失败回调     
    * @param correlationData     
    * @param ack     
    * @param cause     
    */    
    @Override    
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {       		            if(correlationData != null){            
        String id = correlationData.getId();            
        if(ack){                
            LOGGER.info( "rabbitmq.sendExchange【{}】.success", id);            
        }else {                
            LOGGER.error("rabbitmq.sendExchange【{}】.fail", id);                           			dingDingService.sendMessage(dingDingService.buildDingDingMessage("rabbitmq.sendExchange.fail", id, null));            
        }       
    }    
}    
    /**     
    * 从exchange到queue失败回调     
    * @param message     
    * @param replyCode     
    * @param replyText     
    * @param exchange     
    * @param routingKey     
    */    
    @Override    
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {        
        String msgStr = new String(message.getBody(), StandardCharsets.UTF_8);                LOGGER.error("MQ消息推送队列失败,msgStr:" + msgStr +",replyCode:"+ replyCode + "replyText:"+ replyText +",exchange:"+ exchange +",routingKey:"+ routingKey);        dingDingService.sendMessage(dingDingService.buildDingDingMessage("replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey, msgStr, null));    }}
```

#### 消费者：

rabbitMQ 的消费模式分两种 推（ Push）模式和拉（ Pull ）模式 推模式采用 Basic Consume进行消费，而拉模式则是调用 Basic Get 进行消费。

多个消费者可以订阅同一个队列，这时队列中的消息会被平均分摊（Round-Robin ，即轮询）给多个消费者进行处理，而不是每个消费者都收到所有的消息井处理。Rabbi MQ 不支持队列层面的广播消费，如果需要广播消费，需要在其上进行2 次开发，同时也不建议这样做(很多时候轮询的分发机制也不是那么优雅。默认情况下，**如果有n个消费者，那么RabbitMQ会将第m 条消息分发给第 m%n 个消费者（取余的方式）， RabbitMQ 不管消费者是否消费并己经确认（ Basic.Ack ）了消息**。试想一下，如果某些消费者任务繁重，来不及消费那么多的消息，而某些其他消费者由于某些原因（比如业务逻辑简单、机器性能卓越等）很快地处理完了所分配到的消息，进而进程空闲，这样就会造成整体应用吞吐量的下降。那么该如何处理这种情况呢？这里就要用到 **channel.basicQos(int prefetchCount)** 这个方法(**Basic.Qos 的使用对于拉模式的消费方式无效**)，如前面章节所述， channel.basicQos 方法允许限制信道上的消费者所能保持的最大未确认消息的数量)。

消息变成死信 般是由于以下几种情况：

〈1〉消息被拒绝（ Basic.Reject/Basic.Nack ），井且设置 requeue 参数为 false;

〈2〉消息过期；

〈3〉队列达到最大长度。

## （三）Kafka （Kafka 是一种典型的磁盘式堆积，支持事务）

​	起初是由 LinkedIn 公司采用 Scala 语言开发的一个分布式、多分区、多副本且基于 zookeeper 协调的分布式消息系统，现已捐献给 Apache 基金会。它是一种高吞吐量的分布式发布订阅消息系统，以可水平扩展和高吞吐率而被广泛使用。目前越来越多的开源分布式处理系统如 Cloudera、Apache Storm、Spark、Flink 等都支持与 Kafka 集成。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\kafka.png)

​	说实话，Kafka我觉得就是看到了RabbitMQ这个缺陷才设计出的一个改进版，改进的点就是：把一个队列的单一master变成多个master，即一台机器扛不住qps，那么我就用多台机器扛qps，把一个队列的流量均匀分散在多台机器上；这里面的每个master queue 在Kafka中叫做Partition，即一个分片。一个队列有多个主分片，每个主分片又有若干副分片做备份，同步机制类似于RabbitMQ。

### 1:分布式

1. log分区分布到kafka集群多个服务器上

2. 每个分区还可以复制到其他服务器作为备份容错

3. 每个分区有一个leader和>=0个follower,

   leader处理此分区的所有读写请求,而follower只做备份

   .leader宕机,则推举一个follower为新的leader

   > 有消息写入时,是先写入leader,follower再从leader拉取在kafka中读写操作都是leader

4. 为平衡负载,一台服务器可能是一个分区的leader,同时是另一个分区的follower

### 2:生产者

往Topic上发消息时,需要选择发布到Topic上的哪一个分区;可以从分区表中轮流选择,也可以按某种权重选择分区（发送到某个分区的leader上）；

**producer采用push模式将消息发布到broker 中，每条消息都被追加到partition 中，消息发送时都被发送到一个topic，其本质就是一个目录，而topic是由一些Partition Logs分区日志组成，每个partition 都是有序的，有消息写入时,是先写入leader,follower再从leader拉取 在kafka中读写操作都是leader。**

**注意，多个master之间的数据没有交集，即一条消息要么发送到这**个master queue，要么发送到另外一个master queue。**

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\kafka-consume.png)

​	队列读取的时候虚拟出一个Group的概念，**一个Topic内部的消息，只会路由到同Group内的一个consumer上**，同一个Group中的consumer消费的消息是不一样的；Group之间共享一个Topic，看起来就是一个队列的多个拷贝。所以，为了达到多个Group共享一个Topic数据，**Kafka并不会像RabbitMQ那样消息消费完毕立马删除**，而是必须在后台配置保存日期，即只保存最近一段时间的消息，超过这个时间的消息就会从磁盘删除，这样就保证了在一个时间段内，Topic数据对所有Group可见（这个特性使得Kafka非常适合做一个公司的数据总线）。队列读同样是读主分片，并且为了优化性能，消费者与主分片有一 一的对应关系，如果消费者数目大于分片数，则存在某些消费者得不到消息。

### 3:消费者

#### 1)消息模型

- 队列
  一条消息只有其中的一个消费者来处理
- 发布-订阅模式
  消息被广播给**所有**的消费者，接收到消息的消费者都可以处理此消息

#### 2)消费者抽象模型

- 消费者组(consumer group)消费者用一个消费者组名标记自己。**一个发布在Topic上消息被分发给此消费者组中的一个消费者**,每个ConsumerGroup消费的数据都是一样的
- 所有消费者都在一个消费者组中
  队列模型,只有**一个**消费者接收到信息
- 所有消费者都在不同组中
  发布-订阅模型,每一个组都可以接收到,**所有**消费者都接收到

#### 3)集成springboot

##### 1：引入依赖

```xml
  <dependency>
      <groupId>org.springframework.kafka</groupId>
      <artifactId>spring-kafka</artifactId>
      <version>2.2.0.RELEASE</version>
      <scope>compile</scope>
      <optional>true</optional>
      
       <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
        </dependency>
 </dependency>
```

##### 2：application.yml添加kafka相关配置

```yaml
 ##kafka相关配置
  kafka:
    #集群（多个IP:Port 中间用逗号隔开）
    #bootstrap-servers: dev-kafka001:9092,dev-kafka002:9092,dev-kafka003:9092
    bootstrap-servers: 10.59.74.137:9092,10.59.74.138:9092,10.59.74.139:9092
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      group-id: paas_field_local #一个字符串用来指示一组consumer所在的组
      max-poll-records: 1  #一次拉取消息的最大数，我们这里一次拉取一条是为了保证消息的优先级
      auto-commit-interval-ms: 10000 #往zookeeper上写offset的频率
      enable-auto-commit: true
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

##### 3:如何发送消息，消费消息

```java
@Component
public class KafkaProducer {
    private static final Logger Logger = LoggerFactory.getLogger(KafkaProducer.class);
    @Autowired
    private KafkaTemplate kafkaTemplate;
    /**
     * 发送kafka消息
     * @param topic
     * @param message
     */
    public void sendKafkaMessage(String topic, String message){
        Logger.info("sendKafkaMessage:{}:{}",topic , message);
        Object rst =  kafkaTemplate.send(topic, message);
        Logger.info("sendKafkaMessageEnd:{}", JSON.toJSONString(rst));
    }
}
```

```java
@Component
public class KafkaConsumer {
    private final static Logger LOGGER = getLogger(KafkaConsumer.class);
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TemplateTypeService templateTypeService;
    /**
     * 权限变更后接受消息
     * 数据格式 {"accountIds":[111,236],"projectId":1113}
     * @param message
     */
    @KafkaListener(topics = {"${kafka.consumer.updateProjectRole.topic}"})
    public void updateProjectRoleConsumer(String message) {
        LOGGER.info("角色变更接收到kafka消息：{}", message);
        UpdateProjectRoleDTO updateProjectRoleDTO = JSON.parseObject(message, UpdateProjectRoleDTO.class);
        List<Long> accountIdList = updateProjectRoleDTO.getAccountIds();
        for (Long accountId : accountIdList){
            if (accountId != null){
                redisClient.delete(PERMISSION_NAMESPACE, PERMISSION_LIST_KEY + ":" + accountId + ":" + updateProjectRoleDTO.getProjectId());
            }
        }
    }
}

//举例子
   @KafkaListener(topics = "topic_name")
    public void listen (ConsumerRecord<?, ?> record) throws Exception {
        System.out.printf("topic = %s, offset = %d, value = %s \n", record.topic(), record.offset(), record.value());
    }
```

##### 4:多kafka

```java
@Configuration
@EnableKafka
public class TagKafkaTemplateConfiguration {

    @Value("${spring.oos.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.oos.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.oos.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;
    @Value("${spring.oos.kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitIntervalMs;
    @Value("${spring.oos.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.oos.kafka.consumer.auto-offset-reset}")
    private String autoffsetReset;
    @Value("${spring.oos.kafka.consumer.key-deserializer}")
    private String keyDeserializer;
    @Value("${spring.oos.kafka.consumer.value-deserializer}")
    private String valueDeserializer;
    @Value("${spring.oos.kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${spring.oos.kafka.producer.value-serializer}")
    private String valueSerializer;

    /**
     * 发送消息的template
     * @return
     */
   /* @Bean(name="oosKafkaTemplate")
    public KafkaTemplate<String, String> oosKafkaTemplate() {
       return new KafkaTemplate<>(oosProducerFactory());
    }*/

    /**
     * 生产KafkaTemplate工厂
     * @return
     */
   /* private ProducerFactory<String, String> oosProducerFactory() {
        return new DefaultKafkaProducerFactory<>(oosKafkaProducerProperties());
    }*/

    /**
     * 生产KafkaTemplate工厂
     * @return
     */
   /* private Map<String, Object> oosKafkaProducerProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        return props;
    }*/
    /**
     * oos消费工厂
     * @return
     */
    @Bean(name = "oosKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> oosKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        //initialize the specified container.
        factory.setConsumerFactory(oosConsumerFactory());
        factory.setConcurrency(2);
        //The maximum number of concurrent {@link KafkaMessageListenerContainer}s running   默认是1，建议和副本数一致.
        //factory.setReplyTemplate(oosKafkaTemplate());
        //factory.setRetryTemplate();
        //kafka poll拉取的超时时间
        factory.getContainerProperties().setPollTimeout(60000);
        return factory;
    }


    /**
     *  oos消费的KafkaTemplate工厂
     * @return
     */
    private ConsumerFactory<Integer, Object> oosConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(oosConsumerConfigs());
    }

    /**
     * oos消费的 kafka消费配置
     * @return
     */
    private Map<String, Object> oosConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoffsetReset);
        return props;
    }

   /* @Bean
    public KafkaAdmin oosKafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(props);
        //admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    public AdminClient oosAdminClient() {
        return AdminClient.create(oosKafkaAdmin().getConfig());
    }
*/

    /**
     * 打标kafka 集群配置
     */
    @Value("${spring.tag.kafka.bootstrap-servers}")
    private String bootstrapServersByTag;
    @Value("${spring.tag.kafka.consumer.group-id}")
    private String groupIdByTag;
    @Value("${spring.tag.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommitByTag;
    @Value("${spring.tag.kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitIntervalMsByTag;
    @Value("${spring.tag.kafka.consumer.max-poll-records}")
    private String maxPollRecordsByTag;
    @Value("${spring.tag.kafka.consumer.auto-offset-reset}")
    private String autoffsetResetByTag;
    @Value("${spring.tag.kafka.consumer.key-deserializer}")
    private String keyDeserializerByTag;
    @Value("${spring.tag.kafka.consumer.value-deserializer}")
    private String valueDeserializerByTag;
    @Value("${spring.tag.kafka.producer.key-serializer}")
    private String keySerializerByTag;
    @Value("${spring.tag.kafka.producer.value-serializer}")
    private String valueSerializerByTag;

    /**
     * 发送消息的template
     * @return
     *
     */
    @Bean(name="tagKafkaTemplate")
    @Primary
    public KafkaTemplate<String, String> tagKafkaTemplate() {
        return new KafkaTemplate<>(tagProducerFactory());
    }
    /**
     * 生产KafkaTemplate工厂
     * @return
     */
    private ProducerFactory<String, String> tagProducerFactory() {
        return new DefaultKafkaProducerFactory<>(tagProducerConfigs());
    }

    /**
     * 属性配置
     * @return
     */
    private Map<String, Object> tagProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersByTag);
        /**
         * 当从broker接收到的是临时可恢复的异常时，生产者会向broker重发消息，但是不能无限
         * 制重发，如果重发次数达到限制值，生产者将不会重试并返回错误。
         * 通过retries属性设置。默认情况下生产者会在重试后等待100ms，可以通过 retries.backoff.ms属性进行修改
         */
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        /**
         * 在考虑完成请求之前，生产者要求leader收到的确认数量。这可以控制发送记录的持久性。允许以下设置：
         * <ul>
         * <li>
         * <code> acks = 0 </ code>如果设置为零，则生产者将不会等待来自服务器的任何确认。该记录将立即添加到套接字缓冲区并视为已发送。在这种情况下，无法保证服务器已收到记录，并且
         * <code>retries </ code>配置将不会生效（因为客户端通常不会知道任何故障）。为每条记录返回的偏移量始终设置为-1。
         * <li> <code> acks = 1 </code>
         * 这意味着leader会将记录写入其本地日志，但无需等待所有follower的完全确认即可做出回应。在这种情况下，
         * 如果leader在确认记录后立即失败但在关注者复制之前，则记录将丢失。
         * <li><code> acks = all </code>
         * 这意味着leader将等待完整的同步副本集以确认记录。这保证了只要至少一个同步副本仍然存活，记录就不会丢失。这是最强有力的保证。
         * 这相当于acks = -1设置
         */
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        /**
         * 当有多条消息要被发送到统一分区是，生产者会把他们放到统一批里。kafka通过批次的概念来 提高吞吐量，但是也会在增加延迟。
         */
        // 以下配置当缓存数量达到16kb，就会触发网络请求，发送消息
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // 每条消息在缓存中的最长时间，如果超过这个时间就会忽略batch.size的限制，由客户端立即将消息发送出去
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerByTag);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerByTag);
        return props;

    }

    /**
     * oos消费工厂
     * @return
     */
    @Bean(name = "tagKafkaListenerContainerFactory")
    @Primary
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> tagKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        //initialize the specified container.
        factory.setConsumerFactory(tagConsumerFactory());
        //The maximum number of concurrent {@link KafkaMessageListenerContainer}s running   默认是1，建议和副本数一致.
        factory.setConcurrency(2);
        //factory.setReplyTemplate(oosKafkaTemplate());
        //factory.setRetryTemplate();
        //kafka poll拉取的超时时间
        factory.getContainerProperties().setPollTimeout(60000);
        return factory;
    }


    /**
     *  oos消费的KafkaTemplate工厂
     * @return
     */
    private ConsumerFactory<Integer, Object> tagConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(tagConsumerConfigs());
    }

    /**
     * kafka消费配置
     * @return
     */
    private Map<String, Object> tagConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersByTag);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdByTag);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommitByTag);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMsByTag);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecordsByTag);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerByTag);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerByTag);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoffsetResetByTag);
        return props;
    }

  /*  @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setContainerFactory(oosKafkaListenerContainerFactory());
        registrar.setContainerFactoryBeanName("oosKafkaListenerContainerFactory");
        registrar.setContainerFactoryBeanName("tagKafkaListenerContainerFactory");
    }*/
}
```

```java
/**
 * 消费二维码的数据
 */
@Component
@Slf4j
public class YdecConsumer {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private DingDingService dingDingService;
    @Value("${service.kafka.centerorder-settlement.topic:}")
    private String sendTopic;
    @Value("${service.kafka.centerorder-ding.topic:}")
    private String sendDingTopic;
    @Autowired
    private RedisClusterService redisService;
  //containerFactory 指明配置中的哪个kafka
    @KafkaListener(containerFactory = "oosKafkaListenerContainerFactory",topics = {"${service.kafka.centerorder-ydec.topic}"}, groupId = "${service.kafka.ordercenter-oos-tag.groupId}")
    private void consumerExpressPushToSettlement(ConsumerRecord<String, String> record) {
        List<String> range = redisService.range(OrderTagRedisKeyConstants.TAG_YDEC);
        redisService.del(OrderTagRedisKeyConstants.TAG_YDEC);
        if(!CollectionUtils.isEmpty(range)){
        range.forEach(settle -> {
            SettlementOrderTagInfo settlementOrderTagInfo = JSON.parseObject(settle, SettlementOrderTagInfo.class);
                try {
                    kafkaProducer.sendMessage(sendTopic, JSON.toJSONString(settlementOrderTagInfo));
                    log.info("ydec.resend.param:【{}】",settlementOrderTagInfo.toString());
                } catch (Exception e) {
                    redisService.lPush(OrderTagRedisKeyConstants.TAG_YDEC, JSON.toJSONString(settlementOrderTagInfo));
                    kafkaProducer.sendMessage(sendDingTopic,dingDingService.buildDingDingMessage("推送逆向物流历史数据异常","receive.ydec.order.error",e));
                }
            });
        }
        String result = record.value();
        if(StringUtils.isEmpty(result)){
            log.info("receive.ydec.result.is.null!");
        }
        //log.info("receive.ydec.result:{}",result);
        SettlementOrderTagInfo settlementOrderTagInfo = new SettlementOrderTagInfo();
        try {
            //转化数据
            YdecOrderData ydecOrderData = JSON.parseObject(record.value(), YdecOrderData.class);
            if(ObjectUtils.isEmpty(ydecOrderData)){
                log.info("receive.ydec.transfer.result.is.null!");
                return;
            }
            log.info("receive.ydec.result:{}",ydecOrderData.toString());
            settlementOrderTagInfo.setMailNo(ydecOrderData.getMailno());
            settlementOrderTagInfo.setCreateTime(DateUtils.format(ydecOrderData.getCreateTime()));
            if(OrderTagEnum.INSURED.getValue().equalsIgnoreCase(ydecOrderData.getOrderType()) || "support_value".equalsIgnoreCase(ydecOrderData.getOrderType())){
                settlementOrderTagInfo.setTag(OrderTagEnum.INSURED.getValue());
            }else if(OrderTagEnum.TOPAY.getValue().equalsIgnoreCase(ydecOrderData.getOrderType()) || "df".equalsIgnoreCase(ydecOrderData.getOrderType())){
                settlementOrderTagInfo.setTag(OrderTagEnum.TOPAY.getValue());
            }
            if(!StringUtils.isEmpty(settlementOrderTagInfo.getTag())){
                kafkaProducer.sendMessage(sendTopic, JSON.toJSONString(settlementOrderTagInfo));
                log.info("ydec.send.param:【{}】",settlementOrderTagInfo.toString());
            }
            if(ydecOrderData.getZffs() != null && 1 == ydecOrderData.getZffs()){
                settlementOrderTagInfo.setTag(OrderTagEnum.PRIVACY.getValue());
                if(!StringUtils.isEmpty(settlementOrderTagInfo.getTag())){
                    kafkaProducer.sendMessage(sendTopic, JSON.toJSONString(settlementOrderTagInfo));
                    log.info("ydec.send.param:【{}】",settlementOrderTagInfo.toString());
                }
            }
        }catch (Exception e){
            log.error("receive.ydec.order.is.error,data:{}",result);
            redisService.lPush(OrderTagRedisKeyConstants.TAG_YDEC, JSON.toJSONString(settlementOrderTagInfo));
            kafkaProducer.sendMessage(sendDingTopic,dingDingService.buildDingDingMessage("消费白马数据异常","receive.ydec.order.error",e));
        }
    }
}
```

### 4:kafka原理

#### 4-1：写入方式原理

其实在这里我们应说一下如何根据原生的kafk接口去创建自己用的API，所以在这里我们先讲解一下推送到kafka如何把消息推送到kafka服务器上，produce采用push 的方式把消息推送到broker（服务器）上，每条消息都会被append到partition上（或者说topic上）

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\kafka-constructor.jpg)

整个生产者由两个线程执行，主线程由kafkaproducer创建消息，然后通过可能的拦截器，序列化器和分区器的作用之后缓存到消息累加器	RecoredAccumulator。Sender线程负责从RecoredAccumulator中获取消息并将其发送到kafka。RecoredAccumulator主要用来缓存消息，以便Sender线程可以批量发送，RecoredAccumulator缓存的大小可以通过客户端配置参数buffer.memory(默认32m),如果生产产生消息的速度超过发送服务器的速度，会导致生产者空间不足，这个时候kafkaproducer 的send()方法要么阻塞，要么抛出异常取决于max.block.ms(默认60秒)；

主线程中发送过来的消息都会被追加到 RecordAccumulator的某个双端队列( Deque)中,在 RecordAccumulator的内部为每个分区都维护了一个双端队列,队列中的内容就是ProducerBatch,即 Deque< ProducerBatch>。消息写入缓存时,追加到双端队列的尾部; Sender读取消息时,从双端队列的头部读取。注意 ProducerBatch不是 Producer Record, Producer Batch中可以包含一至多个 Producer record。通俗地说, Producerrecord是生产者中创建的消息,而Producer batch是指一个消息批次, ProducerRecord会被包含在 ProducerBatch中,这样可以使字节的使用更加紧凑。与此同时,将较小的 ProducerRecord拼凑成一个较大的 ProducerBatch,也可以减少网络请求的次数以提升整体的吞吐量。 Producer Batch和消息的具体格式有关,更多的详细内容可以参考52节。如果生产者客户端需要向很多分区发送消息,则可以将buffer, memory参数适当调大以增加整体的吞吐量。消息在网络上都是以字节(Byte)的形式传输的,在发送之前需要创建一块内存区域来保存对应的消息。在Kaka生产者客户端中,通过 Java.10. ByteBuffer实现消息内存的创建和释放。不过频繁的创建和释放是比较耗费资源的,在 RecordAccumulator的内部还有一个 BufferPool它主要用来实现 ByteBuffer的复用,以实现缓存的高效利用。不过 BufferPool只针对特定大小的 ByteBuffer进行管理,而其他大小的 Byte Buffer不会缓存进 BufferPool中,这个特定的大小由 batch.size参数来指定,默认值为16384B,即16KB。我们可以适当地调大 batch.size参数以便多缓存一些消息。Producer Batch的大小和 batch.size参数也有着密切的关系。当一条消息( ProducerRecord)流入 RecordAccumulator时,会先寻找与消息分区所对应的双端队列(如果没有则新建),再从
这个双端队列的尾部获取一个 Producer Batch(如果没有则新建),查看 Producer Batch中是否还可以写入这个 ProducerRecord,如果可以则写入,如果不可以则需要创建一个新的Producer Batch。在新建 ProducerBatch时评估这条消息的大小是否超过 batch.size参数的大小,如果不超过,那么就以 batch.size参数的大小来创建 ProducerBatch,这样在使用完这段内存区域之后,可以通过 BufferPool的管理来进行复用:如果超过,那么就以评估的大小来创建 Producerbatch,这段内存区域不会被复用。Sender从 RecordAccumulator中获取缓存的消息之后,会进一步将原本<分区, Deque<ProducerBatch>的保存形式转变成<Node,List< ProducerBatch>的形式,其中Node表示 Kafka集群的 broker节点。对于网络连接来说,生产者客户端是与具体的 broker节点建立的连接,也就是向具体的 broker节点发送消息,而并不关心消息属于哪一个分区;而对于 KafkaProducer的应用逻辑而言,我们只关注向哪个分区中发送哪些消息,所以在这里需要做一个应用逻辑层面到网络IO层面的转换。
在转换成<Node,Lis< Producer Batch>>的形式之后, Sender还会进一步封装成<Nodeequest>的形式,这样就可以将 Request请求发往各个Node了,这里的 Request是指 Kafka的各种协议请求,对于消息发送而言就是指具体的 ProduceRequest,请求在从 Sender线程发往Kaka之前还会保存到 In FlightRequests中, In FlightRequests保存对象的具体形式为Map< Nodeld, Deque< Request>>,它的主要作用是缓存了已经发出去但还没有收到响应的请求( Nodeld是一个 String类型,表示节点的id编号)。与此同时,In FlightRequests还提供了许多管理类的方法,并且通过配置参数还可以限制每个连接(也就是客户端与Node之间的连接)最多缓存的请求数。这个配置参数为max,in, flight, requestsper. connection,默认值为5,即每个连接最多只能缓存5个未响应的请求,超过该数值之后就不能再向这个连接发送更多的请求了,除非有缓存的请求收到了响应( Response)。通过比较 Deque.< Request>的size与这个参数的大小来判断对应的Node中是否已经堆积了很多未响应的消息,如果真是如此,那么说明这个Node节点负载较大或网络连接有问题,再继续向其发送请求会增大请求超时的可能。

##### 4-1-1重要的参数：

ack：这个参数用来指定分区中必须要有多少个副本收到这条消息,之后生产者才会认为这条消息是成功写入的。acks是生产者客户端中一个非常重要的参数,它涉及消息的可靠性和吞吐量之间的权衡。acks参数有3种类型的值(都是字符串类型)。

```
acks = 0 如果设置为零，则生产者将不会等待来自服务器的任何确认。该记录将立即添加到套接字缓冲区并视为已发送。在这种情况下，无法保证服务器已收到记录，并且
         * <code>retries </ code>配置将不会生效（因为客户端通常不会知道任何故障）。为每条记录返回的偏移量始终设置为-1。
acks = 1 这意味着leader会将记录写入其本地日志，但无需等待所有follower的完全确认即可做出回应。在这种情况下， 如果leader在确认记录后立即失败但在关注者复制          之前，则记录将丢失。
acks = all  这意味着leader将等待完整的同步副本集以确认记录。这保证了只要至少一个同步副本仍然存活，记录就不会丢失。这是最强有力的保证。
          这相当于acks = -1设置
```

2. max request size
   这个参数用来限制生产者客户端能发送的消息的最大值,默认值为1048576B,即1MB一般情况下,这个默认值就可以满足大多数的应用场景了。笔者并不建议读者盲目地增大这个参数的配置值,尤其是在对 Kafka整体脉络没有足够把控的时候。因为这个参数还涉及一些其他参数的联动,比如 broker端的 message.max. bytes参数,如果配置错误可能会引起一些不必要的异常。比如将 broker端的 message,max, bytes参数配置为10,而max, request,size参数配置为20,那么当我们发送一条大小为15B的消息时,生产者客户端就会报出如下的异常:
   org. apache. kafka. common errors. RecordTooLargeException: The request included a
   message larger than the max message size the server will accept
3. retries /A retry backoff. msetries参数用来配置生产者重试的次数,默认值为0,即在发生异常的时候不进行任何重试动作。消息在从生产者发出到成功写入服务器之前可能发生一些临时性的异常,比如网络抖动、 leader副本的选举等,这种异常往往是可以自行恢复的,生产者可以通过配置 retries大于0的值,以此通过内部重试来恢复而不是一味地将异常抛给生产者的应用程序。如果重试达到设定的次数,那么生产者就会放弃重试并返回异常。不过并不是所有的异常都是可以通过
   重试来解决的,比如消息太大,超过max, request,size参数配置的值时,这种方式就不可行了。重试还和另一个参数 retry. backoff.ms有关,这个参数的默认值为100,它用来设定两次重试之间的时间间隔,避免无效的频繁重试。在配置 retries和 retry. backoff,ms之前,最好先估算一下可能的异常恢复时间,这样可以设定总的重试时间大于这个异常恢复时间,以此来避免生产者过早地放弃重试。Kaka可以保证同一个分区中的消息是有序的。如果生产者按照一定的顺序发送消息,那
   么这些消息也会顺序地写入分区,进而消费者也可以按照同样的顺序消费它们。对于某些应用来说,顺序性非常重要,比如 MySQL的 binlog传输,如果出现错误就会造成非常严重的后果。如果将acks参数配置为非零值,并且max,in. flight, requests.per, connection参数配置为大于1的值,那么就会岀现错序的现象:如果第一批次消息写入失败,而第二批次消息写入成功,那么生产者会重试发送第一批次的消息,此时如果第一批次的消息写入成功,那么这两个批次的消息就出现了错序。一般而言,在需要保证消息顺序的场合建议把参数max.in. flight. requests.per. connection配置为1,而不是把acks配置为0,不过这样也会影响整体的吞吐。

4. compression. type
   这个参数用来指定消息的压缩方式,默认值为“none”,即默认情况下,消息不会被压缩。
   第2章生产者|43
   该参数还可以配置为“gzip”“ snappy”和“lz4”。对消息进行压缩可以极大地减少网络传输
   量、降低网络IO,从而提高整体的性能。消息压缩是一种使用时间换空间的优化方式,如果对
   时延有一定的要求,则不推荐对消息进行压缩。
5. connections. max. idle. ms
   这个参数用来指定在多久之后关闭限制的连接,默认值是540000(ms),即9分钟。
6. linger. ms
   这个参数用来指定生产者发送 ProducerBatch之前等待更多消息( Producerrecord)加入Producer Batch的时间,默认值为0。生产者客户端会在 Producer Batch被填满或等待时间超过linger.ms值时发送出去。增大这个参数的值会增加消息的延迟,但是同时能提升一定的吞吐量。这个1 inger,ms参数与TCP协议中的Nage算法有异曲同工之妙。
7. receive buffer. bytes
   这个参数用来设置 Socket接收消息缓冲区( SO RECBUF)的大小,默认值为32768(B),即32KB。如果设置为-1,则使用操作系统的默认值。如果 Producer与 Kafka处于不同的机房,则可以适地调大这个参数值。
8. send buffer. bytes
   这个参数用来设置 Socket发送消息缓冲区( SO SNDBUF)的大小,默认值为131072(B),即128KB。与 receive, buffer, bytes参数一样,如果设置为-1,则使用操作系统的默认值。
9. request timeout. ms
   这个参数用来配置 Producer等待请求响应的最长时间,默认值为30000(ms)。请求超时之后可以选择进行重试。注意这个参数需要比 broker端参数 replica.lag.time.max,ms的值要大,这样可以减少因客户端重试而引起的消息重复的概率。还有一些生产者客户端的参数在本节中没有提及,这些参数同样非常重要,它们需要单独的章节或场景来描述。部分参数在前面的章节中已经提及,比如 bootstrap. servers,还有部分参数会在后面的章节中提及,比如 transactiona1.id。

#### 4-2 消费方式原理



##### 分区

上面我们说到了消息都会被发送到topic,不要以为topic是个神圣的东西，其实他就是一个目录，

#### 可靠性消息投递语义

**At least once**
可能会丢消息，但不不会重复

**At most once**
不不丢消息，但可能重复，所以消费端要做幂等

**Exactly once**
消息不不会丢，且保证只投递⼀一次

整体的消息投递语义需要Producer端和Consumer端两者来保证。KAFKA默认是**At most once**，也可以通过配置事务达到**Exactly once**，但效率很低，不推荐。

##### ACK

当生产者向leader发送数据时，可以通过`request.required.acks`参数来设置数据可靠性的级别：

**1（默认）** 数据发送到Kafka后，经过leader成功接收消息的的确认，就算是发送成功了。在这种情况下，如果leader宕机了，则会丢失数据。

**0** 生产者将数据发送出去就不管了，不去等待任何返回。这种情况下数据传输效率最高，但是数据可靠性确是最低的。

**-1** producer需要等待ISR中的所有follower都确认接收到数据后才算一次发送完成，可靠性最高。

![img](https://segmentfault.com/img/remote/1460000017414371)

##### KAFKA为什么快

* Cache Filesystem Cache PageCache缓存
* 顺序写 由于现代的操作系统提供了预读和写技术，磁盘的顺序写大多数情况下比随机写内存还要快。
* Zero-copy 零拷⻉，少了一次内存交换。
* Batching of Messages 批量量处理。合并小的请求，然后以流的方式进行交互，直顶网络上限。
* Pull 拉模式 使用拉模式进行消息的获取消费，与消费端处理能力相符。

5：和rabbitmq对比

下表是对Kafka与RabbitMQ功能的总结性对比及补充说明。

| 功能项     | Kafka（1.1.0版本）                                           | RabbitMQ（3.6.10版本）                                       |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 优先级队列 | 不支持                                                       | 支持。建议优先级大小设置在0-10之间。                         |
| 延迟队列   | 不支持                                                       | 支持                                                         |
| 死信队列   | 不支持                                                       | 支持                                                         |
| 重试队列   | 不支持                                                       | 不支持。RabbitMQ中可以参考延迟队列实现一个重试队列，二次封装比较简单。如果要在Kafka中实现重试队列，首先得实现延迟队列的功能，相对比较复杂。 |
| 消费模式   | 拉模式                                                       | 推模式+拉模式                                                |
| 广播消费   | 支持。Kafka对于广播消费的支持相对而言更加正统。              | 支持，但力度较Kafka弱。                                      |
| 消息回溯   | 支持。Kafka支持按照offset和timestamp两种维度进行消息回溯。   | 不支持。RabbitMQ中消息一旦被确认消费就会被标记删除。         |
| 消息堆积   | 支持                                                         | 支持。一般情况下，内存堆积达到特定阈值时会影响其性能，但这不是绝对的。如果考虑到吞吐这因素，Kafka的堆积效率比RabbitMQ总体上要高很多。 |
| 持久化     | 支持                                                         | 支持                                                         |
| 消息追踪   | 不支持。消息追踪可以通过外部系统来支持，但是支持粒度没有内置的细腻。 | 支持。RabbitMQ中可以采用Firehose或者rabbitmq_tracing插件实现。不过开启rabbitmq_tracing插件件会大幅影响性能，不建议生产环境开启，反倒是可以使用Firehose与外部链路系统结合提供高细腻度的消息追踪支持。 |
| 消息过滤   | 客户端级别的支持                                             | 不支持。但是二次封装一下也非常简单。                         |
| 多租户     | 不支持                                                       | 支持                                                         |
| 多协议支持 | 只支持定义协议，目前几个主流版本间存在兼容性问题。           | RabbitMQ本身就是AMQP协议的实现，同时支持MQTT、STOMP等协议。  |
| 跨语言支持 | 采用Scala和Java编写，支持多种语言的客户端。                  | 采用Erlang编写，支持多种语言的客户端。                       |
| 流量控制   | 支持client和user级别，通过主动设置可将流控作用于生产者或消费者。 | RabbitMQ的流控基于Credit-Based算法，是内部被动触发的保护机制，作用于生产者层面。 |
| 消息顺序性 | 支持单分区（partition）级别的顺序性。                        | 顺序性的条件比较苛刻，需要单线程发送、单线程消费并且不采用延迟队列、优先级队列等一些高级功能，从某种意义上来说不算支持顺序性。 |
| 安全机制   | （TLS/SSL、SASL）身份认证和（读写）权限控制                  | 与Kafka相似                                                  |
| 幂等性     | 支持单个生产者单分区单会话的幂等性。                         | 不支持                                                       |
| 事务性消息 | 支持                                                         | 支持                                                         |

## （四）：RocketMQ 

### 1:rocketmq包括四大核心组成部分：**NameServer**、**Broker**、**Producer**以及**Consumer**四部分。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq_architecture_3.png)

RocketMQ架构上主要分为四部分，如上图所示:

- NameServer：NameServer是一个非常简单的Topic路由注册中心，其角色类似Dubbo中的zookeeper，支持Broker的动态注册与发现。主要包括两个功能：1：**Broker管理**，NameServer接受Broker集群的注册信息并且保存下来作为路由信息的基本数据。然后提供心跳检测机制，检查Broker是否还存活；

  2：**路由信息管理**，每个NameServer将保存关于Broker集群的整个路由信息和用于客户端查询的队列信息。然后Producer和Conumser通过NameServer就可以知道整个Broker集群的路由信息，从而进行消息的投递和消费。NameServer通常也是集群的方式部署，各实例间相互不进行信息通讯。Broker是向每一台NameServer注册自己的路由信息，所以每一个NameServer实例上面都保存一份完整的路由信息。当某个NameServer因某种原因下线了，Broker仍然可以向其它NameServer同步其路由信息，Producer,Consumer仍然可以动态感知Broker的路由的信息。

  nameServer与每台broker服务器保持长连接，并间隔30秒检查broker 是否存活，

- BrokerServer：Broker主要负责消息的存储、投递和查询以及服务高可用保证，为了实现这些功能，Broker包含了以下几个重要子模块。

  1. Remoting Module：整个Broker的实体，负责处理来自clients端的请求。
  2. Client Manager：负责管理客户端(Producer/Consumer)和维护Consumer的Topic订阅信息
  3. Store Service：提供方便简单的API接口处理消息存储到物理硬盘和查询功能。
  4. HA Service：高可用服务，提供Master Broker 和 Slave Broker之间的数据同步功能。
  5. Index Service：根据特定的Message key对投递到Broker的消息进行索引服务，以提供消息的快速查询。

- Producer：消息发布的角色，支持分布式集群方式部署。Producer通过MQ的负载均衡模块选择相应的Broker集群队列进行消息投递，投递的过程支持快速失败并且低延迟。

- Consumer：消息消费的角色，支持分布式集群方式部署。支持以push推，pull拉两种模式对消息进行消费。同时也支持集群方式和广播方式的消费，它提供实时消息订阅机制，可以满足大多数用户的需求。

nameserver启动流程：



### 2:RocketMQ 网络部署特点

- NameServer是一个几乎无状态节点，可集群部署，节点之间无任何信息同步。
- Broker部署相对复杂，Broker分为Master与Slave，一个Master可以对应多个Slave，但是一个Slave只能对应一个Master，Master与Slave 的对应关系通过指定相同的BrokerName，不同的BrokerId 来定义，BrokerId为0表示Master，非0表示Slave。Master也可以部署多个。每个Broker与NameServer集群中的所有节点建立长连接，定时注册Topic信息到所有NameServer。 注意：当前RocketMQ版本在部署架构上支持一Master多Slave，但只有BrokerId=1的从服务器才会参与消息的读负载。
- Producer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic 服务的Master建立长连接，且定时向Master发送心跳。Producer完全无状态，可集群部署。
- Consumer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic服务的Master、Slave建立长连接，且定时向Master、Slave发送心跳。Consumer既可以从Master订阅消息，也可以从Slave订阅消息，消费者在向Master拉取消息时，Master服务器会根据拉取偏移量与最大偏移量的距离（判断是否读老消息，产生读I/O），以及从服务器是否可读等因素建议下一次是从Master还是Slave拉取。

结合部署架构图，描述集群工作流程：

- 启动NameServer，NameServer起来后监听端口，等待Broker、Producer、Consumer连上来，相当于一个路由控制中心。
- Broker启动，跟所有的NameServer保持长连接，定时发送心跳包。心跳包中包含当前Broker信息(IP+端口等)以及存储所有Topic信息。注册成功后，NameServer集群中就有Topic跟Broker的映射关系。
- 收发消息前，先创建Topic，创建Topic时需要指定该Topic要存储在哪些Broker上，也可以在发送消息时自动创建Topic。
- Producer发送消息，启动时先跟NameServer集群中的其中一台建立长连接，并从NameServer中获取当前发送的Topic存在哪些Broker上，轮询从队列列表中选择一个队列，然后与队列所在的Broker建立长连接从而向Broker发消息。
- Consumer跟Producer类似，跟其中一台NameServer建立长连接，获取当前订阅Topic存在哪些Broker上，然后直接跟Broker建立连接通道，开始消费消息。

### 3:Broker:消息中转角色，负责**存储消息**，转发消息。

- **Broker**是具体提供业务的服务器，单个Broker节点与所有的NameServer节点保持长连接及心跳，并会定时将**Topic**信息注册到NameServer，顺带一提底层的通信和连接都是**基于Netty实现**的。
- **Broker**负责消息存储，以Topic为纬度支持轻量级的队列，单机可以支撑上万队列规模，支持消息推拉模型。
- 官网上有数据显示：具有**上亿级消息堆积能力**，同时可**严格保证消息的有序性**。

### 4:producer:消息生产者 

- **RocketMQ** 提供了三种方式发送消息：同步、异步和单向
- **同步发送**：同步发送指消息发送方发出数据后会在收到接收方发回响应之后才发下一个数据包。一般用于重要通知消息，例如重要通知邮件、营销短信。
- **异步发送**：异步发送指发送方发出数据后，不等接收方发回响应，接着发送下个数据包，一般用于可能链路耗时较长而对响应时间敏感的业务场景，例如用户视频上传后通知启动转码服务。
- **单向发送**：单向发送是指只负责发送消息而不等待服务器回应且没有回调函数触发，适用于某些耗时非常短但对可靠性要求并不高的场景，例如日志收集。



### 5：consumer:消息消费者

- **Consumer**也由用户部署，支持PUSH和PULL两种消费模式，支持**集群消费**和**广播消息**，提供**实时的消息订阅机制**。
- **Pull**：拉取型消费者（Pull Consumer）主动从消息服务器拉取信息，只要批量拉取到消息，用户应用就会启动消费过程，所以 Pull 称为主动消费型。
- **Push**：推送型消费者（Push Consumer）封装了消息的拉取、消费进度和其他的内部维护工作，将消息到达时执行的回调接口留给用户应用程序来实现。所以 Push 称为被动消费类型，但从实现上看还是从消息服务器中拉取消息，不同于 Pull 的是 Push 首先要注册消费监听器，当监听器处触发后才开始消费消息。

### 6：消息类型

- 普通消息
- 定时消息
- 顺序消息

### 7:broker存储机制



(1) CommitLog：消息主体以及元数据的存储主体，存储Producer端写入的消息主体内容,消息内容不是定长的。单个文件大小默认1G ，文件名长度为20位，左边补零，剩余为起始偏移量，比如00000000000000000000代表了第一个文件，起始偏移量为0，文件大小为1G=1073741824；当第一个文件写满了，第二个文件为00000000001073741824，起始偏移量为1073741824，以此类推。消息主要是顺序写入日志文件，当文件满了，写入下一个文件；

(2) ConsumeQueue：消息消费队列，引入的目的主要是提高消息消费的性能，由于RocketMQ是基于主题topic的订阅模式，消息消费是针对主题进行的，如果要遍历commitlog文件中根据topic检索消息是非常低效的。Consumer即可根据ConsumeQueue来查找待消费的消息。其中，ConsumeQueue（逻辑消费队列）作为消费消息的索引，保存了指定Topic下的队列消息在CommitLog中的起始物理偏移量offset，消息大小size和消息Tag的HashCode值。consumequeue文件可以看成是基于topic的commitlog索引文件，故consumequeue文件夹的组织方式如下：topic/queue/file三层组织结构，具体存储路径为：$HOME/store/consumequeue/{topic}/{queueId}/{fileName}。同样consumequeue文件采取定长设计，每一个条目共20个字节，分别为8字节的commitlog物理偏移量、4字节的消息长度、8字节tag hashcode，单个文件由30W个条目组成，可以像数组一样随机访问每一个条目，每个ConsumeQueue文件大小约5.72M；

(3) IndexFile：IndexFile（索引文件）提供了一种可以通过key或时间区间来查询消息的方法。Index文件的存储位置是：$HOME \store\index${fileName}，文件名fileName是以创建时的时间戳命名的，固定的单个IndexFile文件大小约为400M，一个IndexFile可以保存 2000W个索引，IndexFile的底层存储设计为在文件系统中实现HashMap结构，故rocketmq的索引文件其底层实现为hash索引。

在上面的RocketMQ的消息存储整体架构图中可以看出，RocketMQ采用的是混合型的存储结构，即为**Broker单个实例下所有的队列共用一个日志数据文件**（即为CommitLog）来存储。RocketMQ的混合型存储结构(多个Topic的消息实体内容都存储于一个CommitLog中)针对Producer和Consumer分别采用了数据和索引部分相分离的存储结构，**Producer发送消息至Broker端，然后Broker端使用同步或者异步的方式对消息刷盘持久化，保存至CommitLog中**。只要消息被刷盘持久化至磁盘文件CommitLog中，那么Producer发送的消息就不会丢失。正因为如此，Consumer也就肯定有机会去消费这条消息。当无法拉取到消息后，可以等下一次消息拉取，同时服务端也支持长轮询模式，如果一个消息拉取请求未拉取到消息，Broker允许等待30s的时间，只要这段时间内有新消息到达，将直接返回给消费端。这里，RocketMQ的具体做法是，使用Broker端的后台服务线程—ReputMessageService不停地分发请求并异步构建ConsumeQueue（逻辑消费队列）和IndexFile（索引文件）数据。



### 8:rocketmq存储结构,为什么写文件这么快

####   (1)RocketMQ在存储设计中通过内存映射、顺序写文件等方式实现了高吞吐

org.apache.rocketmq.store.CommitLog：RocketMQ 对存储消息的物理文件的抽象实现，也就是物理CommitLog文件的具体实现。org.apache.rocketmq.store.MappedFile：CommitLog文件在内存中的映射文件，映射文件同时具有内存的写入速度和与磁盘一样可靠的持久化方式。org.apache.rocketmq.store.MappedFileQueue：映射文件队列中有全部的CommitLog映射文件，第一个映射文件为最先过期的文件，最后一个文件是最后过期的文件，最新的消息总是写入最后一个映射文件。CommitLog、MappedFileQueue、MappedFile与物理CommitLog文件的关系如图

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq文件结构.png)

每个 MappedFileQueue 包含多个 MappedFile，就是真实的物理 CommitLog 文件。在Java 中通过 java.nio.MappedByteBuffer 来实现文件的内存映射，即文件读写都是通过MappedByteBuffer（其实是Page Cache）来操作的。写入数据时先加锁，然后通过Append方式写入最新MappedFile。对于读取消息，大部分情况下用户只关心最新数据，而这些数据都在Page Cache中，也就是说，读写文件就是在Page Cache中进行的，其速度几乎等于直接操作内存的速度。

#### (2)刷盘方式:

##### a:同步刷盘:

org.apache.rocketmq.store.CommitLog.GroupCommitService——同步刷盘服务,在Broker存储消息到Page Cache后，同步将Page Cache刷到磁盘，再返回客户端消息并写入结果.

正常同步刷盘线程会间隔 10ms 执行一次org.apache.rocketmq.store.CommitLog.GroupCommitService.doCommit（）方法，该方法循环每一个同步刷盘请求，如果刷盘成功，那么唤醒等待刷盘请求锁的存储消息线程，并告知刷盘成功

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq同步刷盘.jpg)



##### b:异步刷盘:

org.apache.rocketmq.store.CommitLog.CommitRealTimeService——异步转存服务。Broker通过配置读写分离将消息写入直接内存（Direct Memory，简称 DM），然后通过异步转存服务，将DM 中的数据再次存储到 Page Cache中，以供异步刷盘服务将Page Cache刷到磁盘中

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq异步刷盘.jpg)

#### (3)RocketMQ的存储设计中为什么写文件这么快

很大一部分是基于 Kafka的设计进行优化的，这里我们非常感谢Kafka的设计和开发人员，有了你们才成就了今天的RocketMQ。RocketMQ是基于Java编写的消息中间件，支持万亿级的消息扭转和保存，RocketMQ写文件为什么会这么快呢？

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq读取文件.png)

- Page Cache

  优点:  现代操作系统内核被设计为按照Page读取文件，每个Page默认为4KB。因为程序一般符合局部性原理，所以操作系统在读取一段文件内容时，会将该段内容和附件的文件内容都读取到内核Page中（预读），下次读取的内容如果命中Page Cache就可以直接返回内容，不用再次读取磁盘，过程如图所示。

  缺点:  当遇到操作系统进行脏页回写、内存回收、内存交换等情况时，就会引起较大的消息读写延迟。对于这些情况，RocketMQ 采用了多种优化技术，比如**内存预分配、文件预热、mlock系统调用**等，以保证在最大限度地发挥Page Cache 机制的优点的同时，尽可能地减少消息读写延迟。所以在生产环境部署 RocketMq的时候，尽量采用SSD独享磁盘，这样可以最大限度地保证读写性能。

- Virtual Memory（虚拟内存）：为了保证每个程序有足够的运行空间和编程空间，可以将一些暂时不用的内存数据保存到交换区（其实是磁盘）中，这样就可以运行更多的程序，这种“内存”被称为虚拟内存（因为不是真的内存）。

  操作系统的可分配内存大小=虚拟内存大小+物理内存大小。

- 零拷贝和 Java 文件映射：从文件读取流程可以看到，读取到内核态的数据会经历两次拷贝，第一次从内核态内存拷贝到用户态内存，第二次从用户态内存拷贝到 Java 进程的某个变量地址，这样Java变量才能读取数据，如图6-7所示。为了提高读写文件的效率，IBM实现了零拷贝技术，它是世界上最早实现该技术的公司，后来各个厂商（如甲骨文等）也纷纷实现了该技术。

  java.nio.MappedByteBuffer.java文件中实现了零拷贝技术，即Java进程映射到内核态内存，原来内核态内存与用户态内存的互相拷贝过程就消失了。在消息系统中，用户关心的往往都是最新的数据，理论上，基本的操作都在Page Cacge中，PageCache的操作速度和内存基本持平，所以速度非常快。当然，也存在读取历史消息而历史消息不在Page Cache中的情况，比如在流处理和批处理中，经常将消费重置到历史消息位点，以重新计算全部结果。这种情况只是在第一次拉取消息时会读取磁盘，以后可以利用磁盘预读，几乎可以做到不再直接读取磁盘，其性能与利用Page Cache相比，只在第一次有差异。

### 9:主从同步

我们知道Broker有两种角色Master和Slave。Master主要用于处理生产者、消费者的请求和存储数据。Slave从Master同步所有数据到本地，具体作用体现在以下两方面。

第一，Broker服务高可用。一般生产环境不会部署两个主Broker节点和两个从Broker节点（也叫2m2s），一个 Master宕机后，另一个 Master可以接管工作；如果两个 Master都宕机，消费者可以通过连接Slave继续消费。这样可以保证服务的高可用。

第二，提高服务性能。如果消费者从Master Broker拉取消息时，发现拉取消息的offset和CommitLog的物理offset相差太多，会转向Slave拉取消息，这样可以减轻Master的压力，从而提高性能。

Broker同步数据的方式有两种：同步复制、**异步复制**。

**同步复制**:  是指客户端发送消息到Master，Master将消息同步复制到Slave的过程，可以通过设置参数brokerRole=BrokerRole.SYNC_MASTER 来实现。这种消息配置的可靠性很强，但是效率比较低，适用于金融、在线教育等对消息有强可靠需求的场景。

**异步复制**:  是指客户端发送消息到 Master，再由异步线程 HAService 异步同步到 Slave的过程，可以通过设置参数 brokerRole=BrokerRole.ASYNC_MASTER 来实现。这种消息配置的效率非常高，可靠性比同步复制差，适用于大部分业务场景。

Broker主从同步数据有两种：配置数据和消息数据。

**配置数据**主要包含Topic配置、消费者位点信息、延迟消息位点信息、订阅关系配置等。Broker 主从同步的逻辑是通过 org.apache.rocketmq.broker.slave.SlaveSynchronize.syncAll（）方法实现的。该方法在org.apache.rocketmq.broker.BrokerController.initialize（）方法中被初始化，每60s同步一次，并且同步周期不能修改。消息数据是生产者发送的消息，保存在 CommitLog 中，由 HAService 服务实时同步到SlaveBroker中。所有实现类都在org.apache.rocketmq.store.ha 包下。

### 10:源码分析(理解)

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\rocketmq源码流程.jpg)

## (五):ZeroMQ /activemq

ZeroMQ号称史上最快的消息队列，基于 C 语言开发。ZeroMQ 是一个消息处理队列库，可在多线程、多内核和主机之间弹性伸缩，虽然大多数时候我们习惯将其归入消息队列家族之中，但是其和前面的几款有着本质的区别，ZeroMQ 本身就不是一个消息队列服务器，更像是一组底层网络通讯库，对原有的 Socket API 上加上一层封装而已。

目前市面上的消息中间件还有很多，比如腾讯系的 PhxQueue、CMQ、CKafka，又比如基于 Go 语言的 NSQ，有时人们也把类似 Redis 的产品也看做消息中间件的一种，当然它们都很优秀，但是本文篇幅限制无法穷极所有，下面会针对性的挑选 RabbitMQ 和 Kafka 两款典型的消息中间件来做分析，力求站在一个公平公正的立场来阐述消息中间件选型中的各个要点。

| 名称名称 | 事务                    | 吞吐量     | 队列存放位置           | 负载均衡 | 集群方式 |
| -------- | ----------------------- | ---------- | ---------------------- | -------- | -------- |
| Kafka    | 支持                    | 17.3w/s    | 内存，磁盘，数据库     | 支持     | 天然集群 |
| RocketMQ | 不支持                  | 11.6w/s    | 磁盘                   | 支持     | 支持     |
| RabbitMQ | 支持                    | 5.95w/s    | 内存，磁盘（非持久化） | 支持不好 | 支持简单 |
| activeMQ | 支持                    |            |                        | 支持     | 支持简单 |
| **顺序** | **消息确认/回溯/重试**  | **并发度** |                        |          |          |
| 支持     | 支持 / 支持 / 不支持    | 高         |                        |          |          |
| 支持     | 支持 / 支持 /  支持     | 极高       |                        |          |          |
| 不支持   | 支持 / 不支持 /  不支持 | 高         |                        |          |          |
| 不支持   | 支持 / 不支持 /  不支持 | 高         |                        |          |          |

##      (六)  ：Dubbo

### 一：配置说明

### 1：属性配置覆盖策略

-D > XML > properties
-D 参数优先，这样可以使用户在部署和启动时进行参数重写，比如在启动时需改变协议的端口。
XML 次之，如果在 XML 中有配置，则 dubbo.properties 中的相应配置项无效。
Properties 最后，相当于缺省值，只有 XML 没有配置时，dubbo.properties 的相应配置项才会生效，通常用

### 2:启动时检查

### 3：容错配置

在集群调用失败时，Dubbo 提供了多种容错方案，缺省为 failover 重试。
Failover Cluster
失败自动切换，当出现失败，重试其它服务器 [1]。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。
<dubbo:service retries="2" />或

<dubbo:reference retries="2" />或

<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>

#### Failfast Cluster

快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。

#### Failsafe Cluster

失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

#### Failback Cluster

失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

#### Forking Cluster

并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。

#### Broadcast Cluster

广播调用所有提供者，逐个调用，任意一台报错则报错 [2]。通常用于通知所有提供者更新缓存或日志等本地资源信息。

集群模式配置
按照以下示例在服务提供方和消费方配置集群模式

<dubbo:service cluster="failsafe" />
或

<dubbo:reference cluster="failsafe" />

### 4:负载均衡策略

#### **Random LoadBalance**

随机，按权重设置随机概率。
在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

#### **RoundRobin LoadBalance**

轮询，按公约后的权重设置轮询比率。
存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

#### **LeastActive LoadBalance**

最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

#### **ConsistentHash LoadBalance**

一致性 Hash，相同参数的请求总是发到同一提供者。
当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
算法参见：http://en.wikipedia.org/wiki/Consistent_hashing
缺省只对第一个参数 Hash，如果要修改，请配置 <dubbo:parameter key="hash.arguments" value="0,1" />
缺省用 160 份虚拟节点，如果要修改，请配置 <dubbo:parameter key="hash.nodes" value="320" />
配置
服务端服务级别
<dubbo:service interface="..." loadbalance="roundrobin" />
客户端服务级别
<dubbo:reference interface="..." loadbalance="roundrobin" />
服务端方法级别
<dubbo:service interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:service>
客户端方法级别
<dubbo:reference interface="...">
    <dubbo:method name="..." loadbalance="roundrobin"/>
</dubbo:reference>

### 5：dubbo支持的协议

 http，redis，hessian（一种序列化的方式，比java的序列化要快）

### 6：配置

### 6-1   dubbo内置哪几种容器： spring，jetty，log4j 

### 6-2 Dubbo的序列化架构： Hessian序列化，Dubbo，FastJson，Java自带序列化

### 6-3 Dubbo默认的通信框架： Netty框架

Dubbo的容器只是一个简单的main方法，并加载一个spring容器，用于暴露服务

Dubbo启动时如果依赖的服务不可用： 假如不可用那么启动时会抛出异常。阻止spring初始化完成。 可以check=false关闭检查

### 7： Dubbo的线程模型概述

#### 7.1  all（AllDispatcher类）：

所有消息都派发到业务线程池，这些消息包括请求、响应、连接事件、断开事件、心跳事件等，如图7.1所示。

#### 7.2:direct（DirectDispatcher类）

所有消息都不派发到业务线程池，全部在IO线程上直接执行

#### 7.3:message（MessageOnlyDispatcher类）：

只有请求响应消息派发到业务线程池，其他消息如连接事件、断开事件、心跳事件等，直接在I/O线程上执行

#### 7.4:execution（ExecutionDispatcher类）：

只把请求类消息派发到业务线程池处理，但是响应、连接事件、断开事件、心跳事件等消息直接在I/O线程上执行

#### 7.5:connection（ConnectionOrderedDispatcher类）：

在I/O线程上将连接事件、断开事件放入队列，有序地逐个执行，其他消息派发到业务线程池处理

### 8：Dubbo的线程池策略

我们在上面讲解Dubbo线程模型时提到，为了尽量早地释放Netty的I/O线程，某些线程模型会把请求投递到线程池进行异步处理，那么这里所谓的线程池是什么样的线程池呢？其实这里的线程池ThreadPool也是一个扩展接口SPI，Dubbo提供了该扩展接口的一些实现，具体如下。

#### FixedThreadPool：

创建一个具有固定个数线程的线程池。

####  LimitedThreadPool：

创建一个线程池，这个线程池中的线程个数随着需要量动态增加，但是数量不超过配置的阈值。另外，空闲线程不会被回收，会一直存在。

#### EagerThreadPool：

创建一个线程池，在这个线程池中，当所有核心线程都处于忙碌状态时，将创建新的线程来执行新任务，而不是把任务放入线程池阻塞队列。

#### CachedThreadPool：

创建一个自适应线程池，当线程空闲1分钟时，线程会被回收；当有新请求到来时，会创建新线程。

### 9：服务分组

当一个接口有多种实现时，可以用 group 区分。

### 服务

```xml
<dubbo:service group="feedback" interface="com.xxx.IndexService" />
<dubbo:service group="member" interface="com.xxx.IndexService" />
```

### 引用

```xml
<dubbo:reference id="feedbackIndexService" group="feedback" interface="com.xxx.IndexService" />
<dubbo:reference id="memberIndexService" group="member" interface="com.xxx.IndexService" />
```

### 10：多版本

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。

可以按照以下的步骤进行版本迁移：

1. 在低压力时间段，先升级一半提供者为新版本
2. 再将所有消费者升级为新版本
3. 然后将剩下的一半提供者升级为新版本

老版本服务提供者配置：

```xml
<dubbo:service interface="com.foo.BarService" version="1.0.0" />
```

新版本服务提供者配置：

```xml
<dubbo:service interface="com.foo.BarService" version="2.0.0" />
```

老版本服务消费者配置：

```xml
<dubbo:reference id="barService" interface="com.foo.BarService" version="1.0.0" />
```

新版本服务消费者配置：

```xml
<dubbo:reference id="barService" interface="com.foo.BarService" version="2.0.0" />
```

如果不需要区分版本，可以按照以下的方式配置 [[1\]](http://dubbo.apache.org/zh-cn/docs/user/demos/multi-versions.html#fn1)：

```xml
<dubbo:reference id="barService" interface="com.foo.BarService" version="*" />
```

### 11：结果缓存

结果缓存 [[1\]](http://dubbo.apache.org/zh-cn/docs/user/demos/result-cache.html#fn1)，用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量 [[2\]](http://dubbo.apache.org/zh-cn/docs/user/demos/result-cache.html#fn2)。

### 缓存类型

- `lru` 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
- `threadlocal` 当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
- `jcache` 与 [JSR107](http://jcp.org/en/jsr/detail?id=107%27) 集成，可以桥接各种缓存实现。

缓存类型可扩展，参见：[缓存扩展](http://dubbo.apache.org/zh-cn/docs/dev/impls/cache.html)

### 配置

```xml
<dubbo:reference interface="com.foo.BarService" cache="lru" />
```

或：

```xml
<dubbo:reference interface="com.foo.BarService">
    <dubbo:method name="findBar" cache="lru" />
</dubbo:reference>
```

### 二：dubbo 的spi 机制



### 三：dubbo 服务是如何暴露的

#### 1）：注册中心（zookeeper,redis,nacos,eureka,consol,etcd,muticast,mutiple,sofa）

zookeeer：采用时间通知+客户端拉取的方式：客户端在第一次链接的时候会全量拉取数据，并在订阅的节点上注册一个watcher,客户端保持与注册中心的长连接，后续注册中心有变化就会通过watcher机制的回调主动通知客户端，客户端收到通知后，会把对应节点的全量数据拉取下来，这一点在notifyListener#notify(List<URL> urls)接口的方法上有说明。zookeeper 每个节点都有版本号，数据变更触发watcher事件推送数据给客户端

nacos:



### 四:dubbo 通信机制







## (七)：zookeeper

#### 1:什么是zookeeper

ZooKeeper 是一个开源的分布式协调服务，由雅虎创建，是 Google Chubby 的开源实现。分布式应用程序可以基于 ZooKeeper 实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、配置维护，名字服务、分布式同步、分布式锁和分布式队列等功能。

#### 2:zookeeper的数据模型

很像数据结构当中的树，也很像文件系统的目录

 Znode分为四种类型：

#####          1.持久节点 （PERSISTENT）

​         默认的节点类型。创建节点的客户端与zookeeper断开连接后，该节点依旧存在

#####          2.持久节点顺序节点（PERSISTENT_SEQUENTIAL）

​         所谓顺序节点，就是在创建节点时，Zookeeper根据创建的时间顺序给该节点名称进行编号

#####          3.临时节点（EPHEMERAL）

​         和持久节点相反，当创建节点的客户端与zookeeper断开连接后，临时节点会被删除 

#####          4.临时顺序节点（EPHEMERAL_SEQUENTIAL）

​         临时顺序节点结合和临时节点和顺序节点的特点：在创建节点时，Zookeeper根据创建的时间顺序给该节点名称进行编号；当创建节点的客户端与zookeeper断开连接后，临时节点会被删除

启动zk服务:查看zk的运行状态:./zkServer.sh start`

查看zk的运行状态:./zkServer.sh status` 由于我已经配置了`zk`的集群，所以此处显示状态为`leader`

客户端链接zk: ./zkCli.sh 

create 创建节点:

- `create [-s] [-e] path data acl` 可以注意一下各个版本的变化

```
#创建merryyou节点，节点的内容为merryyou
[zk: localhost:2181(CONNECTED) 1] create /merryyou merryyou
```

create -e 创建临时节点:

- `create -e`

```
#创建临时节点
[zk: localhost:2181(CONNECTED) 4] create -e  /merryyou/temp merryyou
```

create -s 创建顺序节点 自动累加

- `create -s`

```
# 创建顺序节点，顺序节点会自动累加
[zk: localhost:2181(CONNECTED) 2] create -s /merryyou/sec seq
```

set path data [version] 修改节点

```
[zk: localhost:2181(CONNECTED) 6] get /merryyou
```

delete path [version] 删除节点

```
[zk: localhost:2181(CONNECTED) 13] delete /merryyou/sec000000000
```

ls 查看:ls` 查看命令(`niocoder`是我测试集群创建的节点，默认只有`zookeeper`一个节点)

get 获取节点数据和更新信息
get内容为空
cZxid ：创建节点的id
ctime ： 节点的创建时间
mZxid ：修改节点的id
mtime ：修改节点的时间
pZxid ：子节点的id
cversion : 子节点的版本
dataVersion ： 当前节点数据的版本
aclVersion ：权限的版本
ephemeralOwner ：判断是否是临时节点
dataLength ： 数据的长度
numChildren ：子节点的数量stat 获得节点的更新信息

stat 获得节点的更新信息:   stat   /zookeeper

ls2 ls命令和stat命令的整合:ls2  /zookeeper

watcher通知机制
关于watcher机制大体的理解可以为，当每个节点发生变化，都会触发watcher事件，类似于mysql的触发器。zk中 watcher是一次性的，触发后立即销毁。可以参考https://blog.csdn.net/hohoo1990/article/details/78617336 

- stat path [watch] 设置watch事件 
- get path [watch]设置watch事件 
- 子节点创建和删除时触发watch事件，子节点修改不会触发该事件

stat path [watch] 设置watch事件

添加watch 事件

[zk: localhost:2181(CONNECTED) 18] stat /longfei watch


##### getAcl:获取某个节点的acl权限信息

```
#获取节点权限信息默认为 world:cdrwa任何人都可以访问
[zk: localhost:2181(CONNECTED) 34] getAcl /merryyou
```

##### setAcl 设置权限

```
[zk: localhost:2181(CONNECTED) 35] create /merryyou/test test
```

##### acl Auth 密码明文设置

```
[zk: localhost:2181(CONNECTED) 53] create /niocoder/merryyou merryyou
```

##### acl digest 密码密文设置

```
[zk: localhost:2181(CONNECTED) 13] create /names test
```

##### acl ip 控制客户端

```
[zk: localhost:2181(CONNECTED) 22] create /niocoder/ip aa
```

##### ruok 查看zookeeper是否启动

```
[root@localhost bin]# echo ruok | nc 192.168.0.68 2181
```

##### dump 列出没有处理的节点，临时节点

```
imok[root@localhost bin]# echo dump | nc 192.168.0.68 2181
```

##### conf 查看服务器配置

```
[root@localhost bin]# echo conf | nc 192.168.0.68 2181
```

##### cons 显示连接到服务端的信息

```
[root@localhost bin]# echo cons | nc 192.168.0.68 2181
```

##### envi 显示环境变量信息

```
[root@localhost bin]# echo envi | nc 192.168.0.68 2181
```

#### 3:Paxos算法

Paxos作为提高分布式系统的容错性的一致性算法，

#### 4：ZAB协议

zookeeper的所有数据都储存在内存中，数据模型是一棵树（ZNode Tree），由斜杠（/）进行分割的路径，就是一个znode，znode  维护一个叫做Stat 的数据结构，记录了三个数据版本：当前版本version ， 当前znode子节点的版本cversion ，当前acl的版本 aversion

ACL：是zookeeper采取的权限控制的策略，包括五种权限

​	create：

​	read:

​	writer:

​	delete:

​	admin: 设置节点acl的权限

Watcher:zookeeper 允许用户在一些节点注册一些watcher，并且在一些特定的时间触发的时候服务器端会将时间通知到感兴趣的客户端中；





# 七：搜索引擎

## （一）elasticSearch

### 1：理解一下概念

Elasticsearch 是一个分布式、RESTful 风格的搜索和数据分析引擎，能够解决不断涌现出的各种用例。作为 Elastic Stack 的核心，它集中存储您的数据，帮助您发现意料之中以及意料之外的情况。

一个 集群是一组拥有相同 `cluster.name` 的节点， 他们能一起工作并共享数据，还提供容错与可伸缩性。(当然，一个单独的节点也可以组成一个集群) 你可以在 `elasticsearch.yml` 配置文件中 修改 `cluster.name` ，该文件会在节点启动时加载

如果你正在使用 Java，在代码中你可以使用 Elasticsearch 内置的两个客户端：

- 节点客户端（Node client）

  节点客户端作为一个非数据节点加入到本地集群中。换句话说，它本身不保存任何数据，但是它知道数据在集群中的哪个节点中，并且可以把请求转发到正确的节点。

- 传输客户端（Transport client）

  轻量级的传输客户端可以将请求发送到远程集群。它本身不加入集群，但是它可以将请求转发到集群中的一个节点上。

两个 Java 客户端都是通过 *9300* 端口并使用 Elasticsearch 的原生 传输协议和集群交互。集群中的节点通过端口 9300 彼此通信。如果这个端口没有打开，节点将无法形成一个集群。所有其他语言可以使用 RESTful API 通过端口 *9200* 和 Elasticsearch 进行通信，你可以用你最喜爱的 web 客户端访问 Elasticsearch 。事实上，正如你所看到的，你甚至可以使用 `curl` 命令来和 Elasticsearch 交互。

Elasticsearch 是面向文档的，意味着它存储整个对象或文档。Elasticsearch 不仅存储文档，而且索引每个文档的内容使之可以被检索。在 Elasticsearch 中，你可以对文档进行索引、检索、排序和过滤，而不是对行列数据。这是一种完全不同的思考数据的方式，也是 Elasticsearch 能支持复杂全文检索的原因。

### 2：术语概念和存储相关        了解一下

Elasticsearch集群可以包含多个索引(indices)（数据库），每一个索引可以包含多个类型(types)（表），每一个类型包含多个文档(documents)（行），然后每个文档包含多个字段(Fields)（列）。

Relational DB -> Databases -> Tables -> Rows -> Columns
**Elasticsearch -> Indices （索引/库）-> Types （类型/表）-> Documents （文档/行）-> Fields（字段/列）**

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\es-sql对比.jpg)

索引词(term)：是一个能够被索引的精确值，索引词是可以通过term查询进行准确的搜索；

文本（text）：文本是一段普通的非结构化文字，通常文本会被分析成一个个的索引词，储存在es的索引库中，

分析（analysis）:分析是将文本转换为索引词的过程；

Elasticsearch是如何做到快速索引的呢？

Elasticsearch采用了倒排索引的方式，这种方式比传统的关系型数据库中采用的B-Tree和B+Tree要快。

#### I.物理存储

1)Cluster

  集群是一个或者一个以上的节点（服务器）的集合。并在所有节点上提供联合的索引和搜索功能。集群由唯一的名称标识，默认情况下是“elasticsearch”。该名称很重要，因为如果节点设置为通过其名称加入集群，则节点只能是集群的一部分。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\es集群.jpg)

2) 节点Node

节点，一个ES实例就是一个node，一个机器可以有多个实例，所以并不是说一台机器就是一个node，大多数情况下，每个node运行在一个独立的环境或者虚拟机上。

  节点是作为集群一部分的单一服务器，可以存储您的数据，并参与集群的索引和搜索功能。就像一个集群一样，一个节点由一个名称来标识，默认情况下是一个随机的通用唯一标识符（UUID），它在启动时分配给该节点。所有的节点通过设置集群名cluster.name来确定属于某个集群。

3) Index 索引

  **索引是具有某种相似特征的文档（documents）的集合**。

例如，您可以拥有客户数据的索引，产品目录的另一个索引，以及订单数据的另一个索引。索引由名称（必须全部为小写）标识，该名称用于在针对其中的文档执行索引，搜索，更新和删除操作时引用索引。index包含多个shard

4）分片（Shard）

  **每个文档都储存在一个主分片中，然后复制到不同的副本分片中**。

由于索引存在存储可能超过单个节点的硬件限制的大量数据。例如，占用1TB磁盘空间的10亿个文档的单个索引可能不适合单个节点的磁盘，或者可能太慢，无法单独从单个节点提供搜索请求。因此分片就是为了解决这个问题。每个索引被分成若干个分片。分片被存储在不同的节点中。.每个shard都是一个最小工作单元，承载部分数据，lucene实例。分片很重要，使用分片的作用：

​	a) 允许水平分割/缩放内容，提高扩展能力；

​	b) 允许在分片（可能在多个节点上）分布和并行操作，从而提高性能/吞吐量；

注意：**每个es分片，都是一个Lucene的索引**

5） 复制（Replica）

  副本是分片的复制。副本的作用：

a) 如果分片/节点出现故障，则可提供高可用性

b) 允许扩展搜索量/吞吐量，**因为可以对所有副本并行执行搜索**

  默认情默认情况下，**ElasticSearch中的每个索引都分配了5个主分片和5个副本况下（即5primary+5replica=10个分片）且主分片和备分片不会出现在同一个节点上（防止单点故障）**。创建索引后，您可以随时动态更改副本数，但不能更改主分片数。

请求路由：当索引一个文档的时候，文档会被存储到一个主分片中。 Elasticsearch 如何知道一个文档应该存放到哪个分片中呢？当我们创建文档时，它如何决定这个文档应当被存储在分片 1 还是分片 2 中呢？

首先这肯定不会是随机的，否则将来要获取文档的时候我们就不知道从何处寻找了。实际上，这个过程是根据下面这个公式决定的：

`shard = hash(routing) % number_of_primary_shards`

routing 是一个可变值，默认是文档的 _id ，也可以设置成一个自定义的值。 routing 通过 hash 函数生成一个数字，然后这个数字再除以 number_of_primary_shards （主分片的数量）后得到 余数 。这个分布在 0 到 number_of_primary_shards-1 之间的余数，就是我们所寻求的文档所在分片的位置。

7）文档（document）

**文档是储存在elasticserarch中JSON格式字符串**

#### II.逻辑存储

1）索引 Index

2）类型 Type

  类型是您的索引的逻辑类别/分区，允许您将不同类型的文档存储在同一索引中，例如用户的一种类型，博客文章的另一种类型。在索引中不再可能创建多个类型，并且在以后的版本中将删除整个类型的概念。

3)Document

  文档是可以索引的基本信息单元。例如，您可以为单个客户提供文档，单个产品的文档，单个订单的另一个文档。该文档以JSON（JavaScript Object Notation）表示，它是一种无处不在的互联网数据交换格式。

4)Field

  多个字段组成一个文档。一个索引的所有文档类型中对于具有相同名称的字段必须是同一种数据类型，与SQL SERVER不同，同一数据库中不同表中相同名称的列数据类型可以不一样。

5)Mapping

  映射就是字段即字段和字段类型的对应关系。映射机制用于进行字段类型确认，将每个字段匹配为一种确定的数据类型。例如：字段name，类型是String；字段age，类型是Integer。除此之外字段的类型也可以是日期、布尔值、数组、浮点数、对象等等。

### 3：对外API

对外是以HTTP协议的方式约定对外提供；官方文档用的是curl模拟工具，这是控制台工具，有一个Head插件

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\es-Rest请求头.jpg)

```java
//官方给出的java与交互
class ApI{
    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                    new HttpHost("localhost", 9200, "http")));
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
searchSourceBuilder.query(QueryBuilders.matchAllQuery());            
searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));
SearchRequest searchRequest = new SearchRequest();
searchRequest.indices("social-*");
searchRequest.source(searchSourceBuilder);
SearchResponse searchResponse = client.search(searchRequest);
}

```

### 4：使用理解

DSL: Domain Special Language:结构化查询是一种 灵活、多表现的查询语言。

全文搜索（在全文字段上进行全文搜索，通过全文查询理解被查询字段是如何被索引和分析的，在执行之前将每个字段的分析器（或搜索分词器）应用与查询字符串）

1：结构化查询需要使用query 参数
空查询{} 相当于 match_all查询子句
2：使用from 和 size参数实现分页
3：简单子句使用match查询子句寻找包含某个字段的值
4：复合子句，bool过滤子句允许你合并其他合法子句包括must、must_not、should （如若没有must子句至少应该包含一个should子句，如果有must子句，那没有should子句也可以进行查询）
   must: 多个查询条件的完全匹配，相当于and
   must_not：多个查询条件的相反匹配，想大于not
   should: 至少有一个条件匹配，相当于 or .
 =================结构化过滤 Filter  DSL============
原则上说，使用查询语句做全文搜索的或其他需要惊醒相关性分析评分的时候，剩下的全部用 过滤语句

5: term 是查询过滤语句，比如数字、日期、布尔值、或not_analyzed 的字符串（未经分析的文本数据类型）
   terms 是和term 有嗲雷士，但是terms 允许指定多个匹配条件，比如某个字段指定了多个值。
6：range 过滤：允许我们按照指定范围查账号一批数据。gt 大于，gte 大于等于 ，lt小于， lte 小于等于
7：exits 和 missing 过滤：可以用于查找文档中是否指定包含指定字段或没有某个字段，类似于SQL语句中的is_null 条件。
8：bool过滤 ：同4；


8: match_all查询: 是没有查询条件的默认语句
9：match查询： 是一个标准查询，不管你需要全文本查询还是精确查询基本上都用到它。
如果用match 下指定的确切值，在遇到数字、日期、布尔值、或not_analyzed 的字符串，他将会为你搜索你给定的值。
10：multi_match 查询：允许你match查询的基础上同时搜索多个字段。
11：bool 查询： 和bool过滤相似，用于合并多个查询子句，不同的是bool过滤可以直接给出是否匹配成功，而bool 查询要计算每一个查询子句的_score(相关性分值)。
12：
如果query 语句中已经有了match ，此时还想要term语句，可以使用filter 在里面写term语句实现同时兼容，此时外层还需要加一层query的上下文关系。



13：排序————默认情况下 ，es 会以相关性分值_score 字段（一个浮点型的值）进行倒叙排序
sort(与查询语句query平齐):
"sort":[{"date":{"order":"desc"},{"_score":{"order":"desc"}}]

#### 1）：Query与Filter

查询在Query查询上下文和Filter过滤器上下文中，执行的操作是不一样的：

1、查询上下文：是在使用query进行查询时的执行环境，比如使用search的时候。
在查询上下文中，查询会回答这个问题——“这个文档是否匹配这个查询，它的相关度高么？”
ES中索引的数据都会存储一个_score分值，分值越高就代表越匹配。即使lucene使用倒排索引，对于某个搜索的分值计算还是需要一定的时间消耗。

2、过滤器上下文：在使用filter参数时候的执行环境，比如在bool查询中使用Must_not或者filter
在过滤器上下文中，查询会回答这个问题——“这个文档是否匹配？”
它不会去计算任何分值，也不会关心返回的排序问题，因此效率会高一点。
另外，经常使用过滤器，ES会自动的缓存过滤器的内容，这对于查询来说，会提高很多性能。

总而言之：
1 查询上下文：查询操作不仅仅会进行查询，还会计算分值，用于确定相关度；
2 过滤器上下文：查询操作仅判断是否满足查询条件，不会计算得分，查询的结果可以被缓存。

所以，根据实际的需求是否需要获取得分，考虑性能因素，选择不同的查询子句。

#### 2）sort 排序和 order 排序有什么区别？

#### 3） match 和 term 有什么区别？

### 5：索引管理

创建索引的时候可以通过number_of_shards 和 number_ of _ replicas 参数的数量来修改分片和副本的数量





### 安装 Sense[编辑](https://github.com/elasticsearch-cn/elasticsearch-definitive-guide/edit/cn/010_Intro/10_Installing_ES.asciidoc)

Sense 是一个 [Kibana](https://www.elastic.co/guide/en/kibana/4.6/index.html) 应用 它提供交互式的控制台，通过你的浏览器直接向 Elasticsearch 提交请求

## （二）solr



# 八：Seata



# 九：linux-shell脚本

第一行永远都是从#!/bin/bash开始的，“#!”是一个约定的标记，它告诉系统这个脚本需要什么解释器来执行

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\shell\whereis bash.jpg)

##  一：【变量】

接下来我们来讲下shell变量，和其他语言变量一样，shell变量也有自己的语法规则：

1. 定义变量，不能加$，
2. 使用变量，需要加$和{}，
3. 等号左右需顶格不留空。
4. 注意：查看全局变量：printenv / env ;要显示个别环境变量的值 ，只能用printenv 命令，不要用env 命令

## 二：【字符串】

字符串和其他语言相类似，但仍需要注意以下几点：

1. 单双引号的区别：

单引号('')
1.不能有变量，即使有也视为无效
2.不能有单引号，使用转义符也不行
双引号(""):
1.可以有变量
2.可以出现转义符

所以推荐使用双引号。

2.字符串的处理

[字符串拼接] 不用"+"号，直接用$a$b

[字符串长度] 变量前面加#，e.g. a="hello" echo ${#str} 输出5

[提取字符串] 使用":", e.g. b="world" echo ${b:0:5} 输出world   （注意，下标是从0开始，到n+1）

## 三【数组】

定义数组的方式有三：

### 1:横式

array_name=(value0 value1 value2 value3)

### 2:竖式:

array_name=(
value0
value1
value2
value3
)

### 3:单独定义

array_name[0]=value0
array_name[1]=value1
array_name[n]=valuen

### 4：读取数组：

value1=${array_name1}

[所有元素]:echo ${array_name[@]}

数组长度：

length=${#array_name@}

length=${#array_name[*]}

数组中某个元素的长度：length=${#array_name[n]}

## 四：【运算符】

运算符的内容比较多，首先来看下“算数运算符”。顾名思义，算数运算符就是用来做算术的，先来认识一个单词：expr（怎么读我也不知道），这是一个算术运算符的表达式工具，没有它就做不了算术，

### 1：算数运算符

| 表达式 | 解释   | 备注            |
| ------ | ------ | --------------- |
| +      | 加     | `expr $a + $b`  |
| -      | 减     | `expr $a - $b`  |
| *      | 乘     | `expr $a \* $b` |
| /      | 除     | `expr $b / $a`  |
| %      | 取余   | `expr $b % $a`  |
| =      | 赋值   |                 |
| ==     | 相等   |                 |
| !=     | 不相等 |                 |

### 2：关系运算符

| 表达式 | 解释     | 备注          |
| ------ | -------- | ------------- |
| -eq    | 相等     | equal         |
| -ne    | 不相等   | not equal     |
| -gt    | 大于     | greater than  |
| -lt    | 小于     | less than     |
| -ge    | 大于等于 | greater equal |
| -le    | 小于等于 | less equal    |

注意：返回的是true/false

### 3：布尔运算符

| 表达式 | 解释 | 备注                      |
| ------ | ---- | ------------------------- |
| ！     | 非   | 表达式为true，则返回false |
| -o     | 或   | 有一个true，则返回true    |
| -a     | 和   | 两个都为true，才返回true  |

### 4：逻辑运算符

| 表达式 | 解释    | 备注                     |
| ------ | ------- | ------------------------ |
| &&     | 逻辑AND | 有一个true，则返回true   |
| \|\|   | 逻辑OR  | 两个都为true，则返回true |

1.语法使用上
使用【布尔】时，只需要一个中括号[] ; 使用【逻辑】时，需要两个中括号[[ ]]。

### [5：字符串运算符](非数字)

a="abc", b="efg"

| 表达式 | 解释                     | 备注                     |
| ------ | ------------------------ | ------------------------ |
| =      | 检测两个字符串是否相等   | [ $a = $b ] 返回 false。 |
| ！=    | 检测两个字符串是否不相等 | [ $a != $b ] 返回 true。 |
| -z     | 检测字符串长度是否为0    | [ -z $a ] 返回 false。   |
| -n     | 检测字符串长度是否为0    | [ -n $a ] 返回 true。    |
| str    | 检测字符串是否为空       | [ $a ] 返回 true。       |

### 6：文件测试运算符

| 表达式 | 解释                                     | 备注                     |
| ------ | ---------------------------------------- | ------------------------ |
| 表达式 | 解释                                     | 备注                     |
| -d     | 检查文件是否是目录                       | [ -d $file ] 返回 false  |
| -f     | 检查文件是否是普通文件(非目录非设备文件) | [ -f $file ] 返回 true。 |
| -r     | 检查文件是否可读                         | [ -r $file ] 返回 true。 |
| -w     | 检查文件是否可写                         | [ -w $file ] 返回 true。 |
| -x     | 检查文件是否可执行                       | [ -x $file ] 返回 true。 |
| -s     | 检查文件是否不为空                       | [ -s $file ] 返回 true。 |
| -e     | 检查文件是否存在(包括文件或目录)         | [ -e $file ] 返回 true。 |

## 五：【echo】

[显示普通字符串]：echo "test" 或者 echo test
[显示转义符]：echo "\"test\""
[显示变量]：name="jhon" echo "my name is $name"
[显示换行]：echo -e "demo\n" -e 开启转义
[显示不换行]：echo -e "demo\c" -e 开启转义
[显示结果保存到文件中]：echo "this is a test" > test
[原样输出(不转义)]：echo '$name\n"' 使用单引号
[显示命令执行结果]：echo `date`

## 六:【printf】

### 1：printf和echo在使用上都是输出内容，区别是：

1. printf需要手工添加换行符\n，echo不需要
2. printf支持格式化输出，echo不行

先来看下printf的格式：

printf format-string [arguments...]
format-string: 为格式控制字符串
arguments: 为参数列表

### 2：常用的转义符还有：

\r 回车
\n 换行
\t 水平制表符
\v 垂直制表符
\\ 反斜杠

## 七：【流程控制】

接下来的内容比较重要，希望大家在理解的基础上自己多加练习。

### 1：if条件

#### 1）：if confition

then
       command
fi

写成一行：if condition; then command; fi

#### 2）：if...else

if condition
then
      command1
else
      command2
fi

#### 3）：if...elif...else

if condition1
then
       command1
elif condition2
then
       command2
else
       command3
fi

### 2：for循环

for value in item1 item2 item3... itemN
do
    command1
    command2
    command3
    ...
    commandN
done

写成一行：for value in item1 item2 item3... itemN; do command1; command2... done;

### 3：while循环

while condition
do
    command
done

注:当条件成立时，执行循环(可以一次都不执行)

### 4：until循环

until condition
do
    command
done

注：先执行循环，直到条件成立时停止(至少执行一次循环)

### 5：case选择

case value in
case1)
         command1
         ;;
case2)
        command2
        ;;
case3)
       command3
       ;;
*)
      commandN
      ;;
esac

- 注意：

- value可以是变量，也可以是常数;

- .如果没有匹配到，则使用*处理异常

- 跳出循环：break & continue

  【break】:条件满足，跳出所有循环
  【continue】:条件满足，跳出当前循环，继续执行后面的循环

## 八：【函数】

如何定义一个函数：

function_name(){

​      command

}

如何引用一个函数：

function_name parm1 parm2... parmN

# 九:xxl-job

# 十:Netty

## （一）：先看一个例子

```java
//从xxljob里截取的 
public void start(final XxlRpcProviderFactory xxlRpcProviderFactory) throws Exception {
        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                // param
                final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.makeServerThreadPool(
                        NettyHttpServer.class.getSimpleName(),
                        xxlRpcProviderFactory.getCorePoolSize(),
                        xxlRpcProviderFactory.getMaxPoolSize());
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    // start server
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                        //指定 EventLoopGroup 以处理客户端事件；需要适用于 NIO 的实现
                            .channel(NioServerSocketChannel.class)
                        //
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    channel.pipeline()
                                            // beat 3N, close if idle
                                            .addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL * 3, TimeUnit.SECONDS))  
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(5 * 1024 * 1024))  // merge request & reponse to FULL
                                            .addLast(new NettyHttpServerHandler(xxlRpcProviderFactory, serverHandlerPool));
                                }
                            })
                            .childOption(ChannelOption.SO_KEEPALIVE, true);
                    // 连接到远程节点，阻塞等待直到连接完成
                    ChannelFuture future = bootstrap.bind(xxlRpcProviderFactory.getPort()).sync();
                    logger.info(">>>>>>>>>>> xxl-rpc remoting server start success, nettype = {}, port = {}",          NettyHttpServer.class.getName(), xxlRpcProviderFactory.getPort());
                    onStarted();

                    // wait util stop阻塞，直到Channel 关闭
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        logger.info(">>>>>>>>>>> xxl-rpc remoting server stop.");
                    } else {
                        logger.error(">>>>>>>>>>> xxl-rpc remoting server error.", e);
                    }
                } finally {
                    // stop
                    try {
                        //关闭线程池并且释放所有的资源
                        serverHandlerPool.shutdown();	// shutdownNow
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        thread.setDaemon(true);	// daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        thread.start();
    }
```

### 1：ServerBootstrap：

提供了用于处理Channel 事件的EventLoop（聚合了一个多路复用器对象），共享事件的EventLoop也避免了创建额外的线程和上下文的切换，
netty的 ChannelPipeline 和 ChannelHandler 机制类似于Servlet 和Filter ，是职责链模式的一种变形，主要是为了方便时间的拦截，和业务逻辑的制定
，你可以根据需要，通过在 ChannelPipeline 中将多个ChannelHandler链接在一起，但是，如果在引导的过程中你只能设置一个 ChannelHandler，
那么你应该怎么做到这一点呢？正是针对于这个用例，

### 2：ChannelInitializer

Netty 提供了一个特殊ChannelInboundHandlerAdapter的子类 ：ChannelInitializer,它提供了一个initChannel方法，
你只需要简单地向 Bootstrap 或 ServerBootstrap 的实例提供你的 Channel-Initializer 实现即可，并且一旦 Channel 被注册到了它的 EventLoop 之后，就会调用你的initChannel()版本。在该方法返回之后，ChannelInitializer 的实例将会从 Channel-Pipeline 中移除它自己。

3：你可以使用 option()方法来将 ChannelOption 应用到引导。你所提供的值将会被自动应用到引导所创建的所有 Channel。可用的 ChannelOption 包括了底层连接的详细信息，如keep-alive 或者超时属性以及缓冲区设置。
4： Bootstrap 类也可以被用于无连接的协议（区别于基于 TCP 协议的 SocketChannel）。为此，Netty 提供了各种 DatagramChannel 的实现。唯一区别就
是，不再调用 connect()方法，而是只调用 bind()方法，
5：netty 的线程模型并不是一层 不变的，取决于用户启动的参数的配置；netty可以同时支持Reactor单线程模型，多线程模型，主从Reactor模型，
6：channelFuture的设计师因为netty的I/O操作都是异步的，所有的调用都会立即返回，为了获取异步结果而设计的，有两种状态：uncompleted和completed;

7：Promise是可写的Future， 

8：TCP 粘包的原因：
1：应用程序writer 写入的字节大小大于套接字缓冲区的大小
2：进行MSS大小的TCP分段
3：以太网帧的playload大于MTU进行ip 分片

解决策略：
1：消息定长度，例如每个报文大小固定200字节，如果不够空位补空格，
2：在包尾增加换行符进行分割，例如FTP
3：讲消息分为消息头和消息体，消息头中包含消息的总长度，通常涉及思路是消息头中有int32字段来标识消息总长度
4：更复杂的应用层协议

## （二）：组件介绍

为了更好的理解和进一步深入Netty，我们先总体认识一下Netty用到的组件及它们在整个Netty架构中是怎么协调工作的。Netty应用中必不可少的组件：

- Bootstrap or ServerBootstrap：一个Netty应用通常由一个Bootstrap开始，它主要作用是配置整个Netty程序，串联起各个组件。

- EventLoop：目的是为Channel处理IO操作，一个EventLoop可以为多个Channel服务。

- EventLoopGroup：EventLoopGroup会包含多个EventLoop。

- ChannelPipeline：一个Netty应用基于ChannelPipeline机制，这种机制需要依赖于EventLoop和EventLoopGroup，因为它们三个都和事件或者事件处理相关。

- Channel：代表了一个Socket链接，或者其它和IO操作相关的组件，它和EventLoop一起用来参与IO处理。

- Future or ChannelFuture：在Netty中所有的IO操作都是异步的，因此，你不能立刻得知消息是否被正确处理，但是我们可以过一会等它执行完成或者直接注册一个监听，具体的实现就是通过Future和ChannelFutures,他们可以注册一个监听，当操作执行成功或失败时监听会自动触发。总之，所有的操作都会返回一个ChannelFuture。

- ChannelInitializer：当一个链接建立时，我们需要知道怎么来接收或者发送数据，当然，我们有各种各样的Handler实现来处理它，那么ChannelInitializer便是用来配置这些Handler，它会提供一个ChannelPipeline，并把Handler加入到ChannelPipeline。

- ChannelHandler

  Handler，为了支持各种协议和处理数据的方式，便诞生了Handler组件。Handler主要用来处理各种事件，这里的事件很广泛，比如可以是连接、数据接收、异常、数据转换等。

  ChannelInboundHandler，一个最常用的Handler。这个Handler的作用就是处理接收到数据时的事件，也就是说，我们的业务逻辑一般就是写在这个Handler里面的，ChannelInboundHandler就是用来处理我们的核心业务逻辑。

### 1:EventLoop

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\Eventloop类层次结构.jpg)

一个 EventLoopGroup 包含一个或者多个 EventLoop； 
一个 EventLoop 在它的生命周期内只和一个 Thread 绑定； 
所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理； 
一个 Channel 在它的生命周期内只注册于一个 EventLoop； 
一个 EventLoop 可能会被分配给一个或多个 Channel。

注意，在这种设计中，一个给定 Channel 的 I/O 操作都是由相同的 Thread 执行的，实际上消除了对于同步的需要。

### 2:Channel



### 3:ChannelFuture

正如我们已经解释过的那样，Netty 中所有的 I/O 操作都是异步的。因为一个操作可能不会立即返回，所以我们需要一种用于在之后的某个时间点确定其结果的方法。为此，Netty 提供了ChannelFuture 接口，其 addListener()方法注册了一个 ChannelFutureListener，以便在某个操作完成时（无论是否成功）得到通知。

### 4:ChannelHandler(重点,来自netty实战)

从应用程序开发人员的角度来看，Netty 的主要组件是 ChannelHandler，它充当了所有处理入站和出站数据的应用程序逻辑的容器。这是可行的，因为 ChannelHandler 的方法是由网络事件（其中术语“事件”的使用非常广泛）触发的。事实上，ChannelHandler 可专门用于几乎任何类型的动作，例如将数据从一种格式转换为另外一种格式，或者处理转换过程中所抛出的异常。举例来说，ChannelInboundHandler 是一个你将会经常实现的子接口。这种类型ChannelHandler 接收入站事件和数据，这些数据随后将会被你的应用程序的业务逻辑所处理。当你要给连接的客户端发送响应时，也可以从 ChannelInboundHandler 冲刷数据。你的应用程序的业务逻辑通常驻留在一个或者多个 ChannelInboundHandler 中。

正如我们之前所说的，有许多不同类型的 ChannelHandler，它们各自的功能主要取决于它们的超类。Netty 以**适配器**类的形式提供了大量默认的 ChannelHandler 实现，其旨在简化应用程序处理逻辑的开发过程。你已经看到了，ChannelPipeline中的每个ChannelHandler将负责把事件转发到链中的下一个 ChannelHandler。这些适配器类（及它们的子类）将自动执行这个操作，所以你可以只重写那些你想要特殊处理的方法和事件。

下面这些是编写自定义 ChannelHandler 时经常会用到的适配器类：

- ChannelHandlerAdapter
- ChannelInboundHandlerAdapter
- ChannelOutboundHandlerAdapter
- ChannelDuplexHandler

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\netty-channelhandler.jpg)

最常见的情况是，你的应用程序会利用一个 ChannelHandler 来接收解码消息，并对该数据应用业务逻辑。要创建一个这样的 ChannelHandler，你只需要扩展基类 **SimpleChannel-InboundHandler**<T>，其中 T 是你要处理的消息的 Java 类型 。在这个 ChannelHandler 中， 你将需要重写基类的一个或者多个方法，并且获取一个到 ChannelHandlerContext 的引用，这个引用将作为输入参数传递给 ChannelHandler 的所有方法。

由于 SimpleChannelInboundHandler 会自动释放资源，所以你不应该存储指向任何消息的引用供将来使用，因为这些引用都将会失效；在这种类型的 ChannelHandler 中，最重要的方法是 channelRead0(ChannelHandlerContext,T)。除了要求不要阻塞当前的 I/O 线程之外，其具体实现完全取决于你。

### 5:ChannelPipeline

我们感兴趣的方法是：
 channelRead()—对于每个传入的消息都要调用； 
   channelReadComplete()—通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息； 
   exceptionCaught()—在读取操作期间，有异常抛出时会调用。
服务端：
ChannelInboundHandlerAdapter 有一个直观的 API，并且它的每个方法都可以被重写以
挂钩到事件生命周期的恰当点上。

客户端：
在这个场景下，你将扩展 SimpleChannelInboundHandler 类以处理所有必须的任务，  
channelActive()——在到服务器的连接已经建立之后将被调用；
	channelRead0()① 当从服务器接收到一条消息时被调用
	exceptionCaught()——在处理过程中引发异常时被调用。
	
区别：
SimpleChannelInboundHandler 与 ChannelInboundHandler
你可能会想：为什么我们在客户端使用的是 SimpleChannelInboundHandler，而不是在 EchoServerHandler 中所使用的 ChannelInboundHandlerAdapter 呢？这和两个因素的相互作用有
关：业务逻辑如何处理消息以及 Netty 如何管理资源。
在客户端，当 channelRead0()方法完成时，你已经有了传入消息，并且已经处理完它了。当该方
法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的 ByteBuf 的内存引用。
在 EchoServerHandler 中，你仍然需要将传入消息回送给发送者，而 write()操作是异步的，直
到 channelRead()方法返回后可能仍然没有完成（如代码清单 2-1 所示）。为此，EchoServerHandler
扩展了 ChannelInboundHandlerAdapter，其在这个时间点上不会释放消息。

## （二）：组件详解：

### 1:Channel—Socket；

​    EmbeddedChannel； LocalServerChannel； NioDatagramChannel； NioSctpChannel； NioSocketChannel。

Netty中的Channel与Java NIO的概念一样，都是对一个实体或连接的抽象，但Netty提供了一套更加通用的API。就以网络套接字为例，在Java中OIO与NIO是截然不同的两套API，假设你之前使用的是OIO而又想更改为NIO实现，那么几乎需要重写所有代码。而在Netty中，只需要更改短短几行代码（更改Channel与EventLoop的实现类，如把OioServerSocketChannel替换为NioServerSocketChannel），就可以完成OIO与NIO（或其他）之间的转换。每个Channel最终都会被分配一个ChannelPipeline和ChannelConfig，前者持有所有负责处理入站与出站数据以及事件的ChannelHandler，后者包含了该Channel的所有配置设置，并且支持热更新，由于不同的传输类型可能具有其特别的配置，所以该类可能会实现为ChannelConfig的不同子类。

Channel是线程安全的（与之后要讲的线程模型有关），因此你完全可以在多个线程中复用同一个Channel。

### 2:EventLoop—控制流、多线程处理、并发； 

  1）

### 3:ChannelFuture—异步通知。



### 4：ChannelHandler 接口：

1：状态：
ChannelUnregistered Channel 已经被创建，但还未注册到 EventLoop
ChannelRegistered Channel 已经被注册到了 EventLoop
ChannelActive Channel 处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
ChannelInactive Channel 没有连接到远程节点

2：ChannelHandler生命周期：
handlerAdded 当把 ChannelHandler 添加到 ChannelPipeline 中时被调用
handlerRemoved 当从 ChannelPipeline 中移除 ChannelHandler 时被调用
exceptionCaught 当处理过程中在 ChannelPipeline 中有错误产生时被调用

3：ChannelInboundHandler 的生命周期方法
channelRegistered 当 Channel 已经注册到它的 EventLoop 并且能够处理 I/O 时被调用
channelUnregistered 当 Channel 从它的 EventLoop 注销并且无法处理任何 I/O 时被调用
channelActive 当 Channel 处于活动状态时被调用；Channel 已经连接/绑定并且已经就绪
channelInactive 当 Channel 离开活动状态并且不再连接它的远程节点时被调用
channelReadComplete 当Channel上的一个读操作完成时被调用 ①
channelRead 当从 Channel 读取数据时被调用
ChannelWritabilityChanged
当 Channel 的可写状态发生改变时被调用。用户可以确保写操作不会完成
得太快（以避免发生 OutOfMemoryError）或者可以在 Channel 变为再
次可写时恢复写入。可以通过调用 Channel 的 isWritable()方法来检测
Channel 的可写性。与可写性相关的阈值可以通过 Channel.config().
setWriteHighWaterMark()和 Channel.config().setWriteLowWaterMark()方法来设置
userEventTriggered 当 ChannelnboundHandler.fireUserEventTriggered()方法被调
用时被调用，因为一个 POJO 被传经了 ChannelPipeline

4：ChannelHandler 适配器
你可以使用 ChannelInboundHandlerAdapter 和 ChannelOutboundHandlerAdapter
类作为自己的 ChannelHandler 的起始点。这两个适配器分别提供了 ChannelInboundHandler
和 ChannelOutboundHandler 的基本实现。通过扩展抽象类 ChannelHandlerAdapter，它们
获得了它们共同的超接口 ChannelHandler 的方法

 5：ChannelHandlerAdapter 还提供了实用方法 isSharable()。如果其对应的实现被标
注为 Sharable，那么这个方法将返回 true，表示它可以被添加到多个 ChannelPipeline
中。ChannelInboundHandlerAdapter 和 ChannelOutboundHandlerAdapter 中所
提供的方法体调用了其相关联的 ChannelHandlerContext 上的等效方法，从而将事件转发到
了 ChannelPipeline 中的下一个 ChannelHandler 中。





## （三）：零拷贝

零拷贝（zero-copy）是一种目前只有在使用 NIO 和 Epoll 传输时才可使用的特性。它使你可以快速
高效地将数据从文件系统移动到网络接口，而不需要将其从内核空间复制到用户空间，其在像 FTP 或者
HTTP 这样的协议中可以显著地提升性能。但是，并不是所有的操作系统都支持这一特性。特别地，它对
于实现了数据加密或者压缩的文件系统是不可用的——只能传输文件的原始内容。反过来说，传输已被
加密的文件则不是问题。

## （四）：ByteBuf——Netty 的数据容器

ByteBuf 维护了两个不同的索引：一个用于读取，一个用于写入。当你从 ByteBuf 读取时，它的 readerIndex 将会被递增已经被读取的字节数。同样地，当你写入 ByteBuf 时，它的writerIndex 也会被递增。
1．堆缓冲区
最常用的 ByteBuf 模式是将数据存储在 JVM 的堆空间中。这种模式被称为支撑数组
（backing array），它能在没有使用池化的情况下提供快速的分配和释放。这种方式，如代码清单
5-1 所示，非常适合于有遗留的数据需要处理的情况。
ByteBuf heapBuf = ...;
if (heapBuf.hasArray()) {
byte[] array = heapBuf.array();
int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
int length = heapBuf.readableBytes();
handleArray(array, offset, length);
}
注意 当 hasArray()方法返回 false 时，尝试访问支撑数组将触发一个 Unsupported
OperationException。这个模式类似于 JDK 的 ByteBuffer 的用法。
2．直接缓冲区
直接缓冲区是另外一种 ByteBuf 模式。我们期望用于对象创建的内存分配永远都来自于堆
中，但这并不是必须的——NIO 在 JDK 1.4 中引入的 ByteBuffer 类允许 JVM 实现通过本地调
用来分配内存。这主要是为了避免在每次调用本地 I/O 操作之前（或者之后）将缓冲区的内容复
制到一个中间缓冲区（或者从中间缓冲区把内容复制到缓冲区）。
ByteBuf directBuf = ...;
if (!directBuf.hasArray()) {
int length = directBuf.readableBytes();
byte[] array = new byte[length];
directBuf.getBytes(directBuf.readerIndex(), array);
handleArray(array, 0, length);
}
3．复合缓冲区
第三种也是最后一种模式使用的是复合缓冲区，它为多个 ByteBuf 提供一个聚合视图。在
这里你可以根据需要添加或者删除 ByteBuf 实例，这是一个 JDK 的 ByteBuffer 实现完全缺
失的特性。
Netty 通过一个 ByteBuf 子类——CompositeByteBuf——实现了这个模式，它提供了一
个将多个缓冲区表示为单个合并缓冲区的虚拟表示。

警告 CompositeByteBuf 中的 ByteBuf 实例可能同时包含直接内存分配和非直接内存分配。
如果其中只有一个实例，那么对 CompositeByteBuf 上的 hasArray()方法的调用将返回该组
件上的 hasArray()方法的值；否则它将返回 false。

Netty使用了CompositeByteBuf来优化套接字的I/O操作，尽可能地消除了
由JDK的缓冲区实现所导致的性能以及内存使用率的惩罚。不想为每个消息都重新分配这两个缓冲区，所以使用 CompositeByteBuf 是一个
完美的选择。它在消除了没必要的复制的同时，暴露了通用的 ByteBuf API。图 5-2 展示了生成
的消息布局。

# 十一：mysql

### 一：索引

目前Mysql主要支持以下几种索引：B树索引，散列索引，空间索引，全文索引，

索引是在存储引擎实现的，不同的存储引擎的索引实现会有一些差异。

1. 逻辑分类：单利索引，复合索引，唯一索引，和非唯一索引。
2. 如果索引键值的逻辑顺序和索引所服务的的表中相应行的物理顺序相同，那么该索引被称为“簇索引”；

联合索引(e1, e3)：一条查询语句往往应为mysql优化器的关系只用一个索引，就算你有两个索引，他也只用一个；在只用一个的基础之上，联合索引是会比单列索引要快的；

触发联合索引是有条件的：
1、使用联合索引的全部索引键，可触发索引的使用。
例如：SELECT E.* FROM E WHERE E.e1=1 AND E.e3=2

2、使用联合索引的前缀部分索引键，如“key_part_1 <op>常量”，可触发索引的使用。 

​     例如：SELECT E.* FROM E   WHERE E.e1=1

​    使用部分索引键，但不是联合索引的前缀部分，如“key_part_2 <op>常量”，不可触发索引的使用。

​    例如：SELECT E.* FROM E WHERE E.e3=1

3、使用联合索引的全部索引键，但索引键不是AND操作，不可触发索引的使用。
例如：SELECT E.* FROM E WHERE E.e3=2 OR E.e1=1

# 三：数据库设计三范式：

####  第一范式（1NF）

- 数据库的列不可分割，如果有的列包含多种信息，可以把它抽取出来成为一个单独的表；

#### 第二范式（2NF）

- 数据表中的每一行，都应该有唯一的标识，即每一行都应有主键区分

####  第三范式（3NF）

- 数据表中的所有非关键字段，必须依赖于主键

#### 三范式的核心思想

- 不要出现冗余数据（用最少的空间存储最多的信息），第二范式是避免了冗余行的出现，第三范式是避免了冗余列的出现；

# 四：MySQL服务器逻辑架构

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\20170801185146233.png)

### （一）：三个层次：

#### 1:连接层

主要是连接与线程处理，这一层并不是MySQL独有，一般的基于C/S架构的都有类似组件，比如连接处理、授权认证、安全等。

#### 2:sql处理层(服务层)

这是MySQL的核心部分，还可以叫做 SQL Layer，包括缓存查询、解析器、优化器，在 MySQL据库系统处理底层数据之前的所有工作都是在这一层完成的，这一层包含了MySQL核心功能，包括解析、优化SQL语句，查询缓存目录，内置函数（日期、时间、加密等函数）的实现，还有各个存储引擎提供的功能都集中在这一层，如存储过程，触发器，视 图等。

#### 3:数据存储(存储引擎)层

负责数据存储，存储引擎的不同，存储方式、数据格式、提取方式等都不相同，这一部分也是很大影响数据存储与提取的性能的。但是，与分层的思想一致，SQL处理层是通过API与存储引擎通信的，API屏蔽了下层的差异，下层提供对外接口，上层负责调用即可，不必清楚下层是怎么实现的。

**三个层次如何工作的？？？**

首先就是连接层，也就是当MySQL启动（MySQL服务器就是一个进程），完事就等待客户端连接，每当一个客户端发送连接请求，服务器都会新建一个线程处理（如果是线程池的话，则是分配一个空的线程），每个线程独立，拥有各自的内存处理空间，但是，如果这个请求只是查询，没关系，但是若是修改数据，很显然，当两个线程修改同一块内存是会引发数据同步问题的，来看下连接处理的流程图：

### （二）：InnoDB存储引擎（[https://github.com/h2pl/Java-Tutorial/blob/master/docs/database/%E9%87%8D%E6%96%B0%E5%AD%A6%E4%B9%A0Mysql%E6%95%B0%E6%8D%AE%E5%BA%933%EF%BC%9AMysql%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E4%B8%8E%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8%E5%8E%9F%E7%90%86.md](https://github.com/h2pl/Java-Tutorial/blob/master/docs/database/重新学习Mysql数据库3：Mysql存储引擎与数据存储原理.md)）

#### 1:后台线程

  	innoDB存储引擎是多线程的模型，后台有很多不同的后台线程

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\PASOScreenShot20190115113645865.PNG)

##### 1)Master Thread

 这是最核心的一个线程,主要负责将缓冲池中的数据异步刷新到磁盘,保证数据的一致性,包括赃页的刷新、合并插入缓冲、UNDO 页的回收等.

##### 2)IO Thread

 在 InnoDB 存储引擎中大量使用了异步 IO 来处理写 IO 请求, IO Thread 的工作主要是负责这些 IO 请求的回调处理。

##### 3)Purge Thread

 事务被提交之后, undo log 可能不再需要,因此需要 Purge Thread 来回收已经使用并分配的 undo页. InnoDB 支持多个 Purge Thread, 这样做可以加快 undo 页的回收。

##### 4)Page Cleaner Thread

 Page Cleaner Thread 是在InnoDB 1.2.x版本新引入的,其作用是将之前版本中脏页的刷新操作都放入单独的线程中来完成,这样减轻了 Master Thread 的工作及对于用户查询线程的阻塞。

##### 内存Buffer Pool

缓冲池可以通过参数 innodb_buffer_pool_size来设置，缓存的数据类型有:索引页,数据页，自适应哈希索引，存储的锁信息，数据字典信息等；可以有多个缓冲池实例，每个页根据哈希值平均分配到不同的缓冲池中， 缓存池实例数可以通过参数innodb_buffer_pool_instance来配置，默认为一；

InnoDB的逻辑存储结构

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\PASOScreenShot20190115113216122.PNG)

正如之前提到的，MySQL 不会直接去修改磁盘的数据，因为这样做太慢了，MySQL 会先改内存(Buffer Pool)，然后记录 redo log，等有空了再刷磁盘，如果内存里没有数据，就去磁盘 load。

MySQL 是以「页」（page）为单位从磁盘读取数据的，Buffer Pool 里的数据也是如此，实际上，Buffer Pool 是`a linked list of pages`，一个以页为元素的链表。

为什么是链表？因为和缓存一样，它也需要一套淘汰算法来管理数据。

Buffer Pool 采用基于 LRU（least recently used） 的算法来管理内存：

![图片](https://mmbiz.qpic.cn/mmbiz_png/lA1CtgibZZmyiaicGXrxl6QZicE8Ms4ny8M2hYhdUAiaBnGljDySByIwrqCEJa7cEuHJtsQTx0B18mvcv16yF37H2yQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

##### Change Buffer

上面提到过，如果内存里没有对应「页」的数据，MySQL 就会去把数据从磁盘里 load 出来，如果每次需要的「页」都不同，或者不是相邻的「页」，那么每次 MySQL 都要去 load，这样就很慢了。

于是如果 MySQL 发现你要修改的页，不在内存里，就把你要对页的修改，先记到一个叫 Change Buffer 的地方，同时记录 redo log，然后再慢慢把数据 load 到内存，load 过来后，再把 Change Buffer 里记录的修改，应用到内存（Buffer Pool）中，这个动作叫做 **merge**；而把内存数据刷到磁盘的动作，叫 **purge**：

- **merge：Change Buffer -> Buffer Pool**
- **purge：Buffer Pool -> Disk**

上面是 MySQL 官网对 Change Buffer 的定义，仔细看的话，你会发现里面提到：Change Buffer 只在操作「二级索引」（secondary index）时才使用，原因是「聚簇索引」（clustered indexes）必须是「唯一」的，也就意味着每次插入、更新，都需要检查是否已经有相同的字段存在，也就没有必要使用 Change Buffer 了；另外，「聚簇索引」操作的随机性比较小，通常在相邻的「页」进行操作，比如使用了自增主键的「聚簇索引」，那么 insert 时就是递增、有序的，不像「二级索引」，访问非常随机。

> 如果想深入理解 Change Buffer 的原理，除了 MySQL 官网的介绍：Change Buffer，还可以阅读下《MySQL技术内幕》的「2.6.1 - 插入缓冲」章节，里面会从 Change Buffer 的前身 —— Insert Buffer 开始讲起，很透彻。

##### **Adaptive Hash Index**

MySQL 索引，不管是在磁盘里，还是被 load 到内存后，都是 B+ 树，B+ 树的查找次数取决于树的深度。你看，数据都已经放到内存了，还不能“一下子”就找到它，还要“几下子”，这空间牺牲的是不是不太值得？

尤其是那些频繁被访问的数据，每次过来都要走 B+ 树来查询，这时就会想到，我用一个指针把数据的位置记录下来不就好了？

这就是「自适应哈希索引」（Adaptive Hash Index）。自适应，顾名思义，MySQL 会自动评估使用自适应索引是否值得，如果观察到建立哈希索引可以提升速度，则建立。

##### Log Buffer

#### 2：数据的存储

innotDB存储引擎是基于自盘存储的，并将其中的记录按照页的方式进行管理.缓冲池简单，

在 InnoDB 存储引擎中，所有的数据都被**逻辑地**存放在表空间中，表空间（tablespace）是存储引擎中最高的存储逻辑单位，在表空间的下面又包括段（segment）、区（extent）、页（page）：同一个数据库实例的所有表空间都有相同的页大小；默认情况下，表空间中的页大小都为 16KB，当然也可以通过改变 `innodb_page_size` 选项对默认大小进行修改，需要注意的是不同的页大小最终也会导致区大小的不同：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-Tablespace-segment-extent-page-row.jpg)

从图中可以看出，在 InnoDB 存储引擎中，一个区的大小最小为 1MB，页的数量最少为 64 个。

#### 3：如何存储表

MySQL 使用 InnoDB 存储表时，会将**表的定义**和**数据索引**等信息分开存储，其中**表的定义**存储在 `.frm` 文件中，**数据索引**存储在 `.ibd` 文件中，这一节就会对这两种不同的文件分别进行介绍。

#### .frm 文件

无论在 MySQL 中选择了哪个存储引擎，所有的 MySQL 表都会在硬盘上创建一个 `.frm` 文件用来描述表的格式或者说定义；`.frm` 文件的格式在不同的平台上都是相同的。

```
CREATE TABLE test_frm(
    column1 CHAR(5),
    column2 INTEGER
);
```

当我们使用上面的代码创建表时，会在磁盘上的 `datadir` 文件夹中生成一个 `test_frm.frm` 的文件，这个文件中就包含了表结构相关的信息：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-frm-and-ibd-file.jpg)

> MySQL 官方文档中的 [11.1 MySQL .frm File Format](https://dev.mysql.com/doc/internals/en/frm-file-format.html) 一文对于 `.frm` 文件格式中的二进制的内容有着非常详细的表述，在这里就不展开介绍了。

#### .ibd 文件

InnoDB 中用于存储数据的文件总共有两个部分，一是系统表空间文件，包括 `ibdata1`、`ibdata2` 等文件，其中存储了 InnoDB 系统信息和用户数据库表数据和索引，是所有表公用的。当打开 `innodb_file_per_table` 选项时，`.ibd` 文件就是每一个表独有的表空间，文件存储了当前表的数据和相关的索引数据。

#### 4：如何存储记录

与现有的大多数存储引擎一样，InnoDB 使用页作为磁盘管理的最小单位；数据在 InnoDB 存储引擎中都是按行存储的，每个 16KB 大小的页中可以存放 2-200 行的记录。当 InnoDB 存储数据时，它可以使用不同的行格式进行存储；MySQL 5.7 版本支持以下格式的行存储方式：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-Antelope-Barracuda-Row-Format.jpg)

Antelope 是 InnoDB 最开始支持的文件格式，它包含两种行格式 Compact 和 Redundant，它最开始并没有名字；Antelope 的名字是在新的文件格式 Barracuda 出现后才起的，Barracuda 的出现引入了两种新的行格式 Compressed 和 Dynamic；InnoDB 对于文件格式都会向前兼容，而官方文档中也对之后会出现的新文件格式预先定义好了名字：Cheetah、Dragon、Elk 等等。

两种行记录格式 Compact 和 Redundant 在磁盘上按照以下方式存储：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-COMPACT-And-REDUNDANT-Row-Format.jpg)

Compact 和 Redundant 格式最大的不同就是记录格式的第一个部分；在 Compact 中，行记录的第一部分倒序存放了一行数据中列的长度（Length），而 Redundant 中存的是每一列的偏移量（Offset），从总体上上看，Compact 行记录格式相比 Redundant 格式能够减少 20% 的存储空间。

#### 5：行溢出数据

当 InnoDB 使用 Compact 或者 Redundant 格式存储极长的 VARCHAR 或者 BLOB 这类大对象时，我们并不会直接将所有的内容都存放在数据页节点中，而是将行数据中的前 768 个字节存储在数据页中，后面会通过偏移量指向溢出页。

但是当我们使用新的行记录格式 Compressed 或者 Dynamic 时都只会在行记录中保存 20 个字节的指针，实际的数据都会存放在溢出页面中。

#### 6：数据页结构

页是 InnoDB 存储引擎管理数据的最小磁盘单位，而 B-Tree 节点就是实际存放表中数据的页面，我们在这里将要介绍页是如何组织和存储记录的；首先，一个 InnoDB 页有以下七个部分：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-InnoDB-B-Tree-Node.jpg)

每一个页中包含了两对 header/trailer：内部的 Page Header/Page Directory 关心的是页的状态信息，而 Fil Header/Fil Trailer 关心的是记录页的头信息。

在页的头部和尾部之间就是用户记录和空闲空间了，每一个数据页中都包含 Infimum 和 Supremum 这两个**虚拟**的记录（可以理解为占位符），Infimum 记录是比该页中任何主键值都要小的值，Supremum 是该页中的最大值：

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-Infimum-Rows-Supremum.jpg)

User Records 就是整个页面中真正用于存放行记录的部分，而 Free Space 就是空余空间了，它是一个链表的数据结构，为了保证插入和删除的效率，整个页面并不会按照主键顺序对所有记录进行排序，它会自动从左侧向右寻找空白节点进行插入，行记录在物理存储上并不是按照顺序的，它们之间的顺序是由 `next_record` 这一指针控制的。

B+ 树在查找对应的记录时，并不会直接从树中找出对应的行记录，它只能获取记录所在的页，将整个页加载到内存中，再通过 Page Directory 中存储的稀疏索引和 `n_owned`、`next_record` 属性取出对应的记录，不过因为这一操作是在内存中进行的，所以通常会忽略这部分查找的耗时。

InnoDB 存储引擎中对数据的存储是一个非常复杂的话题，这一节中也只是对表、行记录以及页面的存储进行一定的分析和介绍，虽然作者相信这部分知识对于大部分开发者已经足够了，但是想要真正消化这部分内容还需要很多的努力和实践。

#### 7:锁的种类

对数据的操作其实只有两种，也就是读和写，而数据库在实现锁时，也会对这两种操作使用不同的锁；InnoDB 实现了标准的行级锁，也就是共享锁（Shared Lock）和互斥锁（Exclusive Lock）；共享锁和互斥锁的作用其实非常好理解：

- **共享锁（读锁）**：允许事务对一条行数据进行读取；
- **互斥锁（写锁）**：允许事务对一条行数据进行删除或更新；

而它们的名字也暗示着各自的另外一个特性，共享锁之间是兼容的，而互斥锁与其他任意锁都不兼容：

#### 8:锁的粒度

无论是共享锁还是互斥锁其实都只是对某一个数据行进行加锁，InnoDB 支持多种粒度的锁，也就是行锁和表锁；为了支持多粒度锁定，InnoDB 存储引擎引入了意向锁（Intention Lock），意向锁就是一种表级锁。

与上一节中提到的两种锁的种类相似的是，意向锁也分为两种：

- **意向共享锁**：事务想要在获得表中某些记录的共享锁，需要在表上先加意向共享锁；
- **意向互斥锁**：事务想要在获得表中某些记录的互斥锁，需要在表上先加意向互斥锁；

意向锁其实不会阻塞全表扫描之外的任何请求，它们的主要目的是为了表示**是否有人请求锁定表中的某一行数据**。

> 有的人可能会对意向锁的目的并不是完全的理解，我们在这里可以举一个例子：如果没有意向锁，当已经有人使用行锁对表中的某一行进行修改时，如果另外一个请求要对全表进行修改，那么就需要对所有的行是否被锁定进行扫描，在这种情况下，效率是非常低的；不过，在引入意向锁之后，当有人使用行锁对表中的某一行进行修改之前，会先为表添加意向互斥锁（IX），再为行记录添加互斥锁（X），在这时如果有人尝试对全表进行修改就不需要判断表中的每一行数据是否被加锁了，只需要通过等待意向互斥锁被释放就可以了。

#### 9:锁的算法

到目前为止已经对 InnoDB 中锁的粒度有一定的了解，也清楚了在对数据库进行读写时会获取不同的锁，在这一小节将介绍锁是如何添加到对应的数据行上的，我们会分别介绍三种锁的算法：Record Lock、Gap Lock 和 Next-Key Lock。

##### 1:Record Lock

记录锁（Record Lock）是加到**索引记录**上的锁，假设我们存在下面的一张表 `users`：

```
CREATE TABLE users(
    id INT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    age INT,
    PRIMARY KEY(id),
    KEY(last_name),
    KEY(age)
);
```

如果我们使用 `id` 或者 `last_name` 作为 SQL 中 `WHERE` 语句的过滤条件，那么 InnoDB 就可以通过索引建立的 B+ 树找到行记录并添加索引，但是如果使用 `first_name` 作为过滤条件时，由于 InnoDB 不知道待修改的记录具体存放的位置，也无法对将要修改哪条记录提前做出判断就会锁定整个表。

##### 2:Gap Lock

记录锁是在存储引擎中最为常见的锁，除了记录锁之外，InnoDB 中还存在间隙锁（Gap Lock），间隙锁是对索引记录中的一段连续区域的锁；当使用类似 `SELECT * FROM users WHERE id BETWEEN 10 AND 20 FOR UPDATE;` 的 SQL 语句时，就会阻止其他事务向表中插入 `id = 15` 的记录，因为整个范围都被间隙锁锁定了。

> 间隙锁是存储引擎对于性能和并发做出的权衡，并且只用于某些事务隔离级别。

虽然间隙锁中也分为共享锁和互斥锁，不过它们之间并不是互斥的，也就是不同的事务可以同时持有一段相同范围的共享锁和互斥锁，它唯一阻止的就是**其他事务向这个范围中添加新的记录**。

##### 3:Next-Key Lock

Next-Key 锁相比前两者就稍微有一些复杂，它是记录锁和记录前的间隙锁的结合，在 `users` 表中有以下记录：

```
+------|-------------|--------------|-------+
|   id | last_name   | first_name   |   age |
|------|-------------|--------------|-------|
|    4 | stark       | tony         |    21 |
|    1 | tom         | hiddleston   |    30 |
|    3 | morgan      | freeman      |    40 |
|    5 | jeff        | dean         |    50 |
|    2 | donald      | trump        |    80 |
+------|-------------|--------------|-------+
```

如果使用 Next-Key 锁，那么 Next-Key 锁就可以在需要的时候锁定以下的范围：

```
(-∞, 21]
(21, 30]
(30, 40]
(40, 50]
(50, 80]
(80, ∞)
```

> 既然叫 Next-Key 锁，锁定的应该是当前值和后面的范围，但是实际上却不是，Next-Key 锁锁定的是当前值和前面的范围。
>
> 在 InnoDB 存储引擎中，SELECT 操作的不可重复读问题通过 MVCC 得到了解决，而 UPDATE、DELETE 的不可重复读问题通过 Record Lock 解决，INSERT 的不可重复读问题是通过 Next-Key Lock（Record Lock + Gap Lock）解决的。

当我们更新一条记录，比如 `SELECT * FROM users WHERE age = 30 FOR UPDATE;`，InnoDB 不仅会在范围 `(21, 30]` 上加 Next-Key 锁，还会在这条记录后面的范围 `(30, 40]` 加间隙锁，所以插入 `(21, 40]` 范围内的记录都会被锁定。

#### 10:binlog、redo log和undo log  区别与联系

##### 1:binlog

###### binlog的主要使用场景

binlog用于记录数据库执行的写入性操作(不包括查询)信息，以二进制的形式保存在磁盘中。binlog是mysql的逻辑日志，并且由Server层进行记录，使用任何存储引擎的mysql数据库都会记录binlog日志。

- 逻辑日志：可以简单理解为记录的就是sql语句。
- 物理日志：因为mysql数据最终是保存在数据页中的，物理日志记录的就是数据页变更。

binlog是通过追加的方式进行写入的，可以通过max_binlog_size参数设置每个binlog文件的大小，当文件大小达到给定值之后，会生成新的文件来保存日志。

在实际应用中，binlog的主要使用场景有两个，分别是主从复制和数据恢复。

- 主从复制：在Master端开启binlog，然后将binlog发送到各个Slave端，Slave端重放binlog从而达到主从数据一致。
- 数据恢复：通过使用mysqlbinlog工具来恢复数据。

###### binlog刷盘时机

对于InnoDB存储引擎而言，只有在事务提交时才会记录biglog，此时记录还在内存中，那么biglog是什么时候刷到磁盘中的呢？mysql通过sync_binlog参数控制biglog的刷盘时机，取值范围是0-N：

- 0：不去强制要求，由系统自行判断何时写入磁盘；
- 1：每次commit的时候都要将binlog写入磁盘；
- N：每N个事务，才会将binlog写入磁盘。

从上面可以看出，sync_binlog最安全的是设置是1，这也是MySQL 5.7.7之后版本的默认值。但是设置一个大一些的值可以提升数据库性能，因此实际情况下也可以将值适当调大，牺牲一定的一致性来获取更好的性能

###### binlog日志有三种格式

分别为STATMENT、ROW和MIXED。

> 在 MySQL 5.7.7之前，默认的格式是STATEMENT，MySQL 5.7.7之后，默认值是ROW。日志格式通过binlog-format指定。

**STATMENT**

基于SQL语句的复制(statement-based replication, SBR)，每一条会修改数据的sql语句会记录到binlog中。

- 优点：不需要记录每一行的变化，减少了binlog日志量，节约了IO, 从而提高了性能；
- 缺点：在某些情况下会导致主从数据不一致，比如执行sysdate()、slepp()等。

**ROW**

基于行的复制(row-based replication, RBR)，不记录每条sql语句的上下文信息，仅需记录哪条数据被修改了。

- 优点：不会出现某些特定情况下的存储过程、或function、或trigger的调用和触发无法被正确复制的问题；
- 缺点：会产生大量的日志，尤其是alter table的时候会让日志暴涨

**MIXED**

基于STATMENT和ROW两种模式的混合复制(mixed-based replication, MBR)，一般的复制使用STATEMENT模式保存binlog，对于STATEMENT模式无法复制的操作使用ROW模式保存binlog

##### 2:redo log

###### 应用场景

我们都知道，事务的四大特性里面有一个是持久性，具体来说就是只要事务提交成功，那么对数据库做的修改就被永久保存下来了，不可能因为任何原因再回到原来的状态。那么mysql是如何保证一致性的呢？最简单的做法是在每次事务提交的时候，将该事务涉及修改的数据页全部刷新到磁盘中。但是这么做会有严重的性能问题，主要体现在两个方面：

- 因为Innodb是以页为单位进行磁盘交互的，而一个事务很可能只修改一个数据页里面的几个字节，这个时候将完整的数据页刷到磁盘的话，太浪费资源了！
- 一个事务可能涉及修改多个数据页，并且这些数据页在物理上并不连续，使用随机IO写入性能太差！

因此mysql设计了redo log，具体来说就是只记录事务对数据页做了哪些修改，这样就能完美地解决性能问题了(相对而言文件更小并且是顺序IO)。

redo log包括两部分：一个是内存中的日志缓冲(redo log buffer)，另一个是磁盘上的日志文件(redo log file)。mysql每执行一条DML语句，先将记录写入redo log buffer，后续某个时间点再一次性将多个操作记录写到redo log file。这种先写日志，再写磁盘的技术就是MySQL里经常说到的WAL(Write-Ahead Logging) 技术。

在计算机操作系统中，用户空间(user space)下的缓冲区数据一般情况下是无法直接写入磁盘的，中间必须经过操作系统内核空间(kernel space)缓冲区(OS Buffer)。因此，redo log buffer写入redo log file实际上是先写入OS Buffer，然后再通过系统调用fsync()将其刷到redo log file中，过程如下：

mysql支持三种将redo log buffer写入redo log file的时机，可以通过innodb_flush_log_at_trx_commit参数配置，各参数值含义如下：

![img](https://pic4.zhimg.com/80/v2-213622cb332c35e77eea6667a471d8ef_720w.jpg)



###### **redo log记录形式**

前面说过，redo log实际上记录数据页的变更，而这种变更记录是没必要全部保存，因此redo log实现上采用了大小固定，循环写入的方式，当写到结尾时，会回到开头循环写日志。

#### 11:MVCC

###    (三)InnoDB 磁盘架构

磁盘里有什么呢？除了表结构定义和索引，还有一些为了高性能和高可靠而设计的角色，比如 redo log、undo log、Change Buffer，以及 Doublewrite Buffer 等等.

> 有同学会问，那表的数据呢？其实只要理解了 InnoDB 里的所有表数据，都以索引（聚簇索引+二级索引）的形式存储起来，就知道索引已经包含了表数据。

#### **1、表空间（Tablespaces）**

从架构图可以看到，Tablespaces 分为五种：

- The System Tablespace
- File-Per-Table Tablespaces
- General Tablespace
- Undo Tablespaces
- Temporary Tablespaces

其中，我们平时创建的表的数据，可以存放到 The System Tablespace 、File-Per-Table Tablespaces、General Tablespace 三者中的任意一个地方，具体取决于你的配置和创建表时的 sql 语句。

> 这里同样不展开，如何选择不同的表空间存储数据？不同表空间各自的优势劣势等等，传送门：Tablespaces

#### **2、Doublewrite Buffer**

**如果说 Change Buffer 是提升性能，那么 Doublewrite Buffer 就是保证数据页的可靠性。**

怎么理解呢？

前面提到过，MySQL 以「页」为读取和写入单位，一个「页」里面有多行数据，写入数据时，MySQL 会先写内存中的页，然后再刷新到磁盘中的页。

这时问题来了，假设在某一次从内存刷新到磁盘的过程中，一个「页」刷了一半，突然操作系统或者 MySQL 进程奔溃了，这时候，内存里的页数据被清除了，而磁盘里的页数据，刷了一半，处于一个中间状态，不尴不尬，可以说是一个「不完整」，甚至是「坏掉的」的页。

有同学说，不是有 Redo Log 么？其实这个时候 Redo Log 也已经无力回天，Redo Log 是要在磁盘中的页数据是正常的、没有损坏的情况下，才能把磁盘里页数据 load 到内存，然后应用 Redo Log。而如果磁盘中的页数据已经损坏，是无法应用 Redo Log 的。

所以，MySQL 在刷数据到磁盘之前，要先把数据写到另外一个地方，也就是 Doublewrite Buffer，写完后，再开始写磁盘。Doublewrite Buffer 可以理解为是一个备份（recovery），万一真的发生 crash，就可以利用 Doublewrite Buffer 来修复磁盘里的数据。

> 留个问题，有了 Doublewrite Buffer 后，不就意味着 MySQL 要写两次磁盘？性能岂不是很差？

### (四):存储引擎对比

#### 1：Innodb与MyISAM的区别

##### 1）. 存储结构

MyISAM存储表分为三个文件frm（表结构）、MYD（表数据）、MYI（表索引），而Innodb如上文所说，根据存储方式不同，存储结构不同。

##### 2）. 事务支持

MyISAM不支持事务，表锁设计；支持全文索引，缓冲池只缓存索引文件，不缓存数据文件；

而Innodb支持事务，具有事务、回滚和恢复的事务安全。Innodb采用多版本并发控制（MVCC）来获得高并发性，实现了sql的标注4中隔离级别，同时使用一种next-keylocking的策略来避免幻读，

##### 3）. 外键和主键

MyISAM不支持外键，而Innodb支持外键。MyISAM允许没有主键，但是Innodb必须有主键，若未指定主键，会自动生成长度为6字节的主键。

##### 4）. 锁

MyISAM只支持表级锁，而Innodb支持行级锁，具有比较好的并发性能，但是行级锁只有在where子句是对主键筛选才生效，非主键where会锁全表

##### 5）. 索引

###### 索引的数据结构

在上一节中，我们谈了行记录的存储和页的存储，在这里我们就要从更高的层面看 InnoDB 中对于数据是如何存储的；InnoDB 存储引擎在绝大多数情况下使用 B+ 树建立索引，这是关系型数据库中查找最为常用和有效的索引，但是 B+ 树索引并不能找到一个给定键对应的具体值，它只能找到数据行对应的页，然后正如上一节所提到的，数据库把整个页读入到内存中，并在内存中查找具体的数据行。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-B+Tree.jpg)

MyISAM使用B+树作为索引结构，叶节点保存的是存储数据的地址，主键索引key值唯一，辅助索引key可以重复，二者在结构上相同。

Innodb也是用B+树作为索引结构，数据表本身就是按照b+树组织，叶节点key值为数据记录的主键，data域为完整的数据记录，辅助索引data域保存的是数据记录的主键。

B+ 树是平衡树，它查找任意节点所耗费的时间都是完全相同的，比较的次数就是 B+ 树的高度；在这里，我们并不会深入分析或者动手实现一个 B+ 树，只是对它的特性进行简单的介绍。

###### 聚集索引和辅助索引

数据库中的 B+ 树索引可以分为聚集索引（clustered index）和辅助索引（secondary index），它们之间的最大区别就是，聚集索引中存放着一条行记录的全部信息，而辅助索引中只包含索引列和一个用于查找对应行记录的。

### 聚集索引和辅助索引

数据库中的 B+ 树索引可以分为聚集索引（clustered index）和辅助索引（secondary index），它们之间的最大区别就是，聚集索引中存放着一条行记录的全部信息，而辅助索引中只包含索引列和一个用于查找对应行记录的『书签』。

#### 聚集索引

InnoDB 存储引擎中的表都是使用索引组织的，也就是按照键的顺序存放；聚集索引就是按照表中主键的顺序构建一颗 B+ 树，并在叶节点中存放表中的行记录数据。

```sql
CREATE TABLE users(
    id INT NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    PRIMARY KEY(id),
    KEY(last_name, first_name, age)
    KEY(first_name)
);
```

如果使用上面的 SQL 在数据库中创建一张表，B+ 树就会使用 `id` 作为索引的键，并在叶子节点中存储一条记录中的**所有**信息。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\mysql-Clustered-Index.jpg)

> 图中对 B+ 树的描述与真实情况下 B+ 树中的数据结构有一些差别，不过这里想要表达的主要意思是：聚集索引叶节点中保存的是整条行记录，而不是其中的一部分。

聚集索引与表的物理存储方式有着非常密切的关系，所有正常的表应该**有且仅有一个**聚集索引（绝大多数情况下都是主键），表中的所有行记录数据都是按照**聚集索引**的顺序存放的。

当我们使用聚集索引对表中的数据进行检索时，可以直接获得聚集索引所对应的整条行记录数据所在的页，不需要进行第二次操作。

#### 辅助索引

数据库将所有的非聚集索引都划分为辅助索引，但是这个概念对我们理解辅助索引并没有什么帮助；辅助索引也是通过 B+ 树实现的，但是它的叶节点并不包含行记录的全部数据，仅包含索引中的所有键和一个用于查找对应行记录的『书签』，在 InnoDB 中这个书签就是当前记录的主键。

辅助索引的存在并不会影响聚集索引，因为聚集索引构成的 B+ 树是数据实际存储的形式，而辅助索引只用于加速数据的查找，所以一张表上往往有多个辅助索引以此来提升数据库的性能。

> 一张表一定包含一个聚集索引构成的 B+ 树以及若干辅助索引的构成的 B+ 树。





#### 2:innodb关键特性

- ##### 插入缓冲：并不是缓冲池的一部分，他和数据也一样，是物理业的一部分

  innodb存储引擎对于非聚集索引的插入或更新操作，不是每一次直接插入到索引页中，而是先判断插入的非聚集索引页是否在缓冲池，若在，则直接插入；不在，则先放在一个insert buffer对象中。数据库这个非聚集的索引已经插到叶子节点，而实际并没有，知识存放在另一个位置。然后再以一定的频率和情况进行insert buffer和辅助索引页子节点的merge操作，这样就把多次插入操作合并为一个。

  insert buffer使用条件：

  - 索引为辅助索引

  - 索引不是唯一的

    使用命令show engine innodb status查看插入缓冲信息

    ```mysql
    mysql>show engine innodb status\G;
    ......
    -------------------------------------
    INSERT BUFFER AND ADAPTIVE HASH INDEX
    -------------------------------------
    Ibuf: size 1, free list len 33, seg size 35, 2118 merges
    merged operations:
     insert 2296, delete mark 0, delete 0
    discarded operations:
     insert 0, delete mark 0, delete 0
    ......
    ```

    seg size显示当前insert buffer的大小为35*16KB；
    free list len表示空闲列表的长度；
    size代表已经合并记录页的数量
    merges合并次数

    merged operations和discarded operations显示change buffer中每个操作的次数

    merged operations合并操作
    merged operations insert插入的记录数
    merged operations delete mark删除的记录数
    merged operations delete清除记录数

    discarded operations表示发生merge操作时，表已删除。
    discarded operations insert表示取消的合并操作数
    。。。

    **change buffer**
    innodb从1.0.x开始引入change buffer。innodb可以对DML操作都进行缓冲。
    通过参数innodb_change_buffering开启各种buffer选项。可选值：inserts、deletes、purges、changes、all、none。changes表示启用inserts和deletes，all表示启用全部，none表示都不启用。默认all。

    ```mysql
    mysql> show variables like 'innodb_change_buffering';
    +-------------------------+-------+
    | Variable_name           | Value |
    +-------------------------+-------+
    | innodb_change_buffering | all   |
    +-------------------------+-------+
    ```

    通过参数innodb_change_buffer_max_size控制change buffer最大内存使用数量。默认25，表示最多使用1/4的缓冲池内存空间。该参数最大有效值50.

    ```mysql
    mysql> show variables like 'innodb_change_buffer_max_size';
    +-------------------------------+-------+
    | Variable_name                 | Value |
    +-------------------------------+-------+
    | innodb_change_buffer_max_size | 25    |
    +-------------------------------+-------+
    ```

- ##### 两次写

如果说插入缓冲带给InnoDB存储引擎的是性能，两次写带来的则是数据的可靠性。当数据库宕机时，如果此时正在写一个页面，而这个页面只写了一部分（比如16K的页面只写了6K），此时就会发生部分写失效（partial page write）进而导致数据丢失。
也许你会想，不慌，我们有redo log，保证数据可靠性。想法很好，但是必须清楚的是，redo log记录的是对页的物理操作，如果这个页本身已损坏，redo log日志中记录的数据必然也是损坏的，此时再去重做已然没有意义。

> **所以聪明的开发者设计了一种页的副本，当写入失效发生时，先通过页的副本来还原该页，再通过`redo log`进行重做，这就是`double write`。**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200819201729536.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NhcnNvbl9DaHU=,size_16,color_FFFFFF,t_70#pic_center)
如上图所示，InnoDB会将脏页拷贝的double buffer，之后double buffer再分两次，每次写入1MB到共享表空间的物理磁盘上（此时是顺序写），然后再将double buffer中的页写入到各个表空间的数据文件中（此时是离散写），这就是两次写的过程。
那么在磁盘写入的过程崩溃了怎么办呢？可以看到，共享表空间有该页的一个副本，将其拷贝到表空间文件，再通过redo log即可恢复数据。

- ##### 自适应哈希

InnoDB存储引擎会监控表上索引的查找，如果观察到建立哈希索引可以带来速度的提升则建立哈希索引，所以称之为自适应的。自适应哈希索引通过缓冲池的B+树构造，速度很快。而且不需要将整张表都建哈希索引，只是某些页建立。由于innodb不支持hash索引，但是在某些情况下hash索引的效率很高，于是出现了 adaptive hash index功能，innodb存储引擎会监控对表上索引的查找，如果观察到建立hash索引可以提高性能的时候，则自动建立hash索引。可以通过 show engine innodb status\G来查看自适应哈西索引的使用情况。可以使用innodb_adaptive_hash_index来禁用和启用hash索引，默认开启。

- ##### 异步IO

为了提高磁盘操作性能，当前的数据库系统都采用异步IO的方式来处理磁盘操作。InnoDB也是如此。

与AIO对应的是Sync IO，即每进行一次IO操作，需要等待此次操作结束才能继续接下来的操作。但是如果用户发出的是一条索引扫描的查询，那么这条SQL语句可能需要扫描多个索引页，也就是需要进行多次IO操作。在每扫描一个页并等待其完成再进行下一次扫描，这是没有必要的。用户可以在发出一个IO请求后立即再发出另外一个IO请求，当全部IO请求发送完毕后，等待所有IO操作完成，这就是AIO。

AIO的另外一个优势是进行IO Merge操作，也就是将多个IO合并为一个IO操作，这样可以提高IOPS的性能。

在InnoDB 1.1.x之前，AIO的实现是通过InnoDB存储引擎中的代码来模拟的。但是从这之后，提供了内核级别的AIO的支持，称为Native AIO。Native AIO需要操作系统提供支持。Windows和Linux都支持，而Mac则未提供。在选择MySQL数据库服务器的操作系统时，需要考虑这方面的因素。

MySQL可以通过参数innodb_use_native_aio来决定是否启用Native AIO。在InnoDB存储引擎中，read ahead方式的读取都是通过AIO完成，脏页的刷新，也是通过AIO完成。

- ##### 刷新邻接页

  InnoDB存储引擎在刷新一个脏页时，会检测该页所在区(extent)的所有页，如果是脏页，那么一起刷新。这样做的好处是通过AIO可以将多个IO写操作合并为一个IO操作。该工作机制在传统机械磁盘下有显著优势。但是需要考虑下吧两个问题:

  是不是将不怎么脏的页进行写入，而该页之后又会很快变成脏页？
   固态硬盘有很高IOPS，是否还需要这个特性？
   为此InnoDB存储引擎1.2.x版本开始提供参数innodb_flush_neighbors来决定是否启用。对于传统机械硬盘建议使用，而对于固态硬盘可以关闭。

  

#### 3:sql查询优化

##### 1):sql优化

select a.* from A where a.id in (select b.id  from B where )

select a.* from A where exists (select b.id from B wehre b.id = a.id)

1. exists  比 in 效率高(第一种方式是全程扫描B表用到A表的索引，第二种方式是全程扫描A表 用到b表上的索引，B表小)

2. not exists 在任何情况下都比not in效率高 ，因为内外表没用到索引，not exists用到索引

3. 不要select * 查询 全部的列

4. 使用 包含任何用户自定义的函数，存储函数，用户变量，临时表，mysql中的系统表，或者任何包含列级别的表，都不会被缓存

   检查缓存是在解析sql语句之前，然而在检查缓存之前mysql就是检查sql 是不是以sel 开头。即如果查询语句中包含任何的不确定函数，那么查询缓存中是不可能找到缓存结果的，即使执行了也不会被放入缓存。所以比如带日期的查询不要直接使用函数，而应该提前计算好日期。

   配置查询缓存（具体见mysql 高性能书籍第三版7.12.4）：

   query_catch_type:是否打开查询缓存

   query_catch_size:查询缓存使用的总内存空间

   

##### 2):索引优化：

索引是储存在引擎层，而不是服务层实现的，所以并没有统一的索引的标准；不同村到户引擎的索引的工作方式并不一样，也不是多有的存储去引擎都支持所有类型的索引。

###### I:索引类型

普通**索引**：仅加速查询

唯一**索引**：加速查询 + 列值唯一（可以有null）

主键**索引**：加速查询 + 列值唯一（不可以有null）+ 表中只有一个

组合**索引**：多列值组成一个**索引**，专门用于组合搜索，其效率大于**索引**合并

全文**索引**：对文本的内容进行分词，进行搜索

ps.**索引**合并，使用多个单列**索引**组合搜索

*覆盖\**索引**，select的数据列只用从**索引**中就能够取得，不必读取数据行，换句话说查询列要被所建的**索引**覆盖*

###### II:explain 列的解释：

table：显示这一行的数据是关于哪张表的；
type：这是重要的列，显示连接使用了何种类型。从最好到最差的连接类型为 const、eq_reg、ref、range、indexhe 和 ALL；
possible_keys：显示可能应用在这张表中的索引。如果为空，没有可能的索引。可以为相关的域从 WHERE 语句中选择一个合适的语句；
key： 实际使用的索引。如果为NULL，则没有使用索引。很少的情况下，MYSQL会选择优化不足的索引。这种情况下，可以在SELECT语句中使用USE INDEX（indexname）来强制使用一个索引或者用IGNORE INDEX（indexname）来强制MYSQL忽略索引；
key_len：使用的索引的长度。在不损失精确性的情况下，长度越短越好；
ref：显示索引的哪一列被使用了，如果可能的话，是一个常数；
rows：MYSQL认为必须检查的用来返回请求数据的行数；
Extra：关于MYSQL如何解析查询的额外信息。

###### III:索引优化建议：

1：尽量不要用长度很长的列作为索引列，节约索引的空间；

2：联合索引：

​	1).最左前缀匹配原则，非常重要的原则，mysql会一直向右匹配直到遇到范围查询(>、<、between、like)就停止匹配，比如a = 1 and b = 2 and c > 3 and d = 4 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。

​	2).=和in可以乱序，比如a = 1 and b = 2 and c = 3 建立(a,b,c)索引可以任意顺序，mysql的查询优化器会帮你优化成索引可以识别的形式

​	3).尽量选择区分度高的列作为索引,区分度的公式是count(distinct col)/count(*)，表示字段不重复的比例，比例越大我们扫描的记录数越少，唯一键的区分度是	     		1，而一些状态、性别字段可能在大数据面前区分度就是0，那可能有人会问，这个比例有什么经验值吗？使用场景不同，这个值也很难确定，一般需要join的		字段我们都要求是0.1以上，即平均1条扫描10条记录

​	4).索引列不能参与计算，保持列“干净”，比如from_unixtime(create_time) = ’2014-05-29’就不能使用到索引，原因很简单，b+树中存的都是数据表中的字段值，但进行检索时，需要把所有元素都应用函数才能比较，显然成本太大。所以语句应该写成create_time = unix_timestamp(’2014-05-29’);

​	5).尽量的扩展索引，不要新建索引。比如表中已经有a的索引，现在要加(a,b)的索引，那么只需要修改原来的索引即可

​	6)：单列索引和联合索引

​		a:如果建立的是多个单列索引，当你用多个条件同时where的话，只有第一个会用上索引，其他两个会用不到索引，但是如果是用or的话就都会有用；

​		b:如果建立的是联合索引，当你用多个条件同时where的话，只有条件当中有联合索引中的第一个列，索引才会有效（即使调换了位置索引也有效），用or的			话就索引也会无效；

###### IV:使用索引的注意事项

使用**索引**时，有以下一些技巧和注意事项：

###### 	1).**索引**不会包含有NULL值的列

只要列中包含有NULL值都将不会被包含在**索引**中，复合**索引**中只要有一列含有NULL值，那么这一列对于此复合**索引**就是无效的。所以我们在数据库设计时不要让字段的默认值为NULL。

###### 	2).使用短**索引**

对串列进行**索引**，如果可能应该指定一个前缀长度。例如，如果有一个CHAR(255)的列，如果在前10个或20个字符内，多数值是惟一的，那么就不要对整个列进行**索引**。短**索引**不仅可以提高查询速度而且可以节省磁盘空间和I/O操作。

###### 	3).**索引**列排序

MySQL查询只使用一个**索引**，因此如果where子句中已经使用了**索引**的话，那么order by中的列是不会使用**索引**的。因此数据库默认排序可以符合要求的情况下不要使用排序操作；尽量不要包含多个列的排序，如果需要最好给这些列创建复合**索引**。

###### 	4).like语句操作

一般情况下不鼓励使用like操作，如果非使用不可，如何使用也是一个问题。like “%aaa%” 不会使用**索引**而like “aaa%”可以使用**索引**。

###### 	5).不要在列上进行运算

复制代码 代码如下:

select * from users where YEAR(adddate)<2007;

将在每个行上进行运算，这将导致**索引**失效而进行全表扫描，因此我们可以改成:

复制代码 代码如下:

select * from users where adddate<‘2007-01-01';

###### 	6).不使用NOT IN和<>操作

##### V:查询的执行的基础

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\sql查询执行路径.jpg)

1. 查询缓存
2. 服务器进行SQL解析、预处理，再由优化去生成对应的执行计划。
3. 调用存储引擎的的API来执行查询。

#### III:并发问题

并发值 = CPU数量* 磁盘数量 * 2

配置复制：

## 配置主库Master

### 修改MySQL配置

找到主数据库的配置文件my.cnf，我的在`/etc/my.cnf`，在[mysqld]部分插入

```
[mysqld]
log-bin=mysql-bin #开启二进制日志
server-id=1 #设置server-id，必须唯一
复制代码
```

**配置说明 **

> 1. log-bin：设置二进制日志文件的基本名；
> 2. log-bin-index：设置二进制日志索引文件名；
> 3. binlog_format：控制二进制日志格式，进而控制了复制类型，三个可选值
> 4. ​    -STATEMENT：语句复制
> 5. ​    -ROW：行复制
> 6. ​    -MIXED：混和复制，默认选项
> 7. server-id：服务器设置唯一ID，默认为1，推荐取IP最后部分；
> 8. sync-binlog：默认为0，为保证不会丢失数据，需设置为1，用于强制每次提交事务时，同步二进制日志到磁盘上。

## 配置从库Slave

### 修改MySQL配置

找到主数据库的配置文件my.cnf，我的在`/etc/my.cnf`，在[mysqld]部分插入

```
[mysqld]
server-id=2 #设置server-id，必须唯一
```

# springBoot整合sharding-jdbc







# 十二： hadoop安装&配置（参考https://github.com/heibaiying/BigData-Notes）

## 1:安装

## 2：配置3个虚机免秘钥

### 2.1 生成密匙

在每台主机上使用 `ssh-keygen` 命令生成公钥私钥对：

```
ssh-keygen
```

### 2.2 免密登录

将 `hadoop001` 的公钥写到本机和远程机器的 `~/ .ssh/authorized_key` 文件中：

```
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop001
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop002
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop003
```

### 2.3 验证免密登录

```
ssh hadoop002
ssh hadoop003
```

## 3、集群搭建（非高可用，因为没有引入zookeeper）

### 3.1 下载并解压

下载 Hadoop。这里我下载的是 CDH 版本 Hadoop，下载地址为：http://archive.cloudera.com/cdh5/cdh/5/

```shell
# tar -zvxf hadoop-2.6.0-cdh5.15.2.tar.gz 
```

### 3.2 配置环境变量

编辑 `profile` 文件：

```shell
# vim /etc/profile
```

增加如下配置：

```shell
export HADOOP_HOME=/usr/app/hadoop-2.6.0-cdh5.15.2
export  PATH=${HADOOP_HOME}/bin:$PATH
```

执行 `source` 命令，使得配置立即生效：

```shell
# source /etc/profile
```

### 3.3 修改配置

进入 `${HADOOP_HOME}/etc/hadoop` 目录下，修改配置文件。各个配置文件内容如下：

#### 1. hadoop-env.sh

```shell
# 指定JDK的安装位置
export JAVA_HOME=/usr/java/jdk1.8.0_201/
```

#### 2. core-site.xml

```xml
<configuration>
    <property>
        <!--指定 namenode 的 hdfs 协议文件系统的通信地址,端口自己制定-->
        <name>fs.defaultFS</name>
        <value>hdfs://k8s-master:9000</value>
    </property>
    <property>
        <!--指定 hadoop 集群存储临时文件的目录-->
        <name>hadoop.tmp.dir</name>
        <value>/home/hadoop/tmp</value>
    </property>
</configuration>
```

#### 3. hdfs-site.xml

```xml
<configuration>
<property>
      <!--namenode 节点数据（即元数据）的存放位置，可以指定多个目录实现容错，多个目录用逗号分隔-->
    <name>dfs.namenode.name.dir</name>
    <value>/home/hadoop/namenode/data</value>
</property>
<property>
      <!--datanode 节点数据（即数据块）的存放位置-->
    <name>dfs.datanode.data.dir</name>
    <value>/home/hadoop/datanode/data</value>
</property>
     <!--这个配置解决了我不能不访问hadoop ui的问题
netstat -ant 查看监听的端口
-->
<property>
  <name>dfs.http.address</name>
  <value>k8s-master:50070</value>
</property>
</configuration>
```

#### 4. yarn-site.xml

```xml
<configuration>
    <property>
        <!--配置 NodeManager 上运行的附属服务。需要配置成 mapreduce_shuffle 后才可以在 Yarn 上运行 MapReduce 程序。-->
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <!--resourcemanager 的主机名-->
        <name>yarn.resourcemanager.hostname</name>
        <value>k8s-master</value>
    </property>
</configuration>
```

#### 5. mapred-site.xml

```xml
<configuration>
    <property>
        <!--指定 mapreduce 作业运行在 yarn 上-->
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

#### 5. slaves

配置所有从属节点的主机名或 IP 地址，每行一个。所有从属节点上的 `DataNode` 服务和 `NodeManager` 服务都会被启动。

```
k8s-master
k8s-node1
k8s-node2
```

### 3.4 分发程序

将 Hadoop 安装包分发到其他两台服务器，分发后建议在这两台服务器上也配置一下 Hadoop 的环境变量。

```shell
# 将安装包分发到k8s-node1
scp -r /usr/local/src/hadoop/  k8s-node1:/usr/local/src/hadoop/
# 将安装包分发到k8s-node2
scp -r /usr/local/src/hadoop/  k8s-node2:/usr/local/src/hadoop/
```

### 3.5 初始化

在 k8s-master上执行 namenode 初始化命令：

```shell
hdfs namenode  -format
hadoop namenode -format
```

### 3.6 启动集群

进入到 k8s-master的 `${HADOOP_HOME}/sbin` 目录下，启动 Hadoop。此时 k8s-node1和 k8s-node2上的相关服务也会被启动：

```shell
# 启动dfs服务
start-dfs.sh
# 启动yarn服务
start-yarn.sh
```

### 3:检验

2. ```
   
   ```

* sbin>jps
      4944 DataNode
      5860 NodeManager
      3532 Jps
      7852 NameNode
      7932 ResourceManager  

```
   
   1. datanode节点的机器也会同时启动完成
   
3. 监听端口

- 与外部程序交互端口
  <http://192.168.1.105:9000/>
  
- web 页面访问端口
  http://192.168.1.105:50070/dfshealth.html#tab-overview
  
- 看hdfs文件
  
  http://192.168.1.105:8088/cluster.nodes`mapreduce任务`

1. 重启

```

sbin/stop-all.sh  
sbin/start-all.sh

```
## 4:hadoop shell

#### put

使用方法：hadoop fs -put … 
\- 从本地文件系统中复制单个或多个源路径到目标文件系统。也支持从标准输入中读取输入写入目标文件系统。

```

    * hadoop fs -put localfile /user/hadoop/hadoopfile  
    * hadoop fs -put localfile1 localfile2  
     /user/hadoop/hadoopdir  
    * hadoop fs -put localfile  
     hdfs://host:port/hadoop/hadoopfile
    * hadoop fs -put - hdfs://host:port/hadoop/hadoopfile  

```
从标准输入中读取输入。

返回值：
成功返回0，失败返回-1。(我put的时候没有返回值?)

put 的hadoop 的位置的路径下没有其他文件或目录时,直接替换这个路径,而不是在这个路径下新建文集

#### mkdir

使用方法：hadoop fs -mkdir 
\- 接受路径指定的uri作为参数，创建这些目录。其行为类似于Unix mkdir -p，它会创建路径中的各级父目录。
示例：

```

    * hadoop fs -mkdir /user/hadoop/dir1 /user/hadoop/dir2
    * hadoop fs -mkdir hdfs://host1:port1/user/hadoop/dir hdfs://host2:port2/user/hadoop/dir

```
返回值：
成功返回0，失败返回-1。

mkdir多路径的时候(/user/hadoop/dir1) 如果没有父路径则不会新建子目录,会报错

## 5:mapreduce demo运行测试

运行hadoop官网给的demo (失败)

- <http://hadoop.apache.org/docs/r1.0.4/cn/mapred_tutorial.html>

- 运行应用程序：(失败)

```

  > $ bin/hadoop jar /usr/joe/wordcount.jar org.myorg.WordCount /usr/joe/wordcount/input /usr/joe/wordcount/output

  ```
  
  ```

应该为:
`> hadoop jar D:\java\hadoop-2.8.1\sbin\hh\hadoop-test-1.0.0.jar hadoop.WordCount /usr/joe/wordcount/input /usr/joe/wordcount/output`
原因:不用加jar包名称

- 运行失败

  原因:

  ```
  8/01/25 18:41:16 INFO mapreduce.Job: Job job_1516876525498_0002 failed with state FAILED due to: Application application_1516876525498_0002 failed 2 times due to AM Container for appattempt_1516876525498_0002_000
  002 exited with  exitCode: 5
  Failing this attempt.Diagnostics: Exception from container-launch.
  Container id: container_1516876525498_0002_02_000001
  Exit code: 5
  Exception message: AssignProcessToJobObject error (5): ?????
  ```

#### 运行成功 通过idea运行(`成功`) demo



# 十三:HBase

- 参考
  <http://www.ityouknow.com/hbase/2017/07/25/hbase-cluster-setup.html>

1. 下载匹配hadoop版本的hbase
   hadoop-2.7.7 <==> hbase-2.0.x
2. 我没有配置环境变量
   - slave节点出现的问题
     因为没有加hbase目录的环境变量,当主节点启动时,从节点没有启动成功,因为
     全局执行hbase命令的时候,没有找到hbase目录
3. 配置文件

- hbase 相关的配置主要包括hbase-env.sh、hbase-site.xml、regionservers三个文件
- 都在/conf下

1. 具体配置

- 配置hbase-env.sh

  ```shell
  export JAVA_HOME=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64
  export HBASE_HOME=/usr/local/hbase
  export HBASE_CLASSPATH=/usr/local/hbase/conf
  # 此配置信息，设置由hbase自己管理zookeeper，不需要单独的zookeeper。
  export HBASE_MANAGES_ZK=true
  export HBASE_HOME=/usr/local/hbase
  export HADOOP_HOME=/usr/local/hadoop
  #Hbase日志目录
  export HBASE_LOG_DIR=/usr/local/hbase/logs
  ```

- 配置 hbase-site.xml

  ```xml
  <configuration>
      <property>
          <name>hbase.rootdir</name>
          <value>hdfs://10.59.74.192:9000/hbase</value>
      </property>
      <property>
          <name>hbase.cluster.distributed</name>
          <value>true</value>
      </property>
      <property>
          <name>hbase.master</name>
          <value>10.59.74.192:60000</value>
      </property>
      <property>
          <name>hbase.zookeeper.quorum</name>
          <value>10.59.74.192,10.59.76.103,10.59.76.104</value>
      </property>
  </configuration>
  ```

- 配置regionservers

  ```
  10.59.74.192
  10.59.76.103
  10.59.76.104
  ```

1. zookeeper配置

- HQuorumPeer是由HBase控制和启动的，这里的zookeeper是HBASE自带的，一般适用于测试 环境
- 生成环境建议用外部独立的ZK集群。
- 自带和外部独立的zk进程区别：
  - HQuorumPeer :自带
  - QuorumPeer :独立

# 十四:Hive

# 十五：spark

## 1：下载

## 2:配置修改

```shell
cp spark-env.sh.template   spark-env.sh
cp spark-default.conf.template   spark-default.conf
cp slaves.template   slaves
```

修改配置

### 2.1spark-env.sh

```shell
#指定master名称
export SPARK_MASTER_HOST=k8s-master
#指定端口
export SPARK_MASTER_PORT=7077
#指定历史服务参数，注意要和hadoop的端口相匹配，我的是9000
export SPARK_HISTORY_OPTS="-Dspark.history.ui.port=4000 -Dspark.history.ui.retainedApplications=3 -Dspark.history.fs.logDirectory=hdfs://k8s-master:9000/spark_log"
#指定不需要每次在hadoop读取数据输入路径都要加入HDFS的地址，指定hadoop的地址
export HADOOP_CONF_DIR=/usr/local/src/hadoop/hadoop-2.6.0/etc/hadoop/

```

### 2.2slaves

```shell
k8s-master
k8s-node1
k8s-node2
```

### 2.3spark-default.conf

```shell
 spark.eventLog.enabled           true
 spark.eventLog.dir               hdfs://k8s-master:9000/spark_log#注意和hadoop的端口相对应
 spark.eventLot.compress          true
```

## 3：启动：

### Local模式

Local 模式是最简单的一种运行方式，它采用单节点多线程方式运行，不用部署，开箱即用，适合日常测试开发。

```
# 启动spark-shell
spark-shell --master local[2]
```

- **local**：只启动一个工作线程；
- **local[k]**：启动 k 个工作线程；
- **local[\*]**：启动跟 cpu 数目相同的工作线程数。

进入 spark-shell 后，程序已经自动创建好了上下文 `SparkContext`，等效于执行了下面的 Scala 代码：

1:读取本地文件（在本地建立一个txt文件）

```txt
hadoop,spark,hadoop
spark,flink,flink,spark
hadoop,hadoop
```

```shell
#创建目录并上传文件到hdfs 上
hdfs dfs -mkdir -p /data
hdfs dfs -put spark_data.txt /data
```

```scala
val conf = new SparkConf().setAppName("Spark shell").setMaster("local[2]")
val sc = new SparkContext(conf)
val rdd1 = spark.sparkContext.textFile("file:///usr/app/wc.txt")//读取本地文件
val rdd1 = sc.textFile("hdfs://k8s-master:9000/data/spark_data.txt") //读取远程hdfs文件
val rdd1 = sc.textFile("hdfs:///data/spark_data.txt")//配置默认hadoop路径后或者下面一行也可以
val rdd1 = sc.textFile("/data/spark_data.txt")//配置默认hadoop路径后，默认读取hdfs上的文件
var rdd2 = rdd1.flatMap(item => item.split(" "))
 var rdd3 = rdd2.map(item => (item,1))
var rdd4 = rdd3.reduceByKey((curr,agg)=> curr + agg)
rdd4.collect()
//或者  val wordCounts = rdd1.flatMap(line => line.split(",")).map((word => (word, 1))).reduceByKey(_ + _)
```

## 4：Spark架构原理

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\spark架构图.jpg)

- **Cluster Manager(Master)**：在standalone模式中即为Master主节点，控制整个集群，监控worker。在YARN模式中为资源管理器
- **Worker节点**：从节点，负责控制计算节点，启动Executor或者Driver。
- **Driver**： 运行Application 的main()函数
- **Executor**：执行器，是为某个Application运行在worker node上的一个进程

## Spark基本工作原理

#### 1. 迭代式计算：

Spark与MapReduce最大的不用在于其迭代式计算模型：

1. MapReduce，分为两个阶段，map和reduce，两个阶段完成，就结束了。所以我们在一个job里能做的处理很有限，只能在map和reduce里处理数据。

2. Spark，迭代式计算模型，可以分为n个阶段，因为它是内存迭代式的。我们在处理完一个阶段以后，可以继续往下处理很多阶段，而不是只有两个阶段。所以，Spark相较于MapReduce来说，计算模型可以提供更强大的功能。

   

   ![img](https:////upload-images.jianshu.io/upload_images/1900685-41f8d8aef5e0a53d.png?imageMogr2/auto-orient/strip|imageView2/2/w/794/format/webp)

   image.png

#### 2. 分布式

整个Spark有个提交程序的客户端，提交给集群，集群中有很多台机器，作业运行在分布式节点上的，Spark程序提交到集群运行，节点一般处理一部分数据，分布式作并行化。

#### 3. 主要基于内存（少数情况基于磁盘）

RDD的每个partition，在spark节点上存储时，默认都是放在内存中的。但是如果说内存放不了这么多数据时，比如每个节点最多放5万数据，结果每个partition是10万数据。那么就会把partition中的部分数据写入磁盘中，进行保存。而这一切，对于用户来说，都是完全透明的。也就是说，不用管去管RDD的数据存储在哪，内存，还是磁盘。只要关注的是RDD的计算，处理等操作。所以说，RDD的这种自动进行内存和磁盘之间权衡和切换的机制，就是RDD的弹性特点所在。

## 一个spark程序的执行流程

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\spark程序执行流程.jpg)

- **A ->** 当 Driver 进程被启动之后,首先它将发送请求到Master节点上,进行Spark应用程序的注册
- **B ->** Master在接受到Spark应用程序的注册申请之后,会发送给Worker,让其进行资源的调度和分配.
- **C ->** Worker 在接受Master的请求之后,会为Spark应用程序启动Executor, 来分配资源
- **D ->** Executor启动分配资源好后,就会想Driver进行反注册,这是Driver已经知道哪些Executor为他服务了
- **E ->** 当Driver得到注册了Executor之后,就可以开始正式执行spark应用程序了. 首先第一步,就是创建初始RDD,读取数据源,再执行之后的一系列算子. HDFS文件内容被读取到多个worker节点上,形成内存中的分布式数据集,也就是初始RDD
- **F ->** Driver就会根据 Job 任务任务中的算子形成对应的task,最后提交给 Executor, 来分配给task进行计算的线程
- **G ->** task就会去调用对应的任务数据来计算,并task会对调用过来的RDD的partition数据执行指定的算子操作,形成新的RDD的partition,这时一个大的循环就结束了
- 后续的RDD的partition数据又通过Driver形成新的一批task提交给Executor执行,循环这个操作,直到所有的算子结束

## 5:Spark程序运行模式

Spark可以非常方便地与其他的开源产品进行融合。比如，Spark可以使用Hadoop的YARN和Apache Mesos作为它的资源管理和调度器，并且可以处理所有Hadoop支持的数据，包括HDFS、HBase和Cassandra等。这对于已经部署Hadoop集群的用户特别重要，因为不需要做任何数据迁移就可以使用Spark的强大处理能力。Spark也可以不依赖于第三方的资源管理和调度器，它实现了Standalone作为其内置的资源管理和调度框架，这样进一步降低了Spark的使用门槛，使得所有人都可以非常容易地部署和使用Spark。此外，Spark还提供了在EC2上部署Standalone的Spark集群的工具。

[Mesos](https://mesos.apache.org/)：Spark可以运行在Mesos里面（Mesos 类似于yarn的一个资源调度框架）

[standalone](http://spark.apache.org/docs/latest/spark-standalone.html)：Spark自己可以给自己分配资源（master，worker）

[YARN](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html)：Spark可以运行在yarn上面

[Kubernetes](https://kubernetes.io/)：Spark接收 [Kubernetes](https://kubernetes.io/)的资源调度

## 6:RDD及其特点

1、RDD是Spark最基本的数据抽象，全称为Resillient Distributed Dataset，即弹性分布式数据集。

2、RDD在抽象上来说是一种元素集合，包含了数据。它是被分区的，分为多个分区，每个分区分布在集群中的不同节点上，从而让RDD中的数据可以被并行操作。（分布式）

3、RDD通常通过Hadoop上的文件，即HDFS文件或者Hive表，来进行创建；有时也可以通过应用程序中的集合来创建。

4、RDD最重要的特性就是，提供了容错性，可以自动从节点失败中恢复过来。即如果某个节点上的RDD partition，因为节点故障，导致数据丢了，那么RDD会自动通过自己的数据来源重新计算该partition。这一切对使用者是透明的。

5、RDD的数据默认情况下存放在内存中的，但是在内存资源不足时，Spark会**自动**将RDD数据写入磁盘。（弹性）

6、RDD允许用户在执行多个查询时显式地将工作集缓存在内存中，后续的查询能够重用工作集，这极大地提升了Spark程序性能。

## 创建RDD

创建RDDs有两种方法:并行化程序中的现有集合，或者引用外部存储系统中的数据集，比如共享文件系统、HDFS、HBase或任何提供Hadoop InputFormat的数据源。

### Parallelized Collections

```java
`List<Integer> data = Arrays.asList(1, 2, 3, 4, 5); JavaRDD<Integer> distData = sc.parallelize(data);`
```

### External Datasets

```java
`JavaRDD<String> distFile = sc.textFile("hdfs://10.59.74.192:9000/data1/kettle/dianping/dianping_shop/dianping_shop.txt");`
```



## RDD基本操作

SparkHelloWorldDemo.java

```java
// 配置Spark，不设置master参数则默认为集群环境运行，local为本地
SparkConf conf = new SparkConf().setMaster("local").setAppName("Hello Spark !");
// 创建上下文，也就是Spark程序入口 
JavaSparkContext sc = new JavaSparkContext(conf);
// 读取本地文本文件，结果集为一个RDD 
JavaRDD<String> lines = sc.textFile("/Users/hensemlee/learning/my_projs/spark/src/main/java/com/hensemlee/spark.sh"); 
// 进行transform操作，转换后结果仍为一个RDD 
JavaRDD<Integer> lineLengths = lines.map(s -> s.length()); 
// 进行action操作 
int totalLength = lineLengths.reduce((a, b) -> a + b);
System.out.println("总长度为： " + totalLength); // 总长度为： 
```

## RDD的缓存级别都有哪些

NONE :什么类型都不是

 DISK_ONLY：磁盘 

DISK_ONLY_2：磁盘；

双副本 MEMORY_ONLY： 内存；

反序列化；把RDD作为反序列化的方式存储，假如RDD的内容存不下，剩余的分区在以后需要时会重新计算，不会刷到磁盘上。 

MEMORY_ONLY_2：内存；反序列化；

双副本 MEMORY_ONLY_SER：内存；序列化；这种序列化方式，每一个partition以字节数据存储，好处是能带来更好的空间存储，但CPU耗费高 MEMORY_ONLY_SER_2 : 内存；序列化；双副本 MEMORY_AND_DISK：内存 + 磁盘；反序列化；双副本；RDD以反序列化的方式存内存，假如RDD的内容存不下，剩余的会存到磁盘 MEMORY_AND_DISK_2 : 内存 + 磁盘；反序列化；双副本 MEMORY_AND_DISK_SER：内存 + 磁盘；序列化
MEMORY_AND_DISK_SER_2：内存 + 磁盘；序列化；双副本

## 7:Spark SQL

Spark 1.0版本开始，推出了Spark SQL。其实最早使用的，是Hadoop自己的Hive查询引擎；但是后来Spark开发了Shark；Shark底层很多东西还是依赖于Hive，但是修改了内存管理、物理计划、执行三个模块，底层使用Spark的基于内存的计算模型，从而让性能比Hive提升了数倍到上百倍。再后来Shark被淘汰，推出了Spark SQL。Shark的性能比Hive就要高出一个数量级，而Spark SQL的性能又比Shark高出一个数量级。

Spark SQL是一个用于结构化数据处理的Spark模块。与基本的Spark RDD API不同，Spark SQL提供的接口为Spark提供了关于数据结构和正在执行的计算的更多信息。在内部，Spark SQL使用这些额外的信息来执行额外的优化。有几种与Spark SQL交互的方法，包括SQL和Dataset API。当计算结果时，使用相同的执行引擎，而不依赖于使用哪种API/语言来表示计算。这种统一意味着开发人员可以很容易地在不同的api之间来回切换，而这些api提供了表达给定转换的最自然的方式。

Spark SQL的特点

1、支持多种数据源：Hive、RDD、Parquet、JSON、JDBC等。

2、多种性能优化技术：in-memory columnar storage、byte-code generation、cost model动态评估等。

3、组件扩展性：对于SQL的语法解析器、分析器以及优化器，用户都可以自己重新开发，并且动态扩展。

Spark SQL的性能优化

1、内存列存储（in-memory columnar storage） 内存列存储意味着，Spark SQL的数据，不是使用Java对象的方式来进行存储，而是使用面向列的内存存储的方式来进行存储。也就是说，每一列，作为一个数据存储的单位。从而大大优化了内存使用的效率。采用了内存列存储之后，减少了对内存的消耗，也就避免了gc大量数据的性能开销。

2、字节码生成技术（byte-code generation） Spark SQL在其catalyst模块的expressions中增加了codegen模块，对于SQL语句中的计算表达式，比如select num + num from t这种的sql，就可以使用动态字节码生成技术来优化其性能。

3、Scala代码编写的优化 对于Scala代码编写中，可能会造成较大性能开销的地方，自己重写，使用更加复杂的方式，来获取更好的性能。比如Option样例类、for循环、map/filter/foreach等高阶函数，以及不可变对象，都改成了用null、while循环等来实现，并且重用可变的对象。

### Spark SQL组成

SQL

Spark SQL的一个用途是执行SQL查询。Spark SQL还可以用于从现有的Hive中读取数据。当在另一种编程语言中运行SQL时，结果将作为Dataset/DataFrame返回。还可以使用命令行或JDBC/ODBC与SQL接口进行交互。

Dataset和DataFrames

数据集是分布式的数据集合。Dataset是Spark 1.6中添加的一个新接口，它提供了RDDs(强类型，能够使用强大的lambda函数)的优点，以及Spark SQL优化执行引擎的优点。可以从JVM对象构造数据集，然后使用功能转换(map、flatMap、filter等)操作数据集。

DataFrame是组织成命名列的数据集。它在概念上等价于关系数据库中的表或R/Python中的数据框架，但在底层有更丰富的优化。数据流可以由一系列广泛的源构建，例如:结构化数据文件、Hive中的表、外部数据库或现有的RDDs。DataFrame API可在Scala、Java、Python和r中使用。在Scala和Java中，数据aframe由行数据集表示。在Scala API中，DataFrame只是Dataset[Row]的一个类型别名。而在Java API中，用户需要使用Dataset来表示dataframe。



### Spark SQL执行流程

SQL 执行先会经过 SQL Parser 解析 SQL，然后经过 Catalyst 优化器处理，最后到 Spark 执行。而 Catalyst 的过程又分为很多个过程，其中包括：

- Analysis：主要利用 Catalog 信息将 Unresolved Logical Plan 解析成 Analyzed logical plan；
- Logical Optimizations：利用一些 Rule （规则）将 Analyzed logical plan 解析成 Optimized Logical Plan；
- Physical Planning：前面的 logical plan 不能被 Spark 执行，而这个过程是把 logical plan 转换成多个 physical plans，然后利用代价模型（cost model）选择最佳的 physical plan；
- Code Generation：这个过程会把 SQL 查询生成 Java 字节码。

所以整个 SQL 的执行过程可以使用下图表示：



![朱建峰 > 最新 > spark_sql.jpg](http://pms.ipo.com/download/attachments/149489483/spark_sql.jpg?version=1&modificationDate=1573799741000&api=v2)





## 8:Spark Streaming

### 概述

Spark在2.X版本中加入Structed Streaming模块，2.2.0之前都是实验版本，与Spark Streaming一样同是做流处理，但Structed Streaming是基于Spark SQL的流处理引擎。

Spark Streaming的核心思想是利用spark批处理框架，以微批处理（以一段时间的流作为一个batch）的方式，完成对流数据的处理。

Structed Streaming结构化流提供了快速、可伸缩、容错、端到端精确的一次流处理，而无需用户对流进行过多考虑。在内部，默认情况下，结构化流查询同样使用微批处理引擎进行处理，微批处理引擎将数据流处理为一系列小批处理作业，从而实现端到端延迟低至100毫秒，并且可以精确地保证一次容错；也实现了long-running的task，可以"不停的"循环从数据源获取数据并处理，从而实现真正的流处理。以dataset为代表的带有结构化（schema信息）的数据处理由于钨丝计划的完成，表现出更优越的性能。同时Structed Streaming可以从数据中获取时间（eventTime），从而可以针对流数据的生产时间而非收到数据的时间进行处理

Spark Streaming 是Spark核心API的一个扩展，可以实现高吞吐量的、具备容错机制的实时流数据的处理。它支持从多种数据源获取数据，包括Kafk、Flume、以及TCP socket等，从数据源获取数据之后，可以使用诸如map、reduce和window等高级函数进行复杂算法的处理。最后还可以将处理结果存储到文件系统和数据库等系统中。其处理的数据流图如下图：



Spark Streaming接收流数据，并根据一定的时间间隔拆分成一批批batch数据，用抽象接口*DStream*表示（DStream可以看成是一组RDD序列，每个batch对应一个RDD），然后通过Spark Engine处理这些batch数据，最终得到处理后的一批批结果数据。即其核心思想是利用spark批处理框架，以microbatch（以一段时间的流作为一个batch）的方式，完成对流数据的处理。

### Spark Streaming 不足

1.使用 Processing Time 而不是 Event Time

Processing Time 是数据到达 Spark 被处理的时间，而 Event Time 是数据自带的属性，一般表示数据产生于数据源的时间。比如 IoT 中，传感器在 12:00:00 产生一条数据，然后在 12:00:05 数据传送到 Spark，那么 Event Time 就是 12:00:00，而 Processing Time 就是 12:00:05。

 Spark Streaming 是基于 DStream 模型的 micro-batch 模式，简单来说就是将一个微小时间段，比如说 1s，的流数据当前批数据来处理。如果我们要统计某个时间段的一些数据统计，毫无疑问应该使用 Event Time，但是因为 Spark Streaming 的数据切割是基于 Processing Time，这样就导致使用 Event Time 特别的困难。

2.Complex, low-level api

Spark Streaming 的数据模型）提供的 API 类似 RDD 的 API 的，非常的 low level。编写 Spark Streaming 程序的时候，本质上就是要去构造 RDD 的 DAG 执行图，然后通过 Spark Engine 运行。这样导致一个问题是，DAG 可能会因为开发者的水平参差不齐而导致执行效率上的天壤之别。

3.reason about end-to-end application

这里的 end-to-end 指的是直接 input 到 out，比如 Kafka 接入 Spark Streaming 然后再导出到 HDFS 中。DStream 只能保证自己的一致性语义是 exactly-once 的，而 input 接入 Spark Streaming 和 Spark Straming 输出到外部存储的语义往往需要用户自己来保证。而这个语义保证写起来也是非常有挑战性，比如为了保证 output 的语义是 exactly-once 语义需要 output 的存储系统具有幂等的特性，或者支持事务性写入，这个对于开发者来说都不是一件容易的事情。

4.批流代码不统一

Spark Streaming尽管是对 RDD 的封装，但是我们要将 DStream 代码完全转换成 RDD 还是有一点工作量的，更何况现在 Spark 的批处理都用 DataSet/DataFrame API 了。

附上demo：

```java
`public class KafkaSource {   
    public static String topic = "spark_streaming_kafka_test";      
    public static void main(String[] args) throws Exception{         
        Properties p = new Properties();       
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.59.74.137:9092,10.59.74.138:9092,10.59.74.139:9092");
        //kafka地址，多个地址用逗号分割         
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);         p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);       
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p);        
        try {            
            while (true) {                
                String msg = getRandomString(new Random().nextInt(10)); 
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, msg);                 		       kafkaProducer.send(record);             
                System.out.println("消息发送成功:" + msg);          
                Thread.sleep(5000);          
            }       
        } finally {   
            kafkaProducer.close();    
        }   
    }      
    public static String getRandomString(int length){     
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";        
        Random random=new Random();      
        StringBuffer sb=new StringBuffer();      
        for(int i=0; i<length; i++){          
            int number = random.nextInt(62);  
            sb.append(str.charAt(number));    
        }         return sb.toString();   
    } 
}
```



```java
`public class SparkStreamingTest {
    public static void main(String[] args) throws Exception {     
        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.7");   
        SparkConf conf = new SparkConf().setAppName("sparkstreaming-kafka");    
        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(10));     
        Map<String, Object> kafkaParams = new HashMap<>();       
        kafkaParams.put("bootstrap.servers", "10.59.74.137:9092,10.59.74.138:9092,10.59.74.139:9092");         kafkaParams.put("key.deserializer", StringDeserializer.class);      
        kafkaParams.put("value.deserializer", StringDeserializer.class);    
        kafkaParams.put("group.id", "spark_stream_group_test");        
        kafkaParams.put("auto.offset.reset", "latest");      
        kafkaParams.put("enable.auto.commit", false);       
        Collection<String> topics = Arrays.asList("spark_stream_kafka_test");      
        JavaInputDStream<ConsumerRecord<String, String>> stream =  KafkaUtils.createDirectStream(jsc,                         LocationStrategies.PreferConsistent(),  ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)  );         jsc.checkpoint("hdfs://10.59.74.192:9000/data1/kettle/dianping/dianping_shop/kafka_word_count");         
        JavaPairDStream<String, String> stringStringJavaPairDStream = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));       
        JavaDStream<String> map = stringStringJavaPairDStream.map(s -> s._2()); 
        JavaPairDStream<String, Integer> stringIntegerJavaPairDStream = map.flatMap(s -> Arrays.asList(s.split("")).iterator()).mapToPair(s -> new Tuple2<>(s, 1));  
        JavaPairDStream<String, Integer> res = stringIntegerJavaPairDStream.reduceByKey((x, y) -> x + y);         res.dstream().saveAsTextFiles("hdfs://10.59.74.192:9000/data1/kettle/dianping/dianping_shop/kafka_word_count/", "ds"); 
        //        stringIntegerJavaPairDStream1.print();         
        jsc.start();        
        jsc.awaitTermination(); 
    }
}
```

**Structured Streaming执行模式**

Micro-batch模式

同样以micro-batch的模式完成批处理，处理模式类似Spark Streaming的批处理，可以定期（以固定间隔）处理，也可以处理完一个批次后，立刻进入下一批次的处理

Continuous Processing模式

启动长时运行的线程从数据源获取数据，worker线程长时运行，实时处理消息。放入queue中，启动long-running的worker线程从queue中读取数据并处理。该模式下，当前只能支持简单的projection式（如map,filter,mappartitions等）的操作

### **Structured Streaming工作原理**

结构化流中的关键思想是将实时数据流视为一个无边界且能被不断被追加的输入表。这产生了一个新的流处理模型，它非常类似于批处理模型。把流计算表示为标准的批处理式查询，就像在静态表上一样，Spark将它作为无界输入表上的增量查询运行。

对输入的查询将生成“结果表”。每一个触发间隔(例如，每1秒)，都会向输入表添加新行，最终更新结果表。无论何时更新结果表，我们都希望将更改后的结果行写入外部接收器

“输出”定义为写入外部存储的内容。输出可以在不同的模式下定义:

完成模式——整个更新后的结果表将被写入外部存储。如何处理整个表的写入由存储连接器决定。

追加模式——只将结果表中追加的新行(因为最后一个触发器)写入外部存储。这只适用于预期结果表中现有行不会更改的查询。

更新模式——只将结果表中自最后一个触发器以来更新的行写入外部存储(从Spark 2.1.1开始可用)。注意，这与完整模式不同，该模式只输出自上次触发器以来更改的行。如果查询不包含聚合，则等同于Append模式。



第一行DataFrame是输入表，最后一行wordCounts DataFrame是结果表。注意，用于生成单词计数的流数据流上的查询与静态数据流上的查询完全相同。但是，当这个查询启动时，Spark将不断检查套接字连接中的新数据。如果有新数据，Spark将运行一个“增量”查询，该查询将以前的运行计数与新数据组合起来计算更新后的计数，如下所示。

上图中，每一秒处理该秒内的数据，数据追加到输入表中，以“ ”分割计数后追加到结果表中，再以选择的输出模式将该结果输出外部接收器上，上图中为输出到控制台

附上demo

```java
`/**  * StructuredStreaming Test  *  * @author: lijun784  * @email: lijun784@pingan.com.cn  * @create: 2019/8/13 18:06  */ public class StructuredStreamingTest {   
    public static void main(String[] args) throws StreamingQueryException {  
        SparkSession spark = SparkSession.builder().master("local[2]").appName("StructuredStreamingTest").getOrCreate();        
        // Create DataFrame representing the stream of input lines from connection to localhost:9999       
        Dataset<Row> lines = spark.readStream().format("socket").option("host", "localhost").option("port", 9999)load(); 
        // Split the lines into words        
        Dataset<String> words = lines.as(Encoders.STRING()).flatMap((FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator(), Encoders.STRING());        
        // Generate running word count        
        Dataset<Row> wordCounts = words.groupBy("value").count();        
        // Start running the query that prints the running counts to the console         
        StreamingQuery query = wordCounts.writeStream().outputMode("append").format("console").start();        
        query.awaitTermination();     
    } 
}
```



```java
`/**  * @author: lijun784  * @email: lijun784@pingan.com.cn  * @create: 2019/8/13 17:55  */ 
    public class SocketSource {     
        public static void main(String[] args) {        
            try {             ServerSocket server = new ServerSocket(9999);        
                 System.out.println("启动服务器,等待客户端连接....");         
                 Socket client = server.accept();           
                 System.out.println("客户端已连接到服务器");           
                 while (true) {                
                     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));            
                     Random rand = new Random();            
                     StringBuffer data = new StringBuffer();      
                     for (int k = 0; k < 6; k++) {           
                         data.append("data" + rand.nextInt(10) + " ");    
                     }               
                     data.append("\n");               
                     bw.write(data.toString());             
                     bw.flush();                 
                     Thread.sleep(3000);        
                 }       
                } catch (IOException | InterruptedException e) {  
                e.printStackTrace();       
            }  
        } 
    }`
```



## **SparkStreaming与Structured Streaming对比**



| `流处理模式`   | `Spark Streaming`                           | `Structured Streaming`                    |
| -------------- | ------------------------------------------- | ----------------------------------------- |
| 执行模式       | Micro Batch                                 | Micro batch/Streaming                     |
| API            | Dstream/streamingContext（基于RDD）         | Micro batch / Streaming （基于Spark SQL） |
| Job生成方式    | Timer定时器定时生成job                      | Trigger触发                               |
| 支持的数据源   | Socket,filstream,kafka,zeroMq,flume,kinesis | Socket,filstream,kafka,ratesource         |
| executed-based | Executed based on dstream api               | Executed based on sparksql                |
| time-based     | Processing Time                             | ProcessingTime & eventTIme                |
| UI             | Built-in                                    | no                                        |

### spark和flink的数据模型

Spark Streaming 里的 DStream 和 RDD 模型类似，把一个实时进来的无限数据分割为一个个小批数据集合 DStream，定时器定时通知处理系统去处理这些微批数据。劣势非常明显，API 少、难胜任复杂的流计算业务，调大吞吐量而不触发背压是个体力活。不支持乱序处理，或者说很难处理乱序的问题。Spark Streaming 仅适合简单的流处理,因为spark的创始人在当时认为延迟不是那么的重要,他认为现实生活中没有那么多低延迟的应用场景,所以就没太注重延迟的问题,但是随着生活多样化场景的不断增加,对实时性的要求越来越高,所以spark也注意到了这个问题,开始在延迟方面发力,进而推出了Structured Streaming,相信很快sparkstreaming就会被Structured Streaming替代掉.

Spark Structured Streaming 提供了微批和流式两个处理引擎。微批的 API 虽不如 Flink 丰富，窗口、消息时间、trigger、watermarker、流表 join、流流 join 这些常用的能力都具备了。时延仍然保持最小 100 毫秒。当前处在试验阶段的流式引擎，提供了 1 毫秒的时延，但不能保证 exactly-once 语义，支持 at-least-once 语义。同时，微批作业打了快照，作业改为流式模式重启作业是不兼容的。这一点不如 Flink 做的完美。当然了现在还在优化阶段.

综上，Spark Streaming 和 Structured Streaming 是用批计算的思路做流计算。其实，用流计算的思路开发批计算才是最合理的。

Flink 的基本数据模型是数据流，及事件(Event)的序列。数据流作为数据的基本模型可能没有表或者数据块直观熟悉，但是可以证明是完全等效的。流可以是无边界的无限流，即一般意义上的流处理。也可以是有边界的有限流，这样就是批处理。

Flink 采用 Dataflow 模型，和 Lambda 模式不同。Dataflow 是纯粹的节点组成的一个图，图中的节点可以执行批计算，也可以是流计算，也可以是机器学习算法，流数据在节点之间流动，被节点上的处理函数实时 apply 处理，节点之间是用 netty 连接起来，两个 netty 之间 keepalive，网络 buffer 是自然反压的关键。经过逻辑优化和物理优化，Dataflow 的逻辑关系和运行时的物理拓扑相差不大。这是纯粹的流式设计，时延和吞吐理论上是最优的。

# 十六：Flink

## 

#   十七:jenkins(参照廖雪峰)

我们从Jenkins官方网站<https://jenkins.io/>下载最新的war包,这些安装程序都没有war包好使。我从未见过Jenkins这样把Java包做得如此简单的项目。只需要运行命令：

```
java -jar jenkins.war
```

Jenkins就启动成功了！它的war包自带Jetty服务器，剩下的工作我们全部在浏览器中进行。

1：在Jenkins首页选择“新建”，输入名字，选择“构建一个maven项目”：

2：在配置页中，源码管理选择Git，填入地址：

3：默认使用master分支。如果需要口令，在Credentials中添加用户名/口令，或者使用SSH Key。

一个job类似于一个ant task、maven goal或者gradle task。Job可以负责从SCM中取出代码，记录变更日志，执行批处理，收集统计信息，储存编译好的压缩包，邮件通知等等。

创建Job的流程：

![img](file:///D:/Users/ZHUJIANFENG789/AppData/Local/Packages/oice_16_974fa576_32c1d314_2e2b/AC/Temp/msohtmlclip1/01/clip_image002.png?lastModify=1560391910)

1. **JOB类型**：Job是jenkins的主要管理单元，不同的job种类可以完成不同的功能，没有特别要求的话可以选择“自由风格”。
2. **源代码**：持续集成就是用来做自动化的编译和打包的，因此取出源代码是第一步，通过jenkins来取出源代码还能够跟踪到SCM中的变更日志。
3. **执行方式**：jenkins支持定时执行、手工执行、外部触发三种方式。
4. **事前准备**：主要任务开始执行前需要做的准备工作，环境变量、文件夹、参数等等都应该准备阶段完成。
5. **主要执行**：在这个地方可以执行Shell代码，插件，等等。
6. **事后操作**：当build完成后，可以执行sonar、ftp、保存zip等等。

转至元数据结尾

# 十八:docker 

## 一：安装docker

```shell
 yum update
 #安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的
 yum install -y yum-utils device-mapper-persistent-data lvm2
 #设置yum源
 yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
 # 安装docker
 sudo yum install docker-ce docker-ce-cli containerd.io
 # 查看docker版本
docker --version
# 开机启动
systemctl enable --now docker
```

yum install docker-io  安装

docker -h  查看

service docker start /sudo systemctl start docker    启动docker

service docker stop     停止docker

## 二：镜像，容器命令

docer pull               #从镜像仓库拉取镜像命令
docker push           #将镜像推送到镜像仓库
docker images        #查看镜像
docker run              #运行容器
docker ps               #查看容器的进程
docker build           #通过dockfile构建镜像
docker exec            #对容器执行命令（ctrl d 退出容器）

docker login           #登入镜像仓库
docker  stop            #停止容器
docker rm               #删除容器
docker logs             #查看容器日志
docker history         #查看一个镜像的构建历史
docker save             #保存镜像成tar包
docker import          #导入
docker system         #容器使用系统的资源相关命令

​		-p:端口映射

​		-v: 将主机目录挂载到容器目录（比如）

```bash
docker run --name newname -d(后台运行)   镜像名称 --port  123
docker exec -it redis-A /bin/bash    #进入镜像容器中运行
```

安装docker命令补全工具

```shell
yum install -y bash-completion
```

设置docker开机自启动

```shell
systemctl enable docker
```

故障状况

 启动后无法远程到kubernets的dashbaord，后进行如下命令报错。

```
# kubectl get nodes
The connection to the server <master>:6443 was refused - did you specify the right host or port?
```

故障处理：

1.检查环境变量情况（正常）

```
# env | grep -i kub
```

![img](https://oscimg.oschina.net/oscnet/up-bc83fee7c37582a4bea52c48e592967cbd6.png)

2.检查docker服务（正常）

```
# systemctl status docker.service
```

![img](https://oscimg.oschina.net/oscnet/up-5fec37d383306d263df1d973ad3de124cc8.png)

3.检查kubelet服务（表面正常）

```
# systemctl status kubelet.service
```

![img](https://oscimg.oschina.net/oscnet/up-d599c233d43e4a145318fca88cadbda7269.png)

4.查看端口是是否被监听（没有监听）

```
# netstat -pnlt | grep 6443
```

5.检查防火墙状态（正常）

```
# systemctl status firewalld.service
```

![img](https://oscimg.oschina.net/oscnet/up-ad2bbe0fcc8626589340c685113dae06b96.png)

6.查看日志

```
# journalctl -xeu kubelet
```

![img](https://oscimg.oschina.net/oscnet/up-2d3ad1688c4e25d450d027623b18a635dfe.png)

这里分析，应该是镜像的问题。

6.1 重新导入一下API镜像即可。

```
# docker load -i kube-apiserver-amd64_v1.9.0.tar
```

6.2 重启docker和kubelet服务

```
# systemctl restart docker.service

# systemctl restart kubelet.service
```

6.3 检查服务（此时正常）

```
# kubectl get nodes
```

![img](https://oscimg.oschina.net/oscnet/up-7264fe7ce9a1a3d252e4ad854b95680b632.png)

6.4 通过docker logs命令可以查看容器的日志。

docker logs -f -t --tail 100 datacenter

**命令格式：**

```
$ docker logs [OPTIONS] CONTAINER
  Options:
        --details        显示更多的信息
    -f, --follow         跟踪实时日志
        --since string   显示自某个timestamp之后的日志，或相对时间，如42m（即42分钟）
        --tail string    从日志末尾显示多少行日志， 默认是all
    -t, --timestamps     显示时间戳
        --until string   显示自某个timestamp之前的日志，或相对时间，如42m（即42分钟）
```

**例子：**

查看指定时间后的日志，只显示最后100行：

```
$ docker logs -f -t --since="2018-02-08" --tail=100 CONTAINER_ID
```

查看最近30分钟的日志:

```
$ docker logs --since 30m CONTAINER_ID
```

查看某时间之后的日志：

```
$ docker logs -t --since="2018-02-08T13:23:37" CONTAINER_ID
```

查看某时间段日志：

```
$ docker logs -t --since="2018-02-08T13:23:37" --until "2018-02-09T12:23:37" CONTAINER_ID
```

k8s集群有两个段，一个k8s-service服务IP段，容器IP段，这两个的地址段均可在文件中查询得到：分别对应为：`{"k8s-service IP段":"net-conf.json","容器IP段":"kube-apiserver.yaml"}`

## 三:docker 安装mysql

下载完镜像运行（docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql）后发现

用navicat for mysql连接mysql发现报错：Client does not support authentication protocol requested by server。。。

（或者MySQL 连接出现 Authentication plugin 'caching_sha2_password' cannot be loaded](https://www.cnblogs.com/zhurong/p/9898675.html)）

进入容器：

```shell
docker exec -it 62349aa31687 /bin/bash
```

进入mysql：

```sql
mysql -uroot -ps
```

授权：

```sql
mysql> GRANT ALL ON *.* TO 'root'@'%';
```

刷新权限：

```sql
mysql> flush privileges;
```

更新加密规则：

```sql
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'password' PASSWORD EXPIRE NEVER;
```

更新root用户密码：

```sql
mysql> ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```

刷新权限：

```sql
mysql> flush privileges;
```

 rpm包下载mysql 问题汇总：

1：查看原始密码'

解决：

```sh
less /var/log/mysqld.log # 查看mysql日志查看密码
# 会有一行 password is generated XXXXX
```

```mysql
# 登录修改密码
mysql -u root -p
#报错再执行下面这个命令
 set global validate_password_policy=LOW
 #修改密码
ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
```

2:navicat 连接时报错is not allowed to connect tothis mmysql server](https://www.cnblogs.com/companionspace/p/10316083.html)

解决：

```mysql
mysql -u root -p
use mysql;
update user set host = '%' where user ='root';
flush privileges;
```

3：navicat 连接时报错authentication plugin caching_sha2_password

解决：

```mysql
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';  
mysql> flush privileges;
```





1. 



# 十九:  kuberneters

## 一：Docker 及相关设置(所有服务器都需要)

```shell
#下载安装

# 安装docker所需的工具
yum install -y yum-utils device-mapper-persistent-data lvm2
# 配置阿里云的docker源
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 指定安装这个版本的docker-ce
yum install -y docker-ce-18.09.9-3.el7
# 启动docker
systemctl enable docker && systemctl start docker

# 关闭防火墙
systemctl disable firewalld
systemctl stop firewalld

# 关闭selinux
# 临时禁用selinux
setenforce 0
# 永久关闭 修改/etc/sysconfig/selinux文件设置
sed -i 's/SELINUX=permissive/SELINUX=disabled/' /etc/sysconfig/selinux
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config

# 禁用交换分区
swapoff -a
# 永久禁用，打开/etc/fstab注释掉swap那一行。
sed -i 's/.*swap.*/#&/' /etc/fstab

# 修改内核参数
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
```

## 二. master安装

### 1:安装kubeadm、kubelet、kubectl

```shell
# 执行配置k8s阿里云源
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

# 安装kubeadm、kubectl、kubelet
yum install -y kubectl-1.17.0-0 kubeadm-1.17.0-0 kubelet-1.17.0-0

# 启动kubelet服务
systemctl enable kubelet && systemctl start kubelet
#查看版本（）
kkubelet --version
systemctl info kubeadm，
#查看kubelet 状态，注意此时kubelet是未开启状态会有一个异常
#卸载kubelet：
yum -y remove kubelet
```

### 2: 初始化k8s

```shell
#里的--apiserver-advertise-address使用的是master和node间能互相ping通的ip，我这里是192.168.1.108，大概需要2分钟，请耐心等待
kubeadm init --image-repository registry.aliyuncs.com/google_containers --kubernetes-version v1.17.0 --apiserver-advertise-address 192.168.1.108 --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12 --token-ttl 0
```

上面安装完后，会提示你输入如下命令，复制粘贴过来，执行即可。

```shell
# 上面安装完成后，k8s会提示你输入如下命令，执行
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

注意：此时可以用以下命令

```shell
#查看各组件状态
kubectl get componentstatus
#忘记master init 成功生成的join 命令
kubeadm token create --print-join-command
#重启docker,kubelet
systemctl restart docker
systemctl restart kubelet
systemctl daemon-reload
#日志：
journalctl -xeu kubelet
cat /var/log/messages
#动态日志:
journalctl -xefu kubelet

```

### 3:master安装flannel

1：网上的教学

```shell
#前提你能下载下来
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
sed -i 's@quay.io@quay.azk8s.cn@g' kube-flannel.yml
kubectl apply -f kube-flannel.yml
#如果你下载不下来，访问页面https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml 把文字复制下来，然后新建一个文件比如kube-flannel.yml,先下载镜像，然后执行apply命令
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-amd64
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-amd64
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-arm64
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-arm
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-ppc64le
docker pull registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-s390x
#修改tag
docker tag registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-amd64 quay.io/coreos/flannel:v0.12.0-amd64
docker tag registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-arm64 quay.io/coreos/flannel:v0.12.0-arm64
docker tag registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-arm quay.io/coreos/flannel:v0.12.0-arm
docker tag registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-ppc64le quay.io/coreos/flannel:v0.12.0-ppc64le
docker tag registry.cn-shanghai.aliyuncs.com/leozhanggg/flannel:v0.12.0-s390x quay.io/coreos/flannel:v0.12.0-s390x
#执行创建后看到很多created 说明创建成功
kubectl apply -f kube-flannel.yml

```

### 4:节点加入

```shell
#节点安装完kubelet之后是未启动状态，查看日志会有异常failed to load Kubelet config file /var/lib/kubelet/config.yaml,需要kubeadm join 之后才会启动就正常了，我在这纠结了好久
#节点加入
kubeadm join 192.168.1.108:6443 --token lhwtqv.kbn0ogrzoric1wrt --discovery-token-ca-cert-hash sha256:a4ff6dc6c0cf00d0594ce0bfaa0a4c6fe1f861fc9d354e9a4bec9f49c7ec224f
#可能会报错/proc/sys/net/ipv4/ip_forward contents are not set to 1
#解决方案：echo 1 > /proc/sys/net/ipv4/ip_forward
```

### 4：k8s常用命令参考

```shell
#查看镜像
docker image ls
kubectl get svc

```

### 5：常见异常解决方案（主要是我遇到的）

1： Failed to get system container stats for "/system.slice/docker.service": failed to get cgroup stats

```shell
 vi /usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf
 
 
 [Service]
CPUAccounting=true              ## 添加 CPUAccounting=true 选项，开启 systemd CPU 统计功能
MemoryAccounting=true           ## 添加 MemoryAccounting=true 选项，开启 systemd Memory 统计功能
Environment="KUBELET_KUBECONFIG_ARGS=--bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf"
Environment="KUBELET_CONFIG_ARGS=--config=/var/lib/kubelet/config.yaml"
EnvironmentFile=-/var/lib/kubelet/kubeadm-flags.env
EnvironmentFile=-/etc/sysconfig/kubelet
ExecStart=
ExecStart=/usr/bin/kubelet $KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS
```

2:   Dockerfile指令：

1、FROM
格式：FROM <image>或 FROM <image>:<tag>
第一条指令必须为FROM指令，并且，如果在同一个Dockerfile中创建多个镜像时，可以使用多个FROM指令（每个镜像一次）
2、MAINTAINET
格式：MAINTAINET <name>    指定维护者的信息
3、RUN
格式：RUN <command> 或 RUN ["", "", ""]
每条指令将在当前镜像基础上执行，并提交为新的镜像。（可以用“\”换行）
4、CMD
格式：CMD ["","",""] 指定启动容器时执行的命令，每个Dockerfile只能有一条CMD指令，如果指定了多条指令，则最后一条执行。（会被启动时指定的命令覆盖）
5、EXPOSE
格式：EXPOSE <port>  [ <port> ...]
告诉Docker服务端暴露端口，在容器启动时需要通过 -p 做端口映射
6、ENV
格式：ENV <key> <value>
指定环境变量，会被RUN指令使用，并在容器运行时保存

## 三：推送镜像到Docker Hub，从Docker Hub上下载镜像后运行

将xxljob的jar包已经写好的dockerfile 放入同一个文件夹，并执行一下命令(注意有个点)

```shell
#注意最后的 点
docker build -t='xxljob' .
#打完镜像之后可以用看到镜像
docker images 
#要推入docker hub就要重命名镜像名称
docker tag 091612/xxljob:latest xxljob
#从docker hub上拉取,如果tag不是latest，拉取镜像就要带上tag
docker pull 091612/xxljob:latest
```

## 四：通过k8s集群，从DockerHub的个人镜像仓库拉取镜像

K8s 集群一般会管理多个节点，每个节点都有自己的 docker 环境。如果让用户分别到集群节点上登录镜像仓库，这显然是很不方便的。为了解决这个问题，K8s 实现了自动拉取镜像的功能。这个功能的核心，是把 docker.json 内容编码，并以 Secret 的方式作为 Pod 定义的一部分传给 Kubelet。

步骤如下：

1. 创建 secret。这个 secret 的 .dockerconfigjson 数据项包括了一份 base64 编码的 docker.json 文件；

   ```yaml
   apiVersion: v1
   kind: Secret
   metadata:
     name: mysecret
     namespace: default
   type: kubernetes.io/dockerconfigjson
   data:
     .dockerconfigjson: 替换成刚才用config.json生成的那串老长的信息。
   ```

   创建好后执行命令：

   ```shell
   kubectl get secret mysecret
   ```

   

2. 创建 pod，且 pod 编排中 imagePullSecrets 指向第一步创建的 secret；

3. Kubelet 作为集群控制器，监控着集群的变化。当它发现新的 pod 被创建，就会通过 API Server 获取 pod 的定义，这包括 imagePullSecrets 引用的 secret；

4. Kubelet 调用 docker 创建容器且把 .dockerconfigjson 传给 docker；

5. 最后 docker 使用解码出来的账户密码拉取镜像，这和上一节的方法一致。





# 二十:数据结构和算法

基础：数组、队列、栈、链表、树与递归、哈希表、双指针

思想：二分、滑动窗口、动态规划、搜索

提高：贪心、分治、位运算、堆

## 一：树

### 1：阶——>所有节点中拥有的最多的子节点数目称作树的阶。

### 2:  树中结点的最大层次数称为树的深度或高度。

### 3: 结点拥有的子树的数目称为结点的度。度为 0 的结点称为叶子或终端结点。度不为 0 的结点称为**非终端结点**或**分支结点**。

**性质** **6.1** 树中的结点数等于树的边数加 1，也等于所有结点的度数之和加 1。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\树的分类.jpg)

## 二：B树(也叫B-)与B+树

#### 1:B树:     B树是一种多路搜索树。　

首先，B树不要和二叉树混淆，在[计算机科学中](https://en.wikipedia.org/wiki/Computer_science)，**B树**是一种自平衡[树数据结构](https://en.wikipedia.org/wiki/Tree_data_structure)，它维护有序数据并允许以[对数时间](https://en.wikipedia.org/wiki/Logarithmic_time)进行搜索，顺序访问，插入和删除。B树是[二叉搜索树](https://en.wikipedia.org/wiki/Binary_search_tree)的一般化，因为节点可以有两个以上的子节点。[[1\]](https://en.wikipedia.org/wiki/B-tree#cite_note-Comer-1)与其他[自平衡二进制搜索树不同](https://en.wikipedia.org/wiki/Self-balancing_binary_search_tree)，B树非常适合读取和写入相对较大的数据块（如光盘）的存储系统。它通常用于[数据库](https://en.wikipedia.org/wiki/Database)和[文件系统](https://en.wikipedia.org/wiki/File_system)。

通常我们说m阶的B树，它必须满足如下条件

1. 定义任意非叶子结点最多只有M个儿子，且M>2。

2. 根结点的儿子数为[2, M]。

3. 除根结点以外的非叶子结点的儿子数为[M/2, M]。

4. 每个结点存放至少M/2-1（取上整）和至多M-1个关键字；（至少2个关键字）。

5. 非叶子结点的关键字个数=指向儿子的指针个数-1。

6. 非叶子结点的关键字：K[1], K[2], …, K[M-1]，且K[i] <= K[i+1]。

7. 非叶子结点的指针：P[1], P[2], …,P[M]（其中P[1]指向关键字小于K[1]的子树，P[M]指向关键字大于K[M-1]的子树，其它P[i]指向关键字属于(K[i-1], K[i])的子树）。

8. 所有叶子结点位于同一层。

   ![img](https://user-gold-cdn.xitu.io/2019/10/28/16e111e6b21c6a43?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

B树的搜索，从根结点开始，对结点内的关键字（有序）序列进行二分查找，如果命中则结束，否则进入查询关键字所属范围的儿子结点；重复，直到所对应的是叶子结点。

#### **2:查找文件29的过程：**

1. 根据根结点指针找到文件目录的根磁盘块1，将其中的信息导入内存。（磁盘IO操作1次）
2. 此时内存中有两个文件名17，35和三个存储其他磁盘页面地址的数据。根据算法我们发现17<29<35，因此我们找到指针p2。
3. 根据p2指针，我们定位到磁盘块3，并将其中的信息导入内存。（磁盘IO操作2次）
4. 此时内存中有两个文件名26，30和三个存储其他磁盘页面地址的数据。根据算法我们发现26<29<30，因此我们找到指针p2。
5. 根据p2指针，我们定位到磁盘块8，并将其中的信息导入内存。（磁盘IO操作3次）
6. 此时内存中有两个文件名28，29。根据算法我们查找到文件29，并定位了该文件内存的磁盘地址。

#### **3:B树的特性：**

1. 关键字分布在整颗树的所有节点。
2. 任何一个关键字出现且只出现在一个结点中。
3. 搜索有可能在非叶子结点结束。
4. 其搜索性能等价于在关键字全集内做一次二分查找。






### B+树的查找过程

如图所示，如果要查找数据项29，那么首先会把磁盘块1由磁盘加载到内存，此时发生一次IO，在内存中用二分查找确定29在17和35之间，锁定磁盘块1的P2指针，内存时间因为非常短（相比磁盘的IO）可以忽略不计，通过磁盘块1的P2指针的磁盘地址把磁盘块3由磁盘加载到内存，发生第二次IO，29在26和30之间，锁定磁盘块3的P2指针，通过指针加载磁盘块8到内存，发生第三次IO，同时内存中做二分查找找到29，结束查询，总计三次IO。真实的情况是，3层的b+树可以表示上百万的数据，如果上百万的数据查找只需要三次IO，性能提高将是巨大的，如果没有索引，每个数据项都要发生一次IO，那么总共需要百万次的IO，显然成本非常非常高。

### b+树性质

1.通过上面的分析，我们知道IO次数取决于b+数的高度h，假设当前数据表的数据为N，每个磁盘块的数据项的数量是m，则有h=㏒(m+1)N，当数据量N一定的情况下，m越大，h越小；而m = 磁盘块的大小 / 数据项的大小，磁盘块的大小也就是一个数据页的大小，是固定的，如果数据项占的空间越小，数据项的数量越多，树的高度越低。这就是为什么每个数据项，即索引字段要尽量的小，比如int占4字节，要比bigint8字节少一半。这也是为什么b+树要求把真实的数据放到叶子节点而不是内层节点，一旦放到内层节点，磁盘块的数据项会大幅度下降，导致树增高。当数据项等于1时将会退化成线性表。

2.当**b+树的数据项是复合的数据结构**，比如(name,age,sex)的时候，b+数是按照从*左到右的顺序来建立搜索树*的，比如当(张三,20,F)这样的数据来检索的时候，b+树会优先比较name来确定下一步的所搜方向，**如果name相同再依次比较age和sex**，最后得到检索的数据；但当(20,F)这样的没有name的数据来的时候，b+树就不知道下一步该查哪个节点，因为建立搜索树的时候name就是第一个比较因子，必须要先根据name来搜索才能知道下一步去哪里查询。*比如*当(张三,F)这样的数据来检索时，b+树可以用name来指定搜索方向，但下一个字段age的缺失，*所以只能把名字等于张三的数据都找到，然后再匹配性别是F的数据了*， 这个是非常重要的性质，即**索引的最左匹配特性**。



## 二：红黑树

### 1：定义

红黑树是一种自平衡的二叉查找树

### 2：特点

  1）每个结点或者是红的或者是黑的；

   2）根结点是黑的；

   3）每个叶结点（NIL）是黑的；

   4）如果一个结点是红的，则它的两个孩子都是黑的；

   5）对每个结点，从该结点到其他其子孙结点的所有路径上包含相同数目的黑结点。

 为了后面的分析，我们还得知道以下**知识点**。

  （1）黑高度：从某个结点x出发（不包括该结点）到达一个叶结点的任意一条路径上，黑色结点的个数称为该结点x的黑高度。

  （2）一颗有n个内结点的红黑树的高度至多为2lg(n+1)。  （内结点视为红黑树中带关键字的结点）

  （3）包含n个内部节点的红黑树的高度是 O(log(n))。

  （4）特性3中指定红黑树的每个叶子节点都是空节点，但是在Java实现中红黑树将使用null代表空节点，因此遍历红黑树时看不到黑色的叶子节点，反而见到的叶子节点是红色的

  （5）特性4保证了从根节点到叶子节点的最长路径的长度不会超过任何其他路径的两倍，例如黑色高度为3的红黑树，其最短路径(路径指的是根节点到叶子节点)是2(黑节点-黑节点-黑节点)，其最长路径为4(黑节点-红节点-黑节点-红节点-黑节点)。

### 3：实践

#### 红黑树操作

##### 插入操作

首先红黑树在插入节点的时，我们设定插入节点的颜色为**红色**,如果插入的是黑色节点，必然会违背特性5，即改变了红黑树的黑高度，如下插入红色结点又存在着几种情况：

1.**黑父**

如图所示，这种情况不会破坏红黑树的特性，即不需要任何处理

<img src="D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black1.jpg"  />

2.**红父**

当其父亲为红色时又会存在以下的情况

- **红叔**

红叔的情况，其实相对来说比较简单的，如下图所示，只需要通过修改父、叔的颜色为黑色，祖的颜色为红色，而且回去递归的检查祖节点即可

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black2.jpg)

- **黑叔**

黑叔的情况有如下几种，这几种情况下是不能够通过修改颜色达到平衡的效果，因此会通过旋转的操作，红黑树种有两种旋转操作，左旋和右旋(现在存在的疑问，什么时候使用到左旋，什么时候使用到右旋)

- Case 1:[先右旋，在改变颜色(根节点必须为黑色，其两个子节点为红色，叔节点不用改变)],如下图所示，注意省略黑哨兵节点

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black3.jpg)

- Case 2:[先左旋变成Case1中的情况，再右旋，最后改变颜色(根节点必须为黑色，其两个子节点为红色，叔节点不用改变)],如下图所示，注意省略黑哨兵节点

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black4.jpg)

- Case 3:[先左旋，最后改变颜色(根节点必须为黑色，其两个子节点为红色，叔节点不用改变)],如下图所示，注意省略黑哨兵节点

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black5.jpg)

- Case 4:[先右旋变成Case 3的情况，再左旋，最后改变颜色(根节点必须为黑色，其两个子节点为红色，叔节点不用改变)],如下图所示，注意省略黑哨兵节点

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black6.jpg)

以上就是红黑树新增节点所有可能的操作，下面会介绍红黑树中的删除操作

##### 删除操作

删除操作相比于插入操作情况更加复杂，删除一个节点可以大致分为三种情况：

- 1.删除的节点没有孩子节点，即当前节点为叶子节点，这种可以直接删除
- 2.删除的节点有一个孩子节点，这种需要删除当前节点，并使用其孩子节点顶替上来
- 3.删除的节点有两个孩子节点，这种需要先找到其后继节点(树中大于节点的最小的元素);然后将其后继节点的内容复制到该节点上，其后继节点就相当于该节点的替身， 需要注意的是其后继节点一定不会有两个孩子节点(这点应该很好理解，如果后继节点有左孩子节点，那么当前的后继节点肯定不是最小的，说明后继节点只能存在没有孩子节点或者只有一个右孩子节点)，即这样就将问题转换成为1,2中的方式。

在讲述修复操作之前，首先需要明白几点，

1.对于红黑树而言，单支节点的情况只有如下图所示的一种情况，即为当前节点为黑色，其孩子节点为红色,(1.假设当前节点为红色，其两个孩子节点必须为黑色，2.若有孙子节点，则必为黑色，导致黑子数量不等，而红黑树不平衡)

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black1.jpg)

2.由于红黑树是特殊的二叉查找树，它的删除和二叉查找树类型，真正的删除点即为删除点A的中序遍历的后继(前继也可以)，通过红黑树的特性可知这个后继必然最多只能有一个孩子，其这个孩子节点必然是右孩子节点，从而为单支情况(即这个后继节点只能有一个红色孩子或没有孩子)

下面将详细介绍，在执行删除节点操作之后，将通过修复操作使得红黑树达到平衡的情况。

- Case 1:被删除的节点为红色，则这节点必定为叶子节点(首先这里的被删除的节点指的是真正删除的节点，通过上文得知的真正删除的节点要么是节点本身，要么是其后继节点，若是节点本身则必须为叶子节点，不为叶子节点的话其会有左右孩子，则真正删除的是其右孩子树上的最小值，若是后继节点，也必须为叶子节点，若不是则其也会有左右孩子，从而和2中相违背)，这种情况下删除红色叶节点就可以了，不用进行其他的操作了。

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black7.jpg)

- Case 2:被删除的节点是黑色，其子节点是红色，将其子节点顶替上来并改变其颜色为黑色，如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black8.jpg)

- Case 3:被删除的节点是黑色，其子节点也是黑色，将其子节点顶替上来，变成了双黑的问题，此时有以下情况
  - Case 1:新节点的兄弟节点为**红色**，此时若新节点在左边则做左旋操作，否则做右旋操作，之后再将其父节点颜色改变为红色，兄弟节点

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black9.jpg)

从图中可以看出，操作之后红黑树并未达到平衡状态，而是变成的**黑兄**的情况

- Case 2:新节点的兄弟节点为**黑色**,此时可能有如下情况
  - 红父二黑侄：将父节点变成黑色，兄弟节点变成红色，新节点变成黑色即可,如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black10.jpg)

- 黑父二黑侄：将父节点变成新节点的颜色，新节点变成黑色，兄弟节点染成红色，还需要继续以父节点为判定点继续判断,如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black11.jpg)

- 红侄：

情况一:新节点在右子树，红侄在兄弟节点左子树，此时的操作为右旋，并将兄弟节点变为父亲的颜色，父亲节点变为黑色，侄节点变为黑色，如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black12.jpg)

情况二:新节点在右子树，红侄在兄弟节点右子树，此时的操作为先左旋，后右旋并将侄节点变为父亲的颜色，父节点变为黑色，如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black13.jpg)

情况三：新节点在左子树，红侄在兄弟节点左子树,此时的操作为先右旋在左旋并将侄节点变为父亲的颜色，父亲节点变为黑色，如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black14.jpg)

情况四：新节点在右子树，红侄在兄弟节点右子树,此时的操作为左旋，并将兄弟节点变为父节点的颜色，父亲节点变为黑色，侄节点变为黑色，如下图所示

![](D:\Users\ZHUJIANFENG789\Desktop\要懂\location\red-black15.jpg)

#### 红黑树实现

如下是使用JAVA代码实现红黑树的过程，主要包括了插入、删除、左旋、右旋、遍历等操作

##### 插入

```java
/* 插入一个节点
 * @param node
 */
private void insert(RBTreeNode<T> node){
    int cmp;
    RBTreeNode<T> root = this.rootNode;
    RBTreeNode<T> parent = null;

    //定位节点添加到哪个父节点下
    while(null != root){
        parent = root;
        cmp = node.key.compareTo(root.key);
        if (cmp < 0){
            root = root.left;
        } else {
            root = root.right;
        }
    }

    node.parent = parent;
    //表示当前没一个节点，那么就当新增的节点为根节点
    if (null == parent){
        this.rootNode = node;
    } else {
        //找出在当前父节点下新增节点的位置
        cmp = node.key.compareTo(parent.key);
        if (cmp < 0){
            parent.left = node;
        } else {
            parent.right = node;
        }
    }

    //设置插入节点的颜色为红色
    node.color = COLOR_RED;

    //修正为红黑树
    insertFixUp(node);
}

/**
 * 红黑树插入修正
 * @param node
 */
private void insertFixUp(RBTreeNode<T> node){
    RBTreeNode<T> parent,gparent;
    //节点的父节点存在并且为红色
    while( ((parent = getParent(node)) != null) && isRed(parent)){
        gparent = getParent(parent);

        //如果其祖父节点是空怎么处理
        // 若父节点是祖父节点的左孩子
        if(parent == gparent.left){
            RBTreeNode<T> uncle = gparent.right;
            if ((null != uncle) && isRed(uncle)){
                setColorBlack(uncle);
                setColorBlack(parent);
                setColorRed(gparent);
                node = gparent;
                continue;
            }

            if (parent.right == node){
                RBTreeNode<T> tmp;
                leftRotate(parent);
                tmp = parent;
                parent = node;
                node = tmp;
            }

            setColorBlack(parent);
            setColorRed(gparent);
            rightRotate(gparent);
        } else {
            RBTreeNode<T> uncle = gparent.left;
            if ((null != uncle) && isRed(uncle)){
                setColorBlack(uncle);
                setColorBlack(parent);
                setColorRed(gparent);
                node = gparent;
                continue;
            }

            if (parent.left == node){
                RBTreeNode<T> tmp;
                rightRotate(parent);
                tmp = parent;
                parent = node;
                node = tmp;
            }

            setColorBlack(parent);
            setColorRed(gparent);
            leftRotate(gparent);
        }
    }
    setColorBlack(this.rootNode);
}
```

插入节点的操作主要分为以下几步：

- 1.定位：即遍历整理红黑树，确定添加的位置，如上代码中insert方法中就是在找到添加的位置
- 2.修复：这也就是前面介绍的，添加元素后可能会使得红黑树不在满足其特性，这时候需要通过变色、旋转来调整红黑树，也就是如上代码中insertFixUp方法

##### 删除节点

如下为删除节点的代码

```java
private void remove(RBTreeNode<T> node){
    RBTreeNode<T> child,parent;
    boolean color;
    //被删除节点左右孩子都不为空的情况
    if ((null != node.left) && (null != node.right)){

        //获取到被删除节点的后继节点
        RBTreeNode<T> replace = node;

        replace = replace.right;
        while(null != replace.left){
            replace = replace.left;
        }

        //node节点不是根节点
        if (null != getParent(node)){
            //node是左节点
            if (getParent(node).left == node){
                getParent(node).left = replace;
            } else {
                getParent(node).right = replace;
            }
        } else {
            this.rootNode = replace;
        }

        child = replace.right;
        parent = getParent(replace);
        color = getColor(replace);

        if (parent == node){
            parent = replace;
        } else {
            if (null != child){
                setParent(child,parent);
            }
            parent.left = child;

            replace.right = node.right;
            setParent(node.right, replace);
        }

        replace.parent = node.parent;
        replace.color = node.color;
        replace.left = node.left;
        node.left.parent = replace;
        if (color == COLOR_BLACK){
            removeFixUp(child,parent);
        }

        node = null;
        return;
    }

    if (null != node.left){
        child = node.left;
    } else {
        child = node.right;
    }

    parent = node.parent;
    color = node.color;
    if (null != child){
        child.parent = parent;
    }

    if (null != parent){
        if (parent.left == node){
            parent.left = child;
        } else {
            parent.right = child;
        }
    } else {
        this.rootNode = child;
    }

    if (color == COLOR_BLACK){
        removeFixUp(child, parent);
    }
    node = null;
}
/**
 * 删除修复
 * @param node
 * @param parent
 */
private void removeFixUp(RBTreeNode<T> node, RBTreeNode<T> parent){
    RBTreeNode<T> other;
    //node不为空且为黑色，并且不为根节点
    while ((null == node || isBlack(node)) && (node != this.rootNode) ){
        //node是父节点的左孩子
        if (node == parent.left){
            //获取到其右孩子
            other = parent.right;
            //node节点的兄弟节点是红色
            if (isRed(other)){
                setColorBlack(other);
                setColorRed(parent);
                leftRotate(parent);
                other = parent.right;
            }

            //node节点的兄弟节点是黑色，且兄弟节点的两个孩子节点也是黑色
            if ((other.left == null || isBlack(other.left)) &&
                    (other.right == null || isBlack(other.right))){
                setColorRed(other);
                node = parent;
                parent = getParent(node);
            } else {
                //node节点的兄弟节点是黑色，且兄弟节点的右孩子是红色
                if (null == other.right || isBlack(other.right)){
                    setColorBlack(other.left);
                    setColorRed(other);
                    rightRotate(other);
                    other = parent.right;
                }
                //node节点的兄弟节点是黑色，且兄弟节点的右孩子是红色，左孩子是任意颜色
                setColor(other, getColor(parent));
                setColorBlack(parent);
                setColorBlack(other.right);
                leftRotate(parent);
                node = this.rootNode;
                break;
            }
        } else {
            other = parent.left;
            if (isRed(other)){
                setColorBlack(other);
                setColorRed(parent);
                rightRotate(parent);
                other = parent.left;
            }

            if ((null == other.left || isBlack(other.left)) &&
                    (null == other.right || isBlack(other.right))){
                setColorRed(other);
                node = parent;
                parent = getParent(node);
            } else {
                if (null == other.left || isBlack(other.left)){
                    setColorBlack(other.right);
                    setColorRed(other);
                    leftRotate(other);
                    other = parent.left;
                }

                setColor(other,getColor(parent));
                setColorBlack(parent);
                setColorBlack(other.left);
                rightRotate(parent);
                node = this.rootNode;
                break;
            }
        }
    }
    if (node!=null)
        setColorBlack(node);
}
```

删除节点主要分为几种情况去做对应的处理：

- 1.删除节点,按照如下三种情况去删除节点
  - 1.真正删除的节点没有子节点
  - 2.真正删除的节点有一个子节点
  - 3.正在删除的节点有两个子节点
- 2.修复红黑树的特性，如代码中调用removeFixUp方法修复红黑树的特性。

