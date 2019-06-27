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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.conditionseditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.rulebuilderbackoffice.editors.ParameterModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeListModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeNodeModel;
import de.hybris.platform.rulebuilderbackoffice.editors.conditionseditor.ConditionModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductconfigConditionsEditorViewModelTest
{

	private static final String PRODUCT_CODE = "PRODUCT_CODE";

	private ProductconfigConditionsEditorViewModel classUnderTest;

	private Collection<ParameterModel> treeNodeParameters;

	private ParameterModel productParameter;
	private ParameterModel csticParameter;
	private ParameterModel csticValueParameter;

	private ProductModel productParameterValue;
	private static final String CSTIC_PARAMETER_VALUE = "CSTIC_PARAMETER_VALUE";
	private static final String CSTIC_VALUE_PARAMETER_VALUE = "CSTIC_VALUE_PARAMETER_VALUE";


	@Before
	public void setUp()
	{
		classUnderTest = new ProductconfigConditionsEditorViewModel();

		treeNodeParameters = new ArrayList<>();

		productParameter = new ParameterModel();
		productParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
		productParameterValue = new ProductModel();
		productParameterValue.setCode(PRODUCT_CODE);
		productParameter.setValue(productParameterValue);
		treeNodeParameters.add(productParameter);

		final ParameterModel dummyParameter1 = new ParameterModel();
		dummyParameter1.setId("DUMMY1");
		treeNodeParameters.add(dummyParameter1);

		csticParameter = new ParameterModel();
		csticParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		csticParameter.setValue(CSTIC_PARAMETER_VALUE);
		treeNodeParameters.add(csticParameter);

		final ParameterModel dummyParameter2 = new ParameterModel();
		dummyParameter2.setId("DUMMY2");
		treeNodeParameters.add(dummyParameter2);

		csticValueParameter = new ParameterModel();
		csticValueParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		csticValueParameter.setValue(CSTIC_VALUE_PARAMETER_VALUE);
		treeNodeParameters.add(csticValueParameter);
	}

	@Test
	public void testRetrieveTreeNodeParameter()
	{
		final ParameterModel csticParameter = classUnderTest.retrieveTreeNodeParameter(treeNodeParameters,
				SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertNotNull(csticParameter);
		assertEquals(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC, csticParameter.getId());
	}

	@Test
	public void testRetrieveTreeNodeParameterNotExists()
	{
		final ParameterModel csticParameter = classUnderTest.retrieveTreeNodeParameter(treeNodeParameters, "XXX");
		assertNull(csticParameter);
	}

	@Test
	public void testRetrieveParameterValue()
	{
		final Serializable parameterValue = classUnderTest.retrieveParameterValue(csticValueParameter);
		assertNotNull(parameterValue);
		assertEquals(CSTIC_VALUE_PARAMETER_VALUE, parameterValue);
	}

	@Test
	public void testRetrieveParameterValueNotExist()
	{
		Serializable parameterValue = classUnderTest.retrieveParameterValue(new ParameterModel());
		assertNull(parameterValue);

		parameterValue = classUnderTest.retrieveParameterValue(null);
		assertNull(parameterValue);
	}

	@Test
	public void testAddCustomAttributeToParameter()
	{
		classUnderTest.addCustomAttributeToParameter(csticValueParameter,
				SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT, productParameterValue);
		assertEquals(productParameterValue, csticValueParameter.getCustomAttributes()
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT));
	}

	@Test
	public void testAddCustomAttributeToParameterWithoutCutomAttribues()
	{
		csticValueParameter.setCustomAttributes(new HashMap<>());
		classUnderTest.addCustomAttributeToParameter(csticValueParameter,
				SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT, productParameterValue);
		assertEquals(productParameterValue, csticValueParameter.getCustomAttributes()
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT));
	}

	@Test
	public void testAddCustomAttributeToParameterNotExist()
	{
		final ParameterModel parameter = null;
		classUnderTest.addCustomAttributeToParameter(parameter,
				SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT, productParameterValue);
		assertNull(parameter);
	}


	@Test
	public void testAdjustCsticValueParameterMasterParameterProduct()
	{
		final ParameterModel masterParameter = new ParameterModel();
		final ProductModel masterProductParameterValue = new ProductModel();
		masterProductParameterValue.setCode("MASTER_PRODUCT_CODE");
		masterParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
		masterParameter.setValue(masterProductParameterValue);

		classUnderTest.adjustCsticValueParameter(masterParameter, treeNodeParameters);
		final ParameterModel csticValueParameterRetrieved = classUnderTest.retrieveTreeNodeParameter(treeNodeParameters,
				SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		final Serializable attribute = csticValueParameterRetrieved.getCustomAttributes()
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT);

		assertNotNull(attribute);
		assertEquals("MASTER_PRODUCT_CODE", attribute);
	}

	@Test
	public void testAdjustCsticValueParameterMasterParameterCstic()
	{
		final ParameterModel masterParameter = new ParameterModel();
		masterParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);

		classUnderTest.adjustCsticValueParameter(masterParameter, treeNodeParameters);
		final ParameterModel csticValueParameterRetrieved = classUnderTest.retrieveTreeNodeParameter(treeNodeParameters,
				SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		final Serializable attribute = csticValueParameterRetrieved.getCustomAttributes()
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT);

		assertNotNull(attribute);
		assertEquals(PRODUCT_CODE, attribute);

	}

	@Test
	public void testClearDependentParameterValuesForParameterProduct()
	{
		final ConditionModel condition = createConditionModel();
		final TreeNodeModel<ConditionModel> treeNode = new TreeNodeModel<>();
		treeNode.setData(condition);

		classUnderTest.clearDependentParameterValues(treeNode, SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);

		final ParameterModel csticParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertTrue(((String) (csticParameter.getValue())).isEmpty());

		final ParameterModel csticValueParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertTrue(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testClearDependentParameterValuesForParameterCstic()
	{
		final ConditionModel condition = createConditionModel();
		final TreeNodeModel<ConditionModel> treeNode = new TreeNodeModel<>();
		treeNode.setData(condition);

		classUnderTest.clearDependentParameterValues(treeNode, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);

		final ParameterModel csticParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertFalse(((String) (csticParameter.getValue())).isEmpty());

		final ParameterModel csticValueParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertTrue(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testClearParameterValue()
	{

		final ConditionModel condition = createConditionModel();
		classUnderTest.clearParameterValue(condition, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);

		final ParameterModel csticValueParameter = condition.getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertTrue(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testGetProductList()
	{
		final TreeListModel<ConditionModel> conditionModelList = new TreeListModel<>();
		conditionModelList.add(createTreeNodeModel(PRODUCT_CODE,
				SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT));
		conditionModelList
				.add(createTreeNodeModel("4711", SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_CSTIC));

		final List<String> products = classUnderTest.getProductList(conditionModelList);
		assertEquals(1, products.size());
		assertEquals(PRODUCT_CODE, products.get(0));
	}

	@Test
	public void testGetProductModel()
	{
		final String product = classUnderTest.getProductCode(createConditionModel().getParameters());
		assertNotNull(product);
		assertEquals(PRODUCT_CODE, product);
	}

	@Test
	public void testGetProductModelWithoutProductParameter()
	{
		final String product = classUnderTest.getProductCode(new HashMap<>());
		assertNull(product);
	}

	protected TreeNodeModel<ConditionModel> createTreeNodeModel(final String productCode, final String ruleTypeId)
	{

		final ConditionModel conditionModel = createConditionModel();
		final RuleConditionDefinitionData conditionDefinition = new RuleConditionDefinitionData();
		final TreeNodeModel<ConditionModel> conditionModelNode = new TreeNodeModel<>();

		final ParameterModel productParameter = new ParameterModel();
		productParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
		final ProductModel productParameterValue = new ProductModel();
		productParameterValue.setCode(productCode);
		productParameter.setValue(productParameterValue);
		conditionModel.getParameters().put(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT, productParameter);
		conditionDefinition.setId(ruleTypeId);
		conditionModel.setConditionDefinition(conditionDefinition);
		conditionModelNode.setData(conditionModel);

		return conditionModelNode;
	}

	protected ConditionModel createConditionModel()
	{
		final ConditionModel condition = new ConditionModel();
		final Map<String, ParameterModel> parameters = new HashMap<>();
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT, productParameter);
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC, csticParameter);
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE, csticValueParameter);
		condition.setParameters(parameters);
		return condition;
	}

}
