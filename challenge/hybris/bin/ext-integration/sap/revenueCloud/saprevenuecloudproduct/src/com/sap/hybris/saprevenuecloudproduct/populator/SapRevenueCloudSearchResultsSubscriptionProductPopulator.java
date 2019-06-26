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
package com.sap.hybris.saprevenuecloudproduct.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.subscriptionfacades.converters.populator.SearchResultSubscriptionProductPopulator;


/**
 * SOLR Populator for subscription-capable {@link ProductModel}.
 *
 * @param <SOURCE>
 *           source class
 * @param <TARGET>
 *           target class
 */
public class SapRevenueCloudSearchResultsSubscriptionProductPopulator<SOURCE extends SearchResultValueData, TARGET extends ProductData>
		extends SearchResultSubscriptionProductPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		super.populate(source, target);
		target.setSubscriptionCode(getValue(source, "subscriptionCode"));
	}

}
