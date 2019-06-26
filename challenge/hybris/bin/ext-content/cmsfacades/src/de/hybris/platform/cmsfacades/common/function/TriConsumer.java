/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.common.function;


import java.util.Objects;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result.  This is the two-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <B> the type of the third argument to the operation
 * @see java.util.function.Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface TriConsumer<T, U, B>
{

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 * @param u the second input argument
	 * @param b the third input argument
	 */
	void accept(T t, U u, B b);

	/**
	 * Returns a composed {@code TriConsumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation.  If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code TriConsumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default TriConsumer<T, U, B> andThen(TriConsumer<? super T, ? super U, ? super B> after)
	{
		Objects.requireNonNull(after);

		return (l, r, b) -> {
			accept(l, r, b);
			after.accept(l, r, b);
		};
	}
}
