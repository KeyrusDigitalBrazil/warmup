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
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BCustomerPopulatorTest
{
	private B2BCustomerPopulator b2BCustomerPopulator;
	private B2BCustomerModel source;
	private CustomerData target;

	@Mock
	private CurrencyModel testCurrency;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;

	@Mock
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BCustomerModel.class);
		target = new CustomerData();

		b2BCustomerPopulator = new B2BCustomerPopulator();
		b2BCustomerPopulator.setB2bUnitService(b2bUnitService);
		b2BCustomerPopulator.setCurrencyConverter(currencyConverter);
		b2BCustomerPopulator.setCommonI18NService(commonI18NService);
		b2BCustomerPopulator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
	}

	@Test
	public void shouldPopulate()
	{
		given(source.getUid()).willReturn("uid");
		given(source.getName()).willReturn("name");
		final TitleModel title = mock(TitleModel.class);
		given(source.getTitle()).willReturn(title);
		given(title.getCode()).willReturn("titleCode");
		final Boolean isActive = Boolean.TRUE;
		given(source.getActive()).willReturn(isActive);
		final CurrencyData testCurrencyData = Mockito.mock(CurrencyData.class);
		given(currencyConverter.convert(Mockito.any(CurrencyModel.class))).willReturn(testCurrencyData);
		given(commonI18NService.getCurrency("currency")).willReturn(testCurrency);

		// unit
		given(b2bUnitService.getParent(source)).willReturn(testUnit);
		final String unitId = "unitId";
		given(testUnit.getUid()).willReturn(unitId);
		final String unitName = "unitName";
		given(testUnit.getLocName()).willReturn(unitName);
		given(testUnit.getActive()).willReturn(isActive);
		final List<B2BCostCenterModel> costCenters = new ArrayList<>();
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);
		final String costCenterCode = "costCenterCode";
		given(costCenter.getCode()).willReturn(costCenterCode);
		final String costCenterName = "costCenterName";
		given(costCenter.getName()).willReturn(costCenterName);
		costCenters.add(costCenter);
		given(testUnit.getCostCenters()).willReturn(costCenters);

		// roles
		final Set<PrincipalGroupModel> groups = new HashSet<>();
		final UserGroupModel roleGroup = mock(UserGroupModel.class);
		final String roleUid = "usergroup";
		given(roleGroup.getUid()).willReturn(roleUid);
		groups.add(roleGroup);
		final List<String> roleGroups = new ArrayList<>();
		roleGroups.add(roleUid);
		given(b2BUserGroupsLookUpStrategy.getUserGroups()).willReturn(roleGroups);

		//permissionGroups
		final B2BUserGroupModel permissionGroup = mock(B2BUserGroupModel.class);
		final String permissionGroupName = "permissionGroupName";
		given(permissionGroup.getName()).willReturn(permissionGroupName);
		final String permissionGroupUid = "permissionGroupUid";
		given(permissionGroup.getUid()).willReturn(permissionGroupUid);
		given(permissionGroup.getUnit()).willReturn(testUnit);
		groups.add(permissionGroup);
		given(source.getGroups()).willReturn(groups);

		b2BCustomerPopulator.populate(source, target);
		Assert.assertEquals("source and target code should match", source.getUid(), target.getUid());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source and target name should match", source.getTitle().getCode(), target.getTitleCode());
		Assert.assertEquals("source and target name should match", source.getActive(), Boolean.valueOf(target.isActive()));
		Assert.assertNotNull("target currency should not be null", target.getCurrency());
		Assert.assertEquals("source and target currency should match", testCurrencyData, target.getCurrency());

		//unit
		Assert.assertNotNull("target unit should not be null", target.getUnit());
		Assert.assertEquals("source and target unit id should match", unitId, target.getUnit().getUid());
		Assert.assertEquals("source and target unit name should match", unitName, target.getUnit().getName());
		Assert.assertEquals("source and target unit name should match", isActive, Boolean.valueOf(target.getUnit().isActive()));
		Assert.assertNotNull("target unit cost centers should not be null", target.getUnit().getCostCenters());
		Assert.assertEquals("source and target unit cost centers should match", 1, target.getUnit().getCostCenters().size());
		Assert.assertEquals("source and target unit cost centers should match", costCenterCode,
				target.getUnit().getCostCenters().get(0).getCode());
		Assert.assertEquals("source and target unit cost centers should match", costCenterName,
				target.getUnit().getCostCenters().get(0).getName());

		// roles
		Assert.assertNotNull("target roles should not be null", target.getRoles());
		Assert.assertEquals("source and target unit roles should match", 1, target.getRoles().size());
		Assert.assertEquals("source and target unit roles should match", roleUid, target.getRoles().iterator().next());

		// permission groups
		Assert.assertNotNull("target PermissionGroups should not be null", target.getPermissionGroups());
		Assert.assertEquals("source and target unit PermissionGroups should match", 1, target.getPermissionGroups().size());
		final B2BUserGroupData permission = target.getPermissionGroups().iterator().next();
		Assert.assertEquals("source and target unit PermissionGroups should match", permissionGroupName, permission.getName());
		Assert.assertEquals("source and target unit PermissionGroups should match", permissionGroupUid, permission.getUid());
		Assert.assertNotNull("target permission unit should not be null", permission.getUnit());
		Assert.assertEquals("source and target PermissionGroups unit should match", unitId, permission.getUnit().getUid());
		Assert.assertEquals("source and target PermissionGroups unit should match", unitName, permission.getUnit().getName());
		Assert.assertEquals("source and target PermissionGroups unit should match", isActive,
				Boolean.valueOf(permission.getUnit().isActive()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCustomerPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCustomerPopulator.populate(source, null);
	}

}
