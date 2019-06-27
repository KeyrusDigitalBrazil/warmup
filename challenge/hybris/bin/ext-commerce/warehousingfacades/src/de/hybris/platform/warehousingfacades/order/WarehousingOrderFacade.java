/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingfacades.order;

/**
 * Warehousing Facade for the {@link de.hybris.platform.core.model.order.OrderModel}
 */
public interface WarehousingOrderFacade
{
	/**
	 * Puts Order On Hold
	 *
	 * @param orderCode
	 * 		the {@link de.hybris.platform.core.model.order.OrderModel#CODE} to be put on hold
	 */
	void putOrderOnHold(String orderCode);

	/**
	 * ReSources an {@link de.hybris.platform.core.model.order.OrderModel} by its code
	 *
	 * @param orderCode
	 * 		the {@link de.hybris.platform.core.model.order.OrderModel#CODE} to be resourced
	 */
	void reSource(String orderCode);
}
