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
package de.hybris.platform.acceleratorfacades.csv.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.csv.CsvFacade;
import de.hybris.platform.basecommerce.util.AbstractCommerceServicelayerTransactionalTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.GenericVariantProductModel;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;


@IntegrationTest
public class DefaultCsvFacadeIntegrationTest extends AbstractCommerceServicelayerTransactionalTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String USER = "abrode";
	private static final String PRODUCT_CODE = "HW1210-3422";
	private static final String MULTID_PRODUCT_CODE1 = "variantProduct1";
	private static final String MULTID_PRODUCT_CODE2 = "variantProduct2";

	private static final String CATALOG_ID = "testCatalog";
	private static final String CATALOG_VERSION = "Online";
	private static final String UNIT_CODE = "pieces";

	@Resource
	private CsvFacade csvFacade;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CartFacade cartFacade;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UnitService unitService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/acceleratorfacades/test/testCommerceCart.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final UserModel user = userService.getUserForUID(USER);
		userService.setCurrentUser(user);

		createMultiDProducts();
	}


	@Test
	public void testGenerateCsvHeader() throws IOException
	{
		try (final StringWriter writer = new StringWriter())
		{
			List<String> headers = null;
			csvFacade.generateCsvFromCart(headers, false, null, writer);
			assertEquals(0, writer.toString().length());

			headers = new ArrayList<String>();
			headers.add("testHeader1");
			headers.add("testHeader2");
			csvFacade.generateCsvFromCart(headers, false, null, writer);
			assertEquals(0, writer.toString().length());

			csvFacade.generateCsvFromCart(headers, true, null, writer);
			assertEquals("testHeader1,testHeader2\n", writer.toString());
		}
	}

	@Test
	public void testGenerateCsvContentFromCart() throws CommerceCartModificationException, IOException
	{
		try (final StringWriter writer = new StringWriter())
		{
			cartFacade.addToCart(PRODUCT_CODE, 5);
			cartFacade.addToCart(MULTID_PRODUCT_CODE1, 1);
			cartFacade.addToCart(MULTID_PRODUCT_CODE2, 2);

			final CartData cartData = cartFacade.getSessionCart();
			csvFacade.generateCsvFromCart(null, false, cartData, writer);

			final List<OrderEntryData> unGroupedEntries = new ArrayList<OrderEntryData>();
			for (final OrderEntryData entry : cartData.getEntries())
			{
				if (Boolean.TRUE.equals(entry.getProduct().getMultidimensional()))
				{
					unGroupedEntries.addAll(entry.getEntries());
				}
				else
				{
					unGroupedEntries.add(entry);
				}
			}

			final StringTokenizer st = new StringTokenizer(writer.toString(), "\n");
			assertEquals(unGroupedEntries.size(), st.countTokens());

			int index = 0;
			while (st.hasMoreTokens())
			{
				final String oneLine = st.nextToken();
				final OrderEntryData oneEntry = unGroupedEntries.get(index);

				final String[] splitted = oneLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				assertEquals(4, splitted.length);
				assertEquals(oneEntry.getProduct().getCode(), StringEscapeUtils.unescapeCsv(splitted[0]));
				assertEquals(oneEntry.getQuantity().toString(), StringEscapeUtils.unescapeCsv(splitted[1]));
				assertEquals(oneEntry.getProduct().getName(), StringEscapeUtils.unescapeCsv(splitted[2]));
				assertEquals(oneEntry.getBasePrice().getFormattedValue(), StringEscapeUtils.unescapeCsv(splitted[3]));

				index++;
			}
		}
	}

	private void createMultiDProducts()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION);
		final UnitModel unit = unitService.getUnitForCode(UNIT_CODE);

		final VariantCategoryModel color = createVariantCategory("color", catalogVersion);

		final VariantCategoryModel size = createVariantCategory("size", catalogVersion);
		size.setSupercategories(Lists.<CategoryModel> newArrayList(color));
		getModelService().save(size);

		final VariantCategoryModel fit = createVariantCategory("fit", catalogVersion);
		fit.setSupercategories(Lists.<CategoryModel> newArrayList(size));
		getModelService().save(fit);

		final ProductModel base = createProduct("base", catalogVersion, GenericVariantProductModel._TYPECODE, unit, color, size,
				fit);

		final VariantValueCategoryModel red = createVariantValueCategory("red", color, 1, catalogVersion);
		final VariantValueCategoryModel medium = createVariantValueCategory("M", size, 1, catalogVersion);
		final VariantValueCategoryModel wide = createVariantValueCategory("wide", fit, 1, catalogVersion);
		createGenericVariantProduct(MULTID_PRODUCT_CODE1, base, catalogVersion, red, medium, wide);

		final VariantValueCategoryModel black = createVariantValueCategory("black", color, 2, catalogVersion);
		final VariantValueCategoryModel large = createVariantValueCategory("L", size, 2, catalogVersion);
		createGenericVariantProduct(MULTID_PRODUCT_CODE2, base, catalogVersion, black, large, wide);
	}
}
