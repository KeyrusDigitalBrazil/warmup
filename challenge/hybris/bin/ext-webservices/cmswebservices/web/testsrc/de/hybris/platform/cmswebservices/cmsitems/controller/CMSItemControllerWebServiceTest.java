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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_HAS_VARIATIONS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENTS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_COMPONENT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTENT_SLOT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.LABEL_SEARCHPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_HOMEPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_PRIMARY_HOMEPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_PRIMARY_SEARCHPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother.UID_HEADER;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_VERSION;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CURRENT_PAGE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_PAGE_SIZE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_SORT;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_TYPECODE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.util.Locale.ENGLISH;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.model.TimeRestrictionDescription;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2lib.model.components.FlashComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.DateAttributeToDataContentConverter;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.uniqueidentifier.functions.DefaultCatalogVersionModelUniqueIdentifierConverter;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSNavigationEntryModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSNavigationNodeModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSTimeRestrictionModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.FlashComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSVersionWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CMSItemControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String ENDPOINT = "/v1/sites/{siteId}/cmsitems";
	private static final String ENDPOINT_VERSIONS = "/v1/sites/{siteId}/cmsitems/{itemUUID}/versions";
	private static final String RESPONSE = "response";
	private static final String CMS_COMPONENTS = "cmsComponents";

	private static final String CLONE_CONTENT_SLOT_NAME = "my-cloned-content-slot";
	private static final String CLONE_COMPONENT_NAME = "my-cloned-flash-component";
	private static final String NAVIGATION_NODE_NAME = "navigation-node-name";
	private static final String NAVIGATION_NODE_UID = "navigation-node-uid";
	private static final String NAVIGATION_NODE_TITLE = "navigation-node-title";
	private static final String PARENT_UID = "parent-uid";
	private static final String NODE_UID_1 = "uid-1";
	private static final String CHILD_UID_1 = "child-uid-1";
	private static final String CHILD_UID_2 = "child-uid-2";
	private static final String CHILD_UID_3 = "child-uid-3";
	private static final String CHILD_UID_4 = "child-uid-4";
	private static final String PARENT_NAME = "parent-name";
	private static final String CODE_WITH_JPG_EXTENSION = "some-Media_Code.jpg";
	private static final String PAGE_TITLE = "pageTitle";
	private static final String VERSION_UID = "versionId";
	private static final String VERSION_LABEL = "versionLabel";
	private static final String VERSION_DESCRIPTION = "versionDescription";
	private static final String URI_ITEM_UUID = "itemUUID";

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_CURRENT_PAGE = 0;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotModelMother contentSlotModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private CMSNavigationNodeModelMother navigationNodeModelMother;
	@Resource
	private CMSNavigationEntryModelMother navigationEntryModelMother;
	@Resource
	private LinkComponentModelMother linkComponentModelMother;
	@Resource
	private MediaModelMother mediaModelMother;
	@Resource
	private CMSTimeRestrictionModelMother timeRestrictionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private CMSItemConverter cmsItemConverter;
	@Resource
	private FlashComponentModelMother flashComponentModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	@Resource
	private ModelService modelService;
	@Resource
	private List<String> cmsTypeNonCloneableList;
	@Resource
	private Set<String> cmsStructureTypeBlacklistSet;
	@Resource
	private CMSVersionFacade cmsVersionFacade;
	@Resource
	private UserService userService;

	private CatalogVersionModel catalogVersion;

	protected void createElectronicsSite(final CatalogVersionModel... catalogVersions)
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersions);
	}

	protected CatalogVersionModel createEmptyAppleCatalog()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		// defines the store's supported languages
		baseStoreModelMother.createNorthAmerica(catalogVersion);

		return catalogVersion;
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink()
	{

		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		createElectronicsSite(cvm);

		contentSlotModelMother.createHeaderSlotWithParagraphAndLink(catalogVersion);
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction()
	{
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot header
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph and no type restriction
		contentSlotNameModelMother.Header_without_restriction(pageTemplate);
	}

	protected void createNavigationNodes()
	{
		final CMSNavigationNodeModel root = navigationNodeModelMother.createNavigationRootNode(catalogVersion);

		navigationNodeModelMother.createNavigationNode(NAVIGATION_NODE_NAME, NAVIGATION_NODE_UID, root, NAVIGATION_NODE_TITLE,
				catalogVersion);

		final MediaModel mediaModel = mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_JPG_EXTENSION);
		final CMSNavigationEntryModel entryModel = new CMSNavigationEntryModel();
		entryModel.setItem(mediaModel);
		entryModel.setName("entry name");
		entryModel.setUid("entry-uid");

		final CMSNavigationNodeModel rootNavigationNode = navigationNodeModelMother.createNavigationNodeWithEntry(PARENT_NAME,
				PARENT_UID, root, "title-en-1", catalogVersion, entryModel);

		final CMSNavigationNodeModel node1 = navigationNodeModelMother.createNavigationNode("name-1", NODE_UID_1,
				rootNavigationNode, "title-en-1", catalogVersion);

		navigationNodeModelMother.createNavigationNode("child-1", CHILD_UID_1, node1, "child-title-en-1", catalogVersion);
		navigationNodeModelMother.createNavigationNode("child-2", CHILD_UID_2, node1, "child-title-en-2", catalogVersion);
		navigationNodeModelMother.createNavigationNode("child-3", CHILD_UID_3, node1, "child-title-en-3", catalogVersion);

		navigationNodeModelMother.createNavigationNode("child-4", CHILD_UID_4, node1, "child-title-en-4", catalogVersion);
	}

	protected void createNavigationEntries()
	{
		final CMSLinkComponentModel linkComponentModel0 = linkComponentModelMother
				.createContentPageLinkComponentModel(catalogVersion);
		final CMSLinkComponentModel linkComponentModel1 = linkComponentModelMother
				.createContentPageLinkComponentModel(catalogVersion);

		// Navigation Entry without navigationNode associated to it
		navigationEntryModelMother.createEntryAndAddToNavigationNode(null, catalogVersion, linkComponentModel0,
				"CMSNavigationNodeEntry1");

		// Navigation node
		final CMSNavigationNodeModel node1 = navigationNodeModelMother.createNavigationNode("node-name-1", NODE_UID_1, null,
				"title-en-1", catalogVersion);
		// Navigation Entry with association to the navigation node 'node1'
		navigationEntryModelMother.createEntryAndAddToNavigationNode(node1, catalogVersion, linkComponentModel1,
				"CMSNavigationNodeEntry2");
	}

	protected Map<Object, Map<String, Object>> resultsToUidMap(final List<Map<String, Object>> results)
	{
		return results.stream().collect(Collectors.toMap(f -> f.get("uid"), Function.identity()));
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithNavigationTypeRestrictions()
	{
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot header
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph and link type restrictions
		contentSlotNameModelMother.Header(pageTemplate);
	}

	protected String getUuidForAppleStage(final String uid)
	{
		return getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				CatalogVersionModelMother.CatalogVersion.STAGED.getVersion(), uid);
	}

	protected String generateRandomRestrictionName()
	{
		return "restrictionName_" + UUID.randomUUID();
	}

	@Test
	public void shouldDeleteOneComponent() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(ParagraphComponentModelMother.UID_HEADER);

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Response.Status.NO_CONTENT, response);

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.NOT_FOUND, response);
	}

	@Test
	public void shouldGetOneComponent_Paragraph() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("content"), instanceOf(Map.class));
		assertThat(((Map<?, ?>) map.get("content")).get(ENGLISH.toLanguageTag()),
				is(ParagraphComponentModelMother.CONTENT_HEADER_EN));
	}


	@Test
	public void shouldGetOneSlot() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get(CMS_COMPONENTS), instanceOf(Collection.class));
		assertThat(((Collection<?>) map.get(CMS_COMPONENTS)).size(), is(2));
	}


	@Test
	public void shouldGetOnePage() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(PageTemplateModelMother.UID_HOME_PAGE);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
	}

	@Test
	public void shouldGetOneNavigationNode() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithNavigationTypeRestrictions();
		createNavigationNodes();

		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(PARENT_UID);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("children"), instanceOf(Collection.class));
		assertThat(((Collection<?>) map.get("children")).size(), is(1));
		assertThat(map.get("entries"), instanceOf(Collection.class));
		assertThat(((Collection<?>) map.get("entries")).size(), is(1));
	}

	@Test
	public void searchWithItemSearchParamsNavigationNodeNullShouldReturnOneNavigationEntry() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithNavigationTypeRestrictions();
		createNavigationEntries();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE) //
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId()) //
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()) //
				.queryParam(URI_TYPECODE, CMSNavigationEntryModel._TYPECODE) //
				.queryParam("itemSearchParams", CMSNavigationEntryModel.NAVIGATIONNODE + ": null").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);

		assertResponse(Response.Status.OK, response);

		assertEquals(1, items.size());
		assertThat(items.get(0).get("uid"), equalTo("CMSNavigationNodeEntry1"));
	}

	@Test
	public void searchWithNoMaskOrTypeShouldGetAllCMSItems()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);
		final Map<Object, Map<String, Object>> uidMap = resultsToUidMap(items);

		assertResponse(Response.Status.OK, response);

		assertEquals(4, items.size());
		assertTrue(uidMap.containsKey(ParagraphComponentModelMother.UID_HEADER));
		assertTrue(uidMap.containsKey(UID_HOMEPAGE));
		assertTrue(uidMap.containsKey(UID_HEADER));
		assertTrue(uidMap.containsKey(PageTemplateModelMother.UID_HOME_PAGE));
	}

	@Test
	public void searchWithNoMaskShouldGetAllCMSComponentsSortByNameASC()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion())
				.queryParam(URI_TYPECODE, AbstractCMSComponentModel._TYPECODE)
				.queryParam(URI_SORT, AbstractCMSComponentModel.NAME + ":" + SortDirection.ASC).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);

		assertResponse(Response.Status.OK, response);

		assertEquals(2, items.size());
		assertThat(items.get(0).get("uid"), equalTo(ParagraphComponentModelMother.UID_HEADER));
		assertThat(items.get(1).get("uid"), equalTo(FlashComponentModelMother.UID_HEADER));
	}

	@Test
	public void searchWithNoMaskShouldGetAllCMSComponentsSortByNameDESC()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion())
				.queryParam(URI_TYPECODE, AbstractCMSComponentModel._TYPECODE)
				.queryParam(URI_SORT, AbstractCMSComponentModel.NAME + ":" + SortDirection.DESC).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);

		assertResponse(Response.Status.OK, response);

		assertEquals(2, items.size());
		assertThat(items.get(0).get("uid"), equalTo(FlashComponentModelMother.UID_HEADER));
		assertThat(items.get(1).get("uid"), equalTo(ParagraphComponentModelMother.UID_HEADER));
	}

	@Test
	public void searchWithHomeMaskGetsHomePageAndHomePageTemplate()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).queryParam("mask", "home").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);
		final Map<Object, Map<String, Object>> uidMap = resultsToUidMap(items);

		assertResponse(Response.Status.OK, response);

		assertEquals(2, items.size());
		assertTrue(uidMap.containsKey(UID_HOMEPAGE));
		assertTrue(uidMap.containsKey(PageTemplateModelMother.UID_HOME_PAGE));
	}

	@Test
	public void searchWithLabelGetsHomePage()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).queryParam(URI_TYPECODE, ContentPageModel._TYPECODE)
				.queryParam("itemSearchParams", ContentPageModel.LABEL + ":" + ContentPageModelMother.LABEL_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);
		final Map<Object, Map<String, Object>> uidMap = resultsToUidMap(items);

		assertResponse(Response.Status.OK, response);

		assertEquals(1, items.size());
		assertTrue(uidMap.containsKey(UID_HOMEPAGE));
	}

	@Test
	public void searchWithParagraphComponentTypeReturnsOnlyTheOneInstance()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion())
				.queryParam(URI_TYPECODE, CMSParagraphComponentModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);
		final Map<Object, Map<String, Object>> uidMap = resultsToUidMap(items);

		assertResponse(Response.Status.OK, response);

		assertEquals(1, items.size());
		assertTrue(uidMap.containsKey(ParagraphComponentModelMother.UID_HEADER));
	}

	@Test
	public void searchWithBothMaskAndTypeReturnsBothFiltered()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).queryParam("typeCode", "CMSParagraphComponent")
				.queryParam("mask", "paragraph").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final Map<String, Object> results = response.readEntity(Map.class);
		final List<Map<String, Object>> items = (List<Map<String, Object>>) results.get(RESPONSE);
		final Map<Object, Map<String, Object>> uidMap = resultsToUidMap(items);

		assertResponse(Response.Status.OK, response);

		assertEquals(1, items.size());
		assertTrue(uidMap.containsKey(ParagraphComponentModelMother.UID_HEADER));
	}

	@Test
	public void searchWithUnknownTypeWillFailValidation()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).queryParam(URI_TYPECODE, "UnknownComponent").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void searchWithoutCatalogIdWillFailValidation()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void searchWithoutCatalogVersionWillFailValidation()
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldCreateOneContentSlot() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("active", "true");
		inputMap.put("name", "name-responsive-banner-header-logo");
		inputMap.put("urlLink", "url-responsive-banner-header-logo");
		inputMap.put("typeCode", "ContentSlot");
		inputMap.put("itemtype", "ContentSlot");

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("uuid"), notNullValue());
	}

	@Test
	public void shouldValidateForCreatingTimeRestrictionAndReturnComputedData() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final CMSTimeRestrictionModel restriction = new CMSTimeRestrictionModel();
		restriction.setActiveFrom(DateTime.now().toDate());
		restriction.setActiveUntil(DateTime.now().plusDays(5).toDate());
		restriction.setUseStoreTimeZone(Boolean.TRUE);

		final DateAttributeToDataContentConverter dateAttributeToDataContentConverter = new DateAttributeToDataContentConverter();

		final String restrictionName = generateRandomRestrictionName();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", "cms-time-restriction-name");
		inputMap.put("activeFrom", dateAttributeToDataContentConverter.convert(restriction.getActiveFrom()));
		inputMap.put("activeUntil", dateAttributeToDataContentConverter.convert(restriction.getActiveUntil()));
		inputMap.put("useStoreTimeZone", restriction.getUseStoreTimeZone());
		inputMap.put("itemtype", "CMSTimeRestriction");
		inputMap.put("name", restrictionName);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam("dryRun", "true").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.OK, response);

		Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("uuid"), nullValue());
		assertThat(map.get("uid"), nullValue());
		assertThat(map.get("name"), is(restrictionName));
		assertThat(map.get("description"), is(new TimeRestrictionDescription().get(restriction)));

		final Response responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE).queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE)
				.queryParam(URI_CATALOG_ID, catalogVersion.getCatalog().getId())
				.queryParam(URI_CATALOG_VERSION, catalogVersion.getVersion()).queryParam("mask", restrictionName).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		map = responseGet.readEntity(Map.class);
		final Collection<?> pagedResponse = (Collection) map.get(RESPONSE);
		assertResponse(Status.OK, responseGet);
		assertThat(pagedResponse, empty());
	}

	@Test
	public void shouldCreateOneContentPage() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		final String pageTemplateUuid = getUuid(catalogVersion.getCatalog().getId(), catalogVersion.getVersion(),
				pageTemplate.getUid());

		final CMSTimeRestrictionModel timeRestriction = timeRestrictionModelMother.today(catalogVersion);

		final Map<String, String> titleMap = new HashMap<>();
		titleMap.put(ENGLISH.toLanguageTag(), "custom content page test");

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("homepage", "false");
		inputMap.put("name", "name-custom-content-page");
		inputMap.put("masterTemplate", pageTemplateUuid);
		inputMap.put("defaultPage", "true");
		inputMap.put("restrictions", Arrays.asList(cmsItemConverter.convert(timeRestriction)));
		inputMap.put("onlyOneRestrictionMustApply", "false");
		inputMap.put("approvalStatus", CmsApprovalStatus.APPROVED);
		inputMap.put("title", titleMap);
		inputMap.put("itemtype", "ContentPage");
		inputMap.put("label", "somelabel");

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("uuid"), notNullValue());
	}

	@Test
	public void shouldCreateOneNavigationNode() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("visible", "false");
		inputMap.put("name", "name-custom-navigation-node");
		inputMap.put("typeCode", "CMSNavigationNode");
		inputMap.put("itemtype", "CMSNavigationNode");

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("uuid"), notNullValue());
	}

	@Test
	public void shouldUpdateOneParagraphComponent() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(ParagraphComponentModelMother.UID_HEADER);

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<String, Object> inputMap = response.readEntity(Map.class);
		final String newName = "new_paragraph_name";
		inputMap.put("name", newName);

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(uuid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("name"), is(newName));
	}

	@Test
	public void shouldValidateForUpdateOneParagraphComponent() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(ParagraphComponentModelMother.UID_HEADER);

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<String, Object> inputMap = response.readEntity(Map.class);
		final String oldName = (String) inputMap.get("name");
		final String newName = "new_paragraph_name";
		inputMap.put("name", newName);

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(uuid) //
				.queryParam("dryRun", "true").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.OK, response);

		Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), is(newName));

		final Response responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseGet);
		map = responseGet.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("name"), is(oldName));
	}

	@Test
	public void shouldFailUpdatePage_DuplicateLabelForPrimaryPage() throws JAXBException
	{
		// Create empty Apple catalog
		createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(catalogVersion);

		contentPageModelMother.primarySearchPage(catalogVersion);
		contentPageModelMother.primaryHomePage(catalogVersion);
		contentPageModelMother.homePage(catalogVersion);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(UID_PRIMARY_HOMEPAGE);

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<String, Object> inputMap = response.readEntity(Map.class);
		inputMap.put("uid", UID_PRIMARY_HOMEPAGE);
		inputMap.put("label", LABEL_SEARCHPAGE);

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(uuid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), not(empty()));
		final Set<String> subjects = result.getErrors().stream().map(ErrorWsDTO::getSubject).collect(Collectors.toSet());
		assertThat(subjects.contains(ContentPageModel.LABEL), is(true));
	}

	@Test
	public void shouldFailUpdatePage_PrimaryPageHasVariations() throws JAXBException
	{
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		contentPageModelMother.primarySearchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.searchPageFromHomePageTemplate(catalogVersion);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final String uuid = getUuidForAppleStage(UID_PRIMARY_SEARCHPAGE);

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final Map<String, Object> inputMap = response.readEntity(Map.class);
		inputMap.put("uid", ContentPageModelMother.UID_PRIMARY_SEARCHPAGE);
		inputMap.put("pageStatus", CmsPageStatus.DELETED.toString());

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(uuid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), iterableWithSize(2));
		final Set<String> subjects = result.getErrors().stream().map(ErrorWsDTO::getSubject).collect(Collectors.toSet());
		final Set<String> errorCodes = result.getErrors().stream().map(ErrorWsDTO::getErrorCode).collect(Collectors.toSet());
		assertThat(subjects.contains(AbstractPageModel.TYPECODE), is(true));
		assertThat(errorCodes.contains(DEFAULT_PAGE_HAS_VARIATIONS), is(true));
	}

	@Test
	public void shouldReturnLinkToggleContentForComponentModelThatContainsExternalAndUrlLinkFields() throws JAXBException
	{
		// GIVEN
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		// WHEN
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(), catalogVersion.getVersion(),
						FlashComponentModelMother.UID_HEADER)) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));

		assertThat(map.containsKey("external"), is(false));
		assertThat(map.containsKey("urlLink"), is(false));

		final Map<String, Object> linkToggle = (HashMap<String, Object>) map.get(CmsfacadesConstants.FIELD_LINK_TOGGLE_NAME);
		assertThat(linkToggle.get("external"), is(false));
		assertThat(linkToggle.get("urlLink"), is(FlashComponentModelMother.URL_LINK_HEADER));
	}

	protected Map<String, String> getLocalizedContent(final String value)
	{
		final Map<String, String> localizedMap = new HashMap<>();
		localizedMap.put(ENGLISH.getLanguage(), value);
		return localizedMap;
	}

	@Test
	public void shouldNotCloneFlashComponentBlacklistedType() throws Exception
	{
		// clone blacklisted component
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);
		mediaModelMother.createLogoMediaModel(catalogVersion);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourceComponentUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), FlashComponentModelMother.UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceComponentUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_COMPONENT_NAME);
		inputMap.put("itemtype", FlashComponentModel._TYPECODE);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
		assertThat(errors.getErrors().get(0).getMessage(),
				equalTo("Component cannot be cloned. Its type belongs to the nonCloneableTypeList or to the typeBlacklistSet."));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCloneFlashComponentWithUpdateToNameHeightAndMedia() throws Exception
	{
		cmsStructureTypeBlacklistSet.remove(FlashComponentModel._TYPECODE);

		// clone component and modify name, height and English media
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);
		mediaModelMother.createLogoMediaModel(catalogVersion);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourceComponentUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), FlashComponentModelMother.UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceComponentUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_COMPONENT_NAME); // changed
		inputMap.put("itemtype", FlashComponentModel._TYPECODE);
		inputMap.put("height", 500); // changed
		final Map<String, String> mediaMap = new HashMap<>();
		mediaMap.put("en", getUuidForAppleStage(MediaModelMother.MediaTemplate.LOGO.getCode())); // added
		mediaMap.put("fr", null);
		inputMap.put("media", mediaMap);
		final Map<String, Object> linkMap = new HashMap<>();
		linkMap.put("external", false);
		linkMap.put("urlLink", FlashComponentModelMother.URL_LINK_HEADER);
		inputMap.put("linkToggle", linkMap);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), equalTo(CLONE_COMPONENT_NAME));
		assertThat(map.get("height"), equalTo(500));
		assertThat(((Map<?, ?>) map.get("media")).get("en"), notNullValue());
		assertThat(((Map<?, ?>) map.get("media")).get("fr"), nullValue());

		cmsStructureTypeBlacklistSet.add(FlashComponentModel._TYPECODE);
	}

	@Test
	public void shouldCloneFlashComponentWithUpdateToNameLinkToogleAndMedia() throws Exception
	{
		cmsStructureTypeBlacklistSet.remove(FlashComponentModel._TYPECODE);

		// clone component and modify name, linkToggle and French media
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		final MediaModel logoMediaModel = mediaModelMother.createLogoMediaModel(catalogVersion);
		final FlashComponentModel flashComponentModel = flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);
		flashComponentModel.setHeight(500);
		flashComponentModel.setMedia(logoMediaModel, Locale.ENGLISH);
		modelService.save(flashComponentModel);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourceComponentUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), FlashComponentModelMother.UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceComponentUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_COMPONENT_NAME); // changed
		inputMap.put("itemtype", FlashComponentModel._TYPECODE);
		inputMap.put("height", 500);
		final Map<String, String> mediaMap = new HashMap<>();
		mediaMap.put("en", getUuidForAppleStage(MediaModelMother.MediaTemplate.LOGO.getCode()));
		mediaMap.put("fr", getUuidForAppleStage(MediaModelMother.MediaTemplate.LOGO.getCode())); // added
		inputMap.put("media", mediaMap);
		final Map<String, Object> linkMap = new HashMap<>();
		linkMap.put("external", true); // changed
		final String newUrlLinkValue = "my-cloned-" + FlashComponentModelMother.URL_LINK_HEADER;
		linkMap.put("urlLink", newUrlLinkValue); // changed
		inputMap.put("linkToggle", linkMap);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), equalTo(CLONE_COMPONENT_NAME));
		assertThat(((Map<?, ?>) map.get("media")).get("en"), notNullValue());
		assertThat(((Map<?, ?>) map.get("media")).get("fr"), notNullValue());
		assertThat(((Map<?, ?>) map.get("linkToggle")).get("external"), is(Boolean.TRUE));
		assertThat(((Map<?, ?>) map.get("linkToggle")).get("urlLink"), equalTo(newUrlLinkValue));

		cmsStructureTypeBlacklistSet.add(FlashComponentModel._TYPECODE);
	}

	@Test
	public void shouldCloneFlashComponentWithUpdateToNameAndRemoveMedia() throws Exception
	{
		cmsStructureTypeBlacklistSet.remove(FlashComponentModel._TYPECODE);

		// clone component, update name and removed French media
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		final MediaModel logoMediaModel = mediaModelMother.createLogoMediaModel(catalogVersion);
		final FlashComponentModel flashComponentModel = flashComponentModelMother.createHeaderFlashComponentModel(catalogVersion);
		flashComponentModel.setHeight(500);
		flashComponentModel.setMedia(logoMediaModel, Locale.ENGLISH);
		flashComponentModel.setMedia(logoMediaModel, Locale.FRENCH);
		modelService.save(flashComponentModel);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourceComponentUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), FlashComponentModelMother.UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_COMPONENT_UUID, sourceComponentUuid);
		inputMap.put(FIELD_CLONE_COMPONENT, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_COMPONENT_NAME); // changed
		inputMap.put("itemtype", FlashComponentModel._TYPECODE);
		inputMap.put("height", 500);
		final Map<String, String> mediaMap = new HashMap<>();
		mediaMap.put("en", getUuidForAppleStage(MediaModelMother.MediaTemplate.LOGO.getCode()));
		mediaMap.put("fr", null); // removed fr media
		inputMap.put("media", mediaMap);
		final Map<String, Object> linkMap = new HashMap<>();
		linkMap.put("external", false);
		linkMap.put("urlLink", FlashComponentModelMother.URL_LINK_HEADER);
		inputMap.put("linkToggle", linkMap);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(((Map<?, ?>) map.get("media")).get("en"), notNullValue());
		assertThat(((Map<?, ?>) map.get("media")).get("fr"), nullValue());
		assertThat(map.get("name"), equalTo(CLONE_COMPONENT_NAME));

		cmsStructureTypeBlacklistSet.add(FlashComponentModel._TYPECODE);
	}

	@Test
	public void shouldCloneContentSlotAndParagraphComponent() throws Exception
	{
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourcePageUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HOMEPAGE);

		final String sourceContentSlotUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_PAGE_UUID, sourcePageUuid);
		inputMap.put(FIELD_CONTENT_SLOT_UUID, sourceContentSlotUuid);
		inputMap.put(FIELD_CLONE_COMPONENTS, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_CONTENT_SLOT_NAME);
		inputMap.put("itemtype", ContentSlotModel._TYPECODE);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), equalTo(CLONE_CONTENT_SLOT_NAME));

		final List<String> cmsComponents = (List<String>) map.get(CMS_COMPONENTS);
		assertThat(cmsComponents.isEmpty(), is(false));
		assertThat(cmsComponents.get(0), not(ParagraphComponentModelMother.UID_HEADER));
	}

	@Test
	public void shouldCloneContentSlotAndExcludeAllComponents() throws Exception
	{
		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourcePageUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HOMEPAGE);

		final String sourceContentSlotUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_PAGE_UUID, sourcePageUuid);
		inputMap.put(FIELD_CONTENT_SLOT_UUID, sourceContentSlotUuid);
		inputMap.put(FIELD_CLONE_COMPONENTS, false);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_CONTENT_SLOT_NAME);
		inputMap.put("itemtype", ContentSlotModel._TYPECODE);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), equalTo(CLONE_CONTENT_SLOT_NAME));

		final List<String> cmsComponents = (List<String>) map.get(CMS_COMPONENTS);
		assertThat(cmsComponents.isEmpty(), is(true));
	}

	@Test
	public void shouldCloneContentSlotAndExcludeParagraphComponent() throws Exception
	{
		cmsTypeNonCloneableList.add(CMSParagraphComponentModel._TYPECODE);

		// Create empty Apple catalog
		final CatalogVersionModel cvm = createEmptyAppleCatalog();
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite(cvm);

		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);

		final String catalogUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final String sourcePageUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HOMEPAGE);

		final String sourceContentSlotUuid = getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				catalogVersion.getVersion(), UID_HEADER);

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(FIELD_PAGE_UUID, sourcePageUuid);
		inputMap.put(FIELD_CONTENT_SLOT_UUID, sourceContentSlotUuid);
		inputMap.put(FIELD_CLONE_COMPONENTS, true);
		inputMap.put(URI_CATALOG_VERSION, catalogUuid);
		inputMap.put("name", CLONE_CONTENT_SLOT_NAME);
		inputMap.put("itemtype", ContentSlotModel._TYPECODE);

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final Map<String, Object> map = response.readEntity(Map.class);
		assertThat(map.get("uuid"), notNullValue());
		assertThat(map.get("name"), equalTo(CLONE_CONTENT_SLOT_NAME));

		final List<String> cmsComponents = (List<String>) map.get(CMS_COMPONENTS);
		assertThat(cmsComponents.isEmpty(), is(true));

		cmsTypeNonCloneableList.remove(CMSParagraphComponentModel._TYPECODE);
	}

	@Test
	public void shouldFindCmsItemsByUuids() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final String uuid = getUuidForAppleStage(ParagraphComponentModelMother.UID_HEADER);

		final String catalogVersionUuid = catalogVersion.getCatalog().getId()
				+ DefaultCatalogVersionModelUniqueIdentifierConverter.SEPARATOR + catalogVersion.getVersion();

		final Map<String, Object> inputMap = new HashMap<>();
		inputMap.put(URI_CATALOG_VERSION, catalogVersionUuid);
		inputMap.put("uuids", Arrays.asList(uuid));

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path("uuids").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(inputMap, MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map.get("response"), notNullValue());
	}

	@Test
	public void shouldGetItemForVersion() throws Exception
	{

		// GIVEN
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();
		final String uuid = getUuidForAppleStage(UID_HOMEPAGE);

		final CMSVersionWsDTO createdEntity = createVersion(uuid, VERSION_LABEL, VERSION_DESCRIPTION);

		// WHEN
		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint).path(uuid).queryParam(VERSION_UID, createdEntity.getUid()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Status.OK, response);

		final Map<?, ?> map = response.readEntity(Map.class);
		assertThat(map.isEmpty(), is(false));
		assertThat(map, allOf(hasKey(ItemModel.CREATIONTIME), hasKey(ItemModel.MODIFIEDTIME)));
		assertThat(map.get("uuid"), equalTo(uuid));

	}

	protected CMSVersionWsDTO createVersion(final String itemUUID, final String label, final String description) throws Exception
	{

		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");

		final Map<String, String> variables = new HashMap<>();
		variables.put(URI_ITEM_UUID, itemUUID);
		final String versionEndPoint = replaceUriVariablesWithDefaults(ENDPOINT_VERSIONS, variables);

		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		dto.setLabel(label);
		dto.setDescription(description);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		return response.readEntity(CMSVersionWsDTO.class);
	}

}
