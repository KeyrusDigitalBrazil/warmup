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
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.pci.PCICharonFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class PCIAnalyticsProviderImplTest
{

	private PCIAnalyticsProviderImpl classUnderTest;
	@Mock
	private ConfigModel configModel;
	@Mock
	private Converter<ConfigModel, AnalyticsDocument> mockAnalyticsDocumentConverter;
	@Mock
	private PCICharonFacade mockPciCharonFacade;
	private final AnalyticsDocument analyticsDocumentInput = new AnalyticsDocument();
	private final AnalyticsDocument analyticsDocumentOutput = new AnalyticsDocument();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PCIAnalyticsProviderImpl();
		classUnderTest.setAnalyticsDocumentConverter(mockAnalyticsDocumentConverter);
		classUnderTest.setPciCharonFacade(mockPciCharonFacade);
		given(mockAnalyticsDocumentConverter.convert(configModel)).willReturn(analyticsDocumentInput);
		given(mockPciCharonFacade.createAnalyticsDocument(analyticsDocumentInput)).willReturn(analyticsDocumentOutput);
	}


	@Test
	public void testProviderIsActive()
	{
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testGetPopularity()
	{
		final AnalyticsDocument output = classUnderTest.getPopularity(configModel);
		verify(mockAnalyticsDocumentConverter).convert(configModel);
		verify(mockPciCharonFacade).createAnalyticsDocument(analyticsDocumentInput);
		assertNotSame(analyticsDocumentInput, output);
		assertSame(analyticsDocumentOutput, output);
	}


}
