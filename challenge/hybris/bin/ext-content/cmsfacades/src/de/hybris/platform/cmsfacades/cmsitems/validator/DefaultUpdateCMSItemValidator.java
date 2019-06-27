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

import static de.hybris.platform.cms2.model.contents.CMSItemModel.NAME;
import static de.hybris.platform.cms2.model.contents.CMSItemModel.UID;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTAINS_INVALID_CHARS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_LENGTH_EXCEEDED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_NOT_ALLOWED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.util.Strings.isBlank;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the validator for {@link CMSItemModel}
 */
public class DefaultUpdateCMSItemValidator implements Validator<CMSItemModel>
{
	private CMSAdminItemService cmsAdminItemService;
	private Predicate<String> onlyHasSupportedCharactersPredicate;
	private ValidationErrorsProvider validationErrorsProvider;
	private ModelService modelService;
	private Predicate<String> validStringLengthPredicate;
	private Predicate<CMSItemModel> cmsItemNameExistsPredicate;
	private OriginalClonedItemProvider originalClonedItemProvider;

	@Override
	public void validate(final CMSItemModel itemModel)
	{
		final CMSItemModel originalCmsItem = (CMSItemModel) getOriginalClonedItemProvider().getCurrentItem();

		if (isNotBlank(itemModel.getUid()) && !getOnlyHasSupportedCharactersPredicate().test(itemModel.getUid()))
		{
			addError(itemModel, FIELD_CONTAINS_INVALID_CHARS, UID, itemModel.getUid());
		}

		if (!getValidStringLengthPredicate().test(itemModel.getName()))
		{
			addError(itemModel, FIELD_LENGTH_EXCEEDED, NAME, itemModel.getName());
		}

		if (isBlank(itemModel.getName()))
		{
			addError(itemModel, FIELD_REQUIRED, NAME, itemModel.getName());
		}

		// validates if the uid has changed
		if (itemModel.getUid() != null && !itemModel.getUid().equals(originalCmsItem.getUid()))
		{
			addError(itemModel, FIELD_NOT_ALLOWED, UID, itemModel.getUid());
		}

		// It's important to check if the name already exists, even if it has not changed. This can happen when restoring a page.
		// Moreover, the cmsItemNameExistsPredicate already checks if the name is used in OTHER cms item other than the current.
		if (StringUtils.isNotBlank(itemModel.getName()) && getCmsItemNameExistsPredicate().test(itemModel))
		{
			addError(itemModel, FIELD_ALREADY_EXIST, NAME, itemModel.getName());
		}
	}

	/**
	 * convenience method to add a UID related error
	 * @param itemModel the itemModel the UID of which is invalid
	 * @param errorCode the i18n key mentioning the error
	 */
	protected void addError(final CMSItemModel itemModel, final String errorCode, final String field, final String rejectedValue)
	{
		getValidationErrorsProvider().getCurrentValidationErrors()
				.add(newValidationErrorBuilder() //
						.field(field) //
						.errorCode(errorCode) //
						.rejectedValue(rejectedValue) //
						.build());
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
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

	@Required
	public void setOnlyHasSupportedCharactersPredicate(final Predicate<String> onlyHasSupportedCharactersPredicate)
	{
		this.onlyHasSupportedCharactersPredicate = onlyHasSupportedCharactersPredicate;
	}

	protected Predicate<String> getOnlyHasSupportedCharactersPredicate()
	{
		return onlyHasSupportedCharactersPredicate;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected Predicate<String> getValidStringLengthPredicate()
	{
		return validStringLengthPredicate;
	}

	@Required
	public void setValidStringLengthPredicate(final Predicate<String> validStringLengthPredicate)
	{
		this.validStringLengthPredicate = validStringLengthPredicate;
	}

	protected Predicate<CMSItemModel> getCmsItemNameExistsPredicate()
	{
		return cmsItemNameExistsPredicate;
	}

	@Required
	public void setCmsItemNameExistsPredicate(final Predicate<CMSItemModel> cmsItemNameExistsPredicate)
	{
		this.cmsItemNameExistsPredicate = cmsItemNameExistsPredicate;
	}

	protected OriginalClonedItemProvider getOriginalClonedItemProvider()
	{
		return originalClonedItemProvider;
	}

	@Required
	public void setOriginalClonedItemProvider(final OriginalClonedItemProvider originalClonedItemProvider)
	{
		this.originalClonedItemProvider = originalClonedItemProvider;
	}
}
