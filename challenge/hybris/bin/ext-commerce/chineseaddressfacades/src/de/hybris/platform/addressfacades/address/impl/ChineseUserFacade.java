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
package de.hybris.platform.addressfacades.address.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * 
 * Overrides DefaultUserFacade.editAddress() to clear existing city and district value
 *
 */
public class ChineseUserFacade extends DefaultUserFacade implements UserFacade
{
	@Override
	public void editAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
		addressModel.setRegion(null);
		addressModel.setCity(null);
		addressModel.setCityDistrict(null);
		getAddressReversePopulator().populate(addressData, addressModel);
		getCustomerAccountService().saveAddressEntry(currentCustomer, addressModel);
		if (addressData.isDefaultAddress())
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
		}
		else if (addressModel.equals(currentCustomer.getDefaultShipmentAddress()))
		{
			getCustomerAccountService().clearDefaultAddressEntry(currentCustomer);
		}
	}

}
