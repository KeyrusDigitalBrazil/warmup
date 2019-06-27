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
package de.hybris.platform.entitlementservices.daos;

import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
* Data Access Object for looking up items related to {@link EntitlementModel}.
*
* @spring.bean entitlementDao
*/
public interface EntitlementDao
{
    /**
     * Finds the {@link EntitlementModel} for the given code.
     *
     * @param code
     *           the id of the {@link EntitlementModel}.
     * @return {@link EntitlementModel} if the given <code>code</code> was found
     * @throws de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
     *            if nothing was found
     */
    EntitlementModel findEntitlementByCode(String code) throws ModelNotFoundException;
}
