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

package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderCancellationMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * SAP CPI OMM Order Outbound Conversion Service
 */
public class SapCpiOmmOrderOutboundConversionService implements SapCpiOrderOutboundConversionService {

  private List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderMappers;
  private List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> sapCpiOrderCancellationMappers;

  @Override
  public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel) {

    SAPCpiOutboundOrderModel sapCpiOutboundOrder = new SAPCpiOutboundOrderModel();
    getSapCpiOrderMappers().forEach(mapper -> mapper.map(orderModel, sapCpiOutboundOrder));
    return sapCpiOutboundOrder;

  }

  @Override
  public List<SAPCpiOutboundOrderCancellationModel> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
    List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations = new ArrayList<>();
    getSapCpiOrderCancellationMappers().forEach(mapper -> mapper.map(orderCancelRecordEntryModel, sapCpiOutboundOrderCancellations));
    return sapCpiOutboundOrderCancellations;
  }

  protected List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> getSapCpiOrderMappers() {
    return sapCpiOrderMappers;
  }

  @Required
  public void setSapCpiOrderMappers(List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderExchangeMappers) {
    this.sapCpiOrderMappers = sapCpiOrderExchangeMappers;
  }

  protected List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> getSapCpiOrderCancellationMappers() {
    return sapCpiOrderCancellationMappers;
  }

  @Required
  public void setSapCpiOrderCancellationMappers(List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> sapCpiOrderCancellationMappers) {
    this.sapCpiOrderCancellationMappers = sapCpiOrderCancellationMappers;
  }

}
