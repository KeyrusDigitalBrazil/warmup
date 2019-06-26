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
package de.hybris.platform.assistedservicefacades.customer360.impl;

import de.hybris.platform.assistedservicefacades.customer360.AdditionalInformationFrameworkFacade;
import de.hybris.platform.assistedservicefacades.customer360.Fragment;
import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.Section;
import de.hybris.platform.assistedservicefacades.customer360.comparators.FragmentPriorityComparator;
import de.hybris.platform.assistedservicefacades.customer360.comparators.SectionPriorityComparator;
import de.hybris.platform.core.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link AdditionalInformationFrameworkFacade}
 */
public class DefaultAdditionalInformationFrameworkFacade implements AdditionalInformationFrameworkFacade
{
	private static final Logger LOG = Logger.getLogger(AdditionalInformationFrameworkFacade.class);

	private List<Section> sections;
	private Map<String, FragmentModelProvider> modelProvidersMap;
	private Map<String, String> jspProvidersMap;

	@Override
	public List<Section> getSections()
	{
		return this.sections.stream().sorted(new SectionPriorityComparator()).map(s -> {
			if (CollectionUtils.isNotEmpty(s.getFragments()))
			{
				final List<Fragment> sortedFragments = s.getFragments().stream().sorted(new FragmentPriorityComparator())
						.collect(Collectors.toList());
				s.setFragments(sortedFragments);
			}
			return s;
		}).collect(Collectors.toList());
	}

	@Override
	public Section getSection(final String sectionId)
	{
		for (final Section section : getSections())
		{
			if (section.getId().equals(sectionId))
			{
				return section;
			}
		}
		return null;
	}

	@Override
	public Fragment getFragment(final String sectionId, final String fragmentId, final Map<String, String> parameters)
	{
		Fragment populatedFragment = null;

		final Section section = getSection(sectionId);
		if (null != section)
		{
			final List<Fragment> fragments = section.getFragments();
			populatedFragment = fragments.stream().filter(fragment -> fragment.getId().equalsIgnoreCase(fragmentId)).findFirst()
					.orElse(null);
		}
		else // in case section isn't specified search for bean name
		{
			try
			{
				populatedFragment = Registry.getApplicationContext().getBean(fragmentId, Fragment.class);
			}
			catch (final BeansException e)
			{
				LOG.warn(e);
			}
		}

		if (null != populatedFragment)
		{
			final FragmentModelProvider modelProvider = getModelProvidersMap().get(populatedFragment.getId());
			if (modelProvider != null)
			{
				final Map<String, String> aggregatedParametersMap = new HashMap<>();

				if (!MapUtils.isEmpty(populatedFragment.getProperties()))
				{
					aggregatedParametersMap.putAll(populatedFragment.getProperties());
				}
				if (!MapUtils.isEmpty(parameters))
				{
					aggregatedParametersMap.putAll(parameters);
				}
				populatedFragment.setData(modelProvider.getModel(aggregatedParametersMap));
			}
			populatedFragment.setJspPath(getJspProvidersMap().get(populatedFragment.getId()));
		}
		return populatedFragment;
	}

	@Required
	public void setSections(final List<Section> sections)
	{
		this.sections = sections;
	}

	protected Map<String, FragmentModelProvider> getModelProvidersMap()
	{
		return modelProvidersMap;
	}

	@Required
	public void setModelProvidersMap(final Map<String, FragmentModelProvider> modelProvidersMap)
	{
		this.modelProvidersMap = modelProvidersMap;
	}

	protected Map<String, String> getJspProvidersMap()
	{
		return jspProvidersMap;
	}

	@Required
	public void setJspProvidersMap(final Map<String, String> jspProvidersMap)
	{
		this.jspProvidersMap = jspProvidersMap;
	}
}