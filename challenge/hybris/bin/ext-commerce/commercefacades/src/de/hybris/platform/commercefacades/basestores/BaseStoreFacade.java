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
package de.hybris.platform.commercefacades.basestores;

import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;


/**
 * Facade for management of base stores - Its main purpose is to retrieve base store information using existing services.
 */
public interface BaseStoreFacade
{
	/**
	 * Returns base store DTO for a given base store uid
	 *
	 * @param uid
	 * 		the base store unique identifier
	 * @return the corresponding base store
	 */
	BaseStoreData getBaseStoreByUid(String uid);
}
