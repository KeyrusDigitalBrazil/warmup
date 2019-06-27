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
 */
package de.hybris.platform.yacceleratorordermanagement.email;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;


/**
 * Velocity context for return label email.
 */
public class ReturnLabelEmailContext extends AbstractEmailContext<ReturnProcessModel>
{
	private Converter<OrderModel, OrderData> orderConverter;
	private OrderData orderData;

	@Override
	public void init(final ReturnProcessModel returnProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(returnProcessModel, emailPageModel);
		orderData = getOrderConverter().convert(returnProcessModel.getReturnRequest().getOrder());
	}

	@Override
	protected BaseSiteModel getSite(final ReturnProcessModel returnProcessModel)
	{
		return returnProcessModel.getReturnRequest().getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ReturnProcessModel returnProcessModel)
	{
		return (CustomerModel) returnProcessModel.getReturnRequest().getOrder().getUser();
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	public OrderData getOrder()
	{
		return orderData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final ReturnProcessModel returnProcessModel)
	{
		return returnProcessModel.getReturnRequest().getOrder().getLanguage();
	}

}
