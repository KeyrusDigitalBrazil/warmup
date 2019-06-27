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

import de.hybris.platform.rulebuilderbackoffice.editors.EditorException;
import de.hybris.platform.rulebuilderbackoffice.editors.ParameterModel;
import de.hybris.platform.rulebuilderbackoffice.editors.TreeNodeModel;
import de.hybris.platform.rulebuilderbackoffice.editors.actionseditor.ActionModel;
import de.hybris.platform.rulebuilderbackoffice.editors.actionseditor.ActionsEditorViewModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;


/**
 * Implementation of the product configuration specific logic in the action editor view model
 */
@Init(superclass = true)
public class ProductconfigActionsEditorViewModel extends ActionsEditorViewModel implements EventListener<Event>
{
	private static final long serialVersionUID = 1L;

	private List<String> productCodeList;

	@Override
	public void init(@ContextParam(ContextType.EXECUTION) final Execution execution,
			@ContextParam(ContextType.COMPONENT) final Component component)
	{
		final Map<?, ?> args = execution.getArg();
		productCodeList = (List<String>) args.get(SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST);

		super.init(execution, component);
		final EventQueue<Event> queue = EventQueues.lookup("productChange", EventQueues.DESKTOP, true);

		if (!queue.isSubscribed(this))
		{
			queue.subscribe(this);
		}
	}

	@Override
	public void onEvent(final Event event) throws Exception
	{
		if (this.getActions().getChildren() != null)
		{
			final TreeNodeModel<ActionModel> parentNode = this.getActions();
			this.productCodeList = (List<String>) event.getData();

			final List<TreeNodeModel<ActionModel>> childrenList = new ArrayList<>();

			for (final TreeNodeModel<ActionModel> child : parentNode.getChildren())
			{
				addProductCodeList(child.getData().getParameters());
				childrenList.add(child);
			}

			for (final TreeNodeModel<ActionModel> child : childrenList)
			{
				parentNode.removeChild(child);
				parentNode.addChild(child);
			}
		}
	}

	@Override
	protected Map<String, ParameterModel> convertRuleParametersToParameters(final Map<String, RuleParameterData> ruleParameters,
			final Map<String, RuleParameterDefinitionData> ruleParameterDefinitions) throws EditorException
	{
		final Map<String, ParameterModel> parameterMap = super.convertRuleParametersToParameters(ruleParameters,
				ruleParameterDefinitions);
		addProductCodeList(parameterMap);
		return parameterMap;
	}

	protected void addProductCodeList(final Map<String, ParameterModel> parameterMap)
	{
		for (final Map.Entry<String, ParameterModel> entry : parameterMap.entrySet())
		{
			if (SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC.equalsIgnoreCase(entry.getKey())
					|| SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE.equalsIgnoreCase(entry.getKey()))
			{
				final ParameterModel parameterModel = entry.getValue();
				Map<String, Serializable> ca = parameterModel.getCustomAttributes();
				if (ca == null)
				{
					ca = new HashMap<>();
					parameterModel.setCustomAttributes(ca);
				}
				ca.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST,
						(Serializable) productCodeList);
			}
		}
	}

	@Override
	@Command
	@NotifyChange(ACTION_BREADCRUMBS)
	public void changeTreeNodeParameter(@BindingParam("treeNode") final TreeNodeModel<ActionModel> treeNode,
			@BindingParam("parameterId") final String parameterId, @BindingParam("parameterValue") final Serializable parameterValue)
					throws EditorException
	{
		clearDependentParameterValues(treeNode, parameterId);
		super.changeTreeNodeParameter(treeNode, parameterId, parameterValue);
	}

	protected void clearDependentParameterValues(final TreeNodeModel<ActionModel> treeNode, final String parameterId)
	{
		final ActionModel action = treeNode.getData();
		if (parameterId.equalsIgnoreCase(SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC))
		{
			clearParameterValue(action, SapproductconfigrulesbackofficeConstants.PARAMETER_CSTIC_VALUE);
		}
	}

	protected void clearParameterValue(final ActionModel action, final String parameterId)
	{
		final ParameterModel parameter = action.getParameters().get(parameterId);
		if (parameter != null)
		{
			parameter.setValue("");
		}
	}

	/**
	 * @return the productCodeList
	 */
	public List<String> getProductCodeList()
	{
		return Optional.ofNullable(productCodeList).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	/**
	 * @param productCodeList
	 *           the productCodeList to set
	 */
	public void setProductCodeList(final List<String> productCodeList)
	{
		this.productCodeList = Optional.ofNullable(productCodeList).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}
}
