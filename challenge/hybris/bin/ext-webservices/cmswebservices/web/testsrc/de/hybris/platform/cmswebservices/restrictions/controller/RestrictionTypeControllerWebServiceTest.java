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
package de.hybris.platform.cmswebservices.restrictions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.RestrictionTypeData;
import de.hybris.platform.cmswebservices.data.RestrictionTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class RestrictionTypeControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String[] CMS2_RESTRICTION_TYPES =
	{ //
			CMSTimeRestrictionModel._TYPECODE, CMSUserRestrictionModel._TYPECODE, CMSUserGroupRestrictionModel._TYPECODE,
			CMSCatalogRestrictionModel._TYPECODE, CMSInverseRestrictionModel._TYPECODE, CMSCategoryRestrictionModel._TYPECODE,
			CMSProductRestrictionModel._TYPECODE //
	};

	private static final String URI = "/v1/restrictiontypes";

	@Test
	public void shouldGetAllRestrictionTypes() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// Assert correct status code
		assertResponse(Status.OK, response);

		// Assert that all cms2 restriction types are coming out of the API.
		final RestrictionTypeListData entity = response.readEntity(RestrictionTypeListData.class);
		final Collection<String> restrictionTypeCodes = entity.getRestrictionTypes().stream() //
				.map(RestrictionTypeData::getCode) //
				.collect(Collectors.toList());
		assertThat(restrictionTypeCodes, hasItems(CMS2_RESTRICTION_TYPES));
	}

}
