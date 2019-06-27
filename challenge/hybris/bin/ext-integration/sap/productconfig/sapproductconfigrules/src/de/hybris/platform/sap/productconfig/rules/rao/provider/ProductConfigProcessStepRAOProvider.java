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
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Set;


/**
 * provider for the {@link ProductConfigProcessStepModel}
 */
public class ProductConfigProcessStepRAOProvider implements RAOProvider<ProductConfigProcessStepModel>
{
	private Converter<ProductConfigProcessStepModel, ProductConfigProcessStepRAO> productConfigProcessStepRaoConverter;

	protected ProductConfigProcessStepRAO createRAO(final ProductConfigProcessStepModel processStepModel)
	{
		return getProductConfigProcessStepRaoConverter().convert(processStepModel);
	}

	/**
	 * @return the productConfigProcessStepRaoConverter
	 */
	public Converter<ProductConfigProcessStepModel, ProductConfigProcessStepRAO> getProductConfigProcessStepRaoConverter()
	{
		return productConfigProcessStepRaoConverter;
	}

	/**
	 * @param productConfigProcessStepRaoConverter
	 *           the productConfigProcessStepRaoConverter to set
	 */
	public void setProductConfigProcessStepRaoConverter(
			final Converter<ProductConfigProcessStepModel, ProductConfigProcessStepRAO> productConfigProcessStepRaoConverter)
	{
		this.productConfigProcessStepRaoConverter = productConfigProcessStepRaoConverter;
	}

	@Override
	public Set<Object> expandFactModel(final ProductConfigProcessStepModel processStepModel)
	{
		return Collections.singleton(createRAO(processStepModel));
	}
}
