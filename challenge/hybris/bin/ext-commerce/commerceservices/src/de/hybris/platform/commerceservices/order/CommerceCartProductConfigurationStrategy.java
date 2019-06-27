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

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


/**
 * Commerce cart strategy to update order entry's configurations.
 */
public interface CommerceCartProductConfigurationStrategy
{
    /**
     * Update configuration on a configurable product in given order entry.
     *
     * @param parameters configuration data
     * @throws CommerceCartModificationException in case of invalid parameters
     */
    void configureCartEntry(CommerceCartParameter parameters) throws CommerceCartModificationException;
}
