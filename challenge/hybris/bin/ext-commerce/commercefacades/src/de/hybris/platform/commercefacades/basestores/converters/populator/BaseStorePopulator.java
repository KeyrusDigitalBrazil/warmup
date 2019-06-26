/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.basestores.converters.populator;

import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link BaseStoreData} from {@link BaseStoreModel}
 */
public class BaseStorePopulator implements Populator<BaseStoreModel, BaseStoreData>
{
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private Converter<LanguageModel, LanguageData> languageConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private Converter<CountryModel, CountryData> countryConverter;

	@Override
	public void populate(final BaseStoreModel source, final BaseStoreData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setName(source.getName());
			target.setExternalTaxEnabled(source.getExternalTaxEnabled());
			target.setPaymentProvider(source.getPaymentProvider());
			target.setCreateReturnProcessCode(source.getCreateReturnProcessCode());
			target.setMaxRadiusForPosSearch(source.getMaxRadiusForPoSSearch());
			target.setSubmitOrderProcessCode(source.getSubmitOrderProcessCode());

			if (source.getDefaultCurrency() != null)
			{
				target.setDefaultCurrency(getCurrencyConverter().convert(source.getDefaultCurrency()));
			}
			if (source.getDefaultDeliveryOrigin() != null)
			{
				target.setDefaultDeliveryOrigin(getPointOfServiceConverter().convert(source.getDefaultDeliveryOrigin()));
			}
			if (source.getDefaultLanguage() != null)
			{
				target.setDefaultLanguage(getLanguageConverter().convert(source.getDefaultLanguage()));
			}

			target.setCurrencies(new ArrayList<>());
			source.getCurrencies().forEach(currency -> target.getCurrencies().add(getCurrencyConverter().convert(currency)));

			target.setDeliveryCountries(new ArrayList<>());
			source.getDeliveryCountries()
					.forEach(country -> target.getDeliveryCountries().add(getCountryConverter().convert(country)));

			final List<DeliveryModeData> result = new ArrayList<DeliveryModeData>();
			source.getDeliveryModes().forEach(deliveryMode -> result.add(getDeliveryModeConverter().convert(deliveryMode)));
			DeliveryModesData deliveryModesData = new DeliveryModesData();
			deliveryModesData.setDeliveryModes(result);
			target.setDeliveryModes(deliveryModesData);

			target.setLanguages(new ArrayList<>());
			source.getLanguages().forEach(language -> target.getLanguages().add(getLanguageConverter().convert(language)));

			target.setPointsOfService(new ArrayList<>());
			source.getPointsOfService().forEach(pos -> target.getPointsOfService().add(getPointOfServiceConverter().convert(pos)));
		}
	}

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	@Required
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	@Required
	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}
}
