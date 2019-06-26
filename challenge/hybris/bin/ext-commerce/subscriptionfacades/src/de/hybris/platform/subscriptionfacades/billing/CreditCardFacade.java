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
package de.hybris.platform.subscriptionfacades.billing;

import de.hybris.platform.commercefacades.order.data.CardTypeData;

import java.util.Collection;


/**
 * Facade for converting the hybris credit card codes to those of a billing provider.
 */
public interface CreditCardFacade
{
	/**
	 * mappingStrategy calls the appropriate service for converting the credit card codes.
	 * 
	 * @param creditCards
	 * @return false, if no mapping strategy is implemented or no conversion has been processed
	 */
	boolean mappingStrategy(Collection<CardTypeData> creditCards);
}
