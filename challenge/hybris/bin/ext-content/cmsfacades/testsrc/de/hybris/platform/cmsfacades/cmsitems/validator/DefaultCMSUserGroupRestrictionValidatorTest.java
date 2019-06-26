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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSUserGroupRestrictionValidatorTest
{
	@InjectMocks
	private DefaultCMSUserGroupRestrictionValidator validator;
	
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	private ValidationErrors validationErrors = new DefaultValidationErrors();

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	@Test
	public void testValidateWithoutRequiredAttributeAddErrors()
	{
		final CMSUserGroupRestrictionModel item = new CMSUserGroupRestrictionModel();
		validator.validate(item);
		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());

		assertThat(errors.get(0).getField(), is(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.get(0).getErrorCode(), is(CmsfacadesConstants.FIELD_REQUIRED));
	}


	@Test
	public void testValidateWithRequiredAttributeAddErrorsWhenCollectionEmpty()
	{
		final CMSUserGroupRestrictionModel item = new CMSUserGroupRestrictionModel();
		item.setUserGroups(new ArrayList<>());
		validator.validate(item);
		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());

		assertThat(errors.get(0).getField(), is(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.get(0).getErrorCode(), is(CmsfacadesConstants.FIELD_MIN_VIOLATED));
	}

	@Test
	public void testValidateWithRequiredAttributeDoNotAddErrorsWhenCollectionNotEmpty()
	{
		final CMSUserGroupRestrictionModel item = new CMSUserGroupRestrictionModel();
		item.setUserGroups(Arrays.asList(new UserGroupModel()));
		validator.validate(item);
		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(0, errors.size());
	}
}
