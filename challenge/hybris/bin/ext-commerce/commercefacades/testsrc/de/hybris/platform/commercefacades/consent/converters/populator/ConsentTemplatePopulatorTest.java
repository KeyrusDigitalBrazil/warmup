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
package de.hybris.platform.commercefacades.consent.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsentTemplatePopulatorTest
{
	private static final String id = "id";
	private static final String name = "name";
	private static final String description = "description";
	private static final Integer version = Integer.valueOf(0);

	@Mock
	private UserService userService;

	@Mock
	private CommerceConsentService commerceConsentService;

	@Mock
	private Converter<ConsentModel, ConsentData> consentConverter;

	@Mock
	private ConsentTemplateModel source;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private ConsentModel consentModel;

	@Mock
	private ConsentData consentData;

	@InjectMocks
	private Populator<ConsentTemplateModel, ConsentTemplateData> populator = new ConsentTemplatePopulator();

	@Before
	public void setUp()
	{
		doReturn(customerModel).when(userService).getCurrentUser();

		doReturn(id).when(source).getId();
		doReturn(name).when(source).getName();
		doReturn(description).when(source).getDescription();
		doReturn(version).when(source).getVersion();
	}

	@Test
	public void testPopulateWhenUserConsentAvailable()
	{
		final ConsentTemplateData target = new ConsentTemplateData();
		doReturn(consentData).when(consentConverter).convert(consentModel);
		doReturn(consentModel).when(commerceConsentService).getActiveConsent(customerModel, source);

		populator.populate(source, target);

		assertEquals(id, target.getId());
		assertEquals(name, target.getName());
		assertEquals(description, target.getDescription());
		assertEquals(version, target.getVersion());
		assertEquals(id, target.getId());
		assertEquals(consentData, target.getConsentData());
	}

	@Test
	public void testPopulateWhenUserConsentNotAvailable()
	{
		final ConsentTemplateData target = new ConsentTemplateData();
		doReturn(null).when(commerceConsentService).getActiveConsent(customerModel, source);

		populator.populate(source, target);

		assertEquals(id, target.getId());
		assertEquals(name, target.getName());
		assertEquals(description, target.getDescription());
		assertEquals(version, target.getVersion());
		assertEquals(id, target.getId());
		assertNull(target.getConsentData());
	}
}
