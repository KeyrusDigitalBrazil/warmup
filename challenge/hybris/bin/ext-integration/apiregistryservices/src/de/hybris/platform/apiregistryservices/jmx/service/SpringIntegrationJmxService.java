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
package de.hybris.platform.apiregistryservices.jmx.service;

/**
 * Service which loads QueueChannel classes from context, enables monitoring and register jmx beans for them.
 */
public interface SpringIntegrationJmxService
{
    /**
     * Register mbeans
     * @param jmxPath suffix for jxm path
     * @param beanInterface for mbean
     */
    void registerAllSpringQueues(final String jmxPath, final Class beanInterface);
}
