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
/**
 *
 */
package de.hybris.platform.personalizationcmsweb;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationcmsweb.data.CxCmsActionData;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.ActionListWsDTO;
import de.hybris.platform.personalizationwebservices.data.QueryParamsWsDTO;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class PersonalizationCmsWebservicesSecurityTest extends BaseWebServiceTest
{
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";
	private static final String ACTIONS_URI = CUSTOMIZATION_ENDPOINT + "/" + CUSTOMIZATION + "/" + VARIATION_ENDPOINT + "/"
			+ VARIATION + "/" + ACTION_ENDPOINT;
	private static final String ACTION_URI = ACTIONS_URI + "/" + ACTION;
	private static final String PATH = "/v1/query/cxReplaceComponentWithContainer";
	private static final String CUSTOMIZATION_UPDATE_RANK = "/v1/query/cxUpdateCustomizationRank";
	private static final String CONTAINERS_FROM_VARIATION = "/v1/query/cxcmscomponentsfromvariations";


	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationcmsweb/test/webcontext_testdata.impex", "UTF-8"));
		importData(new ClasspathImpExResource("/personalizationcmsweb/test/personalizationcmsweb_testdata.impex", "UTF-8"));
	}

	@Test
	public void getCmsActionPermissionsCheckAdmin() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getCmsAction(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void getCmsActionPermissionsCheckCmsManager() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getCmsAction(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getCmsActionPermissionsCheckReadOnly() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getCmsAction(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getCmsActionPermissionsCheckNoAccess() throws IOException
	{
		WebservicesAssert.assertForbiddenError(getCmsAction(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getCmsActionPermissionsCheckCustomer() throws IOException
	{
		WebservicesAssert.assertForbiddenError(getCmsAction(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getCmsAction(final String user, final String pwd) throws IOException
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTION_URI)//
				.build()//
				.get();
	}


	@Test
	public void createCmsActionPermissionsCheckAdmin() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createCmsAction(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void createCmsActionPermissionsCheckCmsManager() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createCmsAction(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionPermissionsCheckReadOnly() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsAction(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionPermissionsCheckNoAccess() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsAction(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionPermissionsCheckCustomer() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsAction(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response createCmsAction(final String user, final String pwd) throws IOException, JAXBException
	{
		final CxCmsActionData actionData = new CxCmsActionData();
		actionData.setComponentId("bannerHomePage2");
		actionData.setContainerId(CONTAINER);
		final ActionListWsDTO list = new ActionListWsDTO();
		list.setActions(Arrays.asList(actionData));

		return getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTIONS_URI)//
				.build()//
				.post(Entity.entity(marshallDto(actionData, CxCmsActionData.class), MediaType.APPLICATION_JSON));
	}

	@Test
	public void createCmsActionsPermissionsCheckAdmin() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createCmsActions(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void createCmsActionsPermissionsCheckCmsManager() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createCmsActions(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionsPermissionsCheckReadOnly() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsActions(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionsPermissionsCheckNoAccess() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsActions(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void createCmsActionsPermissionsCheckCustomer() throws IOException, JAXBException
	{
		WebservicesAssert.assertForbiddenError(createCmsActions(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}


	protected Response createCmsActions(final String user, final String pwd) throws IOException, JAXBException
	{
		final CxCmsActionData actionData = new CxCmsActionData();
		actionData.setComponentId("bannerHomePage1");
		actionData.setContainerId(CONTAINER);
		final ActionListWsDTO list = new ActionListWsDTO();
		list.setActions(Arrays.asList(actionData));
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTIONS_URI)//
				.build() //
				.method("PATCH", Entity.entity(marshallDto(list, ActionListWsDTO.class), MediaType.APPLICATION_JSON));
	}

	@Test
	public void deleteCmsActionsPermissionsCheckAdmin() throws IOException
	{
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteCmsActions(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void deleteCmsActionsPermissionsCheckCmsManager() throws IOException
	{
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteCmsActions(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionsPermissionsCheckReadOnly() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsActions(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionsPermissionsCheckNoAccess() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsActions(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionsPermissionsCheckCustomer() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsActions(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}


	protected Response deleteCmsActions(final String user, final String pwd) throws IOException
	{
		final String[] actionIds = new String[]
		{ "action1", "action2", "action3" };

		return getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTION_URI)//
				.build()//
				.method("DELETE", Entity.entity(actionIds, MediaType.APPLICATION_JSON));

	}

	@Test
	public void updateCmsActionPermissionsCheckAdmin() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, updateCmsAction(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void updateCmsActionPermissionsCheckCmsManager() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, updateCmsAction(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCmsActionPermissionsCheckReadOnly() throws IOException
	{
		WebservicesAssert.assertForbiddenError(updateCmsAction(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCmsActionPermissionsCheckNoAccess() throws IOException
	{
		WebservicesAssert.assertForbiddenError(updateCmsAction(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCmsActionPermissionsCheckCustomer() throws IOException
	{
		WebservicesAssert.assertForbiddenError(updateCmsAction(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}


	protected Response updateCmsAction(final String user, final String pwd) throws IOException
	{
		final String newActionComponentId = "newComponent";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("componentId", newActionComponentId);
		actionAttributes.put("containerId", CONTAINER);


		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));
	}

	@Test
	public void deleteCmsActionPermissionsCheckAdmin() throws IOException
	{
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteCmsAction(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void deleteCmsActionPermissionsCheckCmsManager() throws IOException
	{
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteCmsAction(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionPermissionsCheckReadOnly() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsAction(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionPermissionsCheckNoAccess() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsAction(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void deleteCmsActionPermissionsCheckCustomer() throws IOException
	{
		WebservicesAssert.assertForbiddenError(deleteCmsAction(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}


	protected Response deleteCmsAction(final String user, final String pwd) throws IOException
	{
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTION_URI)//
				.build()//
				.delete();
		return response;
	}

	@Test
	public void getAllActionsForVariationPermissionsCheckAdmin()
	{
		WebservicesAssert.assertResponse(Status.OK, getAllActionsForVariation(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void getAllActionsForVariationPermissionsCheckCmsManerg()
	{
		WebservicesAssert.assertResponse(Status.OK, getAllActionsForVariation(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllActionsForVariationPermissionsCheckReadOnly()
	{
		WebservicesAssert.assertResponse(Status.OK, getAllActionsForVariation(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllActionsForVariationPermissionsCheckNoAccess()
	{
		WebservicesAssert.assertForbiddenError(getAllActionsForVariation(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllActionsForVariationPermissionsCheckCustomer()
	{
		WebservicesAssert.assertForbiddenError(getAllActionsForVariation(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getAllActionsForVariation(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(ACTIONS_URI)//
				.build()//
				.get();
	}


	@Test
	public void replaceComponentOnPagePermissionsCheckAdmin() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, replaceComponentOnPageFor(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void replaceComponentOnPagePermissionsCheckCmsManager() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, replaceComponentOnPageFor(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void replaceComponentOnPagePermissionsCheckReadOnly() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(replaceComponentOnPageFor(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void replaceComponentOnPagePermissionsCheckNoAccess() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(replaceComponentOnPageFor(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void replaceComponentOnPagePermissionsCheckCustomer() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(replaceComponentOnPageFor(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response replaceComponentOnPageFor(final String user, final String pwd) throws JAXBException
	{
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<String, String>());
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		params.getParams().put("oldComponentId", "bannerHomePage1");
		params.getParams().put("slotId", "Section1Slot-Homepage");

		//CREATE CONTAINER
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));
		return response;
	}

	@Test
	public void updateCustomizationRankPermissionsCheckAdmin() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, updateCustomizationRank(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void updateCustomizationRankPermissionsCheckCmsManager() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, updateCustomizationRank(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCustomizationRankPermissionsCheckReadOnly() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(updateCustomizationRank(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCustomizationRankPermissionsCheckNoAccess() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(updateCustomizationRank(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void updateCustomizationRankPermissionsCheckCustomer() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(updateCustomizationRank(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}


	protected Response updateCustomizationRank(final String user, final String pwd) throws JAXBException
	{
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<String, String>());
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		params.getParams().put("increaseValue", "1");
		params.getParams().put("customization", CUSTOMIZATION);
		//update customization rank
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_UPDATE_RANK)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));
	}

	@Test
	public void getAllContainersFromVariationPermissionsCheckAdmin() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllContainersFromVariation(ADMIN_USERNAME, ADMIN_PASSWORD));
	}

	@Test
	public void getAllContainersFromVariationPermissionsCheckCmsManager() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllContainersFromVariation(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllContainersFromVariationPermissionsCheckReadOnly() throws JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK,
				getAllContainersFromVariation(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllContainersFromVariationPermissionsCheckNoAccess() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(getAllContainersFromVariation(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
	}

	@Test
	public void getAllContainersFromVariationPermissionsCheckCustomer() throws JAXBException
	{
		WebservicesAssert.assertForbiddenError(getAllContainersFromVariation(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));

	}

	protected Response getAllContainersFromVariation(final String user, final String pwd) throws JAXBException
	{
		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<String, String>());
		params.getParams().put("catalog", "testCatalog");
		params.getParams().put("catalogVersion", "Online");
		params.getParams().put("customization", CUSTOMIZATION);
		params.getParams().put("variations", VARIATION);

		//update customization rank
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CONTAINERS_FROM_VARIATION)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));

	}


}
