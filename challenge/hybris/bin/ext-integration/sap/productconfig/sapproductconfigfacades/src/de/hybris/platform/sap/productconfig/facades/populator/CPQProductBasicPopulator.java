/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;


/**
 * For CPQ, variant base products are purchasable (if approved). This is the only attribute that is touched here.
 *
 * @param <SOURCE>
 *           product model
 * @param <TARGET>
 *           product DTO
 */
public class CPQProductBasicPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>

{
	private ConfigurationVariantUtil configurationVariantUtil;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData)
	{
		if (isCPQBaseProduct(productModel))
		{
			populatePurchasable(productModel, productData);
		}
	}

	protected void populatePurchasable(final SOURCE productModel, final TARGET productData)
	{
		productData.setPurchasable(Boolean.valueOf(isApproved(productModel)));
	}

	protected boolean isCPQBaseProduct(final ProductModel productModel)
	{
		return getConfigurationVariantUtil().isCPQBaseProduct(productModel);
	}

	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	/**
	 * @param configurationVariantUtil
	 */
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}

	protected boolean isApproved(final SOURCE productModel)
	{
		final ArticleApprovalStatus approvalStatus = productModel.getApprovalStatus();
		return ArticleApprovalStatus.APPROVED.equals(approvalStatus);
	}


}
