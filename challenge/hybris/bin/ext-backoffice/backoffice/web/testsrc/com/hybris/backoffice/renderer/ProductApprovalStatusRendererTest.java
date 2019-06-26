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
package com.hybris.backoffice.renderer;

import static com.hybris.backoffice.renderer.ProductApprovalStatusRenderer.SCLASS_YW_IMAGE_ATTRIBUTE_SYNC_STATUS_NO_READ;
import static com.hybris.backoffice.renderer.ProductApprovalStatusRenderer.YW_IMAGE_ATTRIBUTE_APPROVAL_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ProductApprovalStatusRendererTest
{

	protected Component parent = new Div();

	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private LabelService labelService;
	@InjectMocks
	private ProductApprovalStatusRenderer renderer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();

		doAnswer(inv -> StringUtils.EMPTY).when(labelService).getObjectLabel(any());
	}

	@Test
	public void shouldNoAccessIconBeDisplayedWhenUserHasNoPermissions()
	{
		// given
		final ProductModel data = mock(ProductModel.class);
		given(data.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(permissionFacade.canReadInstanceProperty(data, ProductModel.APPROVALSTATUS)).willReturn(false);

		// when
		renderer.render(parent, null, data, null, null);

		// then
		final List<Component> components = Selectors.find(parent, "." + SCLASS_YW_IMAGE_ATTRIBUTE_SYNC_STATUS_NO_READ);
		assertThat(components).hasSize(1);
	}

	@Test
	public void shouldNoAccessIconNotBeDisplayedWhenUserHasPermissions()
	{
		// given
		final ProductModel data = mock(ProductModel.class);
		given(data.getApprovalStatus()).willReturn(ArticleApprovalStatus.APPROVED);
		given(permissionFacade.canReadInstanceProperty(data, ProductModel.APPROVALSTATUS)).willReturn(true);

		// when
		renderer.render(parent, null, data, null, null);

		// then
		final List<Component> components = Selectors.find(parent, "." + YW_IMAGE_ATTRIBUTE_APPROVAL_STATUS + "approved");
		assertThat(components).hasSize(1);
	}

}
