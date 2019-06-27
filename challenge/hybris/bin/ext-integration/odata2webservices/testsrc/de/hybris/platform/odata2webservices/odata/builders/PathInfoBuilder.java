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

package de.hybris.platform.odata2webservices.odata.builders;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.apache.olingo.odata2.core.ODataPathSegmentImpl;
import org.apache.olingo.odata2.core.PathInfoImpl;

/**
 * A builder to create {@link org.apache.olingo.odata2.api.uri.PathInfo}s according to specifications in the tests.
 */
public class PathInfoBuilder
{
	private static final String BASE_API_URL = "https://localhost:8080/odata2webservices";

	private EntityKey entityKey;
	private String serviceName;
	private String requestPath;
	private String navigationSegment;

	private PathInfoBuilder()
	{
		// non-instantiable except through the factory method
	}

	/**
	 * Creates new instance of this builder.
	 *
	 * @return instance of the builder, which has no specifications done to it yet.
	 */
	public static PathInfoBuilder pathInfo()
	{
		return new PathInfoBuilder();
	}

	public PathInfoBuilder withServiceName(final String name)
	{
		serviceName = name;
		return this;
	}

	public PathInfoBuilder withEntitySet(final String set)
	{
		requestPath = set;
		return this;
	}

	public PathInfoBuilder withEntityKeys(final String... keys)
	{
		final List<String> keyList = keys != null ? Arrays.asList(keys) : null;
		entityKey = new EntityKey(keyList);
		return this;
	}

	public PathInfoBuilder withNavigationSegment(final String segment)
	{
		navigationSegment = segment;
		return this;
	}

	public PathInfoBuilder withRequestPath(final String path)
	{
		requestPath = path;
		return this;
	}

	public PathInfoImpl build()
	{
		final PathInfoImpl pathInfo = new PathInfoImpl();
		pathInfo.setServiceRoot(serviceRootUri());
		pathInfo.setPrecedingPathSegment(pathSegments(serviceName));
		pathInfo.setODataPathSegment(pathSegments(requestedSegment(), navigationSegment));
		pathInfo.setRequestUri(requestUri());
		return pathInfo;
	}

	private URI requestUri()
	{
		final String uri = StringUtils.isNotBlank(navigationSegment)
				? (serviceRootUri().toString() + "/" + requestedSegment() + "/" + navigationSegment)
				:  StringUtils.isBlank(requestPath) ? serviceRootUri().toString() : (serviceRootUri().toString() + "/" + requestedSegment());
		return URI.create(uri);
	}

	private String requestedSegment()
	{
		return (entityKey != null)
				? (requestPath + entityKey.toString())
				: requestPath;
	}

	private List<PathSegment> pathSegments(final String... segmentNames)
	{
		return Stream.of(segmentNames)
				.filter(StringUtils::isNotBlank)
				.map(this::pathSegment)
				.collect(Collectors.toList());
	}

	private ODataPathSegmentImpl pathSegment(final String segmentName)
	{
		return new ODataPathSegmentImpl(segmentName, new HashMap<>());
	}

	private URI serviceRootUri()
	{
		return URI.create(BASE_API_URL + "/" + serviceName + "/");
	}


	private static class EntityKey
	{
		private final Collection<String> keyAttributeValues;

		private EntityKey(final Collection<String> keys)
		{
			keyAttributeValues = keys;
		}

		@Override
		public String toString()
		{
			return (keyAttributeValues == null)
					? ""
					: ("(" + parseKeys() + ")");
		}

		private String parseKeys()
		{
			if (keyAttributeValues.isEmpty())
			{
				return "''";
			}
			if (keyAttributeValues.size() == 1)
			{
				return "'" + urlEncoded(keyAttributeValues.iterator().next()) + "'";
			}
			return keyAttributeValues.stream()
					.map(key -> key.split("="))
					.map(splitKey -> splitKey[0] + "='" + urlEncoded(splitKey[1]) + "'")
					.reduce((x, y) -> x + "," + y).get();
		}

		private static String urlEncoded(final String value)
		{
			try
			{
				return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
			}
			catch (final UnsupportedEncodingException e)
			{
				throw new IllegalArgumentException("Failed to encode '" + value + "' as UTF-8", e);
			}
		}
	}
}
