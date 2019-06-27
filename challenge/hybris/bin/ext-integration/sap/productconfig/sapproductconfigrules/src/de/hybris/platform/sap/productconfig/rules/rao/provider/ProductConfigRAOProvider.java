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

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * provider for the {@link ProductConfigRAO}
 */
public class ProductConfigRAOProvider implements RAOProvider<ConfigModel>
{


	private Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter;

	protected ProductConfigRAO createRAO(final ConfigModel configModel)
	{
		return getProductConfigRaoConverter().convert(configModel);
	}

	/**
	 * @return the productConfigRaoConverter
	 */
	public Converter<ConfigModel, ProductConfigRAO> getProductConfigRaoConverter()
	{
		return productConfigRaoConverter;
	}

	/**
	 * @param productConfigRaoConverter
	 *           the productConfigRaoConverter to set
	 */
	public void setProductConfigRaoConverter(final Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter)
	{
		this.productConfigRaoConverter = productConfigRaoConverter;
	}

	@Override
	public Set<Object> expandFactModel(final ConfigModel configModel)
	{
		final Set<Object> raoSet = new LinkedHashSet<Object>();

		final ProductConfigRAO productConfigRAO = createRAO(configModel);
		raoSet.add(productConfigRAO);
		addCsticAndValueRAOs(raoSet, productConfigRAO);
		return raoSet;
	}

	protected void addCsticAndValueRAOs(final Set<Object> raoSet, final ProductConfigRAO productConfigRAO)
	{
		if (isEmpty(productConfigRAO.getCstics()))
		{
			return;
		}
		for (final CsticRAO csticRAO : productConfigRAO.getCstics())
		{
			raoSet.add(csticRAO);
			if (isNotEmpty(csticRAO.getAssignedValues()))
			{
				for (final CsticValueRAO csticValueRAO : csticRAO.getAssignedValues())
				{
					raoSet.add(csticValueRAO);
				}
			}
		}
		return;
	}


}
