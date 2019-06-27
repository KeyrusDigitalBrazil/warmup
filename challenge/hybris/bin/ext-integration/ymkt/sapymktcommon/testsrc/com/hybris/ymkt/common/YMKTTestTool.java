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
/**
 *
 */
package com.hybris.ymkt.common;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;



public class YMKTTestTool
{
	private static final Object OBJ = new Object();

	public static void assertEqualsHashCode(final Object... objects)
	{
		YMKTTestTool.assertEqualsHashCode(Arrays.asList(objects));
	}

	public static void assertEqualsHashCode(final Collection<?> objects)
	{
		for (final Object o1 : objects)
		{
			// •For any non-null reference value x, x.equals(null) should return false.
			Assert.assertFalse(o1.equals(null));
			Assert.assertFalse(o1.equals(OBJ));

			// •It is reflexive: for any non-null reference value x, x.equals(x) should return true.
			Assert.assertTrue(o1.equals(o1));

			for (final Object o2 : objects)
			{
				if (o1.equals(o2))
				{
					// •It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true.
					Assert.assertTrue(o2.equals(o1));
					// •If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result.
					Assert.assertEquals(o1.hashCode(), o2.hashCode());
				}
				else
				{
					Assert.assertFalse(o1.equals(o2));
					Assert.assertFalse(o2.equals(o1));
				}

				if (o1.hashCode() != o2.hashCode())
				{
					Assert.assertFalse(o1.equals(o2));
					Assert.assertFalse(o2.equals(o1));
				}
			}
		}
	}
}
