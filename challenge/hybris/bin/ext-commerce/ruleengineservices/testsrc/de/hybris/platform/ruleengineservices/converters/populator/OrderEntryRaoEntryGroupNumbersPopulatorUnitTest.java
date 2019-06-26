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
package de.hybris.platform.ruleengineservices.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import org.junit.Test;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fest.util.Collections.isEmpty;


@UnitTest
public class OrderEntryRaoEntryGroupNumbersPopulatorUnitTest
{
	private OrderEntryRaoEntryGroupNumbersPopulator populator = new OrderEntryRaoEntryGroupNumbersPopulator();

	@Test
	public void shouldPopulateGroupEntryNumbers()
	{
		final Integer entryGroupNumber = Integer.valueOf(5);
		final AbstractOrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setEntryGroupNumbers(singleton(entryGroupNumber));

		final OrderEntryRAO orderEntryRAO = new OrderEntryRAO();

		populator.populate(orderEntry, orderEntryRAO);

		assertThat(orderEntryRAO.getEntryGroupNumbers().size()).isEqualTo(1);
		assertThat(orderEntryRAO.getEntryGroupNumbers().get(0)).isEqualTo(entryGroupNumber);
	}

	@Test
	public void shouldPopulateWithEmptyList()
	{
		final AbstractOrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setEntryGroupNumbers(emptySet());

		final OrderEntryRAO orderEntryRAO = new OrderEntryRAO();

		populator.populate(orderEntry, orderEntryRAO);

		assertThat(isEmpty(orderEntryRAO.getEntryGroupNumbers())).isEqualTo(true);
	}

	@Test
	public void shouldNotPopulateWhenSourceListIsNull()
	{
		final AbstractOrderEntryModel orderEntry = new OrderEntryModel();
		final OrderEntryRAO orderEntryRAO = new OrderEntryRAO();

		populator.populate(orderEntry, orderEntryRAO);

		assertThat(orderEntryRAO.getEntryGroupNumbers()).isNull();
	}

	@Test
	public void shouldOverrideListElementsInCaseSomeExistedBeforeInTarget()
	{
		final Integer entryGroupNumber = Integer.valueOf(5);
		final AbstractOrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setEntryGroupNumbers(singleton(entryGroupNumber));

		final OrderEntryRAO orderEntryRAO = new OrderEntryRAO();
		final Integer anotherEntryGroupNumber = Integer.valueOf(2);
		orderEntryRAO.setEntryGroupNumbers(singletonList(anotherEntryGroupNumber));

		populator.populate(orderEntry, orderEntryRAO);

		assertThat(orderEntryRAO.getEntryGroupNumbers().size()).isEqualTo(1);
		assertThat(orderEntryRAO.getEntryGroupNumbers().contains(anotherEntryGroupNumber)).isEqualTo(false);
		assertThat(orderEntryRAO.getEntryGroupNumbers().contains(entryGroupNumber)).isEqualTo(true);
	}
}
