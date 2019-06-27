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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.marketplaceservices.vendor.impl.DefaultVendorCMSService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class DefaultVendorCMSStrategyTest extends ServicelayerTransactionalTest
{
	private static final String VENDOR_CODE = "default";
	private static final String PAGE_TEMPLATE_ID = "VendorLandingPageTemplate";
	private final static String CONTENT_CATALOG_ID_VALUE = "marketplaceContentCatalog";
	private final static String CONTENT_CATALOG_VERSION_VALUE = "Staged";

	private final static String POSITION_SECTION_2A = "Section2A";
	private final static String POSITION_SECTION_3 = "Section3";

	private final static String CONTENT_SLOT_ID_PATTERN = "BodyContentSlot-{0}-{1}";
	private final static String CONTENT_SLOT_NAME_PATTERN = "Content Slot for {0}";

	private final static String COMPONENT_ID = "testComponent";

	private DefaultVendorCMSStrategy vendorCMSStrategy;

	@Resource(name = "vendorCmsService")
	private DefaultVendorCMSService vendorCmsService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "vendorCMSPageService")
	private VendorCMSPageService vendorCMSPageService;

	private VendorModel vendor;

	private CatalogVersionModel catalogVersion;

	private CatalogModel catalog;

	private PageTemplateModel pageTemplate;

	private AbstractPageModel landingPage;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		vendorCMSStrategy = new DefaultVendorCMSStrategy();
		vendorCMSStrategy.setVendorCmsService(vendorCmsService);
		vendorCMSStrategy.setCatalogVersionService(catalogVersionService);
		vendorCMSStrategy.setModelService(modelService);
		vendorCMSStrategy.setConfigurationService(configurationService);
		vendorCMSStrategy.setVendorCMSPageService(vendorCMSPageService);

		vendor = new VendorModel();
		vendor.setCode(VENDOR_CODE);

		catalog = new CatalogModel();
		catalog.setId(CONTENT_CATALOG_ID_VALUE);

		catalogVersion = new CatalogVersionModel();
		catalogVersion.setVersion(CONTENT_CATALOG_VERSION_VALUE);
		catalogVersion.setCatalog(catalog);

		pageTemplate = new PageTemplateModel();
		pageTemplate.setUid(PAGE_TEMPLATE_ID);
		pageTemplate.setActive(true);
		pageTemplate.setCatalogVersion(catalogVersion);
		modelService.save(pageTemplate);

		landingPage = vendorCMSStrategy.prepareLandingPageForVendor(vendor);
	}

	@Test
	public void testCreateContentSlotAndForPage()
	{
		final ContentSlotModel contentSlot = vendorCMSStrategy.createContentSlotForPage(VENDOR_CODE, catalogVersion,
				POSITION_SECTION_2A, landingPage);
		final String contentSlotId = MessageFormat.format(CONTENT_SLOT_ID_PATTERN, VENDOR_CODE, POSITION_SECTION_2A);
		final String contentSlotName = MessageFormat.format(CONTENT_SLOT_NAME_PATTERN, POSITION_SECTION_2A);
		assertEquals(contentSlotId, contentSlot.getUid());
		assertEquals(contentSlotName, contentSlot.getName());
	}

	@Test
	public void testGetContentSlotByPositionAndCatalogVersion()
	{
		final String contentSlotId = MessageFormat.format(CONTENT_SLOT_ID_PATTERN, VENDOR_CODE, POSITION_SECTION_3);
		final String contentSlotName = MessageFormat.format(CONTENT_SLOT_NAME_PATTERN, POSITION_SECTION_3);
		final ContentSlotModel contentSlot = vendorCMSStrategy.getContentSlotByPositionAndCatalogVersion(vendor,
				POSITION_SECTION_3, catalogVersion);
		assertEquals(contentSlotId, contentSlot.getUid());
		assertEquals(contentSlotName, contentSlot.getName());
	}

	@Test
	public void testGetVendorProductCarouselComponents()
	{
		final ProductCarouselComponentModel testComponent = new ProductCarouselComponentModel();
		testComponent.setUid(COMPONENT_ID);
		testComponent.setCatalogVersion(catalogVersion);
		final ContentSlotModel contentSlot = vendorCMSStrategy.getContentSlotByPositionAndCatalogVersion(vendor, POSITION_SECTION_3,
				catalogVersion);
		testComponent.setSlots(Arrays.asList(contentSlot));
		modelService.save(testComponent);

		final List<AbstractCMSComponentModel> components = vendorCMSStrategy.getVendorProductCarouselComponents(vendor);
		assertEquals(1, components.size());
		assertEquals(COMPONENT_ID, components.get(0).getUid());
	}
}
