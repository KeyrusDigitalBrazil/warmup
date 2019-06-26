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
package de.hybris.platform.cmswebservices.cmsitems.controller;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CURRENT_CONTEXT_CATALOG;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CURRENT_CONTEXT_CATALOG_VERSION;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CURRENT_CONTEXT_SITE_ID;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.MULTI_COUNTRY_ID_CARS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.MULTI_COUNTRY_ID_EUROPE_CARS;
import static de.hybris.platform.cmsfacades.util.models.SiteModelMother.MULTI_COUNTRY_EUROPE_CARS_SITE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.impl.DefaultUniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;



@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CMSItemControllerForMultiCountryWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String CATALOG_VERSION = "catalogVersion";
	private static final String CLONE_COMPONENTS = "cloneComponents";
	private static final String DEFAULT_PAGE = "defaultPage";
	private static final String EN = "en";
	private static final String ITEMTYPE = "itemtype";
	private static final String LABEL = "label";
	private static final String MASTER_TEMPLATE = "masterTemplate";
	private static final String NAME = "name";
	private static final String PAGE_UUID = "pageUuid";
	private static final String SITE_ID = "siteId";
	private static final String TITLE = "title";
	private static final String TYPE = "type";
	private static final String UID = "uid";
	private static final String URI_CONTEXT = "uriContext";
	private static final String APPROVAL_STATUS = "approvalStatus";

	private static final String ENDPOINT = "/v1/sites/{siteId}/cmsitems";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private DefaultUniqueItemIdentifierService cmsUniqueItemIdentifierService;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;

	@Before
	public void start() throws ImpExException
	{
		importCsv("/cmswebservices/test/impex/essentialMultiCountryTestDataAuth.impex", "utf-8");
	}

	@Test
	public void shouldOverrideExistingPrimaryContentPageAfterCloning()
	{
		// GIVEN
		final CatalogVersionModel originalCatalogVersionModel = catalogVersionModelMother
				.createCarGlobalOnlineCatalogVersionModel();
		final ContentPageModel originalContentPageModel = contentPageModelMother.primaryHomePage(originalCatalogVersionModel);

		final CatalogVersionModel targetCatalogVersionModel = catalogVersionModelMother.createCarEuropeStagedCatalogVersionModel();
		final ContentPageModel targetContentPageModel = contentPageModelMother.primaryHomePage(targetCatalogVersionModel);
		final String targetContentPageUid = targetContentPageModel.getUid();

		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(originalCatalogVersionModel);
		final String pageTemplateUuid = getUuid(originalCatalogVersionModel.getCatalog().getId(),
				originalCatalogVersionModel.getVersion(), pageTemplate.getUid());

		final String targetCatalogVersion = MULTI_COUNTRY_ID_EUROPE_CARS.name() + "/" + STAGED.getVersion();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(CATALOG_VERSION, targetCatalogVersion);
		inputMap.put(DEFAULT_PAGE, true);
		inputMap.put(TYPE, "contentPageData");
		inputMap.put(ITEMTYPE, "ContentPage");
		inputMap.put(APPROVAL_STATUS, "APPROVED");
		inputMap.put(LABEL, targetContentPageModel.getLabel());
		inputMap.put(UID, "");
		cmsUniqueItemIdentifierService.getItemData(originalContentPageModel).ifPresent(itemData -> {
			inputMap.put(PAGE_UUID, itemData.getItemId());
		});
		inputMap.put(NAME, "fakeName");

		final Map<String, String> titleMap = new HashMap<>();
		titleMap.put(EN, "fakeTitle");
		inputMap.put(TITLE, titleMap);

		final Map<String, String> cloneContext = new HashMap<>();
		cloneContext.put(CURRENT_CONTEXT_SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);
		cloneContext.put(CURRENT_CONTEXT_CATALOG, MULTI_COUNTRY_ID_CARS.name());
		cloneContext.put(CURRENT_CONTEXT_CATALOG_VERSION, STAGED.getVersion());
		inputMap.put(URI_CONTEXT, cloneContext);
		inputMap.put(CLONE_COMPONENTS, false);
		inputMap.put(MASTER_TEMPLATE, pageTemplateUuid);

		final Map<String, String> endPointParams = new HashMap<>();
		endPointParams.put(SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);

		// WHEN
		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, endPointParams);
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Response.Status.CREATED, response);
		final Map<String, Object> map = response.readEntity(Map.class);
		assertFalse("CMSItemControllerForMultiCountryWebServiceTest should generate new uid for cloned page",
				map.get(UID).equals(targetContentPageUid));
		assertTrue("CMSItemControllerForMultiCountryWebServiceTest should use the same label for cloned page",
				map.get(LABEL).equals(originalContentPageModel.getLabel()));
		assertTrue("CMSItemControllerForMultiCountryWebServiceTest should create cloned page in the target catalog version",
				map.get(CATALOG_VERSION).equals(targetCatalogVersion));
	}

	@Test
	public void shouldOverrideExistingPrimaryProductPageAfterCloning()
	{
		// GIVEN
		final CatalogVersionModel originalCatalogVersionModel = catalogVersionModelMother
				.createCarGlobalOnlineCatalogVersionModel();
		final ProductPageModel originalProductPageModel = productPageModelMother.primaryProductPage(originalCatalogVersionModel,
				CmsPageStatus.ACTIVE);

		final CatalogVersionModel targetCatalogVersionModel = catalogVersionModelMother.createCarEuropeStagedCatalogVersionModel();
		final ProductPageModel targetProductPageModel = productPageModelMother.primaryProductPage(targetCatalogVersionModel,
				CmsPageStatus.ACTIVE);
		final String targetProductPageUid = targetProductPageModel.getUid();

		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(originalCatalogVersionModel);
		final String pageTemplateUuid = getUuid(originalCatalogVersionModel.getCatalog().getId(),
				originalCatalogVersionModel.getVersion(), pageTemplate.getUid());

		final String targetCatalogVersion = MULTI_COUNTRY_ID_EUROPE_CARS.name() + "/" + STAGED.getVersion();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(CATALOG_VERSION, targetCatalogVersion);
		inputMap.put(DEFAULT_PAGE, true);
		inputMap.put(TYPE, "contentProductData");
		inputMap.put(ITEMTYPE, "ProductPage");
		inputMap.put(APPROVAL_STATUS, "APPROVED");
		inputMap.put(UID, "");
		cmsUniqueItemIdentifierService.getItemData(originalProductPageModel).ifPresent(itemData -> {
			inputMap.put(PAGE_UUID, itemData.getItemId());
		});
		inputMap.put(NAME, "fakeName");

		final Map<String, String> titleMap = new HashMap<>();
		titleMap.put(EN, "fakeTitle");
		inputMap.put(TITLE, titleMap);

		final Map<String, String> cloneContext = new HashMap<>();
		cloneContext.put(CURRENT_CONTEXT_SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);
		cloneContext.put(CURRENT_CONTEXT_CATALOG, MULTI_COUNTRY_ID_CARS.name());
		cloneContext.put(CURRENT_CONTEXT_CATALOG_VERSION, STAGED.getVersion());
		inputMap.put(URI_CONTEXT, cloneContext);
		inputMap.put(CLONE_COMPONENTS, false);
		inputMap.put(MASTER_TEMPLATE, pageTemplateUuid);

		final Map<String, String> endPointParams = new HashMap<>();
		endPointParams.put(SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);

		// WHEN
		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, endPointParams);
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Response.Status.CREATED, response);
		final Map<String, Object> map = response.readEntity(Map.class);
		assertFalse("CMSItemControllerForMultiCountryWebServiceTest should generate new uid for cloned page",
				map.get(UID).equals(targetProductPageUid));
		assertTrue("CMSItemControllerForMultiCountryWebServiceTest should use the same label for cloned page",
				map.get(ITEMTYPE).equals(originalProductPageModel.getItemtype()));
		assertTrue("CMSItemControllerForMultiCountryWebServiceTest should create cloned page in the target catalog version",
				map.get(CATALOG_VERSION).equals(targetCatalogVersion));
	}
}
