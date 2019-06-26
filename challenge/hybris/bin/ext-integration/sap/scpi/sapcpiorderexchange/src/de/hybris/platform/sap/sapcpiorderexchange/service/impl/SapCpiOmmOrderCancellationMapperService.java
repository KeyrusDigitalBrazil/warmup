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

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderCancellationMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * SAP CPI OMM Order Cancellation Mapper Service
 */
public class SapCpiOmmOrderCancellationMapperService implements SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel> {

  private SapCpiOrderConversionService sapCpiOrderConversionService;

  @Override
  public void map(OrderCancelRecordEntryModel orderCancelRecordEntryModel, List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellationModels) {

    mapSapCpiCancelOrderToSapCpiCancelOrderOutbound(getSapCpiOrderConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel), sapCpiOutboundOrderCancellationModels);

  }

  protected void mapSapCpiCancelOrderToSapCpiCancelOrderOutbound(List<SapCpiOrderCancellation> sapCpiOrderCancellations,
                                                                 List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations) {
    sapCpiOrderCancellations.forEach(cancellation -> {

      SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation = new SAPCpiOutboundOrderCancellationModel();
      sapCpiOutboundOrderCancellation.setSapCpiConfig(mapOrderCancellationConfigInfo(cancellation.getSapCpiConfig()));
      sapCpiOutboundOrderCancellation.setOrderId(cancellation.getOrderId());
      sapCpiOutboundOrderCancellation.setRejectionReason(cancellation.getRejectionReason());

      List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();
      cancellation.getSapCpiOrderCancellationItems().forEach(item -> {
        SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
        sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
        sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
        sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);

      });

      sapCpiOutboundOrderCancellation.setSapCpiOutboundOrderItems(new HashSet<>(sapCpiOutboundOrderItems));
      sapCpiOutboundOrderCancellations.add(sapCpiOutboundOrderCancellation);

    });

  }

  protected SAPCpiOutboundConfigModel mapOrderCancellationConfigInfo(SapCpiConfig sapCpiConfig) {

    SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();
    SapCpiTargetSystem sapCpiTargetSystem = sapCpiConfig.getSapCpiTargetSystem();

    sapCpiOutboundConfig.setUrl(sapCpiTargetSystem.getUrl());
    sapCpiOutboundConfig.setUsername(sapCpiTargetSystem.getUsername());
    sapCpiOutboundConfig.setClient(sapCpiTargetSystem.getClient());

    sapCpiOutboundConfig.setSenderName(sapCpiTargetSystem.getSenderName());
    sapCpiOutboundConfig.setSenderPort(sapCpiTargetSystem.getSenderPort());

    sapCpiOutboundConfig.setReceiverName(sapCpiTargetSystem.getReceiverName());
    sapCpiOutboundConfig.setReceiverPort(sapCpiTargetSystem.getReceiverPort());

    return sapCpiOutboundConfig;

  }

  protected SapCpiOrderConversionService getSapCpiOrderConversionService() {
    return sapCpiOrderConversionService;
  }

  @Required
  public void setSapCpiOrderConversionService(SapCpiOrderConversionService sapCpiOrderConversionService) {
    this.sapCpiOrderConversionService = sapCpiOrderConversionService;
  }

}
