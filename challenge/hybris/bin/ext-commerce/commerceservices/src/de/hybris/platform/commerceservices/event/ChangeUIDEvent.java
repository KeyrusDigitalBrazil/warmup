/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;



/**
 * ChangeUID event, implementation of {@link AbstractCommerceUserEvent}
 */
public class ChangeUIDEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	private String oldUid;
	private String newUid;

	/**
	 * Default constructor
	 */
	public ChangeUIDEvent()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param oldUid
	 * @param newUid
	 */
	public ChangeUIDEvent(final String oldUid, final String newUid)
	{
		super();
		this.oldUid = oldUid;
		this.newUid = newUid;
	}

	public String getOldUid()
	{
		return oldUid;
	}

	public void setOldUid(final String oldUid)
	{
		this.oldUid = oldUid;
	}

	public String getNewUid()
	{
		return newUid;
	}

	public void setNewUid(final String newUid)
	{
		this.newUid = newUid;
	}

}
