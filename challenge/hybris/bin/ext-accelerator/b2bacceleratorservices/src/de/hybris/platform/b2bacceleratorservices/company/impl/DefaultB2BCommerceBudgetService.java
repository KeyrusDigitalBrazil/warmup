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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BBudgetService;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceBudgetService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * @deprecated Since 6.0. Use {@link DefaultB2BBudgetService} instead.
 *             <p/>
 *             Default implementation of {@link B2BCommerceBudgetService }
 *
 */
@Deprecated
public class DefaultB2BCommerceBudgetService extends DefaultCompanyB2BCommerceService implements B2BCommerceBudgetService
{
	@Override
	public <T extends B2BBudgetModel> T getBudgetModelForCode(final String code)
	{
		return (T) getB2BBudgetService().getB2BBudgetForCode(code);
	}

	@Override
	public SearchPageData<B2BBudgetModel> findPagedBudgets(final PageableData pageableData)
	{
		return getPagedB2BBudgetDao().find(pageableData);
	}
}
