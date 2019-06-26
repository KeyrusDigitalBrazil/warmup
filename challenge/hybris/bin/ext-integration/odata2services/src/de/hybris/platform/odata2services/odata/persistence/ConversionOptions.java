/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

public class ConversionOptions
{
	private boolean includeCollections = true;
	private List<NavigationSegment> navigationSegments;
	private List<List<NavigationPropertySegment>> expand;

	protected ConversionOptions()
	{
		navigationSegments = Collections.emptyList();
		expand = Collections.emptyList();
	}

	public static ConversionOptionsBuilder conversionOptionsBuilder()
	{
		return new ConversionOptionsBuilder();
	}

	public boolean isIncludeCollections()
	{
		return includeCollections;
	}

	protected void setIncludeCollections(final boolean includeCollections)
	{
		this.includeCollections = includeCollections;
	}

	public List<NavigationSegment> getNavigationSegments()
	{
		return navigationSegments;
	}

	protected void setNavigationSegments(final List<NavigationSegment> segments)
	{
		navigationSegments = segments != null
				? Collections.unmodifiableList(segments)
				: Collections.emptyList();
	}

	protected void setExpand(final List<List<NavigationPropertySegment>> expand)
	{
		this.expand = expand != null
				? Collections.unmodifiableList(expand)
				: Collections.emptyList();
	}

	public List<List<NavigationPropertySegment>> getExpand()
	{
		return expand;
	}

	/**
	 * Determines whether navigation segments are present in this options.
	 * @return {@code true}, if at least one navigation segment was added to this options; {@code false}, otherwise.
	 * @see #setNavigationSegments(List)
	 */
	public boolean isNavigationSegmentPresent()
	{
		return ! getNavigationSegments().isEmpty();
	}

	/**
	 * Determines whether an expand option is present in this options.
	 * @return {@code true}, if at least one nagivagtion property segment was added to this options; {@code false}, otherwise.
	 * @see #setExpand(List)
	 */
	public boolean isExpandPresent()
	{
		return getExpand().stream()
				.anyMatch(CollectionUtils::isNotEmpty);
	}

	/**
	 * Navigates the navigation segment.
	 * @param propertyName name of the item property for the navigation segment to navigate.
	 * @return new instance of conversion of options without the consumed navigation segment or this options, if the navigation
	 * did not happen because the segment does not exist or the property name is invalid.
	 */
	public ConversionOptions navigate(final String propertyName)
	{
		return StringUtils.isBlank(propertyName) || navigationSegments.isEmpty()
				? this
				: conversionOptionsBuilder().from(this)
						.withNavigationSegments(navigationSegments.subList(1, navigationSegments.size()))
						.build();
	}

	/**
	 * Determines whether the specified property name is the next navigation segment of this options.
	 * @param propertyName name of the property to enquire about
	 * @return {@code true}, if next navigation segment in this options matches the property name; {@code false}, if next navigation
	 * segment in this options does not match the property name of when there are no navigation segments in this options.
	 */
	public boolean isNextNavigationSegment(final String propertyName) throws EdmException
	{
		return !navigationSegments.isEmpty() && navigationSegments.get(0).getNavigationProperty().getName().equals(propertyName);
	}

	/**
	 * Determines whether the specified property name is the next expand segment of this options.
	 * @param propertyName name of the property to enquire about
	 * @return {@code true}, if next expand segment in this options matches the property name; {@code false}, if next expand
	 * segment in this options does not match the property name of when there are no expand segments in this options.
	 */
	public boolean isNextExpandSegment(final String propertyName) throws EdmException
	{
		for (final List<NavigationPropertySegment> segment : getExpand())
		{
			if (! segment.isEmpty() && segment.get(0).getNavigationProperty().getName().equals(propertyName))
			{
				return true;
			}
		}
		return false;
	}

	public static class ConversionOptionsBuilder
	{
		private final ConversionOptions options = new ConversionOptions();

		public ConversionOptionsBuilder withIncludeCollections(final boolean includeCollections)
		{
			this.options.setIncludeCollections(includeCollections);
			return this;
		}

		public ConversionOptionsBuilder withNavigationSegment(final NavigationSegment s)
		{
			final List<NavigationSegment> segments = new ArrayList<>(options.getNavigationSegments());
			segments.add(s);
			return withNavigationSegments(segments);
		}

		public ConversionOptionsBuilder withNavigationSegments(final List<NavigationSegment> navigationSegments)
		{
			this.options.setNavigationSegments(navigationSegments);
			return this;
		}

		public ConversionOptionsBuilder withExpand(final List<? extends List<NavigationPropertySegment>> expand)
		{
			final List<List<NavigationPropertySegment>> segments = expand != null
					? new ArrayList<>(expand)
					: null;
			this.options.setExpand(segments);
			return this;
		}

		public ConversionOptionsBuilder from(final ConversionOptions options)
		{
			withIncludeCollections(options.isIncludeCollections());
			withNavigationSegments(options.getNavigationSegments());
			withExpand(options.getExpand());

			return this;
		}

		public ConversionOptions build()
		{
			return options;
		}
	}
}
