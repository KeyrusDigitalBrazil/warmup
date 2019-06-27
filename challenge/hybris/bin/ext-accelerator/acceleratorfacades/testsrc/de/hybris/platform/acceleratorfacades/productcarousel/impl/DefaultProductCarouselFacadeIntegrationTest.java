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
package de.hybris.platform.acceleratorfacades.productcarousel.impl;

import static de.hybris.platform.cms2.misc.CMSFilter.PREVIEW_TICKET_ID_PARAM;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Utilities;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;


@IntegrationTest
public class DefaultProductCarouselFacadeIntegrationTest extends ServicelayerTest
{

	@Resource(name = "defaultProductCarouselFacade")
	private DefaultProductCarouselFacade defaultProductCarouselFacade;

	@Resource
	private SessionService sessionService;

	@Resource
	private ModelService modelService;

	@Resource
	private CMSAdminComponentService cmsAdminComponentService;

	@Resource
	private CMSComponentService cmsComponentService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private TypeService typeService;

	@Resource
	private UserService userService;

	@Resource
	private CMSPreviewService cmsPreviewService;

	@Resource
	private CommonI18NService commonI18NService;

	private ProductCarouselComponentModel carouselModel;

	private CatalogVersionModel contentCatalogVersionModel;

	private CatalogVersionModel productCatalogVersionModel;

	private PreviewDataModel previewDataModel;

	private CMSVersionModel cmsVersionData;

	private final CMSPreviewTicketModel cmsPreviewTicketModel = new CMSPreviewTicketModel();

	@BeforeClass
	public static void prepare() throws Exception
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();

		final ApplicationContext appCtx = Registry.getApplicationContext();
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		if (beanFactory.getRegisteredScope("tenant") == null)
		{
			beanFactory.registerScope("tenant", new de.hybris.platform.spring.TenantScope());
		}
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		xmlReader.loadBeanDefinitions(new ClassPathResource("product-carousel-spring-test.xml"));
	}

	@Before
	public void setUp() throws Exception
	{

		importCsv("/acceleratorfacades/test/testProductCarouselFacade.impex", "utf-8");

		contentCatalogVersionModel = catalogVersionService.getCatalogVersion("testContentCatalog", "Online");
		productCatalogVersionModel = catalogVersionService.getCatalogVersion("testProductCatalog", "Online");

		catalogVersionService.setSessionCatalogVersions(Arrays.asList(contentCatalogVersionModel, productCatalogVersionModel));

		carouselModel = (ProductCarouselComponentModel) cmsComponentService.getAbstractCMSComponent("testProductCarouselComponent");

		userService.setCurrentUser(userService.getUserForUID("anonymous"));

	}

	@After
	public void tearDown()
	{

		catalogVersionService.setSessionCatalogVersions(Arrays.asList());
		searchRestrictionService.enableSearchRestrictions();

	}

	@Test
	public void testCollectProducts_not_preview()
	{

		sessionService.setAttribute(PREVIEW_TICKET_ID_PARAM, null);

		final List<ProductData> products = defaultProductCarouselFacade.collectProducts(carouselModel);

		assertThat(products, hasSize(4)); // retrieves only Online (Session catalog) versioned products
		assertThat(products, hasItem(hasProperty("code", equalTo("product07"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product08"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product09"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product10"))));

	}

	@Test
	public void testCollectProducts_in_preview()
	{

		sessionService.setAttribute(PREVIEW_TICKET_ID_PARAM, "cmsTicketId");

		cmsPreviewTicketModel.setId(PREVIEW_TICKET_ID_PARAM);
		modelService.save(cmsPreviewTicketModel);

		cmsPreviewService.storePreviewTicket(cmsPreviewTicketModel);

		final List<ProductData> products = defaultProductCarouselFacade.collectProducts(carouselModel);

		assertThat(products, hasSize(9));
		assertThat(products, hasItem(hasProperty("code", equalTo("product01"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product02"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product04"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product05"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product06"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product07"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product08"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product09"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product10"))));


	}

	@Test
	public void testCollectProducts_in_versionpreview()
	{

		prepareVersionData();
		preparePreviewData();
		sessionService.setAttribute(PREVIEW_TICKET_ID_PARAM, "cmsTicketId");

		cmsPreviewTicketModel.setId(PREVIEW_TICKET_ID_PARAM);
		cmsPreviewTicketModel.setPreviewData(previewDataModel);
		modelService.save(cmsPreviewTicketModel);

		cmsPreviewService.storePreviewTicket(cmsPreviewTicketModel);

		final List<ProductData> products = defaultProductCarouselFacade.collectProducts(carouselModel);

		assertThat(products, hasSize(9));
		assertThat(products, hasItem(hasProperty("code", equalTo("product01"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product02"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product04"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product05"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product06"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product07"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product08"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product09"))));
		assertThat(products, hasItem(hasProperty("code", equalTo("product10"))));


	}

	protected void prepareVersionData()
	{
		cmsVersionData = new CMSVersionModel();
		cmsVersionData.setUid("00000001");
		cmsVersionData.setItemUid("testProductCarouselComponent");
		cmsVersionData.setItemTypeCode("ProductCarouselComponent");
		cmsVersionData.setItemCatalogVersion(contentCatalogVersionModel);
		cmsVersionData.setPayload("some payload");
		modelService.save(cmsVersionData);
	}

	protected void preparePreviewData()
	{
		previewDataModel = new PreviewDataModel();
		previewDataModel.setLiveEdit(Boolean.TRUE);
		previewDataModel.setCatalogVersions(Arrays.asList(contentCatalogVersionModel, productCatalogVersionModel));
		previewDataModel.setVersion(cmsVersionData);
		modelService.save(previewDataModel);
	}

}
