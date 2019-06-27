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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import com.hybris.cis.client.tax.models.CisTaxDoc;
import com.hybris.cis.client.tax.models.CisTaxLine;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;



/**
 *
 *
 */
@UnitTest
public class DefaultCisShippingIncludedStrategyTest
{
	private DefaultCisShippingIncludedStrategy defaultCisShippingIncludedStrategy;

	@Before
	public void setUp()
	{
		defaultCisShippingIncludedStrategy = new DefaultCisShippingIncludedStrategy();
	}

	@Test
	public void shouldNotIncludeShipping()
	{
		final CisTaxDocOrder taxDocOrder = mock(CisTaxDocOrder.class);

		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final List<AbstractOrderEntryModel> orderEntries = mock(ArrayList.class);
		final CisTaxDoc taxDoc = mock(CisTaxDoc.class);
		final List<CisTaxLine> taxLines = mock(ArrayList.class);

		given(Integer.valueOf(taxLines.size())).willReturn(Integer.valueOf(2));
		given(taxDoc.getTaxLines()).willReturn(taxLines);
		given(taxDocOrder.getTaxDoc()).willReturn(taxDoc);
		given(Integer.valueOf(orderEntries.size())).willReturn(Integer.valueOf(2));
		given(abstractOrder.getEntries()).willReturn(orderEntries);
		given(taxDocOrder.getAbstractOrder()).willReturn(abstractOrder);

		Assert.assertFalse(defaultCisShippingIncludedStrategy.isShippingIncluded(taxDocOrder));
	}

	@Test
	public void shouldIncludeShipping()
	{
		final CisTaxDocOrder taxDocOrder = mock(CisTaxDocOrder.class);

		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final List<AbstractOrderEntryModel> orderEntries = mock(ArrayList.class);
		final CisTaxDoc taxDoc = mock(CisTaxDoc.class);
		final List<CisTaxLine> taxLines = mock(ArrayList.class);

		given(Integer.valueOf(taxLines.size())).willReturn(Integer.valueOf(3));
		given(taxDoc.getTaxLines()).willReturn(taxLines);
		given(taxDocOrder.getTaxDoc()).willReturn(taxDoc);
		given(Integer.valueOf(orderEntries.size())).willReturn(Integer.valueOf(2));
		given(abstractOrder.getEntries()).willReturn(orderEntries);
		given(taxDocOrder.getAbstractOrder()).willReturn(abstractOrder);

		Assert.assertTrue(defaultCisShippingIncludedStrategy.isShippingIncluded(taxDocOrder));
	}
}
