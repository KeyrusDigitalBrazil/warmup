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
package de.hybris.platform.cmsfacades.users.services.impl;

import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Default implementation of {@link CMSUserService}.
 */
public class DefaultCMSUserService implements CMSUserService
{
	// ------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------
	private UserService userService;
	private StoreSessionFacade storeSessionFacade;

	// ------------------------------------------------------------------
	// Public Methods
	// ------------------------------------------------------------------
	@Override
	public Set<String> getReadableLanguagesForCurrentUser()
	{
		return getReadableLanguagesForUser(userService.getCurrentUser());
	}

	@Override
	public Set<String> getReadableLanguagesForUser(final UserModel userModel)
	{
		return getLanguagesForUser(userModel, UserGroupModel::getReadableLanguages);
	}

	@Override
	public Set<String> getWriteableLanguagesForCurrentUser()
	{
		return getWriteableLanguagesForUser(userService.getCurrentUser());
	}

	@Override
	public Set<String> getWriteableLanguagesForUser(final UserModel userModel)
	{
		return getLanguagesForUser(userModel, UserGroupModel::getWriteableLanguages);
	}

	// ------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------
	/**
	 * This method returns languages available to the provided user. Which types of languages are returned is controlled by the
	 * languageRetrievalFn provided.
	 *
	 * @param userModel
	 * 		- The model that represent the user whose languages to retrieve.
	 * @param languagesRetrievalFn
	 * 		- Function used to retrieve a collection of languages from a user group model.
	 * @return
	 * 		 A set of strings. Each string represents a language ISO code.
	 */
	protected Set<String> getLanguagesForUser(final UserModel userModel, final Function<UserGroupModel, Collection<LanguageModel>> languagesRetrievalFn)
	{
		if (getUserService().isAdmin(userModel))
		{
			return getAllSupportedLanguages();
		}
		else
		{
			return getUserGroupsForUser(userModel).stream()
					.map(languagesRetrievalFn)
					.flatMap(Collection::stream)
					.map(LanguageModel::getIsocode)
					.collect(Collectors.toSet());
		}
	}

	/**
	 * This method returns all the supported languages in the site.
	 * @return
	 * 		 a set of strings. Each string represents a language ISO code.
	 */
	protected Set<String> getAllSupportedLanguages()
	{
		return getStoreSessionFacade().getAllLanguages().stream()
				.map(LanguageData::getIsocode)
				.collect(Collectors.toSet());
	}


	/**
	 * This method returns a set of user groups the provided user belongs to.
	 * @param userModel
	 * 		- The model representing the user whose user groups to retrieve.
	 * @return
	 * 		  a set of {@link UserGroupModel} instances.
	 */
	protected Set<UserGroupModel> getUserGroupsForUser(final UserModel userModel)
	{
		return userModel.getAllGroups().stream()
				.filter(UserGroupModel.class::isInstance)
				.map(UserGroupModel.class::cast)
				.collect(Collectors.toSet());
	}

	// ------------------------------------------------------------------
	// Getters/Setters
	// ------------------------------------------------------------------
	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}
}
