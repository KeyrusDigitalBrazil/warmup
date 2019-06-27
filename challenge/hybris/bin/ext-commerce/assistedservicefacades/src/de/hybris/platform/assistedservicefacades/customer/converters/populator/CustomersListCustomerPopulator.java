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
package de.hybris.platform.assistedservicefacades.customer.converters.populator;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Class used for populating customer's profile picture, address and recent cart information
 *
 */

public class CustomersListCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private CustomerAccountService customerAccountService;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<MediaModel, ImageData> imageConverter;
	private AssistedServiceService assistedServiceService;
	private BaseSiteService baseSiteService;

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		final AddressModel defaultAddress = getCustomerAccountService().getDefaultAddress(source);
		final CartModel latestCart = getAssistedServiceService().getLatestModifiedCart(source);

		if (null != latestCart && !latestCart.getEntries().isEmpty())
		{
			target.setLatestCartId(latestCart.getCode());
		}

		target.setHasOrder( CollectionUtils.isNotEmpty(source.getOrders()) &&
			source.getOrders().stream().anyMatch(
				orderModel -> getBaseSiteService().getCurrentBaseSite().getUid().equals(orderModel.getSite().getUid())));

		if (null != defaultAddress)
		{
			final AddressData customerAddress = addressConverter.convert(defaultAddress);
			target.setDefaultAddress(customerAddress);
		}

		if (null != source.getProfilePicture())
		{
			target.setProfilePicture(getImageConverter().convert(source.getProfilePicture()));
		}
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

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected AssistedServiceService getAssistedServiceService()
	{
		return assistedServiceService;
	}

	@Required
	public void setAssistedServiceService(final AssistedServiceService assistedServiceService)
	{
		this.assistedServiceService = assistedServiceService;
	}

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
