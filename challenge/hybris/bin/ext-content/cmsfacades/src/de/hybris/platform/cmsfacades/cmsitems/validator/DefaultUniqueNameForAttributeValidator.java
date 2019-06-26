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

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.ITEM_WITH_NAME_ALREADY_EXIST;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;


/**
 * Validator to ensure that any {@link CMSItemModel} cannot have a provided attribute with the same name.
 */
public class DefaultUniqueNameForAttributeValidator implements Validator<CMSItemModel>
{

	private ValidationErrorsProvider validationErrorsProvider;
	private ModelService modelService;
	private TypeService typeService;
	private String attribute;

	@Override
	public void validate(final CMSItemModel validatee)
	{

		if (getTypeService().hasAttribute(getTypeService().getComposedTypeForCode(validatee.getItemtype()), getAttribute()))
		{

			final List<CMSItemModel> items = getModelService().getAttributeValue(validatee, getAttribute());

			if (!CollectionUtils.isEmpty(items))
			{
				final int distinctNamesCount = (int) items.stream().map(CMSItemModel::getName).distinct().count();

				if (distinctNamesCount < items.size())
				{
					getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
							.field(getAttribute()) //
							.errorCode(ITEM_WITH_NAME_ALREADY_EXIST) //
							.errorArgs(new Object[]
							{ getAttribute() }) //
							.build());
				}
			}
		}

	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected String getAttribute()
	{
		return attribute;
	}

	@Required
	public void setAttribute(final String attribute)
	{
		this.attribute = attribute;
	}
}
