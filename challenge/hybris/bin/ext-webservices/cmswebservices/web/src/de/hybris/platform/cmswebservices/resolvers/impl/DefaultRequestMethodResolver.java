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
package de.hybris.platform.cmswebservices.resolvers.impl;

import de.hybris.platform.cmswebservices.resolvers.RequestMethodResolver;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Default implementation of {@link RequestMethodResolver} to resolve a method name for a request.
 */
public class DefaultRequestMethodResolver implements RequestMethodResolver
{
	private List<String> postToGetUriList;
	private PathMatcher pathMatcher;

	/**
	 * {@inheritDoc}
	 * The function uses a list of uri. Whenever a POST request is made for one of them the function returns GET as a result.
	 */
	@Override
	public String resolvePostToGet(HttpServletRequest request)
	{
		if (!request.getMethod().equals(HttpMethod.POST.name()))
		{
			return request.getMethod();
		}

		final boolean found = getPostToGetUriList().stream()
				.anyMatch(uriRegex -> getPathMatcher().match(uriRegex, request.getServletPath()));
		return found ? HttpMethod.GET.name() : request.getMethod();
	}

	protected List<String> getPostToGetUriList()
	{
		return postToGetUriList;
	}

	@Required
	public void setPostToGetUriList(List<String> postToGetUriList)
	{
		this.postToGetUriList = postToGetUriList;
	}

	protected PathMatcher getPathMatcher()
	{
		return pathMatcher;
	}

	@Required
	public void setPathMatcher(PathMatcher pathMatcher)
	{
		this.pathMatcher = pathMatcher;
	}
}
