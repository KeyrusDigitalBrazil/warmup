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

package de.hybris.platform.sap.sapcpiorderexchange.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.orderexchange.actions.SapSendOrderToDataHubAction;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class SapCpiOmmOrderOutboundAction extends SapSendOrderToDataHubAction {

  private static final Logger LOG = Logger.getLogger(SapCpiOmmOrderOutboundAction.class);

  private SapCpiOutboundService sapCpiOutboundService;
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;


  @Override
  public void executeAction(OrderProcessModel process) throws RetryLaterException {

    final OrderModel order = process.getOrder();

    getSapCpiOutboundService().sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(order)).subscribe(

            // onNext
            responseEntityMap -> {

              Registry.activateMasterTenant();

              if (isSentSuccessfully(responseEntityMap)) {

                setOrderStatus(order, ExportStatus.EXPORTED);
                resetEndMessage(process);
                LOG.info(String.format("The OMM order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
                        order.getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              } else {

                setOrderStatus(order, ExportStatus.NOTEXPORTED);
                LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend! %n%s",
                        order.getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              }

              final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
              getBusinessProcessService().triggerEvent(eventName);

            }

            // onError
            , error -> {

              Registry.activateMasterTenant();

              setOrderStatus(order, ExportStatus.NOTEXPORTED);
              LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend through SCPI! %n%s", order.getCode(), error.getMessage()));

              final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
              getBusinessProcessService().triggerEvent(eventName);

            }

    );

  }

  protected SapCpiOutboundService getSapCpiOutboundService() {
    return sapCpiOutboundService;
  }

  @Required
  public void setSapCpiOutboundService(SapCpiOutboundService sapCpiOutboundService) {
    this.sapCpiOutboundService = sapCpiOutboundService;
  }

  protected SapCpiOrderOutboundConversionService getSapCpiOrderOutboundConversionService() {
    return sapCpiOrderOutboundConversionService;
  }

  @Required
  public void setSapCpiOrderOutboundConversionService(SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService) {
    this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
  }

}
