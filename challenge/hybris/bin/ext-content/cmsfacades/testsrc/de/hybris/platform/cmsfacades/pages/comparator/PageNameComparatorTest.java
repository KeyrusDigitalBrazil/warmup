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
package de.hybris.platform.cmsfacades.pages.comparator;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.AbstractPageData;

import org.junit.Test;


@UnitTest
public class PageNameComparatorTest
{
	private final PageNameComparator comparator = new PageNameComparator();

	private final AbstractPageData page1 = new AbstractPageData();
	private final AbstractPageData page2 = new AbstractPageData();

	@Test
	public void shouldBeSmaller()
	{
		page1.setName("A-test");
		page2.setName("x-test");

		final int value = comparator.compare(page1, page2);
		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeGreater()
	{
		page1.setName("x-test");
		page2.setName("A-test");

		final int value = comparator.compare(page1, page2);
		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeEqual()
	{
		page1.setName("x-test");
		page2.setName("x-test");

		final int value = comparator.compare(page1, page2);
		assertThat(value, is(0));
	}

}
