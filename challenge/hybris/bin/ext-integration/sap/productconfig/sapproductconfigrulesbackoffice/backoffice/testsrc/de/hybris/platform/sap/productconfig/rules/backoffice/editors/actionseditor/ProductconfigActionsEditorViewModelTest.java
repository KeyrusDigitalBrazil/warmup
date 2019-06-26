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
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.rulebuilderbackoffice.editors.ParameterModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeNodeModel;
import de.hybris.platform.rulebuilderbackoffice.editors.actionseditor.ActionModel;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.Serializable;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;


@UnitTest
public class ProductconfigActionsEditorViewModelTest
{
	@Mock
	private SessionService sessionService;

	@InjectMocks
	private final ProductconfigActionsEditorViewModel classUnderTest = new ProductconfigActionsEditorViewModel();

	private ParameterModel csticParameter;
	private ParameterModel csticValueParameter;
	private ParameterModel dummyParameter;

	private static final String CSTIC_PARAMETER_VALUE = "CSTIC_PARAMETER_VALUE";
	private static final String CSTIC_VALUE_PARAMETER_VALUE = "CSTIC_VALUE_PARAMETER_VALUE";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		csticParameter = new ParameterModel();
		csticParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		csticParameter.setValue(CSTIC_PARAMETER_VALUE);

		csticValueParameter = new ParameterModel();
		csticValueParameter.setId(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		csticValueParameter.setValue(CSTIC_VALUE_PARAMETER_VALUE);

		dummyParameter = new ParameterModel();
		dummyParameter.setId("DUMMY");
	}

	@Test
	public void testClearDependentParameterValuesForParameterCstic()
	{
		final ActionModel action = createActionModel();
		final TreeNodeModel<ActionModel> treeNode = new TreeNodeModel<>();
		treeNode.setData(action);

		classUnderTest.clearDependentParameterValues(treeNode, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);

		final ParameterModel csticParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertFalse(((String) (csticParameter.getValue())).isEmpty());

		final ParameterModel csticValueParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertTrue(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testClearDependentParameterValuesForNonParameterCstic()
	{
		final ActionModel action = createActionModel();
		final TreeNodeModel<ActionModel> treeNode = new TreeNodeModel<>();
		treeNode.setData(action);

		classUnderTest.clearDependentParameterValues(treeNode, SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);

		final ParameterModel csticParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertFalse(((String) (csticParameter.getValue())).isEmpty());

		final ParameterModel csticValueParameter = treeNode.getData().getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertFalse(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testClearParameterValue()
	{
		final ActionModel action = createActionModel();
		classUnderTest.clearParameterValue(action, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);

		final ParameterModel csticValueParameter = action.getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertTrue(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testClearParameterValueWrongParameterId()
	{
		final ActionModel action = createActionModel();
		classUnderTest.clearParameterValue(action, SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);

		final ParameterModel csticValueParameter = action.getParameters()
				.get(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertFalse(((String) (csticValueParameter.getValue())).isEmpty());
	}

	@Test
	public void testAddProductCodeList()
	{
		final Map<String, ParameterModel> parameterMap = new HashMap<>();

		parameterMap.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC, csticParameter);
		parameterMap.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE, csticValueParameter);
		parameterMap.put("DUMMY_PARAMETER", dummyParameter);

		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add("PRODUCT_1");
		productCodeList.add("PRODUCT_2");

		classUnderTest.setProductCodeList(productCodeList);

		classUnderTest.addProductCodeList(parameterMap);

		final List<String> productCodeListForCstic = retriveProductCodeListForParameter(parameterMap,
				SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
		assertEquals(2, productCodeListForCstic.size());
		assertEquals("PRODUCT_1", productCodeListForCstic.get(0));
		assertEquals("PRODUCT_2", productCodeListForCstic.get(1));

		final List<String> productCodeListForCsticValue = retriveProductCodeListForParameter(parameterMap,
				SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		assertEquals(2, productCodeListForCsticValue.size());
		assertEquals("PRODUCT_1", productCodeListForCsticValue.get(0));
		assertEquals("PRODUCT_2", productCodeListForCsticValue.get(1));

		final List<String> productCodeListForDummy = retriveProductCodeListForParameter(parameterMap, "DUMMY_PARAMETER");
		assertEquals(0, productCodeListForDummy.size());

		final List<String> resultProductCodeList1 = classUnderTest.getProductCodeList();
		assertEquals(2, resultProductCodeList1.size());
	}

	@Test
	public void testInit()
	{
		final Execution execution = Mockito.mock(Execution.class);
		final Desktop desktop = Mockito.mock(Desktop.class);
		final Component component = Mockito.mock(Component.class);
		final ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
		final ComponentDefinition componentDefinition = Mockito.mock(ComponentDefinition.class);

		ExecutionsCtrl.setCurrent(execution);
		ComponentsCtrl.setCurrentInfo(componentInfo);

		given(execution.getDesktop()).willReturn(desktop);
		given(componentInfo.getComponentDefinition()).willReturn(componentDefinition);
		classUnderTest.init(execution, component);
	}

	private List<String> retriveProductCodeListForParameter(final Map<String, ParameterModel> parameterMap, final String parameter)
	{
		List<String> productCodeList = new ArrayList<>();
		final ParameterModel parameterModel = parameterMap.get(parameter);
		final Map<String, Serializable> ca = parameterModel.getCustomAttributes();
		if (ca != null)
		{
			productCodeList = (List<String>) ca
					.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST);
		}
		return productCodeList;
	}

	private ActionModel createActionModel()
	{
		final ActionModel action = new ActionModel();
		final Map<String, ParameterModel> parameters = new HashMap<>();
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC, csticParameter);
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE, csticValueParameter);
		action.setParameters(parameters);
		return action;
	}

}
