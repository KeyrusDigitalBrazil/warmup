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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.common.predicate.OnlyHasSupportedCharactersPredicate;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.function.Predicate;

import static de.hybris.platform.cms2.model.contents.CMSItemModel.NAME;
import static de.hybris.platform.cms2.model.contents.CMSItemModel.UID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUpdateCMSItemValidatorTest
{
	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private final String ITEM_UID = "some item uid";
	private final String OTHER_UID = "some other uid";
	private final String ITEM_NAME = "some item name";

	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	@Mock
	private CMSItemModel cmsItemModel;

	@Mock
	private CMSItemModel itemBeforeUpdateModel;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private OriginalClonedItemProvider originalClonedItemProvider;

	@Mock
	private Predicate<String> onlyHasSupportedCharactersPredicate;

	@Mock
	private Predicate<String> validStringLengthPredicate;

	@Mock
	private Predicate<CMSItemModel> cmsItemNameExistsPredicate;

	@InjectMocks
	private DefaultUpdateCMSItemValidator defaultUpdateCMSItemValidator;

	// ---------------------------------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(cmsItemModel.getUid()).thenReturn(ITEM_UID);
		when(cmsItemModel.getName()).thenReturn(ITEM_NAME);

		when(itemBeforeUpdateModel.getUid()).thenReturn(ITEM_UID);

		when(originalClonedItemProvider.getCurrentItem()).thenReturn(itemBeforeUpdateModel);

		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(onlyHasSupportedCharactersPredicate.test(ITEM_UID)).thenReturn(true);
		when(onlyHasSupportedCharactersPredicate.test(OTHER_UID)).thenReturn(true);
		when(validStringLengthPredicate.test(ITEM_NAME)).thenReturn(true);
		when(cmsItemNameExistsPredicate.test(cmsItemModel)).thenReturn(false);
	}

	// ---------------------------------------------------------------------------------------------------
	// Tests
	// ---------------------------------------------------------------------------------------------------
	@Test
	public void givenProvidedItemHasValidData_WhenItemIsValidated_ThenNoErrorIsReturned()
	{
		// GIVEN

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenUidHasInvalidChars_WhenItemIsValidated_ThenItReturnsAnError()
	{
		// GIVEN
		when(onlyHasSupportedCharactersPredicate.test(ITEM_UID)).thenReturn(false);

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasError(UID, FIELD_CONTAINS_INVALID_CHARS);
	}

	@Test
	public void givenItemHasANameWithInvalidLength_WhenItemIsValidated_ThenItReturnsAnError()
	{
		// GIVEN
		when(validStringLengthPredicate.test(ITEM_NAME)).thenReturn(false);

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasError(NAME, FIELD_LENGTH_EXCEEDED);
	}

	@Test
	public void givenItemHasNoName_WhenItemIsValidated_ThenItReturnsAnError()
	{
		// GIVEN
		when(cmsItemModel.getName()).thenReturn("");

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasError(NAME, FIELD_LENGTH_EXCEEDED);
	}

	@Test
	public void givenItemUidHasChangedDuringUpdate_WhenItemIsValidated_ThenItReturnsAnError()
	{
		// GIVEN
		when(cmsItemModel.getUid()).thenReturn(OTHER_UID);

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasError(UID, FIELD_NOT_ALLOWED);
	}

	@Test
	public void givenProvidedItemHasAnAlreadyExistingName_WhenItemIsValidated_ThenItReturnsAnError()
	{
		// GIVEN
		when(cmsItemNameExistsPredicate.test(cmsItemModel)).thenReturn(true);

		// WHEN
		defaultUpdateCMSItemValidator.validate(cmsItemModel);

		// THEN
		assertHasError(NAME, FIELD_ALREADY_EXIST);
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void assertHasNoErrors()
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();
		assertTrue(errors.isEmpty());
	}

	protected void assertHasError(final String field, final String errorCode)
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();

		assertThat(errors.get(0).getField(), is(field));
		assertThat(errors.get(0).getErrorCode(), is(errorCode));
	}
}
