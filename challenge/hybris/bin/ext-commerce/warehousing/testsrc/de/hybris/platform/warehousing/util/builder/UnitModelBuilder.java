/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.product.UnitModel;

import java.util.Locale;


public class UnitModelBuilder
{
	private final UnitModel model;

	private UnitModelBuilder()
	{
		model = new UnitModel();
	}

	private UnitModel getModel()
	{
		return this.model;
	}

	public static UnitModelBuilder aModel()
	{
		return new UnitModelBuilder();
	}

	public UnitModel build()
	{
		return getModel();
	}

	public UnitModelBuilder withUnitType(final String unitType)
	{
		getModel().setUnitType(unitType);
		return this;
	}

	public UnitModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public UnitModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public UnitModelBuilder withConversion(final Double conversion)
	{
		getModel().setConversion(conversion);
		return this;
	}
}
