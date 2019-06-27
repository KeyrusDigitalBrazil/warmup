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
package de.hybris.platform.subscriptionfacades.billing.impl;

import java.util.Collection;

import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.subscriptionfacades.billing.CreditCardFacade;

/**
 * Facade for converting credit card codes.
 */
public class DefaultCreditCardFacade implements CreditCardFacade
{
	@Override
	public boolean mappingStrategy(final Collection<CardTypeData> creditCards)
	{
		return false;
	}

}
