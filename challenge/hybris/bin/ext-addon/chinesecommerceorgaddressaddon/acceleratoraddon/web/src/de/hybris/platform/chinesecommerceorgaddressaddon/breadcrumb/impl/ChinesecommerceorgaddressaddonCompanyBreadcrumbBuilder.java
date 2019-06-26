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

package de.hybris.platform.chinesecommerceorgaddressaddon.breadcrumb.impl;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;


/**
 * ChinesecommerceorgaddressaddonCompanyBreadcrumbBuilder implementation for account related pages
 */
public class ChinesecommerceorgaddressaddonCompanyBreadcrumbBuilder
{
	private MessageSource messageSource;
	private I18NService i18nService;

	public List<Breadcrumb> createManageUnitsBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = this.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-units/",
				messageSource.getMessage("text.company.manage.units", null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createManageUnitsDetailsBreadcrumbs(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = this.createManageUnitsBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-units/details/?unit=%s",
				urlEncode(uid)), messageSource.getMessage("text.company.manage.units.details", new Object[]
		{ uid }, "View Unit: {0} ", i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	protected List<Breadcrumb> getBreadcrumbs(final String resourceKey)
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

		if (StringUtils.isNotBlank(resourceKey))
		{
			breadcrumbs.add(new Breadcrumb("#", getMessageSource()
					.getMessage(resourceKey, null, getI18nService().getCurrentLocale()), null));
		}

		return breadcrumbs;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}
	
	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	protected String urlEncode(final String url)
	{
		Assert.notNull(url, "Parameter [url] cannot be null");
		return Functions.encodeUrl(url);
	}
}
