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
package de.hybris.platform.commerceservices.organization.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.organization.services.OrgUnitQuoteService;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitAuthorizationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link OrgUnitQuoteService} interface.
 */
public class DefaultOrgUnitQuoteService implements OrgUnitQuoteService
{
	private OrgUnitDao<OrgUnitModel> orgUnitDao;
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;
	private OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy;

	@Override
	public SearchPageData<QuoteModel> getQuotesForEmployee(final EmployeeModel employee, final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("employee", employee);
		validateParameterNotNullStandardMessage("pageableData", pageableData);

		getOrgUnitAuthorizationStrategy().validateViewPermission(employee);

		return orgUnitDao.findQuotesForEmployee(employee,
				getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.VIEW, employee), pageableData);
	}

	protected OrgUnitDao<OrgUnitModel> getOrgUnitDao()
	{
		return orgUnitDao;
	}

	@Required
	public void setOrgUnitDao(final OrgUnitDao<OrgUnitModel> orgUnitDao)
	{
		this.orgUnitDao = orgUnitDao;
	}

	protected QuoteStateSelectionStrategy getQuoteStateSelectionStrategy()
	{
		return quoteStateSelectionStrategy;
	}

	@Required
	public void setQuoteStateSelectionStrategy(final QuoteStateSelectionStrategy quoteStateSelectionStrategy)
	{
		this.quoteStateSelectionStrategy = quoteStateSelectionStrategy;
	}

	protected OrgUnitAuthorizationStrategy getOrgUnitAuthorizationStrategy()
	{
		return orgUnitAuthorizationStrategy;
	}

	@Required
	public void setOrgUnitAuthorizationStrategy(final OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy)
	{
		this.orgUnitAuthorizationStrategy = orgUnitAuthorizationStrategy;
	}

}
