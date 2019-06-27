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
package de.hybris.platform.chinesepspalipayservices.order.impl;

import de.hybris.platform.chinesepspalipayservices.dao.AlipayOrderDao;
import de.hybris.platform.chinesepspalipayservices.order.AlipayOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.impl.DefaultOrderService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AlipayOrderService}
 */
public class DefaultAlipayOrderService extends DefaultOrderService implements AlipayOrderService
{
	private transient AlipayOrderDao alipayOrderDao;

	@Required
	public void setAlipayOrderDao(final AlipayOrderDao alipayOrderDao)
	{
		this.alipayOrderDao = alipayOrderDao;
	}

	@Override
	public OrderModel getOrderByCode(final String code)
	{
		return alipayOrderDao.findOrderByCode(code);
	}

	protected AlipayOrderDao getAlipayOrderDao()
	{
		return alipayOrderDao;
	}


}
