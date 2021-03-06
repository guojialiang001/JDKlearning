/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import  java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import sun.misc.SharedSecrets;

/**
 * hash表依赖map接口，这个实现提供了所有的map操作的配置，并且允许null值和null键,(hashmap类大致相当于hashtable，除了它这个非同步并且允许空值。）
 *  这个类没有保证map的顺序。特指它没有保证顺序，这将保持不变。
 *
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 *
 *
 * 对于主要操作（get 和put）它实现提供了常量时间的表现。假设这个hash函数适当得分散存储在桶中，
 * 对集合视图的迭代时间需要与HashMap的“容量”成比例
 * 实例（存储桶的数量）加上其大小（键值映射的数量）
 * 因此，如果遍历的表现很重要,不要设置初始容量太高（或者负载系数太低）,这个是非常重要的
 *
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 *
 *
 ** 一个hashmap的实例有两个参数影响它的表现，初始化容量，和负载因子，容量是在hash表中的桶的数量，
 *  并且初始容量仅仅是hash表创建的容量。 负载因子是hashmap在它的容量自动增长之前，hash表被允许填满的测量值。
 *  在hash表entry的数量超过负载因子和当前容量，hash表会被重新rehash，（这是 重建内部数据结构）
 *  因此，哈希表的存储桶数大约是当前存储桶数的两倍。
 *
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 *
 *   作为一个常用的规则，默认的负载因子0。75 提供了一个好的时间和空间成本之间的权衡
 *   更高的价值 减少了空间，较高的值会减少空间开销，但会增加查找成本。（反射在大多数的hashmap类的操作）包括 get和put 方法
 *   map实例的预期数量和它的负载因子 在设置其初始容量时应考虑，以尽量减少rehash的次数
 *   如果初始容量大于最大入口数除以负载系数，再也不会发生任何rehash操作
 *

 * <p>As a general rule, the default load factor (.75) offers a good
 * tradeoff between time and space costs.  Higher values decrease the
 * space overhead but increase the lookup cost (reflected in most of
 * the operations of the <tt>HashMap</tt> class, including
 * <tt>get</tt> and <tt>put</tt>).  The expected number of entries in
 * the map and its load factor should be taken into account when
 * setting its initial capacity, so as to minimize the number of
 * rehash operations.  If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no rehash
 * operations will ever occur.
 *
 *efficiently
 *
 *如果许多的映射被存储在hashmap的实例中， 一个十分大的容量会让存储映射的效率要比让它根据需要自动rehash来增长表更高 。
 * 备注：使用许多的key用同样的hashcode 是对于任何hash表当然都缓慢低效。
 *为了减轻影响，当keys都 Comparable，这个类可以使键之间的比较顺序有助于打破联系
 *
 * <p>If many mappings are to be stored in a <tt>HashMap</tt>
 * instance, creating it with a sufficiently large capacity will allow
 * the mappings to be stored more efficiently than letting it perform
 * automatic rehashing as needed to grow the table.  Note that using
 * many keys with the same {@code hashCode()} is a sure way to slow
 * down performance of any hash table. To ameliorate impact, when keys
 * are {@link Comparable}, this class may use comparison order among
 * keys to help break ties.
 *
 *
 * 备注 这个实现是不同步的，
 * 如果多线程同时进入一个hashmap ，并且至少其中一个线程在map的结构上修改，
 * 它一个是在外部同步的（一个结构的修改是任何的增加或者删除一个或多个映射关系的操作）;
 * 只是改变这个值关联了一个key 已包含不是结构修改的实例
 * 这通常是通过在自然封装map的某个对象上进行同步来实现的
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 *
 * 如果没有这个对象存在，这个MAP应该被{@link Collections#synchronizedMap Collections.synchronizedMap}方法包装起来。
 * 这个是最好的被创建的操作，去防止意外的非同步进入到这个MAP
 * Map m = Collections.synchronizedMap(new HashMap(...));
 *
 *
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 *
 * 这个类的所有“集合视图方法”返回的迭代器都是fail-fast 的：  如果map在迭代器创建的任何时间后有结构化的修改
 * 在除了通过迭代器的自己的删除方法的以外的任何方式。迭代器将会抛出一个{@link ConcurrentModificationException}
 *  因此，面对当前的修改，这个迭代器快速的失败和清空， 而不是冒着武断的风险， 未来不确定时间的非确定性行为
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * 请注意，迭代器的fail fast fast行为不能像现在这样得到保证，普通来说，不可能去做任何强硬的保证出现非同步的当前修改
 * fail-fast 迭代器 抛出异常ConcurrentModificationException 在一个 尽力而为的基础
 * 然而， 如果编写一个依赖于此异常的正确性的程序，那将是错误的：迭代器的快速失效行为应该只用于检测bug。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * 这个类是一个 <a href="{@docRoot}/../technotes/guides/collections/index.html"> 的成员
 * Java集合框架。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    /*

        实现备注

       这个map通常 操作作为一个盒子的hash表中，但是当盒子太大，它们被转化成树状物箱
       每一个结构都像是在treemap一样， 大多数方法尝试去使用普通的箱子，但是
       替换成 树形节点的方法 是可应用的（只需要检查节点的实例）
       树形节点的箱子可以像其他一样被遍历和使用但是
       但另外，在人口过多时支持更快的查找
       然而由于绝大多数正常使用的箱并没有人口过多,
       检查树形的存在的箱子可能在表方法的过程中可能会被延迟

       树形的箱子（箱子的元素全都是树形的节点）都通过hashcode主要顺序排列。
       但是在这个关联上，如果两个元素都是 同样" class c 实现了Comparable<C>"
       类型然后他们的compareTo方法会被使用于排序上，（我们保守的检查一般的类型通过反射去校验这个看起来方法的可比性）
       添加树形箱子的复杂性是值得的，在提供 最坏情况O（对数n）操作 当键中的任何一个有去重的哈希或者顺序性
        因此，当hashCode（）方法返回分布不均匀的值时，意外或恶意使用会导致性能下降
        和许多key分享一个hashcode一样， 他们的比较能力也是一样长， （如果两个都不使用，与不采取预防措施相比，我们可能会在时间和空间上浪费大约两倍，
        但已知的唯一案例来自糟糕的用户编程实践，这些实践已经非常缓慢，几乎没有什么区别）





     * Implementation notes.
     *
     * This map usually acts as a binned (bucketed) hash table, but
     * when bins get too large, they are transformed into bins of
     * TreeNodes, each structured similarly to those in
     * java.util.TreeMap. Most methods try to use normal bins, but
     * relay to TreeNode methods when applicable (simply by checking
     * instanceof a node).  Bins of TreeNodes may be traversed and
     * used like any others, but additionally support faster lookup
     * when overpopulated. However, since the vast majority of bins in
     * normal use are not overpopulated, checking for existence of
     * tree bins may be delayed in the course of table methods.
     *
     * Tree bins (i.e., bins whose elements are all TreeNodes) are
     * ordered primarily by hashCode, but in the case of ties, if two
     * elements are of the same "class C implements Comparable<C>",
     * type then their compareTo method is used for ordering. (We
     * conservatively check generic types via reflection to validate
     * this -- see method comparableClassFor).  The added complexity
     * of tree bins is worthwhile in providing worst-case O(log n)
     * operations when keys either have distinct hashes or are
     * orderable, Thus, performance degrades gracefully under
     * accidental or malicious usages in which hashCode() methods
     * return values that are poorly distributed, as well as those in
     * which many keys share a hashCode, so long as they are also
     * Comparable. (If neither of these apply, we may waste about a
     * factor of two in time and space compared to taking no
     * precautions. But the only known cases stem from poor user
     * programming practices that are already so slow that this makes
     * little difference.)
     *

     因为树形节点都是大约两倍于规则节点。当箱子包含足够多的节点以保证使用（请参见TREEIFY_阈值）我们使用他们
    并且当他们变得足够小（以助于删除或者重新安排容量）他们被转化回普通的箱，用户的hashcode会均匀良好的分布，
    树形的箱子很少会被使用，理论上 在随机的hashcode下，频繁的节点在箱子按照泊松分布排列;
    (http://en.wikipedia.org/wiki/Poisson_distribution) 一个参数大约0.5的平均值，默认的重新排列0.75的阈值，虽然有一个大量的变化，因为重新安排容量，
    忽略变化， 期待的 list大小的k值 是 (exp(-0.5) * pow(0.5, k) /factorial(k))
      这个第一个值是
      * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     更多的： 小于1的 一千万个值。


     * Because TreeNodes are about twice the size of regular nodes, we
     * use them only when bins contain enough nodes to warrant use
     * (see TREEIFY_THRESHOLD). And when they become too small (due to
     * removal or resizing) they are converted back to plain bins.  In
     * usages with well-distributed user hashCodes, tree bins are
     * rarely used.  Ideally, under random hashCodes, the frequency of
     * nodes in bins follows a Poisson distribution
     * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
     * parameter of about 0.5 on average for the default resizing
     * threshold of 0.75, although with a large variance because of
     * resizing granularity. Ignoring variance, the expected
     * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
     * factorial(k)). The first values are:
     *
     * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     * more: less than 1 in ten million
     *

        树形箱子的 根节点   是普通的第一个节点， 然而有时候，（当前仅仅在Iterator.remove）
        根可能在别的地方， 但是可以通过父链接（方法TreeNode.root（））恢复。

     * The root of a tree bin is normally its first node.  However,
     * sometimes (currently only upon Iterator.remove), the root might
     * be elsewhere, but can be recovered following parent links
     * (method TreeNode.root()).
     *
       所有的适用的内部得方法接受一个hashcode  作为一个参数（作为普通的来自公共方法的提供）允许他们去相互调用， 没有重新计算用户的hashcode
       大多数的内部方法也接受一个TAB参数，当前表是很普遍的，当重新安排容量或者转化的时候。可能是一个新的或者旧的
     * All applicable internal methods accept a hash code as an
     * argument (as normally supplied from a public method), allowing
     * them to call each other without recomputing user hashCodes.
     * Most internal methods also accept a "tab" argument, that is
     * normally the current table, but may be a new or old one when
     * resizing or converting.
     *
     当箱子的列表都是树形化的时候，分割或者非树形化，我们都让他们保持在同样相对访问/遍历顺序
      (i.e., field Node.next) 去更好的保留他的本地性。并且轻微的简单处理分割和遍历 调用iterator.remove

      在插入的时候去使用comparators，通过再平衡的方式，去保持一个全部的顺序，（或者尽可能靠近这里）
       我们比较类并且标识HASHCODE作为纽带。

     * When bin lists are treeified, split, or untreeified, we keep
     * them in the same relative access/traversal order (i.e., field
     * Node.next) to better preserve locality, and to slightly
     * simplify handling of splits and traversals that invoke
     * iterator.remove. When using comparators on insertion, to keep a
     * total ordering (or as close as is required here) across
     * rebalancings, we compare classes and identityHashCodes as
     * tie-breakers.
     *

    使用 并且在直接转变上对比 存在的子类LinkedHashMap而言 树形模式是复杂的。
    有关定义为在插入时调用的钩子方法，请参见下文
    允许LinkedHashMap内部删除和访问， 否则，它们将保持独立于这些机制
    （这个也是需要一个集合实例去通过许多可以创建新节点的实用方法）
    当前的程序像是 SSA基础的编码格式， 在所有曲折的指针操作中，帮助避免别名错误

     * The use and transitions among plain vs tree modes is
     * complicated by the existence of subclass LinkedHashMap. See
     * below for hook methods defined to be invoked upon insertion,
     * removal and access that allow LinkedHashMap internals to
     * otherwise remain independent of these mechanics. (This also
     * requires that a map instance be passed to some utility methods
     * that may create new nodes.)
     *
     * The concurrent-programming-like SSA-based coding style helps
     * avoid aliasing errors amid all of the twisty pointer operations.
     */


    /**
     * 默认的初始容量，必须要是二的幂次数量。
     */

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16



    /**
     * 最大容量，在两个带参数的构造函数中的任何一个隐式指定了更高的值时使用
     * 必须是2的幂小于等于 1<<30（2乘1073741824）。
     *
     *  *是位移运算符， <<左移运算符，>>右移运算符，还有不带符号的位移运算 >>>
     *      *计算过程以1<<30为例,首先把1转为二进制数字 0000 0000 0000 0000 0000 0000 0000 0001
     *      *然后将上面的二进制数字向左移动30位后面补0得到 0100 0000 0000 0000 0000 0000 0000 0000
     *      *最后将得到的二进制数字转回对应类型的十进制,运行结果为: 2乘1073741824
     *      *>>运算规则：按二进制形式把所有的数字向右移动对应位数，低位移出（舍弃），高位的空位补符号位，即正数补零，负数补1
     *      *>>>运算规则：按二进制形式把所有的数字向右移动对应位数，低位移出（舍弃），高位的空位补零。对于正数来说和带符号右移相同，对于负数来说不同
     */

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 构造函数中未指定时使用的负载因子。
     */

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 箱子的数量阈值是转化树形的结构的阈值,而不是转化链表的。
     * 当添加一个元素到一个箱子中，至少这里面有许多节点，箱子会转化树形（红黑树）
     * 这个数值必须大于2，并且必须至少达到8以符合移除树木时关于收缩后转换回普通箱子的假设。
     *
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * 这个桶的数量的阈值  非树得转化 一个（分割，） 箱子在 一个重新设置容量的操作。
     * 应该是小于TREEIFY_THRESHOLD，并且 最多从 6到网眼与收缩检测下删除。
     *
     * 红黑树转换为链表的阈值。最多为6
     *
     * * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;



    /**
     * 存储箱可树化的最小表容量。（否则，如果bin中的节点太多，则会调整表的大小。
     * 应至少为4*TreeFiy_阈值，以避免调整大小和树化阈值之间的冲突。
     */
    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     *
     *基本散列箱节点，用于大多数条目 （参见下面的TreeNode子类，以及LinkedHashMap中的Entry子类。）
     */

    /**
     * Basic hash bin node, used for most entries.  (See below for
     * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
     */

    static class Node<K,V> implements Map.Entry<K,V> {
        // 静态内部类，node节点，继承于Map.Entry接口
        final int hash;
        //hash 变量 不可变
        final K key;
        // key  不可变
        V value;
        // value
        Node<K,V> next;
        //下一个节点。

        Node(int hash, K key, V value, Node<K,V> next) {
            //有参数构造方法。
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        //获取key值直接返回
        public final V getValue()      { return value; }
        //获取value值直接返回
        public final String toString() { return key + "=" + value; }
        //获取当前节点key和value的全部值输出
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
        //hashcode  对象的hashcode方法（key）异或上对象的hashcode方法(value)。

        public final V setValue(V newValue) {
            // 设置value，参数接入新的value值，返回旧的value值，当前对象value赋值为新值。
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            //equals方法。
            if (o == this)
                //如果传入参数等于当前对象。
                return true;
                //返回true
            if (o instanceof Map.Entry) {
                //如果传入参数是Map.Entry的相关实例。
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                //赋值相关Map.Entry
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    //如果当前对象的key等于entry实例的key并且当前对象的value等于entry实例的value
                    return true;
            }
            return false;
            //否则返回false
        }
    }

    /* ---------------- Static utilities -------------- */


    /**
     *
     * 计算KEY的hashCode() 和 将散列的高位扩展到低位。因为这个表使用了二次幂的伪装。
     * 仅在当前掩码上方的位上变化的哈希集将始终发生冲突。
     *（已知示例中有一组浮动键，它们在小表格中保持连续整数。
     * 所以我们申请一个向下传播高位影响的变换。
     * 在速度和实用性之间需要权衡，并且字节分摊开来。因为许多常见的散列集已经合理地分布了，（所以不要从传播中获益），
     * 因为我们使用树来处理箱子中的大量碰撞.
     * 我们只是以尽可能便宜的方式对一些移位位进行异或运算，以减少系统损耗
     * 以及合并最高位的影响，否则，由于表边界，最高位将永远不会用于索引计算。
     */

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     */
    static final int hash(Object key) {
        int h;
        //变量

        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        //如果KEY为空，返回0 否则-> （调用hashCode()赋值到h） 异或 （h无符号右移16位。）
        //”>>>"表示无符号右移，也叫逻辑右移，即若该数为正，则高位补0，而若该数为负数，则右移后高位同样补0。
    }



    /**
     * 返回X的类，   如果它的形式是“类C实现了Comparable”  否则返回NULL
     * Returns x's Class if it is of the form "class C implements
     * Comparable<C>", else null.
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            //如果是Comparable 实例
            Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
                    //如果是字符串，直接返回C
            if ((ts = c.getGenericInterfaces()) != null) {
                //由此对象表示的类或接口实现的接口不存在
                //那么循环数组， 获取参数类型和真实类型参数不为空。 参数类型为Comparable 参数只有一个，第一个参数为C那么就可以返回。
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                        ((p = (ParameterizedType)t).getRawType() ==
                         Comparable.class) &&
                        (as = p.getActualTypeArguments()) != null &&
                        as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     * 返回k.compareTo(x) 如果x链接KC （k的筛选可比较的类）那么返回0
     */
    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     */
    @SuppressWarnings({"rawtypes","unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable)k).compareTo(x));
    }

    /**
     * 返回给定目标容量的2的幂。
     */

    /**
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        //将CAP传入的值减一。 然后将二进制中的最高非0位后面全置为1，再加一既可以得到大于cap的最小的二次幂数。
        //如果为服务数直接返回1，如果大于等于最大容量，直接返回最大容量，要不然就是加一返回。
       /*
        如果CAP为6
        n = 5时
         n |= n >>> 1;       n |= n >>> 2;      n |= n >>> 4;       n |= n >>> 8;             n |= n >>> 16;
         0000 0101           0000 0111          0000 0111           0000 0111                 0000 0111
         0000 0010           0000 0001          0000 0000           0000 0000                 0000 0000
 结果->   0000 0111           0000 0111          0000 0111           0000 0111                 0000 0111

        0000 0111 加1 等于8


        如果CAP为10
        n = 9时
         n |= n >>> 1;       n |= n >>> 2;      n |= n >>> 4;       n |= n >>> 8;             n |= n >>> 16;
         0000 1001           0000 1101          0000 1111           0000 1111                 0000 1111
         0000 0100           0000 0011          0000 0000           0000 0000                 0000 0000
  结果->  0000 1101           0000 1111          0000 1111           0000 1111                 0000 1111

         0000 1111 加1 等于16


         如果CAP为18
        n = 17时
         n |= n >>> 1;       n |= n >>> 2;      n |= n >>> 4;       n |= n >>> 8;             n |= n >>> 16;
         0001 0001           0001 1001          0001 1111           0001 1111                 0001 1111
         0000 1000           0000 0110          0000 1111           0000 0000                 0000 0000
  结果->  0001 1001           0001 1111          0001 1111           0001 1111                 0001 1111
        0001 1111 加1 等于32

        n=  17   ;   0000 0000  0000 0000  0000 0000  0001 0001
	  n |= n >>> 1;  0000 0000  0000 0000  0000 0000  0001 1001
	  n |= n >>> 2;  0000 0000  0000 0000  0000 0000  0001 1111
	  n |= n >>> 4;  0000 0000  0000 0000  0000 0000  0001 1111
	  n |= n >>> 8;  0000 0000  0000 0000  0000 0000  0001 1111
	  n |= n >>> 16; 0000 0000  0000 0000  0000 0000  0001 1111

      n=  65532   ;  0000 0000  0000 0000  1111 1111  1111 1100
	  n |= n >>> 1;  0000 0000  0000 0000  1111 1111  1111 1110
	  n |= n >>> 2;  0000 0000  0000 0000  1111 1111  1111 1111
	  n |= n >>> 4;  0000 0000  0000 0000  1111 1111  1111 1111
	  n |= n >>> 8;  0000 0000  0000 0000  1111 1111  1111 1111
	  n |= n >>> 16; 0000 0000  0000 0000  1111 1111  1111 1111

       n=   极限值  ; 1000 0000  0000 0000  0000 0000  0000 0000
	  n |= n >>> 1;  1100 0000  0000 0000  0000 0000  0000 0000
	  n |= n >>> 2;  1111 0000  0000 0000  0000 0000  0000 0000
	  n |= n >>> 4;  1111 1111  0000 0000  0000 0000  0000 0000
	  n |= n >>> 8;  1111 1111  1111 1111  0000 0000  0000 0000
	  n |= n >>> 16; 1111 1111  1111 1111  1111 1111  1111 1111


    经过5次计算最后的结果刚好可以填满32位的空间 也就是一个int类型的空间，这就是为什么必须是int类型，且最多只无符号右移16位
    经过5次之后，最高位之后32位内，之后都是1，所以在int正数范围内，是可行的。
        */
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


    /* ---------------- Fields -------------- */



    /**
     * 节点的表，最初加载的时候使用，并且并根据需要调整大小
     * 当分配的时候，长度一直是二的幂次数。
     *（我们在某些操作中也允许长度为零，以允许当前不需要的引导机制。）
     */

    /**
     * The table, initialized on first use, and resized as
     * necessary. When allocated, length is always a power of two.
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     */
    transient Node<K,V>[] table;

    /**
     * Holds cached entrySet(). Note that AbstractMap fields are used
     * for keySet() and values().
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * 此映射中包含的键值映射数。
     */
    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * 此HashMap在结构上被修改的次数结构修改是指更改HashMap中映射数量或以其他方式修改其内部结构的修改 (e.g.,
     * rehash).他的字段用于使HashMap集合视图上的迭代器快速失败。（参见ConcurrentModificationException）.
     */

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    transient int modCount;


    /**
     * 要调整大小的下一个大小值（容量*负载系数）。
     *
     * @serial
     */
    // (序列化后，javadoc描述为true。.
    // 此外，如果尚未分配表数组，则此字段保存初始数组容量，或零表示默认的初始容量.)

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    // (The javadoc description is true upon serialization.
    // Additionally, if the table array has not been allocated, this
    // field holds the initial array capacity, or zero signifying
    // DEFAULT_INITIAL_CAPACITY.)
    int threshold;


    /**
     * The load factor for the hash table.
     *哈希表的加载因子。
     * @serial
     */
    final float loadFactor;

    /* ---------------- Public operations -------------- */


    /**
     *
     *构造一个空hashmap为指定初始化容量和负载因子。
     *
     *
     * @param  initialCapacity the initial capacity 初始化容量
     * @param  loadFactor      the load factor  负载因子
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive    如果初始容量是负值或负载系数是非正值
     */


    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * 用指定的初始容量和默认负载系数（0.75）构造一个空的<tt>HashMap</tt>。
     *
     * @param  initialCapacity 初始容量。
     * @throws IllegalArgumentException if the initial capacity is negative.如果初始容量为负。
     */
    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 用默认初始容量（16）和默认负载系数（0.75）构造一个空的<tt>HashMap</tt>。
     */

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }


    /**
     * 使用与指定的<tt>映射相同的映射构造新的<tt>HashMap</tt>。
     * 创建的<tt>HashMap</tt>具有默认负载因子（0.75）和足够的初始容量，以将映射保存在指定的<tt>Map</tt>中。
     * @param   m 要在此映射中放置其映射的映射
     * @throws  NullPointerException 如果指定的映射为空
     */

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        //使用默认加载因子，然后调用放置putMapEntries
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    /**
     * 实现映射。putAll和Map构造函数。
     * @param m the map
     * @param evict 最初构造此映射时为false，否则为true（在节点插入后转发到方法）。
     */

    /**
     * Implements Map.putAll and Map constructor.
     *
     * @param m the map
     * @param evict false when initially constructing this map, else
     * true (relayed to method afterNodeInsertion).
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        //
        int s = m.size();
        //传输进来的MAP赋值容量大小
        if (s > 0) {
            //如果容量大于0
            if (table == null) { // pre-size
                //当前MAP表为空
                float ft = ((float)s / loadFactor) + 1.0F;
                //ft=传输MAP容量除加载因子加1
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                         (int)ft : MAXIMUM_CAPACITY);
                //如果FT小于最大容量，T等于FT，如果大于最大容量T等于最大容量值。
                if (t > threshold)
                    //如果T大于阈值 返回阈值为大于T的最小的二次幂数
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
                //如果map的容量大于阈值。重新设置容量。
                resize();
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
                //循环设置KEY VALUE
            }
        }
    }


    /**
     *  返回MAP中的KEYVALUE的数字
     *
     * @return the number of key-value mappings in this map
     */

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * 返回TRUE，如果这个集合没有KEY VALUE的映射
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt>      * 返回TRUE，如果这个集合没有KEY VALUE的映射
     */

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * 返回指定键映射到的值，如果此映射不包含该键的映射，则返回{@code null}。
     *  <p>更正式地说，如果此映射包含从键{@code k}到值{@code v}的映射，
     *  使得{@code（key==null？k==null:key.equals（k））
     * 然后该方法返回{@code v} 否则返回{@code null}
     * （最多可以有一个这样的映射。）
     * <p>返回值{@code null}不一定表示映射不包含键的映射；
     * 映射也可能显式地将密钥映射到{@code null}.
     *   {@link#containsKey containsKey}操作可用于区分这两种情况
     *
     * @see #put(Object, Object)
     */

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {
        //获取节点值
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;

    }

    /**
     * 实现映射。Map.get 相关的方法
     * Implements Map.get and related methods.
     *
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    final Node<K,V> getNode(int hash, Object key) {
        //获取节点，先HASH计算找到第一个查询到映射出来的MAP节点
        //    如果第一个节点通过hash等值和KEY等值算到是该节点直接返回
        //    要不然就从第一个节点一直往后查询节点直到查询出来。
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;

        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            //table 表中不为空，有容量值，并计算到第一个table的node也不为空
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                //如果第一个位置的hash等于当前hash值，并且第一个node的KEY也等于对当前KEY，KEY 不为空。就直接返回第一个
                return first;
            if ((e = first.next) != null) {
                //如果查询到的元素下一个节点不为空就继续查询。
                if (first instanceof TreeNode)
                    //如果是树形结构节点实例。直接返回树形节点获取的返回值，
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
                // 要不然就从第一个节点一直往后遍历，如果HSAH值和节点HASH一样，key一样，和当前KEY一样， 并且KEY不为空。那么就返回节点
            }
        }
        return null;
    }




    /**
     * 如果这个MAP 包含一个特殊的kEY 返回true
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     */

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }




    /**
     * 将指定的值与此映射中的指定键相关联。
     * 如果映射以前包含键的映射，则替换旧值。
     * @param key 与指定值关联的键
     * @param value 要与指定键关联的值
     * @return 与键关联的上一个值,或者null值，如果那些没有映射到KEY.（一个null返回值还可以指示先前与key关联的映射null）
     *
     *
     */

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }


    /**
     * Implements Map.put and related methods.
     * 实现了map.put及其相关方法。
     *
     * @param hash hash for key key的HASH值
     * @param key the key key值
     * @param value the value to put  放置的VALUE值
     * @param onlyIfAbsent if true, don't change existing value 如果是true的话，不用改变现存的值。
     * @param evict if false, the table is in creation mode. 如果是FALSE，该表处于创建模式
     * @return previous value, or null if none   以前的值，如果没有，则为null
     */

    /**
     * Implements Map.put and related methods.
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        //判断当前状态是否为空，如果为空重新计算容量，
        //如果计算出的hash值的数组位置为空，在该位置新建一个节点即可。
        //如果在该位置有冲突
        //   如果hash相同，key也相同，那么可以判断为同一份节点，直接替换（KEY具有唯一性）
        //   如果hash计算出来的节点是树状节点实例，那么调用putTreeVal增加树状节点
        //   其他情况就是在尾部增加新节点，如果达到TREEIFY_THRESHOLD(8)阈值，那么会调整树状结构的节点。
        //   加入map之后，根据onlyIfAbsent更改新值返回
        //hash没有冲突的情况下会增加一次结构调整数量和MAP数量，如果大于阈值，会进行再一次的重新计算容量操作。
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //node数组        node节点P      变量n   变量i
        if ((tab = table) == null || (n = tab.length) == 0)
            //先将类的node表赋值到当前tab中 如果为空 或者，tab的长度也赋值给n 长度为0
            //  tab  如果为空 或者 tab的长度为0
            n = (tab = resize()).length;
            //数组列表重新计算容量和长度。 将n设置为重新计算的长度
        if ((p = tab[i = (n - 1) & hash]) == null)
            //将P设为数组列表HASH散列后的数组值如果为空.说明在数组没有hash冲突,就可以根据i的位置在数组上设置相关节点。
            tab[i] = newNode(hash, key, value, null);
        else {
            //hash位置说明有相关冲突。
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                //P的HASH值等于当前hash值，(P的key赋值为K )并且K等于KEY 或者 key不为空，且key等于K值。
                //PUT节点的HASH值等于传入的hash值，或者KEY相等且不为空。
                //keyhash值对比相同， KEY也相同通过EQUALS  对比，说明KEY相同，的话直接替换，保持KEY具有唯一性。
                e = p;
                //直接替换
            else if (p instanceof TreeNode)
                //如果P是TREENODE的相关实例。
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                // p 转化为TREENODE，走putTreeVal方法
            else {
                //hash值不同，KEY也不同。数组表中的位置新加节点。
                //当前区域块中，P为tab HASH后的节点。e为新加入的节点。
                for (int binCount = 0; ; ++binCount) {
                //循环箱子数量
                    if ((e = p.next) == null) {
                        //将P的下一个节点赋值为e ，如果e为空。
                        p.next = newNode(hash, key, value, null);
                        //将P的下一个位置设置为新的节点。
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        //如果箱子循环次数>=树状结构的阈值（8）-1
                        break;
                        //跳出当前循环
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        //PUT节点的HASH值等于传入的hash值，或者KEY相等且不为空。
                        //key的hash值对比相同， KEY也相同通过EQUALS  对比，说明KEY相同。
                        break;
                        //跳出当前循环
                    p = e;
                    //极端情况会将该位置直接替换
                }
            }
            if (e != null) { // existing mapping for key
                //如果e不为空
                V oldValue = e.value;
                //e得值赋值给旧有节点值
                if (!onlyIfAbsent || oldValue == null)
                    //onlyIfAbsent如果为true，则不更改现有值
                    //onlyIfAbsent如果为false，更改现有值 或者,旧有节点（上一个增加节点）为空。
                    e.value = value;
                    //e就赋值
                afterNodeAccess(e);
                //linkhashmap回调方法。
                return oldValue;
                //返回e值。
            }
        }
        ++modCount;
        //增加一次修改hash表的次数。
        if (++size > threshold)
            //增加一次MAP映射数，如果数量大于阈值的话，还会进行一次重新调整容量得操作。
            resize();
        afterNodeInsertion(evict);
        //linkhashmap回调方法。
        return null;
    }


    /**
     * 将表格大小初始化或加倍，如果为空，根据现场阈值中的初始容量目标进行分配。
     * 否则，因为我们正在使用二的幂次扩张，每个存储箱中的元素必须保持相同的索引,
     * 或者在新表中移动二的幂次的偏移量。
     * @return the table
     */

    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     *
     * @return the table
     */
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        //将类中node表赋值到旧有tab中。
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        //如果旧有表为null那么旧有容量为0，要不然旧有的容量就是旧表的容量。
        int oldThr = threshold;
        //将阈值赋值到旧有的阈值变量
        int newCap, newThr = 0;
        //新的cap标记容量，和新的阈值
        if (oldCap > 0) {
            //如果旧有容量大于0
            if (oldCap >= MAXIMUM_CAPACITY) {
                //如果旧有容量大于等于最大容量
                threshold = Integer.MAX_VALUE;
                //阈值为integer的最大值2^31-1
                return oldTab;
                //然后返回旧有表
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                //<<      :     左移运算符，num << 1,相当于num乘以2
                //新的容量为2倍。 如果新的容量小于最大容量，并且旧有的容量大于等于初始容量16
                newThr = oldThr << 1; // double threshold
                //新的阈值赋值为旧有阈值的2倍。
        }
        else if (oldThr > 0) // initial capacity was placed in threshold初始容量设置为阈值
        //如果旧有的阈值大于0
            newCap = oldThr;
            //新的容量等于旧有的阈值。
        else {               // zero initial threshold signifies using defaults零初始阈值表示使用默认值
            newCap = DEFAULT_INITIAL_CAPACITY;
            //新容量为默认初始容量
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
            //新阈值为0.75f*默认初始化容量。
        }
        if (newThr == 0) {
        //如果新的阈值为0
            float ft = (float)newCap * loadFactor;
            //ft为 新容量*hash表的加载银子。
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
            //新的阈值= 新的容量如果小于最大容量，并且》（ft小于最大容量的话就是ft,要不然就是integer的最大容量。）
        }
        threshold = newThr;
        //阈值为新的阈值同步。
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        //节点新表为新建node长度为新的容量大小的数组。
        table = newTab;
        //table值更新为新的tab值。
        if (oldTab != null) {
            //如果旧有得表不为空
            for (int j = 0; j < oldCap; ++j) {
                //循环遍历旧有的容量
                Node<K,V> e;
                //变量E
                if ((e = oldTab[j]) != null) {
                 //赋值e为旧有的J元素,如果他不为空。
                    oldTab[j] = null;
                    //旧有表J元素赋值为NULL
                    if (e.next == null)
                        //如果E的下一个节点为空
                        newTab[e.hash & (newCap - 1)] = e;
                        //把E赋值为新表[e的hash值 与 新表容量-1]
                    else if (e instanceof TreeNode)
                        //如果e是treenode的实例
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                        //e转化为TREEnode吗，并且分割节点。
                    else { // preserve order 维持秩序
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            // next为e的下一个节点。
                            if ((e.hash & oldCap) == 0) {
                                //如果e的hash 与 旧有容量 如果等于0
                                if (loTail == null)
                                    //低链表尾巴为空
                                    loHead = e;
                                    //低链表头节点为e
                                else
                                    loTail.next = e;
                                    ////否则低链表尾节点的下一个节点为e

                                loTail = e;
                                //低链表的尾节点为e
                            }
                            else {
                                // 否则高链表的尾节点为空
                                if (hiTail == null)
                                    // 高链表的尾节点为空
                                    hiHead = e;
                                    // 高链表的头节点为e
                                else
                                    hiTail.next = e;
                                // 高链表的尾节点的下一个节点为e
                                hiTail = e;
                                // 高链表的尾节点为e

                            }
                        } while ((e = next) != null);
                          // e 为next节点，e不为空
                        if (loTail != null) {
                            //如果 //低链表的尾节点不为空
                            loTail.next = null;
                            //低链表的尾节点的下一个节点为空
                            newTab[j] = loHead;
                            //新表的J元素为 低链表的头节点
                        }
                        if (hiTail != null) {
                            //如果  高链表的尾节点不为空
                            hiTail.next = null;
                            //如果  高链表的尾节点的下一个节点为null
                            newTab[j + oldCap] = hiHead;
                            //新表的[J+旧表的容量]的元素为 // 高链表的头节点。
                        }
                    }
                }
            }
        }
        return newTab;
    }


    /**
     * 替换给定散列的索引处bin中的所有链接节点，除非表太小，在这种情况下改为调整大小。
     */
    /**
     * Replaces all linked nodes in bin at index for given hash unless
     * table is too small, in which case resizes instead.
     */
    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            //table 为空，或者 表长度小于 存储箱可树化的最小表容量
            resize();
            //重新计算容量。
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            //计算HASH值获取节点，不为空。
            TreeNode<K,V> hd = null, tl = null;
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            //循环查找该节点后所有链子中的节点，替换成树形节点。
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }



    /**
     /**
     将指定映射中的所有映射复制到此映射。
     这些映射将替换此映射用于的所有映射
     指定映射中当前的任何键。
     @要存储在此映射中的参数m映射
     @如果指定的映射为null，则引发NullPointerException
     */

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        //设置MAP关系到集合中,旧有的和新添加的共存.
        putMapEntries(m, true);
    }


    /**
     *   从该映射中删除指定密钥的映射（如果存在）。
     *
     * @param  key key要从映射中删除其映射的密钥
     * @return 返回与键关联的上一个值，或如果没有键的映射，则为null
     *         （null返回也可以表示映射
     *          *以前将NULL与密钥关联。）
     */



    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */


    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }


    /**
     *
     *实现映射移除和相关方法。
     * @param hash hash for key
     * @param key the key
     * @param value  如果匹配值，则匹配的值，否则忽略
     * @param matchValue 如果为true，则仅在值相等时删除
     * @param movable 如果为false，则在删除时不要移动其他节点
     * @return the node, or null if none
     */


    /**
     * Implements Map.remove and related methods.
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to match if matchValue, else ignored
     * @param matchValue if true only remove if value is equal
     * @param movable if false do not move other nodes while removing
     * @return the node, or null if none
     */
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        //如果当前MAP表不为空，并且长度大于0 并且末尾元素不为空。
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {

            Node<K,V> node = null, e; K k; V v;
            //如果 末尾元素HASH的值等于传输的HASH值,并且KEY值地址相等，字符也相等。那么就让node节点等于P
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            //那么P链表中的NEXT节点不为空
            else if ((e = p.next) != null) {
                //如果P 是树类型节点 以树形节点转化。循环获取（如果hash相等，KEY地址相等，KEY值相等那就将该节点变成NODE）否则一直获取链表后面进行判断。
                if (p instanceof TreeNode)
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                else {
                    do {
                        if (e.hash == hash &&
                            ((k = e.key) == key ||
                             (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            //如果node不为空，并且， （关联VALUE 为FALSE  或者 节点的值等于value 或者   （ value 不为空   并且value 等于V） ）
            if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {
                //如果  是树形节点直接调用树形节点删除方法  如果 NODE 等于P节点，直接 将该值进行链表替换。   否则 不重合，直接将链表替换。
                if (node instanceof TreeNode)
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                //增加操作COUNT  减少一个大小。
                ++modCount;
                --size;
                //调用函数  并返回当前节点。
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }


    /**
     * 从此映射中删除所有映射。
     * 此调用返回后，映射将为空。
     */

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        Node<K,V>[] tab;
        modCount++;
        //添加结果加1
        if ((tab = table) != null && size > 0) {
            //table不为空，并且容量大于0
            size = 0;
            //将容量设置为0
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
            //循环设置所有MAP的位置都设置为NULL
        }
    }

    /**
     * 如果此映射将一个或多个键映射到指定值，则返回true。
     *
     * @param value 要测试其在该地图中的存在的值
     * @return  如果此映射将一个或多个键映射到指定值，则为true
     */

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        //如果MAP 数组不为空 ，元素大于0
        //循环   数组
        //循环   数组元素 的链表， （e不为空）
        //如果value的值等于e的值，  或者  value 不为空，  value值等于e的值  那么返回TRUE  证明在 循环中包含该VALUE
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }



    /**
     * 返回此映射中包含的键的{@link Set}视图。
     * *该集合由MAP支持，因此对MAP的更改不受限制
     * 反映在场景中，反之亦然。如果MAP被修改了
     * 当对集合进行迭代时（通过 迭代器自己的 remove 操作）的结果
     *
     * 迭代没有定义。该套件支持元素移除，
     *
     * 通过
     * Iterator.remove，Set.remove，
     * removeAll，retainAll，以及 clear
     * 操作。它不支持add addAll操作。
     *
     * @return 此MAP中包含的key的集合视图
     */


    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {
        //返回KEYSET如果没有新建，最后返回，
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    final class KeySet extends AbstractSet<K> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<K> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }
        public final Spliterator<K> spliterator() {
            return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super K> action) {
            Node<K,V>[] tab;
            //空指针
            if (action == null)
                throw new NullPointerException();
            //如果长度大于0 并且 数组不为空。   循环数组长度
            // 循环  数组的链表。  接受函数式编程。
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e.key);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }






    /**
     *返回此映射中包含的值的{@link Collection}视图。
     * 集合由映射支持，因此对映射的更改是
     * 反映在收藏中，反之亦然。如果地图是
     * 在对集合进行迭代时修改
     * （除了通过迭代器自己的remove操作），
     * 迭代的结果尚未定义。藏品
     * 支持删除元素，从而删除相应的
     * 通过Iterator.remove，
     * Collection.remove，removeAll，
     * retainAll和clear操作。事实并非如此
     * 支持add或addAll操作。
     */

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a view of the values contained in this map
     */
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    final class Values extends AbstractCollection<V> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<V> iterator()     { return new ValueIterator(); }
        public final boolean contains(Object o) { return containsValue(o); }
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super V> action) {
            // 如果长度大于0 并且 数组不为空。   循环数组长度
            // 循环  数组的链表。  接受函数式编程。
            Node<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e.value);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }




    /**
     返回此映射中包含的值的{@link Collection}视图。
     集合由映射支持，因此对映射的更改是
     反映在收藏中，反之亦然。如果MAP是
     在对集合进行迭代时修改
     （除了通过迭代器自己的remove操作），
     迭代的结果尚未定义。藏品
     支持删除元素，从而删除相应的
     通过Iterator.remove，
     Set.remove，removeAll，
     retainAll和clear操作。事实并非如此
     支持add或addAll操作。
     * @return 返回此映射中包含的值的视图
     */

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }

        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                //判断对象是否是MAPENTRY的实例，如果不是，返回FALSE
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            //获取KEY
            Node<K,V> candidate = getNode(hash(key), key);
            // key hash 根据KEY获取节点。
            return candidate != null && candidate.equals(e);
            //不为空，并且获取得到节点为TRUE
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                //如果O是Map.Entry的实例。
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                //获取KEY和VALUE,并执行removeNode删除节点方法。
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            //分离器 调用EntrySpliterator分离
            return new EntrySpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super Map.Entry<K,V>> action) {
            //遍历。
            Node<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            //判空操作
            if (size > 0 && (tab = table) != null) {
                //非空。
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e);
                }
                //循环遍历所有节点。
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }

    // Overrides of JDK8 Map extension methods

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(hash(key), key, value, true, true);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return removeNode(hash(key), key, value, true, true) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        //根据KEY找到计算HASH找到相关位置，并且检验旧值，找到节点，如果节点不为空，就替换value为新值，不为空返回TRUE 为空，返回false

        Node<K,V> e; V v;
        if ((e = getNode(hash(key), key)) != null &&
            ((v = e.value) == oldValue || (v != null && v.equals(oldValue)))) {
            e.value = newValue;
            afterNodeAccess(e);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        //根据KEY找到计算HASH找到相关位置的节点如果不为空，就替换value值并返回值。
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) != null) {
            V oldValue = e.value;
            e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
        return null;
    }

    @Override
    public V computeIfAbsent(K key,
                             Function<? super K, ? extends V> mappingFunction) {
        if (mappingFunction == null)
            throw new NullPointerException();
        //空检查
        // 容量大于阈值 或者table为空。table长度为0 N为重新计算容量长度。
        //获取第一个节点，如果是树状节点，就将OLD获取树状节点。
        //否则循环，找到HASH相同kEY相同的节点。
        //如果有旧有节点的VALUE直接返回旧有值。
        //如果没有旧有的节点的value，调用函数式接口的计算终止赋值代替。
        //如果是普通节点替换，如果是树形节点有值就替换树形节点，要不然就生成新的节点，大于等于阈值 树形化。


        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            //容量大于阈值 或者table为空。table长度为0
            n = (tab = resize()).length;
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
            V oldValue;
            if (old != null && (oldValue = old.value) != null) {
                afterNodeAccess(old);
                return oldValue;
            }
        }
        V v = mappingFunction.apply(key);
        if (v == null) {
            return null;
        } else if (old != null) {
            old.value = v;
            afterNodeAccess(old);
            return v;
        }
        else if (t != null)
            t.putTreeVal(this, tab, hash, key, v);
        else {
            tab[i] = newNode(hash, key, v, first);
            if (binCount >= TREEIFY_THRESHOLD - 1)
                treeifyBin(tab, hash);
        }
        ++modCount;
        ++size;
        afterNodeInsertion(true);
        return v;
    }

    public V computeIfPresent(K key,
                              BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null)
            throw new NullPointerException();
        //空检查
        //如果 key 对应的 value 不存在，删除这个节点，返回 null，如果存在，则返回通过 remappingFunction 重新计算后的值。
        Node<K,V> e; V oldValue;
        int hash = hash(key);
        if ((e = getNode(hash, key)) != null &&
            (oldValue = e.value) != null) {
            V v = remappingFunction.apply(key, oldValue);
            if (v != null) {
                e.value = v;
                afterNodeAccess(e);
                return v;
            }
            else
                removeNode(hash, key, null, false, true);
        }
        return null;
    }

    @Override
    public V compute(K key,
                     BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null)
            throw new NullPointerException();
//       //检验空指针。
        int hash = hash(key);
        //计算KEY的hash
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            //如果map容量大于阈值，或者table为空，或者table长度为0,n的长度等于重新计算容量后的长度。
            n = (tab = resize()).length;
        if ((first = tab[i = (n - 1) & hash]) != null) {
            //赋值first第一个根据hash查到的节点如果不为空、
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            //如果是树状结构，直接获取树状结构节点。
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
                //循环获取，知道查询到hash相同，key相同的节点。
            }
        }
        V oldValue = (old == null) ? null : old.value;
        //旧有节点如果为空就旧有值就为空，要不然就是OLD的值。
        V v = remappingFunction.apply(key, oldValue);
        //要不然就调用函数式接口，将相关KEYVALUE传参操作方法。。
        //V是通过方法计算出来后的值。
        if (old != null) {
            //如果旧有值不为空
            if (v != null) {
                //v也不为空。。
                old.value = v;
                afterNodeAccess(old);
            }
            else
                removeNode(hash, key, null, false, true);
            //否则就删除节点。
        }
        else if (v != null) {
            //如果V不为空。
            if (t != null)
                t.putTreeVal(this, tab, hash, key, v);
                //如果树状节点不为空，赋值树状节点的值将新的V赋值进去
            else {
                tab[i] = newNode(hash, key, v, first);
                //在i节点添加新节点新的V赋值进去
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, hash);
                //箱子数量到树形阈值8的时候，开始转化树形节点
            }
            ++modCount;
            ++size;
            afterNodeInsertion(true);
        }

        return v;
    }


    /**
     * 合并
     * @param key
     * @param value
     * @param remappingFunction
     * @return
     */
    @Override
    public V merge (K key, V value,
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        //空指针校验，
        if (value == null)
            throw new NullPointerException();
        if (remappingFunction == null)
            throw new NullPointerException();
        //计算hash 初始化。
        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        //如果容量大于阈值或者table为空或者长度为0。那么n数量等于重新计算容量后的长度。
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            n = (tab = resize()).length;
        //如果第一个元素末尾元素有hash不为空，
            //第一个节点为树形节点实例，那么获取树形节点。
            //否则 （第一个节点hash值等于计算hash，并且 k的值等于节点的 KEY值，或者KEY 值相等。）开始从链表往后查节点。如果查到将旧有的值赋值给
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
        }
         //如果找到OLD 值，不等于空，那么走函数式编程。
        // 找不到value值， 赋值value

        //如果v不为空，v赋值oldvalue回调函数。
        //否则删除节点。最后返回v
        if (old != null) {
            V v;
            if (old.value != null)
                v = remappingFunction.apply(old.value, value);
            else
                v = value;
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            }
            else
                removeNode(hash, key, null, false, true);
            return v;
        }
        //value不为空
//           //t不为空，T设置树形结构，否则新建节点，如果大于等于阈值，那么转化树形结构。
        //修改节点数加一。容量加一。尾调用。
        //最后返回value
        if (value != null) {
            if (t != null)
                t.putTreeVal(this, tab, hash, key, value);
            else {
                tab[i] = newNode(hash, key, value, first);
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, hash);
            }
            ++modCount;
            ++size;
            afterNodeInsertion(true);
        }
        return value;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Node<K,V>[] tab;
        if (action == null)
            throw new NullPointerException();
        //如果函数式接口为空，空指针异常。
        if (size > 0 && (tab = table) != null) {
            //如果容量大于0，table不为空。
            int mc = modCount;
            //结构修改至赋值
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next)
                    action.accept(e.key, e.value);
                //循环遍历，让他们接受两个参数，KEY VALUE
            }
            if (modCount != mc)
                throw new ConcurrentModificationException();
            //如果结构修改数不等MC抛出异常。
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        //接受函数式编程接口，使用函数式编程，使得函数式编程循环，操作或者可以替换相应的KEY 或者VALUE值
        Node<K,V>[] tab;
        if (function == null)
            throw new NullPointerException();
        if (size > 0 && (tab = table) != null) {
            int mc = modCount;
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    e.value = function.apply(e.key, e.value);
                }
            }
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }

    /* ------------------------------------------------------------ */
    // Cloning and serialization
    //克隆和序列化

    /**
     *返回HashMap实例的浅层副本：不会克隆键和值本身。
     *
     * @return a shallow copy of this map
     */

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        HashMap<K,V> result;
        try {
            result = (HashMap<K,V>)super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        result.reinitialize();
        //重新初始化状态。
        result.putMapEntries(this, false);
        //放置MAP到result中。
        return result;
    }

    // These methods are also used when serializing HashSets
    //序列化哈希集时也使用这些方法
    final float loadFactor() { return loadFactor; }

    final int capacity() {
        return (table != null) ? table.length :
                (threshold > 0) ? threshold :
                        DEFAULT_INITIAL_CAPACITY;
        //如果表为空，返回table长度0
        //如果表不为空、 阈值大于0，返回当前阈值，否则是默认初始化容量。
    }

    /**
     * 将<tt>HashMap</tt>实例的状态保存到流中（即序列化它）。
     *
     * @serialData 发出HashMap的<i>容量（bucket数组的长度）（int），
     * 然后是<i>大小（int，键值映射的数量），然后是每个键值映射的键（Object）和值（Object）。
     * 键值映射不按特定顺序发出。
     */

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the
     *             bucket array) is emitted (int), followed by the
     *             <i>size</i> (an int, the number of key-value
     *             mappings), followed by the key (Object) and value (Object)
     *             for each key-value mapping.  The key-value mappings are
     *             emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        int buckets = capacity();
        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();
        s.writeInt(buckets);
        s.writeInt(size);
        internalWriteEntries(s);
    }


    /**
     * 从流中重新构造此映射（即，反序列化它）。
     * @param s the stream
     * @throws ClassNotFoundException 如果找不到序列化对象的类
     * @throws IOException  如果发生I/O错误
     */

    /**
     * Reconstitutes this map from a stream (that is, deserializes it).
     * @param s the stream
     * @throws ClassNotFoundException if the class of a serialized object
     *         could not be found
     * @throws IOException if an I/O error occurs
     */
    private void readObject(java.io.ObjectInputStream s)
        //反序列化读取hashmap.
        throws IOException, ClassNotFoundException {
        // Read in the threshold (ignored), loadfactor, and any hidden stuff
        //读取阈值（忽略）、加载因子和任何隐藏内容
        s.defaultReadObject();
        reinitialize();
        //重新加载初始化配置。
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new InvalidObjectException("Illegal load factor: " +
                                             loadFactor);
        //加载银子小于等于0或者 加载银子不是浮点型，抛出异常 。

        s.readInt();                // Read and ignore number of buckets //读取并忽略存储桶数
        int mappings = s.readInt(); // Read number of mappings (size) //读取映射数（大小）
        if (mappings < 0)    //如果映射大小小于0
            throw new InvalidObjectException("Illegal mappings count: " +
                                             mappings);
        //  抛出异常。
        else if (mappings > 0) { // (if zero, use defaults)  （如果为零，则使用默认值）
            // Size the table using given load factor only if within
            // 仅当在以下范围内时，才使用给定的负载系数调整表的大小
            // 范围为0.25。。。4
            float lf = Math.min(Math.max(0.25f, loadFactor), 4.0f);
             //  将0.25和加载因子比较较大值。大值和4相比，谁小就是LF
            float fc = (float)mappings / lf + 1.0f;
            //  fc值 是映射值除lf 加上1。
            int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ?
                       DEFAULT_INITIAL_CAPACITY :
                       (fc >= MAXIMUM_CAPACITY) ?
                       MAXIMUM_CAPACITY :
                       tableSizeFor((int)fc));
            //如果FC小于默认容量那么容量就是默认容量。否则就是—— fc 大于等于 最大容量 就是最大容量，要不然就是大于当前容量的最小二次幂数。
            float ft = (float)cap * lf;
            //ft  等于（二次幂）容量乘以加载因子比较后的值。
            threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ?
                         (int)ft : Integer.MAX_VALUE);
            //阈值 等于  容量小于最大容量，并且 ft小于最大容量值 那就是ft 要么就是最大的integer值。

            // Check Map.Entry[].class since it's the nearest public type to
            // what we're actually creating.
            //检查Map.Entry[].class ，因为它是最接近我们实际创建的公共类型。
            SharedSecrets.getJavaOISAccess().checkArray(s, Map.Entry[].class, cap);
            @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] tab = (Node<K,V>[])new Node[cap];
            //
            table = tab;
            //创建一定容量节点数组，赋值table。
            // Read the keys and values, and put the mappings in the HashMap
            //读取键和值，并将映射放入HashMap中
            for (int i = 0; i < mappings; i++) {
                @SuppressWarnings("unchecked")
                    K key = (K) s.readObject();
                @SuppressWarnings("unchecked")
                    V value = (V) s.readObject();
                putVal(hash(key), key, value, false, false);
            }
            //循环mapping读取KEY和VALUE，并放到hashmap中。
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class HashIterator {
        Node<K,V> next;        // next entry to return 下一个要返回的条目
        Node<K,V> current;     // current entry  当前条目
        int expectedModCount;  // for fast-fail 快速失败
        int index;             // current slot  当前插槽

        HashIterator() {
            //构造方法。
            expectedModCount = modCount;
            Node<K,V>[] t = table;
            current = next = null;
            index = 0;
            if (t != null && size > 0) { // advance to first entry
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            //提前到第一个入口
        }

        public final boolean hasNext() {
            return next != null;
            //下一个循环节点不为空也就是有下一个节点。
        }

        final Node<K,V> nextNode() {
            Node<K,V>[] t;
            Node<K,V> e = next;
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();

            if ((next = (current = e).next) == null && (t = table) != null) {
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            //下一个节点等于NULL，并且table不为空。
                //直到当前索引小于当前长度，并且下一个节点为空。
            //否则一直返回下一个节点。
            return e;
        }

        public final void remove() {
            Node<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            K key = p.key;
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
            //当前值赋值为空，将当前KEY找到并删除节点。将当前结构修改次数同步。
        }
    }

    final class KeyIterator extends HashIterator
        implements Iterator<K> {
        public final K next() { return nextNode().key; }
    }

    final class ValueIterator extends HashIterator
        implements Iterator<V> {
        public final V next() { return nextNode().value; }
    }

    final class EntryIterator extends HashIterator
        implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }
    //key  value  entry  hash 都实现了 Iterator，就可以根据方法遍历。KEY VALUE  entry  hash

    /* ------------------------------------------------------------ */
    // spliterators

    static class HashMapSpliterator<K,V> {
        final HashMap<K,V> map;
        Node<K,V> current;          // current node 当前节点
        int index;                  // current index, modified on advance/split 当前索引，在提前/拆分时修改
        int fence;                  // one past last index 最后一个索引
        int est;                    // size estimate  规模估算
        int expectedModCount;       // for comodification checks 用于共修改检查


        HashMapSpliterator(HashMap<K,V> m, int origin,
                           int fence, int est,
                           int expectedModCount) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getFence() { // initialize fence and size on first use  第一次使用时初始化围栏和大小

            int hi;
            if ((hi = fence) < 0) {
                HashMap<K,V> m = map;
                est = m.size;
                expectedModCount = m.modCount;
                Node<K,V>[] tab = m.table;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            //如果围栏小于0,hashmap大小，和table容量以及围栏长度。
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return (long) est;
            //估计尺寸。返回HASHMAP大小。
        }
    }

    static final class KeySpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<K> {
        KeySpliterator(HashMap<K,V> m, int origin, int fence, int est,
                       int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public KeySpliterator<K,V> trySplit() {
            // 这里的分割方法只是把当前迭代器的开始索引和最后索引除以二而已
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            // 需要遍历的元素个数 est 也需要除以二
            return (lo >= mid || current != null) ? null :
                new KeySpliterator<>(map, lo, index = mid, est >>>= 1,
                                        expectedModCount);
         //尝试分离， 获取围栏，
        }

        public void forEachRemaining(Consumer<? super K> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.key);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        K k = current.key;
                        current = current.next;
                        action.accept(k);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    static final class ValueSpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<V> {
        ValueSpliterator(HashMap<K,V> m, int origin, int fence, int est,
                         int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public ValueSpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new ValueSpliterator<>(map, lo, index = mid, est >>>= 1,
                                          expectedModCount);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.value);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        V v = current.value;
                        current = current.next;
                        action.accept(v);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
        }
    }

    static final class EntrySpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(HashMap<K,V> m, int origin, int fence, int est,
                         int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public EntrySpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new EntrySpliterator<>(map, lo, index = mid, est >>>= 1,
                                          expectedModCount);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K,V>> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K,V>> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        Node<K,V> e = current;
                        current = current.next;
                        action.accept(e);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    /* ------------------------------------------------------------ */
    // LinkedHashMap support


    /*
     * The following package-protected methods are designed to be
     * overridden by LinkedHashMap, but not by any other subclass.
     * Nearly all other internal methods are also package-protected
     * but are declared final, so can be used by LinkedHashMap, view
     * classes, and HashSet.
     */

    // Create a regular (non-tree) node
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }

    // For conversion from TreeNodes to plain nodes
    Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        return new Node<>(p.hash, p.key, p.value, next);
    }

    // Create a tree bin node
    TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        return new TreeNode<>(hash, key, value, next);
    }

    // For treeifyBin
    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {
        return new TreeNode<>(p.hash, p.key, p.value, next);
    }

    /**
     * Reset to initial default state.  Called by clone and readObject.
     */
    void reinitialize() {
        table = null;
        entrySet = null;
        keySet = null;
        values = null;
        modCount = 0;
        threshold = 0;
        size = 0;
    }

    // Callbacks to allow LinkedHashMap post-actions
    //允许LinkedHashMap post操作的回调

    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }

    // Called only from writeObject, to ensure compatible ordering.
    void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
        Node<K,V>[] tab;
        if (size > 0 && (tab = table) != null) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    s.writeObject(e.key);
                    s.writeObject(e.value);
                }
            }
        }
    }

    /* ------------------------------------------------------------ */
    // Tree bins

    /**
     * Entry for Tree bins. Extends LinkedHashMap.Entry (which in turn
     * extends Node) so can be used as extension of either regular or
     * linked node.
     */
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }

        /**
         * Returns root of tree containing this node.
         */
        final TreeNode<K,V> root() {
            for (TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

        /**
         * Ensures that the given root is the first node of its bin.
         */
        static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {
                int index = (n - 1) & root.hash;
                TreeNode<K,V> first = (TreeNode<K,V>)tab[index];
                if (root != first) {
                    Node<K,V> rn;
                    tab[index] = root;
                    TreeNode<K,V> rp = root.prev;
                    if ((rn = root.next) != null)
                        ((TreeNode<K,V>)rn).prev = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)
                        first.prev = root;
                    root.next = first;
                    root.prev = null;
                }
                assert checkInvariants(root);
            }
        }

        /**
         * Finds the node starting at root p with the given hash and key.
         * The kc argument caches comparableClassFor(key) upon first use
         * comparing keys.
         */
        final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
            TreeNode<K,V> p = this;
            do {
                int ph, dir; K pk;
                TreeNode<K,V> pl = p.left, pr = p.right, q;
                if ((ph = p.hash) > h)
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if (pl == null)
                    p = pr;
                else if (pr == null)
                    p = pl;
                else if ((kc != null ||
                          (kc = comparableClassFor(k)) != null) &&
                         (dir = compareComparables(kc, k, pk)) != 0)
                    p = (dir < 0) ? pl : pr;
                else if ((q = pr.find(h, k, kc)) != null)
                    return q;
                else
                    p = pl;
            } while (p != null);
            return null;
        }

        /**
         * Calls find for root node.
         */
        final TreeNode<K,V> getTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }

        /**
         * Tie-breaking utility for ordering insertions when equal
         * hashCodes and non-comparable. We don't require a total
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                (d = a.getClass().getName().
                 compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                     -1 : 1);
            return d;
        }

        /**
         * Forms tree of the nodes linked from this node.
         */
        final void treeify(Node<K,V>[] tab) {
            TreeNode<K,V> root = null;
            for (TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K,V> p = root;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
        }

        /**
         * Returns a list of non-TreeNodes replacing those linked from
         * this node.
         */
        final Node<K,V> untreeify(HashMap<K,V> map) {
            Node<K,V> hd = null, tl = null;
            for (Node<K,V> q = this; q != null; q = q.next) {
                Node<K,V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        /**
         * Tree version of putVal.
         */
        final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                       int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            TreeNode<K,V> root = (parent != null) ? root() : this;
            for (TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                          (kc = comparableClassFor(k)) == null) ||
                         (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                             (q = ch.find(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                             (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    Node<K,V> xpn = xp.next;
                    TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.prev = xp;
                    if (xpn != null)
                        ((TreeNode<K,V>)xpn).prev = x;
                    moveRootToFront(tab, balanceInsertion(root, x));
                    return null;
                }
            }
        }

        /**
         * Removes the given node, that must be present before this call.
         * This is messier than typical red-black deletion code because we
         * cannot swap the contents of an interior node with a leaf
         * successor that is pinned by "next" pointers that are accessible
         * independently during traversal. So instead we swap the tree
         * linkages. If the current tree appears to have too few nodes,
         * the bin is converted back to a plain bin. (The test triggers
         * somewhere between 2 and 6 nodes, depending on tree structure).
         */
        final void removeTreeNode(HashMap<K,V> map, Node<K,V>[] tab,
                                  boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            TreeNode<K,V> first = (TreeNode<K,V>)tab[index], root = first, rl;
            TreeNode<K,V> succ = (TreeNode<K,V>)next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null
                || (movable
                    && (root.right == null
                        || (rl = root.left) == null
                        || rl.left == null))) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            TreeNode<K,V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                TreeNode<K,V> sr = s.right;
                TreeNode<K,V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    TreeNode<K,V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                TreeNode<K,V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }

        /**
         * Splits nodes in a tree bin into lower and upper tree bins,
         * or untreeifies if now too small. Called only from resize;
         * see above discussion about split bits and indices.
         *
         * @param map the map
         * @param tab the table for recording bin heads
         * @param index the index of the table being split
         * @param bit the bit of hash to split on
         */
        final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
            TreeNode<K,V> b = this;
            // Relink into lo and hi lists, preserving order
            TreeNode<K,V> loHead = null, loTail = null;
            TreeNode<K,V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (TreeNode<K,V> e = b, next; e != null; e = next) {
                next = (TreeNode<K,V>)e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                }
                else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR
        //红黑树方法，全部改编自CLR


        static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root,
                                              TreeNode<K,V> p) {
            TreeNode<K,V> r, pp, rl;
            //红黑树左旋：父结点被自己的右孩子取代，而自己成为自己的左孩子。（父子节点互换，原父节点为原父节点的右孩子的左孩子 ）
            if (p != null && (r = p.right) != null) {//P节点的右节点不为空，否则左旋没意义。
                if ((rl = p.right = r.left) != null) // P节点的左节点不为空， 将P节点的左节点赋值为P节点的右节点
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null) //原根节点就是P，然后根节点不能是红色。
                    (root = r).red = false;
                else if (pp.left == p)//p为p的父节点的左孩子节点   p节点的右孩子（r节点）为p的父节点的左孩子
                    pp.left = r;
                else          //要不然  p节点的右孩子（r节点）为p的父节点的右孩子
                    pp.right = r;
                r.left = p; //r的左孩子为p
                p.parent = r;//p的父亲为r =>父子节点互换，原父节点为原父节点的右孩子的左孩子
            }
            return root; //返回根节点
        }

        static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root,
                                               TreeNode<K,V> p) {
            TreeNode<K,V> l, pp, lr;
            //父结点被自己的左孩子取代，而自己成为自己的右孩子。（父子节点互换，原父节点为原父节点的左孩子的右孩子 ）
            if (p != null && (l = p.left) != null) {//P节点的左节点不为空，否则右旋没意义。
                if ((lr = p.left = l.right) != null)// P节点的右节点不为空， 将P节点的右节点赋值为P节点的左节点
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null) //原根节点就是P，然后根节点不能是红色。
                    (root = l).red = false;
                else if (pp.right == p) //p为p的父节点的右孩子节点   p节点的左孩子（l节点）为p的父节点的右孩子
                    pp.right = l;
                else         //要不然  p节点的左孩子（l节点）为p的父节点的右孩子
                    pp.left = l;
                l.right = p; //r的右孩子为p
                p.parent = l;//p的父亲为l =>父子节点互换，原父节点为原父节点的左孩子的右孩子
            }
            return root;//返回根节点
        }

        static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root,
                                                    TreeNode<K,V> x) {
            //红黑树的插入树后，需要平衡节点。该方法 主要是颜色的平衡。

            x.red = true;
            for (TreeNode<K,V> xp, xpp, xppl, xppr;;) {
                //每一次循环修改三代颜色，将爷爷强制改成红色，如果爷爷节点为根节点，再修改成黑色节点。
                if ((xp = x.parent) == null) {//xp为插入节点的父节点。//判断当前插入节点x的父节点是否为空，如果为空 ， 也就是说X为根节点，根节点为黑色。返回根节点。
                    x.red = false;//设置黑色。返回X节点
                    return x;
                }
                else if (!xp.red || (xpp = xp.parent) == null)// 如果（xp为插入节点的父节点。）x的父节点为黑色 或者  xpp=xp的父节点为空。直接返回根节点
                    return root;//返回根节点
                if (xp == (xppl = xpp.left)) { //xp等于x的爷爷节点的左孩子
                    if ((xppr = xpp.right) != null && xppr.red) {//赋值xppr为XPP的右节点 判断不为空，并且XPPR为红色
                        xppr.red = false; //xppr为黑色，xp为黑色 ,xpp为红色
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {//否则
                        if (x == xp.right) {//判断X是不是XP的右节点。
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) { //如果XP不为空。
                            xp.red = false; //把XP变成黑色节点
                            if (xpp != null) { //如果Xpp不为空  xpp变成红色，并把xpp右旋。
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                else {//xp等于x的爷爷节点的右孩子
                    if (xppl != null && xppl.red) {//如果左叔叔不为空并且左叔叔为红色
                        xppl.red = false; // 设置左叔叔为黑色
                        xp.red = false;  //父节点为黑色。
                        xpp.red = true;  //爷爷节点为红色
                        x = xpp; //爷爷作为下一次的孙子，进入下一次循环。
                    }
                    else {//如果左叔叔为空或者左叔叔不为红色。
                        if (x == xp.left) { //如果x是xp 的左孩子
                            root = rotateRight(root, x = xp);//将xp带入 然后右旋，
                            xpp = (xp = x.parent) == null ? null : xp.parent; //重新处理爷爷父亲和儿子的节点关系。
                        }
                        if (xp != null) { //XP 不为空
                            xp.red = false;  //xp为黑色
                            if (xpp != null) { //如果XPP不为空
                                xpp.red = true; //XPP 为红色
                                root = rotateLeft(root, xpp);  //然后根据XPP节点左旋。
                            }
                        }
                    }
                }
            }
        }

        static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root,
                                                   TreeNode<K,V> x) {
            for (TreeNode<K,V> xp, xpl, xpr;;) {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                            (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                    null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                }
                else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                            (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                    null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K,V> boolean checkInvariants(TreeNode<K,V> t) {
            TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
                tb = t.prev, tn = (TreeNode<K,V>)t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }
    }

}
