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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.model.AbstractRuleTemplateModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;


@UnitTest
public class ProductconfigActionsEditorSectionRendererTest
{
	protected static final String RULE_CODE = "TEST_RULE";

	@Mock
	ProductconfigProductCodeExtractor productCodeExtractor;

	@Mock
	ModelService modelService;

	@Mock
	RuleService ruleService;

	@InjectMocks
	private final ProductconfigActionsEditorSectionRenderer classUnderTest = new ProductconfigActionsEditorSectionRendererForTest();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add("PRODUCT_1");
		productCodeList.add("PRODUCT_2");
		given(productCodeExtractor.retrieveProductCodeList(Mockito.any())).willReturn(productCodeList);
		when(ruleService.getRuleTypeFromTemplate(any())).thenReturn(null);
	}

	@Test
	public void testGetEditorId()
	{
		final String editorId = classUnderTest.getEditorId();
		assertEquals(ProductconfigActionsEditorSectionRenderer.PRODUCTCONFIG_ACTIONS_EDITOR_ID, editorId);
	}

	@Test
	public void testAddProductCodeListToParameters()
	{
		final Map<Object, Object> parameters = new HashMap<>();
		final ProductConfigSourceRuleModel model = new ProductConfigSourceRuleModel();
		model.setCode(RULE_CODE);

		classUnderTest.addProductCodeListToParameters(model, parameters);

		final List<String> retrievedProductCodeList = (List<String>) parameters
				.get(SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST);

		assertEquals(2, retrievedProductCodeList.size());
		assertEquals("PRODUCT_1", retrievedProductCodeList.get(0));
		assertEquals("PRODUCT_2", retrievedProductCodeList.get(1));
	}

	@Test
	public void testFillParametersWithNoProductConfigSourceRule()
	{
		final Map<Object, Object> parameters = new HashMap<>();
		final AbstractRuleTemplateModel model = new SourceRuleTemplateModel();

		classUnderTest.fillParameters(model, null, parameters);

		for (final Object paramName : parameters.keySet())
		{
			assertFalse("parameter productCodeList nor expected",
					SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST.equals(paramName));
		}
	}

	private class ProductconfigActionsEditorSectionRendererForTest extends ProductconfigActionsEditorSectionRenderer
	{
		@Override
		protected boolean canChangeProperty(final DataAttribute attribute, final Object instance)
		{
			return false;
		}
	}
}
