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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;

import org.springframework.validation.BindingResult;


/**
 * Simple POJO to be used as internal interface for methods within the {@link UpdateConfigureProductController}.
 */
public class UpdateDataHolder
{
	private ConfigurationData configData;
	private BindingResult bindingResult;
	private UiStatus uiStatus;
	private long startTime;

	/**
	 * @return actual configuration
	 */
	public ConfigurationData getConfigData()
	{
		return configData;
	}

	/**
	 * @param configData
	 *           actual configuration
	 */
	public void setConfigData(final ConfigurationData configData)
	{
		this.configData = configData;
	}

	/**
	 * @return actual UI Errors
	 */
	public BindingResult getBindingResult()
	{
		return bindingResult;
	}

	/**
	 * @param bindingResult
	 *           actual UI Errors
	 */
	public void setBindingResult(final BindingResult bindingResult)
	{
		this.bindingResult = bindingResult;
	}

	/**
	 * @return actual UI state
	 */
	public UiStatus getUiStatus()
	{
		return uiStatus;
	}

	/**
	 * @param uiStatus
	 *           actual UI state
	 */
	public void setUiStatus(final UiStatus uiStatus)
	{
		this.uiStatus = uiStatus;
	}

	/**
	 * @return product code of the product the configuration belongs to
	 */
	public String getProductCode()
	{
		return configData.getKbKey().getProductCode();
	}

	/**
	 * @return time elapsed <b>since last invocation of this method</b>. Returns the actual time on first invocation.
	 */
	public long timeElapsed()
	{
		final long actualTime = System.currentTimeMillis();
		final long duration = actualTime - startTime;
		startTime = actualTime;
		return duration;
	}
}
