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
package de.hybris.platform.patchesdemo.release;

import de.hybris.platform.patches.Patch;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patchesdemo.structure.CountryOrganisation;
import de.hybris.platform.patchesdemo.structure.ShopOrganisation;

import java.util.Set;


/**
 * Patches demo specific interface. Adds global, shop and country specific methods. Check {@link SimpleDemoPatch} for
 * default implementation.
 */
public interface DemoPatch extends Patch
{
	/**
	 * Creates global data for given languages.
	 *
	 * @param languages
	 *           to be imported
	 * @param updateLanguagesOnly
	 *           if import data only for new languages
	 */
	void createGlobalData(final Set<ImportLanguage> languages, boolean updateLanguagesOnly);

	/**
	 * Creates shop specific data.
	 * 
	 * @param unit
	 *           shop for which data should be imported
	 * @param languages
	 *           to be imported
	 * @param updateLanguagesOnly
	 *           import data only for new languages
	 */
	void createShopData(final ShopOrganisation unit, final Set<ImportLanguage> languages, final boolean updateLanguagesOnly);

	/**
	 * Creates country specific data.
	 *
	 * @param country
	 *           for which data should be imported
	 */
	void createCountryData(final CountryOrganisation country);
}
