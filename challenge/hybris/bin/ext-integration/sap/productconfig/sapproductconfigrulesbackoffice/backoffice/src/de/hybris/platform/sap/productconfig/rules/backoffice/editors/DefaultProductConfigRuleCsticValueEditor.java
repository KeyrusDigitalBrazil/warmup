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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.hybris.cockpitng.editors.EditorContext;


/**
 * Default implementation of the characteristic value editor in the product configuration rules
 */
public class DefaultProductConfigRuleCsticValueEditor extends AbstractProductConfigRuleParameterEditor
{
	@Override
	protected List<Object> getPossibleValues(final EditorContext<Object> context)
	{
		List<Object> values;

		final Map<String, Object> parameters = context.getParameters();

		final String cstic = (String) parameters.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_CSTIC);

		final List<String> productCodeList = (List<String>) parameters
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST);

		if (productCodeList != null)
		{
			values = getPossibleValuesForActionParameter(productCodeList, cstic);
		}
		else
		{
			final String productCode = (String) parameters
					.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT);

			values = getPossibleValuesForConditionParameter(productCode, cstic);
		}
		return values;
	}

	protected List<Object> getPossibleValuesForConditionParameter(final String productCode, final String cstic)
	{
		final List<Object> values = new ArrayList<>();

		if (StringUtils.isNotEmpty(productCode))
		{
			addValuesForProductCode(values, productCode, cstic);
		}
		return values;
	}

	protected void addValuesForProductCode(final List<Object> values, final String productCode, final String cstic)
	{
		final String productCodeForProvider = useBaseProductCodeForChangeableVariant(productCode);

		final Map<String, CsticParameterWithValues> csticParametersWithValues = getParameterProviderService()
				.retrieveProductCsticsAndValuesParameters(productCodeForProvider);

		final CsticParameterWithValues csticParameterWithValues = csticParametersWithValues.get(cstic);

		if (csticParameterWithValues != null)
		{
			for (final ValueParameter valueParameter : csticParameterWithValues.getValues())
			{
				final String valueName = valueParameter.getValueName();
				if (!values.contains(valueName))
				{
					values.add(valueName);
				}
			}
		}
	}

	protected List<Object> getPossibleValuesForActionParameter(final List<String> productCodeList, final String cstic)
	{
		final List<Object> values = new ArrayList<>();
		for (final String productCode : productCodeList)
		{
			if (StringUtils.isNotEmpty(productCode))
			{
				addValuesForProductCode(values, productCode, cstic);
			}
		}
		return values;
	}
}
