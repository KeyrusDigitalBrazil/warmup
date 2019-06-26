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

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;

import java.util.List;

/**
 * Provides mapping from {@link OrderCancelRecordEntryModel} to {@link List<SAPCpiOutboundOrderCancellationModel>}.
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public interface SapCpiOrderCancellationMapperService<SOURCE extends OrderCancelRecordEntryModel, TARGET extends SAPCpiOutboundOrderCancellationModel> {

  /**
   *  Performs mapping from source to target.
   *
   * @param source Order Cancel Record Entry Model
   * @param target SAP CPI Outbound Order Cancellation Model
   */
  void map(SOURCE source, List<TARGET> target);

}
