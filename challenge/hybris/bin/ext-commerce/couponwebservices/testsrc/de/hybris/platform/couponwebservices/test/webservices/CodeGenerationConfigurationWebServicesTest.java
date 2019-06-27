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
package de.hybris.platform.couponwebservices.test.webservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@NeedsEmbeddedServer(webExtensions =
{ CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CodeGenerationConfigurationWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_URI = "couponservices/v2";
	private static final String URI = BASE_URI + "/codegenerationconfiguration";

	private WsRequestBuilder wsRequestBuilder;
	private WsSecuredRequestBuilder wsSecuredRequestBuilder;



	@Before
	public void setUp() throws Exception
	{
		wsRequestBuilder = new WsRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME);

		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();

		createCoreData();
		createDefaultUsers();
		importCsv("/couponwebservices/test/ws-user.impex", "utf-8");
	}

	@Test
	public void testGetCodeGenerationConfigurationsWithoutAuthorization() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testGetCodeGenerationConfigurationsUsingClientCredentials() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testGetCodeGenerationConfigurationWithoutAuthorization() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("default-configuration")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testGetCodeGenerationConfigurationUsingClientCredentials() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("default-configuration")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		//then
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "No Code Generation Configuration found for name [INVALID_CODE_GENERATION_CONFIGURATION]",</br>
	 * "reason" : "invalid",</br>
	 * "subject" : "codeGenerationConfiguration",</br>
	 * "type" : "CodeGenerationConfigurationNotFoundError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testInvalidCodeGenerationConfigurationNameError() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("INVALID_CODE_GENERATION_CONFIGURATION")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//then
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("codeGenerationConfiguration", error.getSubject());
	}

	@Test
	public void testGetCodeGenerationConfigurationUsingWrongMediaType() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("default-configuration")//
				.build()//
				.accept(MediaType.APPLICATION_XHTML_XML)//
				.get();
		//then
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_ACCEPTABLE, Optional.empty(), result);
	}

	@Test
	public void testGetCodeGenerationConfigurationsUsingWrongMediaType() throws ImpExException
	{
		//given
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		//when
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_XHTML_XML)//
				.get();
		//then
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_ACCEPTABLE, Optional.empty(), result);
	}

	@Test
	public void testGetCodeGenerationConfigurationsWhenNoConfigurationExists() throws ImpExException
	{
		//when
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//then
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("No Records", error.getReason());
	}
}
