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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


/**
 * Default implementation of the {@link UniqueUIKeyGenerator}.<br>
 * Re-Uses a single StringBuilder instance per thread to save heap memory. This class gets called quite often within a
 * single requests, up to a few million times for large models.
 */
public class UniqueUIKeyGeneratorImpl implements UniqueUIKeyGenerator
{
	/**
	 * Separator for UIKey componets, such as Instance, group and cstic key.
	 */
	public static final String KEY_SEPARATOR = ".";
	/**
	 * to use the key separator ina split statement, we have to escape it.
	 */
	private static final String KEY_SEPARATOR_SPLIT = "\\" + KEY_SEPARATOR;
	/**
	 * Instance key consists of name and id, which are separated by this sepperator.
	 */
	public static final String INSTANCE_SEPERATOR = "-";
	private static final ThreadLocal<StringBuilder> keyBuilder = new ThreadLocal()
	{
		@Override
		protected StringBuilder initialValue()
		{
			return new StringBuilder(128);
		}
	};

	@Override
	public String generateGroupIdForInstance(final InstanceModel instance)
	{
		return generateGroupIdForGroup(instance, null);
	}


	@Override
	public String generateGroupIdForGroup(final InstanceModel instance, final CsticGroup csticModelGroup)
	{
		final StringBuilder strBuilder = getStrBuilder();
		strBuilder.append(instance.getId());
		strBuilder.append(INSTANCE_SEPERATOR);
		strBuilder.append(instance.getName());
		if (csticModelGroup != null)
		{
			strBuilder.append(KEY_SEPARATOR);
			strBuilder.append(csticModelGroup.getName());
		}
		return strBuilder.toString();
	}


	@Override
	public String retrieveInstanceId(final String uiGroupId)
	{
		return uiGroupId.substring(0, uiGroupId.indexOf(INSTANCE_SEPERATOR));
	}


	@Override
	public String generateCsticId(final CsticModel model, final CsticValueModel value, final String prefix)
	{
		final StringBuilder strBuilder = getStrBuilder();
		strBuilder.append(prefix);
		strBuilder.append(KEY_SEPARATOR);
		strBuilder.append(model.getName());
		if (value != null)
		{
			strBuilder.append(KEY_SEPARATOR);
			strBuilder.append(value.getName());
		}

		return strBuilder.toString();
	}

	protected StringBuilder getStrBuilder()
	{
		final StringBuilder strBuilder = keyBuilder.get();
		strBuilder.setLength(0);
		if (strBuilder.capacity() > 1024)
		{
			strBuilder.trimToSize();
			strBuilder.ensureCapacity(128);
		}
		return strBuilder;
	}


	@Override
	public CsticQualifier splitId(final String csticUiKey)
	{
		final String[] splitInstanceGroup = csticUiKey.split(KEY_SEPARATOR_SPLIT, 2);
		final String[] splittedInstancePart = splitInstanceGroup[0].split(INSTANCE_SEPERATOR, 2);
		final String[] splittedGroupPart = splitInstanceGroup[1].split(KEY_SEPARATOR_SPLIT);
		if (splittedGroupPart.length != 2 || splittedInstancePart.length != 2)
		{
			throw new IllegalArgumentException("provided cstic ui key '" + csticUiKey
					+ "' is not of form 'instancedId-InstanceName.groupName.csticName");
		}
		final CsticQualifier csticQualifier = new CsticQualifier();
		csticQualifier.setInstanceId(splittedInstancePart[0]);
		csticQualifier.setInstanceName(splittedInstancePart[1]);
		csticQualifier.setGroupName(splittedGroupPart[0]);
		csticQualifier.setCsticName(splittedGroupPart[1]);
		return csticQualifier;
	}


	@Override
	public String generateId(final CsticQualifier csticQualifier)
	{
		final StringBuilder strBuilder = getStrBuilder();
		strBuilder.append(csticQualifier.getInstanceId());
		strBuilder.append(INSTANCE_SEPERATOR);
		strBuilder.append(csticQualifier.getInstanceName());
		strBuilder.append(KEY_SEPARATOR);
		strBuilder.append(csticQualifier.getGroupName());
		strBuilder.append(KEY_SEPARATOR);
		strBuilder.append(csticQualifier.getCsticName());
		return strBuilder.toString();
	}

}
