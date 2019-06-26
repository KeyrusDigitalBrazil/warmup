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

import de.hybris.platform.rulebuilderbackoffice.editors.actionseditor.ActionsEditorSectionRenderer;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.List;
import java.util.Map;

import com.hybris.cockpitng.engine.WidgetInstanceManager;


public class ProductconfigActionsEditorSectionRenderer extends ActionsEditorSectionRenderer
{
	protected static final String PRODUCTCONFIG_ACTIONS_EDITOR_ID = "actionsEditor";

	private ProductconfigProductCodeExtractor productCodeExtractor;

	@Override
	public String getEditorId()
	{
		return PRODUCTCONFIG_ACTIONS_EDITOR_ID;
	}

	@Override
	protected void fillParameters(final Object model, final WidgetInstanceManager widgetInstanceManager,
			final Map<Object, Object> parameters)
	{
		super.fillParameters(model, widgetInstanceManager, parameters);
		addProductCodeListToParameters(model, parameters);
	}

	protected void addProductCodeListToParameters(final Object model, final Map<Object, Object> parameters)
	{
		if (model instanceof ProductConfigSourceRuleModel)
		{
			final List<String> productCodeList = productCodeExtractor.retrieveProductCodeList((ProductConfigSourceRuleModel) model);
			parameters.put(SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST, productCodeList);
		}
	}

	/**
	 * @return the productCodeExtractor
	 */
	protected ProductconfigProductCodeExtractor getProductCodeExtractor()
	{
		return productCodeExtractor;
	}

	/**
	 * @param productCodeExtractor
	 *           the productCodeExtractor to set
	 */
	public void setProductCodeExtractor(final ProductconfigProductCodeExtractor productCodeExtractor)
	{
		this.productCodeExtractor = productCodeExtractor;
	}
}
