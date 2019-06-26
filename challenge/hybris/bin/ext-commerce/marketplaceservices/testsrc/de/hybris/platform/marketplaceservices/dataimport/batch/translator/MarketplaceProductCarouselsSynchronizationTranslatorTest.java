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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MarketplaceProductCarouselsSynchronizationTranslatorTest
{

	private MarketplaceProductCarouselsSynchronizationTranslator translator;

	private static final SyncItemStatus NOT_SYNC = SyncItemStatus.NOT_SYNC;

	@Mock
	private ModelService modelService;
	@Mock
	private VendorCMSService vendorCMSService;
	@Mock
	private SessionService sessionService;
	@Mock
	private ProductCarouselComponentModel carousel;
	@Mock
	private Item item;
	@Mock
	private Session session;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		translator = new MarketplaceProductCarouselsSynchronizationTranslator();
		translator.setModelService(modelService);
		translator.setVendorCMSService(vendorCMSService);
		translator.setSessionService(sessionService);

		BDDMockito.given(modelService.get(item)).willReturn(carousel);
		BDDMockito.given(vendorCMSService.getProductCarouselSynchronizationStatus(carousel)).willReturn(NOT_SYNC);
		BDDMockito.given(sessionService.createNewSession()).willReturn(session);
		BDDMockito.doNothing().when(sessionService).closeSession(session);
	}

	@Test
	public void testPerformImport() throws ImpExException
	{
		translator.performImport(null, item);
		BDDMockito.verify(vendorCMSService, BDDMockito.times(1)).performProductCarouselSynchronization(carousel, false);
	}
}
