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
package de.hybris.platform.customercouponfacades.converter.populators;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link SearchPageData<CustomerCouponModel>} to {@link CustomerCouponSearchPageData}
 */
public class CustomerCouponSearchPageDataPopulator
		implements Populator<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData>
{

	private Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter;

	@Override
	public void populate(final SearchPageData<CustomerCouponModel> source, final CustomerCouponSearchPageData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setPagination(source.getPagination());
		target.setSorts(source.getSorts());
		target.setCoupons(Converters.convertAll(source.getResults(), getCustomerCouponConverter()));
	}


	protected Converter<CustomerCouponModel, CustomerCouponData> getCustomerCouponConverter()
	{
		return customerCouponConverter;
	}

	@Required
	public void setCustomerCouponConverter(final Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter)
	{
		this.customerCouponConverter = customerCouponConverter;
	}

}
