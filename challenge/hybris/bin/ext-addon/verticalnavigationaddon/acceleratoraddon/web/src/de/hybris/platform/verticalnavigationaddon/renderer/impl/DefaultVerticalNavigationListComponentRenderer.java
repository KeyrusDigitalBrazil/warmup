/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.verticalnavigationaddon.renderer.impl;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.verticalnavigationaddon.constants.VerticalnavigationaddonConstants;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;


/**
 * When product is out of stock, stock notification should be displayed instead of existing AddToCartAction. This
 * renderer is used to redefine AddToCartAction with stock notification function.
 */
public class DefaultVerticalNavigationListComponentRenderer<T extends AbstractCMSComponentModel> extends
		DefaultAddOnCMSComponentRenderer<T>
{
	private static final String COMPONENT_ATTR = "component";

	@Override
	public void renderComponent(final PageContext pageContext, final T component) throws ServletException, IOException
	{
		final Object existingComponentInRequest = pageContext.getAttribute(COMPONENT_ATTR, PageContext.REQUEST_SCOPE);

		pageContext.setAttribute(COMPONENT_ATTR, component, PageContext.REQUEST_SCOPE);

		try
		{
			super.renderComponent(pageContext, component);
		}
		finally
		{
			pageContext.removeAttribute(COMPONENT_ATTR, PageContext.REQUEST_SCOPE);
			if (existingComponentInRequest != null)
			{
				pageContext.setAttribute(COMPONENT_ATTR, existingComponentInRequest, PageContext.REQUEST_SCOPE);

			}
		}
	}

	@Override
	protected String getAddonUiExtensionName(final T component)
	{
		return VerticalnavigationaddonConstants.EXTENSIONNAME;
	}
}
