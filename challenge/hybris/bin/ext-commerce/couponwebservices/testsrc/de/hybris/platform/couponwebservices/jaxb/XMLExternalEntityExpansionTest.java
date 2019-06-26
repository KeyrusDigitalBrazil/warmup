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
package de.hybris.platform.couponwebservices.jaxb;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;


@NeedsEmbeddedServer(webExtensions =
{ CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class XMLExternalEntityExpansionTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static File xxeFile;
	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@BeforeClass
	public static void beforeTests() throws IOException
	{
		xxeFile = File.createTempFile("xxeTests", "txt");
		xxeFile.deleteOnExit();
		FileUtils.write(xxeFile, "xxeAttackSuccessful");
	}

	@Before
	public void setUp() throws Exception
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS);

		createCoreData();
		importCsv("/couponwebservices/test/ws-user.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
	}

	@Test
	public void testXXEAttackProtection() throws IOException
	{
		final Response response = wsSecuredRequestBuilder.grantClientCredentials()//
				.path("/couponservices/v2/singlecodecoupon/create")//
				.build().accept(MediaType.APPLICATION_XML)
				.post(Entity.xml("<!DOCTYPE user[<!ENTITY xxe SYSTEM \"" + xxeFile.getAbsolutePath()
						+ "\" >]><singleCodeCouponWsDTO><couponId>coupon_id &xxe;</couponId></singleCodeCouponWsDTO>"));

		if (response.getStatus() == HttpStatus.SC_CREATED)
		{
			final String wsdto = response.readEntity(String.class);
			Assert.doesNotContain(wsdto, "xxeAttackSuccessful");
		}
		else
		{
			//WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
			assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
		}

	}
}
