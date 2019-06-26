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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiorderexchange.data.SapSendToSapCpiResult;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiSendOmmOrderCancellationToScpiAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.log4j.Logger;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SapCpiSendOmsOrderCancellationToScpiAction extends SapCpiSendOmmOrderCancellationToScpiAction {

    private static final Logger LOG = Logger.getLogger(SapCpiSendOmsOrderCancellationToScpiAction.class);

    @Override
    public void run(TaskService paramTaskService, TaskModel taskModel) throws RetryLaterException {

        final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContext();

        List<SapCpiOrderCancellation> sapCpiOrderCancellations = getSapCpiOrderConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry);

        final List<SapSendToSapCpiResult> results = new ArrayList<>();

        Observable.merge(sendOrderCancellationsToScpi(sapCpiOrderCancellations)).subscribe(

                sapSendToSapCpiResult -> results.add(sapSendToSapCpiResult)

                , error -> handleErrorMessage(orderCancelRecordEntry, error.getMessage())

                , () -> {

                    if (results.stream().allMatch(result -> result.isSuccessful())) {

                        StringBuilder successMsg = new StringBuilder();
                        results.forEach(result -> successMsg.append(result.getMessage()).append("%n"));
                        LOG.info(String.format("Cancellation request for order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
                                orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), String.format(successMsg.toString())));

                    } else {

                        StringBuilder errorMsg = new StringBuilder();
                        results.stream().filter(result -> !result.isSuccessful()).forEach(result -> errorMsg.append(result.getMessage()).append("%n"));
                        handleErrorMessage(orderCancelRecordEntry, String.format(errorMsg.toString()));
                    }

                }
        );

    }

    @Override
    protected void handleErrorMessage(OrderCancelRecordEntryModel orderCancelRecordEntry, String message) {

        Registry.activateMasterTenant();

        final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
        order.setStatus(OrderStatus.CANCELLING);
        getModelService().save(order);

        LOG.error(String.format("The order [%s] cancellation has not been sent to the SAP backend through SCPI! %n%s!", order.getCode(), message));

    }


    protected List<Observable<SapSendToSapCpiResult>> sendOrderCancellationsToScpi(final List<SapCpiOrderCancellation> sapCpiOrderCancellations) {

        return sapCpiOrderCancellations.stream()
                .map(orderCancellation -> getSapCpiOrderService().sendOrderCancellation(orderCancellation).toObservable())
                .collect(Collectors.toList());
    }

}
