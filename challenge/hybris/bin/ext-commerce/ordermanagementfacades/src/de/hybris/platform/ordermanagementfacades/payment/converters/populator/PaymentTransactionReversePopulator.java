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
package de.hybris.platform.ordermanagementfacades.payment.converters.populator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;


/**
 * Ordermanagement populator for converting {@link PaymentTransactionData}
 */
public class PaymentTransactionReversePopulator implements Populator<PaymentTransactionData, PaymentTransactionModel>
{

	private Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> cardPaymentInfoReverseConverter;
	private Converter<PaymentTransactionEntryData, PaymentTransactionEntryModel> paymentTransactionEntryReverseConverter;
	private Converter<AddressData, AddressModel> addressReverseConverter;

	private CommonI18NService commonI18NService;

	@Override
	public void populate(final PaymentTransactionData source, final PaymentTransactionModel target)
	{
		if (source != null && target != null)
		{
			target.setRequestId(source.getRequestId());
			target.setRequestToken(source.getRequestToken());
			target.setPaymentProvider(source.getPaymentProvider());
			target.setPlannedAmount(source.getPlannedAmount());
			target.setCurrency(getCommonI18NService().getCurrency(source.getCurrencyIsocode()));
			target.setVersionID(source.getVersionID());

			if(source.getPaymentInfo()!=null)
			{
				final PaymentInfoModel paymentInfoModel = getCardPaymentInfoReverseConverter().convert(source.getPaymentInfo());
				final AddressModel billingAddress = getAddressReverseConverter().convert(source.getPaymentInfo().getBillingAddress());
				billingAddress.setOwner(paymentInfoModel);
				paymentInfoModel.setBillingAddress(billingAddress);
				if (paymentInfoModel instanceof CreditCardPaymentInfoModel)
				{
					target.setInfo(paymentInfoModel);
				}
			}

			final List<PaymentTransactionEntryModel> paymentTransactionEntries = new ArrayList<>();
			source.getEntries().forEach(paymentTransactionEntryData ->
			{
				final PaymentTransactionEntryModel paymentTransactionEntry = getPaymentTransactionEntryReverseConverter()
						.convert(paymentTransactionEntryData);
				paymentTransactionEntry.setPaymentTransaction(target);
				paymentTransactionEntries.add(paymentTransactionEntry);
			});
			target.setEntries(paymentTransactionEntries);
		}

	}

	protected Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> getCardPaymentInfoReverseConverter()
	{
		return cardPaymentInfoReverseConverter;
	}

	@Required
	public void setCardPaymentInfoReverseConverter(
			final Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> cardPaymentInfoReverseConverter)
	{
		this.cardPaymentInfoReverseConverter = cardPaymentInfoReverseConverter;
	}

	protected Converter<PaymentTransactionEntryData, PaymentTransactionEntryModel> getPaymentTransactionEntryReverseConverter()
	{
		return paymentTransactionEntryReverseConverter;
	}

	@Required
	public void setPaymentTransactionEntryReverseConverter(
			final Converter<PaymentTransactionEntryData, PaymentTransactionEntryModel> paymentTransactionEntryReverseConverter)
	{
		this.paymentTransactionEntryReverseConverter = paymentTransactionEntryReverseConverter;
	}

	protected Converter<AddressData, AddressModel> getAddressReverseConverter()
	{
		return addressReverseConverter;
	}

	@Required
	public void setAddressReverseConverter(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
