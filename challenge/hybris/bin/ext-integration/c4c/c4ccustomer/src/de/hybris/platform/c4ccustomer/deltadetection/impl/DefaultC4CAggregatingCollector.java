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
package de.hybris.platform.c4ccustomer.deltadetection.impl;

import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.enums.ChangeType;
import de.hybris.deltadetection.model.ItemVersionMarkerModel;
import de.hybris.platform.c4ccustomer.deltadetection.C4CAggregatingCollector;
import de.hybris.platform.c4ccustomer.deltadetection.collector.C4CBatchingCollector;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableMap;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Collector that grabs changes of {@link CustomerModel} models and fetches related {@link AddressModel} and vice versa.
 * So, as a result there is a set of changed customers with all addresses belonging to each. 'Changed' here stands for
 * <i>'something has changed in customer fields or any of the addresses related'</i>.
 *
 * <p>
 * Deleted customers are skipped, because C4C does not implement customer removal yet.
 * </p>
 */
public class DefaultC4CAggregatingCollector implements C4CAggregatingCollector
{
	private static final Logger LOG = Logger.getLogger(DefaultC4CAggregatingCollector.class);

	/**
	 * Name of primary key param in all queries.
	 */
	protected static final String PRIMARY_KEY_PARAM_NAME = "PK";

	/**
	 * Select customer model that owns given address.
	 */
	protected static final String OWNING_CUSTOMER_QUERY = "SELECT {item.PK} FROM {Customer AS item} WHERE EXISTS({{"
			+ "SELECT {address.PK} FROM {Address as address} WHERE {address.PK}=?" + PRIMARY_KEY_PARAM_NAME
			+ " AND {address.owner}={item.PK}}})";

	/**
	 * Select addresses what belong to given customer.
	 */
	protected static final String CUSTOMERS_ADDRESSES_QUERY = "SELECT {item.PK} FROM {Address AS item} WHERE {item.owner}=?"
			+ PRIMARY_KEY_PARAM_NAME;

	/**
	 * Select customer by PK.
	 */
	protected static final String CUSTOMER_QUERY = "SELECT {item.PK} FROM {Customer AS item} WHERE {item.PK}=?"
			+ PRIMARY_KEY_PARAM_NAME;

	/**
	 * Select version marker.
	 */
	protected static final String VERSION_MARKER_QUERY = "SELECT {PK} FROM {ItemVersionMarker AS ivm} WHERE {ivm.itemPK} = ?"
			+ PRIMARY_KEY_PARAM_NAME + " AND {ivm.streamId} =?streamId";


	private C4CBatchingCollector customerCollector;
	private C4CBatchingCollector addressCollector;
	private FlexibleSearchService flexibleSearchService;
	private String customerConfigurationId;
	private String addressConfigurationId;
	private ChangeDetectionService changeDetectionService;
	private final List<ItemChangeDTO> deletedItems = new ArrayList<>();
	private TypeService typeService;

	@Override
	public boolean collect(final ItemChangeDTO itemChangeDTO)
	{
		validateParameterNotNullStandardMessage("itemChangeDTO", itemChangeDTO);
		fetchAllDependenciesOf(itemChangeDTO);
		return true;
	}

	/**
	 * This implementation does not finalizes the subsequent collectors. It has to be done manually outside of the class
	 * scope.
	 * <p>
	 * The only reason is the collector is used multiple times (for customers and for addresses), so finalizing batching
	 * collectors in between would produce smaller chunks.
	 * </p>
	 */
	@Override
	public void finish()
	{
		changeDetectionService.consumeChanges(deletedItems);
		deletedItems.clear();
	}

	
	@Override
	public void setCustomerConfigurationId(@Nonnull final String id)
	{
		customerConfigurationId = id;
	}

	@Override
	public void setAddressConfigurationId(@Nonnull final String id)
	{
		addressConfigurationId = id;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Required
	public void setChangeDetectionService(final ChangeDetectionService changeDetectionService)
	{
		this.changeDetectionService = changeDetectionService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * For customer pulls in all dependent addresses. For address fetches it's owner customer and all the addresses of
	 * the customer.
	 *
	 * @param itemChangeDTO changed item
	 */
	protected void fetchAllDependenciesOf(@Nonnull final ItemChangeDTO itemChangeDTO)
	{
		ItemChangeDTO customer = null;
		if (itemChangeDTO.getChangeType() == ChangeType.DELETED)
		{
			deletedItems.add(itemChangeDTO);
		}

		if (typeService.isAssignableFrom(AddressModel._TYPECODE, itemChangeDTO.getItemComposedType()))
		{
			try
			{
				final long customerPk = Long.parseLong(itemChangeDTO.getInfo());
				final CustomerModel cm = getCustomer(customerPk);
				customer = new ItemChangeDTO(cm.getPk().getLong(), itemChangeDTO.getVersion(),
						getChangeTypeOf(cm.getPk().getLong(), getCustomerConfigurationId()), "", CustomerModel._TYPECODE,
						getCustomerConfigurationId());
				
			}
			// The exception could happen if PK has not been put into info yet.
			catch (final NumberFormatException | ModelNotFoundException nfe) // NOSONAR (expected behavior)
			{
				try
				{
					final FlexibleSearchQuery queryOwningCustomer = new FlexibleSearchQuery(OWNING_CUSTOMER_QUERY);
					queryOwningCustomer.addQueryParameter(PRIMARY_KEY_PARAM_NAME, itemChangeDTO.getItemPK());
					final CustomerModel customerModel = flexibleSearchService.searchUnique(queryOwningCustomer);
					customer = new ItemChangeDTO(customerModel.getPk().getLong(), itemChangeDTO.getVersion(),
							getChangeTypeOf(customerModel.getPk().getLong(), getCustomerConfigurationId()),
							"", CustomerModel._TYPECODE, getCustomerConfigurationId());
				}
				catch (final ModelNotFoundException mnf) // NOSONAR (expected behavior)
				{
					LOG.debug("Address " + itemChangeDTO.getItemPK() + " skipped due to non-existing owner customer");
					deletedItems.add(itemChangeDTO);
					return;
				}
				catch (final ClassCastException cc) // NOSONAR (expected behavior)
				{
					LOG.debug("Owner of address " + itemChangeDTO.getItemPK() + " is not a customer. Skipping.");
					deletedItems.add(itemChangeDTO);
					return;
				}
			}
			// result of collectAddressesOf does not always contain the source address
			if (itemChangeDTO.getChangeType() != ChangeType.DELETED)
			{
				// Store owner id in info field
				addressCollector.collect(new ItemChangeDTO(itemChangeDTO.getItemPK(), itemChangeDTO.getVersion(),
						itemChangeDTO.getChangeType(), customer.getItemPK().toString(), itemChangeDTO.getItemComposedType(),
						itemChangeDTO.getStreamId()));
			}
		}
		else if (typeService.isAssignableFrom(CustomerModel._TYPECODE, itemChangeDTO.getItemComposedType()))
		{
			if (itemChangeDTO.getChangeType() == ChangeType.DELETED)
			{
				return;
			}
			customer = itemChangeDTO;
		}

		if (customer == null)
		{
			LOG.warn("C4C - No type supported for synchronization: [" + itemChangeDTO.getItemComposedType() + "]"); // NOSONAR
			deletedItems.add(itemChangeDTO);
			return;
		}
		if(!customerCollector.getCollectedItems().contains(customer.getItemPK().toString()))
		{
			customerCollector.collect(customer);
			collectAddressesOf(customer);
		}
	}

	/**
	 * Fetches addresses owned by the customer.
	 *
	 * @param customer change of a {@link CustomerModel}
	 */
	protected void collectAddressesOf(final ItemChangeDTO customer)
	{
		final FlexibleSearchQuery queryCustomersAddresses = new FlexibleSearchQuery(CUSTOMERS_ADDRESSES_QUERY);
		queryCustomersAddresses.addQueryParameter(PRIMARY_KEY_PARAM_NAME, customer.getItemPK());
		final SearchResult<ItemModel> set = flexibleSearchService.search(queryCustomersAddresses);
		set.getResult().stream()
				.map(i -> new ItemChangeDTO(i.getPk().getLong(), customer.getVersion(),
						getChangeTypeOf(i.getPk().getLong(), getAddressConfigurationId()), customer.getItemPK().toString(),
						AddressModel._TYPECODE, getAddressConfigurationId()))
				.forEach(addressCollector::collect);
	}

	protected ChangeType getChangeTypeOf(final Long pk, final String streamId)
	{
		return getVersionMarkerForItem(pk, streamId) == null ? ChangeType.NEW : ChangeType.MODIFIED;
	}

	protected ItemVersionMarkerModel getVersionMarkerForItem(final Long pk, final String streamId)
	{
		final SearchResult<ItemVersionMarkerModel> result = flexibleSearchService.search(VERSION_MARKER_QUERY,
				ImmutableMap.of(PRIMARY_KEY_PARAM_NAME, pk, "streamId", streamId));
		return result.getResult().isEmpty() ? null : result.getResult().get(0);
	}

	/**
	 * Looks for a customer based on its PK.
	 *
	 * @param customerPk primary key of customer model
	 * @throws ModelNotFoundException if the customer does not exist
	 */
	protected CustomerModel getCustomer(final long customerPk) throws ModelNotFoundException
	{
		final FlexibleSearchQuery queryCustomerByPk = new FlexibleSearchQuery(CUSTOMER_QUERY);
		queryCustomerByPk.addQueryParameter(PRIMARY_KEY_PARAM_NAME, customerPk);
		return flexibleSearchService.searchUnique(queryCustomerByPk);
	}

	/**
	 * @return customer configuration id.
	 */
	protected String getCustomerConfigurationId()
	{
		return customerConfigurationId;
	}

	/**
	 * @return address configuration id.
	 */
	protected String getAddressConfigurationId()
	{
		return addressConfigurationId;
	}

	public C4CBatchingCollector getCustomerCollector() {
		return customerCollector;
	}

	public void setCustomerCollector(C4CBatchingCollector customerCollector) {
		this.customerCollector = customerCollector;
	}

	public C4CBatchingCollector getAddressCollector() {
		return addressCollector;
	}

	public void setAddressCollector(C4CBatchingCollector addressCollector) {
		this.addressCollector = addressCollector;
	}

}
