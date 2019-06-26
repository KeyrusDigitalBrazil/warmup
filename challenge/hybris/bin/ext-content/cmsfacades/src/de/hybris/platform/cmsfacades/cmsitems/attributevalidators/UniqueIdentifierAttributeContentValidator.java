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
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_INVALID_UUID_L10N;
import static java.util.Objects.isNull;

import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

/**
 * Item unique identifier validator adds validation errors when the uuid does to reference any valid item model.     
 */
public class UniqueIdentifierAttributeContentValidator extends AbstractAttributeContentValidator<String>
{
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	
	@Override
	public List<ValidationError> validate(final String value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();
		
		if (isNull(value))
		{
			return errors;
		}
		final Class typeClass = getAttributeDescriptorModelHelperService().getAttributeClass(attribute);
		final Optional<ItemModel> itemModel = getUniqueItemIdentifierService().getItemModel(value, typeClass);
		if (!itemModel.isPresent())
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.rejectedValue(value) //
							.errorCode(FIELD_INVALID_UUID_L10N) //
							.errorArgs(new Object[] {typeClass, value}) //
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

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			final AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;
	}
}
