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
package de.hybris.platform.cmsoccaddon.jaxb.adapters;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * This adapter is used to convert {@link de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO} into
 * {@link de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentListWsDTOAdapter.ListAdaptedComponents}
 */
public class ComponentListWsDTOAdapter extends XmlAdapter<ComponentListWsDTOAdapter.ListAdaptedComponents, ComponentListWsDTO>
{
	public static class ListAdaptedComponents
	{
		@XmlElement(name = "component")
		public List<ComponentAdaptedData> components = new ArrayList<ComponentAdaptedData>();

		private PaginationWsDTO pagination;

		private List<SortWsDTO> sorts;

		/**
		 * @return the pagination
		 */
		public PaginationWsDTO getPagination()
		{
			return pagination;
		}

		/**
		 * @param pagination
		 *           the pagination to set
		 */
		public void setPagination(final PaginationWsDTO pagination)
		{
			this.pagination = pagination;
		}

		/**
		 * @return the sorts
		 */
		public List<SortWsDTO> getSorts()
		{
			return sorts;
		}

		/**
		 * @param sorts
		 *           the sorts to set
		 */
		public void setSorts(final List<SortWsDTO> sorts)
		{
			this.sorts = sorts;
		}
	}

	@Override
	public ListAdaptedComponents marshal(final ComponentListWsDTO componentList)
	{
		if (componentList == null || componentList.getComponent() == null)
		{
			return null;
		}
		final ListAdaptedComponents listAdaptedComponent = new ListAdaptedComponents();

		final List<ComponentAdaptedData> convertedComponents = componentList.getComponent().stream()
				.map(ComponentAdapterUtil::convert).collect(toList());
		listAdaptedComponent.components.addAll(convertedComponents);

		return listAdaptedComponent;
	}

	@Override
	public ComponentListWsDTO unmarshal(final ListAdaptedComponents listAdapedComponents) throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
