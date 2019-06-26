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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CustomerData} with data form {@link B2BCustomerModel}.
 */
public class B2BCustomerPermissionPopulator implements Populator<CustomerModel, CustomerData>
{
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		if (source instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) source;

			if (CollectionUtils.isNotEmpty(customer.getPermissions()))
			{
				target.setPermissions(Converters.convertAll(customer.getPermissions(), getB2BPermissionConverter()));
			}

		}
	}

	protected Converter<B2BPermissionModel, B2BPermissionData> getB2BPermissionConverter()
	{
		return b2BPermissionConverter;
	}

	@Required
	public void setB2BPermissionConverter(final Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter)
	{
		this.b2BPermissionConverter = b2BPermissionConverter;
	}
}
