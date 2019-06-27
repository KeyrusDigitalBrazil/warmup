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
package de.hybris.platform.cmswebservices.types.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class TypeStructureModeControllerWebServiceTest extends ApiBaseIntegrationTest
{
	// Time Restriction
	private static final String NAME = "name";
	private static final String ACTIVE_FROM = "activeFrom";
	private static final String ACTIVE_UNTIL = "activeUntil";

	// Cms Link Component
	private static final String LINK_NAME = "linkName";
	private static final String LINK_TO = "linkTo";
	private static final String PRODUCT = "product";
	private static final String PRODUCT_CMS_STRUCTURE_TYPE = "SingleOnlineProductSelector";
	private static final String CATEGORY = "category";
	private static final String CATEGORY_CMS_STRUCTURE_TYPE = "SingleOnlineCategorySelector";
	private static final String CMS_LINK_TO_OPTION_PREFIX = "se.cms.linkto.option.";
	private static final String CMS_LINK_CONTENT_OPTION = "content";
	private static final String CMS_LINK_PRODUCT_OPTION = "product";
	private static final String CMS_LINK_CATEGORY_OPTION = "category";
	private static final String CMS_LINK_EXTERNAL_OPTION = "external";

	private static final String MODE = "mode";
	private static final String CODE = "code";
	private static final String URI = "/v1/types";

	@Test
	public void shouldFindTimeRestrictionStructureForAddModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.ADD.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		assertTrue(entity.getComponentTypes().get(0).getAttributes().size() > 1);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();

		final ComponentTypeAttributeData name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(false));

		final ComponentTypeAttributeData activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(false));

		final ComponentTypeAttributeData activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(false));
	}

	@Test
	public void shouldFindTimeRestrictionStructureForDefaultModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.DEFAULT.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		assertTrue(entity.getComponentTypes().get(0).getAttributes().size() > 1);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();

		final ComponentTypeAttributeData name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(true));

		final ComponentTypeAttributeData activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(true));

		final ComponentTypeAttributeData activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(true));
	}

	@Test
	public void shouldFindTimeRestrictionStructureForCreateModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.CREATE.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		assertTrue(entity.getComponentTypes().get(0).getAttributes().size() > 1);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();

		final ComponentTypeAttributeData name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(true));

		final ComponentTypeAttributeData activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(true));

		final ComponentTypeAttributeData activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(true));
	}

	@Test
	public void shouldFindCmsLinkComponentStructureAndLinkToOptionsAreAddedCorrectly() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSLinkComponentModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.DEFAULT.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();
		assertTrue(attributes.size() > 1);

		final ComponentTypeAttributeData name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(true));
		assertThat(name.isRequired(), is(true));
		
		final ComponentTypeAttributeData linkName = attributes.get(3);
		assertThat(linkName.getQualifier(), equalTo(LINK_NAME));
		assertThat(linkName.isEditable(), is(true));
		assertThat(linkName.isRequired(), is(true));

		final ComponentTypeAttributeData linkTo = attributes.get(4);
		assertThat(linkTo.getQualifier(), equalTo(LINK_TO));
		final List<OptionData> linkToOptions = linkTo.getOptions();
		assertTrue(linkToOptions.size() == 4);

		assertTrue(linkToOptions.get(0).getLabel().equals(CMS_LINK_TO_OPTION_PREFIX + CMS_LINK_CONTENT_OPTION));
		assertTrue(linkToOptions.get(0).getId().equals(CMS_LINK_CONTENT_OPTION));

		assertTrue(linkToOptions.get(1).getLabel().equals(CMS_LINK_TO_OPTION_PREFIX + CMS_LINK_PRODUCT_OPTION));
		assertTrue(linkToOptions.get(1).getId().equals(CMS_LINK_PRODUCT_OPTION));

		assertTrue(linkToOptions.get(2).getLabel().equals(CMS_LINK_TO_OPTION_PREFIX + CMS_LINK_CATEGORY_OPTION));
		assertTrue(linkToOptions.get(2).getId().equals(CMS_LINK_CATEGORY_OPTION));

		assertTrue(linkToOptions.get(3).getLabel().equals(CMS_LINK_TO_OPTION_PREFIX + CMS_LINK_EXTERNAL_OPTION));
		assertTrue(linkToOptions.get(3).getId().equals(CMS_LINK_EXTERNAL_OPTION));
	}

	@Test
	public void shouldFindCmsLinkComponentStructureAndProductAreAddedCorrectly() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSLinkComponentModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.PRODUCT.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();
		assertTrue(attributes.size() > 2);

		final ComponentTypeAttributeData product = attributes.get(5);
		assertThat(product.getCmsStructureType(), equalTo(PRODUCT_CMS_STRUCTURE_TYPE));
		assertThat(product.getQualifier(), equalTo(PRODUCT));
		assertThat(product.isEditable(), is(true));
		assertThat(product.isPaged(), is(true));
	}

	@Test
	public void shouldFindCmsLinkComponentStructureAndCategoryAreAddedCorrectly() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, CMSLinkComponentModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.CATEGORY.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertNotNull(entity);

		final List<ComponentTypeAttributeData> attributes = entity.getComponentTypes().get(0).getAttributes();
		assertTrue(attributes.size() > 2);

		final ComponentTypeAttributeData category = attributes.get(5);
		assertThat(category.getCmsStructureType(), equalTo(CATEGORY_CMS_STRUCTURE_TYPE));
		assertThat(category.getQualifier(), equalTo(CATEGORY));
		assertThat(category.isEditable(), is(true));
		assertThat(category.isPaged(), is(true));
	}

	/**
	 * A custom page structure type structure is defined in the registry
	 * (cmssmarteditwebservices-structuretype-spring.xml) which overrides the structure provided by the base structure
	 * (cmsfacades). This test validates that the resulted page structure is correct.
	 */
	@Test
	public void shouldFindContentPageStructureForDefaultMode()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam(CODE, ContentPageModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.DEFAULT.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		assertThat(entity, notNullValue());

		final ComponentTypeData componentTypeData = entity.getComponentTypes().get(0);
		assertThat(componentTypeData.getCategory(), equalTo(StructureTypeCategory.PAGE.name()));
		assertThat(componentTypeData.getCode(), equalTo(ContentPageModel._TYPECODE));
		assertThat(componentTypeData.getAttributes().isEmpty(), is(false));
	}

}
