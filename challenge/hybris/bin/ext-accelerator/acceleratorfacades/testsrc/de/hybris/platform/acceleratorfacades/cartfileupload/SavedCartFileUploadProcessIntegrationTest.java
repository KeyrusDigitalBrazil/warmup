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
package de.hybris.platform.acceleratorfacades.cartfileupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cartfileupload.impl.DefaultSavedCartFileUploadFacade;
import de.hybris.platform.acceleratorservices.cartfileupload.events.SavedCartFileUploadEventListener;
import de.hybris.platform.acceleratorservices.enums.ImportStatus;
import de.hybris.platform.acceleratorservices.process.strategies.impl.DefaultSavedCartFileUploadStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


@Ignore // Fails randomly on CSP pipeline
@IntegrationTest
public class SavedCartFileUploadProcessIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(SavedCartFileUploadProcessIntegrationTest.class);
	private static final String PROCESS_DEFINITION_NAME = "savedCartFileUploadProcess";
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String savedCartEntriesQuery = "SELECT {" + CartModel.PK + "} FROM {" + CartModel._TYPECODE + "} "
			+ "WHERE {" + CartModel.USER + "} = ?user AND " + "{" + CartModel.SAVETIME + "} IS NOT NULL";

	@Resource
	private DefaultSavedCartFileUploadFacade savedCartFileUploadFacade;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private DefaultSaveCartFacade saveCartFacade;
	@Resource
	private SavedCartFileUploadEventListener savedCartFileUploadEventListener;
	@Resource
	private EventService eventService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private DefaultSavedCartFileUploadStrategy savedCartFileUploadStrategy;
	@Resource
	private ModelService modelService;


	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for CSV Saved cart upload ..");
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/acceleratorfacades/test/testSavedCartFromFileUploadSetup.impex", "utf-8");
		LOG.info("Finished creating data for CSV cart upload " + (System.currentTimeMillis() - startTime) + "ms");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(catalogVersionModel);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));
		eventService.registerEventListener(savedCartFileUploadEventListener);
	}


	@Test
	public void testSavedCartFromCSVFileUpload() throws IOException, InterruptedException
	{
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("user1@importcsvcart.com");
		userService.setCurrentUser(customerModel);
		final ClassPathResource resource = new ClassPathResource("/acceleratorfacades/test/testCSVFileUploadSavedCart.csv");
		savedCartFileUploadFacade.createCartFromFileUpload(resource.getInputStream(), "test.csv", "text/csv");

		waitForProcessToEnd(PROCESS_DEFINITION_NAME, 60000);

		assertEquals(userService.getCurrentUser().getUid(), customerModel.getUid());
		assertEquals(1, saveCartFacade.getSavedCartsCountForCurrentUser().intValue());

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(savedCartEntriesQuery);
		fQuery.addQueryParameter("user", userService.getCurrentUser());
		final SearchResult<CartModel> cartModelSearchResult = flexibleSearchService.search(fQuery);
		final CartModel cartModel = cartModelSearchResult.getResult().get(0);
		modelService.refresh(cartModel);
		assertEquals(ImportStatus.COMPLETED, cartModel.getImportStatus());
		assertEquals(3, cartModel.getEntries().size());
		assertEquals("HW1210-3423", cartModel.getEntries().get(0).getProduct().getCode());
		assertEquals("HW1210-3425", cartModel.getEntries().get(1).getProduct().getCode());
		assertEquals("26002000_1", cartModel.getEntries().get(2).getProduct().getCode());
	}


	@Test
	public void testSavedCartFromCSVFileUploadForIndex() throws IOException, InterruptedException
	{
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("user2@importcsvcart.com");
		userService.setCurrentUser(customerModel);
		final ClassPathResource resource = new ClassPathResource("/acceleratorfacades/test/testCSVFileUploadForSwapIndex.csv");
		savedCartFileUploadStrategy.setProductCodeIndex(Integer.valueOf(2));
		savedCartFileUploadStrategy.setQtyIndex(Integer.valueOf(1));
		savedCartFileUploadFacade.createCartFromFileUpload(resource.getInputStream(), "test1.csv", "text/csv");

		waitForProcessToEnd(PROCESS_DEFINITION_NAME, 120000);

		assertEquals(userService.getCurrentUser().getUid(), customerModel.getUid());
		assertEquals(1, saveCartFacade.getSavedCartsCountForCurrentUser().intValue());
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(savedCartEntriesQuery);
		fQuery.addQueryParameter("user", userService.getCurrentUser());
		final SearchResult<CartModel> cartModelSearchResult = flexibleSearchService.search(fQuery);
		final CartModel cartModel = cartModelSearchResult.getResult().get(0);
		modelService.refresh(cartModel);
		assertEquals(ImportStatus.COMPLETED, cartModel.getImportStatus());
		assertEquals(2, cartModel.getEntries().size());
		assertEquals("HW1210-3423", cartModel.getEntries().get(0).getProduct().getCode());
		assertEquals("HW1210-3425", cartModel.getEntries().get(1).getProduct().getCode());
	}

	@After
	public void after()
	{
		eventService.unregisterEventListener(savedCartFileUploadEventListener);
	}
}
