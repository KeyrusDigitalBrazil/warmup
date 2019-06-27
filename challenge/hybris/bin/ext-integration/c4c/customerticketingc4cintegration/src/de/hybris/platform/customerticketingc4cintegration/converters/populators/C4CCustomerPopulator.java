/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 *
 * Populator for populating the customer Id value
 *
 */
public class C4CCustomerPopulator implements Populator<CustomerModel, CustomerData>
{

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		target.setCustomerId(source.getCustomerID());
	}
}
