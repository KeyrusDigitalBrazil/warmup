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
package de.hybris.platform.c4ccustomer.deltadetection;

import de.hybris.deltadetection.ChangesCollector;
import de.hybris.platform.c4ccustomer.deltadetection.collector.C4CBatchingCollector;


import javax.annotation.Nonnull;


/**
 * Specification of {@link ChangesCollector}, used during synchronization of customers and addresses.
 */
public interface C4CAggregatingCollector extends ChangesCollector
{
	/**
	 * @param customerCollector Collector that will take care of customers.
	 */
	void setCustomerCollector(@Nonnull C4CBatchingCollector customerCollector);

	/**
	 * @param addressCollector Collector that will take care of addresses.
	 */
	void setAddressCollector(@Nonnull C4CBatchingCollector addressCollector);

	/**
	 * @param id stream configuration id for customers.
	 */
	void setCustomerConfigurationId(@Nonnull String id);

	/**
	 * @param id stream configuration id for addresses.
	 */
	void setAddressConfigurationId(@Nonnull String id);
}
