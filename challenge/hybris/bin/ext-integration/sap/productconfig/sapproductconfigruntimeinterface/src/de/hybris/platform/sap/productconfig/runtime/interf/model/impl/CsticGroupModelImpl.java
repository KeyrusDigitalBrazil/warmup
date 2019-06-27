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

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default implementation of the {@link CsticGroupModel}
 */
public class CsticGroupModelImpl extends BaseModelImpl implements CsticGroupModel
{

	private String name;
	private String description;
	private List<String> csticNames;

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(final String description)
	{
		this.description = description;
	}

	@Override
	public List<String> getCsticNames()
	{
		return Optional.ofNullable(csticNames).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public void setCsticNames(final List<String> csticNames)
	{
		this.csticNames = Optional.ofNullable(csticNames).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(csticNames, description, name);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CsticGroupModelImpl other = (CsticGroupModelImpl) obj;
		if (csticNames == null)
		{
			if (other.csticNames != null)
			{
				return false;
			}
		}
		else if (!csticNames.equals(other.csticNames))
		{
			return false;
		}
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("\nCsticGroupModelImpl [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", csticNames=");
		builder.append(csticNames);
		builder.append("]");
		return builder.toString();
	}

}
