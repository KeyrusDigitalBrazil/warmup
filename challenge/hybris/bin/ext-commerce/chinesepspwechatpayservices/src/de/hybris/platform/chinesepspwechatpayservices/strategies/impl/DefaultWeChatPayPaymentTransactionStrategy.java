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
package de.hybris.platform.chinesepspwechatpayservices.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayPaymentTransactionDao;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayPaymentTransactionEntryDao;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatPayQueryResult;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentTransactionStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Supplier;


public class DefaultWeChatPayPaymentTransactionStrategy implements WeChatPayPaymentTransactionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultWeChatPayPaymentTransactionStrategy.class.getName());

	private ModelService modelService;
	private KeyGenerator paymentTransactionKeyGenerator;
	private WeChatPayPaymentTransactionDao weChatPayPaymentTransactionDao;
	private WeChatPayPaymentTransactionEntryDao weChatPayPaymentTransactionEntryDao;


	@Override
	public void createForNewRequest(final OrderModel orderModel)
	{
		final WeChatPayPaymentTransactionModel transaction = createTransacionForNewRequest(orderModel);
		createTransactionEntryForNewRequest(orderModel, transaction);
	}

	protected final WeChatPayPaymentTransactionModel createTransacionForNewRequest(final OrderModel orderModel)
	{
		final WeChatPayPaymentTransactionModel transaction = modelService.create(WeChatPayPaymentTransactionModel.class);
		transaction.setOrder(orderModel);
		transaction.setCode(orderModel.getCode());
		transaction.setRequestId(orderModel.getCode());
		transaction.setPaymentProvider(WeChatPaymentConstants.Basic.PAYMENT_PROVIDER);
		transaction.setCreationtime(new Date());
		modelService.save(transaction);
		return transaction;
	}

	protected void createTransactionEntryForNewRequest(final OrderModel orderModel,
			final WeChatPayPaymentTransactionModel transaction)
	{
		final WeChatPayPaymentTransactionEntryModel entry = modelService.create(WeChatPayPaymentTransactionEntryModel.class);
		entry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
		if (orderModel.getCurrency() != null)
		{
			entry.setCurrency(orderModel.getCurrency());
		}
		entry.setType(PaymentTransactionType.WECHAT_REQUEST);
		entry.setTime(new Date());
		entry.setPaymentTransaction(transaction);
		entry.setRequestId(transaction.getRequestId());
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
		modelService.save(entry);
	}

	@Override
	public void updateForNotification(final OrderModel orderModel,
			final WeChatRawDirectPayNotification weChatPayNotifyResponseData)
	{
		validateParameterNotNull(orderModel, "The given order is null!");
		validateParameterNotNull(weChatPayNotifyResponseData, "The given notifyData is null!");

		if (weChatPayNotifyResponseData.getOutTradeNo() != null)
		{
			final TransactionStatus status;
			if (WeChatPaymentConstants.Notification.RESULT_SUCCESS.equals(weChatPayNotifyResponseData.getResultCode()))
			{
				status = TransactionStatus.ACCEPTED;
			}
			else
			{
				status = TransactionStatus.REJECTED;
			}
			final Optional<WeChatPayPaymentTransactionModel> weChatPayPaymentTransactionModel = getPaymentTransactionToUpdate(
					orderModel, status, weChatPayNotifyResponseData.getTransactionId());

			weChatPayPaymentTransactionModel.ifPresent(weChatPayTransaction -> {
				weChatPayTransaction.setWeChatPayCode(weChatPayNotifyResponseData.getTransactionId());
				getModelService().save(weChatPayTransaction);
				final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry = getModelService()
						.create(WeChatPayPaymentTransactionEntryModel.class);
				setEntryByTransaction(weChatPayTransaction, weChatPayPaymentTransactionEntry);
				setEntryByNotification(weChatPayNotifyResponseData, weChatPayPaymentTransactionEntry, status);
				weChatPayPaymentTransactionEntry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
				getModelService().save(weChatPayPaymentTransactionEntry);
				LOG.info("Update transaction for WeChat's notification suceessfully");
			});
		}
	}

	@Override
	public Optional<WeChatPayPaymentTransactionEntryModel> saveForStatusCheck(final OrderModel orderModel,
			final WeChatPayQueryResult weChatPayQueryResult)
	{
		validateParameterNotNull(orderModel, "The given order is null!");
		validateParameterNotNull(weChatPayQueryResult, "The given status data is null!");

		final String error = weChatPayQueryResult.getErrCode();
		if (error == null && weChatPayQueryResult.getOutTradeNo() != null)
		{
			final TransactionStatus status = WeChatPaymentConstants.TransactionStatusMap.getWechatpaytohybris()
					.get(weChatPayQueryResult.getTradeState());
			if (status == null)
			{
				return Optional.empty();
			}

			final Optional<WeChatPayPaymentTransactionModel> weChatPayPaymentTransactionModel = getPaymentTransactionToUpdate(
					orderModel, status, weChatPayQueryResult.getOutTradeNo());
			return savePaymentTransactionEntryForQueryOrder(orderModel, weChatPayQueryResult, status,
					weChatPayPaymentTransactionModel);

		}
		return Optional.empty();
	}

	private Optional<WeChatPayPaymentTransactionEntryModel> savePaymentTransactionEntryForQueryOrder(final OrderModel orderModel,
			final WeChatPayQueryResult weChatPayQueryResult, final TransactionStatus status,
			final Optional<WeChatPayPaymentTransactionModel> weChatPayPaymentTransactionModel)
	{
		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry = modelService
				.create(WeChatPayPaymentTransactionEntryModel.class);
		weChatPayPaymentTransactionModel.ifPresent(weChatPayPaymentTransaction -> {
			weChatPayPaymentTransaction.setWeChatPayCode(weChatPayQueryResult.getTransactionId());
			modelService.save(weChatPayPaymentTransaction);

			setEntryByTransaction(weChatPayPaymentTransaction, weChatPayPaymentTransactionEntry);
			setEntryByQueryResult(weChatPayQueryResult, weChatPayPaymentTransactionEntry);

			weChatPayPaymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);
			weChatPayPaymentTransactionEntry.setTransactionStatus(status.name());
			weChatPayPaymentTransactionEntry.setTransactionStatusDetails("Trade Status:" + weChatPayQueryResult.getTradeState());
			weChatPayPaymentTransactionEntry.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue()));
			modelService.save(weChatPayPaymentTransactionEntry);
			modelService.refresh(weChatPayPaymentTransaction);

		});

		if (weChatPayPaymentTransactionEntry.getCode() == null)
		{
			return Optional.empty();
		}
		return Optional.of(weChatPayPaymentTransactionEntry);
	}

	protected Optional<WeChatPayPaymentTransactionModel> getPaymentTransactionToUpdate(final OrderModel orderModel,
			final TransactionStatus status, final String weChatPayCode)
	{
		final Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = getPaymentTransactionWithCaptureEntry(orderModel,
				status);
		if (weChatPayTransaction.isPresent())
		{
			//If the transaction has a captured entry, this transaction has been updated already. Don't need to be updated again.
			return Optional.empty();
		}

		final Supplier<Optional<WeChatPayPaymentTransactionModel>> supplier1 = () -> getWeChatPayPaymentTransactionDao()
				.findTransactionByWeChatPayCode(weChatPayCode);
		final Supplier<Optional<WeChatPayPaymentTransactionModel>> supplier2 = () -> getWeChatPayPaymentTransactionDao()
				.findTransactionByLatestRequestEntry(orderModel, true);
		final Supplier<Optional<WeChatPayPaymentTransactionModel>> supplier3 = () -> getWeChatPayPaymentTransactionDao()
				.findTransactionByLatestRequestEntry(orderModel, false);
		return Stream.of(supplier1, supplier2, supplier3).map(Supplier::get).filter(Optional::isPresent).findFirst()
				.orElse(Optional.empty());
	}

	protected Optional<WeChatPayPaymentTransactionModel> getPaymentTransactionWithCaptureEntry(final OrderModel orderModel,
			final TransactionStatus status)
	{
		final List<WeChatPayPaymentTransactionEntryModel> entryList = getPaymentTransactionEntryByType(orderModel, status,
				PaymentTransactionType.CAPTURE);

		if (entryList.size() == 1)
		{
			final WeChatPayPaymentTransactionModel weChatPayTransaction = (WeChatPayPaymentTransactionModel) entryList.get(0)
					.getPaymentTransaction();
			return Optional.of(weChatPayTransaction);
		}
		return Optional.empty();
	}

	protected List<WeChatPayPaymentTransactionEntryModel> getPaymentTransactionEntryByType(final OrderModel orderModel,
			final TransactionStatus status, final PaymentTransactionType paymentTransactionType)
	{
		for (final PaymentTransactionModel transaction : orderModel.getPaymentTransactions())
		{
			final WeChatPayPaymentTransactionModel weChatPayTransaction = (WeChatPayPaymentTransactionModel) transaction;
			if (transaction instanceof WeChatPayPaymentTransactionModel && weChatPayTransaction.getWeChatPayCode() != null)
			{
				final List<WeChatPayPaymentTransactionEntryModel> entryList = getWeChatPayPaymentTransactionEntryDao()
						.findPaymentTransactionEntryByTypeAndStatus(paymentTransactionType, status, weChatPayTransaction);
				if (!entryList.isEmpty())
				{
					return entryList;
				}
			}
		}
		return Collections.emptyList();
	}

	protected void setEntryByTransaction(final WeChatPayPaymentTransactionModel weChatPayPaymentTransaction,
			final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry)
	{
		weChatPayPaymentTransactionEntry.setRequestId(weChatPayPaymentTransaction.getRequestId());
		weChatPayPaymentTransactionEntry.setPaymentTransaction(weChatPayPaymentTransaction);
	}

	protected void setEntryByNotification(final WeChatRawDirectPayNotification weChatPayNotifyResponseData,
			final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry, final TransactionStatus status)
	{
		weChatPayPaymentTransactionEntry.setTime(new Date());
		weChatPayPaymentTransactionEntry.setOpenId(weChatPayNotifyResponseData.getOpenid());
		weChatPayPaymentTransactionEntry.setCouponFee(Double.valueOf(weChatPayNotifyResponseData.getCouponFee() / 100));
		weChatPayPaymentTransactionEntry.setSettlementTotalFee(Double.valueOf(weChatPayNotifyResponseData.getTotalFee() / 100));
		weChatPayPaymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);
		weChatPayPaymentTransactionEntry.setTransactionStatus(status.name());
		weChatPayPaymentTransactionEntry.setTransactionStatusDetails("Trade Status:" + status);
		weChatPayPaymentTransactionEntry.setCode(String.valueOf(getPaymentTransactionKeyGenerator().generate()));
	}

	protected void setEntryByQueryResult(final WeChatPayQueryResult weChatPayQueryResult,
			final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry)
	{
		weChatPayPaymentTransactionEntry.setTime(new Date());
		weChatPayPaymentTransactionEntry.setOpenId(weChatPayQueryResult.getOpenid());
		weChatPayPaymentTransactionEntry.setCouponFee(Double.valueOf(weChatPayQueryResult.getCouponFee() / 100));
		weChatPayPaymentTransactionEntry.setSettlementTotalFee(Double.valueOf(weChatPayQueryResult.getTotalFee()));
		weChatPayPaymentTransactionEntry.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()));
	}

	protected WeChatPayPaymentTransactionDao getWeChatPayPaymentTransactionDao()
	{
		return weChatPayPaymentTransactionDao;
	}

	@Required
	public void setWeChatPayPaymentTransactionDao(final WeChatPayPaymentTransactionDao weChatPayPaymentTransactionDao)
	{
		this.weChatPayPaymentTransactionDao = weChatPayPaymentTransactionDao;
	}

	protected WeChatPayPaymentTransactionEntryDao getWeChatPayPaymentTransactionEntryDao()
	{
		return weChatPayPaymentTransactionEntryDao;
	}

	@Required
	public void setWeChatPayPaymentTransactionEntryDao(
			final WeChatPayPaymentTransactionEntryDao weChatPayPaymentTransactionEntryDao)
	{
		this.weChatPayPaymentTransactionEntryDao = weChatPayPaymentTransactionEntryDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected KeyGenerator getPaymentTransactionKeyGenerator()
	{
		return paymentTransactionKeyGenerator;
	}

	@Required
	public void setPaymentTransactionKeyGenerator(final KeyGenerator paymentTransactionKeyGenerator)
	{
		this.paymentTransactionKeyGenerator = paymentTransactionKeyGenerator;
	}


}
