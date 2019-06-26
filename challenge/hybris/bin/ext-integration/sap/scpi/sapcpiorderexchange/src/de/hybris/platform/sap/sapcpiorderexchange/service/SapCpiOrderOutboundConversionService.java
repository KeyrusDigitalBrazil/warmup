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

package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.List;

/**
 * SapCpiOrderOutboundConversionService
 */
public interface SapCpiOrderOutboundConversionService {

  /**
   * convertOrderToSapCpiOrder
   * @param orderModel OrderModel
   * @return SAPCpiOutboundOrderModel
   */
  SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel);

  /**
   * convertCancelOrderToSapCpiCancelOrder
   * @param orderCancelRecordEntryModel OrderCancelRecordEntryModel
   * @return List<SAPCpiOutboundOrderCancellationModel>
   */
  List<SAPCpiOutboundOrderCancellationModel> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel);

}
