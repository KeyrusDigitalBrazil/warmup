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

import static org.junit.Assert.*;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationcmsweb.data.CxCmsActionData;
import de.hybris.platform.personalizationfacades.data.ActionData;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.ActionFullListWsDTO;
import de.hybris.platform.personalizationwebservices.data.ActionListWsDTO;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class CmsActionsWebServiceTest extends BaseWebServiceTest
{
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";
	private static final String ACTIONS_FULL_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/actions";

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationcmsweb/test/webcontext_testdata.impex", "UTF-8"));
	}

	@Test
	public void getActionWithConditions() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(ACTIONS_FULL_ENDPOINT)//
				.queryParam("type", "CXCMSACTION") //
				.queryParam("containerId", "container1") //
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final ActionFullListWsDTO actions = response.readEntity(ActionFullListWsDTO.class);
		assertNotNull(actions);
		assertNotNull(actions.getActions());
		assertEquals(5, actions.getActions().size());
	}

	@Test
	public void getActionWithMultipleConditions() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(ACTIONS_FULL_ENDPOINT)//
				.queryParam("type", "CXCMSACTION") //
				.queryParam("variationName", "N1") //
				.queryParam("containerId", "container1")//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final ActionFullListWsDTO actions = response.readEntity(ActionFullListWsDTO.class);
		assertNotNull(actions);
		assertNotNull(actions.getActions());
		assertEquals(5, actions.getActions().size());
	}

	@Test
	public void getCmsActionForCmsManager() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();


		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final ActionData action = response.readEntity(ActionData.class);
		assertNotNull(action);
		assertEquals(ACTION, action.getCode());
	}

	@Test
	public void getCmsActionForAdmin() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForAdmin()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final ActionData action = response.readEntity(ActionData.class);
		assertNotNull(action);
		assertEquals(ACTION, action.getCode());
	}


	@Test
	public void createCmsAction() throws IOException
	{
		//given
		final String newActionCode = "newAction";
		final String newActionComponentId = "newComponent";

		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("code", newActionCode);
		actionAttributes.put("componentId", newActionComponentId);
		actionAttributes.put("componentCatalog", "testCatalog");
		actionAttributes.put("containerId", CONTAINER);

		//when
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
		assertEquals(newActionCode, action.getCode());
		assertEquals(newActionComponentId, action.getComponentId());
		assertEquals(CONTAINER, action.getContainerId());
		assertEquals("testCatalog", action.getComponentCatalog());


		action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path("newAction")//
				.build()//
				.get(CxCmsActionData.class);
		assertEquals(newActionCode, action.getCode());
		assertEquals(newActionComponentId, action.getComponentId());
		assertEquals(CONTAINER, action.getContainerId());
		assertEquals("testCatalog", action.getComponentCatalog());

	}

	@Test
	public void createAutomaticCmsAction() throws IOException
	{
		//given
		final String newActionComponentId = "newComponent";

		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("componentId", newActionComponentId);
		actionAttributes.put("componentCatalog", "testCatalog");
		actionAttributes.put("containerId", CONTAINER);

		//when
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
		final CxCmsActionData action = response.readEntity(CxCmsActionData.class);
		assertEquals(newActionComponentId, action.getComponentId());
		assertEquals(CONTAINER, action.getContainerId());
		assertEquals("testCatalog", action.getComponentCatalog());


		final CxCmsActionData getAction = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(action.getCode())//
				.build()//
				.get(CxCmsActionData.class);
		assertEquals(action.getCode(), getAction.getCode());
		assertEquals(newActionComponentId, getAction.getComponentId());
		assertEquals(CONTAINER, getAction.getContainerId());
		assertEquals("testCatalog", action.getComponentCatalog());

	}

	@Test
	public void createCmsActions() throws IOException
	{
		//given
		final String newActionCode = "newAction";
		final String newActionComponentId = "newComponent";

		final Object action1 = createCmsActionForJSON(newActionCode, newActionComponentId, CONTAINER);
		final Object action2 = createCmsActionForJSON(newActionCode + "1", newActionComponentId + "1", CONTAINER);
		final Object actionList = CreateCmsActionListForJSON(action1, action2);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build() //
				.method("PATCH", Entity.entity(actionList, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);

		final ActionListWsDTO actionListResult = response.readEntity(ActionListWsDTO.class);
		assertNotNull(actionListResult);
		assertNotNull(actionListResult.getActions());
		assertEquals(2, actionListResult.getActions().size());

		final CxCmsActionData action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path("newAction")//
				.build()//
				.get(CxCmsActionData.class);
		assertEquals(newActionCode, action.getCode());
		assertEquals(newActionComponentId, action.getComponentId());
		assertEquals(CONTAINER, action.getContainerId());
		assertEquals("testCatalog", action.getComponentCatalog());
	}

	@Test
	public void createEmptyCmsActionList() throws IOException
	{
		//given
		final Object actionList = CreateCmsActionListForJSON();

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build() //
				.method("PATCH", Entity.entity(actionList, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);
	}


	@Test
	public void createIncompleateCmsActions() throws IOException
	{
		//given
		final String newActionCode = "newAction";
		final String newActionComponentId = "newComponent";

		final Object action1 = createCmsActionForJSON(newActionCode, null, CONTAINER);
		final Object action2 = createCmsActionForJSON(newActionCode + "1", newActionComponentId + "1", CONTAINER);
		final Object actionList = CreateCmsActionListForJSON(action1, action2);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build() //
				.method("PATCH", Entity.entity(actionList, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		//action 1 was not created
		final Response result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(newActionCode)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result);
		//action 2 was not created
		final Response result2 = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(newActionCode + "1")//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result2);
	}

	protected Object createCmsActionForJSON(final String code, final String component, final String container)
	{
		final HashMap<String, String> action = new HashMap<>();
		action.put("type", "cxCmsActionData");
		action.put("code", code);
		action.put("componentId", component);
		action.put("containerId", container);
		action.put("componentCatalog", "testCatalog");

		return action;
	}

	protected Object CreateCmsActionListForJSON(final Object... actions)
	{
		final HashMap<String, Object> result = new HashMap<>();
		result.put("actions", actions);

		return result;
	}

	@Test
	public void deleteCmsActions() throws IOException
	{
		//given
		final String[] actionIds = new String[]
		{ "action1", "action2", "action3" };

		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build()//
				.method("DELETE", Entity.entity(actionIds, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

		//action2 is not present
		Response result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path("action1")//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result);
		ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());

		//action2 is not present
		result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path("action2")//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result);
		errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());

		//action3 is not present
		result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path("action3")//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result);
		errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());

	}

	@Test
	public void deleteNotexistingCmsActions() throws IOException
	{
		//given
		final String[] actionIds = new String[]
		{ "action100", "action200", "action300" };

		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build()//
				.method("DELETE", Entity.entity(actionIds, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

	}

	@Test
	public void updateCmsActionTypeWithOtherType() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "testActionData");


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("TypeConflictError", error1.getType());
	}

	@Test
	public void updateCmsActionTypeWithUnknownType() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "unknowntype");


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("HttpMessageNotReadableError", error1.getType());
	}

	@Test
	public void updateCmsAction() throws IOException
	{
		//give
		final String newActionComponentId = "newComponent";

		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("componentId", newActionComponentId);
		actionAttributes.put("componentCatalog", "testCatalog2");
		actionAttributes.put("containerId", CONTAINER);


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CxCmsActionData action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get(CxCmsActionData.class);

		assertEquals(ACTION, action.getCode());
		assertEquals(newActionComponentId, action.getComponentId());
		assertEquals("testCatalog2", action.getComponentCatalog());
	}

	@Test
	public void deleteCmsAction() throws IOException
	{
		//given
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);

		final Response result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void getAllActionsForVariationTest()
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final ActionListWsDTO actions = response.readEntity(ActionListWsDTO.class);
		//controller.getActions(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION);

		assertNotNull(actions);
		assertNotNull(actions.getActions());
		assertEquals(5, actions.getActions().size());
	}

	@Test
	public void getNonexistingActionByIdTest()
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(NON_EXISTINGACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void getActionByIdFromInvalidVariationTest()
	{

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(NONEXISTING_VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());

	}

	@Test
	public void getActionByIdFromInvalidCustomizationTest()
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(NONEXISTING_CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void createExistingCmsAction() throws IOException
	{
		//given
		final String newActionComponentId = "newComponent";

		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("code", ACTION);
		actionAttributes.put("componentId", newActionComponentId);
		actionAttributes.put("componentCatalog", "testCatalog");
		actionAttributes.put("containerId", CONTAINER);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.build()//
				.post(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("AlreadyExistsError", error1.getType());
	}

	@Test
	public void updateCmsActionWithInconsidtentCode() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("type", "cxCmsActionData");
		actionAttributes.put("code", NON_EXISTINGACTION);


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("CodeConflictError", error1.getType());
	}


	@Test
	public void updateNonExistingCmsActionWithInconsidtentCode() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<>();
		actionAttributes.put("componentId", "thisisanewcomponent");


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(NON_EXISTINGACTION)//
				.build()//
				.put(Entity.entity(actionAttributes, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());

	}

	@Test
	public void deleteNonExistingCmsAction() throws IOException
	{
		//given
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(NON_EXISTINGACTION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void deleteCmsActionFromUnknownVariation() throws IOException
	{
		//given
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(NONEXISTING_VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
	}

	@Test
	public void deleteCmsActionFromUnknownCustomization() throws IOException
	{
		//given
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(NONEXISTING_CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
	}


}
