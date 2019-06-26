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
package de.hybris.platform.c4ccustomer.deltadetection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.enums.ChangeType;
import de.hybris.platform.c4ccustomer.deltadetection.collector.C4CBatchingCollector;
import de.hybris.platform.c4ccustomer.deltadetection.impl.DefaultC4CAggregatingCollector;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.y2ysync.deltadetection.collector.BatchingCollector;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultC4CAggregatingCollectorUnitTest extends ServicelayerTest
{
	private DefaultC4CAggregatingCollector collector;
	private C4CBatchingCollector customersCollector;
	private C4CBatchingCollector addressesCollector;
	private TypeService typeService;

	@Before
	public void setUp()
	{
		collector = new DefaultC4CAggregatingCollector();
		customersCollector = mock(C4CBatchingCollector.class);
		addressesCollector = mock(C4CBatchingCollector.class);
		when(customersCollector.collect(any(ItemChangeDTO.class))).thenReturn(true);
		when(addressesCollector.collect(any(ItemChangeDTO.class))).thenReturn(true);
		collector.setCustomerCollector(customersCollector);
		collector.setAddressCollector(addressesCollector);

		typeService = mock(TypeService.class);
		collector.setTypeService(typeService);
	}

	protected Collection<ComposedTypeModel> generateListOfTypes(final List<String> types)
	{
		return types.stream().map(type ->
			{
				final ComposedTypeModel c = mock(ComposedTypeModel.class);
				when(c.getCode()).thenReturn(type);
				return c;
			}).collect(Collectors.toList());
	}

	@Test
	public void shouldConsumeSubtypesOfCustomerWithNoAddresses()
	{
		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MyCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MySecondCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MyAddress")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MySecondAddress")).thenReturn(true);

		final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);

		// No addresses
		@SuppressWarnings("unchecked")
		final SearchResult<Object> searchResult = mock(SearchResult.class);
		when(searchResult.getResult()).thenReturn(Collections.emptyList());
		when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		collector.setFlexibleSearchService(flexibleSearchService);

		// We provide a MyCustomer item: it should be supported and processed
		final ItemChangeDTO item = new ItemChangeDTO(12345L, new Date(), ChangeType.MODIFIED, "INFO", "MyCustomer", "c4cStreamId");

		collector.collect(item);

		verify(customersCollector).collect(item);
		verify(addressesCollector, never()).collect(any(ItemChangeDTO.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldConsumeSubtypesOfAddressWithCustomer()
	{
		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MyCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MySecondCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MyAddress")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MySecondAddress")).thenReturn(true);

		final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);

		collector.setCustomerConfigurationId("ID");
		collector.setAddressConfigurationId("ID");

		// this will be the customer associated to the address
		final CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getPk()).thenReturn(PK.createFixedUUIDPK(1234, 12345L));

		// the first call is not important, it is only checking if the address has a customer associated to it, the second call will return a customer
		when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(customerModel);

		// No addresses
		final SearchResult<Object> searchResult = mock(SearchResult.class);
		when(searchResult.getResult()).thenReturn(Collections.emptyList());
		when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		// the second case looking for version marker will then evaluate to null.
		when(flexibleSearchService.search(anyString(), any(Map.class))).thenReturn(searchResult);
		collector.setFlexibleSearchService(flexibleSearchService);

		// We provide a MyAddress item: it should be supported and processed
		final ItemChangeDTO item = new ItemChangeDTO(12345L, new Date(), ChangeType.MODIFIED, "INFO", "MyAddress", "c4cStreamId");

		collector.collect(item);

		verify(customersCollector).collect(any(ItemChangeDTO.class));
		verify(addressesCollector).collect(any(ItemChangeDTO.class));
	}

	@Test
	public void shouldNotFailIfItemTypeIsNotSupported()
	{
		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(false);

		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MyCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(CustomerModel._TYPECODE, "MySecondCustomer")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MyAddress")).thenReturn(true);
		when(typeService.isAssignableFrom(AddressModel._TYPECODE, "MySecondAddress")).thenReturn(true);

		// We provide an item that is not supported by the collector: OtherType
		final ItemChangeDTO item = new ItemChangeDTO(12345L, new Date(), ChangeType.MODIFIED, "INFO", "OtherType", "c4cStreamId");

		collector.collect(item);

		verify(customersCollector, never()).collect(any(ItemChangeDTO.class));
		verify(addressesCollector, never()).collect(any(ItemChangeDTO.class));
	}
}
