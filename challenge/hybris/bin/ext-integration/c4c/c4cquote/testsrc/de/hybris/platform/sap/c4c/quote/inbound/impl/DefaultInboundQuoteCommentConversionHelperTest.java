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
package de.hybris.platform.sap.c4c.quote.inbound.impl;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * JUnit for @DefaultInboundQuoteCommentConversionHelper
 */
@UnitTest
public class DefaultInboundQuoteCommentConversionHelperTest {
	@InjectMocks
	private final DefaultInboundQuoteCommentConversionHelper helper = new DefaultInboundQuoteCommentConversionHelper();

	@Mock
	private ModelService modelService;
	@Mock
	private CommentService commentService;
	@Mock
	private QuoteService quoteService;
	@Mock
	private UserService userService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private QuoteModel quote;
	@Mock
	private QuoteEntryModel quoteEntry;
	@Mock
	private UserModel userModel;
	@Mock
	private DomainModel domainModel;
	@Mock
	private ComponentModel componentModel;
	@Mock
	private CommentTypeModel commentTypeModel;
	@Mock
	private SAPConfigurationModel sapConfigurationModel;
	@Mock
	private CommentModel comment;
	@Mock
	private CommentModel recentComment;

	private static final String QUOTE_ID = "31222";
	private static final String TEXT = "abc";
	private static final String USER_ID = "51087";
	private static final String ENTRY_NUM = "1";
	private static final String QUOTE_DOMAIN = "quoteDomain";
	private static final String QUOTE_COMPONENT = "quoteComponent";
	private static final String COMMENT_CODE = "commentCode";
	private static final String USER = "testemail.test.com";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Registry.setCurrentTenantByID("junit");
	}

	@Test
	public void shouldCreateNonEmptyHeaderComments() {
		mockCommons();
		mockCommonsForHeaderAndItem();
		List<CommentModel> comments = Arrays.asList(comment);
		//when(quote.getUser()).thenReturn(user);
		when(userService.getUserForUID(USER)).thenReturn(userModel);
		when(quote.getComments()).thenReturn(comments);
		String resultComments = helper.createHeaderComment(QUOTE_ID, TEXT,USER);
		Assert.assertTrue(!resultComments.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidText() {
		mockCommons();
		when(userService.getUserForUID(USER)).thenReturn(userModel);
		helper.createHeaderComment(QUOTE_ID, null, USER);
	}

	private void mockCommons() {
		when(quoteService.getCurrentQuoteForCode(QUOTE_ID)).thenReturn(quote);
		when(userService.getUserForUID(USER_ID)).thenReturn(userModel);
		when(quote.getStore()).thenReturn(baseStore);
		when(quote.getVersion()).thenReturn(1);
	}

	private void mockCommonsForHeaderAndItem() {
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(commentService.getDomainForCode(QUOTE_DOMAIN)).thenReturn(domainModel);
		when(commentService.getComponentForCode(domainModel, QUOTE_COMPONENT)).thenReturn(componentModel);
		when(recentComment.getCode()).thenReturn(COMMENT_CODE);
		when(modelService.create(CommentModel.class)).thenReturn(recentComment);
		doNothing().when(modelService).save(recentComment);
		doNothing().when(modelService).save(any(ItemModel.class));
	}

	private void mockCommonsForItem() {
		when(quote.getUser()).thenReturn(userModel);
		when(quote.getEntries()).thenReturn(Arrays.asList(quoteEntry));
		when(quoteEntry.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUM));
	}
}
