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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 *
 */
public class PersistentLifecycleStrategiesTestChecker implements LifecycleStrategiesTestChecker
{

	@Resource(name = "sapProductConfigProductConfigurationPersistenceService")
	protected ProductConfigurationPersistenceService cpqPersistenceService;

	@Resource(name = "sessionService")
	protected SessionService sessionService;

	@Resource(name = "flexibleSearchService")
	protected FlexibleSearchService flexibleSearchService;

	@Override
	public void checkConfigDeleted(final String configId, final String cartItemKey)
	{
		if (null != cartItemKey)
		{
			try
			{
				final AbstractOrderEntryModel entryModel = cpqPersistenceService.getOrderEntryByPK(cartItemKey);
				assertNull("Configuration should be null in the cart entry", entryModel.getProductConfiguration());
			}
			catch (final ModelNotFoundException ex)
			{
				//expected
			}
		}
		if (null != configId)
		{
			try
			{
				final ProductConfigurationModel config = cpqPersistenceService.getByConfigId(configId);
				assertNull("config was not deleted from persistence", config);
			}
			catch (final ModelNotFoundException ex)
			{
				//expected
			}
		}
	}


	@Override
	public void checkLinkToCart(final String configId, final String cartItemHandle, final boolean isDraft)
	{
		if (null != cartItemHandle)
		{
			final AbstractOrderEntryModel entryModel = cpqPersistenceService.getOrderEntryByPK(cartItemHandle);
			if (isDraft)
			{
				assertEquals("ConfigurationId was not linked to cart entry as DRAFT", configId,
						entryModel.getProductConfigurationDraft().getConfigurationId());
			}
			else
			{
				assertEquals("ConfigurationId was not linked to cart entry", configId,
						entryModel.getProductConfiguration().getConfigurationId());
			}
		}
	}

	@Override
	public void checkBasicData(final String userName, final String configId)
	{
		final ProductConfigurationModel persistenceModel = cpqPersistenceService.getByConfigId(configId);
		assertNotNull("No persistence Model found for the given config id", persistenceModel);
		assertFalse("Version  is missing in persistence", StringUtils.isEmpty(persistenceModel.getVersion()));
		assertNotNull("CreateionTime is missing in persistence", persistenceModel.getCreationtime());
		assertFalse("SessionId is missing in persistence", StringUtils.isEmpty(persistenceModel.getUserSessionId()));
		assertNotNull("User is missing in persistence", persistenceModel.getUser());
		assertEquals("User name is incorrect", userName, persistenceModel.getUser().getName());
	}

	@Override
	public void checkLinkToProduct(final String productCode, final String configId)
	{
		final ProductConfigurationModel persistenceModel = cpqPersistenceService.getByConfigId(configId);
		String firstCode = null;
		if (!CollectionUtils.isEmpty(persistenceModel.getProduct()))
		{
			firstCode = ((ProductModel) persistenceModel.getProduct().toArray()[0]).getCode();
		}
		if (null == productCode)
		{
			assertTrue("no product expected, but there were " + persistenceModel.getProduct().size() + ", firstCode:" + firstCode,
					CollectionUtils.isEmpty(persistenceModel.getProduct()));
		}
		else
		{
			assertEquals("exactly one product expected", 1, persistenceModel.getProduct().size());
			assertEquals("Product code is incorrect", productCode, firstCode);
		}
	}

	@Override
	public void checkNumberOfConfigsPersisted(final int numExpected)
	{

		final SearchResult<Object> result = flexibleSearchService.search("select {PK} from {productconfiguration}");
		final int numActual = result.getTotalCount();
		assertEquals("Expected " + numExpected + " configs persistet in the current user session, but saw " + numActual,
				numExpected, numActual);

	}

	@Override
	public void checkNumberOfConfigsPersisted(final String message, final int numExpected)
	{

		final SearchResult<Object> result = flexibleSearchService.search("select {PK} from {productconfiguration}");
		final int numActual = result.getTotalCount();
		assertEquals(message, numExpected, numActual);

	}

}
