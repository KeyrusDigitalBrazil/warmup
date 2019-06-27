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
package de.hybris.platform.patchesdemo.structure;

/**
 * Represent project releases. String used in constructor will be used to create paths to impexes that should be
 * imported in logs etc. (should be unique).
 */
public enum Release implements de.hybris.platform.patches.Release
{

	R1("01"), R2("02"), E1("ERROR");

	private String releaseId;

	Release(final String releaseId)
	{
		this.releaseId = releaseId;
	}

	@Override
	public String getReleaseId()
	{
		return this.releaseId;
	}

}
