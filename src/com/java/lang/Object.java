/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

/**
 * 是所有类的根节点
 * 每个类的超类，（所有对象，包括数组，实现此类的方法）
 *
 */




/**
 * Class {@code Object} is the root of the class hierarchy.
 * Every class has {@code Object} as a superclass. All objects,
 * including arrays, implement the methods of this class.
 *
 * @author  unascribed
 * @see     java.lang.Class
 * @since   JDK1.0
 */
public class Object {

    private static native void registerNatives();
    static {
        registerNatives();
    }
    /**
     * 返回OBJECT的运行时的class,返回的CLASS对象，是被synchronized方法上锁并作为class返回的对象，
     * 它的实际上的返回结果类型是{@code Class<? extends |X|>} ,getClass在哪被调用调用， {@code |X|}是静态类型的擦除的表达式
     * 举个例子，此代码片段中不需要强制转换
     * Number n = 0;
     * Class<? extends Number> c = n.getClass();
     * @return 这个{@code Class} OBJECT 代表了运行时的对象的CLASS
     */

    /**
     * Returns the runtime class of this {@code Object}. The returned
     * {@code Class} object is the object that is locked by {@code
     * static synchronized} methods of the represented class.
     *
     * <p><b>The actual result type is {@code Class<? extends |X|>}
     * where {@code |X|} is the erasure of the static type of the
     * expression on which {@code getClass} is called.</b> For
     * example, no cast is required in this code fragment:</p>
     *
     * <p>
     * {@code Number n = 0;                             }<br>
     * {@code Class<? extends Number> c = n.getClass(); }
     * </p>
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     * @jls 15.8.2 Class Literals
     */


    public final native Class<?> getClass();

    /**
     * 返回一个对象的hashcode值。这个方法是对于hash表有支持的好处。 比如hashcode由{@link java.util.HashMap}所规定。
     *
     *这个普通的{@code hashCode}协议是:
     * 在一个java应用执行期间,无论什么时候它被调用同样的对象多次，这个{@code hashCode}方法必须总是返回出来同样的数字。
     * 在{@code equals}比较中使用的对象已修改的情况下，不提供任何信息,
       这个integer从一次执行一个应用到同样的应用的另一个执行不需要一致。
      如果两个对象相等的话，按照 {@code equals(Object)}方法，然后调用这个hashcode方法，在每两个对象之间必须产生出同样的integer结果。
     如果两个对象不相等，根据这个{@link java.lang.Object#equals(java.lang.Object)}方法的话，这是没有要求的
     然后调用{@code hashCode}方法在每两个对象之间必须产生出不同的integer结果。
     然而，这个程序员应该知道生产不同integer结果对于不相等对象可以提高hash表的性能。

    尽可能多的合理性和可行性，这个hashcode方法被class{@code Object}定义，返回不一样的整数对象。
     通过 将对象的内部地址转换为整数是典型的实现 方式。 但是这个实现技巧不是被java程序语言所需要的。




    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.lang.System#identityHashCode
     */





    public native int hashCode();

    /**
     *标识是否一些其他对象是“等于”某个对象。
     *
     * {@code equals}方法实现一个非空对象引用上的等价关系：
     *      自反性：对于任何非空引用值 {@code x},
     *             {@code x.equals(x)} 应该返回TRUE。
     *      对称性： 对于任何非空引用值{@code x}和{@code y},{@code x.equals(y)}应该返回TRUE
     *              当且仅当{@code y.equals(x)} 返回true
     *      传递性：  对于非空引用值{@code x}, {@code y}和 {@code z},
     *               如果{@code x.equals(y)} returns {@code true}
     *               {@code y.equals(z)} returns {@code true}
     *               然后{@code x.equals(z)} should return {@code true}
     *      一致性：  对于任何非空引用值 {@code x} and {@code y},
     *               多次调用 {@code x.equals(y)}一致返回TRUE,或者一致返回FALSE。
     *               使用{@code equals}比较对象的修改，没有提供任何信息
     *      空指针为FALSE:
     *               对于任何非空引用值，{@code x.equals(null)}应该返回false
     {@code equals} 方法对于{@code Object}的类实现在对象上最有区别的可能等价关系



     */


    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     * @see     #hashCode()
     * @see     java.util.HashMap
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }




    /**
     *      创建和返回一个对象的拷贝。“复制”的确切含义可能取决于对象的类别。
     *      总体的意图。对于任何对象 {@code x}表达式
     *      x.clone() != x
     *      将要变成TRUE，并且这个表达式
     *      x.clone().getClass() == x.getClass()
     *      会变成true,但是并不是绝对的要求。
     *      而通常情况下
     *      x.clone().equals(x)也会变成TRUE，
     *      这个类和他的所有超类（除了{@code Object}）遵守这个公约，他会变成是{@code x.clone().getClass() == x.getClass()}
     *      按照约定，这个方法返回的对象应该是独立的（正在被克隆的对象）。为了实现它的独立性，
     *      对象在返回之前,它可能有必要去修改一个对象字段或者更多的对象字段，由{@code super.clone}返回。
     *      典型的，也就是说，复制任何易变的对象，来组成正在被克隆并且代替引用这些对象和引用这些复制的对象内部的“深层次结构”
     *       如果一个类包含了仅仅是原始字段或对不可变对象的引用，
     *       通常，被{@code super.clone}返回的对象中没有字段需要去修改的。
     *       对于{@code Object}类的{@code clone} 方法 执行了一个克隆操作。
     *       首先，如果这个对象的类没有实现{@code Cloneable}接口,然后一个{@code CloneNotSupportedException} 被抛出，
     *       提示所有的数组都被认为实现了{@code Cloneable}接口并且数组类型{@code T[]}的{@code clone}方法的返回类型是{@code T[]}，                                                                1`
     *       {@code T[]}T的位置是任何引用或原始类型
     *       否则。这个方法就会创建一个新的的OBJECT的CLASS的实例并且初始化所有与此对象对应字段的内容完全相同的字段。
     *        像是被指派的，字段的内容本身不是克隆的。因此。这个方法表现了一个对象的“浅复制”,不是一个“深复制”操作。
     *       这个{@code Object}本身不实现接口{@code Cloneable},所以调用这个 {@code clone}方法在类为{@code object}的对象上，将导致出现引发运行时异常
     *
     *       * @return     一个克隆的实例
     *       * @throws  CloneNotSupportedException
     *       如果对象的class不支持{@code Cloneable}接口，子类重写{@code clone}可能也抛出错误表明一个实例不能被克隆。
     *
     */


    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return     a clone of this instance.
     * @throws  CloneNotSupportedException  if the object's class does not
     *               support the {@code Cloneable} interface. Subclasses
     *               that override the {@code clone} method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;



    /**
      返回一个字符串对象的陈述。通常，the {@code toString}方法返回一个字符串对象的“文本表示”。
     这个结果应该是简明但信息丰富的表达，便于人们阅读。
     这个是被推荐的全部子类重写的方法。
     对于 {@code Object}class的 {@code toString}方法返回一个字符串，返回一个字符串，该字符串由对象为实例的类的名称组成
     @字符`{@code @}'和对象哈希代码的无符号十六进制表示形式
     换句话说，此方法返回的字符串等于：
     @return  a string representation of the object.   一个陈述对象的字符串

     */

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }




    /**
     *
     *唤醒正在该对象监视器上等待的单个线程.
     *如果任何线程正在等待这个对象，他们的其中一个会被选中被唤醒,选择是任意的，由实现决定
     *一个线程等待对象的监听器通过调用其中{@code wait}方法。
     *被唤醒的线程将无法继续，直到当前线程放弃对该对象的锁定
     *被唤醒的线程将以通常的方式与任何其他可能积极竞争以同步该对象的线程进行竞争
     *例如，被唤醒的线程在成为下一个锁定该对象的线程方面没有可靠的特权或劣势
     *
     *只有一个现成在一个时间可以占有一个对象的监视器
     *抛出  IllegalMonitorStateException -- 如果当前线程不是对象监视器的拥有者
     *也可以看看：
     *notifyAll(), wait()
     *
     *这个方法应该仅仅被一个线程调用，对这个对象的监视器占有。
     *线程通过以下三种方式之一成为对象监视器的所有者:
     *   通过执行一个同步的实例对象方法。
     *   通过执行{@code synchronized}语句的主体,在对象上同步。
     *   对于对象的类型{@code Class,}通过执行一个类的同步静态方法
     *一个线程在一个时间只能够占有一个对象的监视器。
     *
     * @throws  IllegalMonitorStateException   如果当前线程不是对象监视器的所有者
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     *
     */

    /**
     * Wakes up a single thread that is waiting on this object's
     * monitor. If any threads are waiting on this object, one of them
     * is chosen to be awakened. The choice is arbitrary and occurs at
     * the discretion of the implementation. A thread waits on an object's
     * monitor by calling one of the {@code wait} methods.
     * <p>
     * The awakened thread will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened thread will
     * compete in the usual manner with any other threads that might be
     * actively competing to synchronize on this object; for example, the
     * awakened thread enjoys no reliable privilege or disadvantage in being
     * the next thread to lock this object.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. A thread becomes the owner of the
     * object's monitor in one of three ways:
     * <ul>
     * <li>By executing a synchronized instance method of that object.
     * <li>By executing the body of a {@code synchronized} statement
     *     that synchronizes on the object.
     * <li>For objects of type {@code Class,} by executing a
     *     synchronized static method of that class.
     * </ul>
     * <p>
     * Only one thread at a time can own an object's monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     */
    public final native void notify();



    /**
     *
     *
     * 唤醒全部线程让它们等待对象监视器。 一个线程等待一个对象监视器，通过调用对象的其中之一的{@code wait}方法
     * 被唤醒的线程将无法继续直到当前线程放弃在对象上的锁。
     * 被唤醒的线程将以通常的方式与任何其他线程竞争,可能活跃的完成对象的同步。
     * 举例子，唤醒的线程在成为下一个锁定此对象的线程时没有可靠的特权或缺点
     * 此方法只能由作为此对象监视器所有者的线程调用,对于一方式的形容，
     * 看到{@code notify}方法描述了线程成为监视器所有者的方式，
     *  * @throws  IllegalMonitorStateException   如果当前线程不是对象监视器的持有者。
     *      * @see        java.lang.Object#notify()
     *      * @see        java.lang.Object#wait()
     */




    /**
     * Wakes up all threads that are waiting on this object's monitor. A
     * thread waits on an object's monitor by calling one of the
     * {@code wait} methods.
     * <p>
     * The awakened threads will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened threads
     * will compete in the usual manner with any other threads that might
     * be actively competing to synchronize on this object; for example,
     * the awakened threads enjoy no reliable privilege or disadvantage in
     * being the next thread to lock this object.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     */
    public final native void notifyAll();





    /**
     *
     *
     * 由于当前线程等待直到另一个线程调用{@link java.lang.Object#notify()}方法或者
     * 对于对象的 {@link java.lang.Object#notifyAll()}方法，或者经过了指定的时间
     * 当前线程必须持有对象的监视器
     * 这个方法由于当前线程（调用它的T）将自身置于此对象的等待集中，然后放弃并且在对象上主张同步。
     * 线程T出于线程调度目的而禁用，并处于休眠状态
     * 直到其中四个之一发生：
     * 1一些其他线程调用{@code notify}方法对于这个对象和线程T,碰巧被任意选择为要唤醒的线程.
     * 2一些其他线程对于对象调用{@code notifyAll}方法，
     * 3一些其他线程{@linkplain Thread#interrupt() interrupts} 线程T
     * 4指定的实时时间已过，或多或少，如果{@code timeout}为0,那么就没有考虑到实时性，线程只是等待直到通知。
     * 线程T然后从该对象的等待集中删除，并重新启用线程调度 然后，它以通常的方式与其他线程竞争在对象上同步的权限,
     * 一旦收获到了对象的控制权，它对对象的所有同步声明都恢复到原来的状态，即对于这个调用{@code wait}方法时的情况
     * 因此，从这个{@code wait}方法返回，对象和线程{@code T}的同步状态与调用{@code wait}方法时完全相同。
     * 一个线程也能在没有通知的情况下被唤醒。中断或者超时，一个所谓的虚假唤醒。而这在实践中很少发生。
     * 应用程序必须通过测试导致线程被唤醒的条件来防范它,如果条件不满足，则继续等待
     * 换句话说，等待应该一直循环发生，就像是这个一样。
     *  synchronized (obj) {
     *               while (&lt;condition does not hold&gt;)
     *                   obj.wait(timeout);
     *               ... // Perform action appropriate to condition
     *           }
     *
     *（有关此主题的更多信息，请参阅Doug Lea的“Java并发编程（第二版）”
     * （Addison Wesley，2000）中的第3.2.3节，或Joshua Bloch的“有效Java编程语言指南”
     * （Addison Wesley，2001）中的第50项）。
     * 如果当前线程被{@linkplain java.lang.Thread#interrupt() interrupted}任何线程之前或者当它正在等待中。
     * 然后一个{@code InterruptedException}被抛出来。这个错误是不会被抛出直到如上所述恢复此对象的锁定状态
     * 注释:表示{@code wait}方法，它将当前线程放入此对象的等待集中,仅仅释放这个对象。当线程等待时，
     * 可以同步当前线程的任何其他对象将保持锁定状态。
     * 此方法只能由作为此对象监视器所有者的线程调用.
     * 请参阅{@code notify}方法，了解线程成为监视器所有者的方式的描述
     *

     *
     * @param      timeout   等待的最长时间（毫秒）。
     * @throws  IllegalArgumentException    如果超时值为负。
     * @throws  IllegalMonitorStateException 如果当前线程不是对象监视器的所有者
     *
     * @throws  InterruptedException 如果任何线程在当前线程等待通知之前或期间中断了当前线程。
     *                               引发此异常时，当前线程的中断状态将被清除。
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */



    /**
     * Causes the current thread to wait until either another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or a
     * specified amount of time has elapsed.
     * <p>
     * The current thread must own this object's monitor.
     * <p>
     * This method causes the current thread (call it <var>T</var>) to
     * place itself in the wait set for this object and then to relinquish
     * any and all synchronization claims on this object. Thread <var>T</var>
     * becomes disabled for thread scheduling purposes and lies dormant
     * until one of four things happens:
     * <ul>
     * <li>Some other thread invokes the {@code notify} method for this
     * object and thread <var>T</var> happens to be arbitrarily chosen as
     * the thread to be awakened.
     * <li>Some other thread invokes the {@code notifyAll} method for this
     * object.
     * <li>Some other thread {@linkplain Thread#interrupt() interrupts}
     * thread <var>T</var>.
     * <li>The specified amount of real time has elapsed, more or less.  If
     * {@code timeout} is zero, however, then real time is not taken into
     * consideration and the thread simply waits until notified.
     * </ul>
     * The thread <var>T</var> is then removed from the wait set for this
     * object and re-enabled for thread scheduling. It then competes in the
     * usual manner with other threads for the right to synchronize on the
     * object; once it has gained control of the object, all its
     * synchronization claims on the object are restored to the status quo
     * ante - that is, to the situation as of the time that the {@code wait}
     * method was invoked. Thread <var>T</var> then returns from the
     * invocation of the {@code wait} method. Thus, on return from the
     * {@code wait} method, the synchronization state of the object and of
     * thread {@code T} is exactly as it was when the {@code wait} method
     * was invoked.
     * <p>
     * A thread can also wake up without being notified, interrupted, or
     * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
     * occur in practice, applications must guard against it by testing for
     * the condition that should have caused the thread to be awakened, and
     * continuing to wait if the condition is not satisfied.  In other words,
     * waits should always occur in loops, like this one:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * (For more information on this topic, see Section 3.2.3 in Doug Lea's
     * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley,
     * 2000), or Item 50 in Joshua Bloch's "Effective Java Programming
     * Language Guide" (Addison-Wesley, 2001).
     *
     * <p>If the current thread is {@linkplain java.lang.Thread#interrupt()
     * interrupted} by any thread before or while it is waiting, then an
     * {@code InterruptedException} is thrown.  This exception is not
     * thrown until the lock status of this object has been restored as
     * described above.
     *
     * <p>
     * Note that the {@code wait} method, as it places the current thread
     * into the wait set for this object, unlocks only this object; any
     * other objects on which the current thread may be synchronized remain
     * locked while the thread waits.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @throws  IllegalArgumentException      if the value of timeout is
     *               negative.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final native void wait(long timeout) throws InterruptedException;




    /**
     * 由于当前线程等待直到另一个线程调用{@link java.lang.Object#notify()}方法或者
     * 对于对象的 {@link java.lang.Object#notifyAll()}方法，或者经过了指定的时间
     * 当前线程必须持有对象的监视器
     *
     * 这个方法和{@code wait}方法是一样的一个参数，但它允许更好地控制在放弃之前等待通知的时间
     * 实时数据量以纳秒为单位测量由以下公式得出：
     * 1000000*timeout+nanos
     * 在所有其他的方面，此方法与一个参数的方法{@link#wait（long）}的作用相同
     * 特别的，{@code wait（0，0）}与{@code wait（0）}的意思相同。
     * 当前线程必须持有这个对象监视器。线程释放此监视器的所有权并等待直到
     * 出现以下两种情况之一：
     *      1另一个线程通知等待此对象监视器的线程通过调用来唤醒
     *      2超时时间(由{@code timeout}毫秒加上{@code nanos} 纳秒参数)经过了。
     * 线程然后等待直到它能重新获得监视器的所有权并继续执行。
     *
     *  在一个参数版本中，中断和虚假唤醒是可能的，并且这个方法应该总是在循环中使用:
     *  synchronized (obj) {
     *               while (&lt;condition does not hold&gt;)
     *                   obj.wait(timeout, nanos);
     *               ... // Perform action appropriate to condition
     *           }
     *  此方法只能由作为此对象监视器所有者的线程调用.
     * 请参阅{@code notify}方法，了解线程成为监视器所有者的方式的描述
     *
     *
     *
     * @param      timeout   最大等待时间，单位为毫秒。
     * @param      nanos      额外时间，以纳秒为单位0-999999.
     *
     * @throws  IllegalArgumentException      如果timeout值为负值或nanos值不在0 ~ 999999范围内。
     * @throws  IllegalMonitorStateException  如果当前线程不是这个对象监视器的所有者。
     * @throws  InterruptedException  如果任何线程在当前线程之前或在当前线程期间中断了当前线程
     *                                在等通知。当抛出此异常时，当前线程的中断状态将被清除。
     */



    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or
     * some other thread interrupts the current thread, or a certain
     * amount of real time has elapsed.
     * <p>
     * This method is similar to the {@code wait} method of one
     * argument, but it allows finer control over the amount of time to
     * wait for a notification before giving up. The amount of real time,
     * measured in nanoseconds, is given by:
     * <blockquote>
     * <pre>
     * 1000000*timeout+nanos</pre></blockquote>
     * <p>
     * In all other respects, this method does the same thing as the
     * method {@link #wait(long)} of one argument. In particular,
     * {@code wait(0, 0)} means the same thing as {@code wait(0)}.
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until either of the
     * following two conditions has occurred:
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor
     *     to wake up either through a call to the {@code notify} method
     *     or the {@code notifyAll} method.
     * <li>The timeout period, specified by {@code timeout}
     *     milliseconds plus {@code nanos} nanoseconds arguments, has
     *     elapsed.
     * </ul>
     * <p>
     * The thread then waits until it can re-obtain ownership of the
     * monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout, nanos);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @param      nanos      additional time, in nanoseconds range
     *                       0-999999.
     * @throws  IllegalArgumentException      if the value of timeout is
     *                      negative or the value of nanos is
     *                      not in the range 0-999999.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
           //如果超时抛出IllegalArgumentException 超时值为负值
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            //纳秒超时值超出范围
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }
        if (nanos > 0) {
            //纳秒没有超出范围有时间。
            timeout++;
            //增加最大等待时间。
        }

        wait(timeout);
        //调用wait方法。
    }

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object.
     * In other words, this method behaves exactly as if it simply
     * performs the call {@code wait(0)}.
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until another thread
     * notifies threads waiting on this object's monitor to wake up
     * either through a call to the {@code notify} method or the
     * {@code notifyAll} method. The thread then waits until it can
     * re-obtain ownership of the monitor and resumes execution.
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait();
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     * <p>
     * The general contract of {@code finalize} is that it is invoked
     * if and when the Java&trade; virtual
     * machine has determined that there is no longer any
     * means by which this object can be accessed by any thread that has
     * not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be
     * finalized. The {@code finalize} method may take any action, including
     * making this object available again to other threads; the usual purpose
     * of {@code finalize}, however, is to perform cleanup actions before
     * the object is irrevocably discarded. For example, the finalize method
     * for an object that represents an input/output connection might perform
     * explicit I/O transactions to break the connection before the object is
     * permanently discarded.
     * <p>
     * The {@code finalize} method of class {@code Object} performs no
     * special action; it simply returns normally. Subclasses of
     * {@code Object} may override this definition.
     * <p>
     * The Java programming language does not guarantee which thread will
     * invoke the {@code finalize} method for any given object. It is
     * guaranteed, however, that the thread that invokes finalize will not
     * be holding any user-visible synchronization locks when finalize is
     * invoked. If an uncaught exception is thrown by the finalize method,
     * the exception is ignored and finalization of that object terminates.
     * <p>
     * After the {@code finalize} method has been invoked for an object, no
     * further action is taken until the Java virtual machine has again
     * determined that there is no longer any means by which this object can
     * be accessed by any thread that has not yet died, including possible
     * actions by other objects or classes which are ready to be finalized,
     * at which point the object may be discarded.
     * <p>
     * The {@code finalize} method is never invoked more than once by a Java
     * virtual machine for any given object.
     * <p>
     * Any exception thrown by the {@code finalize} method causes
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     *
     * @throws Throwable the {@code Exception} raised by this method
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     * @jls 12.6 Finalization of Class Instances
     */
    protected void finalize() throws Throwable { }
}
