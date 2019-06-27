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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.ticket.model.CsAgentGroupModel;


public class CsAgentGroupModelBuilder
{
	private final CsAgentGroupModel model;

	private CsAgentGroupModelBuilder()
	{
		model = new CsAgentGroupModel();
	}

	public static CsAgentGroupModelBuilder aModel()
	{
		return new CsAgentGroupModelBuilder();
	}

	private CsAgentGroupModel getModel()
	{
		return this.model;
	}

	public CsAgentGroupModel build()
	{
		return getModel();
	}

	public CsAgentGroupModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

}
