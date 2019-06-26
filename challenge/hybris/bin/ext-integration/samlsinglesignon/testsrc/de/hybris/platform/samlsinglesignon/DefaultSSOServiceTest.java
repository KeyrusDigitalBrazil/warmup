/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.samlsinglesignon;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.ADMIN_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.EMPLOYEE_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.EMPLOYEE_TYPE_NAME;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.FALSE;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_PROPERTY_PREFIX;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_GROUP_2;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_ID;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_NAME;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.core.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import de.hybris.platform.testframework.PropertyConfigSwitcher;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultSSOServiceTest extends ServicelayerTransactionalTest
{

	@Resource(name = "defaultSSOUserService")
	private DefaultSSOService defaultSSOService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private TypeService typeService;

	private TypeModel employeeType;
	private TypeModel customerType;
	private UserGroupModel employeeGroup;
	private UserGroupModel adminGroup;

	private final PropertyConfigSwitcher dbUsegroupMappingSwitcher = new PropertyConfigSwitcher(
			DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING);
	private final List<PropertyConfigSwitcher> mappingsSwitchers = new ArrayList<>();

	@Before
	public void setup()
	{
		employeeType = typeService.getTypeForCode(EmployeeModel._TYPECODE);
		customerType = typeService.getTypeForCode(CustomerModel._TYPECODE);
		employeeGroup = userService.getUserGroupForUID(EMPLOYEE_GROUP);
		adminGroup = userService.getUserGroupForUID(ADMIN_GROUP);
	}

	@After
	public void tearDown()
	{
		dbUsegroupMappingSwitcher.switchBackToDefault();
		mappingsSwitchers.forEach(PropertyConfigSwitcher::switchBackToDefault);
	}

	@Test
	public void shouldCreateSSOUser()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		final UserModel user = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(user).isNotNull();
		assertThat(user.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(user.getName()).isEqualTo(SSO_USER_NAME);
		assertThat(user.getGroups()).containsOnlyElementsOf(newArrayList(employeeGroup));
		assertThat(user.getItemtype()).isEqualTo(EMPLOYEE_TYPE_NAME);
		assertThat(user.getPasswordEncoding()).isEqualTo("md5");
	}

	@Test
	public void shouldGetSSOUser()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		final EmployeeModel employeeModel = modelService.create(EmployeeModel.class);
		employeeModel.setUid(SSO_USER_ID);
		modelService.save(employeeModel);

		// when
		final UserModel user = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(user).isNotNull();
		assertThat(user.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(user.getGroups()).containsOnlyElementsOf(newArrayList(employeeGroup));
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenNoRoleFound()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));
	}

	@Test(expected = SystemException.class)
	public void shouldThrowIllegalArgumentExceptionWheAmbigousRoleFound()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);
		createMappingInProperties(SSO_USER_GROUP_2, "Employee2", EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdEmpty()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(StringUtils.EMPTY, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdNameEmpty()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, StringUtils.EMPTY, newHashSet(SSO_USER_GROUP));
	}


	@Test
	public void shouldUseMappingFromDatabase()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));

		// when
		final UserModel ssoUser = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(ssoUser).isNotNull();
		assertThat(ssoUser.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(ssoUser.getName()).isEqualTo(SSO_USER_NAME);
		assertThat(ssoUser.getGroups()).containsOnly(employeeGroup);
		assertThat(ssoUser.getItemtype()).isEqualTo(EmployeeModel._TYPECODE);
	}

	@Test
	public void shouldMergeUserGroupsOfMultipleMappingsFromDatabase()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));
		createMappingInDatabase(SSO_USER_GROUP_2, employeeType, newHashSet(adminGroup));

		// when
		final UserModel ssoUser = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME,
				newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));

		// then
		assertThat(ssoUser).isNotNull();
		assertThat(ssoUser.getGroups()).containsOnly(employeeGroup, adminGroup);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNoMappingInDatabaseExists()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(TRUE);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then - should throw exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailForAmbiguousMappingsInDatabase()
	{
		// given
		dbUsegroupMappingSwitcher.switchToValue(TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));
		createMappingInDatabase(SSO_USER_GROUP_2, customerType, newHashSet(employeeGroup));

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));

		// then - should throw exception
	}

	private void createMappingInProperties(final String ssoUserGroup, final String userType, final String groups)
	{
		final PropertyConfigSwitcher userTypeSwitcher = new PropertyConfigSwitcher(
				SSO_PROPERTY_PREFIX + ssoUserGroup + ".usertype");
		userTypeSwitcher.switchToValue(userType);
		mappingsSwitchers.add(userTypeSwitcher);

		final PropertyConfigSwitcher groupsSwitcher = new PropertyConfigSwitcher(SSO_PROPERTY_PREFIX + ssoUserGroup + ".groups");
		groupsSwitcher.switchToValue(groups);
		mappingsSwitchers.add(groupsSwitcher);
	}

	private void createMappingInDatabase(final String ssoUserGroup, final TypeModel userType, final Set<UserGroupModel> userGroups)
	{
		final SamlUserGroupModel samlUserGroupModel = new SamlUserGroupModel();

		samlUserGroupModel.setSamlUserGroup(ssoUserGroup);
		samlUserGroupModel.setUserType(userType);
		samlUserGroupModel.setUserGroups(userGroups);

		modelService.save(samlUserGroupModel);
	}

	@Test
	public void checkGettingMappingForRole()
	{
		//given
		Registry.getCurrentTenantNoFallback().getConfig().setParameter("sso.mapping.IDP_ROLE.groups", "hybrisgroup1,hybrisgroup2;hybrisgroup3");

		//when
		final DefaultSSOService.SSOUserMapping mappingForRole = defaultSSOService.getMappingForRole("IDP_ROLE");

		//then
		assertThat(mappingForRole.getGroups()).contains("hybrisgroup1", "hybrisgroup2", "hybrisgroup3");
	}

}
