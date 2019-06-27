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
package de.hybris.platform.sap.sapordermgmtservices.order;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.List;


/**
 * Service representation of an order and order history for SAP synchronous order management
 */
public interface OrderService
{
	/**
	 * Fetches an order from the back end
	 *
	 * @param code
	 *           Technical ID of the order
	 * @return hybris order representation
	 */
	OrderData getOrderForCode(String code);

	/**
	 * Perform an order search, taking pagination and sorting into account
	 *
	 * @param pageableData
	 *           Contains paging and sorting attributes
	 * @param statuses
	 *           Order statuses the search should be performed for
	 * @return Search result, including sorting and pagination
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses);

	/**
	 * Perform an order search without pagination and sorting
	 *
	 * @param statuses
	 *           Order statuses the search should be performed for
	 * @return List of orders
	 */
	List<OrderHistoryData> getOrderHistoryForStatuses(final OrderStatus... statuses);

	/**
	 * Return item from given order code and item number.
	 *
	 * @param orderCode
	 *           Order code
	 * @param itemNumber
	 *           ECC item number (corresponds to entry number in OrderEntry)
	 * @return Item
	 */

	Item getItemFromOrder(final String orderCode, final String itemNumber);



}
