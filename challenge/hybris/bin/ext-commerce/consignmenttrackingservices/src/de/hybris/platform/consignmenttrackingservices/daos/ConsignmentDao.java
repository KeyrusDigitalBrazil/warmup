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
package de.hybris.platform.consignmenttrackingservices.daos;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.List;
import java.util.Optional;


/**
 * A interface for querying consignment
 */
public interface ConsignmentDao
{

	/**
	 * @param orderCode
	 *           code of this consignment's order
	 * @param consignmentCode
	 *           code of ConsignmentModel
	 * @return An optional containing the consignment if it exists and an empty optional otherwise
	 */
	Optional<ConsignmentModel> findConsignmentByCode(String orderCode, String consignmentCode);

	/**
	 *
	 * @param orderCode
	 *           order code for the consignments
	 * @return all the consignments belong to this order
	 */
	List<ConsignmentModel> findConsignmentsByOrder(String orderCode);
}
