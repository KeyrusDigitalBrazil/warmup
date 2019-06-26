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
package de.hybris.platform.entitlementservices.entitlement;

import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * Entitlement service that exposes methods to deal with entitlement operations.
 *
 * @spring.bean entitlementService
 */
public interface EntitlementService
{

    /**
     * This method returns the entitlement by entitlement id <code>entitlementId</code>
     *
     * @param entitlementId
     *           Entitlement Id
     * @return EntitlementModel {@link EntitlementModel}
     * @throws de.hybris.platform.servicelayer.exceptions.ModelNotFoundException if nothing was found
     */
    EntitlementModel getEntitlementForCode(final String entitlementId) throws ModelNotFoundException;
}
