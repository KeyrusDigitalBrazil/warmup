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
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.allocation.impl.DefaultAllocationService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;

/**
 * Service to create consignments for physical products and skipping subscription products . This implementation assumes physical and subscriptions products are assigned to different warehouses.
*/

public class DefaultRevenueCloudAllocationService extends DefaultAllocationService{

	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultRevenueCloudAllocationService.class);
	

	/**
	 * This implementation assumes that all order entries in SourcingResult have the same delivery POS.
	 */
	@Override
	public ConsignmentModel createConsignment(final AbstractOrderModel order, final String code, final SourcingResult result)
	{
		Preconditions.checkArgument(Objects.nonNull(order), "Parameter order cannot be null.");
		Preconditions.checkArgument(Objects.nonNull(result), "Parameter result cannot be null.");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(code), "Parameter code cannot be null or empty");
		Predicate<Entry<AbstractOrderEntryModel, Long>> isSubscriptionProduct = entry -> entry.getKey().getProduct().getSubscriptionCode()!=null? Boolean.TRUE : Boolean.FALSE;
		LOGGER.debug("Creating consignment for Location: '" + result.getWarehouse().getCode() + "'");
		final Set<Entry<AbstractOrderEntryModel, Long>> resultEntries = result.getAllocation().entrySet();

		final Optional<PointOfServiceModel> pickupPos = resultEntries.stream()
				.map(entry -> entry.getKey().getDeliveryPointOfService()).filter(Objects::nonNull).findFirst();
		final ConsignmentModel consignment = getModelService().create(ConsignmentModel.class);
		consignment.setCode(code);
		consignment.setOrder(order);
		LOGGER.info("Creating Consignments");
		// Setting subscription products flag on consignment
		 resultEntries.stream().forEach(entry ->{
			 LOGGER.debug("Iterating all sourcing results");
			 LOGGER.debug(entry.getKey().getProduct().getSubscriptionCode());
		 });
		
		boolean hasAllSubscriptionProducts = resultEntries.stream().allMatch(isSubscriptionProduct);
		if(hasAllSubscriptionProducts) 
		{
			consignment.setSubscriptionProducts(Boolean.TRUE);
		}
		if (pickupPos.isPresent())
		{
			consignment.setStatus(ConsignmentStatus.READY);
			consignment.setDeliveryMode(getDeliveryModeService().getDeliveryModeForCode(PICKUP_CODE));
			//This cannot be null so we put the POS address as placeholder
			consignment.setShippingAddress(pickupPos.get().getAddress());
			consignment.setDeliveryPointOfService(pickupPos.get());
		}
		else
		{
			consignment.setStatus(ConsignmentStatus.READY);
			consignment.setDeliveryMode(order.getDeliveryMode());
			consignment.setShippingAddress(order.getDeliveryAddress());
			consignment.setShippingDate(getShippingDateStrategy().getExpectedShippingDate(consignment));
		}

		final Set<ConsignmentEntryModel> entries = resultEntries.stream()
				.map(mapEntry -> createConsignmentEntry(mapEntry.getKey(), mapEntry.getValue(), consignment))
				.collect(Collectors.toSet());
		consignment.setConsignmentEntries(entries);
		consignment.setWarehouse(result.getWarehouse());
		getModelService().save(consignment);
		if (!consignment.getWarehouse().isExternal())
		{
			getInventoryEventService().createAllocationEvents(consignment);
		}
		return consignment;
	}
	
}
