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
package de.hybris.platform.marketplacefacades.vendor.converters.populator;

import de.hybris.platform.commercefacades.product.data.VendorReviewData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class CustomerVendorReviewPopulator implements Populator<CustomerVendorReviewModel, VendorReviewData>
{

	private Converter<CustomerModel, CustomerData> customerConverter;

	@Override
	public void populate(final CustomerVendorReviewModel source, final VendorReviewData target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		target.setCommunication(source.getCommunication());
		target.setDelivery(source.getDelivery());
		target.setSatisfaction(source.getSatisfaction());
		target.setComment(source.getComment());
		target.setCreateDate(source.getCreateDate());

		final UserModel user = source.getUser();
		if (user instanceof CustomerModel)
		{
			target.setCustomer(getCustomerConverter().convert((CustomerModel) user));
		}
	}

	protected Converter<CustomerModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	@Required
	public void setCustomerConverter(final Converter<CustomerModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

}
