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
package com.sap.hybris.ysaprcomsfulfillment.actions.order;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * Class for checking if the Hybris order has been exported successfully to the SAP/ERP back-ends .
 */
public class SapCheckOrderSendStatusAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        ExportStatus sendStatus = order.getExportStatus();

        if (sendStatus.equals(ExportStatus.EXPORTED)) {
            return Transition.OK;
        } else {
            return Transition.NOK;
        }

    }

}


