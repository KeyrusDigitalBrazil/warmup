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
package de.hybris.platform.patches.actions.data

import de.hybris.platform.patches.actions.PatchAction

/**
 * Sample patch action for {@link PatchActionPerformErrorHandlingTest} purposes
 */
class SamplePatchActionForTests implements PatchAction {
    @Override
    void perform(final PatchActionData data) {
        data.getOption(PatchActionDataOption.Impex.RUN_AGAIN) // do nothing or throw exception
    }
}
