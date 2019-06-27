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
package de.hybris.platform.commercefacades.voucher.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.voucher.VoucherService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populate the {@link de.hybris.platform.commercefacades.order.data.AbstractOrderData} with the vouchers applied to
 * {@link de.hybris.platform.core.model.order.AbstractOrderModel}
 */
public class OrderAppliedVouchersPopulator implements Populator<AbstractOrderModel, AbstractOrderData>
{
	private VoucherService voucherService;

	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		Collection<String> vouchers = Collections.emptyList();

		if (source instanceof CartModel)
		{
			vouchers = getVoucherService().getAppliedVoucherCodes((CartModel) source);
		}
		else if (source instanceof OrderModel)
		{
			vouchers = getVoucherService().getAppliedVoucherCodes((OrderModel) source);
		}

		target.setAppliedVouchers(new ArrayList<>(vouchers));
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	@Required
	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

}
