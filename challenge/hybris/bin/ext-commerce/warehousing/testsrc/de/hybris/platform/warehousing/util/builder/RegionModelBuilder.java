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

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;

import java.util.Locale;


/**
 * Builder class to build a {@link RegionModel} for testing purposes.
 */
public class RegionModelBuilder
{
	private final RegionModel model;

	/**
	 * Creates a new model
	 */
	private RegionModelBuilder()
	{
		model = new RegionModel();
	}

	/**
	 * Builds a {@link RegionModelBuilder}.
	 *
	 * @return the newly created {@link RegionModelBuilder}
	 */
	public static RegionModelBuilder aModel()
	{
		return new RegionModelBuilder();
	}

	/**
	 * Returns the {@link RegionModel}.
	 *
	 * @return the {@link RegionModel}
	 */
	private RegionModel getModel()
	{
		return model;
	}

	/**
	 * Builds a new {@link RegionModel}.
	 *
	 * @return the {@link RegionModel}
	 */
	public RegionModel build()
	{
		return getModel();
	}

	/**
	 * Adds a {@link CountryModel} to the {@link RegionModel}.
	 *
	 * @param country
	 * 		the {@link RegionModel#COUNTRY}
	 * @return the modifier {@link RegionModel}
	 */
	public RegionModelBuilder withCountry(final CountryModel country)
	{
		getModel().setCountry(country);
		return this;
	}

	/**
	 * Adds a the short isocode to the {@link RegionModel}.
	 *
	 * @param isocodeShort
	 * 		the {@link RegionModel#ISOCODESHORT}
	 * @return the modifier {@link RegionModel}
	 */
	public RegionModelBuilder withIsocodeShort(final String isocodeShort)
	{
		getModel().setIsocodeShort(isocodeShort);
		return this;
	}

	/**
	 * Adds an isocode to the {@link RegionModel}.
	 *
	 * @param isocode
	 * 		the {@link RegionModel#ISOCODE}
	 * @return the modifier {@link RegionModel}
	 */
	public RegionModelBuilder withIsocode(final String isocode)
	{
		getModel().setIsocode(isocode);
		return this;
	}

	/**
	 * Adds a name to the {@link RegionModel}.
	 *
	 * @param name
	 * 		the {@link RegionModel#NAME}
	 * @return the modifier {@link RegionModel}
	 */
	public RegionModelBuilder withName(final String name)
	{
		getModel().setName(name, Locale.ENGLISH);
		return this;
	}

}
