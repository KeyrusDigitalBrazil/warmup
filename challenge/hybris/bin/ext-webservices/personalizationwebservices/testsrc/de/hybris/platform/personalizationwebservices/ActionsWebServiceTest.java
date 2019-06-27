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
package de.hybris.platform.personalizationwebservices;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationfacades.data.ActionData;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.mapping.SubclassRegistry;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class ActionsWebServiceTest extends BaseWebServiceTest
{
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";

	@Resource
	SubclassRegistry subclassRegistry;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		subclassRegistry.registerSubclass(ActionData.class, SubActionData.class);
	}


	@Test
	public void getActionWithoutAuthorization() throws IOException
	{
		//when
		final Response response = getWsRequestBuilder()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("UnauthorizedError", error1.getType());
	}

	@Test
	public void getActionWithoutProperRights() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCustomer()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(ACTION_ENDPOINT)//
				.path(ACTION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.FORBIDDEN, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("ForbiddenError", error1.getType());
	}


	@Test
	public void createActionForNotExistingType() throws IOException
	{
		//given
		final HashMap<String, String> actionAttributes = new HashMap<String, String>();
		actionAttributes.put("type", "notExistingActionData");
		actionAttributes.put("code", "newActionCode");
		actionAttributes.put("componentId", "newActionComponentId");
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
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("HttpMessageNotReadableError", error1.getType());
		// this test will fail if there is no subclass registered for ActionData - type attribute will be ignored and different kind of exception would be thrown
	}

	private static class SubActionData extends ActionData
	{
		private static final long serialVersionUID = -1091722211480953545L;
	}

}
