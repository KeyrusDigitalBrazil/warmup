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
package com.hybris.backoffice.workflow.renderer;

import static com.hybris.cockpitng.dataaccess.facades.type.DataType.Type.COMPOUND;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockingDetails;
import org.mockito.verification.VerificationMode;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.common.AbstractWidgetComponentRenderer;

@Ignore("Abstract class should not be instantiated")
public abstract class AbstractWidgetComponentRendererTest<RENDERER extends AbstractWidgetComponentRenderer>
		extends AbstractCockpitngUnitTest<RENDERER>
{

	protected HtmlBasedComponent parent;

	protected RENDERER renderer;

	protected WidgetInstanceManager widgetInstanceManager;

	protected abstract RENDERER createRendererInstance();

	@Before
	public void setUp()
	{
		mockZKEnvironment();

		this.widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
		this.renderer = spy(createRendererInstance());
		final HtmlBasedComponent parent = createParent();
		final MockingDetails mockingDetails = mockingDetails(parent);
		if (!mockingDetails.isMock() && !mockingDetails.isMock())
		{
			this.parent = spy(parent);
		}
		else
		{
			this.parent = parent;
		}
	}

	/**
	 * Creates a parent component on which all rendering operations should be performed
	 *
	 * @return parent component
	 */
	protected HtmlBasedComponent createParent()
	{
		return new Div();
	}

	/**
	 * Mocks the ZK environment. If you need to disable the mocking override the method with empty implementation.
	 *
	 * @see CockpitTestUtil#mockZkEnvironment
	 */
	protected void mockZKEnvironment()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	/**
	 * Mocks call to
	 * {@link com.hybris.cockpitng.widgets.common.WidgetComponentRenderer#render(Object, Object, Object, DataType, WidgetInstanceManager)}
	 *
	 * @return parent component used for mock
	 * @see #createDefaultRenderedConfiguration()
	 * @see #createDefaultRenderedData()
	 */
	protected void executeRendering()
	{
		executeRendering(createDefaultRenderedConfiguration());
	}

	/**
	 * Mocks call to
	 * {@link com.hybris.cockpitng.widgets.common.WidgetComponentRenderer#render(Object, Object, Object, DataType, WidgetInstanceManager)}
	 *
	 * @return parent component used for mock
	 * @see #createDefaultRenderedData()
	 */
	protected void executeRendering(final Object configuration)
	{
		executeRendering(configuration, createDefaultRenderedData());
	}

	/**
	 * Mocks call to
	 * {@link com.hybris.cockpitng.widgets.common.WidgetComponentRenderer#render(Object, Object, Object, DataType, WidgetInstanceManager)}
	 *
	 * @return parent component used for mock
	 */
	protected void executeRendering(final Object configuration, final Object data)
	{
		final DataType dataType = data != null ? new DataType.Builder(data.getClass().getName()).type(COMPOUND).build()
				: DataType.NULL_COMPOUND;
		executeRendering(configuration, data, dataType);
	}

	/**
	 * Mocks call to
	 * {@link com.hybris.cockpitng.widgets.common.WidgetComponentRenderer#render(Object, Object, Object, DataType, WidgetInstanceManager)}
	 *
	 * @return parent component used for mock
	 */
	protected void executeRendering(final Object configuration, final Object data, final DataType dataType)
	{
		renderer.render(parent, configuration, data, dataType, widgetInstanceManager);
	}

	protected Object createDefaultRenderedData()
	{
		return null;
	}

	protected Object createDefaultRenderedConfiguration()
	{
		return null;
	}

	@Test
	public void testMinimumNotification()
	{
		// when
		executeRendering(createDefaultRenderedConfiguration());

		// then
		assertFireComponentRendererCalled(parent);
	}

	protected void assertFireComponentRendererCalled(final HtmlBasedComponent parent)
	{
		verify(renderer).fireComponentRendered(same(parent), any(), any());
	}

	protected void assertFireComponentRendererCalled(final HtmlBasedComponent parent, final VerificationMode verificationMode)
	{
		verify(renderer, verificationMode).fireComponentRendered(same(parent), any(), any());
	}

	protected void assertFireComponentRendererCalled(final HtmlBasedComponent component, final HtmlBasedComponent parent)
	{
		verify(renderer).fireComponentRendered(same(component), same(parent), any(), any());
	}

	protected void assertFireComponentRendererCalled(final HtmlBasedComponent component, final HtmlBasedComponent parent,
			final VerificationMode verificationMode)
	{
		verify(renderer, verificationMode).fireComponentRendered(same(component), same(parent), any(), any());
	}

	protected void assertFireComponentRendererCalled(final Class<? extends Component> componentClass,
			final HtmlBasedComponent parent)
	{
		verify(renderer).fireComponentRendered(any(componentClass), same(parent), any());
	}

	protected void assertFireComponentRendererCalled(final Class<? extends Component> componentClass,
			final HtmlBasedComponent parent, final VerificationMode verificationMode)
	{
		verify(renderer, verificationMode).fireComponentRendered(any(componentClass), same(parent), any(), any());
	}
}
