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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BCostCenterReversePopulatorTest
{
	private final static String UNIT_ID = "testUnit";
	private final static String CURRENCY_ID = "testCurrency";

	private B2BCostCenterReversePopulator b2BCostCenterReversePopulator;
	private B2BCostCenterModel target;

	@Mock
	private B2BCostCenterData source;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private LocaleProvider localeProvider;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private CurrencyModel testCurrency;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		given(localeProvider.getCurrentDataLocale()).willReturn(Locale.ENGLISH);

		final B2BUnitData testUnitData = mock(B2BUnitData.class);
		final CurrencyData testCurrencyData = mock(CurrencyData.class);
		given(testUnitData.getUid()).willReturn(UNIT_ID);
		given(testCurrencyData.getIsocode()).willReturn(CURRENCY_ID);
		given(source.getCode()).willReturn("costCenterCode");
		given(source.getName()).willReturn("costCenterName");
		given(source.getUnit()).willReturn(testUnitData);
		given(source.getCurrency()).willReturn(testCurrencyData);

		// Setup Services
		given(testUnit.getUid()).willReturn(UNIT_ID);
		given(testCurrency.getIsocode()).willReturn(CURRENCY_ID);
		given(b2bUnitService.getUnitForUid(UNIT_ID)).willReturn(testUnit);
		given(commonI18NService.getCurrency(CURRENCY_ID)).willReturn(testCurrency);

		target = new B2BCostCenterModel();
		B2BCommercefacadesTestUtils.getContext(target).setLocaleProvider(localeProvider);
		b2BCostCenterReversePopulator = new B2BCostCenterReversePopulator();
		b2BCostCenterReversePopulator.setB2bUnitService(b2bUnitService);
		b2BCostCenterReversePopulator.setCommonI18NService(commonI18NService);
	}

	@Test
	public void shouldPopulate()
	{
		b2BCostCenterReversePopulator.populate(source, target);
		Assert.assertEquals("source and target code should match", source.getCode(), target.getCode());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertNotNull("target unit should not be null", target.getUnit());
		Assert.assertEquals("source and target unit should match", source.getUnit().getUid(), target.getUnit().getUid());
		Assert.assertNotNull("target currency should not be null", target.getCurrency());
		Assert.assertEquals("source and target currency should match", source.getCurrency().getIsocode(),
				target.getCurrency().getIsocode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCostCenterReversePopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCostCenterReversePopulator.populate(source, null);
	}
}
