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
package de.hybris.platform.sap.productconfig.frontend.renderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.sap.productconfig.frontend.jalo.GeneratedProductConfigurationPriceSummaryComponent;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;

import java.util.Map;

import javax.servlet.jsp.PageContext;


/**
 * This Renderer for the PriceSummaryComponent checks whether the settings required to display the price deatials (BASE
 * and OPTIONS price) are maintained in backoffice. If this is not the case hidding of the price details is enforced,
 * even if display is a ctivated in WCMS cockpit, because meaningfull price details can't be determined without the
 * backofice settings.
 */
public class PriceSummaryComponentRenderer extends DefaultAddOnCMSComponentRenderer<AbstractCMSComponentModel>
{

	private ProviderFactory providerFactory;

	protected PricingConfigurationParameter getPricingParameters()
	{
		return this.getProviderFactory().getPricingParameter();
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	/**
	 * @param providerFactory
	 *           provider factory to get the pricing parameter configuration from backoffice
	 */
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final AbstractCMSComponentModel component)
	{
		final Map<String, Object> vars = super.getVariablesToExpose(pageContext, component);
		if (isNeitherBaseNorOptionPriceAvailable())
		{
			vars.put(GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS, Boolean.FALSE);
		}
		return vars;
	}

	protected boolean isNeitherBaseNorOptionPriceAvailable()
	{
		return !(getPricingParameters().showBasePriceAndSelectedOptions());
	}

}
