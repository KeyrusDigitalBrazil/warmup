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
package de.hybris.platform.sap.sapcarintegration.services.impl;


import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;



/**
 * Test for configuration provider.
 */
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultConnectionServiceTest extends SapcoreSpringJUnitTest
{
	
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	
	
	
	@Resource
	DefaultCarConnectionService carConnectionService; //NOPMD
	
	@Resource
	CarConfigurationService carConfigurationService; //NOPMD
	

	

	
	@Test
	public void testConnection() throws URISyntaxException, MalformedURLException, IOException {

				
		HttpURLConnection connection = carConnectionService.createConnection(carConfigurationService.getRootUrl() + carConfigurationService.getServiceName(), APPLICATION_JSON, HTTP_METHOD_GET);
		assertNotNull(connection);
		
	}

}

