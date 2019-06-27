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
angular.module('localizedElementModule', ['tabsetModule', 'seConstantsModule', 'yLoDashModule'])
    .controller('localizedElementController', function($scope, $attrs, sessionService, lodash, VALIDATION_MESSAGE_TYPES) {

        this.$onChanges = function(changes) {
            if (changes.field) {

                this.model = {
                    field: this.field,
                    component: this.ge.editor.component[this.field.qualifier]
                };
                this.languages = this.ge.editor.languages;

                if (this.field) {
                    var inputTemplate = this.inputTemplate ? this.inputTemplate : $attrs.inputTemplate;

                    this.field.isLanguageEnabledMap = {};
                    sessionService.getCurrentUser().then(function(userData) {
                        var writeableLanguages = userData.writeableLanguages;
                        var readableLanguages = userData.readableLanguages;

                        this.tabs = this.tabs || []; //keep the same tabs reference
                        this.tabs.length = 0;

                        Array.prototype.push.apply(this.tabs, this.languages
                            .filter(function(language) {
                                return lodash.includes(readableLanguages, language.isocode);
                            })
                            .map(function(language) {
                                var languageId = language.isocode;

                                this.field.isLanguageEnabledMap[languageId] = writeableLanguages.some(function(currentLanguage) {
                                    return currentLanguage === languageId;
                                });

                                return {
                                    id: language.isocode,
                                    title: language.isocode.toUpperCase() + (this.field.required && language.required ? "*" : ""),
                                    templateUrl: inputTemplate
                                };
                            }.bind(this)));
                    }.bind(this));
                }
            }
        };

        var previousMessages;

        this.$doCheck = function() {

            if (this.field.messages !== previousMessages) {
                previousMessages = this.field.messages;
                var messageMap = this.field.messages ? this.field.messages.filter(function(messsage) {
                    return messsage.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR;
                }).reduce(function(holder, next) {
                    holder[next.language] = true;
                    return holder;
                }, {}) : {};

                this.tabs.forEach(function(tab) {
                    var message = messageMap[tab.id];
                    tab.hasErrors = message !== undefined ? message : false;
                });
            }
        };
    })
    .component('localizedElement', {
        templateUrl: 'localizedElementTemplate.html',
        transclude: false,
        require: {
            ge: '^genericEditor'
        },
        controller: 'localizedElementController',
        controllerAs: 'le',
        bindings: {
            field: '<',
            inputTemplate: '<'
        }
    });
