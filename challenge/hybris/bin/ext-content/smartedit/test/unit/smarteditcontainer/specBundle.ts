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

/*
 each file passed to the karma configuration acts as an entry point for the webpack configuration
 To avoid a resource/time issue, we use one entry point specBundle.ts instead of passing files individually to the karma configuration.
*/
function importAll(requireContext: any) {
	requireContext
		.keys()
		.forEach(function(key: string) {
			requireContext(key);
		});
}
importAll(require.context('./unit', true, /Test\.(js|ts)$/));
importAll(require.context('../../../jsTarget/web/app/smarteditcontainer', true, /Module\.ts$/));
