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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Populator for {@link B2BUnitModel}.
 */
public class B2BUnitNodePopulator implements Populator<B2BUnitModel, B2BUnitNodeData>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private Converter<B2BUnitModel, B2BUnitNodeData> childNodeConverter;

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitNodeData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setActive(Boolean.TRUE.equals(source.getActive()));
		target.setId(source.getUid());
		target.setName((source.getName() == null ? source.getUid() : source.getName()));

		populateParent(source, target);
		populateChildren(source, target);
	}

	protected void populateChildren(final B2BUnitModel source, final B2BUnitNodeData target)
	{
		final List<B2BUnitNodeData> childNodes = new ArrayList<B2BUnitNodeData>();
		if (CollectionUtils.isNotEmpty(source.getMembers()))
		{
			for (final PrincipalModel principalModel : source.getMembers())
			{
				if (principalModel instanceof B2BUnitModel)
				{
					childNodes.add(getChildNodeConverter().convert((B2BUnitModel) principalModel));
				}
			}
		}
		target.setChildren(childNodes);
	}

	protected void populateParent(final B2BUnitModel source, final B2BUnitNodeData target)
	{
		final B2BUnitModel parent = getB2BUnitService().getParent(source);
		if (parent != null)
		{
			target.setParent(parent.getUid());
		}
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> getChildNodeConverter()
	{
		if (childNodeConverter == null)
		{
			childNodeConverter = lookupChildNodeConverter();
		}
		return childNodeConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> lookupChildNodeConverter()
	{
		throw new IllegalStateException("specify lookupChildNodeConverter via <lookup-method> in spring config.");
	}
}
