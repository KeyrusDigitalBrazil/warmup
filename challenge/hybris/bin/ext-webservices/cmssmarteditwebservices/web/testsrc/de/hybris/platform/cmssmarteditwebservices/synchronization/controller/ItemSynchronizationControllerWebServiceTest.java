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
package de.hybris.platform.cmssmarteditwebservices.synchronization.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.data.ItemSynchronizationWsDTO;
import de.hybris.platform.cmssmarteditwebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmssmarteditwebservices.data.SynchronizationWsDTO;
import de.hybris.platform.cmssmarteditwebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;


@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ItemSynchronizationControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String SITE_ID = "siteId";
	private static final String TARGET_VERSION = "targetVersionId";
	private static final String SOURCE_VERSION = "sourceVersionId";
	private static final String CATALOG = "catalogId";
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}";


	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentCatalogModelMother contentCatalogModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private ParagraphComponentModelMother paragraphComponentModelMother;
	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;

	private CatalogVersionModel stagedCatalogVersionModel;
	private CatalogVersionModel onlineCatalogVersionModel;

	@Before
	public void setup()
	{
		stagedCatalogVersionModel = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		onlineCatalogVersionModel = catalogVersionModelMother.createAppleOnlineCatalogVersionModel();
		final ContentCatalogModel appleContentCatalogModel = contentCatalogModelMother
				.createAppleContentCatalogModel(stagedCatalogVersionModel, onlineCatalogVersionModel);

		siteModelMother.createElectronics(appleContentCatalogModel);

		final ContentPageModel contentPageModel = contentPageModelMother.homePage(stagedCatalogVersionModel);
		final ContentSlotForPageModel contentSlotForPageModel = contentSlotForPageModelMother
				.HeaderHomepage_ParagraphOnly(stagedCatalogVersionModel);
		final CMSParagraphComponentModel headerParagraphComponentModel = paragraphComponentModelMother
				.createHeaderParagraphComponentModel(stagedCatalogVersionModel);

		catalogVersionModelMother.performCatalogSyncronization(stagedCatalogVersionModel, onlineCatalogVersionModel);
	}

	@Test
	public void shouldGetPageSyncronizationStatus() throws Exception
	{
		final HashMap<String, String> variables = getUriVariableMap();

		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)) //
				.path("pages") //
				.path(ContentPageModelMother.UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SyncItemStatusWsDTO entity = response.readEntity(SyncItemStatusWsDTO.class);
		assertNotNull(entity.getItemId());
		assertNotNull(entity.getItemType());
		assertNotNull(entity.getName());
		assertNotNull(entity.getStatus());
		assertNotNull(entity.getName());
		assertThat(entity.getDependentItemTypesOutOfSync(), hasSize(3));
		assertThat(entity.getDependentItemTypesOutOfSync(),
				contains(hasProperty("itemType", is(ContentSlotModel._TYPECODE)),
						hasProperty("itemType", is(CMSParagraphComponentModel._TYPECODE)),
						hasProperty("itemType", is(ContentSlotForPageModel._TYPECODE))));

		assertThat(entity.getSelectedDependencies(), hasSize(1));
		assertThat(entity.getSelectedDependencies().get(0).getName(), is(ContentSlotModelMother.UID_HEADER));
		assertThat(entity.getSelectedDependencies().get(0).getSelectedDependencies(), hasSize(1));
		assertThat(entity.getSelectedDependencies().get(0).getSelectedDependencies().get(0).getName(),
				is(ParagraphComponentModelMother.UID_HEADER));
	}

	@Test
	public void shouldCreateItemSynchronizationForPageSlotAndParagraph() throws Exception
	{
		final HashMap<String, String> variables = getUriVariableMap();

		final SynchronizationWsDTO synchDto = new SynchronizationWsDTO();

		final ItemSynchronizationWsDTO page = getItemSynchronizationWsDTO(ContentPageModelMother.UID_HOMEPAGE,
				AbstractPageModel._TYPECODE);

		final ItemSynchronizationWsDTO slot = getItemSynchronizationWsDTO(ContentSlotModelMother.UID_HEADER,
				ContentSlotModel._TYPECODE);

		final ItemSynchronizationWsDTO paragraph = getItemSynchronizationWsDTO(ParagraphComponentModelMother.UID_HEADER,
				AbstractCMSComponentModel._TYPECODE);

		synchDto.setItems(Arrays.asList(page, slot, paragraph));

		final Response response = getWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(synchDto, MediaType.APPLICATION_JSON));

		assertResponse(Status.NO_CONTENT, response);
	}

	private ItemSynchronizationWsDTO getItemSynchronizationWsDTO(final String itemId, final String itemType)
	{
		final ItemSynchronizationWsDTO itemSynch = new ItemSynchronizationWsDTO();
		itemSynch.setItemId(itemId);
		itemSynch.setItemType(itemType);
		return itemSynch;
	}

	private HashMap<String, String> getUriVariableMap()
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(SITE_ID, SiteModelMother.ELECTRONICS);
		variables.put(CATALOG, ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name());
		variables.put(SOURCE_VERSION, STAGED.getVersion());
		variables.put(TARGET_VERSION, ONLINE.getVersion());
		return variables;
	}
}
