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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import de.hybris.platform.chinesepaymentservices.enums.ServiceType;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentInfoStrategy;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;


public class DefaultAlipayPaymentInfoStrategy implements AlipayPaymentInfoStrategy
{
	private UserService userService;

	private ModelService modelService;


	@Override
	public ChinesePaymentInfoModel updatePaymentInfoForPayemntMethod(final ChinesePaymentInfoModel chinesePaymentInfoModel)
	{
		final CustomerModel currentUser = (CustomerModel) userService.getCurrentUser();

		chinesePaymentInfoModel.setCode(currentUser.getUid() + "_" + UUID.randomUUID());
		chinesePaymentInfoModel.setUser(currentUser);

		chinesePaymentInfoModel.setServiceType(ServiceType.DIRECTPAY);
		modelService.save(chinesePaymentInfoModel);
		return chinesePaymentInfoModel;
	}


	@Override
	public void updatePaymentInfoForPlaceOrder(final OrderModel order)
	{
		if (order.getPaymentInfo() != null)
		{
			final ChinesePaymentInfoModel paymentInfo = (ChinesePaymentInfoModel) order.getPaymentInfo();
			paymentInfo.setOrderCode(order.getCode());
			order.setPaymentStatus(PaymentStatus.NOTPAID);
			modelService.save(paymentInfo);
			modelService.save(order);
		}
	}


	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	protected UserService getUserService()
	{
		return userService;
	}


	protected ModelService getModelService()
	{
		return modelService;
	}



}
