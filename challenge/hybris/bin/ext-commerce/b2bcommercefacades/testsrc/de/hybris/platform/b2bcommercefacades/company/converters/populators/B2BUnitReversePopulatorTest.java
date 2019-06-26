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
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUnitReversePopulatorTest
{
	private B2BUnitReversePopulator b2BUnitReversePopulator;
	private B2BUnitData source;
	private B2BUnitModel target;

	@Mock
	private B2BUnitModel parentUnit;

	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Mock
	private LocaleProvider localeProvider;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BUnitData.class);
		target = new B2BUnitModel();

		given(localeProvider.getCurrentDataLocale()).willReturn(Locale.ENGLISH);
		B2BCommercefacadesTestUtils.getContext(target).setLocaleProvider(localeProvider);

		b2BUnitReversePopulator = new B2BUnitReversePopulator();
		b2BUnitReversePopulator.setB2bUnitService(b2bUnitService);
		b2BUnitReversePopulator.setB2bCommerceUnitService(b2bCommerceUnitService);
	}

	@Test
	public void shouldPopulate()
	{
		given(source.getUid()).willReturn("uid");
		given(source.getName()).willReturn("name");
		final B2BUnitData parentUnitData = mock(B2BUnitData.class);
		final String parentUid = "parentUid";
		given(parentUnitData.getUid()).willReturn(parentUid);
		given(source.getUnit()).willReturn(parentUnitData);
		given(b2bUnitService.getUnitForUid(parentUid)).willReturn(parentUnit);

		b2BUnitReversePopulator.populate(source, target);

		Assert.assertEquals("source and target uid should match", source.getUid(), target.getUid());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source name and target locname should match", source.getName(), target.getLocName());
		Assert.assertEquals("source and target active should match", Boolean.TRUE, target.getActive());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BUnitReversePopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BUnitReversePopulator.populate(source, null);
	}
}
