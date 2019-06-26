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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminTypeRestrictionsService;
import de.hybris.platform.cmsfacades.dto.ComponentTypeAndContentSlotValidationDto;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given component type is allowed in the given slot. This only considers the component's type
 * against the slot's type restrictions.
 * <p>
 * Returns <tt>TRUE</tt> if the component type is allowed in the slot; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ComponentTypeAllowedForContentSlotPredicate implements Predicate<ComponentTypeAndContentSlotValidationDto>
{
	private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeAllowedForContentSlotPredicate.class);

	private CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService;

	@Override
	public boolean test(final ComponentTypeAndContentSlotValidationDto target)
	{
		boolean result = true;
		try
		{
			final Boolean validType = getCmsAdminTypeRestrictionsService()
					.getTypeRestrictionsForContentSlot(target.getPage(), target.getContentSlot()).stream()
					.anyMatch(componentType -> componentType.getCode().equals(target.getComponentType()));

			if (!validType)
			{
				result = false;
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Unable to test if component is allowed in content slot.", e);
			result = false;
		}
		return result;
	}

	protected CMSAdminTypeRestrictionsService getCmsAdminTypeRestrictionsService()
	{
		return cmsAdminTypeRestrictionsService;
	}

	@Required
	public void setCmsAdminTypeRestrictionsService(final CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService)
	{
		this.cmsAdminTypeRestrictionsService = cmsAdminTypeRestrictionsService;
	}

}
