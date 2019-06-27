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
package com.sap.hybris.sec.eventpublisher.populator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.address.Address;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 *
 */
public class SECAddressPopulator implements Populator<AddressModel, Address>
{

	private static final Logger LOGGER = LogManager.getLogger(SECAddressPopulator.class);


	private ConfigurationService configurationService;

	@Override
	public void populate(final AddressModel source, final Address target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateAddressFields(source, target);

	}

	protected String getModelId(final AddressModel customerAddressModel){
		String hybrisCustomerId = null;
		Object owner = customerAddressModel.getOwner();
		if(owner instanceof CustomerModel){
                        CustomerModel customerModel = (CustomerModel) customerAddressModel.getOwner();
                        hybrisCustomerId = customerModel.getCustomerID();
                }
		return hybrisCustomerId;
	}

	protected boolean isDefaultAddress(final AddressModel customerAddressModel){
		Object owner = customerAddressModel.getOwner();
                if(owner instanceof CustomerModel){
			CustomerModel customerModel = (CustomerModel) customerAddressModel.getOwner();
			return customerModel.getDefaultShipmentAddress() != null && customerModel.getDefaultShipmentAddress().getPk().toString().equals(customerAddressModel.getPk().toString());
		} else {
			return false;
		}
	}
	/**
	* 
	*/
	protected void populateAddressFields(final AddressModel customerAddressModel, final Address customerAddressData) {
		ItemModel owner = customerAddressModel.getOwner();
		String hybrisCustomerId = getModelId(customerAddressModel);
		customerAddressData.setHybrisCustomerId(hybrisCustomerId);

        	boolean isDefaultAddress = isDefaultAddress(customerAddressModel);
		customerAddressData.setIsDefault(isDefaultAddress);

		customerAddressData.setHybrisCustomerAddressId(customerAddressModel.getPk().toString());
		final String countryIsoCode = customerAddressModel.getCountry() != null
				? customerAddressModel.getCountry().getIsocode()
				: null;
		customerAddressData
				.setContactName(customerAddressModel.getFirstname() + " " + customerAddressModel.getLastname());
		customerAddressData.setCountry(countryIsoCode);
		customerAddressData.setStreet(customerAddressModel.getStreetname());
		customerAddressData.setStreetNumber(customerAddressModel.getStreetnumber());
		customerAddressData.setStreetAppendix(customerAddressModel.getStreetnumber());
		customerAddressData.setCity(customerAddressModel.getTown());
		customerAddressData.setZipCode(customerAddressModel.getPostalcode());
		customerAddressData.setExtraLine1(customerAddressModel.getLine1());
		customerAddressData.setExtraLine2(customerAddressModel.getLine2());
		final String regionIsoCode = customerAddressModel.getRegion() != null
				? customerAddressModel.getRegion().getIsocodeShort()
				: null;
		customerAddressData.setState(regionIsoCode);
		customerAddressData.setContactPhone(customerAddressModel.getPhone1());

		final boolean isBiliingAddress = customerAddressModel.getBillingAddress().booleanValue();
		final boolean isShippingAddress = customerAddressModel.getShippingAddress().booleanValue();

		if (isShippingAddress) {
			customerAddressData.getTags().add(EventpublisherConstants.SHIPPING_ADDRESS);
		}
		if (isBiliingAddress) {
			customerAddressData.getTags().add(EventpublisherConstants.BILLING_ADDRESS);
		}
		customerAddressData.setOwnerType(owner.getItemtype());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Address JSON:" + customerAddressData.toString());
		}

	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
