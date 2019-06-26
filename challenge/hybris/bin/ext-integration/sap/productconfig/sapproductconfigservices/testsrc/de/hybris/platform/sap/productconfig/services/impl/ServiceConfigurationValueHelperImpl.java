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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;


@SuppressWarnings("javadoc")
public class ServiceConfigurationValueHelperImpl
{
	public CsticModel getCstic(final ConfigModel configModel, final String csticName)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		CsticModel cstic = getCsticModel(csticName, rootInstance.getCstics());

		if (null == cstic)
		{
			final List<InstanceModel> subInstances = rootInstance.getSubInstances();
			if (CollectionUtils.isNotEmpty(subInstances))
			{
				for (final InstanceModel subInstance : subInstances)
				{
					cstic = getCsticModel(csticName, subInstance.getCstics());
				}
			}
		}
		return cstic;
	}

	public CsticModel getCstic(final ConfigModel configModel, final String instanceId, final String csticName)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		CsticModel cstic = !instanceId.equalsIgnoreCase(rootInstance.getId()) ? null
				: getCsticModel(csticName, rootInstance.getCstics());

		if (null == cstic)
		{
			final List<InstanceModel> subInstances = rootInstance.getSubInstances();
			if (CollectionUtils.isNotEmpty(subInstances))
			{
				for (final InstanceModel subInstance : subInstances)
				{
					if (instanceId.equalsIgnoreCase(subInstance.getId()))
					{
						cstic = getCsticModel(csticName, subInstance.getCstics());
					}
				}
			}
		}
		return cstic;
	}

	protected CsticModel getCsticModel(final String csticValueName, final List<CsticModel> cstics)
	{
		for (final CsticModel cstic : cstics)
		{
			if (csticValueName.equalsIgnoreCase(cstic.getName()))
			{
				return cstic;
			}
		}
		return null;
	}

	public CsticValueModel getCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		return getAssignableValue(cstic.getAssignableValues(), csticValueName);
	}

	public boolean isValueAssigned(final CsticModel cstic, final String name)
	{
		final List<CsticValueModel> values = cstic.getAssignedValues();

		for (final CsticValueModel value : values)
		{
			if (name.equalsIgnoreCase(value.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public void setSingleCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		cstic.setSingleValue(csticValueName);
	}

	public void addCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		cstic.addValue(csticValueName);
	}

	protected CsticValueModel getAssignableValue(final List<CsticValueModel> assignableValues, final String value)
	{
		if (CollectionUtils.isNotEmpty(assignableValues))
		{
			for (final CsticValueModel assignableValue : assignableValues)
			{
				if (value.equalsIgnoreCase(assignableValue.getName()))
				{
					return assignableValue;
				}
			}
		}

		return null;
	}

	public void selectUnselectCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName,
			final boolean selected)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		if (selected)
		{
			cstic.addValue(csticValueName);
		}
		else
		{
			cstic.removeValue(csticValueName);
		}
	}
}
