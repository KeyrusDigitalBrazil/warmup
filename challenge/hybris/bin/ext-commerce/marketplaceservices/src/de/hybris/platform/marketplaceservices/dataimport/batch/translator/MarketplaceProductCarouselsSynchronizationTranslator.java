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

import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Marketplace translator for synchronize product carousel.
 */
public class MarketplaceProductCarouselsSynchronizationTranslator extends AbstractSpecialValueTranslator
{

	private ModelService modelService;
	private VendorCMSService vendorCMSService;
	private SessionService sessionService;

	@Override
	public void init(SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		setModelService((ModelService) Registry.getApplicationContext().getBean("modelService"));
		setVendorCMSService((VendorCMSService) Registry.getApplicationContext().getBean("vendorCmsService"));
		setSessionService((SessionService) Registry.getApplicationContext().getBean("sessionService"));
	}

	@Override
	public void performImport(String carouselId, Item item) throws ImpExException
	{
		final ProductCarouselComponentModel carousel = getModelService().get(item);
		if (carousel != null)
		{
			final SyncItemStatus status = getVendorCMSService().getProductCarouselSynchronizationStatus(carousel);
			if (status == SyncItemStatus.NOT_SYNC)
			{
				final Session localSession = getSessionService().createNewSession();
				try
				{
					getVendorCMSService().performProductCarouselSynchronization(carousel, false);
				}
				finally
				{
					getSessionService().closeSession(localSession);
				}
			}
		}
	}


	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected VendorCMSService getVendorCMSService()
	{
		return vendorCMSService;
	}

	public void setVendorCMSService(VendorCMSService vendorCMSService)
	{
		this.vendorCMSService = vendorCMSService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
