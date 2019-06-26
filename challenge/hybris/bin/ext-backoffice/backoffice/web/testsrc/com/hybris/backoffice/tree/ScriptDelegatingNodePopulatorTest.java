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
package com.hybris.backoffice.tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.scripting.engine.impl.DefaultScriptExecutionResult;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.navigation.impl.SimpleNode;
import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.core.context.impl.DefaultCockpitContext;
import com.hybris.cockpitng.tree.node.DynamicNode;
import com.hybris.cockpitng.tree.node.DynamicNodePopulator;


@RunWith(MockitoJUnitRunner.class)
public class ScriptDelegatingNodePopulatorTest
{

	private static final String SCRIPT_URI = "uri";

	static class TestPopulator implements DynamicNodePopulator
	{
		@Override
		public List<NavigationNode> getChildren(final NavigationNode node)
		{
			return TEST_NODES;
		}
	}

	private static final SimpleNode CHILD_NODE = new SimpleNode("child1");
	private static final List<NavigationNode> TEST_NODES = Arrays.asList(new NavigationNode[]
	{ CHILD_NODE });

	@Mock
	private ScriptingLanguagesService scriptingLanguagesService;

	@Mock
	private ScriptExecutable scriptExecutable;

	@Mock
	private DefaultScriptExecutionResult scriptExecutionResult;

	private final ScriptDelegatingNodePopulator populator = new ScriptDelegatingNodePopulator()
	{
		@Override
		protected ScriptingLanguagesService getScriptingLanguagesService()
		{
			return scriptingLanguagesService;
		}
	};

	private final DynamicNode parent = new DynamicNode("parent", populator, 10);
	private final CockpitContext context = new DefaultCockpitContext();


	@Before
	public void init()
	{
		context.setParameter(ScriptDelegatingNodePopulator.PARAM_SCRIPT_URI, SCRIPT_URI);
		parent.setContext(context);
	}

	@Test
	public void testExtractChildren()
	{
		List<NavigationNode> nodes = populator.extractChildren(parent, new Object());
		assertThat(nodes).isEmpty();

		nodes = populator.extractChildren(parent, new Object[]
		{ new Object() });
		assertThat(nodes).isEmpty();

		nodes = populator.extractChildren(parent, CHILD_NODE);
		assertThat(nodes).hasSize(1);
		assertThat(nodes.get(0)).isEqualTo(CHILD_NODE);
		assertThat(nodes.get(0).getParent()).isEqualTo(parent);

		nodes = populator.extractChildren(parent, TEST_NODES);
		assertThat(nodes).hasSize(1);
		assertThat(nodes.get(0)).isEqualTo(CHILD_NODE);
		assertThat(nodes.get(0).getParent()).isEqualTo(parent);

		nodes = populator.extractChildren(parent, TestPopulator.class);
		assertThat(nodes).hasSize(1);
		assertThat(nodes.get(0)).isEqualTo(CHILD_NODE);
		assertThat(nodes.get(0).getParent()).isEqualTo(parent);

		nodes = populator.extractChildren(parent, new TestPopulator());
		assertThat(nodes).hasSize(1);
		assertThat(nodes.get(0)).isEqualTo(CHILD_NODE);
		assertThat(nodes.get(0).getParent()).isEqualTo(parent);
	}


	@Test
	public void testExecuteScript()
	{
		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_URI)).thenReturn(scriptExecutable);

		populator.executeScript(context);

		verify(scriptingLanguagesService).getExecutableByURI(Matchers.eq(SCRIPT_URI));
		verify(scriptExecutable).execute();
		verifyNoMoreInteractions(scriptingLanguagesService);
		verifyNoMoreInteractions(scriptExecutable);


		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_URI)).thenReturn(scriptExecutable);
		when(scriptExecutable.execute()).thenThrow(Exception.class);
	}

	@Test(expected = Exception.class)
	public void testExecuteScriptException()
	{
		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_URI)).thenReturn(scriptExecutable);
		when(scriptExecutable.execute()).thenThrow(Exception.class);

		populator.executeScript(context);
	}


	@Test
	public void testExecuteScriptEmptyUri()
	{
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(ScriptDelegatingNodePopulator.PARAM_SCRIPT_URI, "");

		populator.executeScript(context);

		verifyNoMoreInteractions(scriptingLanguagesService);
		verifyNoMoreInteractions(scriptExecutable);
	}

	@Test
	public void testGetChildren()
	{
		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_URI)).thenReturn(scriptExecutable);
		when(scriptExecutable.execute()).thenReturn(scriptExecutionResult);
		when(scriptExecutionResult.getScriptResult()).thenReturn(TEST_NODES);
		when(Boolean.valueOf(scriptExecutionResult.isSuccessful())).thenReturn(Boolean.TRUE);

		final List<NavigationNode> children = populator.getChildren(parent);

		assertThat(children).isEqualTo(TEST_NODES);

		verify(scriptingLanguagesService).getExecutableByURI(Matchers.eq(SCRIPT_URI));
		verify(scriptExecutable).execute();
		verifyNoMoreInteractions(scriptingLanguagesService);
		verifyNoMoreInteractions(scriptExecutable);
	}
}
