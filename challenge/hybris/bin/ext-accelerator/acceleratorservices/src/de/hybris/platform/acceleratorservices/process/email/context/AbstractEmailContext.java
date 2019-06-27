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
package de.hybris.platform.acceleratorservices.process.email.context;

import de.hybris.platform.acceleratorservices.document.context.AbstractHybrisVelocityContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Utilities;

import java.util.Locale;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Required;


/**
 * The email velocity context.
 */
public abstract class AbstractEmailContext<T extends BusinessProcessModel> extends AbstractHybrisVelocityContext
{
	public static final String TITLE = "title";
	public static final String DISPLAY_NAME = "displayName";
	public static final String EMAIL = "email";
	public static final String FROM_EMAIL = "fromEmail";
	public static final String FROM_DISPLAY_NAME = "fromDisplayName";
	public static final String EMAIL_LANGUAGE = "email_language";
	public static final String DATE_TOOL = "dateTool";

	private CustomerEmailResolutionService customerEmailResolutionService;
	private ConfigurationService configurationService;


	protected CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	@Required
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public String getTitle()
	{
		return (String) get(TITLE);
	}

	public String getDisplayName()
	{
		return (String) get(DISPLAY_NAME);
	}

	public String getEmail()
	{
		return (String) get(EMAIL);
	}

	public String getToEmail()
	{
		return getEmail();
	}

	public String getToDisplayName()
	{
		return getDisplayName();
	}

	public String getFromEmail()
	{
		return (String) get(FROM_EMAIL);
	}

	public String getFromDisplayName()
	{
		return (String) get(FROM_DISPLAY_NAME);
	}

	public LanguageModel getEmailLanguage()
	{
		return (LanguageModel) get(EMAIL_LANGUAGE);
	}

	public void init(final T businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.setBaseSite(getSite(businessProcessModel));
		super.init(businessProcessModel, emailPageModel);

		put(FROM_EMAIL, emailPageModel.getFromEmail());

		final LanguageModel language = getEmailLanguage(businessProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
			final String[] loc = Utilities.parseLocaleCodes(language.getIsocode());
			String fromName = emailPageModel.getFromName(new Locale(loc[0], loc[1], loc[2]));
			if (fromName == null)
			{
				fromName = emailPageModel.getFromName();
			}
			put(FROM_DISPLAY_NAME, fromName);
		}
		else
		{
			put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
		}

		final CustomerModel customerModel = getCustomer(businessProcessModel);
		if (customerModel != null)
		{
			put(TITLE, (customerModel.getTitle() != null && customerModel.getTitle().getName() != null)
					? customerModel.getTitle().getName() : "");
			put(DISPLAY_NAME, customerModel.getDisplayName());
			put(EMAIL, getCustomerEmailResolutionService().getEmailForCustomer(customerModel));
		}

		put(DATE_TOOL, new DateTool());
	}

	protected abstract BaseSiteModel getSite(final T businessProcessModel);

	protected abstract CustomerModel getCustomer(final T businessProcessModel);

	protected abstract LanguageModel getEmailLanguage(final T businessProcessModel);

}
