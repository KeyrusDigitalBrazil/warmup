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

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_MEDIA_CODE_L10N;

import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

/**
 * Media attribute content validator adds validation errors when String value is not a valid unique identifier for Media.  
 */
public class MediaAttributeContentValidator extends AbstractAttributeContentValidator<String>
{
	
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	
	@Override
	public List<ValidationError> validate(final String value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();
		if (value == null)
		{
			return errors;
		}
		final Optional<MediaModel> media = getUniqueItemIdentifierService().getItemModel(value, MediaModel.class);

		if (!media.isPresent())
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(INVALID_MEDIA_CODE_L10N) //
							.build()
			);
		}
		return errors;
	}
	

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
