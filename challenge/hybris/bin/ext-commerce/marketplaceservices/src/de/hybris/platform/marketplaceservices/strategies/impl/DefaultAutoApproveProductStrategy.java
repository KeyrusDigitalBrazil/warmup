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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.strategies.AutoApproveProductStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.validation.coverage.CoverageInfo;
import de.hybris.platform.validation.coverage.strategies.impl.ValidationBasedCoverageCalculationStrategy;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;


public class DefaultAutoApproveProductStrategy implements AutoApproveProductStrategy
{

	private static final String PRODUCT_COVERAGE_INDEX_KEY = "marketplaceservices.default.product.coverage.index";
	private static final String APPAREL_PRODUCT_CANONICAL_NAME = "apparel.product.canonical.name";

	private ModelService modelService;

	private ValidationBasedCoverageCalculationStrategy validationCoverageCalculationStrategy;

	@Override
	public CoverageInfo autoApproveVariantAndApparelProduct(final ProductModel product)
	{
		final boolean isVariantProduct = product instanceof VariantProductModel;
		final boolean isApparelProduct = isApparelProduct(product);
		if (isVariantProduct || isApparelProduct)
		{
			return null;
		}

		final CoverageInfo coverageInfo = getValidationCoverageCalculationStrategy().calculate(product);
		final BigDecimal defaultThresholdIndex = getDefaultThresholdIndex();
		if (coverageInfo == null || BigDecimal.valueOf(coverageInfo.getCoverageIndex()).compareTo(defaultThresholdIndex) >= 0)
		{
			return null;
		}
		return coverageInfo;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public ValidationBasedCoverageCalculationStrategy getValidationCoverageCalculationStrategy()
	{
		return validationCoverageCalculationStrategy;
	}

	public void setValidationCoverageCalculationStrategy(
			final ValidationBasedCoverageCalculationStrategy validationCoverageCalculationStrategy)
	{
		this.validationCoverageCalculationStrategy = validationCoverageCalculationStrategy;
	}
	
	protected boolean isApparelProduct(final ProductModel product) {
		return Config.getString(APPAREL_PRODUCT_CANONICAL_NAME, StringUtils.EMPTY) //NOSONAR
				.equals(product.getClass().getName()); //NOSONAR
	}
	
	protected BigDecimal getDefaultThresholdIndex() {
		return BigDecimal.valueOf(Config.getDouble(PRODUCT_COVERAGE_INDEX_KEY, 1.0));
	}
}
