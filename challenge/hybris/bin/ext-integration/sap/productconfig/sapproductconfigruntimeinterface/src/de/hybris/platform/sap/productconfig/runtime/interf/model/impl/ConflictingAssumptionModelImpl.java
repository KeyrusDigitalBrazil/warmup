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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;


/**
 * Implementation of {@link ConflictingAssumptionModel}
 */
public class ConflictingAssumptionModelImpl implements ConflictingAssumptionModel
{

	private String csticName;
	private String valueName;
	private String instanceId;
	private String id;

	@Override
	public String getInstanceId()
	{
		return instanceId;
	}

	@Override
	public String getValueName()
	{
		return valueName;
	}

	@Override
	public void setCsticName(final String csticName)
	{
		this.csticName = csticName;

	}

	@Override
	public String getCsticName()
	{
		return csticName;
	}

	@Override
	public void setValueName(final String valueName)
	{
		this.valueName = valueName;

	}

	@Override
	public void setInstanceId(final String instanceId)
	{
		this.instanceId = instanceId;

	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(50);
		builder.append("\nConflictingAssumptionModelImpl [instanceId=");
		builder.append(instanceId);
		builder.append(", csticName=");
		builder.append(csticName);
		builder.append(", valueName=");
		builder.append(valueName);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void setId(final String assumptionId)
	{
		id = assumptionId;

	}


	@Override
	public String getId()
	{
		return id;
	}
}
