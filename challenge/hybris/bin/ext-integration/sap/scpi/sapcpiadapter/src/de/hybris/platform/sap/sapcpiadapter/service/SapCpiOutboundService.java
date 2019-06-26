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

package de.hybris.platform.sap.sapcpiadapter.service;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * SapCpiOutboundService
 */
public interface SapCpiOutboundService {

  String SUCCESS = "success";
  String RESPONSE_STATUS = "responseStatus";
  String RESPONSE_MESSAGE = "responseMessage";

  /**
   * Send order
   *
   * @param sapCpiOutboundOrderModel SAPCpiOutboundOrderModel
   * @return Observable<ResponseEntity <Map>>
   */
  Observable<ResponseEntity<Map>> sendOrder(SAPCpiOutboundOrderModel sapCpiOutboundOrderModel);

  /**
   * Send order cancellation
   *
   * @param sapCpiOutboundOrderCancellationModel SAPCpiOutboundOrderCancellationModel
   * @return Observable<ResponseEntity <Map>>
   */
  Observable<ResponseEntity<Map>> sendOrderCancellation(SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel);

  /**
   * Send customer
   *
   * @param sapCpiOutboundCustomerModel SAPCpiOutboundCustomerModel
   * @return Observable<ResponseEntity <Map>>
   */
  Observable<ResponseEntity<Map>> sendCustomer(SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel);

  /**
   * Send B2B Customer
   *
   * @param sapCpiOutboundB2BCustomerModel SAPCpiOutboundB2BCustomerModel
   * @return Observable<ResponseEntity <Map>>
   */
  Observable<ResponseEntity<Map>> sendB2BCustomer(SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomerModel);

  /**
   * isSentSuccessfully
   *
   * @param responseEntityMap ResponseEntity<Map>
   * @return boolean
   */
  static boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap) {
    return SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS)) && responseEntityMap.getStatusCode().is2xxSuccessful();
  }

  /**
   * getPropertyValue
   *
   * @param responseEntityMap ResponseEntity<Map>
   * @param property          String
   * @return String
   */
  static String getPropertyValue(ResponseEntity<Map> responseEntityMap, String property) {

    Object next = responseEntityMap.getBody().keySet().iterator().next();
    checkArgument(next != null, String.format("SCPI response entity key set cannot be null for property [%s]!", property));

    String responseKey = next.toString();
    checkArgument(responseKey != null && !responseKey.isEmpty(), String.format("SCPI response property can neither be null nor empty for property [%s]!", property));

    Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
    checkArgument(propertyValue != null, String.format("SCPI response property [%s] value cannot be null!", property));

    return propertyValue.toString();

  }

}
