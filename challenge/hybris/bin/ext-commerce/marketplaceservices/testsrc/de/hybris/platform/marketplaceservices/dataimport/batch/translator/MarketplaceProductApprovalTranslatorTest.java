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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.strategies.impl.DefaultAutoApproveProductStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.coverage.CoverageInfo;
import de.hybris.platform.validation.coverage.CoverageInfo.CoveragePropertyInfoMessage;
import de.hybris.platform.validation.coverage.strategies.impl.ValidationBasedCoverageCalculationStrategy;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MarketplaceProductApprovalTranslatorTest
{
	private static final String PRODUCT_CODE = "Canon_123456";
	private static final String PRODUCT_SKU = "123456";
	private static final String ERROR_MSG1 = "errro 1";
	private static final String ERROR_MSG2 = "error 2";

	private MarketplaceProductApprovalTranslator translator;
	private Item item;
	private ProductModel product;

	@Mock
	private ValidationBasedCoverageCalculationStrategy strategy;

	@Mock
	private ModelService modelService;
	
   @Mock
	private DefaultAutoApproveProductStrategy autoApproveProductStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new MarketplaceProductApprovalTranslator();
		translator.setModelService(modelService);
		translator.setAutoApproveProductStrategy(autoApproveProductStrategy);
		product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		Mockito.when(modelService.get(item)).thenReturn(product);

	}

	@Test
	public void testValidationPass()
	{
		Mockito.when(autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product)).thenReturn(null);
		translator.performImport(PRODUCT_SKU, item);
		assertTrue(product.getSaleable());
		assertEquals(product.getApprovalStatus(), ArticleApprovalStatus.APPROVED);
		Mockito.verify(modelService).save(product);
	}


	@Test
	public void testValidationFail()
	{
		final CoveragePropertyInfoMessage msg1 = new CoveragePropertyInfoMessage("1", ERROR_MSG1);
		final CoveragePropertyInfoMessage msg2 = new CoveragePropertyInfoMessage("2", ERROR_MSG2);

		try
		{
			final CoverageInfo coverage = new CoverageInfo(0.8);
			coverage.setPropertyInfoMessages(Arrays.asList(msg1, msg2));
			Mockito.when(autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product)).thenReturn(coverage);
			translator.performImport(PRODUCT_SKU, item);
			fail("Should have thrown exception but did not!");
		}
		catch (final IllegalArgumentException e)
		{
			final String msg = e.getMessage();
			assertTrue(msg.contains(ERROR_MSG1));
			assertTrue(msg.contains(ERROR_MSG2));
		}
	}
}
