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
package de.hybris.platform.assistedserviceyprofilefacades.impl;

import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;
import de.hybris.platform.assistedserviceyprofilefacades.YProfileAffinityFacade;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.DeviceAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.RecentlyViewedComparator;
import de.hybris.platform.assistedserviceyprofilefacades.data.TechnologyUsedData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import de.hybris.platform.yaasyprofileconnect.yaas.Profile;
import de.hybris.platform.yaasyprofileconnect.yaas.ProfileReference;
import de.hybris.platform.yaasyprofileconnect.yaas.UserAgent;
import de.hybris.platform.yaasyprofileconnect.yaas.client.AsmProfileDataServiceClientAdapter;
import de.hybris.platform.yaasyprofileconnect.yaas.client.AsmProfileIdentityServiceClientAdapter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.assistedservicefacades.impl.DefaultAssistedServiceFacade.ASM_CUSTOMER_PROFILE_REFERENCES;
import static de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants.IDENTITY_ORIGIN_USER_ACCOUNT;
import static de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants.IDENTITY_TYPE_EMAIL;


/**
 * Default implementation of {@link YProfileAffinityFacade} that uses json data from yprofile.
 */
public class DefaultYProfileAffinityFacade implements YProfileAffinityFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultYProfileAffinityFacade.class);

	private UserService userService;
	private SessionService sessionService;
	private AsmProfileDataServiceClientAdapter asmProfileDataServiceClientAdapter;
	private AsmProfileIdentityServiceClientAdapter asmProfileIdentityServiceClientAdapter;
	private Converter<Map.Entry<String,Affinity>, ProductAffinityData> productAffinityConverter;
	private Converter<Map.Entry<String,Affinity>, CategoryAffinityData> categoryAffinityConverter;
	private Converter<Map.Entry<String, UserAgent>, TechnologyUsedData> deviceAffinityConverter;


	/**
	 * Returns list of products affinities associated to the current session customer.
	 *
	 * @param productAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed.
	 *
	 * @return List<ProductAffinityData>
	 */
	@Override
	public List<ProductAffinityData> getProductAffinities(final ProductAffinityParameterData productAffinityParameterData)
	{
		final Map<String, Affinity> productAffinitiesMap;

		try
		{
			productAffinitiesMap = getProfileData(YaasyprofileconnectConstants.SCHEMA_COMMERCE_PRODUCT_AFFINITY)
					.get().getInsights().getAffinities().getProducts();
		}
		catch (final NoSuchElementException|NullPointerException e)
		{
			LOG.warn("Couldn't retrieve product affinity data", e);
			return new ArrayList<>();
		}

		final List sortedProducts = getSortedAffinities(productAffinitiesMap, productAffinityParameterData.getSizeLimit());

		return Converters.convertAll(sortedProducts, getProductAffinityConverter());
	}

	/**
	 * Returns list of categories affinities associated to the current session customer.
	 *
	 * @param categoryAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed.
	 *
	 * @return List<CategoryAffinityData>
	 */
	@Override
	public List<CategoryAffinityData> getCategoryAffinities(final CategoryAffinityParameterData categoryAffinityParameterData)
	{
		final Map<String, Affinity> categoryAffinitiesMap;

		try
		{
			categoryAffinitiesMap = getProfileData(YaasyprofileconnectConstants.SCHEMA_COMMERCE_CATEGORY_AFFINITY)
					.get().getInsights().getAffinities().getCategories();
		}
		catch (final NoSuchElementException|NullPointerException e)
		{
			LOG.warn("Couldn't retrieve category affinity data", e);
			return new ArrayList<>();
		}

		final List sortedCategories = getSortedAffinities(categoryAffinitiesMap, categoryAffinityParameterData.getSizeLimit());

		return  Converters.convertAll(sortedCategories, getCategoryAffinityConverter());
	}

	/**
	 * Returns list of device affinities associated to the current session customer.
	 *
	 * @param deviceAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed.
	 * @return List<TechnologyUsedData>
	 */
	@Override
	public List<TechnologyUsedData> getDeviceAffinities(final DeviceAffinityParameterData deviceAffinityParameterData)
	{
		final Map<String, UserAgent> deviceAffinitiesMap;

		try
		{
			deviceAffinitiesMap = getProfileData(YaasyprofileconnectConstants.SCHEMA_COMMERCE_DEVICE_AFFINITY).get().getUserAgents();
		}
		catch (NoSuchElementException|NullPointerException e)
		{
			LOG.warn("Couldn't retrieve device affinity data", e);
			return new ArrayList<>();
		}

		return  Converters.convertAll(deviceAffinitiesMap.entrySet().parallelStream().limit(deviceAffinityParameterData.getSizeLimit()).collect(Collectors.toList()),
				getDeviceAffinityConverter());
	}

	/**
	 * Returns list of device affinities associated to the current session customer.
	 *
	 * @param profileFieldsSchema
	 *           schema for retrieving a profile populated by schema data e.g. categories affinities or userAgents.
	 * @return Optional<Profile>
	 */
	protected Optional<Profile> getProfileData(String profileFieldsSchema)
	{
		final UserModel user = getUserService().getCurrentUser();
		if (StringUtils.isEmpty(profileFieldsSchema))
		{
			LOG.warn("Mappers don't define any required fields so no data will be read from Yaas Profile");
			return Optional.empty();
		}

		final String profileId = getProfileId(user);

		if (StringUtils.isNotEmpty(profileId))
		{
			try
			{
				 return Optional.of(asmProfileDataServiceClientAdapter.getProfile(profileId, profileFieldsSchema));
			}
			catch (final NotFoundException e)
			{
				LOG.debug("Profile not found : {}", profileId, e);
			}
			catch (final HttpException e)
			{
				LOG.warn("Failed to connect to Profile Endpoint. Data for user {} is not retrieved", user.getUid(), e);
			}
			catch (final SystemException e)
			{
				LOG.warn(
						"Failed to get yaas profile for user {}. Check if yaas configuration is properly defined for AsmProfileDataServiceClient. Error message : {} ",
						user.getUid(), e);
			}
		}

		return Optional.empty();
	}

	/**
	 * Returns id of Yaas Profile associated to current session customer.
	 *
	 * @param user
	 *           schema for retrieving a profile populated by schema data e.g. categories affinities or userAgents.
	 * @return String
	 */
	protected String getProfileId(UserModel user)
	{
		Map<String, String> storedProfileReferences = getSessionService().getAttribute(ASM_CUSTOMER_PROFILE_REFERENCES);

		if (!MapUtils.isNotEmpty(storedProfileReferences))
		{
			storedProfileReferences = new HashMap<>();
		}
		else if (storedProfileReferences.containsKey(user.getUid()))
		{
			return storedProfileReferences.get(user.getUid());
		}

		List<ProfileReference> profileReferences = null;

		try
		{
			profileReferences= asmProfileIdentityServiceClientAdapter.getProfileReferences(user.getUid(), IDENTITY_TYPE_EMAIL,
					IDENTITY_ORIGIN_USER_ACCOUNT);
		}
		catch (final NotFoundException e)
		{
			LOG.warn("Profile reference not found for user {}", user.getUid(), e);
		}
		catch (final HttpException e)
		{
			LOG.warn("Failed to connect to Profile Endpoint. Profile Id for user {} is not retrieved", user.getUid(), e);
		}
		catch (final SystemException e)
		{
			LOG.warn(
					"Failed to get yaas profile reference for user {}. Check if yaas configuration is properly defined for AsmProfileIdentityServiceClient. Error message : {} ",
					user.getUid(), e);
		}

		//We can use any profile reference of the user because profile data will be merged on Yaas side.
		final String profileId;
		if (CollectionUtils.isNotEmpty(profileReferences))
		{
			profileId = profileReferences.get(0).getProfileId();
			storedProfileReferences.put(user.getUid(), profileId);
			getSessionService().setAttribute(ASM_CUSTOMER_PROFILE_REFERENCES, storedProfileReferences);
		}
		else
		{
			profileId = StringUtils.EMPTY;
		}

		return profileId;
	}

	/**
	 * Returns sorted affinities.
	 *
	 * @param profileData
	 *           affinities to be sorted.
	 * @param sizeLimit
	 *           limit for returned affinities.
	 * @return String
	 */
	protected List getSortedAffinities(Map<String, Affinity> profileData, int sizeLimit) {
		final List profileDataList = new ArrayList(profileData.entrySet());

		return (List) profileDataList.parallelStream().sorted(new RecentlyViewedComparator()).limit(sizeLimit).collect(Collectors.toList());
	}

	protected UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected SessionService getSessionService() {
		return sessionService;
	}

	@Required
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	protected AsmProfileDataServiceClientAdapter getAsmProfileDataServiceClientAdapter() {
		return asmProfileDataServiceClientAdapter;
	}

	@Required
	public void setAsmProfileDataServiceClientAdapter(AsmProfileDataServiceClientAdapter asmProfileDataServiceClientAdapter) {
		this.asmProfileDataServiceClientAdapter = asmProfileDataServiceClientAdapter;
	}

	protected AsmProfileIdentityServiceClientAdapter getAsmProfileIdentityServiceClientAdapter() {
		return asmProfileIdentityServiceClientAdapter;
	}

	@Required
	public void setAsmProfileIdentityServiceClientAdapter(AsmProfileIdentityServiceClientAdapter asmProfileIdentityServiceClientAdapter) {
		this.asmProfileIdentityServiceClientAdapter = asmProfileIdentityServiceClientAdapter;
	}

	protected Converter<Map.Entry<String, Affinity>, ProductAffinityData> getProductAffinityConverter() {
		return productAffinityConverter;
	}

	@Required
	public void setProductAffinityConverter(Converter<Map.Entry<String, Affinity>, ProductAffinityData> productAffinityConverter) {
		this.productAffinityConverter = productAffinityConverter;
	}

	protected Converter<Map.Entry<String, Affinity>, CategoryAffinityData> getCategoryAffinityConverter() {
		return categoryAffinityConverter;
	}

	@Required
	public void setCategoryAffinityConverter(Converter<Map.Entry<String, Affinity>, CategoryAffinityData> categoryAffinityConverter) {
		this.categoryAffinityConverter = categoryAffinityConverter;
	}

	protected Converter<Map.Entry<String, UserAgent>, TechnologyUsedData> getDeviceAffinityConverter() {
		return deviceAffinityConverter;
	}

	@Required
	public void setDeviceAffinityConverter(Converter<Map.Entry<String, UserAgent>, TechnologyUsedData> deviceAffinityConverter) {
		this.deviceAffinityConverter = deviceAffinityConverter;
	}
}