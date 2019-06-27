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
angular.module('i18nMockModule', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])
    .run(function($httpBackend, I18N_RESOURCE_URI, languageService) {
        $httpBackend.whenGET(I18N_RESOURCE_URI + '/' + languageService.getBrowserLocale()).respond({
            'se.modal.administration.configuration.edit.title': 'edit configuration',
            'se.configurationform.actions.cancel': 'cancel',
            'se.configurationform.actions.submit': 'submit',
            'se.configurationform.actions.close': 'close',
            'se.configurationform.json.parse.error': 'this value should be a valid JSON format',
            'se.configurationform.duplicate.entry.error': 'This is a duplicate key',
            'se.configurationform.save.error': 'Save error',
            'se.actions.loadpreview': 'load preview',
            'se.unknown.request.error': 'Your request could not be processed! Please try again later!',
            'se.logindialogform.username.or.password.invalid': 'Invalid username or password',
            'se.logindialogform.username.and.password.required': 'Username and password required',
            'se.authentication.form.input.username': 'username',
            'se.authentication.form.input.password': 'password',
            'se.authentication.form.button.submit': 'submit',
            'type.componenttype1.content.name': 'Content',
            'type.componenttype1.name.name': 'Name',
            'type.componenttype1.mediaContainer.name': 'Media Container',
            'se.componentform.actions.exit': 'Exit',
            'se.componentform.actions.cancel': 'Cancel',
            'se.componentform.actions.submit': 'Submit',
            'abanalytics.popover.title': 'ab analytics',
            'type.componenttype1.content.tooltip': 'enter content',
            'se.cms.component.confirmation.modal.cancel': 'Cancel',
            'se.cms.component.confirmation.modal.save': 'Save',
            'toolbar.action.render.component': 'Render Component',
            'toolbar.action.render.slot': 'Render Slot',
            'se.moretext.more.link': 'MoreLink',
            'se.moretext.less.link': 'LessLink',
            'se.moretext.custom.more.link': 'CustomLinkMore',
            'se.moretext.custom.less.link': 'CustomLinkLess',
            'se.deviceorientation.vertical.label': 'Vertical',
            'se.deviceorientation.horizontal.label': 'Horizontal',
            'se.confirmation.modal.cancel': 'cancel',
            'se.confirmation.modal.ok': 'ok'
        });
    });

try {
    angular.module('smarteditloader').requires.push('i18nMockModule');
    angular.module('smarteditcontainer').requires.push('i18nMockModule');
} catch (ex) {}
