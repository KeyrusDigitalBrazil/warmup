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

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.warehousing.util.builder.CountryModelBuilder;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;


public class Countries extends AbstractItems<CountryModel>
{
	public static final String ISOCODE_CANADA = "CA";
	public static final String ISOCODE_UNITED_STATES = "US";
	public static final String ISOCODE_FRANCE = "FR";

	private CountryDao countryDao;

	public CountryModel Canada()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCountryDao().findCountriesByCode(ISOCODE_CANADA), 
				() -> CountryModelBuilder.aModel() 
						.withIsoCode(ISOCODE_CANADA) 
						.withName("Canada", Locale.ENGLISH) 
						.withActive(Boolean.TRUE) 
						.build());
	}

	public CountryModel UnitedStates()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCountryDao().findCountriesByCode(ISOCODE_UNITED_STATES), 
				() -> CountryModelBuilder.aModel() 
						.withIsoCode(ISOCODE_UNITED_STATES) 
						.withName("United States", Locale.ENGLISH) 
						.withActive(Boolean.TRUE) 
						.build());
	}

	public CountryModel France()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCountryDao().findCountriesByCode(ISOCODE_FRANCE),
				() -> CountryModelBuilder.aModel()
						.withIsoCode(ISOCODE_FRANCE)
						.withName("France", Locale.ENGLISH)
						.withActive(Boolean.TRUE)
						.build());
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
