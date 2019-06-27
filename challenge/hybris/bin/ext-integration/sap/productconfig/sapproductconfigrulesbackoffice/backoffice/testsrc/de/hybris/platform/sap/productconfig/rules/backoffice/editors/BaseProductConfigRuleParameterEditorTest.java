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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.sap.productconfig.services.ProductCsticAndValueParameterProviderService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.hybris.cockpitng.editors.EditorContext;


@UnitTest
public class BaseProductConfigRuleParameterEditorTest
{

	protected static final String PRODUCT_CODE = "XXX";
	protected static final String PRODUCT_CODE2 = "YYY";

	protected static final String CSTIC_1 = "C1";
	protected static final String CSTIC_2 = "C2";
	protected static final String CSTIC_3 = "C3";
	protected static final String CSTIC_4 = "C4";

	@Mock
	protected EditorContext<Object> context;

	@Mock
	protected ProductCsticAndValueParameterProviderService parameterProviderService;

	@Mock
	protected FlexibleSearchService flexibleSearchService;

	@Mock
	protected SearchResult<Object> searchResult;

	protected Map<String, CsticParameterWithValues> csticParametersWithValues;
	protected Map<String, CsticParameterWithValues> csticParametersWithValues2;

	protected Map<String, Object> parameters;
	protected ProductModel productModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		csticParametersWithValues = createCsticWithValues();
		csticParametersWithValues2 = createCsticWithValues2();

		productModel = new ProductModel();
		productModel.setCode(PRODUCT_CODE);

		parameters = new HashMap<>();
		parameters.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT, productModel.getCode());
		parameters.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_CSTIC, CSTIC_2);

		given(context.getParameters()).willReturn(parameters);
		given(parameterProviderService.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE))
				.willReturn(csticParametersWithValues);
		given(parameterProviderService.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE2))
				.willReturn(csticParametersWithValues2);

		given(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).willReturn(searchResult);
		given(searchResult.getResult()).willReturn(new ArrayList<Object>());

	}

	protected Map<String, CsticParameterWithValues> createCsticWithValues()
	{
		final Map<String, CsticParameterWithValues> csticParametersWithValues = new HashMap<>();
		addEntry(csticParametersWithValues, CSTIC_1, 3);
		addEntry(csticParametersWithValues, CSTIC_2, 4);
		addEntry(csticParametersWithValues, CSTIC_3, 5);
		return csticParametersWithValues;
	}

	protected Map<String, CsticParameterWithValues> createCsticWithValues2()
	{
		final Map<String, CsticParameterWithValues> csticParametersWithValues = new HashMap<>();
		addEntry(csticParametersWithValues, CSTIC_2, 5);
		csticParametersWithValues.get(CSTIC_2).getValues().remove(3);
		csticParametersWithValues.get(CSTIC_2).getValues().remove(2);
		csticParametersWithValues.get(CSTIC_2).getValues().remove(1);
		addEntry(csticParametersWithValues, CSTIC_4, 2);
		return csticParametersWithValues;
	}

	private void addEntry(final Map<String, CsticParameterWithValues> parameterMap, final String csticName,
			final int numberOfValues)
	{
		final CsticParameterWithValues csticParameterWithValues = new CsticParameterWithValues();

		final CsticParameter csticParameter = new CsticParameter();
		csticParameter.setCsticName(csticName);

		final List<ValueParameter> valueList = new ArrayList<>();

		for (int i = 1; i <= numberOfValues; i++)
		{
			final ValueParameter valueParameter = new ValueParameter();
			valueParameter.setValueName(csticName + i);
			valueList.add(valueParameter);
		}

		csticParameterWithValues.setCstic(csticParameter);
		csticParameterWithValues.setValues(valueList);

		parameterMap.put(csticName, csticParameterWithValues);
	}

	@Test
	public void testCreateCsticWithValues()
	{
		final Map<String, CsticParameterWithValues> csticsWithValues = createCsticWithValues();
		assertEquals(3, csticsWithValues.size());
	}
}
