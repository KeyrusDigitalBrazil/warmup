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
package de.hybris.platform.sap.productconfig.runtime.mock.provider.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMockFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * Class to provide based on the product code, all relevant Cstic/CsticValue pairs, for the backoffice rule editor.
 */
public class ProductCsticAndValueParameterProviderMockImpl implements ProductCsticAndValueParameterProvider
{
	private ConfigMockFactory configMockFactory;

	@Override
	public Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode)
	{
		final ConfigModel model = getConfigModel(productCode);
		final List<CsticModel> cstics = model.getRootInstance().getCstics();

		return cstics.stream().collect(Collectors.toMap(CsticModel::getName, this::getCsticParameters));
	}

	protected CsticParameterWithValues getCsticParameters(final CsticModel cstic)
	{
		final CsticParameter csticParameter = new CsticParameter();
		csticParameter.setCsticName(cstic.getName());
		csticParameter.setCsticDescription(cstic.getLanguageDependentName());

		final List<ValueParameter> values = getValuesForCstic(cstic.getAssignableValues());

		final CsticParameterWithValues csticParameterWithValues = new CsticParameterWithValues();
		csticParameterWithValues.setCstic(csticParameter);
		csticParameterWithValues.setValues(values);

		return csticParameterWithValues;
	}

	protected List<ValueParameter> getValuesForCstic(final List<CsticValueModel> csticValues)
	{
		final List<ValueParameter> values = new ArrayList<>();
		for (final CsticValueModel valueModel : csticValues)
		{
			final ValueParameter value = new ValueParameter();

			value.setValueName(valueModel.getName());
			value.setValueDescription(valueModel.getLanguageDependentName());

			values.add(value);
		}
		return values;
	}

	protected ConfigModel getConfigModel(final String productCode)
	{
		final ConfigMock mock = getConfigMockFactory().createConfigMockForProductCode(productCode);
		return mock.createDefaultConfiguration();
	}

	/**
	 * @return the configMockFactory
	 */
	public ConfigMockFactory getConfigMockFactory()
	{
		return configMockFactory;
	}

	/**
	 * @param configMockFactory
	 *           the runTimeConfigMockFactory to set
	 */
	public void setConfigMockFactory(final ConfigMockFactory configMockFactory)
	{
		this.configMockFactory = configMockFactory;
	}
}
