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
package de.hybris.platform.acceleratorservices.order.strategies;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Strategy must decide if any action must be taken on the given consignment and perform the action.
 */
public interface UncollectedConsignmentsStrategy
{
    /**
     * Process consignment under some conditions.
     *
     * @param consignmentModel to process
     * @return true if any action has been performed
     */
    boolean processConsignment(final ConsignmentModel consignmentModel);

    /**
     * @return order time threshold in hours
     */
    Integer getTimeThreshold();
}
