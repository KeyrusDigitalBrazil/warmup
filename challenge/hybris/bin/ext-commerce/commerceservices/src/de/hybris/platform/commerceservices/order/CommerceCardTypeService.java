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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.payment.dto.CardType;

import java.util.Collection;


/**
 * Service that returns defined card types
 */
public interface CommerceCardTypeService
{
	/**
	 * Get all credit card types
	 * 
	 * @return the {@link Collection} of card types
	 */
	Collection<CardType> getCardTypes();

	/**
	 * Gets a card type by code
	 * 
	 * @param code
	 *           the card type code
	 * @return the card type
	 */
	CardType getCardTypeForCode(String code);
}
