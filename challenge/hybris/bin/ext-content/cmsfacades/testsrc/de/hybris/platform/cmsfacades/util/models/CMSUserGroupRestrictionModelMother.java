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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.cmsfacades.util.builder.CMSUserGroupRestrictionModelBuilder;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.Collection;


public class CMSUserGroupRestrictionModelMother extends AbstractModelMother<CMSUserGroupRestrictionModel>
{
	public static final String UID_CMSMANAGER = "cmsmanager";
	public static final String NAME_CMSMANAGER = "name-cmsmanager";

	private CMSRestrictionDao restrictionDao;
	private UserGroupModelMother userGroupModelMother;

	@SuppressWarnings(
			{ "unchecked", "rawtypes" })
	public CMSUserGroupRestrictionModel cmsManager(final CatalogVersionModel catalogVersion, final AbstractPageModel... pages)
	{
		return getFromCollectionOrSaveAndReturn( //
				() -> (Collection) getRestrictionDao().findRestrictionsById(UID_CMSMANAGER, catalogVersion), //
				() -> CMSUserGroupRestrictionModelBuilder.aModel() //
						.withUid(UID_CMSMANAGER) //
						.withCatalogVersion(catalogVersion) //
						.withName(NAME_CMSMANAGER) //
						.withPages(pages) //
						.withIncludeSubgroups(false) //
						.withUserGroups(getUserGroupModelMother().userGroup01(), getUserGroupModelMother().userGroup02()) //
						.build());
	}

	public UserGroupModelMother getUserGroupModelMother()
	{
		return userGroupModelMother;
	}

	public void setUserGroupModelMother(UserGroupModelMother userGroupModelMother)
	{
		this.userGroupModelMother = userGroupModelMother;
	}

	public CMSRestrictionDao getRestrictionDao()
	{
		return restrictionDao;
	}

	public void setRestrictionDao(final CMSRestrictionDao restrictionDao)
	{
		this.restrictionDao = restrictionDao;
	}
}
