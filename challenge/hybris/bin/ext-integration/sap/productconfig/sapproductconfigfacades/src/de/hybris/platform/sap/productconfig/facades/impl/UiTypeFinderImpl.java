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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link UiTypeFinder}.
 */
public class UiTypeFinderImpl implements UiTypeFinder
{
	private int dropDownListThreshold = 4;

	private static final Logger LOG = Logger.getLogger(UiTypeFinderImpl.class);
	private static final String LOG_CSTIC_NAME = "CsticModel [CSTIC_NAME='";

	@Override
	public UiType findUiTypeForCstic(final CsticModel model)
	{
		return findUiTypeForCstic(model, null);
	}

	@Override
	public UiType findUiTypeForCstic(final CsticModel model, final CsticData data)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// isDebugEnabled causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		final List<UiType> posibleTypes = collectPossibleTypes(model, data, isDebugEnabled);
		final UiType uiType = chooseUiType(posibleTypes, model);

		if (isDebugEnabled)
		{
			LOG.debug("UI type found for " + LOG_CSTIC_NAME + model.getName() + "';CSTIC_TYPE='" + model.getValueType()
					+ "';CSTIC_UI_TYPE='" + uiType + "']");
		}

		return uiType;
	}

	protected List<UiType> addUiTypeToListLowMem(final List<UiType> list, final UiType uiType)
	{
		// list is empty, beside this element no more are added
		List<UiType> newList;
		if (list.isEmpty())
		{
			newList = Collections.singletonList(uiType);
		}
		else
		{
			newList = new ArrayList(list);
			newList.add(uiType);
		}
		return newList;
	}

	protected List<UiType> mergeUiTypeListLowMem(final List<UiType> list1, final List<UiType> list2)
	{
		// 99% case either both list empty, or one list empty, other with one
		// element.
		List<UiType> newList;
		if (list1.isEmpty() && list2.isEmpty())
		{
			newList = list1;
		}
		else if (!list1.isEmpty() && list2.isEmpty())
		{
			newList = list1;
		}
		else if (list1.isEmpty() && !list2.isEmpty())
		{
			newList = list2;
		}
		else
		{
			newList = new ArrayList<>(list1.size() + list2.size());
			newList.addAll(list1);
			newList.addAll(list2);
		}
		return newList;
	}

	protected List<UiType> collectPossibleTypes(final CsticModel model, final CsticData data, final boolean isDebugEnabled)
	{
		List<UiType> possibleTypes;
		final boolean isReadOnly = isReadonly(model, isDebugEnabled);
		final boolean hasValueImages = hasCsticValueImages(data);
		if (isReadOnly && !hasValueImages)
		{
			possibleTypes = Collections.singletonList(UiType.READ_ONLY);
		}
		else
		{
			possibleTypes = checkForSingelValueTypes(model, isDebugEnabled);
			possibleTypes = mergeUiTypeListLowMem(possibleTypes,
					checkForMultiSelectionTypes(model, isDebugEnabled, hasValueImages, isReadOnly));
			possibleTypes = mergeUiTypeListLowMem(possibleTypes,
					checkForSingleSelectionTypes(model, isDebugEnabled, hasValueImages, isReadOnly));
		}
		return possibleTypes;
	}

	protected List<UiType> checkForSingleSelectionTypes(final CsticModel model, final boolean isDebugEnabled,
			final boolean hasValueImages, final boolean isReadOnly)
	{
		List<UiType> possibleTypes = Collections.emptyList();
		if (isSingleSelectionImage(model, isDebugEnabled, hasValueImages))
		{
			if (isReadOnly)
			{
				possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.READ_ONLY_SINGLE_SELECTION_IMAGE);
			}
			else
			{
				possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.SINGLE_SELECTION_IMAGE);
			}
		}
		if (isRadioButton(model, isDebugEnabled, hasValueImages))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.RADIO_BUTTON);
		}
		if (isRadioButtonAdditionalValue(model, isDebugEnabled))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		}
		if (isDDLB(model, isDebugEnabled, hasValueImages))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.DROPDOWN);
		}
		if (isDDLBAdditionalValue(model, isDebugEnabled))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.DROPDOWN_ADDITIONAL_INPUT);
		}

		return possibleTypes;
	}

	protected List<UiType> checkForMultiSelectionTypes(final CsticModel model, final boolean isDebugEnabled,
			final boolean hasValueImages, final boolean isReadOnly)
	{
		List<UiType> possibleTypes = Collections.emptyList();
		if (isMultiSelectionImage(model, isDebugEnabled, hasValueImages))
		{
			if (isReadOnly)
			{
				possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.READ_ONLY_MULTI_SELECTION_IMAGE);
			}
			else
			{
				possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.MULTI_SELECTION_IMAGE);
			}

		}
		if (isCheckbox(model, isDebugEnabled, hasValueImages))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.CHECK_BOX);
		}
		if (isCheckboxList(model, isDebugEnabled, hasValueImages))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.CHECK_BOX_LIST);
		}

		return possibleTypes;
	}

	protected List<UiType> checkForSingelValueTypes(final CsticModel model, final boolean isDebugEnabled)
	{
		List<UiType> possibleTypes = Collections.emptyList();
		if (isStringInput(model, isDebugEnabled))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.STRING);
		}
		if (isNumericInput(model, isDebugEnabled))
		{
			possibleTypes = addUiTypeToListLowMem(possibleTypes, UiType.NUMERIC);

		}

		return possibleTypes;
	}

	protected UiType chooseUiType(final List<UiType> posibleTypes, final CsticModel model)
	{
		UiType uiType;
		if (posibleTypes.isEmpty())
		{
			uiType = UiType.NOT_IMPLEMENTED;
		}
		else if (posibleTypes.size() == 1)
		{
			uiType = posibleTypes.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Cstic: [" + model + "] has an ambigious uiType: [" + posibleTypes + "]");
		}

		return uiType;
	}

	protected List<UiValidationType> collectPossibleValidationTypes(final CsticModel model, final boolean isDebugEnabled)
	{
		List<UiValidationType> possibleTypes;

		if (isReadonly(model, isDebugEnabled))
		{
			possibleTypes = Collections.singletonList(UiValidationType.NONE);
		}
		else if (isSimpleNumber(model, isDebugEnabled) && (isInput(model, isDebugEnabled)
				|| (isSingleSelection(model, isDebugEnabled) && editableWithAdditionalValue(model, isDebugEnabled))))
		{
			possibleTypes = Collections.singletonList(UiValidationType.NUMERIC);
		}
		else
		{
			possibleTypes = Collections.emptyList();
		}

		return possibleTypes;
	}

	protected UiValidationType chooseUiValidationType(final List<UiValidationType> posibleTypes, final CsticModel model)
	{
		UiValidationType uiType;
		if (posibleTypes.isEmpty())
		{
			uiType = UiValidationType.NONE;
		}
		else if (posibleTypes.size() == 1)
		{
			uiType = posibleTypes.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Cstic: [" + model + "] has an ambigious uiValidationType: [" + posibleTypes + "]");
		}
		return uiType;
	}

	protected boolean isReadonly(final CsticModel model, final boolean isDebugEnabled)
	{
		boolean isReadOnly = model.isReadonly();
		if (model.isConstrained() && !model.isAllowsAdditionalValues()
				&& (model.getAssignableValues() == null || model.getAssignableValues().isEmpty()))
		{
			isReadOnly = true;
		}

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isReadonly='" + isReadOnly + "']");
		}

		return isReadOnly;
	}

	protected boolean isDDLB(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isDDLB = isSingleSelection(model, isDebugEnabled)
				&& model.getAssignableValues().size() > dropDownListThreshold && editableWithoutAdditionalValue(model, isDebugEnabled)
				&& !hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isDDLB='" + isDDLB + "']");
		}

		return isDDLB;
	}

	protected boolean isDDLBAdditionalValue(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isDDLB = isSingleSelection(model, isDebugEnabled)
				&& model.getAssignableValues().size() > dropDownListThreshold && editableWithAdditionalValue(model, isDebugEnabled);

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isDDLBAdditionalValue='" + isDDLB + "']");
		}

		return isDDLB;
	}

	protected boolean isSingleSelectionImage(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isSingleSelectionImage = isSingleSelection(model, isDebugEnabled) && !model.isAllowsAdditionalValues()
				&& hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isSingleSelectionImage='" + isSingleSelectionImage + "']");
		}

		return isSingleSelectionImage;
	}

	protected boolean isRadioButton(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isRadioButton = isSingleSelection(model, isDebugEnabled)
				&& model.getAssignableValues().size() <= dropDownListThreshold
				&& editableWithoutAdditionalValue(model, isDebugEnabled) && !hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isRadioButton='" + isRadioButton + "']");
		}

		return isRadioButton;
	}

	protected boolean isRadioButtonAdditionalValue(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isRadioButton = isSingleSelection(model, isDebugEnabled)
				&& model.getAssignableValues().size() <= dropDownListThreshold && editableWithAdditionalValue(model, isDebugEnabled);

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isRadioButtonAdditionalValue='" + isRadioButton + "']");
		}

		return isRadioButton;
	}

	protected boolean isCheckbox(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isCheckbox = isMultiSelection(model, isDebugEnabled) && model.getStaticDomainLength() == 1 && !hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isCheckbox='" + isCheckbox + "']");
		}
		return isCheckbox;
	}

	protected boolean isCheckboxList(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isCheckboxList = isMultiSelection(model, isDebugEnabled) && model.getStaticDomainLength() != 1
				&& !hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isCheckboxList='" + isCheckboxList + "']");
		}

		return isCheckboxList;
	}

	protected boolean isMultiSelectionImage(final CsticModel model, final boolean isDebugEnabled, final boolean hasValueImages)
	{
		final boolean isMultiSelectionImage = isMultiSelection(model, isDebugEnabled) && !model.isAllowsAdditionalValues()
				&& hasValueImages;

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isMultiSelectionImage='" + isMultiSelectionImage + "']");
		}

		return isMultiSelectionImage;
	}


	protected boolean isStringInput(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isStringInput = isInput(model, isDebugEnabled) && CsticModel.TYPE_STRING == model.getValueType();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isStringInput='" + isStringInput + "']");
		}

		return isStringInput;
	}

	protected boolean isNumericInput(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isNumeric = isInput(model, isDebugEnabled)
				&& (CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType());

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isNumericInput='" + isNumeric + "']");
		}

		return isNumeric;
	}

	protected boolean isSelection(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isSelection = isValueTypeSupported(model, isDebugEnabled)
				&& isConstrainedOrHasAssignableValues(model, isDebugEnabled) && !model.isIntervalInDomain();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isSelection='" + isSelection + "']");
		}

		return isSelection;
	}

	protected boolean isConstrainedOrHasAssignableValues(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isConstrainedOrHasAssignableValues = model.isConstrained() || !model.getAssignableValues().isEmpty();
		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isConstrainedOrHasAssignableValues='"
					+ isConstrainedOrHasAssignableValues + "']");
		}
		return isConstrainedOrHasAssignableValues;

	}

	protected boolean isMultiSelection(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isMultiSelection = isValueTypeSupported(model, isDebugEnabled) && !model.isIntervalInDomain()
				&& !model.isAllowsAdditionalValues() && model.isMultivalued();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isMultiSelection='" + isMultiSelection + "']");
		}

		return isMultiSelection;
	}

	protected boolean isSingleSelection(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isSingleSelection = isSelection(model, isDebugEnabled) && !model.isMultivalued();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isSingleSelection='" + isSingleSelection + "']");
		}

		return isSingleSelection;
	}

	protected boolean isInput(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isInput = isInputTypeSupported(model, isDebugEnabled)
				&& (isSimpleInput(model) || isIntervallBasedInput(model) || isAdditionalValueWithoutDomian(model));
		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isInput='" + isInput + "']");
		}
		return isInput;
	}

	protected boolean isInputTypeSupported(final CsticModel model, final boolean isDebugEnabled)
	{
		return isValueTypeSupported(model, isDebugEnabled) && !model.isReadonly() && !model.isMultivalued();
	}

	protected boolean isIntervallBasedInput(final CsticModel model)
	{
		return model.isIntervalInDomain() && !model.isAllowsAdditionalValues();
	}

	protected boolean isSimpleInput(final CsticModel model)
	{
		return model.getAssignableValues().isEmpty() && !model.isConstrained();
	}

	protected boolean isAdditionalValueWithoutDomian(final CsticModel model)
	{
		final boolean isSingleSelectedValue = model.getAssignableValues().size() == 1 && model.getSingleValue() != null
				&& model.getSingleValue().equals(model.getAssignableValues().get(0).getName());
		return model.isAllowsAdditionalValues() && (model.getAssignableValues().isEmpty() || isSingleSelectedValue);
	}

	protected boolean editableWithoutAdditionalValue(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isSupported = !model.isAllowsAdditionalValues() && !model.isReadonly();

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_editableWithoutAdditionalValue='" + isSupported + "']");
		}

		return isSupported;
	}

	protected boolean editableWithAdditionalValue(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isSupported = model.isAllowsAdditionalValues() && !model.isReadonly() && !model.isIntervalInDomain()
				&& !isAdditionalValueWithoutDomian(model);

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_editableWithAdditionalValue='" + isSupported + "']");
		}

		return isSupported;
	}

	protected boolean isValueTypeSupported(final CsticModel model, final boolean isDebugEnabled)
	{
		final boolean isValueTypeSupported = isSimpleString(model, isDebugEnabled) || isSimpleNumber(model, isDebugEnabled);

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isValueTypeSupported='" + isValueTypeSupported + "']");
		}

		return isValueTypeSupported;
	}

	protected boolean isSimpleString(final CsticModel model, final boolean isDebugEnabled)
	{
		boolean isSimpleString = CsticModel.TYPE_STRING == model.getValueType();
		if (isSimpleString)
		{
			isSimpleString = model.getEntryFieldMask() == null || model.getEntryFieldMask().isEmpty();
		}

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isSimpleString='" + isSimpleString + "']");
		}

		return isSimpleString;
	}

	protected boolean isSimpleNumber(final CsticModel model, final boolean isDebugEnabled)
	{
		boolean isNumber = CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType();
		if (isNumber)
		{
			// Scientific format and multi values interval is not supported
			final boolean isScientific = model.getEntryFieldMask() != null && model.getEntryFieldMask().contains("E");

			isNumber = !isScientific;
		}

		if (isDebugEnabled)
		{
			LOG.debug(LOG_CSTIC_NAME + model.getName() + "';CSTIC_isSimpleNumber='" + isNumber + "']");
		}
		return isNumber;
	}

	/**
	 * @param dropDownListThreshold
	 *           the dropDownListThreshold to set
	 */
	public void setDropDownListThreshold(final int dropDownListThreshold)
	{
		this.dropDownListThreshold = dropDownListThreshold;
	}

	@Override
	public UiValidationType findUiValidationTypeForCstic(final CsticModel model)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// isDebugEnabled causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		final List<UiValidationType> possibleTypes = collectPossibleValidationTypes(model, isDebugEnabled);
		final UiValidationType uiValidationType = chooseUiValidationType(possibleTypes, model);

		if (isDebugEnabled)
		{
			LOG.debug("UI validation type found for " + LOG_CSTIC_NAME + model.getName() + "';CSTIC_TYPE='" + model.getValueType()
					+ "';CSTIC_UI_VALIDATION_TYPE='" + uiValidationType + "']");
		}

		return uiValidationType;
	}

	/**
	 * Checks cstic DTO whether it has at least one image on value level.
	 *
	 * @param data
	 * @return
	 */
	protected boolean hasCsticValueImages(final CsticData data)
	{
		if (data == null)
		{
			return false;
		}

		final List<CsticValueData> domainValues = data.getDomainvalues();
		for (final CsticValueData value : domainValues)
		{
			final boolean valueImage = hasValueImage(value);
			if (valueImage)
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Checks value DTO whether it has at least one image.
	 *
	 * @param valueData
	 * @return
	 */
	protected boolean hasValueImage(final CsticValueData valueData)
	{
		final List<ImageData> media = valueData.getMedia();
		if (media == null || media.isEmpty())
		{
			return false;
		}
		else
		{
			for (final ImageData image : media)
			{
				if (CPQImageType.VALUE_IMAGE.toString().equals(image.getFormat()))
				{
					return true;
				}
			}
			return false;
		}
	}

}
