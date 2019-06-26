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
package de.hybris.platform.ruleengineservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	private static final String CATALOG_AWARE_VALUE = "code::catalog";

	private static final String CATALOG = "catalog";

	private static final String CATALOG_VERSION = "catalogVersion";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private ProductDao productDao;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private ProductModel product;

	@InjectMocks
	private ProductRuleParameterValueMapper mapper;

	@Mock
	private CatalogModel catalog;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private RuleParameterValueMapper<CatalogModel> catalogRuleParameterValueMapper;

	@Before
	public void setUp()
	{
		given(catalog.getId()).willReturn(CATALOG);
		given(catalogVersion.getVersion()).willReturn(CATALOG_VERSION);
		given(catalogRuleParameterValueMapper.fromString(CATALOG)).willReturn(catalog);
		given(catalogVersionService.getCatalogVersion(CATALOG, CATALOG_VERSION)).willReturn(catalogVersion);
		mapper.setDelimiter("::");
		mapper.setCatalogVersionName(CATALOG_VERSION);
	}

	@Test
	public void nullTestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void nullTestToString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noProductFoundTest()
	{
		//given
		given(productDao.findProductsByCode(Mockito.anyString())).willReturn(null);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedProductTest()
	{
		//given
		final List<ProductModel> products = new ArrayList<>();
		products.add(mock(ProductModel.class));
		products.add(mock(ProductModel.class));

		given(productDao.findProductsByCode(Mockito.anyString())).willReturn(products);

		//when
		final ProductModel mappedProduct = mapper.fromString(ANY_STRING);

		//then
		assertTrue(products.contains(mappedProduct));
	}

	@Test
	public void mappedCatalogAwareCategoryTest()
	{
		//given
		final List<ProductModel> products = new ArrayList<>();
		products.add(mock(ProductModel.class));
		products.add(mock(ProductModel.class));

		given(productDao.findProductsByCode(catalogVersion, "code")).willReturn(products);

		//when
		final ProductModel mappedProduct = mapper.fromString(CATALOG_AWARE_VALUE);

		//then
		assertTrue(products.contains(mappedProduct));

		verify(catalogRuleParameterValueMapper).fromString(CATALOG);
	}

	@Test
	public void mappedProductIsFirstFoundTest()
	{
		//given
		final List<ProductModel> products = new ArrayList<>();
		products.add(mock(ProductModel.class));
		products.add(mock(ProductModel.class));

		given(productDao.findProductsByCode(Mockito.anyString())).willReturn(products);

		//when
		final ProductModel mappedProduct = mapper.fromString(ANY_STRING);

		//then
		assertEquals(products.get(0), mappedProduct);
	}

	@Test
	public void objectToStringTest()
	{
		//given
		givenStringRepresentationAttribute();
		given(product.getCatalogVersion()).willReturn(catalogVersion);
		given(catalogVersion.getCatalog()).willReturn(catalog);
		given(catalogRuleParameterValueMapper.toString(product.getCatalogVersion().getCatalog())).willReturn(CATALOG);

		//when
		final String stringRepresentation = mapper.toString(product);

		//then
		assertEquals(ANY_STRING + "::" + CATALOG, stringRepresentation);
	}

	private void givenStringRepresentationAttribute()
	{
		given(product.getCode()).willReturn(ANY_STRING);
	}
}
