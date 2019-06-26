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
 package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.coverage.CoverageInfo;
import de.hybris.platform.validation.coverage.strategies.impl.ValidationBasedCoverageCalculationStrategy;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yacceleratorcore.model.ApparelProductModel;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class DefaultAutoApproveProductStrategyTest
{
	@Mock
	private ModelService modelService;

	@Mock
	private ValidationBasedCoverageCalculationStrategy validationCoverageCalculationStrategy;

	@Spy
	private final DefaultAutoApproveProductStrategy autoApproveProductStrategy = new DefaultAutoApproveProductStrategy();

	private static final Double defaultConverage = 0.9;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		autoApproveProductStrategy.setModelService(modelService);
		autoApproveProductStrategy.setValidationCoverageCalculationStrategy(validationCoverageCalculationStrategy);

	}

	@Test
	public void testAutoApproveVariantAndApparelProductWithVariantProduct()
	{
		final VariantProductModel product = new VariantProductModel();
		Mockito.when(autoApproveProductStrategy.isApparelProduct(product)).thenReturn(false);
		final CoverageInfo converageInfo = autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product);
		Assert.assertNull(converageInfo);

	}

	@Test
	public void testAutoApproveVariantAndApparelProductWithApparelProduct()
	{
		final ApparelProductModel product = new ApparelProductModel();
		Mockito.when(autoApproveProductStrategy.isApparelProduct(product)).thenReturn(true);
		final CoverageInfo converageInfo = autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product);
		Assert.assertNull(converageInfo);

	}

	@Test
	public void testAutoApproveVariantAndApparelProductWithNullCoverage()
	{
		final ProductModel product = new ProductModel();
		Mockito.when(autoApproveProductStrategy.isApparelProduct(product)).thenReturn(false);
		Mockito.when(validationCoverageCalculationStrategy.calculate(product)).thenReturn(null);
		Mockito.when(autoApproveProductStrategy.getDefaultThresholdIndex()).thenReturn(new BigDecimal(defaultConverage));

		final CoverageInfo coverageInfo = autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product);
		Assert.assertNull(coverageInfo);

	}

	@Test
	public void testAutoApproveVariantAndApparelProductWithCoverage()
	{
		final ProductModel product = new ProductModel();
		final CoverageInfo coverage = new CoverageInfo(1.0);
		Mockito.when(autoApproveProductStrategy.isApparelProduct(product)).thenReturn(false);
		Mockito.when(validationCoverageCalculationStrategy.calculate(product)).thenReturn(coverage);
		Mockito.when(autoApproveProductStrategy.getDefaultThresholdIndex()).thenReturn(new BigDecimal(defaultConverage));

		final CoverageInfo coverageInfo = autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product);
		Assert.assertNull(coverageInfo);

	}

	@Test
	public void testAutoApproveVariantAndApparelProductWithLowCoverage()
	{
		final ProductModel product = new ProductModel();
		final CoverageInfo coverage = new CoverageInfo(0.8);
		Mockito.when(autoApproveProductStrategy.isApparelProduct(product)).thenReturn(false);
		Mockito.when(validationCoverageCalculationStrategy.calculate(product)).thenReturn(coverage);
		Mockito.when(autoApproveProductStrategy.getDefaultThresholdIndex()).thenReturn(new BigDecimal(0.9));

		final CoverageInfo coverageInfo = autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product);
		Assert.assertEquals(coverage, coverageInfo);

	}

}
