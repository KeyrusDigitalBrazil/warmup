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
package de.hybris.platform.promotions.attributehandlers;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionOrderEntryConsumedOrderEntryNumberAttributeHandlerUnitTest
{
	private PromotionOrderEntryConsumedOrderEntryNumberAttributeHandler handler =
			new PromotionOrderEntryConsumedOrderEntryNumberAttributeHandler();

	@Mock
	private PromotionOrderEntryConsumedModel promotionOrderEntryConsumed;

	@Mock
	private AbstractOrderEntryModel orderEntry;

	@Before
	public void setUp()
	{
		when(orderEntry.getEntryNumber()).thenReturn(Integer.valueOf(1));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnSet()
	{
		handler.set(promotionOrderEntryConsumed, Integer.valueOf(Integer.MIN_VALUE));
	}

	@Test
	public void shouldReturnNumberFromOrderEntryWhenEntryIsAvailable()
	{
		when(promotionOrderEntryConsumed.getOrderEntry()).thenReturn(orderEntry);

		final Integer result = handler.get(promotionOrderEntryConsumed);

		assertThat(result).isEqualTo(1);
	}

	@Test
	public void shouldReturnOrderEntryNumberWhenEntryIsNotAvailable()
	{
		when(promotionOrderEntryConsumed.getOrderEntry()).thenReturn(null);
		when(promotionOrderEntryConsumed.getOrderEntryNumber()).thenReturn(Integer.valueOf(1));

		final Integer result = handler.get(promotionOrderEntryConsumed);

		assertThat(result).isEqualTo(1);
	}
}
