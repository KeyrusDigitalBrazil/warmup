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
import {diNameUtils, SeModule} from 'smarteditcommons';
import {
	DEFAULT_SYNCHRONIZATION_EVENT,
	DEFAULT_SYNCHRONIZATION_POLLING,
	DEFAULT_SYNCHRONIZATION_STATUSES
} from 'cmscommons/components/synchronize/synchronizationConstants';

@SeModule({
	providers: [
		diNameUtils.makeValueProvider({DEFAULT_SYNCHRONIZATION_STATUSES}),
		diNameUtils.makeValueProvider({DEFAULT_SYNCHRONIZATION_POLLING}),
		diNameUtils.makeValueProvider({DEFAULT_SYNCHRONIZATION_EVENT})
	]
})

export class CmsConstantsModule {}