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
package de.hybris.platform.acceleratorcms.component.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * GenericViewCMSComponentRenderer.
 *
 * Generic CMS component renderer. Looks for a controller bean named with the typecode of the CMS component (with suffix
 * 'Controller'), if a controller exists with that name then the component is rendered by a server side include to
 * '/view/[controllerName]'. If there is no controller defined then the default include is
 * '/view/DefaultCMSComponentController'.
 */
public class GenericViewCMSComponentRenderer implements CMSComponentRenderer<AbstractCMSComponentModel>, BeanFactoryAware
{
	public static final String DEFAULT_CONTROLLER = "DefaultCMSComponentController";

	private static final Logger LOG = Logger.getLogger(GenericViewCMSComponentRenderer.class);

	private static final String COMPONENT_ATTR = "component";

	private BeanFactory beanFactory;

	protected BeanFactory getBeanFactory()
	{
		return beanFactory;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;
	}

	@Override
	public void renderComponent(final PageContext pageContext, final AbstractCMSComponentModel component)
			throws ServletException, IOException
	{

		final Object existingComponentInRequest = pageContext.getAttribute(COMPONENT_ATTR, PageContext.REQUEST_SCOPE);

		pageContext.setAttribute(COMPONENT_ATTR, component, PageContext.REQUEST_SCOPE);

		try
		{
			final String itemType = component.getItemtype();
			String controllerName = itemType + "Controller";
			if (!getBeanFactory().containsBean(controllerName))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("No controller defined for ContentElement [" + itemType + "]. Using default Controller");
				}
				controllerName = DEFAULT_CONTROLLER;
			}

			final String includePath = "/view/" + controllerName;

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Rendering CMS Component type [" + itemType + "] uid [" + component.getUid() + "], include path ["
						+ includePath + "]");
			}
			renderView(pageContext, component, includePath);
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

	protected void renderView(final PageContext pageContext, final AbstractCMSComponentModel component, final String includePath)
			throws ServletException, IOException // NOSONAR
	{
		pageContext.include(includePath);
	}
}
