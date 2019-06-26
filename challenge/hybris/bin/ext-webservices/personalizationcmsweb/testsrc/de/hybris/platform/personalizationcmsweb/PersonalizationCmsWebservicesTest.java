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
package de.hybris.platform.personalizationcmsweb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.personalizationcmsweb.data.CxCmsActionData;
import de.hybris.platform.personalizationcmsweb.data.CxCmsComponentContainerData;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.CustomizationListWsDTO;
import de.hybris.platform.personalizationwebservices.data.QueryParamsWsDTO;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class PersonalizationCmsWebservicesTest extends BaseWebServiceTest
{
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";
	private static final String PATH = "/v1/query/cxReplaceComponentWithContainer";
	private static final String CUSTOMIZATION_UPDATE_RANK = "/v1/query/cxUpdateCustomizationRank";
	private static final String componentToPersonalize = "bannerHomePage1";
	private static final String slotId = "Section1Slot-Homepage";
	private static final String catalog = "testCatalog";
	private static final String catalogVersion = "Online";
	private static final String pageId = "homepage";
	private static final String newActionCode = "newAction";
	private static final String newActionComponentId = "newPersonalizedBanner";
	private static final String CONTAINER_CLEANUP_ENABLED_PROPERTY = "personalizationcms.containers.cleanup.enabled";

	private String containerCleanupEnabled;

	private static final Logger LOG = LoggerFactory.getLogger(PersonalizationCmsWebservicesTest.class);

	@Resource
	private CMSPageDao cmsPageDao;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ConfigurationService configurationService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationcmsweb/test/personalizationcmsweb_testdata.impex", "UTF-8"));
		containerCleanupEnabled = (String) configurationService.getConfiguration().getProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY);
		configurationService.getConfiguration().setProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY, Boolean.TRUE.toString());
	}

	@After
	public void cleanup()
	{
		configurationService.getConfiguration().setProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY, containerCleanupEnabled);
	}

	@Test
	public void shouldReplaceHomepageBannerComponentOnPage() throws IOException, JAXBException
	{
		replaceComponentOnPageAndAssertItExists("bannerHomePage1", 0, "homepage", "Section1Slot-Homepage", "testCatalog", "Online");
	}


	@Test
	public void shouldReplaceHomepageBannerComponentOnPageTemplate() throws IOException, JAXBException
	{
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("oldComponentId", "bannerHomePage2");
		params.getParams().put("slotId", "templateSlot1");
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CxCmsComponentContainerData data = unmarshallResult(response, CxCmsComponentContainerData.class);
		Assert.assertNotNull(data.getUid());
		Assert.assertEquals(data.getSourceId(), data.getUid());
		Assert.assertEquals("bannerHomePage2", data.getDefaultComponentUid());

		//additional verifications in database
		final List<AbstractPageModel> pages = cmsPageDao.findPagesById("homepage",
				Arrays.asList(catalogVersionService.getCatalogVersion("testCatalog", "Online")));
		Assert.assertEquals(pages.size(), 1);
		final AbstractPageModel page = pages.get(0);
		Assert.assertEquals(page.getMasterTemplate().getContentSlots().size(), 1);
		final ContentSlotForTemplateModel slot = page.getMasterTemplate().getContentSlots().get(0);
		Assert.assertEquals(slot.getContentSlot().getCmsComponents().size(), 1);
		final AbstractCMSComponentModel component = slot.getContentSlot().getCmsComponents().get(0);
		Assert.assertTrue(component instanceof CxCmsComponentContainerModel);
		Assert.assertEquals(data.getUid(), component.getUid());
		Assert.assertNotNull(((CxCmsComponentContainerModel) component).getDefaultCmsComponent());
		Assert.assertEquals("bannerHomePage2", ((CxCmsComponentContainerModel) component).getDefaultCmsComponent().getUid());
	}

	@Test
	public void shouldAddAndRemovePersonalizationOnComponent() throws IOException, JAXBException
	{
		final CxCmsComponentContainerData containerData = replaceComponentOnPageAndAssertItExists(componentToPersonalize, 0, pageId,
				slotId, catalog, catalogVersion);

		//CREATE ACTION
		createActionOnContainerAndAssertItExists(newActionCode, newActionComponentId, containerData.getSourceId());

		//DELETE LAST ACTION
		deleteActionAndAssertItIsGone(newActionCode);

		//assert that the container is gone and the default component is back on the page
		final ContentSlotModel slot = getSlot(pageId, catalog, catalogVersion, slotId);
		Assert.assertEquals(slot.getCmsComponents().size(), 2);
		final AbstractCMSComponentModel component2 = slot.getCmsComponents().get(0);
		Assert.assertTrue(component2 instanceof SimpleResponsiveBannerComponentModel);

		assertContainerIsDeleted(containerData.getSourceId());
	}

	@Test
	public void shouldAdd1ActionAndRemoveVariation() throws IOException, JAXBException
	{
		//CREATE CONTAINER
		final CxCmsComponentContainerData containerData = replaceComponentOnPageAndAssertItExists(componentToPersonalize, 0, pageId,
				slotId, catalog, catalogVersion);

		//CREATE ACTION
		createActionOnContainerAndAssertItExists(newActionCode, newActionComponentId, containerData.getSourceId());

		//DELETE LAST ACTION
		deleteVariationAndAssertItIsGone();

		//assert that the container is gone and the default component is back on the page
		final ContentSlotModel slot = getSlot(pageId, catalog, catalogVersion, slotId);
		Assert.assertEquals(slot.getCmsComponents().size(), 2);
		final AbstractCMSComponentModel component2 = slot.getCmsComponents().get(0);
		Assert.assertTrue(component2 instanceof SimpleResponsiveBannerComponentModel);

		assertContainerIsDeleted(containerData.getSourceId());
	}

	@Test
	public void shouldAdd1ActionAndRemoveCustomization() throws IOException, JAXBException
	{
		//CREATE CONTAINER
		final CxCmsComponentContainerData containerData = replaceComponentOnPageAndAssertItExists(componentToPersonalize, 0, pageId,
				slotId, catalog, catalogVersion);

		//CREATE ACTION
		createActionOnContainerAndAssertItExists(newActionCode, newActionComponentId, containerData.getSourceId());

		//DELETE LAST ACTION
		deleteCustomizationAndAssertItIsGone();

		//assert that the container is gone and the default component is back on the page
		final ContentSlotModel slot = getSlot(pageId, catalog, catalogVersion, slotId);
		Assert.assertEquals(slot.getCmsComponents().size(), 2);
		final AbstractCMSComponentModel component2 = slot.getCmsComponents().get(0);
		Assert.assertTrue(component2 instanceof SimpleResponsiveBannerComponentModel);

		assertContainerIsDeleted(containerData.getSourceId());
	}

	@Test
	public void shouldAdd2AndRemove1Action() throws IOException, JAXBException
	{
		final String actionCode1 = "newAction1";
		final String actionCode2 = "newAction2";

		//CREATE CONTAINER
		final CxCmsComponentContainerData containerData = replaceComponentOnPageAndAssertItExists(componentToPersonalize, 0, pageId,
				slotId, catalog, catalogVersion);

		//CREATE 2 ACTIONS
		final String componentId = "newPersonalizedBanner";
		createActionOnContainerAndAssertItExists(actionCode1, componentId, containerData.getSourceId());
		createActionOnContainerAndAssertItExists(actionCode2, componentId, containerData.getSourceId());

		//DELETE ACTION
		deleteActionAndAssertItIsGone(actionCode1);

		//additional verifications in database
		final ContentSlotModel slot = getSlot(pageId, catalog, catalogVersion, slotId);
		Assert.assertEquals(slot.getCmsComponents().size(), 2);
		final AbstractCMSComponentModel component = slot.getCmsComponents().get(0);
		Assert.assertTrue(component instanceof CxCmsComponentContainerModel);
		Assert.assertEquals(containerData.getUid(), component.getUid());
		Assert.assertNotNull(((CxCmsComponentContainerModel) component).getDefaultCmsComponent());
		Assert.assertEquals("bannerHomePage1", ((CxCmsComponentContainerModel) component).getDefaultCmsComponent().getUid());
	}

	@Test
	public void shouldUpdateCustomizationRankAndNothingChange() throws JAXBException
	{
		//given
		final Builder builder = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.build();
		//check customization rank before update
		final Response responseBeforUpdate = builder.get();
		WebservicesAssert.assertResponse(Status.OK, responseBeforUpdate);
		CustomizationListWsDTO customizations = responseBeforUpdate.readEntity(CustomizationListWsDTO.class);
		final Integer rankBeforUpdate = getCustomizationRank(customizations, "customization1");
		assertEquals(0, rankBeforUpdate.intValue());
		//prepare parameters
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("customization", "customization1");
		params.getParams().put("increaseValue", "-1");
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		//when
		//update customization rank
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_UPDATE_RANK)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.OK, response);
		//then
		//check customization rank after update -> nothing change customization with highest rank
		final Response responseAfterUpdate = builder.get();
		WebservicesAssert.assertResponse(Status.OK, responseAfterUpdate);
		customizations = responseAfterUpdate.readEntity(CustomizationListWsDTO.class);
		final Integer rankAfterUpdate = customizations.getCustomizations().stream()
				.filter(a -> a.getCode().equals("customization1")).findFirst().get().getRank();
		assertEquals(0, rankAfterUpdate.intValue());
	}

	@Test
	public void shouldUpdateCustomizationRankToHigher() throws JAXBException
	{
		//given
		final Builder builder = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.build();
		//check customization rank before update
		final Response responseBeforUpdate = builder.get();
		WebservicesAssert.assertResponse(Status.OK, responseBeforUpdate);
		CustomizationListWsDTO customizations = responseBeforUpdate.readEntity(CustomizationListWsDTO.class);
		final Integer rankBeforUpdate = getCustomizationRank(customizations, "customization5");
		assertEquals(4, rankBeforUpdate.intValue());
		//prepare parameters
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("customization", "customization5");
		params.getParams().put("increaseValue", "-1");
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		//when
		//update customization rank
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_UPDATE_RANK)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.OK, response);
		//then
		//check customization rank after update -> rank should be higher
		final Response responseAfterUpdate = builder.get();
		WebservicesAssert.assertResponse(Status.OK, responseAfterUpdate);
		customizations = responseAfterUpdate.readEntity(CustomizationListWsDTO.class);
		final Integer rankAfterUpdate = customizations.getCustomizations().stream()
				.filter(a -> a.getCode().equals("customization5")).findFirst().get().getRank();
		assertEquals(2, rankAfterUpdate.intValue());
	}


	@Test
	public void shouldReturnValidationErrorOnUpdateCustomizationRankWhenNotANumber() throws JAXBException
	{
		//prepare parameters
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("customization", "customization1");
		params.getParams().put("increaseValue", "notAnumber");
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		//when
		//update customization rank
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_UPDATE_RANK)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("ValidationError", error1.getType());
	}

	@Test
	public void shouldReturnValidationErrorOnUpdateCustomizationRankWhenNotInteger() throws JAXBException
	{
		//prepare parameters
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("customization", "customization1");
		params.getParams().put("increaseValue", "0.5");
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "catalogVersion");
		//when
		//update customization rank
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_UPDATE_RANK)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("ValidationError", error1.getType());
	}

	protected Integer getCustomizationRank(final CustomizationListWsDTO customizations, final String customizationCode)
	{
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());

		final Optional<CustomizationData> customization = customizations.getCustomizations().stream()
				.filter(a -> a.getCode().equals(customizationCode)).findFirst();
		assertTrue(customization.isPresent());
		return customization.get().getRank();
	}

	protected void deleteVariationAndAssertItIsGone()
	{
		Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

		response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	protected void deleteCustomizationAndAssertItIsGone()
	{
		Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

		response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	protected void deleteActionAndAssertItIsGone(final String actionCode1)
	{
		Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(actionCode1)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

		response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(actionCode1)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}


	protected CxCmsComponentContainerData replaceComponentOnPageAndAssertItExists(final String componentToPersonalize,
			final int componentPosition, final String pageId, final String slotId, final String catalog, final String catalogVersion)
			throws JAXBException
	{
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put("oldComponentId", componentToPersonalize);
		params.getParams().put("slotId", slotId);
		params.getParams().put("catalog", catalog);
		params.getParams().put("catalogVersion", catalogVersion);

		//CREATE CONTAINER
		final Response response = getWsSecuredRequestBuilderForCmsManager().path(PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CxCmsComponentContainerData containerData = unmarshallResult(response, CxCmsComponentContainerData.class);
		Assert.assertNotNull(containerData.getSourceId());
		Assert.assertEquals(componentToPersonalize, containerData.getDefaultComponentUid());

		//additional verifications in database
		final ContentSlotModel slot = getSlot(pageId, catalog, catalogVersion, slotId);
		Assert.assertEquals(slot.getCmsComponents().size(), 2);
		final AbstractCMSComponentModel component = slot.getCmsComponents().get(componentPosition);
		Assert.assertTrue(component instanceof CxCmsComponentContainerModel);
		Assert.assertEquals(containerData.getUid(), component.getUid());
		Assert.assertNotNull(((CxCmsComponentContainerModel) component).getDefaultCmsComponent());
		Assert.assertEquals(componentToPersonalize, ((CxCmsComponentContainerModel) component).getDefaultCmsComponent().getUid());

		return containerData;
	}


	protected ContentSlotModel getSlot(final String pageId, final String catalog, final String catalogVersion, final String slotId)
	{
		final List<AbstractPageModel> pages = cmsPageDao.findPagesById(pageId,
				Arrays.asList(catalogVersionService.getCatalogVersion(catalog, catalogVersion)));
		Assert.assertEquals(1, pages.size());

		final AbstractPageModel page = pages.get(0);

		Assert.assertEquals(2, page.getContentSlots().size());
		List<ContentSlotForPageModel> pageModelList = page.getContentSlots().stream()
				.filter(c -> slotId.equals(c.getContentSlot().getUid()))
				.collect(Collectors.toList());
		Assert.assertEquals(1, pageModelList.size());

		final ContentSlotModel slot = pageModelList.get(0).getContentSlot();

		modelService.refresh(slot);
		return slot;
	}



	protected void createActionOnContainerAndAssertItExists(final String actionCode, final String componentId,
			final String containerUid)
	{

		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("code", actionCode);
		actionAttributes.put("componentId", componentId);
		actionAttributes.put("containerId", containerUid);

		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build()//
				.post(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);
		final String location = response.getHeaderString("Location");
		assertTrue(location.contains("newAction"));
		CxCmsActionData action = response.readEntity(CxCmsActionData.class);
		assertEquals(actionCode, action.getCode());
		assertEquals(componentId, action.getComponentId());
		assertEquals(containerUid, action.getContainerId());

		action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(actionCode)//
				.build()//
				.get(CxCmsActionData.class);
		assertEquals(actionCode, action.getCode());
		assertEquals(componentId, action.getComponentId());
		assertEquals(containerUid, action.getContainerId());
	}

	protected void assertContainerIsDeleted(final String containerUid)
	{
		//check that the container was deleted
		final CxCmsComponentContainerModel example = new CxCmsComponentContainerModel();
		example.setUid(containerUid);
		boolean modelNotFoundThrown = false;
		try
		{
			flexibleSearchService.getModelByExample(example);
		}
		catch (final ModelNotFoundException e)
		{
			modelNotFoundThrown = true;
		}
		assertTrue("container is still in the database", modelNotFoundThrown);
	}

}
