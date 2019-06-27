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
import {SeModule} from 'smarteditcommons';
import {PageComponentsModule} from './pages';
import {GenericEditorWidgetsModule} from './genericEditor';
import {PageVersionsModule} from './versioning/pageVersionsMenuModule';
import {CmsComponentsModule} from './cmsComponents';
import {NavigationModule} from "./navigation/NavigationModule";

/**
 * @ngdoc overview
 * @name cmsSmarteditComponentsModule
 *
 * @description
 * Module containing all the components defined within the CmsSmartEdit container.
 */
@SeModule({
	imports: [
		PageVersionsModule,
		PageComponentsModule,
		GenericEditorWidgetsModule,
		CmsComponentsModule,
		NavigationModule
	]
})
export class CmsSmarteditComponentsModule {}
