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

package de.hybris.platform.sap.sapcpiadapter.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

public class SapCpiOutboundServiceImpl implements SapCpiOutboundService {

  // Customer Outbound
  private static final String OUTBOUND_CUSTOMER_OBJECT = "OutboundB2CCustomer";
  private static final String OUTBOUND_CUSTOMER_DESTINATION = "scpiCustomerDestination";

  // B2B Customer Outbound
  private static final String OUTBOUND_B2B_CUSTOMER_OBJECT = "OutboundB2BCustomer";
  private static final String OUTBOUND_B2B_CUSTOMER_DESTINATION = "scpiB2BCustomerDestination";

  // Order Outbound
  private static final String OUTBOUND_ORDER_OBJECT = "OutboundOMMOrderOMSOrder";
  private static final String OUTBOUND_ORDER_DESTINATION = "scpiOrderDestination";

  // Order Cancellation Outbound
  private static final String OUTBOUND_ORDER_CANCELLATION_OBJECT = "OutboundCancelOMMOrderOMSOrder";
  private static final String OUTBOUND_ORDER_CANCELLATION_DESTINATION = "scpiOrderCancellationDestination";

  private OutboundServiceFacade outboundServiceFacade;

  @Override
  public Observable<ResponseEntity<Map>> sendCustomer(SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel) {
    return getOutboundServiceFacade().send(sapCpiOutboundCustomerModel, OUTBOUND_CUSTOMER_OBJECT, OUTBOUND_CUSTOMER_DESTINATION);
  }

  @Override
  public Observable<ResponseEntity<Map>> sendB2BCustomer(SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomerModel) {
    return getOutboundServiceFacade().send(sapCpiOutboundB2BCustomerModel, OUTBOUND_B2B_CUSTOMER_OBJECT, OUTBOUND_B2B_CUSTOMER_DESTINATION);
  }

  @Override
  public Observable<ResponseEntity<Map>> sendOrder(SAPCpiOutboundOrderModel sapCpiOutboundOrderModel) {
    return getOutboundServiceFacade().send(sapCpiOutboundOrderModel, OUTBOUND_ORDER_OBJECT, OUTBOUND_ORDER_DESTINATION);
  }

  @Override
  public Observable<ResponseEntity<Map>> sendOrderCancellation(SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel) {
    return getOutboundServiceFacade().send(sapCpiOutboundOrderCancellationModel, OUTBOUND_ORDER_CANCELLATION_OBJECT, OUTBOUND_ORDER_CANCELLATION_DESTINATION);
  }

  protected OutboundServiceFacade getOutboundServiceFacade() {
    return outboundServiceFacade;
  }

  @Required
  public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
    this.outboundServiceFacade = outboundServiceFacade;
  }

}
