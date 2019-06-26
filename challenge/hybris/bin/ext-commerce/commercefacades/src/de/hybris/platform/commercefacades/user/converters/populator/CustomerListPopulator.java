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
package de.hybris.platform.commercefacades.user.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commerceservices.model.CustomerListModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converter implementation for {@link de.hybris.platform.commerceservices.model.CustomerListModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerListData} as target type.
 */
public class CustomerListPopulator implements Populator<CustomerListModel, CustomerListData>
{
	private Map<String, String> customerListAdditionalColumnsMap;

	@Override
	public void populate(final CustomerListModel source, final CustomerListData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		final List<String> columnValues = new ArrayList<>();
		for (final String columnKey : source.getAdditionalColumnsKeys())
		{
			if (getCustomerListAdditionalColumnsMap().containsKey(columnKey))
			{
				columnValues.add(getCustomerListAdditionalColumnsMap().get(columnKey));
			}
		}
		target.setAdditionalColumnsKeys(columnValues);
		target.setSearchBoxEnabled(source.isSearchBoxEnabled());
	}

	protected Map<String, String> getCustomerListAdditionalColumnsMap()
	{
		return customerListAdditionalColumnsMap;
	}

	@Required
	public void setCustomerListAdditionalColumnsMap(final Map<String, String> customerListAdditionalColumnsMap)
	{
		this.customerListAdditionalColumnsMap = customerListAdditionalColumnsMap;
	}


}
