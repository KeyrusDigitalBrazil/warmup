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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cmsfacades.util.builder.CountryModelBuilder;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class CountryModelMother extends AbstractModelMother<CountryModel>
{
	public static final String ISOCODE_CANADA = "CA";
	public static final String ISOCODE_UNITED_STATES = "US";

	private CountryDao countryDao;

	public CountryModel Canada()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCountryDao().findCountriesByCode(ISOCODE_CANADA), () -> CountryModelBuilder
				.aModel().withIsoCode(ISOCODE_CANADA).withName("Canada", Locale.ENGLISH).withActive(Boolean.TRUE).build());
	}

	public CountryModel UnitedStates()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCountryDao().findCountriesByCode(ISOCODE_UNITED_STATES),
				() -> CountryModelBuilder.aModel().withIsoCode(ISOCODE_UNITED_STATES).withName("United States", Locale.ENGLISH)
						.withActive(Boolean.TRUE).build());
	}

	public CountryDao getCountryDao()
	{
		return countryDao;
	}

	@Required
	public void setCountryDao(final CountryDao countryDao)
	{
		this.countryDao = countryDao;
	}

}