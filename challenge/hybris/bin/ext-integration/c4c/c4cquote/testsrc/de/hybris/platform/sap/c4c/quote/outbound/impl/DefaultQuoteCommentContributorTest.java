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
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;


@UnitTest
public class DefaultQuoteCommentContributorTest
{
	@InjectMocks
	private DefaultQuoteCommentContributor commentContributor = new DefaultQuoteCommentContributor();
	@Mock
	private QuoteService quoteService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateRows()
	{
		QuoteModel latestQuote = Mockito.mock(QuoteModel.class);
		QuoteModel outdatedQuote = Mockito.mock(QuoteModel.class);
		
		when(latestQuote.getCode()).thenReturn("123456");
		when(latestQuote.getVersion()).thenReturn(4);
		List<CommentModel> latestComments = populateComments(5);
		when(latestQuote.getComments()).thenReturn(latestComments);
		
		List<CommentModel> outdatedComments = new ArrayList<CommentModel>(latestComments);
		outdatedComments.remove(4);
		outdatedComments.remove(3);
		
		when(outdatedQuote.getComments()).thenReturn(outdatedComments);
		when(quoteService.getQuoteForCodeAndVersion(Mockito.anyString(), Mockito.anyInt())).thenReturn(outdatedQuote);

		List<Map<String, Object>> rows = commentContributor.createRows(latestQuote);

		Assert.assertNotNull(rows);
		Assert.assertEquals(2, rows.size());

	}

	@Test
	public void testCreateRowsWithoutComments()
	{
		QuoteModel quote = Mockito.mock(QuoteModel.class);
		when(quote.getComments()).thenReturn(new ArrayList<CommentModel>());
		when(quote.getVersion()).thenReturn(3);

		List<Map<String, Object>> rows = commentContributor.createRows(quote);

		Assert.assertNotNull(rows);
		Assert.assertEquals(0, rows.size());

	}

	protected List<CommentModel> populateComments(final int size)
	{
		List<CommentModel> comments = new ArrayList<>();
		B2BCustomerModel author = new B2BCustomerModel();
		for (int i = 0; i < size; i++)
		{
			CommentModel comment = new CommentModel();
			comment.setText("Comment " + i);
			comment.setAuthor(author);
			comments.add(comment);
		}
		return comments;
	}

}
