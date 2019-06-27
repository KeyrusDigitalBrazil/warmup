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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default implementation of the {@link CsticGroup}
 */
public class CsticGroupImpl implements CsticGroup
{
	private String name;
	private String description;
	private List<CsticModel> cstics = Collections.emptyList();


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
	public List<CsticModel> getCstics()
	{

		return Optional.ofNullable(cstics).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public void setCstics(final List<CsticModel> cstics)
	{
		this.cstics = Optional.ofNullable(cstics).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

}
