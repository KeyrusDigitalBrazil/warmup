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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BPermissionsReversePopulatorTest
{
	private B2BPermissionsReversePopulator b2BPermissionsReversePopulator;
	private B2BPermissionData source;
	private B2BPermissionModel permissionTarget;
	private B2BOrderThresholdPermissionModel orderThresholdPermissionTarget;
	private B2BOrderThresholdTimespanPermissionModel orderThresholdTimespanPermissionTarget;

	@Mock
	private B2BUnitData testUnitData;

	@Mock
	private B2BUnitModel testUnitModel;

	@Mock
	private CurrencyModel currencyModel;

	@Mock
	private CurrencyData currency;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BPermissionData.class);
		permissionTarget = new B2BPermissionModel();
		orderThresholdPermissionTarget = new B2BOrderThresholdPermissionModel();
		orderThresholdTimespanPermissionTarget = new B2BOrderThresholdTimespanPermissionModel();

		b2BPermissionsReversePopulator = new B2BPermissionsReversePopulator();
		b2BPermissionsReversePopulator.setB2BUnitService(b2BUnitService);
		b2BPermissionsReversePopulator.setCommonI18NService(commonI18NService);

		given(source.getCode()).willReturn("code");
		given(source.getUnit()).willReturn(testUnitData);
		String unitUid = "unitUid";
		given(testUnitData.getUid()).willReturn(unitUid);
		given(b2BUnitService.getUnitForUid(unitUid)).willReturn(testUnitModel);
		given(source.getValue()).willReturn(1000.0);
		given(source.getCurrency()).willReturn(currency);
		String isoCode = "isoCode";
		given(currency.getIsocode()).willReturn(isoCode);
		given(commonI18NService.getCurrency(isoCode)).willReturn(currencyModel);
		given(source.getPeriodRange()).willReturn(B2BPeriodRange.YEAR);
	}

	@Test
	public void shouldPopulateWithB2BPermissionModel()
	{
		b2BPermissionsReversePopulator.populate(source, permissionTarget);

		Assert.assertEquals("source and target code should match", source.getCode(), permissionTarget.getCode());
		Assert.assertEquals("source and target unit should match", testUnitModel, permissionTarget.getUnit());
	}

	@Test
	public void shouldPopulateWithB2BOrderThresholdPermissionModel()
	{
		b2BPermissionsReversePopulator.populate(source, orderThresholdPermissionTarget);

		Assert.assertEquals("source and target code should match", source.getCode(), orderThresholdPermissionTarget.getCode());
		Assert.assertEquals("source and target unit should match", testUnitModel, orderThresholdPermissionTarget.getUnit());
		Assert.assertEquals("source and target threshold should match", source.getValue(),
				orderThresholdPermissionTarget.getThreshold());
		Assert.assertEquals("source and target currency should match", currencyModel, orderThresholdPermissionTarget.getCurrency());
	}

	@Test
	public void shouldPopulateWithB2BOrderThresholdTimespanPermissionModel()
	{
		b2BPermissionsReversePopulator.populate(source, orderThresholdTimespanPermissionTarget);

		Assert.assertEquals("source and target code should match", source.getCode(),
				orderThresholdTimespanPermissionTarget.getCode());
		Assert.assertEquals("source and target unit should match", testUnitModel, orderThresholdTimespanPermissionTarget.getUnit());
		Assert.assertEquals("source and target threshold should match", source.getValue(),
				orderThresholdTimespanPermissionTarget.getThreshold());
		Assert.assertEquals("source and target currency should match", currencyModel,
				orderThresholdTimespanPermissionTarget.getCurrency());
		Assert.assertEquals("source and target period range should match", source.getPeriodRange(),
				orderThresholdTimespanPermissionTarget.getRange());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BPermissionsReversePopulator.populate(null, orderThresholdTimespanPermissionTarget);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BPermissionsReversePopulator.populate(source, null);
	}

}
