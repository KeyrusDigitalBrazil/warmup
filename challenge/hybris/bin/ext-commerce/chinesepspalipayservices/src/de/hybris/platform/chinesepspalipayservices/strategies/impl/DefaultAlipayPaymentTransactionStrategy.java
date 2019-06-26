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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionDao;
import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionEntryDao;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayErrorInfo;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayNotification;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.chinesepspalipayservices.enums.AlipayPayMethod;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentTransactionStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultAlipayPaymentTransactionStrategy implements AlipayPaymentTransactionStrategy
{
	private static final String THE_GIVEN_ORDER_IS_NULL = "The given order is null!";
	private static final Logger LOG = Logger.getLogger(DefaultAlipayPaymentTransactionStrategy.class.getName());
	private ModelService modelService;
	private KeyGenerator paymentTransactionKeyGenerator;
	private AlipayPaymentTransactionEntryDao alipayPaymentTransactionEntryDao;
	private AlipayPaymentTransactionDao alipayPaymentTransactionDao;

	@Override
	public void createForNewRequest(final OrderModel orderModel, final String requestUrl)
	{
		createTransacionForNewRequest(orderModel, requestUrl);
	}

	protected final void createTransacionForNewRequest(final OrderModel orderModel,
			final String requestUrl)
	{
		final AlipayPaymentTransactionModel alipayPaymentTransactionModel = modelService
				.create(AlipayPaymentTransactionModel.class);
		alipayPaymentTransactionModel.setOrder(orderModel);
		alipayPaymentTransactionModel.setCode(orderModel.getCode());
		alipayPaymentTransactionModel.setPaymentUrl(requestUrl);
		alipayPaymentTransactionModel.setPayMethod(AlipayPayMethod.DIRECTPAY);
		alipayPaymentTransactionModel.setRequestId(orderModel.getCode());
		alipayPaymentTransactionModel.setPaymentProvider(PaymentConstants.Basic.PAYMENT_PROVIDER);
		alipayPaymentTransactionModel.setTransactionInitiateDate(new Date());
		final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntryModel = createTransactionEntryForNewRequest(
				orderModel, alipayPaymentTransactionModel);
		alipayPaymentTransactionModel.setEntries(Collections.singletonList((alipayPaymentTransactionEntryModel)));
		getModelService().save(alipayPaymentTransactionModel);
	}

	protected AlipayPaymentTransactionEntryModel createTransactionEntryForNewRequest(final OrderModel orderModel,
			final AlipayPaymentTransactionModel alipayPaymentTransactionModel)
	{
		final AlipayPaymentTransactionEntryModel entry = modelService
				.create(AlipayPaymentTransactionEntryModel.class);
		entry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
		if (orderModel.getCurrency() != null)
		{
			entry.setCurrency(orderModel.getCurrency());
		}
		entry.setType(PaymentTransactionType.REQUEST);
		entry.setTime(new Date());
		entry.setPaymentTransaction(alipayPaymentTransactionModel);
		entry.setRequestId(alipayPaymentTransactionModel.getRequestId());
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
		return entry;
	}

	@Override
	public AlipayPaymentTransactionEntryModel saveForStatusCheck(final OrderModel orderModel,
			final AlipayRawPaymentStatus alipayRawPaymentStatus)
	{

		validateParameterNotNull(orderModel, THE_GIVEN_ORDER_IS_NULL);
		validateParameterNotNull(alipayRawPaymentStatus, "The given status data is null!");
		final String error = alipayRawPaymentStatus.getError();
		if (error == null && alipayRawPaymentStatus.getTradeNo() != null)
		{
			final TransactionStatus status = PaymentConstants.TransactionStatusMap.getAlipaytohybris().get(
					alipayRawPaymentStatus.getTradeStatus());
			if (status == null)
			{
				return null;
			}

			final AlipayPaymentTransactionModel alipayPaymentTransaction = getPaymentTransactionToUpdate(orderModel, status,
					alipayRawPaymentStatus.getTradeNo());

			if (alipayPaymentTransaction != null)
			{
				alipayPaymentTransaction.setAlipayCode(alipayRawPaymentStatus.getTradeNo());
				modelService.save(alipayPaymentTransaction);
				final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntry = modelService
						.create(AlipayPaymentTransactionEntryModel.class);

				setEntryByTransaction(alipayPaymentTransaction, alipayPaymentTransactionEntry);
				setEntryByRawPaymentStatus(alipayRawPaymentStatus, alipayPaymentTransactionEntry);

				alipayPaymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);
				alipayPaymentTransactionEntry.setTransactionStatus(status.name());
				alipayPaymentTransactionEntry.setTransactionStatusDetails("Trade Status" + alipayRawPaymentStatus.getTradeStatus());
				alipayPaymentTransactionEntry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
				modelService.save(alipayPaymentTransactionEntry);
				modelService.refresh(alipayPaymentTransaction);

				return alipayPaymentTransactionEntry;
			}
		}

		return null;

	}


	@Override
	public boolean checkCaptureTransactionEntry(final OrderModel orderModel, final TransactionStatus status)
	{
		final Optional<AlipayPaymentTransactionModel> returnTransaction = getPaymentTransactionWithCaptureEntry(orderModel, status);
		return returnTransaction.isPresent();
	}


	@Override
	public void updateForCancelPayment(final OrderModel orderModel, final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult)
	{
		validateParameterNotNull(orderModel, THE_GIVEN_ORDER_IS_NULL);
		validateParameterNotNull(alipayRawCancelPaymentResult, "The given closeTradeResponseData is null!");


		for (final PaymentTransactionModel transaction : orderModel.getPaymentTransactions())
		{
			if (transaction instanceof AlipayPaymentTransactionModel)
			{
				final AlipayPaymentTransactionEntryModel entry = modelService
						.create(AlipayPaymentTransactionEntryModel.class);
				entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
				entry.setType(PaymentTransactionType.CANCEL);
				entry.setTime(new Date());
				entry.setPaymentTransaction(transaction);
				if (alipayRawCancelPaymentResult.getError() == null)
				{
					entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
				}
				else
				{
					entry.setTransactionStatus(TransactionStatus.ERROR.name());
					entry.setTransactionStatusDetails(alipayRawCancelPaymentResult.getError());
				}

				modelService.save(entry);
			}
		}

	}

	@Override
	public void updateForNotification(final OrderModel orderModel, final AlipayRawDirectPayNotification directPayNotifyResponseData)
	{
		validateParameterNotNull(orderModel, THE_GIVEN_ORDER_IS_NULL);
		validateParameterNotNull(directPayNotifyResponseData, "The given notifyData is null!");

		if (directPayNotifyResponseData.getTradeNo() != null)
		{
			final TransactionStatus status = PaymentConstants.TransactionStatusMap.getAlipaytohybris().get(
					directPayNotifyResponseData.getTradeStatus());
			final AlipayPaymentTransactionModel alipayPaymentTransaction = getPaymentTransactionToUpdate(orderModel, status,
					directPayNotifyResponseData.getTradeNo());

			if (null == alipayPaymentTransaction)
			{
				LOG.warn("no AlipayPaymentTransaction found");
				return;
			}

			alipayPaymentTransaction.setAlipayCode(directPayNotifyResponseData.getTradeNo());
			modelService.save(alipayPaymentTransaction);
			final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntry = modelService
					.create(AlipayPaymentTransactionEntryModel.class);
			setEntryByTransaction(alipayPaymentTransaction, alipayPaymentTransactionEntry);
			setEntryByNotification(directPayNotifyResponseData, alipayPaymentTransactionEntry);
			alipayPaymentTransactionEntry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
			modelService.save(alipayPaymentTransactionEntry);
		}
	}


	@Override
	public void updateForError(final OrderModel orderModel, final AlipayRawDirectPayErrorInfo alipayRawDirectPayErrorInfo)
	{
		final AlipayPaymentTransactionModel alipayPaymentTransaction = alipayPaymentTransactionDao
				.findTransactionByLatestRequestEntry(orderModel, true);
		if (null == alipayPaymentTransaction)
		{
			LOG.warn("no AlipayPaymentTransaction found");
			return;
		}

		final AlipayPaymentTransactionEntryModel entry = modelService
				.create(AlipayPaymentTransactionEntryModel.class);
		entry.setType(PaymentTransactionType.CAPTURE);
		entry.setTransactionStatus(TransactionStatus.ERROR.name());
		entry.setTransactionStatusDetails("Error Code" + alipayRawDirectPayErrorInfo.getErrorCode());
		entry.setPayerEmail(alipayRawDirectPayErrorInfo.getBuyerEmail());
		entry.setPayerId(alipayRawDirectPayErrorInfo.getBuyerId());
		entry.setPaymentTransaction(alipayPaymentTransaction);
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
		modelService.save(entry);
		modelService.refresh(alipayPaymentTransaction);

	}

	@Override
	public Optional<AlipayPaymentTransactionModel> getPaymentTransactionWithCaptureEntry(final OrderModel orderModel,
			final TransactionStatus status)
	{
		final List<AlipayPaymentTransactionEntryModel> entryList = getPaymentTransactionEntryByType(orderModel, status,
				PaymentTransactionType.CAPTURE);

		if (entryList.size() == 1)
		{
			final AlipayPaymentTransactionModel alipayTransaction = (AlipayPaymentTransactionModel) entryList.get(0)
					.getPaymentTransaction();
			return Optional.of(alipayTransaction);
		}
		return Optional.empty();
	}

	@Override
	public Optional<AlipayPaymentTransactionEntryModel> getPaymentTransactionEntry(final OrderModel orderModel,
			final TransactionStatus status, final PaymentTransactionType paymentTransactionType)
	{
		final List<AlipayPaymentTransactionEntryModel> entryList = getPaymentTransactionEntryByType(orderModel, status,
				paymentTransactionType);

		if (entryList.size() == 1)
		{
			final AlipayPaymentTransactionEntryModel alipayTransactionEntry = entryList.get(0);
			return Optional.of(alipayTransactionEntry);
		}
		return Optional.empty();
	}

	@Override
	public Map<OrderModel, Boolean> updateForRefundNotification(final List<AlipayRefundData> alipayRefundData)
	{

		final Map<OrderModel, Boolean> refundStatus = new HashMap<>();
		for (final AlipayRefundData refundData : alipayRefundData)
		{
			final AlipayPaymentTransactionModel refundTransaction = alipayPaymentTransactionDao
					.findTransactionByAlipayCode(refundData.getAlipayCode());

			final OrderModel order = (OrderModel) refundTransaction.getOrder();

			final List<AlipayPaymentTransactionEntryModel> successRefundEntryList = alipayPaymentTransactionEntryDao
					.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.REFUND_STANDALONE, TransactionStatus.ACCEPTED,
							refundTransaction);

			final List<AlipayPaymentTransactionEntryModel> refundEntries = alipayPaymentTransactionEntryDao
					.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.REFUND_STANDALONE, TransactionStatus.REVIEW,
							refundTransaction);

			if (!successRefundEntryList.isEmpty() && !refundEntries.isEmpty())
			{
				continue;
			}

			if (CollectionUtils.isNotEmpty(refundEntries))
			{
				final AlipayPaymentTransactionEntryModel refundEntry = refundEntries.get(0);

				if ("SUCCESS".equals(refundData.getPayerRefundStatus()))
				{
					refundEntry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
					refundStatus.put(order, Boolean.TRUE);
				}
				else
				{
					refundEntry.setTransactionStatus(TransactionStatus.REJECTED.name());
					refundStatus.put(order, Boolean.FALSE);
				}
				refundEntry.setAdjustedAmount(refundData.getPayerRefundAmount());
				refundEntry.setTransactionStatusDetails(refundData.getPayerRefundStatus() + "; Refund Batch No: "
						+ refundData.getBatchNo());
				refundEntry.setTime(new Date());
				modelService.save(refundEntry);
				modelService.refresh(refundTransaction);
			}

		}
		return refundStatus;
	}

	@Override
	public void updateTransactionForRefundRequest(final OrderModel orderModel,
			final AlipayRefundRequestData alipayRefundRequestData)
	{
		final Optional<AlipayPaymentTransactionModel> result = getPaymentTransactionWithCaptureEntry(orderModel,
				TransactionStatus.ACCEPTED);
		result.ifPresent(alipayPaymentTransactionModel -> {
			final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntryModel = new AlipayPaymentTransactionEntryModel();
			alipayPaymentTransactionEntryModel.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
			if (orderModel.getCurrency() != null)
			{
				alipayPaymentTransactionEntryModel.setCurrency(orderModel.getCurrency());
			}
			alipayPaymentTransactionEntryModel.setType(PaymentTransactionType.REFUND_STANDALONE);
			alipayPaymentTransactionEntryModel.setTime(new Date());
			alipayPaymentTransactionEntryModel.setPaymentTransaction(alipayPaymentTransactionModel);
			alipayPaymentTransactionEntryModel.setRequestId(alipayPaymentTransactionModel.getRequestId());
			alipayPaymentTransactionEntryModel.setTransactionStatus(TransactionStatus.REVIEW.name());
			alipayPaymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name()
					+ "; Refund request: " + alipayRefundRequestData.getBatchNo());
			alipayPaymentTransactionEntryModel.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
			modelService.save(alipayPaymentTransactionEntryModel);
		});
	}

	protected List<AlipayPaymentTransactionEntryModel> getPaymentTransactionEntryByType(final OrderModel orderModel,
			final TransactionStatus status, final PaymentTransactionType paymentTransactionType)
	{
		for (final PaymentTransactionModel transaction : orderModel.getPaymentTransactions())
		{
			if (transaction instanceof AlipayPaymentTransactionModel)
			{
				final AlipayPaymentTransactionModel alipayTransaction = (AlipayPaymentTransactionModel) transaction;
				final List<AlipayPaymentTransactionEntryModel> entryList = alipayPaymentTransactionEntryDao
						.findPaymentTransactionEntryByTypeAndStatus(paymentTransactionType, status, alipayTransaction);
				if (alipayTransaction.getAlipayCode() != null && !entryList.isEmpty())
				{
					return entryList;
				}
			}
		}
		return Collections.emptyList();
	}


	protected AlipayPaymentTransactionModel getPaymentTransactionToUpdate(final OrderModel orderModel,
			final TransactionStatus status, final String alipayCode)
	{
		AlipayPaymentTransactionModel alipayPaymentTransaction = null;
		final Optional<AlipayPaymentTransactionModel> result = getPaymentTransactionWithCaptureEntry(orderModel, status);
		if (result.isPresent())
		{
			return alipayPaymentTransaction;
		}


		alipayPaymentTransaction = alipayPaymentTransactionDao.findTransactionByAlipayCode(alipayCode);
		if (alipayPaymentTransaction != null)
		{
			return alipayPaymentTransaction;
		}

		alipayPaymentTransaction = alipayPaymentTransactionDao.findTransactionByLatestRequestEntry(orderModel, true);
		if (alipayPaymentTransaction != null)
		{
			return alipayPaymentTransaction;
		}

		alipayPaymentTransaction = alipayPaymentTransactionDao.findTransactionByLatestRequestEntry(orderModel, false);
		if (alipayPaymentTransaction != null)
		{
			return alipayPaymentTransaction;
		}

		return alipayPaymentTransaction;
	}

	protected void setEntryByTransaction(final AlipayPaymentTransactionModel alipayPaymentTransaction,
			final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntry)
	{
		alipayPaymentTransactionEntry.setRequestId(alipayPaymentTransaction.getRequestId());
		alipayPaymentTransactionEntry.setPaymentTransaction(alipayPaymentTransaction);
	}

	protected void setEntryByRawPaymentStatus(final AlipayRawPaymentStatus alipayRawPaymentStatus,
			final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntry)
	{
		alipayPaymentTransactionEntry.setAdjustedAmount(Double.valueOf(alipayRawPaymentStatus.getTotalFee()));
		alipayPaymentTransactionEntry.setPayerEmail(alipayRawPaymentStatus.getBuyerEmail());
		alipayPaymentTransactionEntry.setPayerId(alipayRawPaymentStatus.getBuyerId());
		if (alipayRawPaymentStatus.getFlagTradeLocked() != null)
		{
			alipayPaymentTransactionEntry.setLocked("0".equals(alipayRawPaymentStatus.getFlagTradeLocked().trim()) ? Boolean.TRUE
					: Boolean.FALSE);
		}
		if (alipayRawPaymentStatus.getUseCoupon() != null)
		{
			alipayPaymentTransactionEntry.setCouponUsed("T".equals(alipayRawPaymentStatus.getUseCoupon().trim()) ? Boolean.TRUE
					: Boolean.FALSE);
		}
		alipayPaymentTransactionEntry.setSellerFee(Double.valueOf(alipayRawPaymentStatus.getToSellerFee()));
		alipayPaymentTransactionEntry.setSupplementaryStatus(alipayRawPaymentStatus.getAdditionalTradeStatus());
		alipayPaymentTransactionEntry.setTime(new Date());
		alipayPaymentTransactionEntry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
	}

	protected void setEntryByNotification(final AlipayRawDirectPayNotification directPayNotifyResponseData,
			final AlipayPaymentTransactionEntryModel alipayPaymentTransactionEntry)
	{
		alipayPaymentTransactionEntry.setTime(new Date());
		if (directPayNotifyResponseData.getUseCoupon() != null)
		{
			alipayPaymentTransactionEntry.setCouponUsed("T".equals(directPayNotifyResponseData.getUseCoupon().trim()) ? Boolean.TRUE
					: Boolean.FALSE);
		}
		alipayPaymentTransactionEntry.setPayerEmail(directPayNotifyResponseData.getBuyerEmail());
		alipayPaymentTransactionEntry.setPayerId(directPayNotifyResponseData.getBuyerId());
		alipayPaymentTransactionEntry.setAdjustedAmount(Double.valueOf(directPayNotifyResponseData.getTotalFee()));
		alipayPaymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);

		final String tradeStatus = directPayNotifyResponseData.getTradeStatus().trim();
		alipayPaymentTransactionEntry.setTransactionStatus(PaymentConstants.TransactionStatusMap.getAlipaytohybris()
				.get(tradeStatus).name());
		alipayPaymentTransactionEntry.setTransactionStatusDetails("Trade Status:" + tradeStatus);
		alipayPaymentTransactionEntry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
	}


	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setPaymentTransactionKeyGenerator(final KeyGenerator paymentTransactionKeyGenerator)
	{
		this.paymentTransactionKeyGenerator = paymentTransactionKeyGenerator;
	}


	@Required
	public void setAlipayPaymentTransactionEntryDao(final AlipayPaymentTransactionEntryDao alipayPaymentTransactionEntryDao)
	{
		this.alipayPaymentTransactionEntryDao = alipayPaymentTransactionEntryDao;
	}

	@Required
	public void setAlipayPaymentTransactionDao(final AlipayPaymentTransactionDao alipayPaymentTransactionDao)
	{
		this.alipayPaymentTransactionDao = alipayPaymentTransactionDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected KeyGenerator getPaymentTransactionKeyGenerator()
	{
		return paymentTransactionKeyGenerator;
	}

	protected AlipayPaymentTransactionEntryDao getAlipayPaymentTransactionEntryDao()
	{
		return alipayPaymentTransactionEntryDao;
	}

	protected AlipayPaymentTransactionDao getAlipayPaymentTransactionDao()
	{
		return alipayPaymentTransactionDao;
	}


}
