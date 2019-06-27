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
package de.hybris.platform.adaptivesearchwebservices.test

import de.hybris.platform.adaptivesearchwebservices.constants.AdaptivesearchwebservicesConstants
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import org.junit.Before

import javax.annotation.Resource

import static javax.ws.rs.core.MediaType.APPLICATION_JSON
import static javax.ws.rs.core.MediaType.APPLICATION_XML

import de.hybris.platform.servicelayer.ServicelayerTest

import groovy.json.JsonSlurper


abstract class AbstractWebServiceTest extends ServicelayerTest {

	protected static final String OAUTH_CLIENT_ID = "mobile_android"
	protected static final String OAUTH_CLIENT_PASS = "secret"

	@Resource
	private ConfigurationService configurationService

	@Before
	void setUp() {
		createCoreData()
	}

	WsSecuredRequestBuilder getWsSecuredRequestBuilder(final String user, final String pwd)
	{
		return new WsSecuredRequestBuilder()//
				.extensionName(AdaptivesearchwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.resourceOwner(user, pwd)//
				.grantResourceOwnerPasswordCredentials()
	}


    def parseResponse(response, format) {
		def entity = response.readEntity(String.class);

		switch (format) {
			case APPLICATION_JSON:
				return new JsonSlurper().parseText(entity)
			case APPLICATION_XML:
				return new XmlSlurper().parseText(entity)
			default:
				return null
		}
	}

	/**
	 * Checks if a node exists and is not empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isNotEmpty(node) {
		(node != null) && (node.size() > 0)
	}

	/**
	 * Checks if a node doesn't exist or is empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isEmpty(node) {
		(node == null) || (node.size() == 0)
	}

	/**
	 * Returns property from tenant_junit.property
	 *
	 * @param property name
	 * @return property string value
	 */
	protected String getConfigurationProperty(final String propertyName)
	{
		return configurationService.getConfiguration().getString(propertyName)
	}
}
