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

package de.hybris.platform.chineseprofileservices.security;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.util.Config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Implementation for {@link UserDetailsService}. Delivers main functionality for chinese user details.
 */
public class ChineseUserDetailsService implements UserDetailsService
{

	private String rolePrefix = "ROLE_";

	private UserDao userDao;

	private CommonI18NService commonI18NService;

	private ModelService modelService;

	@Override
	public CoreUserDetails loadUserByUsername(final String username)
	{
		if (username == null)
		{
			return null;
		}

		UserModel user;
		try
		{
			user = userDao.findUserByUID(username);
		}
		catch (final ModelNotFoundException e)
		{
			throw new UsernameNotFoundException("User not found!");
		}
		if (Objects.isNull(user))
		{
			throw new UsernameNotFoundException("User not found!");
		}

		final User sourceUser = modelService.getSource(user);
		final boolean enabled = isNotAnonymousOrAnonymousLoginIsAllowed(sourceUser) && !sourceUser.isLoginDisabledAsPrimitive()
				&& !this.isAccountDeactivated(sourceUser);

		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		String password = sourceUser.getEncodedPassword(JaloSession.getCurrentSession().getSessionContext());

		// a null value has to be transformed to empty string, otherwise the constructor
		// of org.springframework.security.userdetails.User will fail
		if (password == null)
		{
			password = StringUtils.EMPTY;
		}

		return new CoreUserDetails(user.getUid(), password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked,
				getAuthorities(sourceUser), getCommonI18NService().getCurrentLanguage().getIsocode());
	}

	protected boolean isAccountDeactivated(final User user)
	{
		return user.getDeactivationDate() != null && user.getDeactivationDate().toInstant().isBefore(Instant.now());
	}

	protected boolean isNotAnonymousOrAnonymousLoginIsAllowed(final User user)
	{
		return !user.isAnonymousCustomer() || !Config.getBoolean(Customer.LOGIN_ANONYMOUS_ALWAYS_DISABLED, true);//NOSONAR
	}

	protected Collection<GrantedAuthority> getAuthorities(final User user)
	{
		final Set<PrincipalGroup> groups = user.getGroups();
		final Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(groups.size());
		final Iterator<PrincipalGroup> itr = groups.iterator();
		while (itr.hasNext())
		{
			final PrincipalGroup group = itr.next();
			authorities.add(new SimpleGrantedAuthority(rolePrefix + group.getUID().toUpperCase()));//NOSONAR
			for (final PrincipalGroup gr : group.getAllGroups())
			{
				authorities.add(new SimpleGrantedAuthority(rolePrefix + gr.getUID().toUpperCase()));//NOSONAR
			}

		}
		return authorities;
	}

	@Required
	public void setUserDao(final UserDao userDao)
	{
		this.userDao = userDao;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public void setRolePrefix(final String rolePrefix)
	{
		this.rolePrefix = rolePrefix;
	}

	public UserDao getUserDao()
	{
		return userDao;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
