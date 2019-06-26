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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

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
public class UniqueIdentifierAttributeContentValidatorTest
{

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
		
	@InjectMocks
	private UniqueIdentifierAttributeContentValidator validator;
	
	@Mock
	private ItemModel itemModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ValidationErrors validationErrors;

	@Before
	public void setup()
	{
		
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	@Test
	public void testWhenUUIDIsValid_shouldNotAddError()
	{
		when(uniqueItemIdentifierService.getItemModel(any(), any())).thenReturn(Optional.of(itemModel));
		validator.validate("", attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}
	
	@Test
	public void testWhenUUIDIsInValid_shouldAddError()
	{
		when(uniqueItemIdentifierService.getItemModel(any(), any())).thenReturn(Optional.empty());
		final List<ValidationError> errors = validator.validate("", attributeDescriptor);
		assertThat(errors, not(empty()));
	}
}
