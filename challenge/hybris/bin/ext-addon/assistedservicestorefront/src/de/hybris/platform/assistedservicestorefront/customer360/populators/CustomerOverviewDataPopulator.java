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

import de.hybris.platform.assistedservicestorefront.customer360.CustomerOverviewData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;


/**
 * CustomerModel -> CustomerOverviewData populator
 *
 */
public class CustomerOverviewDataPopulator implements Populator<CustomerModel, CustomerOverviewData>
{
	private Converter<MediaModel, ImageData> imageConverter;
	private Converter<AddressModel, AddressData> addressConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CustomerModel customerModel, final CustomerOverviewData customerOverviewData)
	{
		customerOverviewData.setFullName(customerModel.getName());
		customerOverviewData.setEmail(customerModel.getUid());
		customerOverviewData.setSignedUp(customerModel.getCreationtime());
		final AddressModel defaultShipmentAddress = customerModel.getDefaultShipmentAddress();

		if (defaultShipmentAddress != null)
		{
			customerOverviewData.setAddress(getAddressConverter().convert(defaultShipmentAddress));
		}

		if (null != customerModel.getProfilePicture())
		{
			customerOverviewData.setProfilePicture(getImageConverter().convert(customerModel.getProfilePicture()));
		}

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

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

}
