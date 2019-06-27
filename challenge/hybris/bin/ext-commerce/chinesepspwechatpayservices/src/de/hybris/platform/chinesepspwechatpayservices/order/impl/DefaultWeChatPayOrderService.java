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
package de.hybris.platform.chinesepspwechatpayservices.order.impl;

import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.chinesepspwechatpayservices.order.WeChatPayOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.impl.DefaultOrderService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Global class for Chinesepspwechatpayservices order impl.
 */
public class DefaultWeChatPayOrderService extends DefaultOrderService implements WeChatPayOrderService
{
	private transient WeChatPayOrderDao weChatPayOrderDao;

	@Override
	public Optional<OrderModel> getOrderByCode(final String code)
	{
		return weChatPayOrderDao.findOrderByCode(code);
	}

	@Required
	public void setWeChatPayOrderDao(final WeChatPayOrderDao weChatPayOrderDao)
	{
		this.weChatPayOrderDao = weChatPayOrderDao;
	}

	protected WeChatPayOrderDao getWeChatPayOrderDao()
	{
		return weChatPayOrderDao;
	}


}
