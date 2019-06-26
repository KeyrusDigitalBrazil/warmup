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
package de.hybris.platform.cmsfacades.version.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ITEM_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UID;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Base Validator for {@link CMSVersionData}
 */
public class BaseCMSVersionValidator implements Validator
{
	private BiPredicate<String, Class<?>> itemModelExistsPredicate;
	private BiPredicate<String, String> cmsVersionBelongsToCMSItemPredicate;
	private Predicate<String> cmsVersionExistsPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CMSVersionData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final CMSVersionData cmsVersionData = (CMSVersionData) obj;

		ValidationUtils.rejectIfEmpty(errors, FIELD_ITEM_UUID, CmsfacadesConstants.FIELD_REQUIRED);

		if (getItemModelExistsPredicate().negate().test(cmsVersionData.getItemUUID(), CMSItemModel.class))
		{
			errors.rejectValue(FIELD_ITEM_UUID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (cmsVersionData.getUid() != null)
		{
			if (getCmsVersionExistsPredicate().negate().test(cmsVersionData.getUid()))
			{
				errors.rejectValue(FIELD_UID, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
			}

			if (getCmsVersionBelongsToCMSItemPredicate().negate().test(cmsVersionData.getItemUUID(), cmsVersionData.getUid()))
			{
				errors.rejectValue(FIELD_UID, CmsfacadesConstants.VERSION_DOES_NOT_BELONG_TO_CMS_ITEM);
			}
		}

	}

	protected BiPredicate<String, Class<?>> getItemModelExistsPredicate()
	{
		return itemModelExistsPredicate;
	}

	@Required
	public void setItemModelExistsPredicate(final BiPredicate<String, Class<?>> itemModelExistsPredicate)
	{
		this.itemModelExistsPredicate = itemModelExistsPredicate;
	}

	protected BiPredicate<String, String> getCmsVersionBelongsToCMSItemPredicate()
	{
		return cmsVersionBelongsToCMSItemPredicate;
	}

	@Required
	public void setCmsVersionBelongsToCMSItemPredicate(final BiPredicate<String, String> cmsVersionBelongsToCMSItemPredicate)
	{
		this.cmsVersionBelongsToCMSItemPredicate = cmsVersionBelongsToCMSItemPredicate;
	}

	protected Predicate<String> getCmsVersionExistsPredicate()
	{
		return cmsVersionExistsPredicate;
	}

	@Required
	public void setCmsVersionExistsPredicate(final Predicate<String> cmsVersionExistsPredicate)
	{
		this.cmsVersionExistsPredicate = cmsVersionExistsPredicate;
	}

}
