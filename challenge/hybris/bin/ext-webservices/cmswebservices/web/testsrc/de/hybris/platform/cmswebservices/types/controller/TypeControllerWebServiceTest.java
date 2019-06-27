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
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2lib.enums.CarouselScroll;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class TypeControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String URI = "/v1/types";

	@Resource
	private ModelService modelService;

	@Test
	public void getAllTypesTest() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//check that we have a result
		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		// check that we have a body
		assertNotNull(entity);

		//check that we have a couple of entries
		assertTrue(entity.getComponentTypes().size() > 1);

		final List<ComponentTypeData> components = entity.getComponentTypes();
		ComponentTypeData paragraphComponent = new ComponentTypeData();

		for (final ComponentTypeData component : components)
		{
			if (component.getCode().equals(CMSParagraphComponentModel._TYPECODE))
			{
				paragraphComponent = component;
			}
		}

		// check that it contains the paragraph component
		assertNotNull(paragraphComponent);
		assertThat(paragraphComponent.getI18nKey(), equalTo("type.cmsparagraphcomponent.name"));
	}

	@Test
	public void getAllTypesByCategoryTest() throws Exception
	{
		final String componentCategory = StructureTypeCategory.COMPONENT.name();
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam("category", componentCategory) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//check that we have a result
		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		// check that we have a body
		assertNotNull(entity);

		//check that we have a couple of entries
		assertTrue(entity.getComponentTypes().size() > 1);

		final List<ComponentTypeData> components = entity.getComponentTypes();

		components.stream().forEach(componentTypeData -> assertThat(componentTypeData.getCategory(), is(componentCategory)));
	}

	@Test
	public void shouldGetFiedlForEnumPopulatedWithValuesForProductCarouselComponent() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI).path(ProductCarouselComponentModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeData component = response.readEntity(ComponentTypeData.class);
		assertThat(component.getCode(), equalTo(ProductCarouselComponentModel._TYPECODE));
		assertThat(component.getI18nKey(), equalTo("type.productcarouselcomponent.name"));

		final ComponentTypeAttributeData scroll = getAttribute(component.getAttributes(), "scroll");
		assertThat(scroll.getCmsStructureType(), equalTo("EditableDropdown"));
		assertThat(scroll.getQualifier(), equalTo("scroll"));

		assertThat(scroll.getOptions().size(), equalTo(CarouselScroll.values().length));
		getScrollOptions().forEach(option -> {
			final OptionData returnedOption = getOption(scroll.getOptions(), option.getId());
			assertThat(returnedOption.getLabel(), equalTo(option.getLabel()));
		});

		assertThat(scroll.isPaged(), equalTo(false));
		assertThat(scroll.getIdAttribute(), equalTo("value"));
		assertThat(scroll.getLabelAttributes(), contains("label"));
		assertThat(scroll.getI18nKey(), equalTo("type.productcarouselcomponent.scroll.name"));
	}

	protected List<OptionData> getScrollOptions()
	{
		final List<OptionData> options = asList(CarouselScroll.values()).stream().map(e -> {
			final OptionData optionData = new OptionData();
			optionData.setId(e.name());
			optionData.setLabel("type.carouselscroll." + e.getCode().toLowerCase() + ".name");
			return optionData;
		}).collect(toList());
		return options;
	}

	protected void createMediaFormat(final String qualifier)
	{
		final MediaFormatModel mediaFormat = modelService.create(MediaFormatModel.class);
		mediaFormat.setQualifier(qualifier);
		modelService.save(mediaFormat);
	}

	protected ComponentTypeAttributeData getAttribute(final List<ComponentTypeAttributeData> attributes, final String qualifier)
	{
		return attributes.stream() //
				.filter(attribute -> qualifier.equals(attribute.getQualifier())) //
				.findFirst() //
				.orElseThrow(
						() -> new IllegalArgumentException("No attribute with qualifier [" + qualifier + "] in list of attributes."));
	}

	protected OptionData getOption(final List<OptionData> options, final String id)
	{
		return options.stream() //
				.filter(option -> id.equals(option.getId())) //
				.findFirst() //
				.orElseThrow(() -> new IllegalArgumentException("No option with id [" + id + "] in list of options."));
	}
}
