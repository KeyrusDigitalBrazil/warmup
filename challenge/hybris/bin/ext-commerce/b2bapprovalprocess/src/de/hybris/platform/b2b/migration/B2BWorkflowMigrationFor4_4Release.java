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
package de.hybris.platform.b2b.migration;

import de.hybris.platform.b2b.constants.B2BApprovalConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.workflow.model.AbstractWorkflowActionModel;
import de.hybris.platform.workflow.model.AbstractWorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * B2BWorkflowMigrationFor4_4Release is used to migrate b2bapprovalprocess essential data.
 */

@SystemSetup(extension = B2BApprovalConstants.EXTENSIONNAME)
@Component("b2bWorkflowMigrationFor4_4Release")
public class B2BWorkflowMigrationFor4_4Release
{

	private static final Logger LOG = Logger.getLogger(B2BWorkflowMigrationFor4_4Release.class.getName());
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private TypeService typeService;

	@SystemSetup(extension = B2BApprovalConstants.EXTENSIONNAME, type = Type.ESSENTIAL)
	public void migrateWorkflowActionModels() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{

		final List<WorkflowActionModel> actionsToMigrate = findWorkflowActionsToMigrate();

		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("Migrating name attribute to qualifier attribute for %s WorkflowActionModels ",
					Integer.valueOf(actionsToMigrate.size())));
		}
		// copy over the name attribute into the new qualifier attribute.
		CollectionUtils.forAllDo(actionsToMigrate, new Closure()
		{
			@Override
			public void execute(final Object object)
			{
				final AbstractWorkflowActionModel action = (AbstractWorkflowActionModel) object;
				action.setQualifier(action.getName(Locale.ENGLISH));
			}
		});
		getModelService().saveAll(actionsToMigrate);

		final List<AbstractWorkflowDecisionModel> decisions = new ArrayList<AbstractWorkflowDecisionModel>();
		for (final AbstractWorkflowActionModel action : actionsToMigrate)
		{
			if (action instanceof WorkflowActionModel)
			{
				decisions.addAll((Collection<AbstractWorkflowDecisionModel>) PropertyUtils.getProperty(action,
						WorkflowActionModel.DECISIONS));
			}
			else if (action instanceof WorkflowActionTemplateModel)
			{
				decisions.addAll((Collection<AbstractWorkflowDecisionModel>) PropertyUtils.getProperty(action,
						WorkflowActionTemplateModel.DECISIONTEMPLATES));
			}
		}

		CollectionUtils.forAllDo(decisions, new Closure()
		{
			@Override
			public void execute(final Object object)
			{
				final AbstractWorkflowDecisionModel decision = (AbstractWorkflowDecisionModel) object;
				decision.setQualifier(decision.getName(Locale.ENGLISH));
			}
		});

		this.getModelService().saveAll(decisions);
		Assert.isTrue(findWorkflowActionsToMigrate().isEmpty(), "WorkflowActions where not migrated successfully.");
	}


	protected List<WorkflowActionModel> findWorkflowActionsToMigrate()
	{

		final String sql = String.format("SELECT {w:pk} from {%s as w}, {Principal as p} "
				+ "WHERE {w:principalAssigned} = {p:pk} AND {w:qualifier} is null AND {p:name[en]} is not null "
				+ "AND {p:itemtype} IN (?principalTypes)", WorkflowActionModel._TYPECODE);
		final Map<String, Object> attr = new HashMap<String, Object>(1);
		attr.put(
				"principalTypes",
				Arrays.asList(new ComposedTypeModel[]
				{ getTypeService().getComposedTypeForCode(EmployeeModel._TYPECODE),
						getTypeService().getComposedTypeForCode(B2BCustomerModel._TYPECODE) }));

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql);
		query.getQueryParameters().putAll(attr);
		final SearchResult<WorkflowActionModel> search = getFlexibleSearchService().search(query);
		return search.getResult();
	}


	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Autowired
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Autowired
	public void setModelService(@Qualifier("defaultModelService") final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Autowired
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
