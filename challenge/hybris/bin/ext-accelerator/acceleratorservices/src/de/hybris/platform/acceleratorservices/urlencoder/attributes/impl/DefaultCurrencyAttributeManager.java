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
package de.hybris.platform.acceleratorservices.urlencoder.attributes.impl;

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Collection;

import de.hybris.platform.returns.model.ReturnProcessModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;


/**
 * Default implementation for currency attribute handler. This changes the store currency if currency is included as one
 * of the encoding attribute.
 */
public class DefaultCurrencyAttributeManager extends AbstractUrlEncodingAttributeManager
{
	@Override
	public Collection<String> getAllAvailableValues()
	{
		return CollectionUtils.collect(getCommerceCommonI18NService().getAllCurrencies(), new Transformer()
		{
			@Override
			public Object transform(final Object object)
			{
				return ((CurrencyModel) object).getIsocode();
			}
		});
	}

	@Override
	public void updateAndSyncForAttrChange(final String value)
	{
		if (isValid(value))
		{
			getStoreSessionService().setCurrentCurrency(value);
		}
	}

	@Override
	public String getDefaultValue()
	{
		return getCommerceCommonI18NService().getDefaultCurrency().getIsocode();
	}

	@Override
	public String getCurrentValue()
	{
		return getCommerceCommonI18NService().getCurrentCurrency().getIsocode();
	}

	@Override
	public String getAttributeValueForEmail(final BusinessProcessModel businessProcessModel)
	{
		if(businessProcessModel instanceof StoreFrontCustomerProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getCurrency().getIsocode();
		}
		else if(businessProcessModel instanceof OrderProcessModel)
		{
			return ((OrderProcessModel)businessProcessModel).getOrder().getCurrency().getIsocode();
		}
		else if (businessProcessModel instanceof ConsignmentProcessModel)
		{
			return ((ConsignmentProcessModel) businessProcessModel).getConsignment().getOrder().getCurrency().getIsocode();
		}
		else if (businessProcessModel instanceof ReturnProcessModel)
		{
			return ((ReturnProcessModel) businessProcessModel).getReturnRequest().getOrder().getCurrency().getIsocode();
		}
		return getDefaultValue();
	}
}
