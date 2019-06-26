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
import de.hybris.deltadetection.enums.ChangeType

log.info('input resource (Media code): ' + cronjob.job.input.code)
if (change.changeType == ChangeType.DELETED) {
    changeDetectionService.consumeChanges([change])
    log.info('Consumed: ' + change)
    return true
}
log.info('Not consumed: ' + change)
true
