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
package de.hybris.platform.assistedservicestorefront.component.renderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedservicestorefront.constants.AssistedservicestorefrontConstants;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

public class AssistedServiceComponentRenderer<C extends AbstractCMSComponentModel> extends DefaultAddOnCMSComponentRenderer<C>
{

	private final static Logger LOG = Logger.getLogger(AssistedServiceComponentRenderer.class); //NOSONAR
	private AssistedServiceFacade assistedServiceFacade;

	@Override
	public void renderComponent(final PageContext pageContext, final C component) throws ServletException, IOException
	{
		final String asmRequestParam = pageContext.getRequest().getParameter(AssistedservicestorefrontConstants.ASM_REQUEST_PARAM);
		boolean asmSessionStatus = getAssistedServiceFacade().isAssistedServiceModeLaunched();

		// Check for "asm" parameter in HTTP request
		if (asmRequestParam != null)
		{
			// change behavior only when it's 'true' or 'false' as a value
			if (asmRequestParam.equalsIgnoreCase(Boolean.TRUE.toString()))
			{
				asmSessionStatus = true;
				if (!getAssistedServiceFacade().isAssistedServiceModeLaunched())
				{
					getAssistedServiceFacade().launchAssistedServiceMode();
				}
			}
			else if (asmRequestParam.equalsIgnoreCase(Boolean.FALSE.toString()))
			{
				getAssistedServiceFacade().quitAssistedServiceMode();
				asmSessionStatus = false;
			}
		}

		// render component only when it's necessary
		if (asmSessionStatus || getAssistedServiceFacade().isAssistedServiceAgentLoggedIn())
		{

			final String asmModuleView = "/WEB-INF/views/addons/" + getAddonUiExtensionName(component) + "/"
					+ getUIExperienceFolder() + "/cms/asm/assistedServiceComponent.jsp";
			final Map<String, Object> exposedVariables = exposeVariables(pageContext, component);
			pageContext.include(asmModuleView);
			exposedVariables.remove(AssistedserviceservicesConstants.AGENT); // agent can be used for other jsp\tags
			unExposeVariables(pageContext, component, exposedVariables);
		}
	}

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> exposedVariables = super.getVariablesToExpose(pageContext, component);
		exposedVariables.putAll(getAssistedServiceFacade().getAssistedServiceSessionAttributes());
		return exposedVariables;
	}


	protected void handleException(final Throwable throwable, final C component)
	{
		LOG.warn("Error processing component tag. currentComponent [" + component + "] exception: " + throwable.getMessage());
	}

	/**
	 * @return the assistedServiceFacade
	 */
	public AssistedServiceFacade getAssistedServiceFacade()
	{
		return assistedServiceFacade;
	}

	/**
	 * @param assistedServiceFacade
	 *           the assistedServiceFacade to set
	 */
	@Required
	public void setAssistedServiceFacade(final AssistedServiceFacade assistedServiceFacade)
	{
		this.assistedServiceFacade = assistedServiceFacade;
	}

}