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

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.daos.AddressDao;
import de.hybris.platform.warehousing.util.builder.AddressModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class Addresses extends AbstractItems<AddressModel>
{
	public static final Double LATITUDE_MONTREAL = 45.5016330;
	public static final Double LONGITUDE_MONTREAL = -73.5740030;
	public static final Double LATITUDE_MONTREAL_NANCY_HOME = 45.5027940;
	public static final Double LONGITUDE_MONTREAL_NANCY_HOME = -73.5714720;
	public static final Double LATITUDE_BOSTON = 42.3519410;
	public static final Double LONGITUDE_BOSTON = -71.0478470;
	public static final String STREET_NUMBER_MONTREAL_MAISONNEUVE = "999";
	public static final String STREET_NAME_MONTREAL_MAISONNEUVE = "De Maisonneuve";
	public static final String POSTAL_CODE_MONTREAL_MAISONNEUVE = "H3A 3L4";
	public static final String STREET_NUMBER_MONTREAL_DUKE = "111";
	public static final String STREET_NAME_MONTREAL_DUKE = "Duke";
	public static final String POSTAL_CODE_MONTREAL_DUKE = "H3C 2M1";
	public static final String STREET_NUMBER_MONTREAL_NANCY_HOME = "705";
	public static final String STREET_NAME_MONTREAL_NANCY_HOME = "Ste-Catherine";
	public static final String POSTAL_CODE_MONTREAL_NANCY_HOME = "H3B 4G5";

	public static final String STREET_NUMBER_BOSTON = "33-41";
	public static final String STREET_NAME_BOSTON = "Farnsworth";
	public static final String POSTAL_CODE_BOSTON = "02210";

	public static final String NAME_MONTREAL = "Montreal";
	public static final String NAME_BOSTON = "Boston";


	private AddressDao addressDao;
	private Countries countries;
	private Users users;
	private Regions regions;

	public AddressModel MontrealDeMaisonneuvePos()
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getAddressDao().findAddressesForOwner(getUsers().ManagerMontrealMaisonneuve()),
				() -> AddressModelBuilder.aModel().withStreetNumber(STREET_NUMBER_MONTREAL_MAISONNEUVE)
						.withStreetName(STREET_NAME_MONTREAL_MAISONNEUVE).withTown(NAME_MONTREAL)
						.withPostalCode(POSTAL_CODE_MONTREAL_MAISONNEUVE).withCountry(getCountries().Canada())
						.withDuplicate(Boolean.FALSE).withBillingAddress(Boolean.FALSE).withContactAddress(Boolean.FALSE)
						.withUnloadingAddress(Boolean.FALSE).withShippingAddress(Boolean.TRUE)
						.withOwner(getUsers().ManagerMontrealMaisonneuve()).withLatitude(LATITUDE_MONTREAL)
						.withLongitude(LONGITUDE_MONTREAL).withRegion(getRegions().quebecRegion()).build());
	}

	public AddressModel MontrealDukePos()
	{
		return getFromCollectionOrSaveAndReturn(() -> getAddressDao().findAddressesForOwner(getUsers().ManagerMontrealDuke()),
				() -> AddressModelBuilder.aModel().withStreetNumber(STREET_NUMBER_MONTREAL_DUKE)
						.withStreetName(STREET_NAME_MONTREAL_DUKE).withTown(NAME_MONTREAL).withPostalCode(POSTAL_CODE_MONTREAL_DUKE)
						.withCountry(getCountries().Canada()).withDuplicate(Boolean.FALSE).withBillingAddress(Boolean.FALSE)
						.withContactAddress(Boolean.FALSE).withUnloadingAddress(Boolean.FALSE).withShippingAddress(Boolean.TRUE)
						.withOwner(getUsers().ManagerMontrealDuke()).withRegion(getRegions().quebecRegion()).build());
	}

	public AddressModel MontrealNancyHome()
	{
		return getFromCollectionOrSaveAndReturn(() -> getAddressDao().findAddressesForOwner(getUsers().Nancy()),
				() -> AddressModelBuilder.fromModel(ShippingAddress()).withStreetNumber(STREET_NUMBER_MONTREAL_NANCY_HOME)
						.withStreetName(STREET_NAME_MONTREAL_NANCY_HOME).withTown(NAME_MONTREAL)
						.withPostalCode(POSTAL_CODE_MONTREAL_NANCY_HOME).withCountry(getCountries().Canada())
						.withOwner(getUsers().Nancy()).withLatitude(LATITUDE_MONTREAL_NANCY_HOME)
						.withLongitude(LONGITUDE_MONTREAL_NANCY_HOME).withRegion(getRegions().quebecRegion()).build());
	}

	public AddressModel Boston()
	{
		return getFromCollectionOrSaveAndReturn(() -> getAddressDao().findAddressesForOwner(getUsers().Bob()),
				() -> AddressModelBuilder.fromModel(ShippingAddress()).withStreetNumber(STREET_NUMBER_BOSTON)
						.withStreetName(STREET_NAME_BOSTON).withTown(NAME_BOSTON).withPostalCode(POSTAL_CODE_BOSTON)
						.withCountry(getCountries().UnitedStates()).withDuplicate(Boolean.FALSE).withBillingAddress(Boolean.TRUE)
						.withContactAddress(Boolean.FALSE).withUnloadingAddress(Boolean.FALSE).withShippingAddress(Boolean.TRUE)
						.withOwner(getUsers().Bob()).withLatitude(LATITUDE_BOSTON).withLongitude(LONGITUDE_BOSTON)
						.withRegion(getRegions().massachusettsRegion()).build());
	}

	protected AddressModel ShippingAddress()
	{
		return AddressModelBuilder.aModel().withDuplicate(Boolean.FALSE).withBillingAddress(Boolean.FALSE)
				.withContactAddress(Boolean.FALSE).withUnloadingAddress(Boolean.FALSE).withShippingAddress(Boolean.TRUE).build();
	}

	public AddressDao getAddressDao()
	{
		return addressDao;
	}

	@Required
	public void setAddressDao(final AddressDao addressDao)
	{
		this.addressDao = addressDao;
	}

	public Countries getCountries()
	{
		return countries;
	}

	@Required
	public void setCountries(final Countries countries)
	{
		this.countries = countries;
	}

	public Users getUsers()
	{
		return users;
	}

	@Required
	public void setUsers(final Users users)
	{
		this.users = users;
	}

	protected Regions getRegions()
	{
		return regions;
	}

	@Required
	public void setRegions(final Regions regions)
	{
		this.regions = regions;
	}
}
