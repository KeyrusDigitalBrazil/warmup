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
package de.hybris.platform.sap.productconfig.rules.conditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleValueOperator;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class RuleConfigurableProductConditionTranslatorTest
{
	public static final String PRODUCT_XYZ = "PRODUCT_XYZ";
	public static final String CSTIC_XYZ = "CSTIC_XYZ";
	public static final String CSTIC_VALUE_XYZ = "CSTIC_VALUE_XYZ";
	public static final String V_PRODUCT = "$v_Product";
	public static final String V_CSTIC_VALUE = "$v_CsticValue";
	public static final String V_CSTIC = "$v_Cstic";
	private RuleConfigurableProductConditionTranslator classUnderTest;

	@Mock
	private RuleCompilerContext context;

	@Mock
	private RuleIrLocalVariablesContainer variablesContainer;

	@Mock
	private RuleConditionData condition;

	private RuleConditionDefinitionData conditionDefinition;

	Map<String, RuleParameterData> ruleParameters;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new RuleConfigurableProductConditionTranslator();

		conditionDefinition = new RuleConditionDefinitionData();

		ruleParameters = new HashMap<String, RuleParameterData>();

		final RuleParameterData productParameter = new RuleParameterData();
		productParameter.setValue(PRODUCT_XYZ);
		ruleParameters.put(RuleConfigurableProductInCartConditionTranslator.PRODUCT_PARAM, productParameter);

		final RuleParameterData csticParameter = new RuleParameterData();
		csticParameter.setValue(CSTIC_XYZ);
		ruleParameters.put(RuleConfigurableProductInCartConditionTranslator.CSTIC_PARAM, csticParameter);

		final RuleParameterData csticValueParameter = new RuleParameterData();
		csticValueParameter.setValue(CSTIC_VALUE_XYZ);
		ruleParameters.put(RuleConfigurableProductInCartConditionTranslator.CSTIC_VALUE_PARAM, csticValueParameter);

		given(condition.getParameters()).willReturn(ruleParameters);

		given(context.createLocalContainer()).willReturn(variablesContainer);

		given(context.generateLocalVariable(variablesContainer, ProductConfigRAO.class)).willReturn(V_PRODUCT);
		given(context.generateLocalVariable(variablesContainer, CsticValueRAO.class)).willReturn(V_CSTIC_VALUE);
		given(context.generateLocalVariable(variablesContainer, CsticRAO.class)).willReturn(V_CSTIC);
	}

	@Test
	public void testTranslate() throws RuleCompilerException
	{
		final RuleIrExistsCondition irConfigurableProductCondition = (RuleIrExistsCondition) classUnderTest.translate(context,
				condition, conditionDefinition);

		assertNotNull(irConfigurableProductCondition);

		final List<RuleIrCondition> children = irConfigurableProductCondition.getChildren();
		assertEquals(6, children.size());

		final RuleIrAttributeCondition irCsticValueCondition = (RuleIrAttributeCondition) children.get(0);
		verifyAttributeCondition(irCsticValueCondition, V_CSTIC_VALUE,
				RuleConfigurableProductConditionTranslator.CSTIC_VALUE_RAO_NAME_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				CSTIC_VALUE_XYZ);

		final RuleIrAttributeCondition irCsticCondition = (RuleIrAttributeCondition) children.get(1);
		verifyAttributeCondition(irCsticCondition, V_CSTIC, RuleConfigurableProductConditionTranslator.CSTIC_RAO_NAME_ATTRIBUTE,
				RuleIrAttributeOperator.EQUAL, CSTIC_XYZ);

		final RuleIrAttributeRelCondition irCsticCsticValueCategoryRel = (RuleIrAttributeRelCondition) children.get(2);
		verifyAttributeRelCondition(irCsticCsticValueCategoryRel, V_CSTIC,
				RuleConfigurableProductConditionTranslator.CSTIC_RAO_ASSIGNED_VALUES_ATTRIBUTE, RuleIrAttributeOperator.CONTAINS,
				V_CSTIC_VALUE);

		final RuleIrAttributeRelCondition irProductConfigurationCsticCategoryRel = (RuleIrAttributeRelCondition) children.get(3);
		verifyAttributeRelCondition(irProductConfigurationCsticCategoryRel, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_CSTICS_ATTRIBUTE,
				RuleIrAttributeOperator.CONTAINS, V_CSTIC);

		final RuleIrAttributeCondition irProductConfigurationCondition = (RuleIrAttributeCondition) children.get(4);
		verifyAttributeCondition(irProductConfigurationCondition, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_CODE_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				PRODUCT_XYZ);

		final RuleIrAttributeCondition irProductConfigurationInCartCondition = (RuleIrAttributeCondition) children.get(5);
		verifyAttributeCondition(irProductConfigurationInCartCondition, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_IN_CART_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				Boolean.FALSE);
	}

	private void verifyAttributeCondition(final RuleIrAttributeCondition ruleIrCondition, final String variable,
			final String attribute, final RuleIrAttributeOperator operator, final Object value)
	{
		assertEquals(variable, ruleIrCondition.getVariable());
		assertEquals(attribute, ruleIrCondition.getAttribute());
		assertEquals(operator, ruleIrCondition.getOperator());
		assertEquals(value, ruleIrCondition.getValue());
	}

	private void verifyAttributeRelCondition(final RuleIrAttributeRelCondition ruleIrCondition, final String variable,
			final String attribute, final RuleIrAttributeOperator operator, final String targetVariable)
	{
		assertEquals(variable, ruleIrCondition.getVariable());
		assertEquals(attribute, ruleIrCondition.getAttribute());
		assertEquals(operator, ruleIrCondition.getOperator());
		assertEquals(targetVariable, ruleIrCondition.getTargetVariable());
	}

	@Test
	public void testTranslateDoesNotHave() throws RuleCompilerException
	{
		final RuleParameterData valueOperatorParameter = new RuleParameterData();
		valueOperatorParameter.setValue(ProductConfigRuleValueOperator.DOES_NOT_HAVE);
		ruleParameters.put(RuleConfigurableProductInCartConditionTranslator.VALUE_OPERATOR_PARAM, valueOperatorParameter);


		final RuleIrExistsCondition irConfigurableProductCondition = (RuleIrExistsCondition) classUnderTest.translate(context,
				condition, conditionDefinition);

		assertNotNull(irConfigurableProductCondition);

		final List<RuleIrCondition> children = irConfigurableProductCondition.getChildren();
		assertEquals(5, children.size());

		final RuleIrNotCondition irNotCondition = (RuleIrNotCondition) children.get(0);

		final RuleIrAttributeCondition irCsticValueCondition = (RuleIrAttributeCondition) irNotCondition.getChildren().get(0);
		verifyAttributeCondition(irCsticValueCondition, V_CSTIC_VALUE,
				RuleConfigurableProductConditionTranslator.CSTIC_VALUE_RAO_NAME_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				CSTIC_VALUE_XYZ);

		final RuleIrAttributeRelCondition irCsticCsticValueCategoryRel = (RuleIrAttributeRelCondition) irNotCondition.getChildren()
				.get(1);
		verifyAttributeRelCondition(irCsticCsticValueCategoryRel, V_CSTIC,
				RuleConfigurableProductConditionTranslator.CSTIC_RAO_ASSIGNED_VALUES_ATTRIBUTE, RuleIrAttributeOperator.CONTAINS,
				V_CSTIC_VALUE);

		final RuleIrAttributeCondition irCsticCondition = (RuleIrAttributeCondition) children.get(1);
		verifyAttributeCondition(irCsticCondition, V_CSTIC, RuleConfigurableProductConditionTranslator.CSTIC_RAO_NAME_ATTRIBUTE,
				RuleIrAttributeOperator.EQUAL, CSTIC_XYZ);

		final RuleIrAttributeRelCondition irProductConfigurationCsticCategoryRel = (RuleIrAttributeRelCondition) children.get(2);
		verifyAttributeRelCondition(irProductConfigurationCsticCategoryRel, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_CSTICS_ATTRIBUTE,
				RuleIrAttributeOperator.CONTAINS, V_CSTIC);

		final RuleIrAttributeCondition irProductConfigurationCondition = (RuleIrAttributeCondition) children.get(3);
		verifyAttributeCondition(irProductConfigurationCondition, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_CODE_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				PRODUCT_XYZ);

		final RuleIrAttributeCondition irProductConfigurationInCartCondition = (RuleIrAttributeCondition) children.get(4);
		verifyAttributeCondition(irProductConfigurationInCartCondition, V_PRODUCT,
				RuleConfigurableProductConditionTranslator.PRODUCT_CONFIGURATION_RAO_IN_CART_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				Boolean.FALSE);
	}

	@Test
	public void testTranslateWithoutProduct()
	{
		ruleParameters.remove(RuleConfigurableProductInCartConditionTranslator.PRODUCT_PARAM);
		RuleIrCondition result = classUnderTest.translate(context,
				condition, conditionDefinition);
		assertEquals(RuleIrFalseCondition.class, result.getClass());
	}
}
