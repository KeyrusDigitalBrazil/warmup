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
package de.hybris.platform.cissapdigitalpayment.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRegistrationUrlResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentClientModel;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hybris.charon.Charon;

import rx.Observable;


/**
 * Default implementation of {@link CisSapDigitalPaymentService}
 */
public class DefaultCisSapDigitalPaymentService implements CisSapDigitalPaymentService
{

	private static final Logger LOG = Logger.getLogger(DefaultCisSapDigitalPaymentService.class);

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;

	private static Map<String, String> pollCardStatusMap;

	private long defaultPollCardDelay;



	/**
	 * Default implementation for CisClientService interface method
	 *
	 * @param xCisClientRef
	 *           - xCisClientRef
	 * @param tenantId
	 *           - tenant ID
	 *
	 * @return boolean
	 *
	 */
	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return true;
	}

	/**
	 * Retrive the card registration URL from SAP Digital payments
	 *
	 * @return CisSapDigitalPaymentRegistrationUrlResult - Registration URL response from digital payments wrapped with
	 *         Observable<>
	 * @throws TimeoutException
	 */
	@Override
	public Observable<CisSapDigitalPaymentRegistrationUrlResult> getRegistrationUrl() throws TimeoutException
	{
		final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig = getSapDigitalPaymentConfigurationStrategy()
				.getSapDigitalPaymentConfiguration();
		return getCisSapDigitalPaymentClient(sapDigitalPaymentConfig).getRegistrationUrl().map(registratioUrlResp -> {
			logSuccess("successfully received registarion URL response" + registratioUrlResp.toString());
			return registratioUrlResp;
		}).doOnError(DefaultCisSapDigitalPaymentService::logError);
	}



	/**
	 * Polling the card details entered at SAP Digital payments screen. This method is invoked by a process that
	 * continuously polls the card with the session ID received during the card registration request. Polling will continue
	 * until it receives Cancelled, Timeout or Success status.
	 *
	 * @param sessionId
	 *           - Session IDreceived during the card registration
	 * @return CisSapDigitalPaymentPollRegisteredCardResult - Poll card response wrapped in Observable<>
	 * @param sapDigiPayConfig
	 *           - SAP Digital payment configuration
	 *
	 */
	@Override
	public Observable<CisSapDigitalPaymentPollRegisteredCardResult> pollRegisteredCard(final String sessionId,
			final SAPDigitalPaymentConfigurationModel sapDigiPayConfig)
	{
		return getCisSapDigitalPaymentClient(sapDigiPayConfig).pollRegisteredCard(sessionId).map(registeredCard -> {
			logSuccess("Successfully poll the registed card");
			return registeredCard;
		}).doOnError(DefaultCisSapDigitalPaymentService::logError)
				.repeatWhen(completed -> completed.delay(configurePollingCardDelay(sapDigiPayConfig), TimeUnit.MILLISECONDS))
				.takeUntil(DefaultCisSapDigitalPaymentService::checkPollCardTransactionResult);
	}

	/**
	 *
	 * Used to configure the polling card interval. Read from the SAP Digital payment configuration. If no values are set,
	 * read the default value injected to defaultPollCardDelay property.
	 *
	 * @param sapDigiPayConfig
	 *           - SAP Digital payment configuration
	 *
	 * @return delayInterval - Delay interval for polling the registered card.
	 *
	 */
	private long configurePollingCardDelay(final SAPDigitalPaymentConfigurationModel sapDigiPayConfig)
	{
		validateParameterNotNull(sapDigiPayConfig, "SAP Digital payment configutation cannot be null");
		return sapDigiPayConfig.getPollCardDelay() != null ? sapDigiPayConfig.getPollCardDelay().longValue()
				: getDefaultPollCardDelay();
	}

	/**
	 * Make a payment authorization call to SAP Digital payments
	 *
	 * @param authorizationRequests
	 *           - List of CisSapDigitalPaymentAuthorizationRequest object
	 * @return CisSapDigitalPaymentAuthorizationResultList - Payment authorization response wrapped around Observable<>
	 *
	 */
	@Override
	public Observable<CisSapDigitalPaymentAuthorizationResultList> authorizePayment(
			final CisSapDigitalPaymentAuthorizationRequestList authorizationRequests)
	{
		final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig = getSapDigitalPaymentConfigurationStrategy()
				.getSapDigitalPaymentConfiguration();
		return getCisSapDigitalPaymentClient(sapDigitalPaymentConfig).authorizatePayment(authorizationRequests).map(authResp -> {
			logSuccess("successfully received the payment authorization response");
			return authResp;
		}).doOnError(DefaultCisSapDigitalPaymentService::logError);
	}



	/**
	 * Delete the payment card information from SAP Digital payments
	 *
	 * @param deletCardRequests
	 *           - Delete card request list of CisSapDigitalPaymentTokenizedCardResult
	 * @param sapDigitalPaymentConfigurationStrategy
	 *           - SAP Digital payment configuration strategy
	 * @return CisSapDigitalPaymentCardDeletionResultList - delete card result wrapped around Observable<>
	 *
	 */
	@Override
	public Observable<CisSapDigitalPaymentCardDeletionResultList> deleteCard(
			final CisSapDigitalPaymentCardDeletionRequestList deletCardRequests,
			final SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy)
	{
		final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig = sapDigitalPaymentConfigurationStrategy
				.getSapDigitalPaymentConfiguration();
		if (null != sapDigitalPaymentConfig)
		{
			return getCisSapDigitalPaymentClient(sapDigitalPaymentConfig).deleteCard(deletCardRequests).map(deleteCardResp -> {
				logSuccess("successfully received the delete card response");
				return deleteCardResp;
			}).doOnError(DefaultCisSapDigitalPaymentService::logError);
		}
		return Observable.empty();
	}


	/**
	 * Settlement of the authorized payment with SAP Digital payments.
	 *
	 * @param chargeRequests
	 *           - List of CisSapDigitalPaymentChargeRequest.
	 * @param sapDigitalPaymentConfig
	 *           - SAP Digital payment configuration
	 * @return CisSapDigitalPaymentChargeResultList - Charge payment result wrapped around Observable<>
	 *
	 */
	@Override
	public Observable<CisSapDigitalPaymentChargeResultList> chargePayment(
			final CisSapDigitalPaymentChargeRequestList chargeRequests,
			final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
	{

		validateParameterNotNullStandardMessage("CisSapDigitalPaymentChargeRequestList", chargeRequests);
		validateParameterNotNullStandardMessage("SAPDigitalPaymentConfigurationModel", sapDigitalPaymentConfig);

		return getCisSapDigitalPaymentClient(sapDigitalPaymentConfig).chargePayment(chargeRequests).map(chargeResp -> {
			logSuccess("successfully received the payment charge response");
			return chargeResp;
		}).doOnError(DefaultCisSapDigitalPaymentService::logError);
	}


	/**
	 * Refund the payment with SAP Digital payments.
	 *
	 * @param -
	 *           refundRequests - List of CisSapDigitalPaymentRefundRequest
	 * @param -
	 *           sapDigitalPaymentConfig - SAP Digital payment configuration
	 * @return CisSapDigitalPaymentRefundResultList - refund payment result wrapped around Observable<>
	 */
	@Override
	public Observable<CisSapDigitalPaymentRefundResultList> refundPayment(
			final CisSapDigitalPaymentRefundRequestList refundRequests,
			final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
	{
		validateParameterNotNullStandardMessage("CisSapDigitalPaymentRefundRequestList", refundRequests);
		validateParameterNotNullStandardMessage("SAPDigitalPaymentConfigurationModel", sapDigitalPaymentConfig);

		return getCisSapDigitalPaymentClient(sapDigitalPaymentConfig).refundPayment(refundRequests).map(refundResp -> {
			logSuccess("successfully received the refund payment response");
			return refundResp;
		}).doOnError(DefaultCisSapDigitalPaymentService::logError);
	}





	/**
	 * Create the Sap Digital payment charon client from the SAP digital payment configuration
	 *
	 * @param sapDigitalPaymentConfig
	 *           - sap digital payment configuration
	 * @return SapDigitalPaymentClient - SAP Digital payment client
	 */
	public SapDigitalPaymentClient getCisSapDigitalPaymentClient(final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
	{
		return Charon.from(SapDigitalPaymentClient.class).config(createDigitalPaymentConfigurationMap(sapDigitalPaymentConfig))
				.build();
	}

	/**
	 * Creates the Map<String,String> with all the properties required to create a SapDigitalPaymentClient.
	 *
	 * @param sapDigitalPaymentConfig
	 *           - SAP Digital payment configuration model
	 *
	 * @return Map<String, String>
	 */
	protected Map<String, String> createDigitalPaymentConfigurationMap(
			final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
	{
		final Map<String, String> sapDigitalPaymentConfigMap = new HashMap<>();
		try
		{
			validateParameterNotNull(sapDigitalPaymentConfig, "Sap Digital payment configuration cannot be null");

			final SAPDigitalPaymentClientModel sapDpClientModel = sapDigitalPaymentConfig.getSapDigitalpaymentClient();
			if (null != sapDpClientModel)
			{
				sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_ID_KEY,
						sapDpClientModel.getClientId());
				sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_SECRET_KEY,
						sapDpClientModel.getClientSecret());
				sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_GRANT_TYPE_KEY,
						StringUtils.join(sapDpClientModel.getAuthorizedGrantTypes(), ","));
				sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_SCOPE,
						StringUtils.join(sapDpClientModel.getScope(), ","));

				sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_URL_KEY,
						sapDpClientModel.getTokenUrl());
			}



			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_URL_KEY,
					sapDigitalPaymentConfig.getBaseUrl());

			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_COMPANY_CODE_KEY,
					sapDigitalPaymentConfig.getCompanyCode());
			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOMER_COUNTRY_KEY,
					sapDigitalPaymentConfig.getCustomerCountry());
			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_PAYMENT_METHOD_KEY,
					sapDigitalPaymentConfig.getPaymentMethod());
			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOM_PARAM_KEY,
					sapDigitalPaymentConfig.getCustomParam());

			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_KEY,
					String.valueOf(sapDigitalPaymentConfig.getMaxRetry()));
			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_INTERVAL_KEY,
					String.valueOf(sapDigitalPaymentConfig.getRetryInterval()));
			sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_TIMEOUT_KEY,
					String.valueOf(sapDigitalPaymentConfig.getTimeOut()));
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
			LOG.error("Error while reading the SAP Digital payment configurations. Configuration details might be missing"
					+ e.getMessage());
		}
		return sapDigitalPaymentConfigMap;

	}



	/**
	 * Logs the success
	 */
	private static void logSuccess(final String message)
	{
		LOG.info(message);
	}

	/**
	 * Logs the error received
	 */
	private static void logError(final Throwable error)
	{
		LOG.error("Error while fetching the response" + error);
	}

	/**
	 * Checks the poll card transaction status
	 */
	private static boolean checkPollCardTransactionResult(
			final CisSapDigitalPaymentPollRegisteredCardResult sapDigitalPaymentPollRegisteredCardResult)
	{
		if (null != sapDigitalPaymentPollRegisteredCardResult
				&& null != sapDigitalPaymentPollRegisteredCardResult.getCisSapDigitalPaymentTransactionResult())
		{
			final String pollStatus = getPollCardStatusMap().get(
					sapDigitalPaymentPollRegisteredCardResult.getCisSapDigitalPaymentTransactionResult().getDigitalPaytTransResult());
			if (CisSapDigitalPaymentConstant.POLL_REG_CARD_PENDING_STAT.equals(pollStatus))
			{
				return false;
			}
			else if (CisSapDigitalPaymentConstant.POLL_REG_CARD_CANCELLED_STAT.equals(pollStatus)
					|| CisSapDigitalPaymentConstant.POLL_REG_CARD_SUCCESS_STAT.equals(pollStatus)
					|| CisSapDigitalPaymentConstant.POLL_REG_CARD_TIMEOUT_STAT.equals(pollStatus))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the pollCardStatusMap
	 */
	public static Map<String, String> getPollCardStatusMap()
	{
		return pollCardStatusMap;
	}

	/**
	 * @param pollCardStatusMap
	 *           the pollCardStatusMap to set
	 */
	public static void setPollCardStatusMap(final Map<String, String> pollCardStatusMap)
	{
		DefaultCisSapDigitalPaymentService.pollCardStatusMap = pollCardStatusMap;
	}



	/**
	 * @return the defaultPollCardDelay
	 */
	public long getDefaultPollCardDelay()
	{
		return defaultPollCardDelay;
	}

	/**
	 * @param defaultPollCardDelay
	 *           the defaultPollCardDelay to set
	 */
	public void setDefaultPollCardDelay(final long defaultPollCardDelay)
	{
		this.defaultPollCardDelay = defaultPollCardDelay;
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

	/**
	 * @return the log
	 */
	public static Logger getLog()
	{
		return LOG;
	}





}
