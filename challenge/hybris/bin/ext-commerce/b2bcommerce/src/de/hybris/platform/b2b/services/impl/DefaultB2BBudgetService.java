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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.StandardDateRange;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BBudgetService}
 *
 * @spring.bean b2bBudgetService
 */
public class DefaultB2BBudgetService implements B2BBudgetService<B2BBudgetModel, B2BCustomerModel>
{

	private B2BBudgetDao b2bBudgetDao;
	private PagedGenericDao<B2BBudgetModel> pagedB2BBudgetDao;
	private SessionService sessionService;
	private SearchRestrictionService searchRestrictionService;

	@Override
	public Collection<B2BBudgetModel> getCurrentBudgets(final B2BCostCenterModel costCenter)
	{
		final Set<B2BBudgetModel> b2bBudgets = new HashSet<B2BBudgetModel>();
		final Date currentDate = new Date();
		for (final B2BBudgetModel b2bBudget : costCenter.getBudgets())
		{
			final StandardDateRange dateRange = b2bBudget.getDateRange();
			if (b2bBudget.getActive().booleanValue() && dateRange.encloses(currentDate)
					&& b2bBudget.getCurrency().equals(costCenter.getCurrency()))
			{
				b2bBudgets.add(b2bBudget);
			}
		}
		return b2bBudgets;

	}

	/**
	 * @deprecated Since 6.0.
	 */
	@Deprecated
	@Override
	public boolean isBudgetExisting(final String code)
	{
		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return Boolean.valueOf(getB2BBudgetForCode(code) != null);
			}
		})).booleanValue();

	}

	@Override
	public Set<B2BBudgetModel> getB2BBudgets()
	{
		return new HashSet<B2BBudgetModel>(getB2bBudgetDao().find());
	}

	@Override
	public B2BBudgetModel getB2BBudgetForCode(final String code)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bBudgetDao().findBudgetByCode(code);
			}
		});
	}

	@Override
	public SearchPageData<B2BBudgetModel> findPagedBudgets(final PageableData pageableData)
	{
		return getPagedB2BBudgetDao().find(pageableData);
	}

	protected B2BBudgetDao getB2bBudgetDao()
	{
		return b2bBudgetDao;
	}

	@Required
	public void setB2bBudgetDao(final B2BBudgetDao b2bBudgetDao)
	{
		this.b2bBudgetDao = b2bBudgetDao;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected PagedGenericDao<B2BBudgetModel> getPagedB2BBudgetDao()
	{
		return pagedB2BBudgetDao;
	}

	@Required
	public void setPagedB2BBudgetDao(final PagedGenericDao<B2BBudgetModel> pagedB2BBudgetDao)
	{
		this.pagedB2BBudgetDao = pagedB2BBudgetDao;
	}

}
