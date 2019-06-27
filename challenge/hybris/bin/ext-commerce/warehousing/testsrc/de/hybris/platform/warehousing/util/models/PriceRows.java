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

import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.warehousing.util.builder.PriceRowBuilder;
import de.hybris.platform.warehousing.util.dao.impl.PriceRowWarehousingDao;

import org.springframework.beans.factory.annotation.Required;


public class PriceRows extends AbstractItems<PriceRowModel>
{
	public static final Double CAMERA_PRICE = 23d;
	public static final Double MEMORY_CARD_PRICE = 17d;
	public static final Double LENS_PRICE = 13d;
	private PriceRowWarehousingDao priceRowWarehousingDao;
	private Currencies currencies;
	private Units units;
	private Catalogs catalogs;
	private Customers customers;

	public PriceRowModel CameraPrice(final String productId)
	{
		return getOrSaveAndReturn(() -> getPriceRowWarehousingDao().getByCode(productId),
				() -> PriceRowBuilder.aModel().withCurrency(getCurrencies().AmericanDollar()).withPrice(CAMERA_PRICE)
						.withProductId(productId).withUnit(getUnits().Unit())
						.withCatalogVersion(getCatalogs().Primary().getActiveCatalogVersion()).withUser(getCustomers().polo()).build());
	}

	public PriceRowModel MemoryCardPrice(final String productId)
	{
		return getOrSaveAndReturn(() -> getPriceRowWarehousingDao().getByCode(productId),
				() -> PriceRowBuilder.aModel().withCurrency(getCurrencies().AmericanDollar()).withPrice(MEMORY_CARD_PRICE)
						.withProductId(productId).withUnit(getUnits().Unit())
						.withCatalogVersion(getCatalogs().Primary().getActiveCatalogVersion()).withUser(getCustomers().polo()).build());
	}

	public PriceRowModel LensPrice(final String productId)
	{
		return getOrSaveAndReturn(() -> getPriceRowWarehousingDao().getByCode(productId),
				() -> PriceRowBuilder.aModel().withCurrency(getCurrencies().AmericanDollar()).withPrice(LENS_PRICE)
						.withProductId(productId).withUnit(getUnits().Unit())
						.withCatalogVersion(getCatalogs().Primary().getActiveCatalogVersion()).withUser(getCustomers().polo()).build());
	}

	protected PriceRowWarehousingDao getPriceRowWarehousingDao()
	{
		return priceRowWarehousingDao;
	}

	@Required
	public void setPriceRowWarehousingDao(final PriceRowWarehousingDao priceRowWarehousingDao)
	{
		this.priceRowWarehousingDao = priceRowWarehousingDao;
	}

	protected Currencies getCurrencies()
	{
		return currencies;
	}

	@Required
	public void setCurrencies(final Currencies currencies)
	{
		this.currencies = currencies;
	}

	protected Units getUnits()
	{
		return units;
	}

	@Required
	public void setUnits(final Units units)
	{
		this.units = units;
	}

	protected Customers getCustomers()
	{
		return customers;
	}

	@Required
	public void setCustomers(final Customers customers)
	{
		this.customers = customers;
	}

	protected Catalogs getCatalogs()
	{
		return catalogs;
	}

	@Required
	public void setCatalogs(final Catalogs catalogs)
	{
		this.catalogs = catalogs;
	}
}
