/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.yacceleratorordermanagement.integration.util;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.warehousing.constants.WarehousingConstants;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.util.DeclineEntryBuilder;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;


/**
 * this class is mainly to decline orders
 */
@Component
public class DeclineUtil extends ProcessUtil
{
	protected static final String DECLINE_ENTRIES = "declineEntries";
	protected static final String REALLOCATE_CONSIGNMENT_CHOICE = "reallocateConsignment";
	protected static final String IS_CONSIGNMENT_AUTO_DECLINED = "isConsignmentAutoDecline";

	/**
	 * automatically decline consignment
	 *
	 * @param consignmentModels
	 * @param declineEntryInfo
	 * @param orderProcessModel
	 * @param declineReason
	 * @throws InterruptedException
	 */
	public void autoDeclineDefaultConsignment(final ConsignmentModel consignmentModels,
			final Map<ConsignmentEntryModel, Long> declineEntryInfo, final OrderProcessModel orderProcessModel,
			final DeclineReason declineReason) throws InterruptedException
	{
		autoDeclineDefaultConsignment(consignmentModels, declineEntryInfo, orderProcessModel, declineReason, false);
	}

	/**
	 * automatically decline consignment
	 *
	 * @param consignmentModels
	 * @param declineEntryInfo
	 * @param orderProcessModel
	 * @param declineReason
	 * @param multiBusinessProcessParameters
	 * 		true to generate a BusinessProcessParameters with declineEntries and isConsignmentAutoDecline
	 * @throws InterruptedException
	 */
	public void autoDeclineDefaultConsignment(final ConsignmentModel consignmentModels,
			final Map<ConsignmentEntryModel, Long> declineEntryInfo, final OrderProcessModel orderProcessModel,
			final DeclineReason declineReason, final boolean multiBusinessProcessParameters) throws InterruptedException
	{
		//when decline the order
		final ConsignmentModel cons = consignmentModels;

		final String consignmentProcessCode = cons.getCode() + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX;
		final ConsignmentProcessModel consignmentProcess = cons.getConsignmentProcesses().stream()
				.filter(cp -> cp.getCode().equals(consignmentProcessCode)).findFirst().get();

		waitUntilConsignmentProcessIsNotRunning(orderProcessModel, cons, timeOut);
		final BusinessProcessParameterModel declineParam = new BusinessProcessParameterModel();
		declineParam.setName(DECLINE_ENTRIES);
		declineParam.setValue(DeclineEntryBuilder.aDecline().build_Auto(declineEntryInfo, declineReason));
		declineParam.setProcess(consignmentProcess);
		if (multiBusinessProcessParameters)
		{
			final BusinessProcessParameterModel secondParam = new BusinessProcessParameterModel();
			secondParam.setName(IS_CONSIGNMENT_AUTO_DECLINED);
			secondParam.setValue(true);
			secondParam.setProcess(consignmentProcess);
			consignmentProcess.setContextParameters(Sets.newHashSet(declineParam, secondParam));
		}
		else
		{
			consignmentProcess.setContextParameters(Sets.newHashSet(declineParam));
		}
		getModelService().save(consignmentProcess);

		//when decline the order
		getConsignmentBusinessProcessService()
				.triggerChoiceEvent(cons, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
		waitUntilConsignmentProcessIsNotRunning(orderProcessModel, cons, timeOut);
	}

	/**
	 * manually decline consignment
	 *
	 * @param consignmentModels
	 * @param declineEntryInfo
	 * @param orderProcessModel
	 * @param warehouseModel
	 * @param declineReason
	 * @throws InterruptedException
	 */
	public void manualDeclineDefaultConsignment(final ConsignmentModel consignmentModels,
			final Map<ConsignmentEntryModel, Long> declineEntryInfo, final OrderProcessModel orderProcessModel,
			final WarehouseModel warehouseModel, final DeclineReason declineReason) throws InterruptedException
	{
		//when decline the order
		final ConsignmentModel cons = consignmentModels;

		final String consignmentProcessCode = cons.getCode() + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX;
		final ConsignmentProcessModel consignmentProcess = cons.getConsignmentProcesses().stream()
				.filter(cp -> cp.getCode().equals(consignmentProcessCode)).findFirst().get();

		waitUntilConsignmentProcessIsNotRunning(orderProcessModel, cons, timeOut);
		final BusinessProcessParameterModel declineParam = new BusinessProcessParameterModel();
		declineParam.setName(DECLINE_ENTRIES);
		declineParam.setValue(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo, warehouseModel, declineReason));
		declineParam.setProcess(consignmentProcess);
		consignmentProcess.setContextParameters(Collections.singleton(declineParam));
		getModelService().save(consignmentProcess);

		//when decline the order
		getConsignmentBusinessProcessService()
				.triggerChoiceEvent(cons, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
		waitUntilConsignmentProcessIsNotRunning(orderProcessModel, cons, timeOut);
	}

	protected WarehousingBusinessProcessService<ConsignmentModel> getConsignmentBusinessProcessService()
	{
		return consignmentBusinessProcessService;
	}

	public void setConsignmentBusinessProcessService(
			final WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService)
	{
		this.consignmentBusinessProcessService = consignmentBusinessProcessService;
	}
}
