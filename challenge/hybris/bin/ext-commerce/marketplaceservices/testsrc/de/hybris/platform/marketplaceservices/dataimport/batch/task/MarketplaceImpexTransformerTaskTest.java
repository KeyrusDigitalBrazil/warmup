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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.marketplaceservices.dataimport.batch.MarketplaceBatchHeader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;


@UnitTest
public class MarketplaceImpexTransformerTaskTest
{
	private static final String FILE_NAME = "base_product-en-35.csv";
	private static final String LANGUAGE = "en";
	private static final String VENDOR = "vendor1";
	private static final String VENDOR_CATALOG = "vendor1Catalog";
	private static final String ENCODING = "UTF-8";

	private MarketplaceImpexTransformerTask marketplaceImpexTransformerTask;
	private MarketplaceBatchHeader marketplaceBatchHeader;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Mock
	private CleanupHelper cleanupHelper;

	@Before
	public void prepare() throws IOException
	{

		marketplaceImpexTransformerTask = new MarketplaceImpexTransformerTask();
		marketplaceImpexTransformerTask.setFieldSeparator(',');
		marketplaceImpexTransformerTask.setEncoding(ENCODING);
		marketplaceImpexTransformerTask.setLinesToSkip(0);
		marketplaceImpexTransformerTask.setCleanupHelper(cleanupHelper);

		final BatchHeader header = new BatchHeader();
		marketplaceBatchHeader = new MarketplaceBatchHeader(header);
		marketplaceBatchHeader.setCatalog(VENDOR_CATALOG);
		marketplaceBatchHeader.setEncoding(ENCODING);
		final File folder = tempFolder.newFolder(VENDOR, "processing");
		final File file = new File(folder, FILE_NAME);
		marketplaceBatchHeader.setFile(file);
		marketplaceBatchHeader.setLanguage(LANGUAGE);
		marketplaceBatchHeader.setNet(false);
		marketplaceBatchHeader.setSequenceId((long) 35);
		marketplaceBatchHeader.setStoreBaseDirectory(folder.getAbsolutePath());
		marketplaceBatchHeader.setVendorCode(VENDOR);

	}

	@Test
	public void testBuildReplacementSymbols()
	{
		final Map<String, String> symbols = new HashMap<>();
		final DefaultImpexConverter converter = new DefaultImpexConverter();
		marketplaceImpexTransformerTask.buildReplacementSymbols(symbols, marketplaceBatchHeader, converter);

		assertTrue(symbols.containsKey("$VENDOR$"));
		assertEquals(VENDOR, symbols.get("$VENDOR$"));
	}
}
