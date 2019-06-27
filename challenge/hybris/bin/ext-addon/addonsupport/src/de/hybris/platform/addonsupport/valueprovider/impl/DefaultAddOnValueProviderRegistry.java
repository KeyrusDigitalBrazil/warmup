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
package de.hybris.platform.addonsupport.valueprovider.impl;

import de.hybris.platform.addonsupport.valueprovider.AddOnValueProvider;
import de.hybris.platform.addonsupport.valueprovider.AddOnValueProviderRegistry;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link AddOnValueProviderRegistry} interface.
 */
public class DefaultAddOnValueProviderRegistry implements AddOnValueProviderRegistry
{

	private Map<String, AddOnValueProvider> valueProviders;

	@Override
	public Optional<AddOnValueProvider> get(final String addOnName)
	{
		return Optional.ofNullable(getValueProviders().get(addOnName));
	}

	protected Map<String, AddOnValueProvider> getValueProviders()
	{
		return valueProviders;
	}

	@Required
	public void setValueProviders(final Map<String, AddOnValueProvider> valueProviders)
	{
		this.valueProviders = valueProviders;
	}

}
