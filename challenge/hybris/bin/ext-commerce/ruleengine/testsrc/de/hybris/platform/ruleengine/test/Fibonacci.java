/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengine.test;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Fibonacci numbers calculation class, used for sample rules evaluation
 */
public class Fibonacci
{

	private int sequence;
	private long value;

	public Fibonacci(final int sequence)
	{
		this.sequence = sequence;
		this.value = -1;
	}

	public long getValue()
	{
		checkArgument(sequence > -1, "Illegal sequence [" + sequence + "]");

		if (value > -1)  // already calculated
		{
			return value;
		}

		if (sequence == 0 || sequence == 1)
		{
			value = sequence;
		}
		else if (sequence > 1)
		{
			value = new Fibonacci(sequence - 2).getValue() + new Fibonacci(sequence - 1).getValue();
		}
		return value;
	}

	public int getSequence()
	{
		return sequence;
	}

	public static void main(String[] args)
	{
		System.out.println("Fibonacci sequence: ");
		for (int i = 0; i < 20; i++)
		{
			System.out.println(new Fibonacci(i).getValue());
		}
	}
}
