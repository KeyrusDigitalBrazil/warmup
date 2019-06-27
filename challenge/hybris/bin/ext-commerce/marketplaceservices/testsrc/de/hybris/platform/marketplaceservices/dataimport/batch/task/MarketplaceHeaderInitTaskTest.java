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
package de.hybris.platform.marketplaceservices.dataimport.batch.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;
import de.hybris.platform.acceleratorservices.util.RegexParser;
import de.hybris.platform.marketplaceservices.dataimport.batch.MarketplaceBatchHeader;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.VendorProductCatalogParser;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class MarketplaceHeaderInitTaskTest
{
	private static final String LANGUAGE = "en";
	private static final String FALLBACK_LANGUAGE = "zh";
	private static final String VENDOR = "vendor1";
	private static final String TAXGROUP = "us-sales-tax-full";
	private static final String FILE_NAME = "base_product-vendor1-en-35.csv";
	private static final String VENDOR_CATALOG = "testProductCatalog";

	@Mock
	private SequenceIdParser sequenceIdParser;
	@Mock
	private RegexParser languageParser;
	@Mock
	private VendorProductCatalogParser vendorCatalogParser;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Spy
	private final MarketplaceHeaderInitTask marketplaceHeaderInitTask=new MarketplaceHeaderInitTask();
	
	private BatchHeader header;
	private File file;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void prepare() throws IOException
	{
		MockitoAnnotations.initMocks(this);
		header = new BatchHeader();
		header.setCatalog("");
		header.setLanguage(LANGUAGE);
		header.setStoreBaseDirectory(tempFolder.getRoot().getAbsolutePath());
		file = tempFolder.newFile(FILE_NAME);
		header.setFile(file);

		
		marketplaceHeaderInitTask.setFallbackLanguage(FALLBACK_LANGUAGE);
		marketplaceHeaderInitTask.setLanguageParser(languageParser);
		marketplaceHeaderInitTask.setSequenceIdParser(sequenceIdParser);
		marketplaceHeaderInitTask.setVendorCatalogParser(vendorCatalogParser);
		marketplaceHeaderInitTask.setConfigurationService(configurationService);
	}

	@Test
	public void testExecute()
	{
		Mockito.when(sequenceIdParser.getSequenceId(file)).thenReturn((long) 29);
		Mockito.when(languageParser.parse(FILE_NAME, 1)).thenReturn("en");
		Mockito.when(vendorCatalogParser.getVendorCatalog(file)).thenReturn(VENDOR_CATALOG);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString("marketplaceservices.product.taxgroup")).thenReturn(TAXGROUP);

		doReturn(VENDOR).when(marketplaceHeaderInitTask).resolveVendorCode(file);

		final BatchHeader batchHeader = marketplaceHeaderInitTask.execute(header);

		assertEquals(29, batchHeader.getSequenceId().longValue());
		assertEquals(LANGUAGE, batchHeader.getLanguage());
		assertEquals(VENDOR_CATALOG, batchHeader.getCatalog());
		assertTrue(batchHeader instanceof MarketplaceBatchHeader);
		final MarketplaceBatchHeader marketplaceBatchHeader = (MarketplaceBatchHeader) batchHeader;
		assertEquals(VENDOR, marketplaceBatchHeader.getVendorCode());
		assertEquals(TAXGROUP, marketplaceBatchHeader.getTaxGroup());
	}


}
