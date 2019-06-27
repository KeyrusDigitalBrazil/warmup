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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class provides the nearest stores that the as agent has been mapped to, otherwise the browsers location will be
 * considered.
 */
public class StoreLocationsProvider implements FragmentModelProvider<String>
{
	private AssistedServiceService assistedServiceService;

	@Override
	public String getModel(final Map<String, String> parameters)
	{
		String addressData = null;
		final PointOfServiceModel agentStore = getAssistedServiceService().getAssistedServiceAgentStore();

		if (agentStore != null && agentStore.getAddress() != null)
		{
			addressData = prepareAddressData(agentStore.getAddress());
		}
		return addressData;
	}

	protected String prepareAddressData(final AddressModel address)
	{
		final StringBuilder qBulder = new StringBuilder(address.getTown());

		if (address.getCountry() != null)
		{
			qBulder.append(' ').append(address.getCountry().getName());
		}

		if (StringUtils.isNotEmpty(address.getPostalcode()))
		{
			qBulder.append(' ').append(address.getPostalcode());
		}
		return qBulder.toString();
	}

	protected AssistedServiceService getAssistedServiceService()
	{
		return assistedServiceService;
	}

	@Required
	public void setAssistedServiceService(final AssistedServiceService assistedServiceService)
	{
		this.assistedServiceService = assistedServiceService;
	}
}
