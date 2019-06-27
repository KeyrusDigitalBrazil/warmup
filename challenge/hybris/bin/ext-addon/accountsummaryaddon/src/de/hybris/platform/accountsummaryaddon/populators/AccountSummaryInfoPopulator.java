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
package de.hybris.platform.accountsummaryaddon.populators;

import de.hybris.platform.accountsummaryaddon.data.AccountSummaryInfoData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


public class AccountSummaryInfoPopulator implements Populator<B2BUnitModel, AccountSummaryInfoData>
{

	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	private Converter<B2BUnitModel, B2BAmountBalanceData> b2bAmountBalanceConverter;
	protected UserService userService;
	private I18NService i18NService;
	private AmountFormatter amountFormatter;

	private static final String COMMA_SEPERATOR = ",";

	@Override
	public void populate(final B2BUnitModel source, final AccountSummaryInfoData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final B2BUnitData b2bUnitData = getB2bUnitConverter().convert(source);
		target.setB2bUnitData(b2bUnitData);
		target.setAmountBalanceData(getB2bAmountBalanceConverter().convert(source));
		target.setBillingAddress(getDefaultAddress(b2bUnitData));
		target.setFormattedCreditLimit(getFormattedCreditLimit(source.getCreditLimit()));

		setAccountManagerDetails(source, target);
	}

	protected void setAccountManagerDetails(final B2BUnitModel b2bUnitModel, final AccountSummaryInfoData target)
	{
		String accountManagerName = StringUtils.EMPTY;
		final StringBuilder emailStringBuilder = new StringBuilder();
		final EmployeeModel employeeModel = b2bUnitModel.getAccountManager();

		if (employeeModel != null)
		{
			final UserModel accountManager = userService.getUserForUID(employeeModel.getUid());

			if (accountManager != null)
			{
				populateEmail(emailStringBuilder, accountManager);
				accountManagerName = accountManager.getDisplayName();
			}
		}

		target.setAccountManagerName(accountManagerName);
		target.setAccountManagerEmail(emailStringBuilder.toString());
	}

	protected void populateEmail(final StringBuilder emailStringBuilder, final UserModel accountManager)
	{
		final Collection<AddressModel> userAddresses = accountManager.getAddresses();

		if (userAddresses != null)
		{
			for (final AddressModel userAddress : userAddresses)
			{
				if (StringUtils.isEmpty(emailStringBuilder.toString()))
				{
					emailStringBuilder.append(userAddress.getEmail());
				}
				else
				{
					emailStringBuilder.append(COMMA_SEPERATOR).append(userAddress.getEmail());
				}
			}
		}
	}

	protected String getFormattedCreditLimit(final B2BCreditLimitModel creditLimit)
	{
		return creditLimit != null ? getAmountFormatter().formatAmount(creditLimit.getAmount(), creditLimit.getCurrency(),
				getI18NService().getCurrentLocale()) : StringUtils.EMPTY;
	}

	protected AddressData getDefaultAddress(final B2BUnitData b2bUnitData)
	{
		AddressData billingAddress = new AddressData();

		if (b2bUnitData.getAddresses() != null)
		{
			final List<AddressData> addresses = b2bUnitData.getAddresses();
			for (final AddressData addressData : addresses)
			{
				if (addressData.isShippingAddress())
				{
					billingAddress = addressData;
				}

				if (addressData.isBillingAddress())
				{
					billingAddress = addressData;
					break;
				}
			}
		}
		else
		{
			billingAddress = null;
		}
		return billingAddress;
	}

	/**
	 * @return the b2bUnitConverter
	 */
	public Converter<B2BUnitModel, B2BUnitData> getB2bUnitConverter()
	{
		return b2bUnitConverter;
	}

	/**
	 * @param b2bUnitConverter
	 *           the b2bUnitConverter to set
	 */
	public void setB2bUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		this.b2bUnitConverter = b2bUnitConverter;
	}

	/**
	 * @return the b2bAmountBalanceConverter
	 */
	public Converter<B2BUnitModel, B2BAmountBalanceData> getB2bAmountBalanceConverter()
	{
		return b2bAmountBalanceConverter;
	}

	/**
	 * @param b2bAmountBalanceConverter
	 *           the b2bAmountBalanceConverter to set
	 */
	public void setB2bAmountBalanceConverter(final Converter<B2BUnitModel, B2BAmountBalanceData> b2bAmountBalanceConverter)
	{
		this.b2bAmountBalanceConverter = b2bAmountBalanceConverter;
	}

	/**
	 * @return the amountFormatter
	 */
	public AmountFormatter getAmountFormatter()
	{
		return amountFormatter;
	}

	/**
	 * @param amountFormatter
	 *           the amountFormatter to set
	 */
	public void setAmountFormatter(final AmountFormatter amountFormatter)
	{
		this.amountFormatter = amountFormatter;
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
