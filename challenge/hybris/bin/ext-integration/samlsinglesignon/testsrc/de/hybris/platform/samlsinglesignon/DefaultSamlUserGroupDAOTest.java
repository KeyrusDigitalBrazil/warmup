/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.samlsinglesignon;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.samlsinglesignon.DefaultSamlUserGroupDAO;
import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.google.common.collect.Sets;



import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class DefaultSamlUserGroupDAOTest extends ServicelayerTransactionalTest
{
	private static final String MY_SAML_USER_GROUP = "mySamlUserGroup";

	@Resource
	private DefaultSamlUserGroupDAO defaultSamlUserGroupDAO;

	@Resource
	private TypeService typeService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;


	@Test
	public void shouldReturnNullForInexistentSamlUserGroup()
	{
		// when
		final Optional<SamlUserGroupModel> samlUserGroup = defaultSamlUserGroupDAO.findSamlUserGroup("inexistent");

		// then
		assertThat(samlUserGroup.isPresent()).isFalse();
	}

	@Test
	public void shouldFindExistingSamlUserGroup()
	{
		// given
		final SamlUserGroupModel samlUserGroupModel = new SamlUserGroupModel();
		samlUserGroupModel.setSamlUserGroup(MY_SAML_USER_GROUP);
		samlUserGroupModel.setUserType(typeService.getTypeForCode(EmployeeModel._TYPECODE));
		samlUserGroupModel.setUserGroups(Sets.newHashSet(userService.getUserGroupForUID("employeegroup")));

		modelService.save(samlUserGroupModel);

		// when
		final Optional<SamlUserGroupModel> foundSamlUserGroup = defaultSamlUserGroupDAO.findSamlUserGroup(MY_SAML_USER_GROUP);

		// then
		assertThat(foundSamlUserGroup.isPresent()).isTrue();
		assertThat(foundSamlUserGroup.get()).isEqualTo(samlUserGroupModel);
	}
}
