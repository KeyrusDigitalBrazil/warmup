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
package de.hybris.platform.assistedservicestorefront.customer360.populators;

import de.hybris.platform.assistedservicestorefront.customer360.CustomerProfileData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;


/**
 * CustomerModel -> CustomerProfileData populator
 *
 */
public class CustomerProfileDataPopulator implements Populator<CustomerModel, CustomerProfileData>
{
	private CustomerAccountService customerAccountService;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private int paymentMethodsListSize;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CustomerModel userModel, final CustomerProfileData customerProfileData)
	{
		//The default Delivery Address, should always be the default address selected in the My Account / Address Book
		// Phone 2 needs to come from Delivery Address (if present)
		if (userModel.getDefaultShipmentAddress() != null)
		{
			customerProfileData.setDeliveryAddress(getAddressConverter().convert(userModel.getDefaultShipmentAddress()));
			if (userModel.getDefaultShipmentAddress().getPhone1() != null)
			{
				customerProfileData.setPhone2(userModel.getDefaultShipmentAddress().getPhone1());
			}
		}
		else if (!userModel.getAddresses().isEmpty())
		{
			final AddressModel addressModel = userModel.getAddresses().stream().findFirst().get();
			customerProfileData.setDeliveryAddress(getAddressConverter().convert(addressModel));
			if (addressModel.getPhone1() != null)
			{
				customerProfileData.setPhone2(addressModel.getPhone1());
			}
		}

		// Phone 1 needs to come from Billing Address (if present)
		if ( userModel.getDefaultPaymentInfo() != null && userModel.getDefaultPaymentInfo().getBillingAddress() != null)
		{
			customerProfileData.setBillingAddress(getAddressConverter().convert(userModel.getDefaultPaymentInfo().getBillingAddress()));

			if (userModel.getDefaultPaymentInfo().getBillingAddress().getPhone1() != null)
			{
				customerProfileData.setPhone1(userModel.getDefaultPaymentInfo().getBillingAddress().getPhone1());
			}
		}
		else if (!userModel.getPaymentInfos().isEmpty()) // If there is no default billing address in EC, then display the last used payment details billing address under Billing Address
		{
			final PaymentInfoModel paymentInfoModel = userModel.getPaymentInfos().stream().findFirst().get();
			customerProfileData.setBillingAddress(getAddressConverter().convert(paymentInfoModel.getBillingAddress()));
			if (paymentInfoModel.getBillingAddress().getPhone1() != null)
			{
				customerProfileData.setPhone1(paymentInfoModel.getBillingAddress().getPhone1());
			}
		}

		customerProfileData.setPaymentInfoList(getPaymentInfoList(userModel));
	}

	protected List<CCPaymentInfoData> getPaymentInfoList(final CustomerModel userModel)
	{
		final PaymentInfoModel defaultPaymentInfoModel = userModel.getDefaultPaymentInfo();
		List<CCPaymentInfoData> ccPaymentInfoData = null;
		List<CreditCardPaymentInfoModel> paymentInfoModels= new ArrayList<CreditCardPaymentInfoModel>();
		paymentInfoModels.addAll(getCustomerAccountService().getCreditCardPaymentInfos(userModel, true));
		if (defaultPaymentInfoModel != null && CollectionUtils.isNotEmpty(paymentInfoModels))
		{
			paymentInfoModels.sort((p1, p2) -> {
				if (p1.getCode().equals(defaultPaymentInfoModel.getCode()))
				{
					return -1;
				}
				if (p2.getCode().equals(defaultPaymentInfoModel.getCode()))
				{
					return 1;
				}
				return p1.getCreationtime().compareTo(p2.getCreationtime());
			});
			if (paymentInfoModels.size() > getPaymentMethodsListSize())
			{
				paymentInfoModels = paymentInfoModels.subList(0, getPaymentMethodsListSize());
			}
			ccPaymentInfoData = getCreditCardPaymentInfoConverter().convertAll(paymentInfoModels);
			ccPaymentInfoData.get(0).setDefaultPaymentInfo(true);
		}
		return ccPaymentInfoData;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected int getPaymentMethodsListSize()
	{
		return paymentMethodsListSize;
	}

	@Required
	public void setPaymentMethodsListSize(final int paymentMethodsListSize)
	{
		this.paymentMethodsListSize = paymentMethodsListSize;
	}

}
