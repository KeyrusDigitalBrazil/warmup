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
package com.sap.hybris.sapomsreturnprocess.returns.strategy;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

import java.util.List;
import java.util.Map;


public interface ReturnOrderStartegy
{
	/**
	 * This method will make a map of order entry and list of consignment as per the splitting strategy
	 *
	 * @param orderEntryConsignmentMap
	 */
	void splitOrder(Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap);


}
