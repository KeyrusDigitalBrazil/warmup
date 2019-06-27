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
package de.hybris.platform.marketplaceservices.solr;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.solr.resolver.ProductVendorValueResolver;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@UnitTest
public class ProductVendorValueResolverTest extends AbstractValueResolverTest
{

	@Mock
	private ProductModel product;

	@Mock
	private VendorService vendorService;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private VendorModel vendor;

	private ProductVendorValueResolver valueResolver;
	private final String VENDOR_CODE = "Vendor_Code";
	private final String FILE_NAME_1 = "Vendor_Field_Name1";
	private final String FILE_NAME_2 = "Vendor_Field_Name2";
	private Collection<String> fieldNames;

	@Before
	public void setUp()
	{
		valueResolver = new ProductVendorValueResolver();
		valueResolver.setFieldNameProvider(fieldNameProvider);
		valueResolver.setVendorService(vendorService);
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());

		fieldNames = new ArrayList<>();
		fieldNames.add(FILE_NAME_1);
		fieldNames.add(FILE_NAME_2);
	}

	@Test
	public void resolverNoVendorForProduct() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(vendorService.getVendorByProduct(product)).thenReturn(Optional.of(vendor));


		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
	}

	@Test
	public void resolverVendorForProduct() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(vendorService.getVendorByProduct(product)).thenReturn(Optional.of(vendor));
		when(vendor.getCode()).thenReturn(VENDOR_CODE);
		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Collections.singletonList(FILE_NAME_1));

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument()).addField(FILE_NAME_1, VENDOR_CODE);
	}

	@Test
	public void resolverVendorMutilFieldNameForProduct() throws Exception
	{

		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(vendorService.getVendorByProduct(product)).thenReturn(Optional.of(vendor));
		when(vendor.getCode()).thenReturn(VENDOR_CODE);
		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(fieldNames);

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument()).addField(FILE_NAME_1, VENDOR_CODE);
		verify(getInputDocument()).addField(FILE_NAME_2, VENDOR_CODE);
	}
}
