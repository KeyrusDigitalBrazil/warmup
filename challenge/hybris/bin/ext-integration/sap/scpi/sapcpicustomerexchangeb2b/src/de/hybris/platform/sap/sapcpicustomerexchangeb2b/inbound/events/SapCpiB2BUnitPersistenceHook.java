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
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.inbound.events;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SapCpiB2BUnitPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiB2BUnitPersistenceHook.class);

	private static final String DELETE_MESSAGE_FUNCTION = "003";
	private static final String UPDATE_MESSAGE_FUNCTION = "004";

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof B2BUnitModel)
		{
			LOG.info("The persistence hook sapCpiB2BUnitPersistenceHook is called!");
			return processB2BUnitAddresses((B2BUnitModel) item);
		}

		return Optional.of(item);
	}

	private Optional<ItemModel> processB2BUnitAddresses(B2BUnitModel b2bUnitModel)
	{
		//Remove duplicates addresses
		final List<AddressModel> addresses = new ArrayList<>(new HashSet<>(b2bUnitModel.getAddresses()));

		// New and existing addresses
		final List<AddressModel> newAndExistingAddresses = new ArrayList<>();
		addresses.stream()
				.filter(address -> isCreateAddress(address))
				.forEach(newAndExistingAddresses::add);

		// Updated addresses
		final List<AddressModel> updatedAddresses = new ArrayList<>();
		b2bUnitModel.getAddresses().stream()
				.filter(address -> isUpdateAddress(address))
				.forEach(updatedAddresses::add);

		// Addresses to persist
		final List<AddressModel> addressesToSave = new ArrayList<>();

		// Add updated Addresses
		updatedAddresses.forEach(addressesToSave::add);

		// Add new and existing (not updated) Addresses
		newAndExistingAddresses.stream().filter(address -> isNewAddress(address, updatedAddresses)).forEach(addressesToSave::add);

		addressesToSave.forEach(address -> address.setSapMessageFunction(null));
		b2bUnitModel.setAddresses(addressesToSave);

		return Optional.of(b2bUnitModel);

	}

	private boolean isCreateAddress(AddressModel addressModel)
	{
		return !(DELETE_MESSAGE_FUNCTION.equalsIgnoreCase(addressModel.getSapMessageFunction()) ||
				UPDATE_MESSAGE_FUNCTION.equalsIgnoreCase(addressModel.getSapMessageFunction()));
	}

	private boolean isUpdateAddress(AddressModel addressModel)
	{
		return UPDATE_MESSAGE_FUNCTION.equalsIgnoreCase(addressModel.getSapMessageFunction());
	}

	private boolean isNewAddress(AddressModel address, List<AddressModel> updatedAddresses)
	{
		return !updatedAddresses.stream().filter(updatedAddress -> isUpdatedAddress(updatedAddress, address)).findFirst().isPresent();
	}

	private boolean isUpdatedAddress(AddressModel updatedAddress, AddressModel address)
	{

		return updatedAddress.getSapAddressUsage() != null &&
						updatedAddress.getSapAddressUsage().equalsIgnoreCase(address.getSapAddressUsage()) &&
						updatedAddress.getSapAddressUsageCounter() != null &&
						updatedAddress.getSapAddressUsageCounter().equalsIgnoreCase(address.getSapAddressUsageCounter());
	}

}
