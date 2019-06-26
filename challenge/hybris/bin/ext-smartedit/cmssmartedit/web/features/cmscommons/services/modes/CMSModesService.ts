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
import {SeInjectable} from 'smarteditcommons';
/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:CMSModesService
 *
 * @description
 * General methods for cms modes.
 */
@SeInjectable()
export class CMSModesService {

	public static readonly VERSIONING_PERSPECTIVE_KEY: string = 'se.cms.perspective.versioning';

	constructor(private perspectiveService: any) {}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:CMSModesService#isVersioningPerspectiveActive
     * @methodOf cmsSmarteditServicesModule.service:CMSModesService
     *
     * @description
     * Returns a promise that resolves to a boolean which is true if the current perspective loaded is versioning, false otherwise.
     * 
     * @returns {Promise<boolean>} the promise that resolves to a boolean
     */
	isVersioningPerspectiveActive(): Promise<boolean> {
		return this.perspectiveService.getActivePerspectiveKey().then((activePerspectiveKey: string) => {
			return activePerspectiveKey === CMSModesService.VERSIONING_PERSPECTIVE_KEY;
		});
	}
}