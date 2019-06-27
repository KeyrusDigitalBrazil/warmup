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
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.excel.translators.generic.factory.RequiredAttributeTestFactory;


public class ExcelGenericReferenceValidatorTest
{

	@InjectMocks
	ExcelGenericReferenceValidator excelGenericReferenceValidator;

	@Before
	public void setUpMockito()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldBuildFlexibleQueryToCheckWhetherCatalogExist()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put("Catalog.id", "Default");

		// when
		final Optional<FlexibleSearchQuery> flexibleSearchQuery = excelGenericReferenceValidator
				.buildFlexibleSearchQuery(RequiredAttributeTestFactory.prepareStructureForCatalog(), params, new HashMap<>());

		// then
		assertThat(flexibleSearchQuery).isPresent();
		assertThat(flexibleSearchQuery.get().getQuery()).isEqualToIgnoringCase("SELECT {pk} FROM {Catalog} WHERE {id} = ?id");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsKeys("id");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsValues("Default");
	}

	@Test
	public void shouldBuildFlexibleQueryToCheckWhetherCatalogVersionExist()
	{
		// given
		final long catalogPk = 123L;
		final Map<String, String> params = new HashMap<>();
		params.put("CatalogVersion.version", "Online");
		params.put("Catalog.id", "Default");
		final HashMap context = new HashMap();
		context.put("Catalog_Default", prepareCatalog(catalogPk));

		// when
		final Optional<FlexibleSearchQuery> flexibleSearchQuery = excelGenericReferenceValidator
				.buildFlexibleSearchQuery(RequiredAttributeTestFactory.prepareStructureForCatalogVersion(), params, context);

		// then
		assertThat(flexibleSearchQuery).isPresent();
		assertThat(flexibleSearchQuery.get().getQuery())
				.isEqualToIgnoringCase("SELECT {pk} FROM {CatalogVersion} WHERE {version} = ?version AND {catalog} = ?catalog");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsKeys("version", "catalog");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsValues("Online", catalogPk);
	}

	@Test
	public void shouldBuildFlexibleQueryToCheckWhetherSupercategoryExist()
	{
		// given
		final long catalogPk = 123L;
		final long catalogVersionPk = 987L;
		final Map<String, String> params = new HashMap<>();
		params.put("Category.code", "Hardware");
		params.put("CatalogVersion.version", "Online");
		params.put("Catalog.id", "Default");
		final HashMap<String, Object> context = new HashMap<>();
		context.put("Catalog_Default", prepareCatalog(catalogPk));
		context.put("CatalogVersion_Online_Default", prepareCatalogVersion(catalogVersionPk));

		// when
		final Optional<FlexibleSearchQuery> flexibleSearchQuery = excelGenericReferenceValidator
				.buildFlexibleSearchQuery(RequiredAttributeTestFactory.prepareStructureForSupercategories(), params, context);

		// then
		assertThat(flexibleSearchQuery).isPresent();
		assertThat(flexibleSearchQuery.get().getQuery())
				.isEqualToIgnoringCase("SELECT {pk} FROM {Category} WHERE {code} = ?code AND {catalogVersion} = ?catalogVersion");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsKeys("code", "catalogVersion");
		assertThat(flexibleSearchQuery.get().getQueryParameters()).containsValues("Hardware", catalogVersionPk);
	}

	private CatalogModel prepareCatalog(final long pkValue)
	{
		final CatalogModel catalogModel = Mockito.mock(CatalogModel.class);
		given(catalogModel.getPk()).willReturn(PK.fromLong(pkValue));
		return catalogModel;
	}

	private CatalogVersionModel prepareCatalogVersion(final long pkValue)
	{
		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		given(catalogVersionModel.getPk()).willReturn(PK.fromLong(pkValue));
		return catalogVersionModel;
	}
}
