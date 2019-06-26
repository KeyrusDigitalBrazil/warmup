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
package de.hybris.platform.assistedserviceyprofilefacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedserviceyprofilefacades.YProfileAffinityFacade;
import de.hybris.platform.assistedserviceyprofilefacades.data.DeviceAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.SummaryTechnologyUsedData;
import de.hybris.platform.assistedserviceyprofilefacades.data.TechnologyUsedData;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * YProfile Technology fragment provider
 */
public class TechnologyUsedProvider implements FragmentModelProvider<SummaryTechnologyUsedData>
{
	private int deviceFetchLimit = 100;

	private YProfileAffinityFacade yProfileAffinityFacade;
	private UserService userService;

	@Override
	public SummaryTechnologyUsedData getModel(final Map<String, String> parameters)
	{
		final SummaryTechnologyUsedData summaryTechnologyUsedData = new SummaryTechnologyUsedData();

		final DeviceAffinityParameterData deviceAffinityParameterData = new DeviceAffinityParameterData();
		deviceAffinityParameterData.setSizeLimit(deviceFetchLimit);

		final List<TechnologyUsedData> usedTechnologies =
				getyProfileAffinityFacade().getDeviceAffinities(deviceAffinityParameterData);

		final Map<String, TechnologyUsedData> mostPopularTechnologyByDevice = new HashMap<>();

		//Currently we don't have convenient way to get usage percentages
		usedTechnologies.forEach(usedTechnology -> {
			if (usedTechnology.getDevice() != null
					&& !mostPopularTechnologyByDevice.containsKey(usedTechnology.getDevice()))
			{
				mostPopularTechnologyByDevice.put(usedTechnology.getDevice(), usedTechnology);
			}
		});

		summaryTechnologyUsedData.setTechnologyUsedData(new ArrayList<>(mostPopularTechnologyByDevice.values()));
		summaryTechnologyUsedData.setName(getUserService().getCurrentUser().getName());
		
		return summaryTechnologyUsedData;
	}

	protected YProfileAffinityFacade getyProfileAffinityFacade()
	{
		return yProfileAffinityFacade;
	}

	@Required
	public void setyProfileAffinityFacade(final YProfileAffinityFacade yProfileAffinityFacade)
	{
		this.yProfileAffinityFacade = yProfileAffinityFacade;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public int getDeviceFetchLimit()
	{
		return deviceFetchLimit;
	}

	public void setDeviceFetchLimit(final int deviceFetchLimit)
	{
		this.deviceFetchLimit = deviceFetchLimit;
	}
}
