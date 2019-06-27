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
package de.hybris.platform.bmecat.parser.taglistener;

import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.constants.BMECatConstants;
import de.hybris.platform.bmecat.parser.BMECatObjectProcessor;

import java.util.Arrays;
import java.util.Collection;


/**
 * 
 * 
 */
public class ClassificationGroupsTagListener extends DefaultBMECatTagListener
{

	/**
	 * @param parent
	 */
	public ClassificationGroupsTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * @param parent
	 */
	public ClassificationGroupsTagListener(final DefaultBMECatTagListener parent)
	{
		super(parent);
	}

	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new ClassificationGroupTagListener(this) });
	}

	@Override
	protected Object processEndElement(final BMECatObjectProcessor processor)
	{
		return getSubTagValueCollection(BMECatConstants.XML.TAG.CLASSIFICATION_GROUP);
	}

	public String getTagName()
	{
		return BMECatConstants.XML.TAG.CLASSIFICATION_GROUPS;
	}

}
