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
package de.hybris.platform.selectivecartfacades.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartfacades.strategies.SelectiveCartUpdateStrategy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SelectiveCartUpdateStrategy}
 */
public class DefaultSelectiveCartUpdateStrategy implements SelectiveCartUpdateStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSelectiveCartUpdateStrategy.class);
	private SelectiveCartFacade selectiveCartFacade;

	@Override
	public void update()
	{
		
		try
		{
			getSelectiveCartFacade().updateCartFromWishlist();

		}
		catch (final CommerceCartModificationException e) // NOSONAR
		{
			LOG.warn("Failed to update cart.");
		}
	}

	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

	@Required
	public void setSelectiveCartFacade(final SelectiveCartFacade selectiveCartFacade)
	{
		this.selectiveCartFacade = selectiveCartFacade;
	}
}
