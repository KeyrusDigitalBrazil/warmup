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
package de.hybris.platform.b2b.services.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultB2BCommentServiceMockTest extends HybrisMokitoTest
{
	private final DefaultB2BCommentService defaultB2BCommentService = new DefaultB2BCommentService();
	@Mock
	private ModelService mockModelService;

	@Test
	public void testAddComment() throws Exception
	{
		final AbstractOrderModel order = mock(AbstractOrderModel.class);
		final B2BCommentModel comment = mock(B2BCommentModel.class);

		defaultB2BCommentService.setModelService(mockModelService);
		defaultB2BCommentService.addComment(order, comment);

		verify(order, Mockito.times(1)).setB2bcomments(any(Collection.class));
		verify(mockModelService, Mockito.times(1)).save(order);
	}
}
