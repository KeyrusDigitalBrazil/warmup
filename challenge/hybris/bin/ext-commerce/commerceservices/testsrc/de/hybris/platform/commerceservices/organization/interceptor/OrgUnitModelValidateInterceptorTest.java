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
package de.hybris.platform.commerceservices.organization.interceptor;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.impl.DefaultOrgUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class OrgUnitModelValidateInterceptorTest extends BaseCommerceBaseTest
{
	private static final String CA_UNIT_UID = "canada";
	private static final String MT_UNIT_UID = "montreal";
	private static final String US_UNIT_UID = "usa";
	private static final String NA_UNIT_UID = "northAmerica";

	@Resource(name = "defaultOrgUnitService")
	private DefaultOrgUnitService defaultOrgUnitService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "l10nService")
	private L10NService l10NService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
	}

	@Test
	public void shoudRestrainorgUnitFromBeingInMoreThanOneOrgUnitGroup()
	{
		// Get org unit
		final Optional<OrgUnitModel> canadaOptional = defaultOrgUnitService.getUnitForUid(CA_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());

		// Check groups of org unit
		final OrgUnitModel canada = canadaOptional.get();
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(canada.getGroups());
		Assert.assertEquals("Group size should be 1.", 1, groups.size());
		Assert.assertTrue("Group instance should be OrgUnitModel.", groups.iterator().next() instanceof OrgUnitModel);

		// Add another org unit to the groups
		final Optional<OrgUnitModel> usaOptional = defaultOrgUnitService.getUnitForUid(US_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", usaOptional.isPresent());
		groups.add(usaOptional.get());
		canada.setGroups(groups);

		thrown.expect(ModelSavingException.class);
		thrown.expectMessage(l10NService.getLocalizedString("error.orgunit.no.multiple.parent", new Object[]
		{ OrgUnitModel.class.getSimpleName(), canada.getUid() }));
		modelService.save(canada);
	}

	@Test
	public void shoudAllowUnitBeingInOneOrgUnitGroup()
	{
		// Get org unit
		Optional<OrgUnitModel> canadaOptional = defaultOrgUnitService.getUnitForUid(CA_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());

		// Check groups of org unit
		OrgUnitModel canada = canadaOptional.get();
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(canada.getGroups());
		Assert.assertEquals("Group size should be 1.", 1, groups.size());
		Assert.assertTrue("Group instance should be OrgUnitModel.", groups.iterator().next() instanceof OrgUnitModel);

		// Add a employeegroup to the groups
		final UserGroupModel employeegroup = userService.getUserGroupForUID("employeegroup");
		Assert.assertNotNull("employeegroup is null", employeegroup);
		Assert.assertFalse("employeegroup instance should not be OrgUnitModel.", employeegroup instanceof OrgUnitModel);

		groups.add(employeegroup);
		canada.setGroups(groups);
		modelService.save(canada);
		canadaOptional = defaultOrgUnitService.getUnitForUid(CA_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());

		canada = canadaOptional.get();
		Assert.assertEquals("Group size should be 2.", 2, canada.getGroups().size());
	}

	@Test
	public void shouldNotAllowToActivateUnitwhoseParentsHaveBeenDisabled()
	{
		// Get disabled unit montreal
		final Optional<OrgUnitModel> montrealOptional = defaultOrgUnitService.getUnitForUid(MT_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", montrealOptional.isPresent());
		Assert.assertFalse("Unit is not disabled.", montrealOptional.get().getActive().booleanValue());

		// Get disabled unit montreal's parent unit canada, which is also disabled
		final Optional<OrgUnitModel> canadaOptional = defaultOrgUnitService.getParent(montrealOptional.get());
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());
		Assert.assertFalse("Unit is not disabled.", canadaOptional.get().getActive().booleanValue());
		Assert.assertEquals("montreal's parent unit should be canada", CA_UNIT_UID, canadaOptional.get().getUid());

		thrown.expect(ModelSavingException.class);
		thrown.expectMessage(l10NService.getLocalizedString("error.orgunit.enable.orgunitparent.disabled", new Object[]
		{ OrgUnitModel.class.getSimpleName(), MT_UNIT_UID, CA_UNIT_UID }));
		defaultOrgUnitService.activateUnit(montrealOptional.get());
	}

	@Test
	public void shouldAllowToActivateUnitwhoseParentsHaveBeenEnabled()
	{
		// Get disabled unit
		Optional<OrgUnitModel> canadaOptional = defaultOrgUnitService.getUnitForUid(CA_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());
		Assert.assertFalse("Unit is not disabled.", canadaOptional.get().getActive().booleanValue());

		// Get disabled unit canada's parent unit northAmerica, which is enabled
		final Optional<OrgUnitModel> northAmericaOptional = defaultOrgUnitService.getParent(canadaOptional.get());
		Assert.assertTrue("Existing OrgUnitModel was not returned.", northAmericaOptional.isPresent());
		Assert.assertTrue("Unit is not enabled.", northAmericaOptional.get().getActive().booleanValue());
		Assert.assertEquals("canada's parent unit should be northAmerica", NA_UNIT_UID, northAmericaOptional.get().getUid());

		defaultOrgUnitService.activateUnit(canadaOptional.get());

		canadaOptional = defaultOrgUnitService.getUnitForUid(CA_UNIT_UID);
		Assert.assertTrue("Existing OrgUnitModel was not returned.", canadaOptional.isPresent());
		Assert.assertTrue("Unit was not enabled.", canadaOptional.get().getActive().booleanValue());
	}

}
