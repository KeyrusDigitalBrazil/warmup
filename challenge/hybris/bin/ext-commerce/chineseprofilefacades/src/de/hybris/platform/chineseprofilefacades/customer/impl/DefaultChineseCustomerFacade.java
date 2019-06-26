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
package de.hybris.platform.chineseprofilefacades.customer.impl;

import de.hybris.platform.chineseprofilefacades.customer.ChineseCustomerFacade;
import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.service.hooks.CustomerSettingsChangedHook;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 *  Implementation for {@link ChineseCustomerFacade}. Its main purpose is to retrieve chinese customer related DTOs using existing services.
 */
public class DefaultChineseCustomerFacade extends DefaultCustomerFacade implements ChineseCustomerFacade
{

	private ChineseCustomerAccountService chineseCustomerAccountService;

	private ConfigurationService configurationService;

	private List<CustomerSettingsChangedHook> customerSettingsChangedHooks;
	@Override
	public void saveEmailLanguageForCurrentUser(final String languageISO)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		if (currentUser != null && !getUserService().isAnonymousUser(currentUser))
		{
			currentUser.setEmailLanguage(languageISO);
		}
	}

	@Override
	public String generateVerificationCode()
	{
		return chineseCustomerAccountService.generateVerificationCode();
	}

	@Override
	public void sendVerificationCode(final VerificationData data)
	{
		chineseCustomerAccountService.sendVerificationCode(data);
	}

	@Override
	public void saveVerificationCodeInSession(final VerificationData data, final String name)
	{
		data.setTime(new Date());
		getSessionService().setAttribute(name, data);
	}

	@Override
	public void removeVerificationCodeFromSession(final String name)
	{
		getSessionService().removeAttribute(name);
	}

	@Override
	public void saveMobileNumber(final VerificationData data)
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		customer.setMobileNumber(data.getMobileNumber());
		chineseCustomerAccountService.updateMobileNumber(customer);
	}

	@Override
	public int getVerificationCodeTimeout(final String key)
	{
		return configurationService.getConfiguration().getInt(key);
	}

	@Override
	public boolean isMobileNumberUnique(final String mobileNumber)
	{
		return !chineseCustomerAccountService.getCustomerForMobileNumber(mobileNumber).isPresent();
	}

	@Override
	public void updateProfile(final CustomerData customerData) throws DuplicateUidException
	{
		super.updateProfile(customerData);
		final CustomerModel customer = getCurrentSessionCustomer();
		customer.setMobileNumber(customerData.getMobileNumber());
		chineseCustomerAccountService.updateMobileNumber(customer);
	}

	@Override
	public void unbindMobileNumber()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		final Set<NotificationChannel> channels = customer.getNotificationChannels();
		if (customer.getNotificationChannels().contains(NotificationChannel.SMS))
		{
			customer.setNotificationChannels(channels.stream().filter(c -> c != NotificationChannel.SMS)
					.collect(Collectors.toSet()));
		}
		customer.setMobileNumber(StringUtils.EMPTY);
		chineseCustomerAccountService.updateMobileNumber(customer);
		
		if (getCustomerSettingsChangedHooks() != null)
		{
			for (final CustomerSettingsChangedHook customerSettingsChangedHook : getCustomerSettingsChangedHooks())
			{
				customerSettingsChangedHook.afterUnbindMobileNumber(customer);
			}
		}

	}
	
	protected ChineseCustomerAccountService getChineseCustomerAccountService()
	{
		return chineseCustomerAccountService;
	}

	@Required
	public void setChineseCustomerAccountService(final ChineseCustomerAccountService chineseCustomerAccountService)
	{
		this.chineseCustomerAccountService = chineseCustomerAccountService;
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

	protected List<CustomerSettingsChangedHook> getCustomerSettingsChangedHooks()
	{
		return customerSettingsChangedHooks;
	}

	@Required
	public void setCustomerSettingsChangedHooks(final List<CustomerSettingsChangedHook> customerSettingsChangedHooks)
	{
		this.customerSettingsChangedHooks = customerSettingsChangedHooks;
	}

}
