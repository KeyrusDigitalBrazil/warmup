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
package com.hybris.backoffice.excel.translators.generic.factory;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.testframework.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Test;


@Transactional
@IntegrationTest
public class DefaultExportDataFactoryIntegrationTest extends ServicelayerTest
{

	@Resource
	DefaultExportDataFactory defaultExportDataFactory;

	@Test
	public void shouldExportValueForCatalogVersion()
	{
		// given
		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId("Default");
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setVersion("Online");
		catalogVersionModel.setCatalog(catalogModel);

		// when
		final Optional<String> exportedValue = defaultExportDataFactory
				.create(RequiredAttributeTestFactory.prepareStructureForCatalogVersion(), catalogVersionModel);

		// then
		assertThat(exportedValue).isPresent();
		assertThat(exportedValue.get()).isEqualTo("Online:Default");
	}

	@Test
	public void shouldExportValueForSupercategories()
	{
		// given
		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId("Default");
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setVersion("Online");
		catalogVersionModel.setCatalog(catalogModel);
		final CategoryModel firstCategory = new CategoryModel();
		firstCategory.setCode("firstCategory");
		firstCategory.setCatalogVersion(catalogVersionModel);

		final CategoryModel secondCategory = new CategoryModel();
		secondCategory.setCode("secondCategory");
		secondCategory.setCatalogVersion(catalogVersionModel);


		// when
		final Optional<String> exportedValue = defaultExportDataFactory.create(
				RequiredAttributeTestFactory.prepareStructureForSupercategories(), Arrays.asList(firstCategory, secondCategory));

		// then
		assertThat(exportedValue).isPresent();
		assertThat(exportedValue.get()).isEqualTo("firstCategory:Online:Default,secondCategory:Online:Default");
	}

	@Test
	public void shouldExportValueForPrices()
	{
		// given
		final Collection<PriceRowModel> priceRows = Arrays.asList(preparePriceRow(3.14, "PLN", "pieces"),
				preparePriceRow(1.1, "USD", "pieces"));

		// when
		final Optional<String> exportedValue = defaultExportDataFactory
				.create(RequiredAttributeTestFactory.prepareStructureForPrices(), priceRows);

		// then
		assertThat(exportedValue).isPresent();
		assertThat(exportedValue.get()).isEqualTo("3.14:pieces:PLN,1.1:pieces:USD");
	}

	private static PriceRowModel preparePriceRow(final Double value, final String currency, final String unit)
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(currency);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(unit);
		final PriceRowModel priceRowModel = new PriceRowModel();
		priceRowModel.setPrice(value);
		priceRowModel.setCurrency(currencyModel);
		priceRowModel.setUnit(unitModel);
		return priceRowModel;
	}

}
