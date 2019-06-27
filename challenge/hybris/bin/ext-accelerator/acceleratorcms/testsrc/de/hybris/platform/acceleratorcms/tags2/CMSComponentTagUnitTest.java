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
package de.hybris.platform.acceleratorcms.tags2;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorservices.util.HtmlElementHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class CMSComponentTagUnitTest
{
	final String CSS_CLASS = "ASDF_CLASS";
	final String DYNAMIC_CSS_CLASS = "DYNAMIC_CLASS";
	final String ATTR_KEY = "attrkey";
	final String ATTR_VAL = "attrvalue";
	final String UID = "ASDF_UID";
	final CMSComponentTag t = new CMSComponentTag();
	final CMSComponentTag spy = Mockito.spy(t);
	final CmsPageRequestContextData contextData = Mockito.mock(CmsPageRequestContextData.class);
	final PageContext pageContext = Mockito.mock(PageContext.class);
	final AbstractCMSComponentModel currentComponent = Mockito.mock(AbstractCMSComponentModel.class);

	@Before
	public void setUp()
	{
		spy.htmlElementHelper = new HtmlElementHelper();
		spy.setPageContext(pageContext);
		spy.currentComponent = currentComponent;
		spy.cmsDynamicAttributeServices = Collections.emptyList();
		Mockito.when(currentComponent.getUid()).thenReturn(UID);
		Mockito.when(pageContext.getAttribute("contentSlot", PageContext.REQUEST_SCOPE)).thenReturn(null);
		Mockito.when(spy.getElementCssClass()).thenReturn(CSS_CLASS);
		spy.currentCmsPageRequestContextData = contextData;
	}

	@Test
	public void testGetElementAttributeWithLiveEdit()
	{
		spy.dynamicAttributes = null;
		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.TRUE);
		Assert.assertEquals(spy.getElementAttributes().get("class"), CSS_CLASS);
	}

	@Test
	public void testGetElementAttributeWithoutLiveEdit()
	{
		spy.dynamicAttributes = null;
		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.FALSE);
		Assert.assertEquals(spy.getElementAttributes().get("class"), CSS_CLASS);
	}

	@Test
	public void testGetElementAttributeWithDynamicAttrsNoLiveEdit()
	{
		final HashMap<String, String> attrs = new HashMap<>();
		attrs.put("class", DYNAMIC_CSS_CLASS);
		attrs.put(ATTR_KEY, ATTR_VAL);
		spy.dynamicAttributes = attrs;

		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.FALSE);

		final Map<String, String> mergedAttributes = spy.getElementAttributes();
		final List<String> classes = Arrays.asList(mergedAttributes.get("class").split(" "));

		Assert.assertTrue(classes.contains(CSS_CLASS));
		Assert.assertTrue(classes.contains(DYNAMIC_CSS_CLASS));
		Assert.assertEquals(mergedAttributes.get(ATTR_KEY), ATTR_VAL);
	}

	@Test
	public void testGetElementAttributeWithDynamicAttrsYesLiveEdit()
	{
		final HashMap<String, String> attrs = new HashMap<>();
		attrs.put("class", DYNAMIC_CSS_CLASS);
		attrs.put(ATTR_KEY, ATTR_VAL);
		spy.dynamicAttributes = attrs;

		Mockito.when(Boolean.valueOf(contextData.isLiveEdit())).thenReturn(Boolean.TRUE);

		final Map<String, String> mergedAttributes = spy.getElementAttributes();
		final List<String> classes = Arrays.asList(mergedAttributes.get("class").split(" "));

		Assert.assertTrue(classes.contains(CSS_CLASS));
		Assert.assertTrue(classes.contains(DYNAMIC_CSS_CLASS));
		Assert.assertEquals(mergedAttributes.get(ATTR_KEY), ATTR_VAL);
	}
}
