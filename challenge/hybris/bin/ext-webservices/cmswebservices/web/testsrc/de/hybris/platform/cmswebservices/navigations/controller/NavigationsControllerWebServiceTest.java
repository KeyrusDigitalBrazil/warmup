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
package de.hybris.platform.cmswebservices.navigations.controller;

import static de.hybris.platform.cms2.constants.Cms2Constants.ROOT;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.util.models.CMSNavigationNodeModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.NavigationEntryData;
import de.hybris.platform.cmswebservices.data.NavigationNodeData;
import de.hybris.platform.cmswebservices.data.NavigationNodeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class NavigationsControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/navigations";

	private static final String PARENT_UID_PARAM = "parentUid";
	private static final String ANCESTOR_TRAIL_FROM_PARAM = "ancestorTrailFrom";
	private static final String PARENT_UID = "parent-uid";
	private static final String NODE_UID_1 = "uid-1";
	private static final String CHILD_UID_1 = "child-uid-1";
	private static final String CHILD_UID_2 = "child-uid-2";
	private static final String CHILD_UID_3 = "child-uid-3";
	private static final String CHILD_UID_4 = "child-uid-4";
	private static final String PARENT_NAME = "parent-name";
	private static final String CODE_WITH_JPG_EXTENSION = "some-Media_Code.jpg";

	private CatalogVersionModel catalogVersion;

	@Resource
	private CMSNavigationNodeModelMother navigationNodeModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private MediaModelMother mediaModelMother;

	private CMSNavigationNodeModel rootNavigationNode;
	private CMSNavigationNodeModel node1;
	private CMSNavigationNodeModel navigationNodeChild4;

	@Before
	public void setup()
	{
		createSiteAndCatalog();

		final CMSNavigationNodeModel root = navigationNodeModelMother.createNavigationRootNode(catalogVersion);

		final MediaModel mediaModel = mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_JPG_EXTENSION);
		final CMSNavigationEntryModel entryModel= new CMSNavigationEntryModel();
		entryModel.setItem(mediaModel);
		entryModel.setName("entry name");
		entryModel.setUid("entry-uid");
		rootNavigationNode = navigationNodeModelMother.createNavigationNodeWithEntry(PARENT_NAME, PARENT_UID,
				root, "title-en-1", catalogVersion, entryModel);

		node1 = navigationNodeModelMother.createNavigationNode("name-1", NODE_UID_1, rootNavigationNode,
				"title-en-1", catalogVersion);

		navigationNodeModelMother.createNavigationNode("child-1", CHILD_UID_1, node1, "child-title-en-1",
				catalogVersion);
		navigationNodeModelMother.createNavigationNode("child-2", CHILD_UID_2, node1, "child-title-en-2",
				catalogVersion);
		navigationNodeModelMother.createNavigationNode("child-3", CHILD_UID_3, node1, "child-title-en-3",
				catalogVersion);

		navigationNodeChild4 = navigationNodeModelMother.createNavigationNode("child-4", CHILD_UID_4,
				node1, "child-title-en-4", catalogVersion);
	}

	@Test
	public void testGetAllNavigationNodes() throws CMSItemNotFoundException
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final List<NavigationNodeData> navigationNodes = response.readEntity(NavigationNodeListData.class).getNavigationNodes();

		assertThat(navigationNodes.size(), is(6));
	}


	@Test
	public void testGetRootlNavigationNodes() throws CMSItemNotFoundException
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(PARENT_UID_PARAM, ROOT) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final List<NavigationNodeData> navigationNodes = response.readEntity(NavigationNodeListData.class).getNavigationNodes();



		assertThat(navigationNodes.size(), is(1));

		assertNode(navigationNodes.get(0), PARENT_UID, ROOT, 0, true);
	}

	@Test
	public void testGetChildrenlNavigationNodes() throws CMSItemNotFoundException
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(PARENT_UID_PARAM, NODE_UID_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final List<NavigationNodeData> navigationNodes = response.readEntity(NavigationNodeListData.class).getNavigationNodes();

		assertThat(navigationNodes.size(), is(4));

		assertNode(navigationNodes.get(0), CHILD_UID_1, NODE_UID_1, 0, false);
		assertNode(navigationNodes.get(1), CHILD_UID_2, NODE_UID_1, 1, false);
		assertNode(navigationNodes.get(2), CHILD_UID_3, NODE_UID_1, 2, false);
		assertNode(navigationNodes.get(3), CHILD_UID_4, NODE_UID_1, 3, false);

	}


	@Test
	public void testGetOnelNavigationNode() throws CMSItemNotFoundException
	{
		final Map<String, String> map = new HashMap<>();
		map.put("uid", PARENT_UID);
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, map))
				.path(PARENT_UID)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final NavigationNodeData navigationNode = response.readEntity(NavigationNodeData.class);

		assertThat(navigationNode, notNullValue());

		assertNode(navigationNode, PARENT_UID, ROOT, 0, true);
		assertThat(navigationNode.getEntries().size(), is(1));
	}


	@Test
	public void testDeleteOnelNavigationNode() throws CMSItemNotFoundException
	{
		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		assertResponse(Response.Status.OK, getResponse);

		final Response deleteResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Response.Status.NO_CONTENT, deleteResponse);

		final Response secondGetResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		assertResponse(Response.Status.NOT_FOUND, secondGetResponse);
	}

	@Test
	public void testCreateOnelNavigationNode() throws CMSItemNotFoundException
	{
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setParentUid(PARENT_UID);
		navigationNode.setName("new navigation node");
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final NavigationNodeData navigationNodeResponse = response.readEntity(NavigationNodeData.class);

		assertThat(navigationNodeResponse.getUid(), notNullValue());
		assertThat(navigationNode.getParentUid(), is(PARENT_UID));
	}


	@Test
	public void testCreateOnelNavigationNodeWithUid() throws CMSItemNotFoundException
	{
		final String newUid = "newUid";
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setParentUid(PARENT_UID);
		navigationNode.setUid(newUid);
		navigationNode.setName("new navigation node");
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final NavigationNodeData navigationNodeResponse = response.readEntity(NavigationNodeData.class);

		assertThat(navigationNodeResponse.getUid(), notNullValue());
		assertThat(navigationNode.getParentUid(), is(PARENT_UID));
		assertThat(navigationNode.getUid(), is(newUid));
	}



	@Test
	public void testCreateOnelNavigationNodeWithUidAndEntries() throws CMSItemNotFoundException
	{
		final String newUid = "newUid";
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setParentUid(PARENT_UID);
		navigationNode.setUid(newUid);
		navigationNode.setName("new navigation node");
		final NavigationEntryData entry1 = new NavigationEntryData();
		entry1.setItemId(CODE_WITH_JPG_EXTENSION);
		entry1.setItemSuperType("Media");
		navigationNode.setEntries(Arrays.asList(entry1));

		final Map<String, String> params = new HashMap<>();
		params.put(CmswebservicesConstants.URI_SITE_ID, SiteModelMother.ELECTRONICS);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, params))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final NavigationNodeData navigationNodeResponse = response.readEntity(NavigationNodeData.class);

		assertThat(navigationNodeResponse.getUid(), notNullValue());
		assertThat(navigationNode.getParentUid(), is(PARENT_UID));
		assertThat(navigationNode.getUid(), is(newUid));
	}

	@Test
	public void testCreateOnelNavigationNodeWithUidAndEntrieWithErrors_shouldReturnValidationError() throws CMSItemNotFoundException
	{
		final String newUid = "newUid";
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setParentUid(PARENT_UID);
		navigationNode.setUid(newUid);
		navigationNode.setName("new navigation node");
		final NavigationEntryData entry1 = new NavigationEntryData();
		entry1.setItemId(CODE_WITH_JPG_EXTENSION);
		entry1.setItemSuperType("InvalidSuperType");
		navigationNode.setEntries(Arrays.asList(entry1));

		final Map<String, String> params = new HashMap<>();
		params.put(CmswebservicesConstants.URI_SITE_ID, SiteModelMother.ELECTRONICS);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, params))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void testCreateOnelNavigationNodeWithRootAsUid() throws CMSItemNotFoundException
	{
		final String newUid = "root";
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setParentUid(PARENT_UID);
		navigationNode.setUid(newUid);
		navigationNode.setName("new navigation node");
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);
	}


	@Test
	public void testUpdateNameForOnelNavigationNode() throws CMSItemNotFoundException
	{
		final String newParentName = "update-parent-name";

		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setUid(PARENT_UID);
		navigationNode.setParentUid(ROOT);
		navigationNode.setPosition(0);
		navigationNode.setName(newParentName);
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(PARENT_UID)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.OK, response);

		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(PARENT_UID)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		assertResponse(Response.Status.OK, getResponse);

		final NavigationNodeData data = getResponse.readEntity(NavigationNodeData.class);

		assertNode(data, PARENT_UID, ROOT, 0, true);
		assertThat(data.getName(), is(newParentName));
	}

	@Test
	public void testUpdatePositionForOnelNavigationNode() throws CMSItemNotFoundException
	{
		final String newChildName = "update-parent-name";

		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setUid(CHILD_UID_1);
		navigationNode.setParentUid(NODE_UID_1);
		// when we set the position to a very high number, the node is supposed to move to the last position on the list.
		navigationNode.setPosition(100);
		navigationNode.setName(newChildName);
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.OK, response);

		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		assertResponse(Response.Status.OK, getResponse);

		final NavigationNodeData data = getResponse.readEntity(NavigationNodeData.class);

		assertNode(data, CHILD_UID_1, NODE_UID_1, 3, false);
		assertThat(data.getName(), is(newChildName));
	}

	@Test
	public void testUpdatePositionAndParentForOnelNavigationNode_failsBecauseParentIsImmutable() throws CMSItemNotFoundException
	{
		final String newChildName = "update-parent-name";

		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setUid(CHILD_UID_1);
		navigationNode.setParentUid(ROOT);
		// when we set the position to a very high number, the node is supposed to move to the last position on the list.
		navigationNode.setPosition(100);
		navigationNode.setName(newChildName);
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CHILD_UID_1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(navigationNode, MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void testGetNavigationNodeAncestryWhenNodeIsRoot_shouldReturnEmptyList() throws CMSItemNotFoundException
	{
		final String nodeUid = ROOT;
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(ANCESTOR_TRAIL_FROM_PARAM, nodeUid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final List<NavigationNodeData> navigationNodes = response.readEntity(NavigationNodeListData.class).getNavigationNodes();

		assertThat(navigationNodes.size(), is(0));
	}


	@Test
	public void testGetNavigationNodeAncestryWhenNodeIsNotFound_shouldFailWithNotFound() throws CMSItemNotFoundException
	{
		final String nodeUid = "invalid-node-uid";
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(ANCESTOR_TRAIL_FROM_PARAM, nodeUid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.NOT_FOUND, response);
	}


	@Test
	public void testGetNavigationNodeAncestryWhenNodeIsChild_shouldReturnAncestorListAndSelf() throws CMSItemNotFoundException
	{
		final String nodeUid = CHILD_UID_4;
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(ANCESTOR_TRAIL_FROM_PARAM, nodeUid) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final List<NavigationNodeData> navigationNodes = response.readEntity(NavigationNodeListData.class).getNavigationNodes();

		assertThat(navigationNodes.size(), is(3));

		final Set<String> nodeUidsResponse = navigationNodes.stream().map(navigationNodeData -> navigationNodeData.getUid()).collect(
				Collectors.toSet());
		assertThat(nodeUidsResponse, contains(navigationNodeChild4.getUid(), node1.getUid(), rootNavigationNode.getUid()));
	}

	protected void assertNode(final NavigationNodeData data, final String expectedUid, final String expectedParentUid, final Integer expectedPosition, final boolean hasChildren)
	{
		assertThat(data.getUid(), is(expectedUid));
		assertThat(data.getPosition(), is(expectedPosition));
		assertThat(data.getHasChildren(), is(hasChildren));
		assertThat(data.getParentUid(), equalToIgnoringCase(expectedParentUid));
	}

	protected void createSiteAndCatalog()
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS);
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();

	}

}
