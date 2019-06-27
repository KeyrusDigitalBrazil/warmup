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
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;


/**
 * Tests the conversion of subscription product to String as determined by the value provider
 */
@UnitTest
public class TermLimitValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private TypeService typeService;

	@Mock
	private SessionService sessionService;

	@Mock
	private IndexedProperty indexedProperty;

	@Override
	protected String getPropertyName()
	{
		return "termLimit";
	}

	@Mock
	private EnumerationValueModel enumVal;

	@Mock
	private SubscriptionProductService subscriptionProductService;

	@Before
	public void setUp()
	{
		configure();
	}

	private final ProductModel subscriptionProduct = new ProductModel();
	private final SubscriptionTermModel subscriptionTerm = new SubscriptionTermModel();

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new TermLimitValueProvider());
		configureBase();


		((TermLimitValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((TermLimitValueProvider) getPropertyFieldValueProvider()).setSessionService(sessionService);
		((TermLimitValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		((TermLimitValueProvider) getPropertyFieldValueProvider()).setTypeService(typeService);
		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.FALSE);
		given(subscriptionProductService.isSubscription(subscriptionProduct)).willReturn(Boolean.TRUE);

		subscriptionTerm.setTermOfServiceNumber(Integer.valueOf(1));
		subscriptionTerm.setTermOfServiceFrequency(TermOfServiceFrequency.MONTHLY);
		subscriptionProduct.setSubscriptionTerm(subscriptionTerm);

		when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testConversionOfValue()
	{
		final TermLimitValueProvider termLimitValueProvider = new TermLimitValueProvider();

		termLimitValueProvider.setTypeService(typeService);
		termLimitValueProvider.setSubscriptionProductService(subscriptionProductService);

		when(enumVal.getCode()).thenReturn(TermOfServiceFrequency.MONTHLY.getCode());
		when(enumVal.getName()).thenReturn("Months");

		when(typeService.getEnumerationValue(TermOfServiceFrequency._TYPECODE, TermOfServiceFrequency.MONTHLY.getCode()))
				.thenReturn(enumVal);
		final Object value = termLimitValueProvider.getPropertyValue(subscriptionProduct);
		assertTrue("", value instanceof String);
		assertEquals("", "1 Months", value);
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

}
