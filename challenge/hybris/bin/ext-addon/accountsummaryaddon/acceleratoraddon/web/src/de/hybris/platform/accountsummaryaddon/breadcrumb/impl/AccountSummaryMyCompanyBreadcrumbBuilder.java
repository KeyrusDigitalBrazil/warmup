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
package de.hybris.platform.accountsummaryaddon.breadcrumb.impl;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * Builds a breadcrumb for the account summary section.
 */
public class AccountSummaryMyCompanyBreadcrumbBuilder
{

	private static final String ACCOUNT_SUMMARY_UNIT_TREE_PATH = "/my-company/organization-management/accountsummary-unit/";
	private static final String ACCOUNT_SUMMARY_UNIT_DETAIL_PATH = "/my-company/organization-management/accountsummary-unit/details/?unit=%s";
	private static final String TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS = "text.company.accountsummary.details";
	private static final String TEXT_COMPANY_ACCOUNTSUMMARY = "text.company.accountsummary";

	private MessageSource messageSource;
	private I18NService i18nService;

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	public List<Breadcrumb> createAccountSummaryBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(new Breadcrumb(ACCOUNT_SUMMARY_UNIT_TREE_PATH, getMessageSource().getMessage(TEXT_COMPANY_ACCOUNTSUMMARY,
				null, getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createAccountSummaryDetailsBreadcrumbs(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = this.createAccountSummaryBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(String.format(ACCOUNT_SUMMARY_UNIT_DETAIL_PATH, Functions.encodeUrl(uid)), getMessageSource()
				.getMessage(TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS, new Object[]
				{ uid }, "View Unit: {0} ", getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}

}