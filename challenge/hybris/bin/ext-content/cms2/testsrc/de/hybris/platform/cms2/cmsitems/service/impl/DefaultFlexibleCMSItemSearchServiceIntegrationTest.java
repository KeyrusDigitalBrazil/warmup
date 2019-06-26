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
package de.hybris.platform.cms2.cmsitems.service.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultFlexibleCMSItemSearchServiceIntegrationTest extends ServicelayerTest
{
	private static final String EXCLUDED_TYPES = "excludedTypes";
	private static final String EXCLUDED_TYPES_QUERY_STRING = " AND {type.code} NOT IN (?" + EXCLUDED_TYPES + ")";

	@Resource
	private DefaultFlexibleCMSItemSearchService cmsItemSearchService;

	@Resource
	private TypeService typeService;

	@Resource
	private UserService userService;

	@Test
	public void shouldAppendTypeExclusionsForBlacklistedTypes()
	{
		final ComposedTypeModel type = typeService.getComposedTypeForCode(AbstractPageModel._TYPECODE);

		final StringBuilder queryBuilder = new StringBuilder();
		final HashMap<String, Object> queryParameters = new HashMap<>();

		cmsItemSearchService.appendTypeExclusions(Arrays.asList(type), queryBuilder, queryParameters);

		assertThat(queryBuilder.toString(), equalTo(EXCLUDED_TYPES_QUERY_STRING));
		assertThat((Set<String>) queryParameters.get(EXCLUDED_TYPES), hasSize(greaterThan(0)));
	}

	@Test
	public void shouldAppendTypeExclusionsForNoReadTypePermissions() throws ImpExException
	{
		// Create user and type permissions
		super.importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);

		final ComposedTypeModel abstractPageType = typeService.getComposedTypeForCode(AbstractPageModel._TYPECODE);
		final StringBuilder queryBuilder = new StringBuilder();
		final HashMap<String, Object> queryParameters = new HashMap<>();

		cmsItemSearchService.appendTypeExclusions(Arrays.asList(abstractPageType), queryBuilder, queryParameters);

		assertThat(queryBuilder.toString(), equalTo(EXCLUDED_TYPES_QUERY_STRING));
		assertThat((Set<String>) queryParameters.get(EXCLUDED_TYPES), hasItem(equalTo(ProductPageModel._TYPECODE)));
	}

}
