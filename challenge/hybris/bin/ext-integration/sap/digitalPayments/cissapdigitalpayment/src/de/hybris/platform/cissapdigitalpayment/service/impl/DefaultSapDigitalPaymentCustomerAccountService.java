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

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTokenizedCardResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentDeleteCardException;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentCustomerAccountService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;



/**
 * Default implementation for {@link SapDigitalPaymentCustomerAccountService}
 */
public class DefaultSapDigitalPaymentCustomerAccountService extends DefaultCustomerAccountService
		implements SapDigitalPaymentCustomerAccountService
{

	private static final Logger LOG = Logger.getLogger(DefaultSapDigitalPaymentCustomerAccountService.class);

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;

	private Map<String, String> sapDigiPayDeleteCardTranResult;

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;





	/**
	 * Unlink the credit card payment info from the customer. Send the request to SAP Digital Payment to remove the
	 * payment details. Once successfully removed, unlink it from the customer paymentInfo List
	 *
	 * @param customerModel
	 *           - Customer for whom the payment details need to be removed
	 *
	 * @param creditCardPaymentInfo
	 *           - Credit card information to be removed.
	 */
	@Override
	public void unlinkCCPaymentInfo(final CustomerModel customerModel, final CreditCardPaymentInfoModel creditCardPaymentInfo)
	{


		//Send the request to SAP Digital payments to remove the registered card
		CisSapDigitalPaymentCardDeletionResultList cardDeletionResultList = null;

		final List<CreditCardPaymentInfoModel> creditCardPaymentInfoModelList = Arrays.asList(creditCardPaymentInfo);

		cardDeletionResultList = getCisSapDigitalPaymentService()
				.deleteCard(createDeleteCardRequestList(creditCardPaymentInfoModelList), getSapDigitalPaymentConfigurationStrategy())
				.toBlocking().first();
		if (isCardDeletionBySAPDigiPaySuccess(cardDeletionResultList))
		{
			//After the card is successfully removed, delete the card from Customer account.
			super.unlinkCCPaymentInfo(customerModel, creditCardPaymentInfo);
		}
		else
		{
			LOG.info("Failed to delete the credit card  details");
			throw new SapDigitalPaymentDeleteCardException("Failed to delete the card details from SAP Digital payments");
		}
	}




	/**
	 * Check if the card card deletion is success based on the status return
	 */
	private boolean isCardDeletionBySAPDigiPaySuccess(final CisSapDigitalPaymentCardDeletionResultList cardDeletionResultList)
	{
		boolean cardDeletionSuccess = false;
		if (null != cardDeletionResultList)
		{
			final Optional<CisSapDigitalPaymentCardDeletionResult> deleteCardResult = cardDeletionResultList
					.getCisSapDigitalPaymentCardDeletionResult().stream().findFirst();
			if (deleteCardResult.isPresent())
			{
				cardDeletionSuccess = CisSapDigitalPaymentConstant.TRANS_RES_SUCCESS_STAT.equals(getSapDigiPayDeleteCardTranResult()
						.get(deleteCardResult.get().getCisDigitalPaymentCardDeletionTxResult().getDigitalPaytTransResult()));
			}
		}
		return cardDeletionSuccess;

	}



	/**
	 * Create delete card request list from CreditCardPaymentInfo List
	 *
	 * @param creditCardPaymentInfoList
	 *           - List of CreditCardPaymentInfoModel
	 *
	 * @return CisSapDigitalPaymentCardDeletionRequestList - List of CisSapDigitalPaymentTokenizedCardResult
	 */
	@Override
	public CisSapDigitalPaymentCardDeletionRequestList createDeleteCardRequestList(
			final List<CreditCardPaymentInfoModel> creditCardPaymentInfoList)
	{

		validateParameterNotNullStandardMessage("creditCardPaymentInfoList", creditCardPaymentInfoList);


		final CisSapDigitalPaymentCardDeletionRequestList deleteCardReqList = new CisSapDigitalPaymentCardDeletionRequestList();


		final List<CisSapDigitalPaymentTokenizedCardResult> tokenizedCardList = new ArrayList<>();

		if (null != creditCardPaymentInfoList)
		{
			for (final CreditCardPaymentInfoModel creditCardPaymentInfo : creditCardPaymentInfoList)
			{
				final CisSapDigitalPaymentTokenizedCardResult tokenizedCard = new CisSapDigitalPaymentTokenizedCardResult();

				tokenizedCard.setPaytCardByDigitalPaymentSrvc(creditCardPaymentInfo.getSubscriptionId());
				if (null != creditCardPaymentInfo.getType())
				{
					tokenizedCard.setPaymentCardType(creditCardPaymentInfo.getType().toString());
				}
				tokenizedCard.setPaymentCardExpirationMonth(creditCardPaymentInfo.getValidToMonth());
				tokenizedCard.setPaymentCardExpirationYear(creditCardPaymentInfo.getValidToYear());
				tokenizedCard.setPaymentCardMaskedNumber(creditCardPaymentInfo.getNumber());
				tokenizedCard.setPaymentCardHolderName(creditCardPaymentInfo.getCcOwner());

				tokenizedCardList.add(tokenizedCard);
			}
		}
		deleteCardReqList.setCisSapDigitalPaymentCardDeletionReqList(tokenizedCardList);

		return deleteCardReqList;
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
	 * @return the sapDigiPayDeleteCardTranResult
	 */
	public Map<String, String> getSapDigiPayDeleteCardTranResult()
	{
		return sapDigiPayDeleteCardTranResult;
	}



	/**
	 * @param sapDigiPayDeleteCardTranResult
	 *           the sapDigiPayDeleteCardTranResult to set
	 */
	public void setSapDigiPayDeleteCardTranResult(final Map<String, String> sapDigiPayDeleteCardTranResult)
	{
		this.sapDigiPayDeleteCardTranResult = sapDigiPayDeleteCardTranResult;
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
