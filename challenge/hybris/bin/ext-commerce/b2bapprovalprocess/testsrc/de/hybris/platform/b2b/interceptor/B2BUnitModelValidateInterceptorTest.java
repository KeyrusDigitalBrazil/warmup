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
package de.hybris.platform.b2b.interceptor;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BUnitModelValidateInterceptorTest extends B2BIntegrationTest
{
	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "l10nService")
	private L10NService l10NService;

	@Resource
	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void beforeTest() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		i18nService.setCurrentLocale(Locale.ENGLISH);
	}

	@Test
	public void shouldB2BUnitWithoutParentAllowedToBeCreatedByAdmingroupUser()
	{
		final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
		final String uid = "testCreatedByAdmingroupUser";
		b2bUnit.setUid(uid);
		modelService.save(b2bUnit);

		Assert.assertNotNull("Existing B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(uid));
	}

	@Test
	public void shouldB2BUnitWithoutParentNotAllowedToBeCreatedByNonAdmingroupUser()
	{
		userService.setCurrentUser(userService.getUserForUID("anonymous"));

		final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
		b2bUnit.setUid("testCreatedByEmployeegroupUser");
		thrown.expect(ModelSavingException.class);
		thrown.expectMessage(l10NService.getLocalizedString("error.b2bunit.root.create.nonadmin"));
		modelService.save(b2bUnit);
	}

	@Test
	public void shouldB2BUnitWithParentAllowedToBeCreatedByNonAdmingroupUser()
	{
		final B2BUnitModel parentUnit = modelService.create(B2BUnitModel.class);
		final String parentUid = "testCreatedByAdmingroupUser";
		parentUnit.setUid(parentUid);
		modelService.save(parentUnit);
		Assert.assertNotNull("Existing B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(parentUid));

		userService.setCurrentUser(userService.getUserForUID("anonymous"));

		// add parent unit
		final Set<PrincipalGroupModel> unitGroups = new HashSet<>();
		unitGroups.add(parentUnit);
		final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
		final String uid = "testCreatedByNonAdmingroupUser";
		b2bUnit.setUid(uid);
		b2bUnit.setGroups(unitGroups);
		modelService.save(b2bUnit);
		Assert.assertNotNull("Existing B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(uid));
	}

	@Test
	public void shouldDisabledB2BUnitAllowedToBeCreated()
	{
		final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
		final String uid = "testCreatedByAdmingroupUser";
		b2bUnit.setUid(uid);
		b2bUnit.setActive(Boolean.FALSE);
		modelService.save(b2bUnit);

		Assert.assertNotNull("The existing disabled B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(uid));
	}

	@Test
	public void shouldNotAllowUserToBecomeApprover()
	{
		final B2BUnitModel unit = modelService.create(B2BUnitModel.class);
		final String uid = "aUnit";
		unit.setUid(uid);
		unit.setLocName(uid);

		final B2BCustomerModel customer = modelService.create(B2BCustomerModel.class);
		final String uidCust = "test";
		customer.setUid(uidCust);
		customer.setName(uidCust);
		customer.setEmail("test@test.com");

		final Set<PrincipalGroupModel> groups = new HashSet<>(customer.getGroups());
		groups.add(unit);
		customer.setGroups(groups);

		final Set<B2BCustomerModel> approvers = new HashSet<>();
		approvers.add(customer);
		unit.setApprovers(approvers);

		modelService.save(unit);

		// The unit will be created normally (see B2BUnitModelValidateInterceptor.onValidate)...
		Assert.assertNotNull("Existing B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(uid));

		// ... but he is not added to the approvers list.
		Assert.assertFalse("Customer became approver even when he is not in b2bapprovergroup.",
				b2bUnitService.getUnitForUid(uid).getApprovers().contains(customer));
	}

	@Test
	public void shouldAllowUserToBecomeApprover()
	{
		final B2BUnitModel unit = modelService.create(B2BUnitModel.class);
		final String uid = "aUnit";
		unit.setUid(uid);
		unit.setLocName("aUnit");

		final B2BCustomerModel customer = modelService.create(B2BCustomerModel.class);
		customer.setUid("test");
		customer.setName("test");
		customer.setEmail("test@test.com");

		final Set<PrincipalGroupModel> groups = new HashSet<>(customer.getGroups());
		final UserGroupModel b2bApproverGroup = userService.getUserGroupForUID("b2bapprovergroup");
		groups.add(unit);
		groups.add(b2bApproverGroup);
		customer.setGroups(groups);

		final Set<B2BCustomerModel> approvers = new HashSet<>();
		approvers.add(customer);
		unit.setApprovers(approvers);

		modelService.save(unit);

		// The unit will be created normally (see B2BUnitModelValidateInterceptor.onValidate)...
		Assert.assertNotNull("Existing B2BUnitModel was not returned.", b2bUnitService.getUnitForUid(uid));

		// ... and he is added to the approvers list.
		Assert.assertTrue("Customer should become approver because he is in b2bapprovergroup.",
				b2bUnitService.getUnitForUid(uid).getApprovers().contains(customer));
	}
}

