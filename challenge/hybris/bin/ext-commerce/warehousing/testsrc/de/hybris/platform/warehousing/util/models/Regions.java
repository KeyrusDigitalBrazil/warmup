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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.i18n.daos.RegionDao;
import de.hybris.platform.warehousing.util.builder.RegionModelBuilder;

import org.springframework.beans.factory.annotation.Required;


/**
 * Testing model to represent the hybris {@link RegionModel} in all integration tests.
 */
public class Regions extends AbstractItems<RegionModel>
{
	public static final String QUEBEC_NAME = "Quebec";
	public static final String MASSACHUSETTS_NAME = "Massachusetts";
	public static final String QUEBEC_ISOCODE = "CA-QC";
	public static final String MASSACHUSETTS_ISOCODE = "US-MA";
	public static final String QUEBEC_ISOCODE_SHORT = "QC";
	public static final String MASSACHUSETTS_ISOCODE_SHORT = "MA";

	private RegionDao regionDao;
	private Countries countries;

	public RegionModel quebecRegion()
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getRegionDao().findRegionsByCountryAndCode(getCountries().Canada(), QUEBEC_ISOCODE),
				() -> RegionModelBuilder.aModel().withCountry(getCountries().Canada()).withIsocode(QUEBEC_ISOCODE)
						.withIsocodeShort(QUEBEC_ISOCODE_SHORT).withName(QUEBEC_NAME).build());
	}

	public RegionModel massachusettsRegion()
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getRegionDao().findRegionsByCountryAndCode(getCountries().UnitedStates(), MASSACHUSETTS_ISOCODE),
				() -> RegionModelBuilder.aModel().withCountry(getCountries().UnitedStates()).withIsocode(MASSACHUSETTS_ISOCODE)
						.withIsocodeShort(MASSACHUSETTS_ISOCODE_SHORT).withName(MASSACHUSETTS_NAME).build());
	}

	protected RegionDao getRegionDao()
	{
		return regionDao;
	}

	@Required
	public void setRegionDao(final RegionDao regionDao)
	{
		this.regionDao = regionDao;
	}

	protected Countries getCountries()
	{
		return countries;
	}

	@Required
	public void setCountries(final Countries countries)
	{
		this.countries = countries;
	}
}
