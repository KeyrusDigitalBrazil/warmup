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
package de.hybris.platform.sap.sapcpireturnsexchange.service;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpireturnsexchange.constants.SapcpireturnsexchangeConstants;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;

/**
 *
 */
public interface SapCpiOutboundReturnService {

    Observable<ResponseEntity<Map>> sendReturnOrder(SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrderModel);

    Observable<ResponseEntity<Map>> sendReturnOrderCancellation(
            SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel);

    static boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap) {

        return SapcpireturnsexchangeConstants.SUCCESS
                .equalsIgnoreCase(getPropertyValue(responseEntityMap, SapcpireturnsexchangeConstants.RESPONSE_STATUS))
                && responseEntityMap.getStatusCode().is2xxSuccessful();

    }

    static String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property) {

        final Object next = responseEntityMap.getBody().keySet().iterator().next();
        checkArgument(next != null,
                String.format("SCPI response entity key set cannot be null for property [%s]!", property));

        final String responseKey = next.toString();
        checkArgument(responseKey != null && !responseKey.isEmpty(),
                String.format("SCPI response property can neither be null nor empty for property [%s]!", property));

        final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
        checkArgument(propertyValue != null,
                String.format("SCPI response property [%s] value cannot be null!", property));

        return propertyValue.toString();

    }

}
