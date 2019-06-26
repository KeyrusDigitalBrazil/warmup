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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import java.io.IOException;

import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.address.Address;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteAddressEvent;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.tx.AfterSaveEvent;

/**
 * Replicate the updated/created address to target
 */
public class AfterAddressSaveEventHandler extends DefaultSaveEventHandler {
	private Populator addressPopulator;
	private EventService eventService;

	@Override
	public boolean shouldHandle(AfterSaveEvent event, ItemModel model) throws IOException {
		if (event.getType() == AfterSaveEvent.REMOVE) {
			return true;
		}else if (model instanceof AddressModel && model.getOwner() instanceof CustomerModel) {
			AddressModel addressModel = (AddressModel) model;
			if (!checkGuestCustomer(addressModel)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ResponseData handle(AfterSaveEvent event, ItemModel model) throws IOException, ReflectiveOperationException {
		ResponseData resData = null;
		if (event.getType() == AfterSaveEvent.REMOVE) {
			DefaultSecDeleteAddressEvent deleteEvent = new DefaultSecDeleteAddressEvent();
			deleteEvent.setAddressId(event.getPk().toString());
			getEventService().publishEvent(deleteEvent);
		} else if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (model instanceof AddressModel)) {
			AddressModel addressModel = (AddressModel) model;
			resData = createOrUpdateCustomerAddress(addressModel);
		}
		return resData;
	}

	private boolean checkGuestCustomer(final AddressModel addressModel) {
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
		return CustomerType.GUEST.equals(customerModel.getType());
	}

	/**
	 * @param addressModel
	 * @throws ReflectiveOperationException
	 * @throws IOException
	 *
	 */
	private ResponseData createOrUpdateCustomerAddress(final AddressModel addressModel)
			throws IOException, ReflectiveOperationException {
		final Address addressJson = new Address();
		getAddressPopulator().populate(addressModel, addressJson);
		final ResponseData resData = publish(getFinalJson(addressModel, addressJson.toString()),
				addressModel.getItemtype());
		return resData;
	}

	/**
	 * @return the addressPopulator
	 */
	public Populator getAddressPopulator() {
		return addressPopulator;
	}

	/**
	 * @param addressPopulator
	 *            the addressPopulator to set
	 */
	public void setAddressPopulator(final Populator addressPopulator) {
		this.addressPopulator = addressPopulator;
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

}
