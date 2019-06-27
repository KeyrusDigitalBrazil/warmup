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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.rulebuilderbackoffice.editors.EditorException;
import de.hybris.platform.rulebuilderbackoffice.editors.ParameterModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeListModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeNodeModel;
import de.hybris.platform.rulebuilderbackoffice.editors.conditionseditor.ConditionModel;
import de.hybris.platform.rulebuilderbackoffice.editors.conditionseditor.ConditionsEditorViewModel;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;


/**
 * Implementation of the product configuration specific logic in the condition editor view model
 */
@Init(superclass = true)
public class ProductconfigConditionsEditorViewModel extends ConditionsEditorViewModel
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init(final Execution execution, final Component component)
	{
		super.init(execution, component);
		EventQueues.lookup("productChange", EventQueues.DESKTOP, true);
	}

	@Override
	protected List<ParameterModel> updateDependentParameters(final ParameterModel masterParameter,
			final Collection<ParameterModel> treeNodeParameters)
	{
		final List<ParameterModel> dependentParameters = super.updateDependentParameters(masterParameter, treeNodeParameters);
		adjustCsticValueParameter(masterParameter, treeNodeParameters);
		return dependentParameters;
	}

	@Override
	@Command
	@NotifyChange(CONDITION_BREADCRUMBS)
	public void changeTreeNodeParameter(@BindingParam("treeNode") final TreeNodeModel<ConditionModel> treeNode,
			@BindingParam("parameterId") final String parameterId, @BindingParam("parameterValue") final Serializable parameterValue)
					throws EditorException
	{
		clearDependentParameterValues(treeNode, parameterId);
		super.changeTreeNodeParameter(treeNode, parameterId, parameterValue);
	}

	@Override
	@Command
	@NotifyChange(CONDITION_BREADCRUMBS)
	public void removeCondition(@BindingParam("treeNode") final TreeNodeModel<ConditionModel> treeNode) throws EditorException
	{
		final String id = treeNode.getData().getConditionDefinition().getId();
		super.removeCondition(treeNode);

		if (getConditions() != null
				&& SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT.equalsIgnoreCase(id))
		{
			triggerActionUpdate();
		}
	}

	protected void adjustCsticValueParameter(final ParameterModel masterParameter,
			final Collection<ParameterModel> treeNodeParameters)
	{
		Serializable product = null;
		boolean valueParameterUpdateRequired = false;
		if (masterParameter.getId().equalsIgnoreCase(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT))
		{
			valueParameterUpdateRequired = true;
			product = masterParameter.getValue();
			if (getConditions() != null)
			{
				triggerActionUpdate();
			}
		}
		else if (masterParameter.getId().equalsIgnoreCase(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC))
		{
			valueParameterUpdateRequired = true;
			final ParameterModel productParameter = retrieveTreeNodeParameter(treeNodeParameters,
					SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
			product = retrieveParameterValue(productParameter);
		}

		if (valueParameterUpdateRequired)
		{
			final ParameterModel csticValueParameter = retrieveTreeNodeParameter(treeNodeParameters,
					SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);

			String productCode = null;
			if (product == null)
			{
				addCustomAttributeToParameter(csticValueParameter, SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_CSTIC, null);
			}
			else
			{
				productCode = ((ProductModel) product).getCode();
			}

			addCustomAttributeToParameter(csticValueParameter, SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT, productCode);
		}
	}

	protected ParameterModel retrieveTreeNodeParameter(final Collection<ParameterModel> treeNodeParameters,
			final String parameterId)
	{
		ParameterModel parameter = null;
		for (final ParameterModel pm : treeNodeParameters)
		{
			if (pm.getId().equalsIgnoreCase(parameterId))
			{
				parameter = pm;
				break;
			}
		}
		return parameter;
	}

	protected Serializable retrieveParameterValue(final ParameterModel parameter)
	{
		if (parameter != null)
		{
			return parameter.getValue();
		}
		return null;
	}

	protected void addCustomAttributeToParameter(final ParameterModel parameter, final String attributeId,
			final Serializable attributeValue)
	{
		if (parameter != null)
		{
			Map<String, Serializable> ca = parameter.getCustomAttributes();
			if (ca == null)
			{
				ca = new HashMap<>();
				parameter.setCustomAttributes(ca);
			}
			ca.put(attributeId, attributeValue);
		}
	}

	protected void clearDependentParameterValues(final TreeNodeModel<ConditionModel> treeNode, final String parameterId)
	{
		final ConditionModel condition = treeNode.getData();

		if (parameterId.equalsIgnoreCase(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT))
		{
			clearParameterValue(condition, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC);
			clearParameterValue(condition, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		}
		else if (parameterId.equalsIgnoreCase(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC))
		{
			clearParameterValue(condition, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		}
	}

	protected void clearParameterValue(final ConditionModel condition, final String parameterId)
	{
		final ParameterModel parameter = condition.getParameters().get(parameterId);
		if (parameter != null)
		{
			parameter.setValue("");
		}
	}

	protected void triggerActionUpdate()
	{
		final List<String> productList = getProductList(getConditions().getChildren());
		EventQueues.lookup("productChange", EventQueues.DESKTOP, true).publish(new Event("productChanged", null, productList));
	}

	protected List<String> getProductList(final TreeListModel<ConditionModel> conditionModelList)
	{
		final List<String> products = new ArrayList<>();

		for (int i = 0; i < conditionModelList.size(); i++)
		{
			final ConditionModel condtitionModel = conditionModelList.get(i).getData();
			final String id = condtitionModel.getConditionDefinition().getId();

			if (SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT.equalsIgnoreCase(id))
			{
				final String productCode = getProductCode(condtitionModel.getParameters());
				if (productCode != null && !products.contains(productCode))
				{
					products.add(productCode);
				}
			}
		}

		return products;
	}

	protected String getProductCode(final Map<String, ParameterModel> parameters)
	{
		final ParameterModel productParameter = parameters.get(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
		if (productParameter != null)
		{
			final Serializable productModel = productParameter.getValue();
			if (productModel instanceof ProductModel)
			{
				return ((ProductModel) productModel).getCode();
			}
		}
		return null;
	}
}
