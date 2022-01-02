/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java.util.function;

import java.util.Objects;

/**
 表示接受单个输入参数并返回no的操作
 后果与大多数其他功能接口不同，{@code Consumer}预期通过副作用进行操作。
 <p>这是一个功能界面</a>
 其函数方法是{@link#accept（Object）}。
 @param<T>操作的输入类型
 @从1.8开始
 */

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * 对给定参数执行此操作。
     *
     * @param t 输入参数
     */

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

    /**
     *返回一个组合的{@code Consumer}，该组合按顺序执行以下操作
     *操作之后是{@code after}操作。如果执行任何一项
     *操作抛出一个异常，它被中继到
     *镇静操作。如果执行此操作引发异常，
     *将不执行{@code after}操作。
     *
     *@param在此操作之后执行此操作
     *@返回按此顺序执行的组合{@code Consumer}
     *操作后接{@code after}操作
     *如果{@code after}为空，@将引发NullPointerException
     */

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
