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

package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link de.hybris.platform.commercefacades.order.data.OrderData} with {@link OrderModel}.
 */
public class B2BOrderApprovalDashboardListPopulator implements Populator<OrderModel, OrderData>
{

	private Converter<UserModel, CustomerData> b2bCustomerConverter;

	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setPurchaseOrderNumber(source.getPurchaseOrderNumber());
		target.setB2bCustomerData(b2bCustomerConverter.convert(source.getUser()));

	}

	protected Converter<UserModel, CustomerData> getB2bCustomerConverter()
	{
		return b2bCustomerConverter;
	}

	@Required
	public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter)
	{
		this.b2bCustomerConverter = b2bCustomerConverter;
	}
}
