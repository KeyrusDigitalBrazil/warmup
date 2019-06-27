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
package de.hybris.platform.sap.sapcpiorderexchangeoms.actions;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiOmmOrderOutboundCancellationAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class SapCpiOmsOrderOutboundCancellationAction extends SapCpiOmmOrderOutboundCancellationAction {

  private static final Logger LOG = Logger.getLogger(SapCpiOmsOrderOutboundCancellationAction.class);

  @Override
  public void run(TaskService paramTaskService, TaskModel taskModel) throws RetryLaterException {

    final List<ResponseEntity<Map>> results = new ArrayList<>();
    final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContext();
    final List<SAPCpiOutboundOrderCancellationModel> sapCpiOrderCancellations = getSapCpiOrderOutboundConversionService()
            .convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry);

    if(sapCpiOrderCancellations.isEmpty()){

      LOG.warn(String.format("There are no SAP orders attached to the order [%s] so that cancellation requests can be sent to SCPI!",
              orderCancelRecordEntry.getModificationRecord().getOrder().getCode()));

      return;
    }

    Observable.merge(sendOrderCancellationsToScpi(sapCpiOrderCancellations)).subscribe(

            new Subscriber<ResponseEntity<Map>>() {

              @Override
              public void onNext(ResponseEntity<Map> responseEntity) {
                results.add(responseEntity);

              }

              @Override
              public void onCompleted() {

                if (results.stream().allMatch(result -> isSentSuccessfully(result))) {

                  StringBuilder successMsg = new StringBuilder();
                  results.forEach(result -> successMsg.append(getPropertyValue(result, RESPONSE_MESSAGE)).append("%n"));
                  LOG.info(String.format("The OMS order [%s] cancellation request has been successfully sent to the SAP backend through SCPI! %n%s",
                          orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), String.format(successMsg.toString())));

                } else {

                  StringBuilder errorMsg = new StringBuilder();
                  results.stream().filter(result -> !isSentSuccessfully(result)).forEach(result -> errorMsg.append(getPropertyValue(result, RESPONSE_MESSAGE)).append("%n"));
                  LOG.error(String.format("The OMS order [%s] cancellation request has not been sent to the SAP backend! %n%s",
                          orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), String.format(errorMsg.toString())));
                  handleOrderCancellationFailure(orderCancelRecordEntry);

                }

              }

              @Override
              public void onError(Throwable throwable) {

                LOG.error(String.format("The OMS order [%s] cancellation request has not been sent to the SAP backend through SCPI! %n%s",
                        orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), throwable.getMessage()));
                handleOrderCancellationFailure(orderCancelRecordEntry);

              }

            }

    );

  }

  protected List<Observable<ResponseEntity<Map>>> sendOrderCancellationsToScpi(List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations) {

    return sapCpiOutboundOrderCancellations.stream()
            .map(sapCpiOutboundOrderCancellation -> getSapCpiOutboundService().sendOrderCancellation(sapCpiOutboundOrderCancellation))
            .collect(Collectors.toList());

  }

}
