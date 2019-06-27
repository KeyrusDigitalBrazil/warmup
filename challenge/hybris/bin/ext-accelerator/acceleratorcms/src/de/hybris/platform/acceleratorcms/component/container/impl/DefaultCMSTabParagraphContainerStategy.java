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
package de.hybris.platform.acceleratorcms.component.container.impl;

import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphContainerModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implement a strategy to return the CMSTabParagraphContainer as the result component if needed
 */
public class DefaultCMSTabParagraphContainerStategy implements CMSComponentContainerStrategy
{
	private TypeService typeService;
	private List<String> showContainerForTypes;

	@Override
	public List<AbstractCMSComponentModel> getDisplayComponentsForContainer(final AbstractCMSComponentContainerModel container)
	{
		final List<AbstractCMSComponentModel> components = (List) container.getSimpleCMSComponents();
		if (container instanceof CMSTabParagraphContainerModel && needShowContainer(components))
		{
			return Arrays.asList(container);
		}
		return components;
	}

	/**
	 * check whether show the container in corresponding jsp
	 *
	 * @param components
	 *           the children components
	 * @return children components all match given types then return true otherwise false
	 */
	protected boolean needShowContainer(final List<AbstractCMSComponentModel> components)
	{
		return components.stream().allMatch(component -> getShowContainerForTypes()
				.contains(getTypeService().getComposedTypeForClass(component.getClass()).getCode()));
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected List<String> getShowContainerForTypes()
	{
		return showContainerForTypes;
	}

	@Required
	public void setShowContainerForTypes(final List<String> showContainerForTypes)
	{
		this.showContainerForTypes = showContainerForTypes;
	}

}
