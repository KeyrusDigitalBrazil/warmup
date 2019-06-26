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
package de.hybris.platform.cmsfacades.mediacontainers.impl;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link MediaContainerFacade} interface
 */
public class DefaultMediaContainerFacade implements MediaContainerFacade
{
	protected static final String MEDIA_CONTAINER_QUALIFIER = "mediaContainer_";

	private PersistentKeyGenerator mediaQualifierIdGenerator;

	private CMSAdminSiteService cmsAdminSiteService;

	private PermissionCRUDService permissionCRUDService;

	@Override
	public MediaContainerModel createMediaContainer()
	{
		if(!getPermissionCRUDService().canCreateTypeInstance(MediaContainerModel._TYPECODE))
		{
			PermissionExceptionUtils.createTypePermissionException(PermissionsConstants.CREATE, MediaContainerModel._TYPECODE);
		}

		final MediaContainerModel mediaContainerModel = new MediaContainerModel();
		mediaContainerModel.setQualifier(MEDIA_CONTAINER_QUALIFIER + getMediaQualifierIdGenerator().generate());
		mediaContainerModel.setCatalogVersion(getCmsAdminSiteService().getActiveCatalogVersion());
		return mediaContainerModel;
	}

	protected PersistentKeyGenerator getMediaQualifierIdGenerator()
	{
		return mediaQualifierIdGenerator;
	}

	@Required
	public void setMediaQualifierIdGenerator(final PersistentKeyGenerator mediaQualifierIdGenerator)
	{
		this.mediaQualifierIdGenerator = mediaQualifierIdGenerator;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	@Required
	public void setPermissionCRUDService(PermissionCRUDService permissionCRUDService)
	{
		this.permissionCRUDService = permissionCRUDService;
	}
}
