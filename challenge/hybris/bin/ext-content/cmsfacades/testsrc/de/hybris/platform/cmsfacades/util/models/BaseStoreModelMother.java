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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.builder.BaseStoreModelBuilder;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.daos.BaseStoreDao;

import org.springframework.beans.factory.annotation.Required;


public class BaseStoreModelMother extends AbstractModelMother<BaseStoreModel>
{
	public static final String UID_NORTH_AMERICA = "north-america";
	public static final String CODE_RETURN_PROCESS = "return-process";

	private BaseStoreDao baseStoreDao;
	private LanguageModelMother languageModelMother;
	private CatalogVersionModelMother catalogVersionModelMother;
	private CurrencyModelMother currencyModelMother;
	private CountryModelMother countryModelMother;
	private DeliveryModeModelMother deliveryModeModelMother;

	public BaseStoreModel createNorthAmerica(final CatalogVersionModel... catalogVersionModels)
	{
		final CatalogModel[] catalogModels = asList(catalogVersionModels).stream().map(e -> e.getCatalog()).collect(toList())
				.toArray(new CatalogModel[] {});

		return getFromCollectionOrSaveAndReturn(() -> getBaseStoreDao().findBaseStoresByUid(UID_NORTH_AMERICA),
				() -> BaseStoreModelBuilder.aModel().withCatalogs(catalogModels)
						.withCurrencies(getCurrencyModelMother().createUSDollar())
						.withDefaultCurrency(getCurrencyModelMother().createUSDollar())
						.withDefaultLanguage(getLanguageModelMother().createEnglish())
						.withLanguages(getLanguageModelMother().createEnglish(), getLanguageModelMother().createFrench())
						.withDeliveryCountries(getCountryModelMother().UnitedStates(), getCountryModelMother().Canada())
						.withNet(Boolean.FALSE).withPaymentProvider("Mockup") //
						.withSubmitOrderProcessCode("order-process") //
						.withUid(UID_NORTH_AMERICA)
						.withDeliveryModes(getDeliveryModeModelMother().Pickup(), getDeliveryModeModelMother().Regular()) //
						.build());
	}

	public BaseStoreDao getBaseStoreDao()
	{
		return baseStoreDao;
	}

	@Required
	public void setBaseStoreDao(final BaseStoreDao baseStoreDao)
	{
		this.baseStoreDao = baseStoreDao;
	}

	public LanguageModelMother getLanguageModelMother()
	{
		return languageModelMother;
	}

	@Required
	public void setLanguageModelMother(final LanguageModelMother languageModelMother)
	{
		this.languageModelMother = languageModelMother;
	}

	public CatalogVersionModelMother getCatalogVersionModelMother()
	{
		return catalogVersionModelMother;
	}

	@Required
	public void setCatalogVersionModelMother(final CatalogVersionModelMother catalogVersionModelMother)
	{
		this.catalogVersionModelMother = catalogVersionModelMother;
	}

	public CurrencyModelMother getCurrencyModelMother()
	{
		return currencyModelMother;
	}

	@Required
	public void setCurrencyModelMother(final CurrencyModelMother currencyModelMother)
	{
		this.currencyModelMother = currencyModelMother;
	}

	public CountryModelMother getCountryModelMother()
	{
		return countryModelMother;
	}

	@Required
	public void setCountryModelMother(final CountryModelMother countryModelMother)
	{
		this.countryModelMother = countryModelMother;
	}

	public DeliveryModeModelMother getDeliveryModeModelMother()
	{
		return deliveryModeModelMother;
	}

	@Required
	public void setDeliveryModeModelMother(final DeliveryModeModelMother deliveryModeModelMother)
	{
		this.deliveryModeModelMother = deliveryModeModelMother;
	}
}
