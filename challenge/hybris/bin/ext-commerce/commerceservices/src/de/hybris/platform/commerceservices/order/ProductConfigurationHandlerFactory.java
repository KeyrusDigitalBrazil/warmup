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


import de.hybris.platform.catalog.enums.ConfiguratorType;

/**
 * Keeping configuration handlers.
 */
public interface ProductConfigurationHandlerFactory
{
    /**
     * Find {@code ProductConfigurationHandler} responsible for given configuration type.
     * @param configuratorType configuration type
     * @return handler of {@code null} if there is no handler registered for this configuration type.
     */
    ProductConfigurationHandler handlerOf(ConfiguratorType configuratorType);
}
