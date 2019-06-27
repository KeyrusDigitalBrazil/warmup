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
package de.hybris.platform.personalizationpromotionsweb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationfacades.data.ActionData;
import de.hybris.platform.personalizationpromotionsweb.data.CxPromotionActionData;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
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

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class CxPromotionsActionsWebServiceTest extends BaseWebServiceTest
{
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationpromotionsweb/test/webcontext_testdata.impex", "UTF-8"));
	}

	@Test
	public void createPromotionAction() throws IOException
	{
		//given
		final String newPromotionActionCode = "newPromotionActionCode";
		final String newPromotionActionPromotionId = "promotionRule1";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionActionData");
		actionAttributes.put("code", newPromotionActionCode);
		actionAttributes.put("promotionId", newPromotionActionPromotionId);

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
		WebservicesAssert.assertResponse(Response.Status.CREATED, response);
		final String location = response.getHeaderString("Location");
		assertTrue(location.contains(newPromotionActionCode));
		CxPromotionActionData action = response.readEntity(CxPromotionActionData.class);
		assertEquals(newPromotionActionCode, action.getCode());
		assertEquals(newPromotionActionPromotionId, action.getPromotionId());


		action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(newPromotionActionCode)//
				.build()//
				.get(CxPromotionActionData.class);
		assertEquals(newPromotionActionCode, action.getCode());
		assertEquals(newPromotionActionPromotionId, action.getPromotionId());
	}

	@Test
	public void getPromotionActionForAdmin() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.OK, response);

		final ActionData action = response.readEntity(ActionData.class);
		assertNotNull(action);
		assertEquals(ACTION, action.getCode());
	}

	public void getPromotionActionForCmsManager() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.OK, response);

		final ActionData action = response.readEntity(ActionData.class);
		assertNotNull(action);
		assertEquals(ACTION, action.getCode());
	}

	@Test
	public void createAutomaticPromotionAction() throws IOException
	{
		//given
		final String newPromotionActionPromotionId = "promotionRule1";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionActionData");
		actionAttributes.put("promotionId", newPromotionActionPromotionId);

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
		WebservicesAssert.assertResponse(Response.Status.CREATED, response);
		final CxPromotionActionData action = response.readEntity(CxPromotionActionData.class);
		assertEquals(newPromotionActionPromotionId, action.getPromotionId());


		final CxPromotionActionData getAction = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(action.getCode())//
				.build()//
				.get(CxPromotionActionData.class);
		assertEquals(action.getCode(), getAction.getCode());
		assertEquals(newPromotionActionPromotionId, getAction.getPromotionId());
	}

	@Test
	public void createExistingPromotionAction() throws IOException
	{
		//given
		final String newActionPromotionId = "newActionPromotionId";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionActionData");
		actionAttributes.put("code", ACTION);
		actionAttributes.put("promotionId", newActionPromotionId);

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
		WebservicesAssert.assertResponse(Response.Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("AlreadyExistsError", error1.getType());
	}

	@Test
	public void updatePromotionActionTypeWithOtherType() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionTestActionData");


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
		WebservicesAssert.assertResponse(Response.Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("TypeConflictError", error1.getType());
	}

	@Test
	public void updatePromotionActionTypeWithUnknownType() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
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
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("HttpMessageNotReadableError", error1.getType());
	}

	@Test
	public void updatePromotionAction() throws IOException
	{
		//given
		final String newActionPromotionId = "promotionRule2";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionActionData");
		actionAttributes.put("promotionId", newActionPromotionId);


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
		WebservicesAssert.assertResponse(Response.Status.OK, response);
		final CxPromotionActionData action = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get(CxPromotionActionData.class);

		assertEquals(ACTION, action.getCode());
		assertEquals(newActionPromotionId, action.getPromotionId());
	}

	@Test
	public void updatePromotionActionWithInconsistentCode() throws IOException
	{
		//give
		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "cxPromotionActionData");
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
		WebservicesAssert.assertResponse(Response.Status.CONFLICT, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("CodeConflictError", error1.getType());
	}

	@Test
	public void updateNonExistingPromotionActionWithInconsistentCode() throws IOException
	{
		//give
		final String newActionPromotionId = "promotionRule2";

		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("promotionId", newActionPromotionId);


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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
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
		WebservicesAssert.assertResponse(Response.Status.OK, response);
		final ActionListWsDTO actions = response.readEntity(ActionListWsDTO.class);

		assertNotNull(actions);
		assertNotNull(actions.getActions());
		assertEquals(1, actions.getActions().size());
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}


	@Test
	public void deletePromotionAction() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.NO_CONTENT, response);

		final Response result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void deleteNonExistingPromotionAction() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("NotFoundError", error1.getType());
	}

	@Test
	public void deletePromotionActionFromUnknownVariation() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
	}

	@Test
	public void deletePromotionActionFromUnknownCustomization() throws IOException
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
		WebservicesAssert.assertResponse(Response.Status.NOT_FOUND, response);
	}
}
