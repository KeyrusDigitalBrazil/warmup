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
package de.hybris.platform.commerceservices.storesession;

/**
 * Defines an API for current language and currency 
 */
public interface StoreSessionService
{
	/**
	 * Sets the current language and validates, if language chosen is supported for current store.
	 *
	 * @param isocode
	 *           language iso
	 */
	void setCurrentLanguage(String isocode);

	/**
	 * Sets the current currency and validates, if currency chosen is supported for current currency.
	 *
	 * @param isocode
	 *           currency iso
	 */
	void setCurrentCurrency(String isocode);

}
