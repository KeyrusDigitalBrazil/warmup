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
package de.hybris.platform.sap.productconfig.b2b.integrationtests;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.strategies.CartValidationStrategy;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.b2bservices.strategies.impl.ProductConfigurationB2BCartValidationStrategyImpl;
import de.hybris.platform.sap.productconfig.facades.integrationtests.ConfigurationValidatorCheckoutIntegrationTest;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * This test just repeats the tests from its super class in a B2B context, as we register a different validator for B2B.
 * Registration of the validator is done implicitly by including sapproductconfigb2bservices extension as required for
 * this extension.
 */
@SuppressWarnings("javadoc")
@IntegrationTest
public class ConfigurationValidatorCheckoutB2BIntegrationTest extends ConfigurationValidatorCheckoutIntegrationTest
{
	@Resource(name = "cartValidationStrategy")
	CartValidationStrategy cartValidationStrategy;

	@Test
	public void testCheckoutValidationRunsInB2BContext()
	{
		assertTrue("We must run in a B2B context",
				cartValidationStrategy instanceof ProductConfigurationB2BCartValidationStrategyImpl);
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}


}
