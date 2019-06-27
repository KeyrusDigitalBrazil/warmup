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
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CouponRedemptionWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_URI = "couponservices/v2";
	private static final String URI = BASE_URI + "/singlecodecouponredemption";

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
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-redemption-test-data.impex", "utf-8");
	}

	@Test
	public void testGetSingleCodeCouponRedemptionWithoutAuthorization()
	{
		final Response result = wsRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("TEST_COUPON1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testGetSingleCodeCouponRedemptionUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("TEST_COUPON1").queryParam("customerId", "ppetersonson")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "No single code coupon found for invalid couponId [INVALID_TEST_COUPON1]",</br>
	 * "type" : "CouponNotFoundError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testGetSingleCodeCouponRedemptionInvalidCouponCodeError()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("INVALID_TEST_COUPON1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), response);
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "Cannot find user with uid 'invalid_user",</br>
	 * "type" : "UnknownIdentifierError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testGetSingleCodeCouponRedemptionInvalidCustomerIdError()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("TEST_COUPON1").queryParam("customerId", "invalid_user")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "No single code coupon was found for code [COUPON1]",</br>
	 * "type" : "CouponNotFoundError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testGetCouponRedemptionWhenMultiCodeCouponProvidedError()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("COUPON1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), response);
	}

}
