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
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.daos.BaseStoreDao;
import de.hybris.platform.warehousing.util.builder.BaseStoreModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class BaseStores extends AbstractItems<BaseStoreModel>
{
	public static final String UID_NORTH_AMERICA = "north-america";
	public static final String CODE_RETURN_PROCESS = "return-process";

	private BaseStoreDao baseStoreDao;
	private Languages languages;
	private Catalogs catalogs;
	private Currencies currencies;
	private Countries countries;
	private DeliveryModes deliveryModes;
	private AtpFormulas atpFormulas;
	private SourcingConfigs sourcingConfigs;
	private ContentCatalogs contentCatalogs;

	public BaseStoreModel NorthAmerica()
	{
		return getFromCollectionOrSaveAndReturn(() -> getBaseStoreDao().findBaseStoresByUid(UID_NORTH_AMERICA), 
				() -> BaseStoreModelBuilder.aModel() 
						.withCatalogs(getContentCatalogs().contentCatalog_online())
						.withCurrencies(getCurrencies().AmericanDollar()) 
						.withDefaultCurrency(getCurrencies().AmericanDollar()) 
						.withDefaultLanguage(getLanguages().English()) 
						.withDeliveryCountries(getCountries().UnitedStates(), getCountries().Canada(), getCountries().France())
						.withLanguages(getLanguages().English()) 
						.withNet(Boolean.FALSE) 
						.withPaymentProvider("Mockup") 
						.withSubmitOrderProcessCode("order-process") 
						.withUid(UID_NORTH_AMERICA) 
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular()) 
						.withCreateReturnProcessCode(CODE_RETURN_PROCESS)
						.withAtpFormula(atpFormulas.Hybris())
						.withSourcingConfig(sourcingConfigs.HybrisConfig())
						.withExternalTaxEnabled(true)
						.build());
	}

	public Languages getLanguages()
	{
		return languages;
	}

	@Required
	public void setLanguages(final Languages languages)
	{
		this.languages = languages;
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

	public Catalogs getCatalogs()
	{
		return catalogs;
	}

	@Required
	public void setCatalogs(final Catalogs catalogs)
	{
		this.catalogs = catalogs;
	}

	public ContentCatalogs getContentCatalogs()
	{
		return contentCatalogs;
	}

	@Required
	public void setContentCatalogs(final ContentCatalogs contentCatalogs)
	{
		this.contentCatalogs = contentCatalogs;
	}

	public Currencies getCurrencies()
	{
		return currencies;
	}

	@Required
	public void setCurrencies(final Currencies currencies)
	{
		this.currencies = currencies;
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

	public DeliveryModes getDeliveryModes()
	{
		return deliveryModes;
	}

	@Required
	public void setDeliveryModes(final DeliveryModes deliveryModes)
	{
		this.deliveryModes = deliveryModes;
	}

	@Required
	public void setAtpFormulas(AtpFormulas atpFormulas)
	{
		this.atpFormulas = atpFormulas;
	}

	public AtpFormulas getAtpFormulas()
	{
		return atpFormulas;
	}

	public SourcingConfigs getSourcingConfigs()
	{
		return sourcingConfigs;
	}

	@Required
	public void setSourcingConfigs(final SourcingConfigs sourcingConfigs)
	{
		this.sourcingConfigs = sourcingConfigs;
	}
}
