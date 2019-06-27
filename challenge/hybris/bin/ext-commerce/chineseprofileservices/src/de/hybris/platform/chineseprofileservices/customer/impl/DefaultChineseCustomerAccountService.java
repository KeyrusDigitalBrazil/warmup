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
package de.hybris.platform.chineseprofileservices.customer.impl;

import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.chineseprofileservices.strategies.VerificationCodeGenerationStrategy;
import de.hybris.platform.chineseprofileservices.strategies.VerificationCodeSendingStrategy;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.daos.UserDao;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation for {@link ChineseCustomerAccountService}. Delivers main functionality for chinese Customer account.
 */
public class DefaultChineseCustomerAccountService extends DefaultCustomerAccountService implements ChineseCustomerAccountService
{

	private VerificationCodeGenerationStrategy verificationCodeGenerationStrategy;

	private VerificationCodeSendingStrategy verificationCodeSendingStrategy;

	private UserDao userDao;

	@Override
	public String generateVerificationCode()
	{
		return verificationCodeGenerationStrategy.generate();
	}

	@Override
	public void sendVerificationCode(final VerificationData data)
	{
		verificationCodeSendingStrategy.send(data.getMobileNumber(), data.getVerificationCode());
	}

	@Override
	public void updateMobileNumber(final CustomerModel customerModel)
	{
		getModelService().save(customerModel);
	}

	@Override
	public Optional<CustomerModel> getCustomerForMobileNumber(final String mobileNumber)
	{
		final CustomerModel customer = (CustomerModel) userDao.findUserByUID(mobileNumber);
		return Objects.nonNull(customer) ? Optional.of(customer) : Optional.empty();
	}

	protected VerificationCodeGenerationStrategy getVerificationCodeGenerationStrategy()
	{
		return verificationCodeGenerationStrategy;
	}

	@Required
	public void setVerificationCodeGenerationStrategy(final VerificationCodeGenerationStrategy verificationCodeGenerationStrategy)
	{
		this.verificationCodeGenerationStrategy = verificationCodeGenerationStrategy;
	}

	protected VerificationCodeSendingStrategy getVerificationCodeSendingStrategy()
	{
		return verificationCodeSendingStrategy;
	}

	@Required
	public void setVerificationCodeSendingStrategy(final VerificationCodeSendingStrategy verificationCodeSendingStrategy)
	{
		this.verificationCodeSendingStrategy = verificationCodeSendingStrategy;
	}

	protected UserDao getUserDao()
	{
		return userDao;
	}

	@Required
	public void setUserDao(final UserDao userDao)
	{
		this.userDao = userDao;
	}

}
