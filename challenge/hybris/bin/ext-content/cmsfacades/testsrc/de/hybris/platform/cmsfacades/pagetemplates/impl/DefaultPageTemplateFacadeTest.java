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
package de.hybris.platform.cmsfacades.pagetemplates.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.PageTemplateDTO;
import de.hybris.platform.cmsfacades.data.PageTemplateData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageTemplateFacadeTest
{
	private static final String PAGE_TYPE_CODE = "pageTypeCode";
	private static final String INVALID = "invalid";

	@Spy
	@InjectMocks
	private DefaultPageTemplateFacade pageTemplateFacade;

	@Mock
	private CMSAdminPageService cmsAdminPageService;
	@Mock
	private Converter<PageTemplateModel, PageTemplateData> pageTemplateModelConverter;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Mock
	private CMSPageTypeModel pageType;

	@Mock
	private PageTemplateModel model1;
	@Mock
	private PageTemplateModel model2;
	@Mock
	private PageTemplateData data1;
	@Mock
	private PageTemplateData data2;
	@Mock
	private MediaModel iconMedia;

	@Mock
	private SessionSearchRestrictionsDisabler searchRestrictionsDisabler;

	@Before
	public void setup()
	{
		when(iconMedia.getURL()).thenReturn("previewIconRUL");
		when(model1.getUid()).thenReturn("theUid");
		when(model1.getFrontendTemplateName()).thenReturn("theName");
		when(model1.getPreviewIcon()).thenReturn(iconMedia);

		when(permissionCRUDService.canReadType(PAGE_TYPE_CODE)).thenReturn(true);
		when(permissionCRUDService.canReadType(INVALID)).thenReturn(true);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier supplier = (Supplier) args[0];
			return supplier.get();
		}).when(searchRestrictionsDisabler).execute(any());
	}


	@Test
	public void willRetrieveActivetemplatesForTheGivenPageType()
	{
		final Boolean active = true;

		final PageTemplateDTO pageTemplateDto = new PageTemplateDTO();
		pageTemplateDto.setPageTypeCode(PAGE_TYPE_CODE);
		pageTemplateDto.setActive(active);

		when(cmsAdminPageService.getPageTypeByCode(PAGE_TYPE_CODE)).thenReturn(Optional.of(pageType));
		when(cmsAdminPageService.getAllRestrictedPageTemplates(active, pageType)).thenReturn(asList(model1));
		when(pageTemplateModelConverter.convert(model1)).thenReturn(data1);

		final List<PageTemplateData> result = pageTemplateFacade.findPageTemplates(pageTemplateDto);

		assertThat(result.size(), is(equalTo(1)));
		assertThat(result, contains(data1));

		verify(cmsAdminPageService, never()).getAllRestrictedPageTemplates(false, pageType);

	}

	@Test
	public void willRetrieveNonActivetemplatesForTheGivenPageType()
	{
		final Boolean active = false;

		final PageTemplateDTO pageTemplateDto = new PageTemplateDTO();
		pageTemplateDto.setPageTypeCode(PAGE_TYPE_CODE);
		pageTemplateDto.setActive(active);

		when(cmsAdminPageService.getPageTypeByCode(PAGE_TYPE_CODE)).thenReturn(Optional.of(pageType));
		when(cmsAdminPageService.getAllRestrictedPageTemplates(active, pageType)).thenReturn(asList(model1));
		when(pageTemplateModelConverter.convert(model1)).thenReturn(data1);

		final List<PageTemplateData> result = pageTemplateFacade.findPageTemplates(pageTemplateDto);

		assertThat(result.size(), is(equalTo(1)));
		assertThat(result, contains(data1));

		verify(cmsAdminPageService, never()).getAllRestrictedPageTemplates(true, pageType);

	}

	@Test
	public void willRetrieveAlltemplatesForTheGivenPageType()
	{
		final PageTemplateDTO pageTemplateDto = new PageTemplateDTO();
		pageTemplateDto.setPageTypeCode(PAGE_TYPE_CODE);

		when(cmsAdminPageService.getPageTypeByCode(PAGE_TYPE_CODE)).thenReturn(Optional.of(pageType));
		when(cmsAdminPageService.getAllRestrictedPageTemplates(true, pageType)).thenReturn(asList(model1));
		when(cmsAdminPageService.getAllRestrictedPageTemplates(false, pageType)).thenReturn(asList(model2));
		when(pageTemplateModelConverter.convert(model1)).thenReturn(data1);
		when(pageTemplateModelConverter.convert(model2)).thenReturn(data2);

		final List<PageTemplateData> result = pageTemplateFacade.findPageTemplates(pageTemplateDto);

		assertThat(result.size(), is(equalTo(2)));
		assertThat(result, contains(data1, data2));

	}

	@Test
	public void willRetrieveNoTemplateForTheInvalidPageType()
	{
		final PageTemplateDTO pageTemplateDto = new PageTemplateDTO();
		pageTemplateDto.setPageTypeCode(INVALID);

		when(cmsAdminPageService.getPageTypeByCode(INVALID)).thenReturn(Optional.ofNullable(null));

		final List<PageTemplateData> result = pageTemplateFacade.findPageTemplates(pageTemplateDto);

		assertThat(result, empty());
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailFindByIdInsufficientTypePermission()
	{
		final PageTemplateDTO pageTemplateDto = new PageTemplateDTO();
		pageTemplateDto.setPageTypeCode(PAGE_TYPE_CODE);

		when(cmsAdminPageService.getPageTypeByCode(PAGE_TYPE_CODE)).thenReturn(Optional.of(pageType));
		doThrow(new TypePermissionException("Failure!")).when(pageTemplateFacade).createTypePermissionException(any(), any());

		when(permissionCRUDService.canReadType(anyString())).thenReturn(Boolean.FALSE);

		pageTemplateFacade.findPageTemplates(pageTemplateDto);
	}
}

