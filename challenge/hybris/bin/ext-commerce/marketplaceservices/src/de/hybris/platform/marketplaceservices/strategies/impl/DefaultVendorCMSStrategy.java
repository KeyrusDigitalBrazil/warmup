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
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.strategies.VendorCMSStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;



public class DefaultVendorCMSStrategy implements VendorCMSStrategy
{
	private static final String CONTENT_CATALOG_ID_KEY = "marketplace.content.catalog.code";
	private static final String CONTENT_CATALOG_VERSION_KEY = "marketplace.content.catalog.version";
	private static final String LANDING_PAGE_UID_SUFFIX = "vendor.landing.page.uid.suffix";
	private static final String LANDING_PAGE_NAME_SUFFIX = "vendor.landing.page.name.suffix";
	private static final String LANDING_PAGE_NEW_RESTRICTION_UID_SUFFIX = "vendor.landing.page.restriction.uid.suffix";
	private static final String LANDING_PAGE_NEW_RESTRICTION_NAME_PREFIX = "vendor.landing.page.restriction.name.prefix";

	private static final String CONTENT_SLOT_ID_PATTERN = "BodyContentSlot-{0}-{1}";
	private static final String CONTENT_SLOT_NAME_PATTERN = "Content Slot for {0}";
	private static final String VENDOR_LANDING_PAGE_TEMPLATE = "VendorLandingPageTemplate";
	private static final String VENDOR_LANDING_PAGE_BANNER_POSITION = "Section2A";
	private static final String VENDOR_LANDING_PAGE_PRODUCT_CAROUSEL_POSITION = "Section3";
	private static final String SPACE = " ";

	private ConfigurationService configurationService;

	private CatalogVersionService catalogVersionService;

	private VendorCMSService vendorCmsService;

	private ModelService modelService;

	private VendorCMSPageService vendorCMSPageService;

	@Override
	public AbstractPageModel prepareLandingPageForVendor(final VendorModel vendor)
	{
		final String vendorCode = vendor.getCode();
		final String contentCatalogId = getStringConfiguration(CONTENT_CATALOG_ID_KEY);
		final String contentCatalogVersion = getStringConfiguration(CONTENT_CATALOG_VERSION_KEY);
		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(contentCatalogId,
				contentCatalogVersion);
		final Optional<PageTemplateModel> pageTemplate = getVendorCmsService()
				.getPageTemplateByIdAndCatalogVersion(VENDOR_LANDING_PAGE_TEMPLATE, catalogVersion);
		final String pageUid = vendorCode + getStringConfiguration(LANDING_PAGE_UID_SUFFIX);
		final String pageName = vendorCode.toUpperCase(Locale.ROOT) + SPACE + getStringConfiguration(LANDING_PAGE_NAME_SUFFIX);
		final VendorPageModel landingPage = getVendorCmsService().saveOrUpdateCMSVendorPage(pageUid, pageName, catalogVersion,
				pageTemplate.get(), false, CmsApprovalStatus.APPROVED);
		final String restrictionUid = vendorCode + getStringConfiguration(LANDING_PAGE_NEW_RESTRICTION_UID_SUFFIX);
		final String restrictionName = getStringConfiguration(LANDING_PAGE_NEW_RESTRICTION_NAME_PREFIX) + SPACE
				+ vendorCode.toUpperCase(Locale.ROOT);
		getVendorCmsService().saveOrUpdateCMSVendorRestriction(vendor, catalogVersion, restrictionUid, restrictionName,
				landingPage);
		createContentSlotForPage(vendorCode, catalogVersion, VENDOR_LANDING_PAGE_BANNER_POSITION, landingPage);
		createContentSlotForPage(vendorCode, catalogVersion, VENDOR_LANDING_PAGE_PRODUCT_CAROUSEL_POSITION, landingPage);
		return landingPage;
	}

	@Override
	public ContentSlotModel getContentSlotByPositionAndCatalogVersion(final VendorModel vendor, final String position,
			final CatalogVersionModel catalogVersion)
	{
		return getVendorCMSPageService().getPageForVendor(vendor, catalogVersion)
				.map(page -> getVendorCmsService().getContentSlotByPageAndPosition(position, page, catalogVersion).orElse(null))
				.orElse(null);
	}

	@Override
	public List<AbstractCMSComponentModel> getVendorProductCarouselComponents(final VendorModel vendor)
	{
		final String contentCatalogId = getStringConfiguration(CONTENT_CATALOG_ID_KEY);
		final String contentCatalogVersion = getStringConfiguration(CONTENT_CATALOG_VERSION_KEY);
		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(contentCatalogId,
				contentCatalogVersion);

		return getVendorCMSPageService()
				.getPageForVendor(vendor,
						catalogVersion)
				.map(page -> getVendorCmsService()
						.getContentSlotByPageAndPosition(VENDOR_LANDING_PAGE_PRODUCT_CAROUSEL_POSITION, page, catalogVersion)
						.map(contentSlot -> getVendorCmsService().getCMSComponentsByContentSlotAndCatalogVersions(contentSlot.getUid(),
								Arrays.asList(catalogVersion)))
						.orElse(Collections.emptyList()))
				.orElse(Collections.emptyList());
	}

	/**
	 * extract a method to create content slot with its for page and components
	 *
	 * @param vendor
	 *           the specific vendor
	 * @param catalogVersion
	 *           the catalog version
	 * @param section
	 *           the position of this slot
	 * @param page
	 *           the page
	 * @param component
	 *           components in the slot
	 */
	protected ContentSlotModel createContentSlotForPage(final String vendorCode, final CatalogVersionModel catalogVersion, final String position,
			final AbstractPageModel page, final AbstractCMSComponentModel... component)
	{
		final String contentSlotId = MessageFormat.format(CONTENT_SLOT_ID_PATTERN, vendorCode, position);
		final String contentSlotName = MessageFormat.format(CONTENT_SLOT_NAME_PATTERN, position);
		final String contentSlotForPageId = MessageFormat.format(CONTENT_SLOT_ID_PATTERN, position, page.getUid());
		final ContentSlotModel contentSlot = getVendorCmsService().saveOrUpdateCMSContentSlot(catalogVersion, contentSlotId,
				contentSlotName, true);
		getVendorCmsService().saveOrUpdateCMSContentSlotForPage(catalogVersion, contentSlotForPageId, position, page, contentSlot);
		if (component != null)
		{
			contentSlot.setCmsComponents(Arrays.asList(component));
		}
		getModelService().save(contentSlot);
		return contentSlot;
	}

	/**
	 * get configuration value by key
	 *
	 * @param key
	 *           the configuration key
	 * @return the configuration value
	 */
	protected String getStringConfiguration(final String key)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("marketplace configuration key", key);
		final String value = getConfigurationService().getConfiguration().getString(key);
		ServicesUtil.validateParameterNotNullStandardMessage("marketplace configuration value", value);
		return value;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected VendorCMSService getVendorCmsService()
	{
		return vendorCmsService;
	}

	@Required
	public void setVendorCmsService(final VendorCMSService vendorCmsService)
	{
		this.vendorCmsService = vendorCmsService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected VendorCMSPageService getVendorCMSPageService()
	{
		return vendorCMSPageService;
	}

	@Required
	public void setVendorCMSPageService(final VendorCMSPageService vendorCMSPageService)
	{
		this.vendorCMSPageService = vendorCMSPageService;
	}

}
