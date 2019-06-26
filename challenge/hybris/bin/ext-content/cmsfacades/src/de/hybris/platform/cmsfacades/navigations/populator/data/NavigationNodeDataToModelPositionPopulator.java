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
package de.hybris.platform.cmsfacades.navigations.populator.data;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link CMSNavigationNodeModel}'s the position of the node in relation to its
 * siblings.
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade} instead.
 */
@Deprecated
public class NavigationNodeDataToModelPositionPopulator implements Populator<NavigationNodeData, CMSNavigationNodeModel>
{

	private AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter;

	@Override
	public void populate(final NavigationNodeData source, final CMSNavigationNodeModel target) throws ConversionException
	{
		final NavigationNodeData currentModel = getNavigationModelToDataConverter().convert(target);
		if (ObjectUtils.notEqual(source.getPosition(), currentModel.getPosition()))
		{
			final CMSNavigationNodeModel parent = target.getParent();
			final List<CMSNavigationNodeModel> children = new LinkedList<>(parent.getChildren());

			children.removeIf(cmsNavigationNodeModel -> StringUtils.equals(cmsNavigationNodeModel.getUid(), target.getUid()));

			final int position = getFinalPosition(source.getPosition(), children.size());
			children.add(position, target);

			parent.setChildren(children);
		}
	}

	/**
	 * Returns the final position of the node to make sure the node's position is within parent's list boundaries
	 *
	 * @param position
	 * 		the desired position
	 * @param childrenSize
	 * 		the size of the parent's children list
	 * @return the final position where the node will be put
	 */
	protected int getFinalPosition(final Integer position, final Integer childrenSize)
	{
		if (childrenSize == 0 || position <= 0)
		{
			return 0;
		}
		else if (position > childrenSize)
		{
			return childrenSize;
		}
		else
		{
			return position;
		}
	}

	protected AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> getNavigationModelToDataConverter()
	{
		return navigationModelToDataConverter;
	}

	@Required
	public void setNavigationModelToDataConverter(
			final AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter)
	{
		this.navigationModelToDataConverter = navigationModelToDataConverter;
	}
}
