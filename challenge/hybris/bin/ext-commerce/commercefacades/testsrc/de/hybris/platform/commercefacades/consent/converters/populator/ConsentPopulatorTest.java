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
import static org.mockito.Mockito.doReturn;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.converters.Populator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsentPopulatorTest
{
	private static final String code = "code";
	private static final Date consentWithdrawalDate = new Date();
	private static final Date consentAgreementDate = new Date();

	@Mock
	private ConsentModel source;

	private Populator<ConsentModel, ConsentData> populator = new ConsentPopulator();

	@Before
	public void setUp()
	{
		doReturn(code).when(source).getCode();
		doReturn(consentAgreementDate).when(source).getConsentGivenDate();
		doReturn(consentWithdrawalDate).when(source).getConsentWithdrawnDate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateThrowsExceptionWhenSourceNull()
	{
		populator.populate(null, new ConsentData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateThrowsExceptionWhenTargetNull()
	{
		populator.populate(source, null);
	}

	@Test
	public void testPopulate()
	{
		final ConsentData target = new ConsentData();
		populator.populate(source, target);

		assertEquals(code, target.getCode());
		assertEquals(consentAgreementDate, target.getConsentGivenDate());
		assertEquals(consentWithdrawalDate, target.getConsentWithdrawnDate());
	}
}
