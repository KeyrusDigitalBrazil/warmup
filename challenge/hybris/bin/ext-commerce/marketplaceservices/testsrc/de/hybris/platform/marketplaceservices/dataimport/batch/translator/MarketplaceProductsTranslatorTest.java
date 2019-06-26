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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.DescriptorParams;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.product.ProductService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;


@UnitTest
public class MarketplaceProductsTranslatorTest
{
	private static final String VENDOR_CODE = "Canon";
	private static final String VENDOR_CATALOG = "Canon_productCatalog";
	private static final String VERSION = "Online";

	private static final String PRODUCT_CODE_1 = "Product1";
	private static final String PRODUCT_CODE_2 = "Product2";
	private static final String IMPORT_PRODUCTS = String.join(",", PRODUCT_CODE_1, PRODUCT_CODE_2);

	@Spy
	private final MarketplaceProductsTranslator translator = new MarketplaceProductsTranslator();

	private TestDescriptorParams params;
	private Item item;
	private CatalogVersionModel catalogVersion;
	private ProductModel product1, product2;
	private Product productItem1, productItem2;

	@Mock
	private VendorService vendorService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private VendorModel vendor;

	@Mock
	private StandardColumnDescriptor descriptor;

	@Mock
	private ProductService productService;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private ProductManager productManager;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(translator.getApplicationContext()).thenReturn(applicationContext);
		Mockito.when(translator.getProductManager()).thenReturn(productManager);
		translator.init(descriptor);
		translator.setProductService(productService);
		translator.setCatalogVersionService(catalogVersionService);
		translator.setVendorService(vendorService);

		params = new TestDescriptorParams(
				ImmutableMap.of("vendor", VENDOR_CODE, "vendorCatalog", VENDOR_CATALOG, "version", VERSION));

		product1 = new ProductModel();
		product1.setCode(PRODUCT_CODE_1);

		product2 = new ProductModel();
		product2.setCode(PRODUCT_CODE_2);

		productItem1 = new Product();
		productItem2 = new Product();


		Mockito.when(descriptor.getDescriptorData()).thenReturn(params);

		Mockito.when(applicationContext.getBean("vendorService")).thenReturn(vendorService);
		Mockito.when(applicationContext.getBean("productService")).thenReturn(productService);
		Mockito.when(applicationContext.getBean("catalogVersionService")).thenReturn(catalogVersionService);

		Mockito.when(productManager.getProductByPK(product1.getPk())).thenReturn(productItem1);
		Mockito.when(productManager.getProductByPK(product2.getPk())).thenReturn(productItem2);

		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.of(vendor));
		Mockito.when(catalogVersionService.getCatalogVersion(VENDOR_CATALOG, VERSION)).thenReturn(catalogVersion);
		Mockito.when(productService.getProductForCode(catalogVersion, VENDOR_CODE + "_" + PRODUCT_CODE_1)).thenReturn(product1);
		Mockito.when(productService.getProductForCode(catalogVersion, VENDOR_CODE + "_" + PRODUCT_CODE_2)).thenReturn(product2);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoProduct()
	{
		translator.importValue("", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoVendor()
	{
		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.empty());
		translator.importValue(IMPORT_PRODUCTS, item);
	}

	@Test
	public void testImportValueWithValidProducts()
	{
		final List<Product> expectResult = new ArrayList<>(Arrays.asList(productItem1, productItem2));

		@SuppressWarnings("unchecked")
		final
		List<Product> acturalResult = (List<Product>) translator.importValue(IMPORT_PRODUCTS, item);

		assertEquals(expectResult.size(), acturalResult.size());
	}

	private static final class TestDescriptorParams extends DescriptorParams
	{
		public TestDescriptorParams(Map<String, String> m)
		{
			this.addAllModifier(m);
		}
	}
}
