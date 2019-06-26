/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.addon.component.renderer;

import java.util.Map;

import javax.servlet.jsp.PageContext;

import com.hybris.merchandising.constants.MerchandisingaddonConstants;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;


/**
 * MerchandisingComponentRenderer is a custom component renderer for Merch v2 CMS components.
 * This is intended to allow us to expose additional values to the page if required (e.g.
 * the component ID).
 *
 * @param <C> a Component which extends {@code AbstractCMSComponentModel}.
 */
public class MerchandisingComponentRenderer<C extends AbstractCMSComponentModel> extends DefaultAddOnCMSComponentRenderer<C> {
	public static final String COMPONENT_ID = "componentID";
	public static final String SERVICE_URL = "serviceUrl";

	private ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final ConsumedDestinationModel model = consumedDestinationLocatorStrategy.lookup(MerchandisingaddonConstants.STRATEGY_SERVICE);
		final Map<String, Object> variables = super.getVariablesToExpose(pageContext, component);
		model.getDestinationTarget().getDestinations()
									.stream()
									.filter(dest -> dest.getEndpoint().getId().equals(MerchandisingaddonConstants.STRATEGY_SERVICE))
									.forEach(dest -> {
										variables.put(COMPONENT_ID, component.getUid());
										variables.put(SERVICE_URL, dest.getUrl());
									});
		return variables;
	}

	/**
	 * Sets the injected {@link ConsumedDestinationLocatorStrategy}.
	 * @param consumedDestinationLocatorStrategy the locator strategy to inject.
	 */
	public void setConsumedDestinationLocatorStrategy(
			ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy) {
		this.consumedDestinationLocatorStrategy = consumedDestinationLocatorStrategy;
	}
}
