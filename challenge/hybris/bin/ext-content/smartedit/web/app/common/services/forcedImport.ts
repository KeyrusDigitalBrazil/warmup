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

/**
 * We are doing forced imports in order to generate the types (d.ts) of below interfaces or classes correctly.
 * If we don't include the below imports, as a part of webpack tree shaking, the types will not be generated.
 * There is an open issue in typescript github regarding forced imports
 * https://github.com/Microsoft/TypeScript/issues/9191
 * https://github.com/Microsoft/TypeScript/wiki/FAQ#why-are-imports-being-elided-in-my-emit
 * 
 * If an interface X extends an interface Y, make sure X has all types it needs from Y by checking index.d.ts, if not, do force import of X and Y.
 */
import 'smarteditcommons/services/dependencyInjection/types';
import 'smarteditcommons/services/dependencyInjection/ISeComponent';
import 'smarteditcommons/services/crossFrame/CrossFrameEventService';
import 'smarteditcommons/services/interfaces/IAlertService';
import 'smarteditcommons/services/interfaces/IBrowserService';
import 'smarteditcommons/services/interfaces/ICatalogService';
import 'smarteditcommons/services/interfaces/IContextualMenuButton';
import 'smarteditcommons/services/interfaces/IContextualMenuConfiguration';
import 'smarteditcommons/services/interfaces/IDecorator';
import 'smarteditcommons/services/interfaces/IExperience';
import 'smarteditcommons/services/interfaces/IFeature';
import 'smarteditcommons/services/interfaces/IFeatureService';
import 'smarteditcommons/services/interfaces/IModalService';
import 'smarteditcommons/services/interfaces/IPrioritized';
import 'smarteditcommons/services/interfaces/IReflectable';
import 'smarteditcommons/services/rest/IRestService';
import 'smarteditcommons/services/rest/IRestServiceFactory';
import 'smarteditcommons/services/interfaces/IToolbarItem';
import 'smarteditcommons/services/interfaces/IUriContext';
import 'smarteditcommons/services/interfaces/IURIBuilder';
import 'smarteditcommons/services/SystemEventService';
import 'smarteditcommons/services/wizard/WizardServiceModule';
import 'smarteditcommons/modules/translations/translationServiceModule';
import 'smarteditcommons/components/yDropdown/yDropDownMenu/IYDropdownMenuItem';
