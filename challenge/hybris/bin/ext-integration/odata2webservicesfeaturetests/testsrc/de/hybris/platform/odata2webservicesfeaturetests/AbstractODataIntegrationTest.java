/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2webservicesfeaturetests;

import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants;
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;

import org.junit.Before;
import org.junit.Ignore;

@Ignore
public abstract class AbstractODataIntegrationTest extends ServicelayerTest
{
	static final String BATCH_URI = "$batch";
	static final String METADATA_URI = "$metadata";
	static final String PRODUCT_QUERY_STRING = "AProduct";
	static final String PRODUCTS_QUERY = "AProducts";
	static final String GET_PRODUCT_QUERY = "AProducts('code')";

	static final String UNITS_QUERY = "AUnits";

	static final String WEBROOT = "/odata2webservices_junit/";
	static final String UTF_8 = "UTF-8";

	static final String TEST_USER = "integrationtestuser";
	static final String TEST_ADMIN = "integrationtestadmin";
	static final String TEST_CREATE_USER = "integrationtestcreate";
	static final String TEST_VIEW_USER = "integrationtestview";
	static final String PASSWORD = "password";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		// For the integrationadmingroup (from odata2services)
		importCsv("/impex/essentialdata-odata2services.impex", UTF_8);
		// we create the users
		importCsv("/test/user-odata2webservicesfeaturetests.impex", UTF_8);
	}

	protected WsRequestBuilder request()
	{
		return new WsRequestBuilder()
				.extensionName(Odata2webservicesConstants.EXTENSIONNAME);
	}

	protected BasicAuthRequestBuilder basicAuthRequest()
	{
		return new BasicAuthRequestBuilder()
				.extensionName(Odata2webservicesConstants.EXTENSIONNAME);
	}
}
