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

import de.hybris.platform.cms2.model.pages.ContentPageModel;

import java.util.function.Predicate;


/**
 * Predicate to test if a given page type code is a Content page code.
 */
public class ContentPageTypeCodePredicate implements Predicate<String>
{
	@Override
	public boolean test(String pageTypeModel)
	{
		return ContentPageModel._TYPECODE.equals(pageTypeModel);
	}
}
