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
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaAttributeContentValidatorTest
{

	private static final String VALID_MEDIA_CODE = "valid-media-code";
	private static final String INVALID_MEDIA_CODE = "invalid-media-code";
	
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	
	@InjectMocks
	private MediaAttributeContentValidator validator;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private MediaModel mediaModel;
	@Mock 
	private ValidationErrors validationErrors;

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		
		when(uniqueItemIdentifierService.getItemModel(VALID_MEDIA_CODE, MediaModel.class)).thenReturn(Optional.of(mediaModel));
		when(uniqueItemIdentifierService.getItemModel(INVALID_MEDIA_CODE, MediaModel.class)).thenReturn(Optional.empty());
	}

	@Test
	public void testWhenIsValidMediaCode_shouldntAddError()
	{
		validator.validate(VALID_MEDIA_CODE, attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}
	
	@Test
	public void testWhenIsInValidMediaCode_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(INVALID_MEDIA_CODE, attributeDescriptor);
		assertThat(errors, not(empty()));
	}
		
}
