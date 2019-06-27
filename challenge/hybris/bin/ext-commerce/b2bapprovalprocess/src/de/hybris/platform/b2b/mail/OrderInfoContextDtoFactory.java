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
package de.hybris.platform.b2b.mail;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * A factory for creating {@link de.hybris.platform.b2b.mail.impl.OrderInfoContextDto} and populating the Dto with data
 * based on the order
 * 
 * @param <T>
 *           A paramatarized dto
 */
public interface OrderInfoContextDtoFactory<T>
{
	public T createOrderInfoContextDto(final OrderModel order);
}
