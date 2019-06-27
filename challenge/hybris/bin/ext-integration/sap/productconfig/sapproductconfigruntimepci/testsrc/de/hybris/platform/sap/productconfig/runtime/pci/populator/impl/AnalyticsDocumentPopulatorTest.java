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
package de.hybris.platform.sap.productconfig.runtime.pci.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsContextEntry;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class AnalyticsDocumentPopulatorTest
{
	private static final String PRODUCT_NAME = "product name";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private AnalyticsDocumentPopulator classUnderTest;
	private ConfigModel source;
	private AnalyticsDocument target;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParameter;
	@Mock
	private ConfigurationParameterB2B configurationParameterB2B;
	@Mock
	private Converter<InstanceModel, AnalyticsItem> analyticsItemConverter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new AnalyticsDocumentPopulator();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		classUnderTest.setAnalyticsItemConverter(analyticsItemConverter);
		source = new ConfigModelImpl();
		source.setName(PRODUCT_NAME);
		source.setRootInstance(new InstanceModelImpl());
		target = new AnalyticsDocument();
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target);
		assertEquals(PRODUCT_NAME, target.getRootProduct());
	}

	@Test
	public void testFillContext()
	{
		classUnderTest.fillContext(target);
		assertNotNull(target.getContextAttributes());
		assertEquals(3, target.getContextAttributes().size());
		assertTrue(isEntryPresent(AnalyticsDocumentPopulator.SALES_ORG, target.getContextAttributes()));
		assertTrue(isEntryPresent(AnalyticsDocumentPopulator.DISTRIBUTION_CHANNEL, target.getContextAttributes()));
		assertTrue(isEntryPresent(AnalyticsDocumentPopulator.DIVISION, target.getContextAttributes()));
	}

	private boolean isEntryPresent(final String name, final List<AnalyticsContextEntry> context)
	{
		for (final AnalyticsContextEntry entry : context)
		{
			if (name.equals(entry.getName()))
			{
				return true;
			}
		}
		return false;
	}

	@Test
	public void testCreateContextEntry()
	{
		final AnalyticsContextEntry result = classUnderTest.createContextEntry(NAME, VALUE);
		assertNotNull(result);
		assertEquals(NAME, result.getName());
		assertEquals(VALUE, result.getValue());
	}

	@Test
	public void testPopulateRootItem()
	{
		Mockito.when(analyticsItemConverter.convert(Mockito.any())).thenReturn(new AnalyticsItem());
		classUnderTest.populateRootItem(source, target);
		assertNotNull(target.getRootItem());
	}
}
