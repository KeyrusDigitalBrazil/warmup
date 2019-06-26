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
package de.hybris.platform.cissapdigitalpayment.retention.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionRequestList;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentCustomerAccountService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.retention.hook.ItemCleanupHook;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;


/**
 *
 */
public class CisSapDigitalPaymentCustomerCleanupHook implements ItemCleanupHook<CustomerModel>
{

	private static final Logger LOG = Logger.getLogger(CisSapDigitalPaymentCustomerCleanupHook.class);

	private SapDigitalPaymentCustomerAccountService sapDigitalPaymentCustomerAccountService;

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;



	@Override
	public void cleanupRelatedObjects(final CustomerModel customerModel)
	{

		validateParameterNotNullStandardMessage("customerModel", customerModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Cleaning up CreditCardPaymentInfo  objects for Customer from SAP Digital Payments");
		}

		final List<CreditCardPaymentInfoModel> creditCardPaymentInfoList = customerModel.getPaymentInfos().stream()
				.filter(CreditCardPaymentInfoModel.class::isInstance).map(CreditCardPaymentInfoModel.class::cast)
				.collect(Collectors.toList());

		final CisSapDigitalPaymentCardDeletionRequestList deleteCardReqList = getSapDigitalPaymentCustomerAccountService()
				.createDeleteCardRequestList(creditCardPaymentInfoList);
		try
		{
			getCisSapDigitalPaymentService().deleteCard(deleteCardReqList, getSapDigitalPaymentConfigurationStrategy()).first();
		}
		catch (final NoSuchElementException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Empty response from SAP Digital Payments for delete card request" + e);
			}
			LOG.error("Empty response from SAP Digital Payments for delete card request" + e.getMessage());
		}

	}

	/**
	 * @return the sapDigitalPaymentCustomerAccountService
	 */
	public SapDigitalPaymentCustomerAccountService getSapDigitalPaymentCustomerAccountService()
	{
		return sapDigitalPaymentCustomerAccountService;
	}

	/**
	 * @param sapDigitalPaymentCustomerAccountService
	 *           the sapDigitalPaymentCustomerAccountService to set
	 */
	public void setSapDigitalPaymentCustomerAccountService(
			final SapDigitalPaymentCustomerAccountService sapDigitalPaymentCustomerAccountService)
	{
		this.sapDigitalPaymentCustomerAccountService = sapDigitalPaymentCustomerAccountService;
	}

	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}

	/**
	 * @return the sapDigitalPaymentConfigurationStrategy
	 */
	public SapDigitalPaymentConfigurationStrategy getSapDigitalPaymentConfigurationStrategy()
	{
		return sapDigitalPaymentConfigurationStrategy;
	}

	/**
	 * @param sapDigitalPaymentConfigurationStrategy
	 *           the sapDigitalPaymentConfigurationStrategy to set
	 */
	public void setSapDigitalPaymentConfigurationStrategy(
			final SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy)
	{
		this.sapDigitalPaymentConfigurationStrategy = sapDigitalPaymentConfigurationStrategy;
	}



}
