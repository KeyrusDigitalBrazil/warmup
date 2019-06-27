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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.strategies.VendorCMSStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;


/**
 * Slots translator for marketplace
 */
public class MarketplaceSlotsTranslator extends AbstractValueTranslator
{
	private static final String VENDOR_SERVICE = "vendorService";
	private static final String VENDORCMS_STRATEGY = "vendorCmsStrategy";
	private static final String VENDOR_CMSPAGE_SERVICE = "vendorCMSPageService";
	private static final String CATEGORYVERSION_SERVICE = "catalogVersionService";

	private VendorService vendorService;
	private VendorCMSPageService vendorCMSPageService;
	private VendorCMSStrategy vendorCmsStrategy;
	private CatalogVersionService catalogVersionService;


	@Override
	public Object importValue(final String paramString, final Item paramItem)
	{
		if (StringUtils.isBlank(paramString))
		{
			throw new IllegalArgumentException("position is missing");
		}

		final String vendorCode = this.getColumnDescriptor().getDescriptorData().getModifier("vendor");
		final String contentCatalog = this.getColumnDescriptor().getDescriptorData().getModifier("contentCatalog").trim();
		final String version = this.getColumnDescriptor().getDescriptorData().getModifier("version").trim();

		final Optional<VendorModel> vendorOptional = getVendorService().getVendorByCode(vendorCode);
		if (!vendorOptional.isPresent())
		{
			throw new IllegalArgumentException("Invalid vendor code: " + vendorCode);
		}
		final VendorModel vendor = vendorOptional.get();
		final CatalogVersionModel catalogVersion = this.getCatalogVersionService().getCatalogVersion(contentCatalog, version);

		prepareVendorLandingPage(vendor, catalogVersion);

		final ContentSlotModel productCarouselSlot = getVendorCMSStrategy().getContentSlotByPositionAndCatalogVersion(vendor,
				paramString, catalogVersion);

		final Set<ContentSlotModel> slots = new HashSet<>();

		slots.add(productCarouselSlot);

		return slots;

	}

	protected void prepareVendorLandingPage(final VendorModel vendor, final CatalogVersionModel catalogVersion)
	{
		final Optional<VendorPageModel> vendorPage = getVendorCMSPageService().getPageForVendor(vendor, catalogVersion);
		if (!vendorPage.isPresent() || vendorPage.get().getDefaultPage())
		{
			vendorCmsStrategy.prepareLandingPageForVendor(vendor);
		}
	}


	@Override
	public String exportValue(final Object paramObject)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(final StandardColumnDescriptor descriptor)
	{
		super.init(descriptor);
		setVendorService((VendorService) getApplicationContext().getBean(VENDOR_SERVICE));
		setVendorCMSPageService((VendorCMSPageService) getApplicationContext().getBean(VENDOR_CMSPAGE_SERVICE));
		setVendorCMSStrategy((VendorCMSStrategy) getApplicationContext().getBean(VENDORCMS_STRATEGY));
		setCatalogVersionService((CatalogVersionService) getApplicationContext().getBean(CATEGORYVERSION_SERVICE));
	}


	protected VendorService getVendorService()
	{
		return vendorService;
	}

	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

	protected VendorCMSPageService getVendorCMSPageService()
	{
		return vendorCMSPageService;
	}

	public void setVendorCMSPageService(final VendorCMSPageService vendorCMSPageService)
	{
		this.vendorCMSPageService = vendorCMSPageService;
	}

	protected VendorCMSStrategy getVendorCMSStrategy()
	{
		return vendorCmsStrategy;
	}

	public void setVendorCMSStrategy(final VendorCMSStrategy vendorCMSStrategy)
	{
		this.vendorCmsStrategy = vendorCMSStrategy;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
	}

}
