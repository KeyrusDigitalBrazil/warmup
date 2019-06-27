/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.servicelayer.user.daos.UserGroupDao;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.warehousing.util.builder.CsAgentGroupModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class CsAgentGroups extends AbstractItems<CsAgentGroupModel>
{
	public static final String UID_FRAUDAGENT = "fraudAgentGroup";

	private UserGroupDao userGroupDao;

	public CsAgentGroupModel fraudAgentGroup()
	{
		return getOrCreateCsAgentGroup(UID_FRAUDAGENT);
	}

	protected CsAgentGroupModel getOrCreateCsAgentGroup(final String uid)
	{
		return getOrSaveAndReturn(() -> (CsAgentGroupModel) getUserGroupDao().findUserGroupByUid(uid),
				() -> CsAgentGroupModelBuilder.aModel().withUid(uid).build());
	}

	public UserGroupDao getUserGroupDao()
	{
		return userGroupDao;
	}

	@Required
	public void setUserGroupDao(final UserGroupDao userGroupDao)
	{
		this.userGroupDao = userGroupDao;
	}


}
