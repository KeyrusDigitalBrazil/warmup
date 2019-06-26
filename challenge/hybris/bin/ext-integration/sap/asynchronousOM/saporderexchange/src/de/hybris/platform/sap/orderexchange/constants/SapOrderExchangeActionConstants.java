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
package de.hybris.platform.sap.orderexchange.constants;

public class SapOrderExchangeActionConstants {

    private SapOrderExchangeActionConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";

    public static final String ERP_ORDER_SEND_COMPLETION_EVENT = "ERPOrderSendCompletionEvent_";

}
