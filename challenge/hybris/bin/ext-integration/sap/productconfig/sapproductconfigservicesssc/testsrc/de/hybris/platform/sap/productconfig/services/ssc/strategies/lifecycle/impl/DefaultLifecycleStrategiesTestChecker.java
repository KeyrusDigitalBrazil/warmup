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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Resource;


public class DefaultLifecycleStrategiesTestChecker implements LifecycleStrategiesTestChecker
{


	@Resource(name = "sapProductConfigSessionAccessService")
	private SessionAccessService sessionAccess;

	@Resource(name = "sessionService")
	protected SessionService sessionService;


	@Override
	public void checkBasicData(final String userName, final String configId)
	{
		// no basic data saved for SSC
	}

	@Override
	public void checkLinkToProduct(final String productCode, final String configId)
	{
		if (null != productCode)
		{
			assertEquals(configId, sessionAccess.getConfigIdForProduct(productCode));
		}
		else
		{
			final ProductConfigSessionAttributeContainer container = sessionService
					.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			assertFalse("We expect that configuration is not linked to any product: " + configId,
					container.getProductConfigurations().values().contains(configId));
		}
	}

	@Override
	public void checkLinkToCart(final String configId, final String cartItemHandle, final boolean isDraft)
	{
		if (null != cartItemHandle)
		{
			if (isDraft)
			{
				assertEquals(cartItemHandle, sessionAccess.getCartEntryForDraftConfigId(configId));
				assertEquals(configId, sessionAccess.getDraftConfigIdForCartEntry(cartItemHandle));
			}
			else
			{
				assertEquals(cartItemHandle, sessionAccess.getCartEntryForConfigId(configId));
				assertEquals(configId, sessionAccess.getConfigIdForCartEntry(cartItemHandle));
			}
		}
		else
		{
			final ProductConfigSessionAttributeContainer container = sessionService
					.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			assertFalse(container.getCartEntryConfigurations().values().contains(configId));
			assertFalse(container.getCartEntryDraftConfigurations().values().contains(configId));
		}
	}

	@Override
	public void checkNumberOfConfigsPersisted(final int numExpected)
	{
		final ProductConfigSessionAttributeContainer container = sessionService
				.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
		final Collection<String> configIdsLinkedToProducts = container.getProductConfigurations().values();
		final Collection<String> configIdsLinkedToCart = container.getCartEntryConfigurations().values();
		final Collection<String> configIdsLinkedToCartAsDraft = container.getCartEntryDraftConfigurations().values();
		final HashSet<String> allConfigIds = new HashSet<>(configIdsLinkedToProducts);
		allConfigIds.addAll(configIdsLinkedToCart);
		allConfigIds.addAll(configIdsLinkedToCartAsDraft);
		final String msg = "Expected " + numExpected + ", but saw " + allConfigIds.size()
				+ " cached in session; product<->configId: " + container.getProductConfigurations().toString()
				+ "; cartEntry<->configId: " + container.getCartEntryConfigurations().toString() + ";  cartEntryDraft<->configId: "
				+ container.getCartEntryDraftConfigurations().toString();
		assertEquals(msg, numExpected, allConfigIds.size());
	}

	@Override
	public void checkConfigDeleted(final String configId, final String cartItemKey)
	{
		final ProductConfigSessionAttributeContainer container = sessionService
				.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
		if (null != configId && null != container)
		{
			assertFalse(container.getProductConfigurations().values().contains(configId));
			assertFalse(container.getCartEntryConfigurations().values().contains(configId));
		}
		if (null != cartItemKey && null != container)
		{
			assertFalse(container.getCartEntryConfigurations().keySet().contains(cartItemKey));
		}
	}
}
