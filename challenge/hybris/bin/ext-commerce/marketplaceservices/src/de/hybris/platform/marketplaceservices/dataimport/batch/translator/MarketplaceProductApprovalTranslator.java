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

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.strategies.AutoApproveProductStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.coverage.CoverageInfo;

import java.util.stream.Collectors;


/**
 * Marketplace translator for approving the product.
 */
public class MarketplaceProductApprovalTranslator extends AbstractSpecialValueTranslator
{
	private static final String MODEL_SERVICE = "modelService";
	private static final String AUTOAPPROVEPRODUCT_STRATEGY = "autoApproveProductStrategy";

	private ModelService modelService;
	private AutoApproveProductStrategy autoApproveProductStrategy;

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor)
	{
		setModelService((ModelService) Registry.getApplicationContext().getBean(MODEL_SERVICE));
		setAutoApproveProductStrategy((AutoApproveProductStrategy) Registry.getApplicationContext().getBean(AUTOAPPROVEPRODUCT_STRATEGY));
	}

	@Override
	public void performImport(final String code, final Item processedItem)
	{
		final ProductModel product = getModelService().get(processedItem);

		final CoverageInfo coverageInfo = getAutoApproveProductStrategy().autoApproveVariantAndApparelProduct(product);
		if (coverageInfo != null)
		{
			final String errorMsg = coverageInfo.getPropertyInfoMessages().stream()
					.map(CoverageInfo.CoveragePropertyInfoMessage::getMessage).collect(Collectors.joining(" "));
			throw new IllegalArgumentException("Cannot approve product " + product.getVendorSku() + ", error is： " + errorMsg);
		}
		else
		{
			product.setSaleable(Boolean.TRUE);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			modelService.save(product);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	public AutoApproveProductStrategy getAutoApproveProductStrategy() {
		return autoApproveProductStrategy;
	}

	public void setAutoApproveProductStrategy(AutoApproveProductStrategy autoApproveProductStrategy) {
		this.autoApproveProductStrategy = autoApproveProductStrategy;
	}

}
