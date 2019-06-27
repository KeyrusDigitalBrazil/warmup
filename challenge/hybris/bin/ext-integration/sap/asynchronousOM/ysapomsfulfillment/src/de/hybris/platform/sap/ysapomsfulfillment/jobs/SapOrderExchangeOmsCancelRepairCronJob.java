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
package de.hybris.platform.sap.ysapomsfulfillment.jobs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.orderexchange.cancellation.SapOrderCancelService;
import de.hybris.platform.sap.orderexchange.outbound.OrderExchangeRepair;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.util.Config;

public class SapOrderExchangeOmsCancelRepairCronJob extends AbstractJobPerformable<CronJobModel> {
	private static final Logger LOG = Logger.getLogger(SapOrderExchangeOmsCancelRepairCronJob.class);
	private static final String REPAIR_JOB_CANCEL_MINUTES = "ysapomsfulfillment.repair.job.cancel.minutes";

	private OrderExchangeRepair orderExchangeRepair;
	private BusinessProcessService businessProcessService;
	private SapOrderCancelService sapOrderCancelService;
	private TimeService timeService;

	@Override
	public PerformResult perform(CronJobModel cronJobModel) {

		resetOrders(getOrderExchangeRepair().findAllOrdersInStatus(OrderStatus.CANCELLING));

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected void resetOrders(List<OrderModel> orderList) {

		orderList.stream().filter(entry -> isOrderRestorable(entry)).forEach(order -> {
			try {

				getSapOrderCancelService().restoreAfterCancelFailed(order);

			} catch (OrderCancelException e) {
				LOG.error(String.format("Error while restoring cancelled Order %s", order.getCode()) + order.getCode(),
						e);
			}
		});
	}
	
	protected boolean isOrderRestorable(OrderModel order) {

		long configTime = Long.parseLong(Config.getParameter(REPAIR_JOB_CANCEL_MINUTES));

		final long actualTime = TimeUnit.MILLISECONDS.toMinutes(timeService.getCurrentTime().getTime()
				- order.getDate().getTime());

		return actualTime > configTime;
	}

	protected OrderExchangeRepair getOrderExchangeRepair() {
		return orderExchangeRepair;
	}

	@Required
	public void setOrderExchangeRepair(OrderExchangeRepair orderExchangeRepair) {
		this.orderExchangeRepair = orderExchangeRepair;
	}

	protected BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}

	protected SapOrderCancelService getSapOrderCancelService() {
		return sapOrderCancelService;
	}

	@Required
	public void setSapOrderCancelService(SapOrderCancelService sapOrderCancelService) {
		this.sapOrderCancelService = sapOrderCancelService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}

	@Required
	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}

}
