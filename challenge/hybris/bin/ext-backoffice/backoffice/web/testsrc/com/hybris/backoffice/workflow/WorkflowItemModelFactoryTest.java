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
package com.hybris.backoffice.workflow;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.components.visjs.network.data.Node;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowItemModelFactoryTest
{
	private static final String ACTION_NAME = "action";
	private static final String ACTION_TEMPLATE_NAME = "actionTemplate";
	private static final String DECISION_NAME = "decision";
	private static final String DECISION_TEMPLATE_NAME = "decisionTemplate";

	@Mock
	WorkflowActionModel mockedAction;

	@Mock
	WorkflowActionTemplateModel mockedActionTemplate;

	@Mock
	WorkflowDecisionModel mockedDecision;

	@Mock
	WorkflowDecisionTemplateModel mockedDecisionTemplate;

	@Mock
	LabelService mockedLabelService;

	@Mock
	CockpitLocaleService mockedLocaleService;

	@Mock
	LinkModel mockedLinkModel;

	@InjectMocks
	WorkflowItemModelFactory workflowItemModelFactory;

	@Test
	public void shouldTellLinkIsAnAndConnection()
	{
		// given
		given(mockedLinkModel.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION)).willReturn(true);

		// when
		final boolean result = WorkflowItemModelFactory.isAndConnection(mockedLinkModel);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldTellLinkIsNotAnAndConnection()
	{
		// when
		final boolean result = WorkflowItemModelFactory.isAndConnection(mockedLinkModel);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldTellNullLinkIsNotAnAndConnection()
	{
		// when
		final boolean result = WorkflowItemModelFactory.isAndConnection(null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldTellLinkIsAnAndConnectionTemplate()
	{
		// given
		given(mockedLinkModel.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION_TEMPLATE)).willReturn(true);

		// when
		final boolean result = WorkflowItemModelFactory.isAndConnectionTemplate(mockedLinkModel);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldTellLinkIsNotAnAndConnectionTemplate()
	{
		// when
		final boolean result = WorkflowItemModelFactory.isAndConnectionTemplate(mockedLinkModel);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldTellNullLinkIsNotAnAndConnectionTemplate()
	{
		// when
		final boolean result = WorkflowItemModelFactory.isAndConnectionTemplate(null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldCreateWorkflowItemFromAction()
	{
		// given
		given(mockedLocaleService.getCurrentLocale()).willReturn(Locale.ENGLISH);

		given(mockedDecision.getPk()).willReturn(PK.fromLong(2L));

		given(mockedAction.getPk()).willReturn(PK.fromLong(1L));
		given(mockedAction.getName(Locale.ENGLISH)).willReturn(ACTION_NAME);
		given(mockedAction.getIncomingDecisions()).willReturn(newArrayList(mockedDecision));

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedAction);

		// then
		final Node node = result.createNode();
		assertThat(node.getId()).isEqualTo("1");
		assertThat(node.getLabel()).isEqualTo(ACTION_NAME);
		assertThat(node.getGroup()).isEqualTo(ACTION_NAME);

		assertThat(node.getLevel()).isEqualTo(WorkflowItem.BASE_LEVEL);
		assertThat(result.getNeighborsIds()).containsOnly("2");
	}

	@Test
	public void shouldCreateWorkflowItemFromActionWithAndConnections()
	{
		// given
		final PK mockedLinkModelPk = PK.fromLong(1L);
		given(mockedLinkModel.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION)).willReturn(true);
		given(mockedLinkModel.getPk()).willReturn(mockedLinkModelPk);

		given(mockedAction.getIncomingLinks()).willReturn(newArrayList(mockedLinkModel));

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedAction);

		// then
		assertThat(result.getNeighborsIds()).containsOnly(String.valueOf(mockedLinkModelPk));
	}

	@Test
	public void shouldCreateWorkflowItemFromActionTemplate()
	{
		// given
		given(mockedLocaleService.getCurrentLocale()).willReturn(Locale.ENGLISH);

		given(mockedDecisionTemplate.getPk()).willReturn(PK.fromLong(2L));

		given(mockedActionTemplate.getPk()).willReturn(PK.fromLong(1L));
		given(mockedActionTemplate.getName(Locale.ENGLISH)).willReturn(ACTION_TEMPLATE_NAME);
		given(mockedActionTemplate.getIncomingTemplateDecisions()).willReturn(newArrayList(mockedDecisionTemplate));

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedActionTemplate);

		// then
		final Node node = result.createNode();
		assertThat(node.getId()).isEqualTo("1");
		assertThat(node.getLabel()).isEqualTo(ACTION_TEMPLATE_NAME);
		assertThat(node.getGroup()).isEqualTo(ACTION_NAME);

		assertThat(node.getLevel()).isEqualTo(WorkflowItem.BASE_LEVEL);
		assertThat(result.getNeighborsIds()).containsOnly("2");
	}

	@Test
	public void shouldCreateWorkflowItemFromActionWithDetailsInTitle()
	{
		// given
		final PrincipalModel mockedAssignedUser = mock(PrincipalModel.class);
		final Date mockedLastUpdateDate = mock(Date.class);

		given(mockedLocaleService.getCurrentLocale()).willReturn(Locale.ENGLISH);
		given(mockedLabelService.getObjectLabel(mockedAssignedUser)).willReturn("assigned user");
		given(mockedLabelService.getObjectLabel(mockedLastUpdateDate)).willReturn("today");

		given(mockedAction.getPk()).willReturn(PK.fromLong(1L));
		given(mockedAction.getName(Locale.ENGLISH)).willReturn(ACTION_NAME);
		given(mockedAction.getPrincipalAssigned()).willReturn(mockedAssignedUser);
		given(mockedAction.getActivated()).willReturn(mockedLastUpdateDate);

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedAction);

		// then
		final Node node = result.createNode();
		assertThat(node.getTitle()).contains("today");
		assertThat(node.getTitle()).contains("assigned user");
	}

	@Test
	public void shouldCreateWorkflowItemFromActionTemplateWithAndConnections()
	{
		// given
		final PK mockedLinkModelPk = PK.fromLong(1L);
		given(mockedLinkModel.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION_TEMPLATE)).willReturn(true);
		given(mockedLinkModel.getPk()).willReturn(mockedLinkModelPk);

		given(mockedActionTemplate.getIncomingLinkTemplates()).willReturn(newArrayList(mockedLinkModel));

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedActionTemplate);

		// then
		assertThat(result.getNeighborsIds()).containsOnly(String.valueOf(mockedLinkModelPk));
	}

	@Test
	public void shouldCreateWorkflowItemFromDecision()
	{
		// given
		given(mockedLocaleService.getCurrentLocale()).willReturn(Locale.GERMAN);

		given(mockedAction.getPk()).willReturn(PK.fromLong(2L));

		given(mockedDecision.getPk()).willReturn(PK.fromLong(1L));
		given(mockedDecision.getName(Locale.GERMAN)).willReturn(DECISION_NAME);
		given(mockedDecision.getAction()).willReturn(mockedAction);

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedDecision);

		// then
		final Node node = result.createNode();
		assertThat(node.getId()).isEqualTo("1");
		assertThat(node.getLabel()).isEqualTo(DECISION_NAME);
		assertThat(node.getGroup()).isEqualTo(DECISION_NAME);

		assertThat(node.getLevel()).isEqualTo(WorkflowItem.BASE_LEVEL);
		assertThat(result.getNeighborsIds()).containsOnly("2");
	}

	@Test
	public void shouldCreateWorkflowItemFromDecisionTemplate()
	{
		// given
		given(mockedLocaleService.getCurrentLocale()).willReturn(Locale.GERMAN);

		given(mockedActionTemplate.getPk()).willReturn(PK.fromLong(2L));

		given(mockedDecisionTemplate.getPk()).willReturn(PK.fromLong(1L));
		given(mockedDecisionTemplate.getName(Locale.GERMAN)).willReturn(DECISION_TEMPLATE_NAME);
		given(mockedDecisionTemplate.getActionTemplate()).willReturn(mockedActionTemplate);

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedDecisionTemplate);

		// then
		final Node node = result.createNode();
		assertThat(node.getId()).isEqualTo("1");
		assertThat(node.getLabel()).isEqualTo(DECISION_TEMPLATE_NAME);
		assertThat(node.getGroup()).isEqualTo(DECISION_NAME);

		assertThat(node.getLevel()).isEqualTo(WorkflowItem.BASE_LEVEL);
		assertThat(result.getNeighborsIds()).containsOnly("2");
	}

	@Test
	public void shouldCreateWorkflowItemFromLinkModel()
	{
		// given
		final ItemModel mockedTarget = mock(ItemModel.class);
		given(mockedTarget.getPk()).willReturn(PK.fromLong(1L));

		final ItemModel mockedSource = mock(ItemModel.class);
		given(mockedSource.getPk()).willReturn(PK.fromLong(2L));

		given(mockedLinkModel.getTarget()).willReturn(mockedTarget);
		given(mockedLinkModel.getSource()).willReturn(mockedSource);

		// when
		final WorkflowItem result = workflowItemModelFactory.create(mockedLinkModel);

		// then
		final Node node = result.createNode();
		assertThat(node.getId()).isEqualTo("AND1");
		assertThat(node.getLabel()).isEqualTo("AND");
		assertThat(node.getLevel()).isEqualTo(WorkflowItem.BASE_LEVEL);
		assertThat(node.getGroup()).isEqualTo("andConnection");

		assertThat(result.getNeighborsIds()).containsOnly("2");
	}

	@Test
	public void shouldMergeNeighbors()
	{
		// given
		final ItemModel mockedItemModel = mock(ItemModel.class);
		given(mockedItemModel.getPk()).willReturn(PK.fromLong(1L));
		final Node mockedNode = mock(Node.class);

		final WorkflowItem mockedTarget = mock(WorkflowItem.class);
		given(mockedTarget.getId()).willReturn("2");
		given(mockedTarget.getLevel()).willReturn(2);
		given(mockedTarget.getType()).willReturn(WorkflowItem.Type.ACTION);
		given(mockedTarget.createNode()).willReturn(mockedNode);
		given(mockedTarget.getNeighborsIds()).willReturn(newArrayList("1", "2", "3"));

		final WorkflowItem mockedSource = mock(WorkflowItem.class);
		given(mockedSource.getNeighborsIds()).willReturn(newArrayList("4", "5", "6"));

		// when
		final WorkflowItem result = workflowItemModelFactory.mergeNeighbors(mockedTarget, mockedSource);

		// then
		assertThat(result.getId()).isEqualTo("2");
		assertThat(result.getLevel()).isEqualTo(2);
		assertThat(result.getType()).isEqualTo(WorkflowItem.Type.ACTION);
		assertThat(result.createNode()).isEqualTo(mockedNode);
		assertThat(result.getNeighborsIds()).containsOnly("1", "2", "3", "4", "5", "6");
	}
}
