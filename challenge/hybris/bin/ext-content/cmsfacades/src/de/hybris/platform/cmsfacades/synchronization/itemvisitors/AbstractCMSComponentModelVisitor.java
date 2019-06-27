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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors;

import static com.google.common.collect.Lists.newLinkedList;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for visiting {@link AbstractCMSComponentModel} models for the cms synchronization service to work properly. 
 * In this implementation, it will collect all component's restrictions.   
 *
 * @param <CMSCOMPONENTTYPE> the component type that extends {@link AbstractCMSComponentModel}
 */
public abstract class AbstractCMSComponentModelVisitor<CMSCOMPONENTTYPE extends AbstractCMSComponentModel> implements ItemVisitor<CMSCOMPONENTTYPE>
{

	@Override
	public List<ItemModel> visit(CMSCOMPONENTTYPE source, List<ItemModel> arg1, Map<String, Object> arg2)
	{
		return newLinkedList(source.getRestrictions());
	}

}
