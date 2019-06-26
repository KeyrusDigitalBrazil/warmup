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
package de.hybris.platform.commerceservices.comments.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentDao;
import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests for {@link DefaultCommerceCommentService}.
 */
@UnitTest
public class DefaultCommerceCommentServiceTest
{

	private static final String TEST_CODE = "testCode";

	@InjectMocks
	private final DefaultCommerceCommentService service = new DefaultCommerceCommentService();

	@Mock
	private ModelService modelService;
	@Mock
	private CommentDao commentDao;
	@Mock
	private KeyGenerator commentsKeyGenerator;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAddComments()
	{
		final CommerceCommentParameter parameter = buildParameter("text", "domain", "component", "commentType");
		final List<DomainModel> domainList = listStub(DomainModel.class, "domain");
		final List<ComponentModel> componentList = listStub(ComponentModel.class, "component", "component2");
		final List<CommentTypeModel> typeList = listStub(CommentTypeModel.class, "commentType", "commentType2");
		setDomain(domainList.get(0), componentList, typeList);

		Mockito.when(commentDao.findDomainsByCode(parameter.getDomainCode())).thenReturn(domainList);
		Mockito.when(commentDao.findComponentsByDomainAndCode(domainList.get(0), parameter.getComponentCode()))
				.thenReturn(componentList);
		Mockito.when(modelService.create(CommentModel.class)).thenReturn(new CommentModel());
		Mockito.when(commentsKeyGenerator.generate()).thenReturn(TEST_CODE);

		service.addComment(parameter);

		final List<CommentModel> comments = parameter.getItem().getComments();
		Assert.assertEquals(1, comments.size());
		final CommentModel comment = comments.get(0);
		Assert.assertEquals(TEST_CODE, comment.getCode());
		Assert.assertEquals(parameter.getAuthor(), comment.getAuthor());
		Assert.assertEquals(parameter.getText(), comment.getText());
		Assert.assertEquals(parameter.getComponentCode(), comment.getComponent().getCode());
		Assert.assertEquals(parameter.getDomainCode(), comment.getComponent().getDomain().getCode());
		Assert.assertEquals(parameter.getCommentTypeCode(), comment.getCommentType().getCode());
	}

	@Test
	public void shouldNotOverrideComments()
	{
		final String[] texts =
		{ "text", "text2" };
		final ItemModel item = new ItemModel();
		final UserModel author = new UserModel();

		final CommerceCommentParameter parameter = buildParameter(item, author, texts[0], "domain", "component", "commentType");
		final CommerceCommentParameter parameter2 = buildParameter(item, author, texts[1], "domain", "component", "commentType");
		final List<DomainModel> domainList = listStub(DomainModel.class, "domain");
		final List<ComponentModel> componentList = listStub(ComponentModel.class, "component", "component2");
		final List<CommentTypeModel> typeList = listStub(CommentTypeModel.class, "commentType", "commentType2");
		setDomain(domainList.get(0), componentList, typeList);

		Mockito.when(commentDao.findDomainsByCode(parameter.getDomainCode())).thenReturn(domainList);
		Mockito.when(commentDao.findComponentsByDomainAndCode(domainList.get(0), parameter.getComponentCode()))
				.thenReturn(componentList);
		Mockito.when(modelService.create(CommentModel.class)).thenReturn(new CommentModel());

		service.addComment(parameter);
		service.addComment(parameter2);

		final List<CommentModel> comments = parameter.getItem().getComments();
		Assert.assertEquals(texts.length, comments.size());
		// check if all texts are contained in the comments
		for (final CommentModel comment : comments)
		{
			Assert.assertTrue(Arrays.asList(texts).contains(comment.getText()));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidParameter()
	{
		service.addComment(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidItem()
	{
		final CommerceCommentParameter parameter = buildParameter("text", "domain", "component", "commentType");
		parameter.setItem(null);
		service.addComment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidText()
	{
		final CommerceCommentParameter parameter = buildParameter(null, "domain", "component", "commentType");
		service.addComment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidAuthor()
	{
		final CommerceCommentParameter parameter = buildParameter(new ItemModel(), null, "text", "domain", "component",
				"commentType");
		service.addComment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidDomain()
	{
		final CommerceCommentParameter parameter = buildParameter("text", null, "component", "commentType");
		service.addComment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidComponent()
	{
		final CommerceCommentParameter parameter = buildParameter("text", "domain", null, "commentType");
		service.addComment(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveValidCommentType()
	{
		final CommerceCommentParameter parameter = buildParameter("text", "domain", "component", null);
		service.addComment(parameter);
	}


	private CommerceCommentParameter buildParameter(final String text, final String domain, final String component,
			final String commentType)
	{
		return buildParameter(new ItemModel(), new UserModel(), text, domain, component, commentType);
	}

	private CommerceCommentParameter buildParameter(final ItemModel item, final UserModel author, final String text,
			final String domain, final String component, final String commentType)

	{
		final CommerceCommentParameter parameter = new CommerceCommentParameter();
		parameter.setItem(item);
		parameter.getItem().setComments(new ArrayList<CommentModel>());
		parameter.setAuthor(author);
		parameter.setText(text);
		parameter.setDomainCode(domain);
		parameter.setComponentCode(component);
		parameter.setCommentTypeCode(commentType);

		return parameter;
	}

	private <T extends ItemModel> List<T> listStub(final Class<T> clazz, final String... codes)
	{
		final List<T> models = new ArrayList<T>();
		for (final String code : codes)
		{
			try
			{
				final T model = clazz.newInstance();
				setCodeOnItem(model, code);
				models.add(model);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return models;
	}

	public <T extends ItemModel> void setCodeOnItem(final T obj, final String code)
	{
		try
		{
			final Method m = obj.getClass().getMethod("setCode", code.getClass());
			m.invoke(obj, code);
		}
		catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	private void setDomain(final DomainModel domain, final List<ComponentModel> componentList,
			final List<CommentTypeModel> typeList)
	{
		domain.setCommentTypes(typeList);
		for (final ComponentModel component : componentList)
		{
			component.setDomain(domain);
		}
	}

}
