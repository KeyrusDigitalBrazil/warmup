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
package de.hybris.platform.marketplaceservices.vendor.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.marketplaceservices.data.MarketplaceCMSDataFactory;
import de.hybris.platform.marketplaceservices.data.MarketplaceRestrictionData;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Provide vendor page related services
 */
public class DefaultVendorCMSPageService extends DefaultCMSPageService implements VendorCMSPageService
{

	private transient MarketplaceCMSDataFactory marketplaceCMSDataFactory;

	@Override
	public Optional<VendorPageModel> getPageForVendor(final VendorModel vendorModel)
	{
		final ComposedTypeModel type = getTypeService().getComposedTypeForCode("VendorPage");
		final Collection<CatalogVersionModel> versions = getCatalogVersionService().getSessionCatalogVersions();
		final MarketplaceRestrictionData data = getMarketplaceCMSDataFactory().createRestrictionData(vendorModel);
		final Collection<AbstractPageModel> pages = getCmsPageDao().findAllPagesByTypeAndCatalogVersions(type, versions);
		final Collection<AbstractPageModel> result = getCmsRestrictionService().evaluatePages(pages, data);
		if (CollectionUtils.isNotEmpty(result))
		{
			if (result.size() > 1)
			{
				LOG.warn("More than one page found for vendor [" + vendorModel.getCode() + "]. Returning default.");
			}
			return Optional.ofNullable((VendorPageModel) result.iterator().next());
		}
		return Optional.empty();
	}


	@Override
	public Optional<VendorPageModel> getPageForVendor(final VendorModel vendorModel, final CatalogVersionModel catalogVersionModel)
	{
		final ComposedTypeModel type = getTypeService().getComposedTypeForCode("VendorPage");
		final Set<CatalogVersionModel> versions = new HashSet<>();
		versions.add(catalogVersionModel);
		final MarketplaceRestrictionData data = getMarketplaceCMSDataFactory().createRestrictionData(vendorModel);
		final Collection<AbstractPageModel> pages = getCmsPageDao().findAllPagesByTypeAndCatalogVersions(type, versions);
		final Collection<AbstractPageModel> result = getCmsRestrictionService().evaluatePages(pages, data);
		if (result.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			if (result.size() > 1)
			{
				LOG.warn("More than one page found for vendor [" + vendorModel.getCode() + "].");
			}
			return Optional.of((VendorPageModel) result.iterator().next());
		}
	}

	@Required
	protected MarketplaceCMSDataFactory getMarketplaceCMSDataFactory()
	{
		return marketplaceCMSDataFactory;
	}

	public void setMarketplaceCMSDataFactory(final MarketplaceCMSDataFactory marketplaceCMSDataFactory)
	{
		this.marketplaceCMSDataFactory = marketplaceCMSDataFactory;
	}



}
