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
function importAll(requireContext: any) {
	requireContext
		.keys()
		.forEach((key: any) => {
			requireContext(key);
		});
}

export function doImport() {
	importAll((require as any).context('./', true, /\.js$/));
	importAll((require as any).context('../personalizationcommons', true, /\.js$/));
	importAll((require as any).context('../../', true, /.*\/featureExtensions\/.*\/personalizationsmarteditcontainer\/.*\.js$/));
}