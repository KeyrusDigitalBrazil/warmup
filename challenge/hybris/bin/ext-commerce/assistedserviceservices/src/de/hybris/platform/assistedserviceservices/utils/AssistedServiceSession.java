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
package de.hybris.platform.assistedserviceservices.utils;

import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;


/**
 * Class represents Assisted Service emulation parameters.
 */
public class AssistedServiceSession implements Serializable
{
	private volatile String flashErrorMessage = null;
	private volatile String flashErrorMessageArgs = null;
	private volatile String forwardUrl = null;
	private volatile PK currentAgentPk = null;
	private volatile PK currentEmulatedCustomerPk = null;
	private volatile CustomerEmulationParams customerEmulationParams = null;
	private Collection<? extends GrantedAuthority> initialAuthorities = null; //NOSONAR
	private transient volatile ModelService cachedModelService = null;

	public Map<String, Object> getAsmSessionParametersMap()
	{
		final HashMap<String, Object> asmSessionParams = new HashMap<>();
		asmSessionParams.put(AssistedserviceservicesConstants.AGENT, getAgent());
		asmSessionParams.put(AssistedserviceservicesConstants.EMULATED_CUSTOMER, getEmulatedCustomer());
		return Collections.unmodifiableMap(asmSessionParams);
	}

	public UserModel getAgent()
	{
		return getAgentPk() == null ? null : getModelService().get(getAgentPk());
	}

	public PK getAgentPk()
	{
		return this.currentAgentPk;
	}

	public void setAgent(final UserModel agent)
	{
		this.currentAgentPk = (agent == null) ? null : agent.getPk();
	}

	public void setForwardUrl(final String fwd)
	{
		this.forwardUrl = fwd;
	}

	public String getForwardUrl()
	{
		return this.forwardUrl;
	}

	public UserModel getEmulatedCustomer()
	{
		return getEmulatedCustomerPk() == null ? null : getModelService().get(getEmulatedCustomerPk());
	}

	public PK getEmulatedCustomerPk()
	{
		return this.currentEmulatedCustomerPk;
	}

	public void setEmulatedCustomer(final UserModel emulatedCustomer)
	{
		this.currentEmulatedCustomerPk = (emulatedCustomer == null) ? null : emulatedCustomer.getPk();
	}

	public void setSavedEmulationData(final CustomerEmulationParams emulationParams)
	{
		this.customerEmulationParams = emulationParams;
	}

	public CustomerEmulationParams getSavedEmulationData()
	{
		return this.customerEmulationParams;
	}

	public void setInitialAgentAuthorities(final Collection<? extends GrantedAuthority> authorities)
	{
		this.initialAuthorities = Collections.unmodifiableCollection(authorities);
	}

	public Collection<? extends GrantedAuthority> getInitialAgentAuthorities()
	{
		return this.initialAuthorities;
	}

	public void setFlashErrorMessage(final String flashErrorMessage)
	{
		this.flashErrorMessage = flashErrorMessage;
	}

	public String getFlashErrorMessage()
	{
		final String flashErrorMessage = this.flashErrorMessage == null ? null : new String(this.flashErrorMessage); //NOSONAR
		this.flashErrorMessage = null;
		return flashErrorMessage;
	}

	/**
	 * @return the flashErrorMessageArgs
	 */
	public String getFlashErrorMessageArgs()
	{
		final String flashErrorMessageArgs = this.flashErrorMessageArgs == null ? null : new String(this.flashErrorMessageArgs); //NOSONAR
		this.flashErrorMessageArgs = null;
		return flashErrorMessageArgs;
	}

	/**
	 * @param flashErrorMessageArgs
	 *           the flashErrorMessageArgs to set
	 */
	public void setFlashErrorMessageArgs(final String flashErrorMessageArgs)
	{
		this.flashErrorMessageArgs = flashErrorMessageArgs;
	}

	protected ModelService getModelService()
	{
		if (cachedModelService == null)
		{
			cachedModelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);
		}
		return cachedModelService;
	}
}