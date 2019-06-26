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
import {deprecate} from "./deprecate";
deprecate();

import {
	LanguageDropdownSelectorComponent,
	LanguageSelectorComponent,
	YEventMessageComponent,
	YInfiniteScrollingComponent,
	YMoreTextComponent
} from 'smarteditcommons/components';
import {
	CompileHtmlDirective
} from 'smarteditcommons/directives';

import {CrossFrameEventService} from './crossFrame/CrossFrameEventService';
import {CrossFrameEventServiceGateway} from './crossFrame/CrossFrameEventServiceGateway';
import {SystemEventService} from './SystemEventService';
import {FunctionsModule} from '../utils/functionsModule';
import {LanguageService} from './language/LanguageService';
import {LanguageServiceGateway} from './language/LanguageServiceGateway';
import {SeModule} from './dependencyInjection/di';
import {SmarteditRootModule} from './SmarteditRootModule';
import {TranslationServiceModule} from '../modules/translations/translationServiceModule';
import {FlawInjectionInterceptorModule} from './flaws/flawInjectionInterceptorModule';
import {ConfigModule} from './ConfigModule';
import {AuthorizationService} from "smarteditcommons/services/auth/AuthorizationService";
import {CommonsRestServiceModule} from "smarteditcommons/services/rest/CommonsRestServiceModule";

/**
 * @ngdoc overview
 * @name smarteditCommonsModule
 *
 * @description
 * Module containing all the services shared within the smartedit commons.
 */
@SeModule({
	imports: [
		SmarteditRootModule,
		CommonsRestServiceModule,
		FunctionsModule,
		FlawInjectionInterceptorModule,
		'infinite-scroll',
		'resourceLocationsModule',
		'seConstantsModule',
		'yjqueryModule',
		'yLoDashModule',
		TranslationServiceModule,
		ConfigModule,
		'ui.select',
		'ngSanitize'
	],
	providers: [
		AuthorizationService,
		SystemEventService,
		CrossFrameEventServiceGateway,
		CrossFrameEventService,
		LanguageServiceGateway,
		LanguageService
	],
	declarations: [
		CompileHtmlDirective,
		YInfiniteScrollingComponent,
		YEventMessageComponent,
		YMoreTextComponent,
		LanguageDropdownSelectorComponent,
		LanguageSelectorComponent
	]
})
export class SmarteditCommonsModule {}