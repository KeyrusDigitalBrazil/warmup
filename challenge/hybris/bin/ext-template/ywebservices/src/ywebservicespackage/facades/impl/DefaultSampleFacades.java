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
package ywebservicespackage.facades.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;

import ywebservicespackage.data.UserData;
import ywebservicespackage.dto.SampleWsDTO;
import ywebservicespackage.dto.TestMapWsDTO;
import ywebservicespackage.facades.SampleFacades;
import ywebservicespackage.services.SampleUserServices;


public class DefaultSampleFacades implements SampleFacades
{
	private SampleUserServices sampleUserService;

	private Converter<UserModel, UserData> userConverter;

	@Override
	@Cacheable(value = "sampleCache", key = "T(de.hybris.platform.webservicescommons.cache.CacheKeyGenerator).generateKey(false,false,'getSample',#value)")
	public SampleWsDTO getSampleWsDTO(final String value)
	{
		final SampleWsDTO sampleDTO = new SampleWsDTO();
		sampleDTO.setValue(value);
		return sampleDTO;
	}


	@Override
	@Secured("ROLE_CUSTOMERGROUP")
	public UserData getUser(final String id)
	{
		final UserModel user = getSampleUserService().getUserById(id);
		if (user != null)
		{
			return getUserConverter().convert(user);
		}
		else
		{
			return null;
		}
	}

	@Override
	public List<UserData> getUsers()
	{
		final Collection<UserModel> userModels = getSampleUserService().getUsers();
		return userModels.stream().map(u -> getUserConverter().convert(u)).collect(Collectors.toList());
	}

	@Override
	public SearchPageData<UserData> getUsers(final SearchPageData<?> params)
	{
		final PaginationData pagination = params.getPagination();

		final Collection<UserModel> userModels = getSampleUserService().getUsers();
		final List<UserData> limitedUsers = userModels.stream().skip(pagination.getCurrentPage() * (long) pagination.getPageSize())
				.limit(pagination.getPageSize()).map(u -> getUserConverter().convert(u)).collect(Collectors.toList());

		final PaginationData resultPagination = new PaginationData();
		resultPagination.setNeedsTotal(pagination.isNeedsTotal());
		resultPagination.setCurrentPage(pagination.getCurrentPage());
		resultPagination.setPageSize(limitedUsers.size());
		resultPagination.setTotalNumberOfResults(userModels.size());
		resultPagination.setNumberOfPages(userModels.size() / pagination.getPageSize());

		final SearchPageData<UserData> result = new SearchPageData<>();
		result.setPagination(resultPagination);
		result.setResults(limitedUsers);

		return result;
	}

	@Override
	public TestMapWsDTO getMap()
	{
		final TestMapWsDTO result = new TestMapWsDTO();
		result.setIntegerMap(new HashMap<String, Integer>());
		result.setStringMap(new HashMap<String, String>());

		result.getIntegerMap().put("integerKey", Integer.valueOf(10001));
		result.getStringMap().put("StringKey", "StringValue");
		return result;
	}

	public SampleUserServices getSampleUserService()
	{
		return sampleUserService;
	}

	@Required
	public void setSampleUserService(final SampleUserServices sampleUserService)
	{
		this.sampleUserService = sampleUserService;
	}

	public Converter<UserModel, UserData> getUserConverter()
	{
		return userConverter;
	}

	@Required
	public void setUserConverter(final Converter<UserModel, UserData> userConverter)
	{
		this.userConverter = userConverter;
	}
}
