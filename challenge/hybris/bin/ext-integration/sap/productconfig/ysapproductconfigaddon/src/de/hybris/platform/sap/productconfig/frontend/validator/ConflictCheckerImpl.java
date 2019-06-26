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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;

import reactor.util.CollectionUtils;


/**
 * Default implementation of {@link ConflictChecker}.<br>
 * Treats missing mandatory fields and conflicts as less severe than UI-Validation Errors, hence ensures that
 * {@link GroupStatusType#ERROR} is not overwritten by a conflict (@link {@link GroupStatusType#CONFLICT} or a missing
 * mandatory field {@link GroupStatusType#WARNING}.
 */
public class ConflictCheckerImpl implements ConflictChecker
{
	private static final String UNUSED_DUMMY_MESSAGE = "no message";
	private static final String DEFAULT_ERROR_CODE = "sapproductconfig.conflict.default";
	private static final String DEFAULT_MISSING_MANDATORY_TEXT_CODE = "sapproductconfig.missing.mandatory.field.default";
	private static final String DEFAULT_MISSING_MANDATORY_RADIO_CODE = "sapproductconfig.missing.mandatory.radio.default";
	private static final String DEFAULT_MISSING_MANDATORY_MULTI_CODE = "sapproductconfig.missing.mandatory.multi.default";
	private static final String DEFAULT_MISSING_MANDATORY_TEXT_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.field.with.fieldname";
	private static final String DEFAULT_MISSING_MANDATORY_RADIO_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.radio.with.fieldname";
	private static final String DEFAULT_MISSING_MANDATORY_MULTI_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.multi.with.fieldname";
	private static final String NULL_ERROR_CODE = null;

	@Override
	public void checkConflicts(final ConfigurationData config, final BindingResult bindingResult)
	{
		final List<UiGroupData> groups = config.getGroups();

		for (int ii = 0; ii < groups.size(); ii++)
		{
			final UiGroupData group = groups.get(ii);
			final String prefix = "groups[" + ii + "].";
			checkConflitcsInGroups(group, prefix, bindingResult);
		}
	}

	protected GroupStatusType checkConflitcsInGroups(final UiGroupData group, final String prefix,
			final BindingResult bindingResult)
	{
		final List<CsticData> cstics = group.getCstics();

		boolean groupConflict = false;
		if (!CollectionUtils.isEmpty(cstics))
		{
			groupConflict = checkConflictsInCstics(prefix, bindingResult, cstics);
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (!CollectionUtils.isEmpty(subGroups))
		{
			groupConflict = checkConflictsInSubGroups(group, prefix, bindingResult, subGroups) || groupConflict;
		}

		if (groupConflict && group.getGroupStatus() != GroupStatusType.ERROR && group.getGroupStatus() != GroupStatusType.WARNING)
		{
			group.setGroupStatus(GroupStatusType.CONFLICT);
		}

		return group.getGroupStatus();
	}

	protected boolean checkConflictsInCstics(final String prefix, final BindingResult bindingResult, final List<CsticData> cstics)
	{
		boolean groupConflict = false;

		for (int ii = 0; ii < cstics.size(); ii++)
		{
			if (validateCsicConflict(cstics.get(ii), prefix, ii, bindingResult))
			{
				groupConflict = true;
			}
		}
		return groupConflict;
	}

	protected boolean validateCsicConflict(final CsticData csticData, final String prefix, final int index,
			final BindingResult bindingResult)
	{
		final List<ConflictData> conflicts = csticData.getConflicts();

		final boolean nothingToValidate = isEmptyOrNull(conflicts);
		if (nothingToValidate)
		{
			return false;
		}

		final String path = prefix + "cstics[" + index + "].value";
		validate(csticData, conflicts, path, bindingResult);

		return true;
	}

	protected boolean checkConflictsInSubGroups(final UiGroupData group, final String prefix, final BindingResult bindingResult,
			final List<UiGroupData> subGroups)
	{
		boolean groupConflict = false;
		for (int ii = 0; ii < subGroups.size(); ii++)
		{
			final String path = prefix + "subGroups[" + ii + "].";
			if (checkConflictsInSubGroup(group, path, bindingResult, subGroups.get(ii)))
			{
				groupConflict = true;
			}
		}
		return groupConflict;
	}

	protected boolean checkConflictsInSubGroup(final UiGroupData group, final String path, final BindingResult bindingResult,
			final UiGroupData subGroup)
	{
		final GroupStatusType state = checkConflitcsInGroups(subGroup, path, bindingResult);

		switch (state)
		{
			case CONFLICT:
				return true;
			case ERROR:
				group.setGroupStatus(GroupStatusType.ERROR);
				break;
			case WARNING:
				group.setGroupStatus(GroupStatusType.WARNING);
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	public void checkMandatoryFields(final ConfigurationData config, final BindingResult bindingResult)
	{
		final List<UiGroupData> groups = config.getGroups();

		for (int ii = 0; ii < groups.size(); ii++)
		{
			final UiGroupData group = groups.get(ii);
			final String prefix = "groups[" + ii + "].";
			checkMandatoryFieldsInGroups(group, prefix, bindingResult);
		}
	}

	protected GroupStatusType checkMandatoryFieldsInGroups(final UiGroupData group, final String prefix,
			final BindingResult bindingResult)
	{
		final List<CsticData> cstics = group.getCstics();

		boolean groupWarning = false;
		if (!CollectionUtils.isEmpty(cstics))
		{
			groupWarning = checkMandatoryFIeldsInCstics(prefix, bindingResult, cstics);
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (!CollectionUtils.isEmpty(subGroups))
		{
			groupWarning = checkMandatoryFieldsInSubGroups(group, prefix, bindingResult, subGroups) || groupWarning;
		}


		if (groupWarning && group.getGroupStatus() != GroupStatusType.ERROR)
		{
			group.setGroupStatus(GroupStatusType.WARNING);
		}

		return group.getGroupStatus();
	}

	protected boolean checkMandatoryFieldsInSubGroups(final UiGroupData group, final String prefix,
			final BindingResult bindingResult, final List<UiGroupData> subGroups)
	{
		boolean groupWarning = false;
		for (int ii = 0; ii < subGroups.size(); ii++)
		{
			final String path = prefix + "subGroups[" + ii + "].";
			if (checkMondatoryFieldsInSubGroup(group, path, bindingResult, subGroups.get(ii)))
			{
				groupWarning = true;
			}
		}
		return groupWarning;
	}

	protected boolean checkMondatoryFieldsInSubGroup(final UiGroupData group, final String path,
			final BindingResult bindingResult, final UiGroupData subGroup)
	{
		final GroupStatusType state = checkMandatoryFieldsInGroups(subGroup, path, bindingResult);
		boolean groupWarning = false;

		if (GroupStatusType.ERROR.equals(group.getGroupStatus()))
		{
			return false;
		}

		switch (state)
		{
			case CONFLICT:
				group.setGroupStatus(GroupStatusType.CONFLICT);
				break;
			case ERROR:
				group.setGroupStatus(GroupStatusType.ERROR);
				break;
			case WARNING:
				groupWarning = true;
				break;
			default:
				break;
		}
		return groupWarning;
	}

	protected boolean checkMandatoryFIeldsInCstics(final String prefix, final BindingResult bindingResult,
			final List<CsticData> cstics)
	{
		boolean groupWarning = false;
		for (int ii = 0; ii < cstics.size(); ii++)
		{
			if (validateMandatoryCstic(cstics.get(ii), prefix, ii, bindingResult))
			{
				groupWarning = true;
			}

		}
		return groupWarning;
	}

	protected boolean validateMandatoryCstic(final CsticData csticData, final String prefix, final int index,
			final BindingResult bindingResult)
	{
		if (!csticData.isRequired())
		{
			return false;
		}

		final String path = prefix + "cstics[" + index + "].value";

		return validateMandatoryFields(csticData, path, bindingResult);
	}


	protected boolean validateMandatoryFields(final CsticData csticData, final String path, final BindingResult bindingResult)
	{

		if (!checkEmpty(csticData))
		{
			return false;
		}

		csticData.setCsticStatus(CsticStatusType.WARNING);

		final String errorText = determineMandatoryFieldErrorMessage(csticData.getType());
		final String[] errorCodes = determineMandatoryFieldErrorCode(csticData.getType());
		final MandatoryFieldError error = new MandatoryFieldError(csticData, path, null, errorCodes, errorText);
		bindingResult.addError(error);

		return true;

	}

	protected String determineMandatoryFieldErrorMessage(final UiType type)
	{
		switch (type)
		{
			case RADIO_BUTTON:
			case DROPDOWN:
				return "Please select one value";
			case CHECK_BOX_LIST:
				return "Please select one or more values";
			default:
				return "Please enter a value for the required field";
		}
	}

	protected String[] determineMandatoryFieldErrorCode(final UiType type)
	{
		final String[] errorCodes = new String[2];

		switch (type)
		{
			case RADIO_BUTTON:
			case DROPDOWN:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_RADIO_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_RADIO_CODE;
				break;
			case CHECK_BOX_LIST:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_MULTI_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_MULTI_CODE;
				break;
			default:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_TEXT_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_TEXT_CODE;
				break;
		}
		return errorCodes;
	}

	protected boolean checkEmpty(final CsticData csticData)
	{
		final String value = csticData.getValue();
		if (UiType.CHECK_BOX_LIST == csticData.getType())
		{
			for (final CsticValueData csticValue : csticData.getDomainvalues())
			{
				if (csticValue.isSelected())
				{
					return false;
				}
			}
			return true;
		}
		return StringUtils.isBlank(value);
	}

	protected void validate(final CsticData csticData, final List<ConflictData> conflicts, final String path,
			final BindingResult bindingResult)
	{
		for (final ConflictData conflict : conflicts)
		{
			final String conflictText = determineErrorMessage(conflict);
			final String[] errorCodes = determineErrorCode(conflict);
			final ConflictError error = new ConflictError(csticData, path, csticData.getValue(), errorCodes, conflictText);
			bindingResult.addError(error);
		}
	}


	protected String determineErrorMessage(final ConflictData conflict)
	{
		String conflictText = conflict.getText();

		if (conflictText == null)
		{
			conflictText = UNUSED_DUMMY_MESSAGE;
		}

		return conflictText;
	}


	protected String[] determineErrorCode(final ConflictData conflict)
	{
		final String[] errorCodes = new String[1];

		if (conflict.getText() == null)
		{
			errorCodes[0] = DEFAULT_ERROR_CODE;
		}
		else
		{
			errorCodes[0] = NULL_ERROR_CODE;
		}

		return errorCodes;
	}

	protected boolean isEmptyOrNull(final List<?> list)
	{
		return list == null || list.isEmpty();
	}


	@Override
	public void checkCompletness(final ConfigurationData config)
	{
		checkCompletness(config.getGroups());
	}


	protected boolean checkCompletness(final List<UiGroupData> uiGroups)
	{
		if (uiGroups == null)
		{
			return true;
		}

		boolean isAllComplete = true;
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (groupIsNotEmpty(uiGroup))
			{
				boolean isGroupComplete;
				if (hasNoErrorOrWarningOrConflict(uiGroup))
				{
					isGroupComplete = checkAllCsticCopmplete(uiGroup);
				}
				else
				{
					isGroupComplete = false;
				}
				final boolean allSubGroupsComplete = checkCompletness(uiGroup.getSubGroups());
				if (isGroupComplete && allSubGroupsComplete)
				{
					uiGroup.setGroupStatus(GroupStatusType.FINISHED);
				}
				isAllComplete = isAllComplete && isGroupComplete && allSubGroupsComplete;
			}
		}
		return isAllComplete;

	}

	protected boolean groupIsNotEmpty(final UiGroupData uiGroup)
	{
		final List<CsticData> cstics = uiGroup.getCstics();
		final List<UiGroupData> subGroups = uiGroup.getSubGroups();
		return !isEmptyOrNull(cstics) || !isEmptyOrNull(subGroups);
	}

	protected boolean checkAllCsticCopmplete(final UiGroupData uiGroup)
	{
		boolean isComplete = true;
		final List<CsticData> cstics = uiGroup.getCstics();
		if (!CollectionUtils.isEmpty(cstics))
		{
			if (uiGroup.isVisited())
			{
				isComplete = cstics.stream().noneMatch(cstic -> cstic.isRequired() && checkEmpty(cstic));
			}
			else
			{
				isComplete = false;
			}
		}
		return isComplete;
	}


	protected boolean hasNoErrorOrWarningOrConflict(final UiGroupData uiGroup)
	{
		final GroupStatusType groupStatus = uiGroup.getGroupStatus();
		return !GroupStatusType.ERROR.equals(groupStatus) && !GroupStatusType.WARNING.equals(groupStatus)
				&& !GroupStatusType.CONFLICT.equals(groupStatus);
	}
}
