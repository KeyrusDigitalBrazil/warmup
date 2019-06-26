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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2lib.components.BannerComponent;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * Banner component translator for marketplace, compose the vendor code with banner uid
 */
public class MarketplaceBannerComponentTranslator extends AbstractValueTranslator
{
	private static final String VENDOR_CMS_SERVICE = "vendorCmsService";
	private static final String CATALOG_VERSION_SERVICE = "catalogVersionService";
	private static final String MODEL_SERVICE = "modelService";

	private VendorCMSService vendorCmsService;

	private CatalogVersionService catalogVersionService;

	private ModelService modelService;

	@Override
	public Object importValue(final String paramString, final Item paramItem)
	{
		final String contentCatalog = this.getColumnDescriptor().getDescriptorData().getModifier("contentCatalog").trim();
		final String version = this.getColumnDescriptor().getDescriptorData().getModifier("version").trim();
		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(contentCatalog, version);
		final String vendorCode = getColumnDescriptor().getDescriptorData().getModifier("vendor").trim();
		final Set<BannerComponent> banners = Arrays.stream(StringUtils.split(paramString, ",")).map(bannerUid -> {
			final AbstractCMSComponentModel banner = getVendorCmsService()
					.getCMSComponentByIdAndCatalogVersion(vendorCode + "_" + bannerUid, catalogVersion).get();
			return (BannerComponent) modelService.getSource(banner);
		}).collect(Collectors.toSet());
		return banners;
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
		setVendorCmsService((VendorCMSService) Registry.getApplicationContext().getBean(VENDOR_CMS_SERVICE));
		setCatalogVersionService((CatalogVersionService) Registry.getApplicationContext().getBean(CATALOG_VERSION_SERVICE));
		setModelService((ModelService) Registry.getApplicationContext().getBean(MODEL_SERVICE));
	}

	protected VendorCMSService getVendorCmsService()
	{
		return vendorCmsService;
	}

	public void setVendorCmsService(final VendorCMSService vendorCmsService)
	{
		this.vendorCmsService = vendorCmsService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
