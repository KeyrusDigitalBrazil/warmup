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
package com.hybris.backoffice.widgets.actions.audit;

import static com.hybris.backoffice.widgets.actions.audit.CreateAuditReportAction.PARAMETER_ALLOWED_CONFIG_CODES;
import static com.hybris.backoffice.widgets.actions.audit.CreateAuditReportAction.PARAMETER_AUDIT;
import static com.hybris.backoffice.widgets.actions.audit.CreateAuditReportAction.PARAMETER_PRESELECTED_CONFIG;
import static com.hybris.backoffice.widgets.actions.audit.CreateAuditReportAction.PARAMETER_REPORT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.audit.internal.config.AuditConfigService;
import de.hybris.platform.auditreport.model.AuditReportDataModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.engine.impl.ComponentWidgetAdapter;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowContextParameterNames;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class CreateAuditReportActionTest extends AbstractCockpitngUnitTest<CreateAuditReportAction>
{
	@Spy
	@InjectMocks
	private CreateAuditReportAction action;

	@Mock
	private TypeFacade typeFacade;
	@Mock
	private AuditConfigService auditConfigService;
	@Mock
	private LabelService labelService;
	@Mock
	private ComponentWidgetAdapter componentWidgetAdapter;

	@Mock
	private ActionContext<ItemModel> context;

	@Test
	public void testPerformWhenReportCreatedSuccessfully()
	{
		// given
		final ItemModel user = mock(UserModel.class);
		when(context.getData()).thenReturn(user);

		final String type = "userType";
		when(typeFacade.getType(user)).thenReturn(type);

		when(labelService.getObjectLabel(user)).thenReturn("objectLabel");

		doReturn("2017/10/20").when(action).prepareFormattedDate(context);

		// when
		final ActionResult<?> result = getActionInstance().perform(context);

		// then
		assertThat(result.getResultCode()).isSameAs(ActionResult.SUCCESS);

		final Class<Map<String, Object>> mapClass = (Class) Map.class;
		final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(mapClass);
		verify(action).sendOutput(eq("openWizard"), captor.capture());

		final Map<String, Object> parameters = captor.getValue();
		assertThat(parameters).hasSize(7);
		assertThat(parameters).containsEntry(PARAMETER_AUDIT, false);
		assertThat(parameters.get(PARAMETER_ALLOWED_CONFIG_CODES)).asList().isEmpty();
		assertThat(parameters).containsEntry(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(),
				AuditReportDataModel._TYPECODE);
		assertThat(parameters).containsEntry(ConfigurableFlowContextParameterNames.PARENT_OBJECT.getName(), user);
		assertThat(parameters).containsEntry(ConfigurableFlowContextParameterNames.PARENT_OBJECT_TYPE.getName(), type);
		assertThat(parameters.get(PARAMETER_REPORT_NAME)).asString().startsWith("PDR objectLabel (");
		assertThat(parameters).containsEntry(PARAMETER_PRESELECTED_CONFIG, null);
	}

	@Test
	public void testCanPerformWhenDataIsNotNull()
	{
		// given
		final UserModel user = mock(UserModel.class);
		when(context.getData()).thenReturn(user);

		// when
		final boolean result = getActionInstance().canPerform(context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testCanPerformWhenDataIsNull()
	{
		// when
		final boolean result = getActionInstance().canPerform(context);

		// then
		assertThat(result).isFalse();
	}


	@Test
	public void shouldReturnValidReportName()
	{
		//given
		doReturn("2017/10/20").when(action).prepareFormattedDate(context);

		//when
		final String reportName = action.prepareReportName(context, "Awesome report name");

		//then
		assertThat(reportName).isEqualTo("PDR Awesome report name (2017/10/20)");
	}

	public CreateAuditReportAction getActionInstance()
	{
		return action;
	}
}
