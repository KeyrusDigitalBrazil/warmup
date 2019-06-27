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

export * from 'smarteditcontainer/services/bootstrap';
export * from 'smarteditcontainer/services/notifications';
export * from 'smarteditcontainer/services/DelegateRestServiceOuter';
export * from 'smarteditcontainer/services/DragAndDropCrossOriginOuter';
export * from 'smarteditcontainer/services/ExperienceServiceOuter';
export {IframeManagerService} from 'smarteditcontainer/services/iframe/IframeManagerService';
export {DEVICE_ORIENTATIONS} from 'smarteditcontainer/services/iframe/DeviceOrientationsValue';
export {DEVICE_SUPPORTS} from 'smarteditcontainer/services/iframe/DeviceSupportsValue';
export * from 'smarteditcontainer/services/perspectives/FeatureServiceOuter';
export * from 'smarteditcontainer/services/PageInfoServiceOuter';
export * from 'smarteditcontainer/services/PreviewServiceOuter';
export * from 'smarteditcontainer/services/ProductService';
export * from 'smarteditcontainer/services/perspectives/PerspectiveServiceOuter';
export * from 'smarteditcontainer/services/RestServiceFactory';
export * from 'smarteditcontainer/services/SessionServiceOuter';
export * from 'smarteditcontainer/services/SharedDataServiceOuter';
export * from 'smarteditcontainer/services/SiteService';
export * from 'smarteditcontainer/services/StorageServiceOuter';
export {PermissionsRegistrationService} from 'smarteditcontainer/services/PermissionsRegistrationService';
export * from 'smarteditcontainer/services/CatalogServiceOuter';
export * from 'smarteditcontainer/services/UrlServiceOuter';
export * from 'smarteditcontainer/services/WaitDialogServiceOuter';
export * from 'smarteditcontainer/services/catalogAwareRouteResolverModule';
export * from 'smarteditcontainer/services/storage';
export * from 'smarteditcontainer/services/PermissionServiceOuter';
export * from 'smarteditcontainer/services/CatalogVersionPermissionRestService';

// SmarteditServicesModule must be the last one to be imported, error only seen in runtime
export * from 'smarteditcontainer/services/SmarteditServicesModule';