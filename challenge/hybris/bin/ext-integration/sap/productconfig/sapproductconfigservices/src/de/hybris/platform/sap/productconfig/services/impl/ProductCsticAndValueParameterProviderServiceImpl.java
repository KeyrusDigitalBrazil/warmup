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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.services.ProductCsticAndValueParameterProviderService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Service for retrieving characteristics and their possible values for rules in backoffice
 */
public class ProductCsticAndValueParameterProviderServiceImpl implements ProductCsticAndValueParameterProviderService
{
	private ProviderFactory providerFactory;

	@Override
	public Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode)
	{
		return getProviderFactory().getProductCsticAndValueParameterProvider()
				.retrieveProductCsticsAndValuesParameters(productCode);
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	/**
	 * @param providerFactory
	 *           factory to access the analytics provider
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}



}
