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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


@UnitTest
public class QuoteConfigurationPopulatorTest extends AbstractOrderConfigurationPopulatorTest
{
	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	@Before
	public void setup()
	{
		super.setup();
		classUnderTest = new QuoteConfigurationPopulator();
		((QuoteConfigurationPopulator) classUnderTest).setCpqConfigurableChecker(cpqConfigurableChecker);

		source = new QuoteModel();
		source.setEntries(entryList);
		target = new QuoteData();
		target.setEntries(targetEntryList);
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testWriteToTargetEntryIllegalArgument()
	{
		super.testWriteToTargetEntryIllegalArgument();
	}

	@Override
	@Test
	public void testWriteToTargetEntry()
	{
		super.testWriteToTargetEntry();
	}

	@Override
	@Test
	public void testWriteToTargetEntryInconsistent()
	{
		super.testWriteToTargetEntryInconsistent();
	}

	@Override
	@Test
	public void testCreateConfigurationInfos()
	{
		super.testCreateConfigurationInfos();
	}

	@Override
	@Test(expected = ConversionException.class)
	public void testCreateConfigurationInfosException()
	{
		super.testCreateConfigurationInfosException();
	}

	@Override
	@Test(expected = IllegalStateException.class)
	public void testWriteToTargetEntrySummaryMapNull()
	{
		super.testWriteToTargetEntrySummaryMapNull();
	}


	@Test
	public void testPopulateWithConfigurableProduct()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
		when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		when(sourceEntry.getPk()).thenReturn(PK.parse(PK_VALUE));

		((QuoteConfigurationPopulator) classUnderTest).populate((QuoteModel) source, (QuoteData) target);
		final OrderEntryData result = target.getEntries().get(0);
		assertNotNull(result);
		assertEquals(PK_VALUE, result.getItemPK());
		assertTrue(result.isConfigurationAttached());
		assertTrue(result.isConfigurationConsistent());
		assertEquals(0, result.getConfigurationErrorCount());
		assertTrue(result.getConfigurationInfos().isEmpty());
	}

	@Test
	public void testPopulateWithNonConfigurableProduct()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(false);

		((QuoteConfigurationPopulator) classUnderTest).populate((QuoteModel) source, (QuoteData) target);
		final OrderEntryData result = target.getEntries().get(0);
		assertNotNull(result);
		assertEquals("123", result.getItemPK());
		assertFalse(result.isConfigurationAttached());
		assertFalse(result.isConfigurationConsistent());
		assertEquals(0, result.getConfigurationErrorCount());
		assertNull(result.getConfigurationInfos());
	}

}
