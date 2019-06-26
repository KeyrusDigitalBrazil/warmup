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
package de.hybris.platform.sap.sapinvoiceaddon.breadcrumb.impl;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.sap.sapinvoiceaddon.constants.SapinvoiceaddonConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

/**
 * Builds a breadcrumb for the Invoice Details section.
 */
public class InvoiceDetailsMyCompanyBreadcrumbBuilder {

	private MessageSource messageSource;
	private I18NService i18nService;
	private ConfigurationService configurationService;

	protected I18NService getI18nService() {
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService) {
		this.i18nService = i18nService;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}

	@Required
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(
			final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public List<Breadcrumb> createAccountSummaryBreadcrumbs() {
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs
				.add(new Breadcrumb(
						SapinvoiceaddonConstants.ACCOUNT_STATUS_PATH_UNIT,
						getMessageSource()
								.getMessage(
										SapinvoiceaddonConstants.TEXT_COMPANY_ACCOUNTSUMMARY,
										null,
										getI18nService().getCurrentLocale()),
						null));
	
		return breadcrumbs;
	}

	protected List<Breadcrumb> createOrganizationManagementBreadcrumbs() {
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(new Breadcrumb(SapinvoiceaddonConstants.MY_COMPANY_URL,
				getMessageSource().getMessage(
						SapinvoiceaddonConstants.MY_COMPANY_MESSAGE_KEY, null,
						getI18nService().getCurrentLocale()), null));

		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/",
				messageSource.getMessage("text.company.organizationManagement",
						null, i18nService.getCurrentLocale()), null));
		return breadcrumbs;
	}

	public List<Breadcrumb> createInvoiceDetailsBreadcrumbs(
			final String documentNumber, final String b2bUnitUID) {
		final List<Breadcrumb> breadcrumbs = this
				.createAccountSummaryBreadcrumbs();

		if (b2bUnitUID != null) {
			breadcrumbs
					.add(new Breadcrumb(
							String.format(
									SapinvoiceaddonConstants.ACCOUNT_STATUS_DOCUMENTS_UNIT_PATH,
									b2bUnitUID),
							getMessageSource()
									.getMessage(
											SapinvoiceaddonConstants.TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS,
											null,
											getI18nService()
													.getCurrentLocale()),
							null));
		} else {
			breadcrumbs
					.add(new Breadcrumb(
							SapinvoiceaddonConstants.ACCOUNT_STATUS_PATH_UNIT,
							getMessageSource()
									.getMessage(
											SapinvoiceaddonConstants.TEXT_COMPANY_ACCOUNTSUMMARY,
											null,
											getI18nService()
													.getCurrentLocale()),
							null));
		}
		
	
		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
				SapinvoiceaddonConstants.ACCOUNT_INVOICE_DETAILS,
				new Object[] { documentNumber }, "Invoice {0}",
				getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}

}
