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
package de.hybris.platform.marketplaceservices.dataimport.batch.util;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class VendorProductCatalogParserTest
{
	private static final String FILE_NAME = "base_product-en-35.csv";

	@Mock
	private VendorDao vendorDao;

	private VendorProductCatalogParser vendorProductCatalogParser;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private File file;

	@Before
	public void prepare() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		vendorProductCatalogParser = new VendorProductCatalogParser();
		vendorProductCatalogParser.setVendorDao(vendorDao);

		final File folder = tempFolder.newFolder("vendor1", "processing");
		file = new File(folder, FILE_NAME);
	}

	@Test
	public void testGetVendorCatalogSuccess() throws IOException
	{
		final VendorModel vendorModel = new VendorModel();
		vendorModel.setCode("vendor1");
		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId("vendor1ProductCatalog");
		vendorModel.setCatalog(catalogModel);
		Mockito.when(vendorDao.findVendorByCode("vendor1")).thenReturn(Optional.of(vendorModel));

		final String catalog = vendorProductCatalogParser.getVendorCatalog(file);

		assertEquals("vendor1ProductCatalog", catalog);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetVendorCatalogFail() throws IOException
	{
		final VendorModel vendorModel = new VendorModel();
		vendorModel.setCode("vendor1");
		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId("vendor1ProductCatalog");
		vendorModel.setCatalog(catalogModel);
		Mockito.when(vendorDao.findVendorByCode("vendor1")).thenReturn(Optional.empty());

		vendorProductCatalogParser.getVendorCatalog(file);

	}
}
