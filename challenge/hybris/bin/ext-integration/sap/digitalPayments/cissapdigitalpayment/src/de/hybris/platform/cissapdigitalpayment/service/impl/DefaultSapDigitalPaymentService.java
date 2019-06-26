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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorization;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCard;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCharge;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeRequest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefund;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundRequest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentSource;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTransactionResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentPollRegisteredCardException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRefundException;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.model.SapDigitPayPollCardProcessModel;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default implementation of {@link SapDigitalPaymentService}
 */

public class DefaultSapDigitalPaymentService implements SapDigitalPaymentService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapDigitalPaymentService.class);

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;
	private ModelService modelService;
	private PaymentService paymentService;
	private CommonI18NService commonI18NService;
	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;
	private BusinessProcessService businessProcessService;
	private CartService cartService;
	private CommerceCardTypeService commerceCardTypeService;
	private CustomerAccountService customerAccountService;
	private CommerceCheckoutService commerceCheckoutService;
	private Converter<AddressModel, AddressData> addressConverter;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private CalculationService calculationService;
	private Map<String, String> sapDigiPayAuthTranResult;
	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;


	/**
	 * Authorize the payment transaction. If the payment transaction is success from the SAP Digital Payments, a new
	 * PaymentTransactionEntryModel object is created, set its transaction type to AUTHORIZATION, populate the
	 * transaction and authorization fields.
	 *
	 * @param merchantTransactionCode
	 *           - merchant transaction code
	 *
	 * @param paymentProvider
	 *           - payment provider
	 * @param deliveryAddress
	 *           - delivery address captured during the checkout
	 *
	 * @param dpAuthResult
	 *           - SAP Digital payments authorization result
	 */
	@Override
	public PaymentTransactionEntryModel authorize(final String merchantTransactionCode, final String paymentProvider,
			final AddressModel deliveryAddress, final CisSapDigitalPaymentAuthorizationResult dpAuthResult)
	{

		final PaymentTransactionModel transaction = (PaymentTransactionModel) this.getModelService()
				.create(PaymentTransactionModel.class);
		transaction.setCode(merchantTransactionCode);


		final String subscriptionID = getSubscriptionId(dpAuthResult.getCisSapDigitalPaymentSource());

		final CisSapDigitalPaymentTransactionResult authTrxResult = dpAuthResult.getCisSapDigitalPaymentTransactionResult();



		PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) getModelService()
				.create(PaymentTransactionEntryModel.class);

		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.AUTHORIZATION;
		entry.setType(paymentTransactionType);

		if (authTrxResult != null)
		{


			transaction.setRequestId(authTrxResult.getDigitalPaymentTransaction());
			transaction.setRequestToken(authTrxResult.getDigitalPaymentTransaction());
			transaction.setPaymentProvider(paymentProvider);


			this.getModelService().save(transaction);
			if (checkIfTransactionSucess(authTrxResult))
			{
				entry = fillAuthorizationTransactionDetails(dpAuthResult, entry);
				transaction.setPlannedAmount(
						new BigDecimal(dpAuthResult.getCisSapDigitalPaymentAuthorization().getAuthorizedAmountInAuthznCrcy()));
				transaction.setCurrency(this.getCommonI18NService()
						.getCurrency(dpAuthResult.getCisSapDigitalPaymentAuthorization().getAuthorizationCurrency()));
			}
			entry.setPaymentTransaction(transaction);
			entry.setRequestId(authTrxResult.getDigitalPaymentTransaction());
			entry.setRequestToken(authTrxResult.getDigitalPaymentTransaction());
			entry.setTransactionStatus(getTransactionStatus(authTrxResult));
			entry.setTransactionStatusDetails(authTrxResult.getDigitalPaytTransRsltDesc());

			if (StringUtils.isEmpty(entry.getCode()))
			{
				final String newEntryCode = getPaymentService().getNewPaymentTransactionEntryCode(transaction,
						paymentTransactionType);
				entry.setCode(newEntryCode);
			}

			if (subscriptionID != null)
			{
				entry.setSubscriptionID(subscriptionID);
			}
		}

		this.getModelService().saveAll(transaction, entry);
		this.getModelService().refresh(transaction);
		return entry;
	}


	/**
	 * Settle the payment authorization. Iterate the payment transaction to find a transaction entry of type
	 * AUTHORIZATION, Creates a List<CisSapDigitalPaymentChargeRequest>, and call the SAP Digital payment client charge
	 * api, checks the response status. For successful response, extract the capture transaction details to a new
	 * PaymentTransactionEntryModel object with payment transaction type as CAPTURE.
	 *
	 * @param transaction
	 *           - payment transaction associated with the order
	 * @return PaymentTransactionEntryModel - payment transaction entry object
	 *
	 * @throws SapDigitalPaymentCaptureException
	 */
	@Override
	public PaymentTransactionEntryModel capture(final PaymentTransactionModel transaction) throws SapDigitalPaymentCaptureException
	{

		PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.getModelService()
				.create(PaymentTransactionEntryModel.class);
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.CAPTURE;
		final String newEntryCode = getPaymentService().getNewPaymentTransactionEntryCode(transaction, paymentTransactionType);

		try
		{

			final PaymentTransactionEntryModel paymentTransactionEntry = transaction.getEntries().stream()
					.map(PaymentTransactionEntryModel.class::cast)
					.filter(e -> PaymentTransactionType.AUTHORIZATION.equals(e.getType())).findFirst().get();

			final CisSapDigitalPaymentChargeResultList dpChargeResultList = getCisSapDigitalPaymentService()
					.chargePayment(createPaymentChargeRequestList(transaction, paymentTransactionEntry),
							getSapDigitalPaymentConfiguration(transaction.getOrder()))
					.toBlocking().first();

			final CisSapDigitalPaymentChargeResult dpChargeResult = dpChargeResultList.getCisSapDigitalPaymentChargeResults()
					.stream().findFirst().get();

			final CisSapDigitalPaymentTransactionResult captureTrxResult = dpChargeResult.getCisSapDigitalPaymentTransactionResult();

			if (checkIfTransactionSucess(captureTrxResult))
			{
				entry = fillCaptureTransactionDetails(dpChargeResult, entry);
				entry.setType(paymentTransactionType);
				entry.setRequestId(captureTrxResult.getDigitalPaymentTransaction());
				entry.setRequestToken(captureTrxResult.getDigitalPaymentTransaction());
				entry.setPaymentTransaction(transaction);
				entry.setTransactionStatus(getTransactionStatus(captureTrxResult));
				entry.setTransactionStatusDetails(captureTrxResult.getDigitalPaytTransRsltDesc());

			}
		}
		catch (final NoSuchElementException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Could not capture without authorization [%s]", e));
			}
			throw new SapDigitalPaymentCaptureException(
					String.format("Could not capture without authorization [%s]", e.getMessage()));
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while capturing the the payment details [%s]", e));
			}
			LOG.error(String.format("Error while capturing the the payment details [%s]", e.getMessage()));
		}

		entry.setCode(newEntryCode);
		this.getModelService().saveAll(transaction, entry);
		this.getModelService().refresh(transaction);
		return entry;

	}

	/**
	 * Refund the payment to the same card used for placing the order
	 *
	 * @param transaction
	 *           - payment transaction associated with the return request's order.
	 *
	 * @param amountToRefund
	 *           - amount to be refunded
	 *
	 * @return PaymentTransactionEntryModel - payment transaction entry created for the refund request
	 */
	@Override
	public PaymentTransactionEntryModel refund(final PaymentTransactionModel transaction, final BigDecimal amountToRefund)
			throws SapDigitalPaymentRefundException
	{

		PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.getModelService()
				.create(PaymentTransactionEntryModel.class);
		final PaymentTransactionType transactionType = PaymentTransactionType.REFUND_FOLLOW_ON;
		final String newEntryCode = getPaymentService().getNewPaymentTransactionEntryCode(transaction, transactionType);

		try
		{
			final PaymentTransactionEntryModel paymentTransactionEntry = transaction.getEntries().stream()
					.map(PaymentTransactionEntryModel.class::cast).filter(e -> PaymentTransactionType.CAPTURE.equals(e.getType()))
					.findFirst().get();


			final CisSapDigitalPaymentRefundResultList dpRefundResultList = getCisSapDigitalPaymentService()
					.refundPayment(createPaymentRefundRequestList(transaction, paymentTransactionEntry, amountToRefund),
							getSapDigitalPaymentConfiguration(transaction.getOrder()))
					.toBlocking().first();


			final CisSapDigitalPaymentRefundResult dpRefundResult = dpRefundResultList.getCisSapDigitalPaymentRefundResuts().stream()
					.findFirst().get();

			final CisSapDigitalPaymentTransactionResult refundTrxResult = dpRefundResult.getCisSapDigitalPaymentTransactionResult();

			if (checkIfTransactionSucess(refundTrxResult))
			{
				entry = fillRefundTransactionDetails(dpRefundResult, entry);
				entry.setType(transactionType);
				entry.setRequestId(refundTrxResult.getDigitalPaymentTransaction());
				entry.setRequestToken(refundTrxResult.getDigitalPaymentTransaction());
				entry.setPaymentTransaction(transaction);
				entry.setTransactionStatus(getTransactionStatus(refundTrxResult));
				entry.setTransactionStatusDetails(refundTrxResult.getDigitalPaytTransRsltDesc());
			}
		}
		catch (final NoSuchElementException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Could not refund without payment capture [%s]", e));
			}
			throw new SapDigitalPaymentRefundException(
					String.format("Could not refund without payment capture [%s]", e.getMessage()));
		}
		catch (final RuntimeException e)
		{

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Exception during refund the payment details [%s]", e));
			}
			LOG.error(String.format("Exception during refund the payment details [%s]", e));
		}
		entry.setCode(newEntryCode);
		this.getModelService().saveAll(transaction, entry);
		this.getModelService().refresh(transaction);
		return entry;
	}



	/**
	 * Return the {@link SAPDigitalPaymentConfigurationModel } from the base store associated to the order
	 */
	private SAPDigitalPaymentConfigurationModel getSapDigitalPaymentConfiguration(final AbstractOrderModel order)
	{
		validateParameterNotNullStandardMessage("order", order);
		if (null != order.getStore() && null != order.getStore().getSapDigitalPaymentConfiguration())
		{
			return order.getStore().getSapDigitalPaymentConfiguration();
		}
		return getSapDigitalPaymentConfigurationStrategy().getSapDigitalPaymentConfiguration();

	}


	/**
	 * Create the payment charge request
	 *
	 * @param transaction
	 *           - payment transaction
	 *
	 * @param entry
	 *           - payment transaction entry
	 *
	 * @return {@link CisSapDigitalPaymentChargeRequestList}
	 */
	private CisSapDigitalPaymentChargeRequestList createPaymentChargeRequestList(final PaymentTransactionModel transaction,
			final PaymentTransactionEntryModel entry)
	{
		validateParameterNotNullStandardMessage("transaction", transaction);
		validateParameterNotNullStandardMessage("plannedAmount", transaction.getPlannedAmount());
		validateParameterNotNullStandardMessage("currency", transaction.getCurrency());
		validateParameterNotNullStandardMessage("entry", entry);

		final CisSapDigitalPaymentChargeRequestList paymentChargeReqList = new CisSapDigitalPaymentChargeRequestList();

		final CisSapDigitalPaymentChargeRequest paymentChargeRequest = new CisSapDigitalPaymentChargeRequest();
		final CisSapDigitalPaymentCard paymentCard = new CisSapDigitalPaymentCard();
		try
		{
			final PaymentInfoModel paymentInfoModel = transaction.getInfo();
			if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
			{
				final CreditCardPaymentInfoModel ccPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
				paymentCard.setPaytCardByDigitalPaymentSrvc(ccPaymentInfoModel.getSubscriptionId());
				final CisSapDigitalPaymentSource paymentSource = new CisSapDigitalPaymentSource();
				paymentSource.setCisSapDigitalPaymentCard(paymentCard);
				paymentChargeRequest.setCisSapDigitalPaymentSource(paymentSource);
				paymentChargeRequest.setAmountInPaymentCurrency(transaction.getPlannedAmount().toString());
				paymentChargeRequest.setPaymentCurrency(transaction.getCurrency().getIsocode());
				paymentChargeRequest.setPaymentIsToBeCaptured(Boolean.FALSE.toString());
				paymentChargeRequest.setReferenceDocument(transaction.getOrder().getCode());
				final CisSapDigitalPaymentAuthorization paymentAuthorization = new CisSapDigitalPaymentAuthorization();
				paymentAuthorization.setAuthorizationByPaytSrvcPrvdr(entry.getAuthByDigitalPaytSrvc());
				paymentAuthorization.setAuthorizationByAcquirer(entry.getAuthByAcquirer());
				paymentChargeRequest.setCisSapDigitalPaymentAuthorization(paymentAuthorization);
			}

			final List<CisSapDigitalPaymentChargeRequest> chargeRequests = new ArrayList<>();
			chargeRequests.add(paymentChargeRequest);
			paymentChargeReqList.setCisSapDigitalPaymentChargeRequests(chargeRequests);
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while creating the Payment charge request [%s]", e));
			}
			LOG.error("Error while creating the Payment charge request [%s]", e.getMessage());
		}
		return paymentChargeReqList;
	}



	/**
	 * Create the payment refund request
	 *
	 * @param transaction
	 *           - payment transaction
	 *
	 * @param entry
	 *           - payment transaction entry
	 *
	 * @param amountToRefund
	 *           - amount to refund
	 *
	 * @return {@link CisSapDigitalPaymentRefundRequestList}
	 */
	private CisSapDigitalPaymentRefundRequestList createPaymentRefundRequestList(final PaymentTransactionModel transaction,
			final PaymentTransactionEntryModel entry, final BigDecimal amountToRefund)
	{
		validateParameterNotNullStandardMessage("transaction", transaction);
		validateParameterNotNullStandardMessage("entry", entry);
		validateParameterNotNullStandardMessage("amountToRefund", amountToRefund);

		final CisSapDigitalPaymentRefundRequestList paymentRefundeReqList = new CisSapDigitalPaymentRefundRequestList();

		final CisSapDigitalPaymentRefundRequest refundRequest = new CisSapDigitalPaymentRefundRequest();
		final CisSapDigitalPaymentCard paymentCard = new CisSapDigitalPaymentCard();
		try
		{

			final PaymentInfoModel paymentInfoModel = transaction.getInfo();
			if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
			{
				final CreditCardPaymentInfoModel ccPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
				paymentCard.setPaytCardByDigitalPaymentSrvc(ccPaymentInfoModel.getSubscriptionId());
				final CisSapDigitalPaymentSource paymentSource = new CisSapDigitalPaymentSource();
				paymentSource.setCisSapDigitalPaymentCard(paymentCard);

				refundRequest.setCisSapDigitalPaymentSource(paymentSource);
				refundRequest.setAmountInRefundCurrency(amountToRefund.toString());
				refundRequest.setRefundCurrency(transaction.getCurrency().getIsocode());
				refundRequest.setPaymentByPaymentServicePrvdr(entry.getAuthByPaytSrvcPrvdr());
				refundRequest.setReferenceDocument(transaction.getOrder().getCode());
			}

			final List<CisSapDigitalPaymentRefundRequest> refundRequests = new ArrayList<>();
			refundRequests.add(refundRequest);
			paymentRefundeReqList.setCisSapDigitalPaymentRefundRequests(refundRequests);

		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while creating the Payment refund request [%s]", e));
			}
			LOG.error(String.format("Error while creating the Payment refund request [%s]", e.getMessage()));
		}
		return paymentRefundeReqList;

	}


	/**
	 *
	 * Populate the {@link PaymentTransactionEntryModel} from the {@link CisSapDigitalPaymentAuthorizationResult}
	 *
	 * @param authTrxResult
	 *           - authorization result
	 * @param entry
	 *           - {@link PaymentTransactionEntryModel}
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	private PaymentTransactionEntryModel fillAuthorizationTransactionDetails(
			final CisSapDigitalPaymentAuthorizationResult authTrxResult, final PaymentTransactionEntryModel entry)
	{

		final CisSapDigitalPaymentAuthorization auth = authTrxResult.getCisSapDigitalPaymentAuthorization();
		if (auth != null)
		{
			entry.setCode(auth.getAuthorizationByDigitalPaytSrvc());
			entry.setAmount(new BigDecimal(auth.getAuthorizedAmountInAuthznCrcy()));
			entry.setCurrency(this.getCommonI18NService().getCurrency(auth.getAuthorizationCurrency()));
			//Adding the new filed added PaymentTransaction -start

			entry.setAuthByPaytSrvcPrvdr(auth.getAuthorizationByPaytSrvcPrvdr());
			entry.setAuthByAcquirer(auth.getAuthorizationByAcquirer());
			entry.setAuthByDigitalPaytSrvc(auth.getAuthorizationByDigitalPaytSrvc());


			entry.setAuthStatus(auth.getAuthorizationStatus());
			entry.setAuthStatusName(auth.getAuthorizationStatusName());
			entry.setTime(auth.getAuthorizationDateTime() == null ? new Date()
					: constructAuthorizationDateTime(auth.getAuthorizationDateTime()));
		}
		return entry;
	}

	/**
	 *
	 * Populate the {@link PaymentTransactionEntryModel} from the {@link CisSapDigitalPaymentChargeResult}
	 *
	 * @param chargeTrxResult
	 *           - charge transaction result
	 * @param entry
	 *           - {@link PaymentTransactionEntryModel}
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	private PaymentTransactionEntryModel fillCaptureTransactionDetails(final CisSapDigitalPaymentChargeResult chargeTrxResult,
			final PaymentTransactionEntryModel entry)
	{
		final CisSapDigitalPaymentCharge charge = chargeTrxResult.getCisSapDigitalPaymentCharge();
		if (charge != null)
		{
			entry.setAmount(new BigDecimal(charge.getAmountInPaymentCurrency()));
			entry.setCurrency(this.getCommonI18NService().getCurrency(charge.getPaymentCurrency()));
			//Adding the new filed added PaymentTransaction -start
			entry.setAuthByPaytSrvcPrvdr(charge.getPaymentByPaymentServicePrvdr());
			entry.setAuthStatus(charge.getPaymentStatus());
			entry.setAuthStatusName(charge.getPaymentStatusName());
			entry.setTime(
					charge.getPaymentDateTime() == null ? new Date() : constructAuthorizationDateTime(charge.getPaymentDateTime()));
		}
		return entry;
	}

	/**
	 *
	 * Populate the {@link PaymentTransactionEntryModel} from the {@link CisSapDigitalPaymentRefundResult}
	 *
	 * @param refundTrxResult
	 *           - refund transaction result
	 * @param entry
	 *           - {@link PaymentTransactionEntryModel}
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	private PaymentTransactionEntryModel fillRefundTransactionDetails(final CisSapDigitalPaymentRefundResult refundTrxResult,
			final PaymentTransactionEntryModel entry)
	{
		final CisSapDigitalPaymentRefund refund = refundTrxResult.getCisSapDigitalPaymentRefund();
		if (refund != null)
		{
			entry.setAmount(new BigDecimal(refund.getAmountInRefundCurrency()));
			entry.setCurrency(this.getCommonI18NService().getCurrency(refund.getRefundCurrency()));
			//Adding the new filed added PaymentTransaction -start
			entry.setAuthByPaytSrvcPrvdr(refund.getRefundByPaymentServiceProvider());
			entry.setAuthStatus(refund.getRefundStatus());
			entry.setAuthStatusName(refund.getRefundStatusName());
			entry.setTime(
					refund.getRefundDateTime() == null ? new Date() : constructAuthorizationDateTime(refund.getRefundDateTime()));
		}
		return entry;
	}




	/**
	 * Checks if the transaction is success
	 */
	private boolean checkIfTransactionSucess(final CisSapDigitalPaymentTransactionResult transactionResult)
	{
		// YTODO Auto-generated method stub
		if (StringUtils.isNotEmpty(transactionResult.getDigitalPaytTransResult()))
		{
			return CisSapDigitalPaymentConstant.TRANS_RES_SUCCESS_STAT
					.equals(getSapDigiPayAuthTranResult().get(transactionResult.getDigitalPaytTransResult()));
		}
		return false;

	}




	/**
	 * This method will get the card registration URL from the SAP Digital payment
	 */
	@Override
	public String getCardRegistrationUrl()
	{
		return getPaymentFormActionUrlStrategy().getHopRequestUrl();
	}



	/**
	 *
	 * Creates {@link SapDigitPayPollCardProcessModel} and triggers the process
	 */
	@Override
	public void createPollRegisteredCardProcess(final String sessionId)
	{
		if (StringUtils.isNotEmpty(sessionId))
		{

			final String processCode = CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_POLL_REG_CARD_PROCESS_DEF_NAME + "-"
					+ sessionId + "-" + System.currentTimeMillis();
			final SapDigitPayPollCardProcessModel pollCardProcessModel = getBusinessProcessService().createProcess(processCode,
					CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_POLL_REG_CARD_PROCESS_DEF_NAME);
			pollCardProcessModel.setSessionId(sessionId);

			final CartModel currentCart = getCartService().getSessionCart();
			//Set the sessionCart and session user to the process
			getModelService().refresh(currentCart);
			pollCardProcessModel.setSessionCart(currentCart);
			pollCardProcessModel.setSessionUser(getUserService().getCurrentUser());
			pollCardProcessModel.setBaseStore(getBaseStoreService().getCurrentBaseStore());
			try
			{
				getBusinessProcessService().startProcess(pollCardProcessModel);
			}
			catch (final Exception e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Error while executing poll Registered card process [%s]", e));
				}
				throw new SapDigitalPaymentPollRegisteredCardException(
						String.format("Error while executing poll Registered card process [%s]", e.getMessage()));
			}
		}
	}

	/**
	 * Creates a payment subscription and returns the {@link CreditCardPaymentInfoModel }
	 *
	 * @param paymentInfoData
	 *           - credit card payment info data
	 *
	 * @param params
	 *           - pass the parameters like cart, user, payment provider etc
	 *
	 * @return {@link CreditCardPaymentInfoModel}
	 */
	@Override
	public CreditCardPaymentInfoModel createPaymentSubscription(final CCPaymentInfoData paymentInfoData,
			final Map<String, Object> params)
	{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);


		if (params.get(CisSapDigitalPaymentConstant.CART) instanceof CartModel)
		{
			saveDeliveryAddressToPaymentInfoData(paymentInfoData, (CartModel) params.get(CisSapDigitalPaymentConstant.CART));
		}
		final AddressData billingAddressData = paymentInfoData.getBillingAddress();
		validateParameterNotNullStandardMessage("billingAddress", billingAddressData);
		try
		{
			if (params.get(CisSapDigitalPaymentConstant.CART) instanceof CartModel
					&& params.get(CisSapDigitalPaymentConstant.USER) instanceof CustomerModel
					&& checkIfCurrentUserIsTheCartUser((UserModel) params.get(CisSapDigitalPaymentConstant.USER),
							(CartModel) params.get(CisSapDigitalPaymentConstant.CART)))
			{

				final CardInfo cardInfo = populateCardInfo(paymentInfoData);
				final BillingInfo billingInfo = populateBillingInfo(paymentInfoData.getBillingAddress());
				return getCustomerAccountService().createPaymentSubscription(
						(CustomerModel) params.get(CisSapDigitalPaymentConstant.USER), cardInfo, billingInfo,
						billingAddressData.getTitleCode(), getPaymentProvider(params), paymentInfoData.isSaved());
			}
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while creating payment subscription [%s] ", e));
			}
			LOG.error(String.format("Error while creating payment subscription [%s] ", e.getMessage()));
		}
		return null;
	}

	/**
	 * returns the payment provider from the base store. If null, then return the default payment provider.
	 */
	private String getPaymentProvider(final Map<String, Object> params)
	{
		if (params.get(CisSapDigitalPaymentConstant.BASE_STORE) instanceof BaseStoreModel)
		{
			final BaseStoreModel currentStore = (BaseStoreModel) params.get(CisSapDigitalPaymentConstant.BASE_STORE);
			return currentStore.getPaymentProvider() != null ? currentStore.getPaymentProvider()
					: CisSapDigitalPaymentConstant.PAYMENT_PROVIDER;
		}
		return CisSapDigitalPaymentConstant.PAYMENT_PROVIDER;
	}




	/**
	 * Saves the payment details to the cart.
	 *
	 * @param paymentInfoId
	 *           - payment info ID
	 * @param params
	 *           - {@link Map<String, Object>}
	 *
	 * @return {@link Boolean}
	 */
	@Override
	public boolean saveCreditCardPaymentDetailsToCart(final String paymentInfoId, final Map<String, Object> params)
	{

		validateParameterNotNullStandardMessage("paymentInfoId", paymentInfoId);
		try
		{
			if (params.get(CisSapDigitalPaymentConstant.CART) instanceof CartModel
					&& params.get(CisSapDigitalPaymentConstant.USER) instanceof CustomerModel
					&& checkIfCurrentUserIsTheCartUser((UserModel) params.get(CisSapDigitalPaymentConstant.USER),
							(CartModel) params.get(CisSapDigitalPaymentConstant.CART))
					&& StringUtils.isNotBlank(paymentInfoId))
			{
				final CustomerModel currentUserForCheckout = (CustomerModel) params.get(CisSapDigitalPaymentConstant.USER);
				final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
						.getCreditCardPaymentInfoForCode(currentUserForCheckout, paymentInfoId);
				final CartModel cartModel = (CartModel) params.get(CisSapDigitalPaymentConstant.CART);

				boolean paymentAddedToCart = false;
				if (ccPaymentInfoModel != null)
				{
					final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
					parameter.setCart(cartModel);
					parameter.setPaymentInfo(ccPaymentInfoModel);
					paymentAddedToCart = getCommerceCheckoutService().setPaymentInfo(parameter);
				}
				if (paymentAddedToCart)
				{
					getModelService().save(cartModel);
				}
				return paymentAddedToCart;

			}
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while save the card payment information to the cart [%s]", e));
			}
			LOG.error(String.format("Error while save the card payment information to the cart [%s]", e.getMessage()));
		}
		return false;
	}


	/**
	 * Saves the delivery address to payment info
	 */
	protected void saveDeliveryAddressToPaymentInfoData(final CCPaymentInfoData paymentInfoData, final CartModel currentCart)
	{
		//Get the shipping address from the cart and set it as the billing address. This will be replaced when the billing address is captured at Hybris side.
		if (null != currentCart.getDeliveryAddress())
		{
			paymentInfoData.setBillingAddress(getAddressConverter().convert(currentCart.getDeliveryAddress()));
		}

	}


	/**
	 * Populates the {@link BillingInfo}
	 */
	private BillingInfo populateBillingInfo(final AddressData billingAddressData)
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity(billingAddressData.getTown());
		billingInfo.setCountry(billingAddressData.getCountry() == null ? null : billingAddressData.getCountry().getIsocode());
		billingInfo.setFirstName(billingAddressData.getFirstName());
		billingInfo.setLastName(billingAddressData.getLastName());
		billingInfo.setEmail(billingAddressData.getEmail());
		billingInfo.setPhoneNumber(billingAddressData.getPhone());
		billingInfo.setPostalCode(billingAddressData.getPostalCode());
		billingInfo.setStreet1(billingAddressData.getLine1());
		billingInfo.setStreet2(billingAddressData.getLine2());
		return billingInfo;
	}




	/**
	 * Populates the {@link populateCardInfo}
	 */
	protected CardInfo populateCardInfo(final CCPaymentInfoData paymentInfoData)
	{
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName(paymentInfoData.getAccountHolderName());
		cardInfo.setCardNumber(paymentInfoData.getCardNumber());
		final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(paymentInfoData.getCardType());
		cardInfo.setCardType(cardType == null ? null : cardType.getCode());
		cardInfo.setExpirationMonth(Integer.valueOf(paymentInfoData.getExpiryMonth()));
		cardInfo.setExpirationYear(Integer.valueOf(paymentInfoData.getExpiryYear()));
		cardInfo.setIssueNumber(paymentInfoData.getIssueNumber());
		//Adding payment token to cardInfo
		cardInfo.setCardToken(paymentInfoData.getSubscriptionId());
		return cardInfo;
	}




	protected boolean checkIfCurrentUserIsTheCartUser(final UserModel currentUser, final CartModel currentCart)
	{
		//Skip the check for anonymous user
		if (!isAnonymousCheckout(currentUser))
		{
			return currentCart == null ? false : currentCart.getUser().equals(currentUser);
		}
		return true;
	}


	protected boolean isAnonymousCheckout(final UserModel currentUser)
	{
		return getUserService().isAnonymousUser(currentUser);
	}





	/**
	 * Retries the transaction status
	 *
	 * @param transactionResult
	 *           - {@linkCisSapDigitalPaymentTransactionResult}
	 * @return {@link String}
	 */
	protected String getTransactionStatus(final CisSapDigitalPaymentTransactionResult transactionResult)
	{
		if (StringUtils.isNotEmpty(transactionResult.getDigitalPaytTransResult())
				&& CisSapDigitalPaymentConstant.TRANS_RES_SUCCESS_STAT
						.equals(getSapDigiPayAuthTranResult().get(transactionResult.getDigitalPaytTransResult())))
		{
			return TransactionStatus.ACCEPTED.toString();
		}
		//Need to check all the transaction codes from the digital payment and map the results
		return TransactionStatus.ERROR.toString();
	}

	/**
	 * Returns the authorization date and time. In case of errors, return a new {@link Date}
	 */
	protected Date constructAuthorizationDateTime(final String authorizationDateTime)
	{
		final DateFormat df = new SimpleDateFormat(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_AUTH_DATE_FORMAT);
		try
		{
			return df.parse(authorizationDateTime);
		}
		catch (final ParseException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while parsing the Authorization date [%s]", e));
			}
			LOG.error(String.format("Error while parsing the Authorization date [%s]", e.getMessage()));
		}
		return new Date();

	}

	/**
	 * Check for the subscription ID from the authorization response. If empty,set an empty string
	 */
	protected String getSubscriptionId(final CisSapDigitalPaymentSource source)
	{

		validateParameterNotNullStandardMessage("source", source);
		return source.getCisSapDigitalPaymentCard().getPaytCardByDigitalPaymentSrvc() != null
				? source.getCisSapDigitalPaymentCard().getPaytCardByDigitalPaymentSrvc()
				: StringUtils.EMPTY;
	}


	/**
	 * Checks if the transaction happened with SAP Digital payment transaction
	 *
	 * @param txn
	 *           - {@link PaymentTransactionModel}
	 *
	 * @return {@link Boolean}
	 */
	public boolean isSapDigitalPaymentTransaction(final PaymentTransactionModel txn)
	{
		final PaymentTransactionEntryModel paymentTransactionEntry = txn.getEntries().stream()
				.map(PaymentTransactionEntryModel.class::cast).filter(e -> PaymentTransactionType.AUTHORIZATION.equals(e.getType()))
				.findFirst().get();
		return paymentTransactionEntry.getAuthByDigitalPaytSrvc() != null;
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
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the paymentService
	 */
	public PaymentService getPaymentService()
	{
		return paymentService;
	}

	/**
	 * @param paymentService
	 *           the paymentService to set
	 */
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the paymentFormActionUrlStrategy
	 */
	public PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	/**
	 * @param paymentFormActionUrlStrategy
	 *           the paymentFormActionUrlStrategy to set
	 */
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}




	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}


	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}





	/**
	 * @return the commerceCardTypeService
	 */
	public CommerceCardTypeService getCommerceCardTypeService()
	{
		return commerceCardTypeService;
	}




	/**
	 * @param commerceCardTypeService
	 *           the commerceCardTypeService to set
	 */
	public void setCommerceCardTypeService(final CommerceCardTypeService commerceCardTypeService)
	{
		this.commerceCardTypeService = commerceCardTypeService;
	}




	/**
	 * @return the customerAccountService
	 */
	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}


	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}


	/**
	 * @return the commerceCheckoutService
	 */
	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}


	/**
	 * @param commerceCheckoutService
	 *           the commerceCheckoutService to set
	 */
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}


	/**
	 * @return the addressConverter
	 */
	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
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


	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}


	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}


	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}


	/**
	 * @return the sapDigiPayAuthTranResult
	 */
	public Map<String, String> getSapDigiPayAuthTranResult()
	{
		return sapDigiPayAuthTranResult;
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
	 * @param sapDigiPayAuthTranResult
	 *           the sapDigiPayAuthTranResult to set
	 */
	public void setSapDigiPayAuthTranResult(final Map<String, String> sapDigiPayAuthTranResult)
	{
		this.sapDigiPayAuthTranResult = sapDigiPayAuthTranResult;
	}







}
