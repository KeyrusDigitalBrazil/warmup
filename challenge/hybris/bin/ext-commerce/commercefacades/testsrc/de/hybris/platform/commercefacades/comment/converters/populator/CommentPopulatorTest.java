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
package de.hybris.platform.commercefacades.comment.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommentPopulatorTest
{

	private static final String COMMENT_USER = "commentUser";
	private static final Date COMMENT_CREATION_DATE = new Date();
	private static final String COMMENT_TEXT = "testCommentText";

	private CommentPopulator commentPopulator;
	private CommentModel source;
	private CommentData target;


	@Mock
	private Converter<UserModel, PrincipalData> principalConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		source = mock(CommentModel.class);
		target = new CommentData();

		commentPopulator = new CommentPopulator();

		commentPopulator.setPrincipalConverter(principalConverter);
	}

	@Test
	public void shouldPopulate()
	{
		given(source.getText()).willReturn(COMMENT_TEXT);
		given(source.getCreationtime()).willReturn(COMMENT_CREATION_DATE);

		final CustomerModel customerModel = mock(CustomerModel.class);
		final PrincipalData customerData = new PrincipalData();
		customerData.setUid(COMMENT_USER);

		given(principalConverter.convert(customerModel)).willReturn(customerData);
		given(source.getAuthor()).willReturn(customerModel);

		commentPopulator.populate(source, target);

		Assert.assertEquals("source and target text should match", source.getText(), target.getText());
		Assert.assertEquals("source and target creationTime should match", source.getCreationtime(), target.getCreationDate());
		final PrincipalData author = target.getAuthor();
		Assert.assertNotNull("target author should not be null", author);
		Assert.assertEquals(author.getUid(), COMMENT_USER);
		Assert.assertTrue("target fromCustomer should be true", BooleanUtils.toBoolean(target.getFromCustomer()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfPopulateIsCalledWithNullSource()
	{
		commentPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfPopulateIsCalledWithNullTarget()
	{
		commentPopulator.populate(source, null);
	}

}
