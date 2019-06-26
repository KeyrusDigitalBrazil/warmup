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
package de.hybris.platform.secureportaladdon.interceptors;

import de.hybris.platform.addonsupport.valueprovider.AddOnValueProvider;
import de.hybris.platform.addonsupport.valueprovider.AddOnValueProviderRegistry;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;


public class AsmRequestProcessor implements SecurePortalRequestProcessor
{
	private String assistedServiceAddOnName;
	private String agentLoggedInKey;
	private String asmRequestParameter;
	private String quitAsmRequestUri;
	private SessionService sessionService;
	private AddOnValueProviderRegistry addOnValueProviderRegistry;


	public String getOtherRequestParameters(final HttpServletRequest request)
	{
		String assistedServiceModeRequested = request.getParameter(getAsmRequestParameter());
		if (request.getRequestURI().endsWith(getQuitAsmRequestUri()))
		{
			assistedServiceModeRequested = "false";
		}

		return assistedServiceModeRequested == null ? null : getAsmRequestParameter() + "=" + assistedServiceModeRequested;
	}

	public boolean skipSecureCheck()
	{
		final Optional<AddOnValueProvider> optionalValueProvider = getAddOnValueProviderRegistry().get(
				getAssistedServiceAddOnName());

		if (optionalValueProvider.isPresent())
		{
			final Optional<Boolean> value = optionalValueProvider.get().getValue(getAgentLoggedInKey(), Boolean.class);
			return value.isPresent() ? value.get().booleanValue() : false;
		}

		return false;
	}

	protected String getAssistedServiceAddOnName()
	{
		return assistedServiceAddOnName;
	}

	@Required
	public void setAssistedServiceAddOnName(final String assistedServiceAddOnName)
	{
		this.assistedServiceAddOnName = assistedServiceAddOnName;
	}

	protected String getAgentLoggedInKey()
	{
		return agentLoggedInKey;
	}

	@Required
	public void setAgentLoggedInKey(final String agentLoggedInKey)
	{
		this.agentLoggedInKey = agentLoggedInKey;
	}

	protected String getAsmRequestParameter()
	{
		return asmRequestParameter;
	}

	@Required
	public void setAsmRequestParameter(final String asmRequestParameter)
	{
		this.asmRequestParameter = asmRequestParameter;
	}

	protected String getQuitAsmRequestUri()
	{
		return quitAsmRequestUri;
	}

	@Required
	public void setQuitAsmRequestUri(final String quitAsmRequestUri)
	{
		this.quitAsmRequestUri = quitAsmRequestUri;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected AddOnValueProviderRegistry getAddOnValueProviderRegistry()
	{
		return addOnValueProviderRegistry;
	}

	@Required
	public void setAddOnValueProviderRegistry(final AddOnValueProviderRegistry addOnValueProviderRegistry)
	{
		this.addOnValueProviderRegistry = addOnValueProviderRegistry;
	}
}
