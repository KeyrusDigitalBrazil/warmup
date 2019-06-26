angular.module('personalizationsearchRestServiceModule', [
        'smarteditServicesModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditServicesModule'
    ])
    .factory('personalizationsearchRestService', function(restServiceFactory, personalizationsmarteditContextService, personalizationsmarteditUtils) {

        var SEARCH_PROFILES = "/adaptivesearchwebservices/v1/searchprofiles";

        var UPDATE_CUSTOMIZATION_RANK = "/personalizationwebservices/v1/query/cxUpdateSearchProfileActionRank";
        var GET_INDEX_TYPES_FOR_SITE = "/personalizationwebservices/v1/query/cxGetIndexTypesForSite";

        var restService = {};

        restService.getSearchProfiles = function(filter) {

            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;

            var catalogVersionsStr = (experienceData.productCatalogVersions || []).map(function(cv) {
                return cv.catalog + ':' + cv.catalogVersion;
            }).join(",");

            var restService = restServiceFactory.get(SEARCH_PROFILES);

            var param = {
                "catalogVersions": catalogVersionsStr
            };

            filter = angular.extend(filter, param);

            return restService.get(filter);
        };


        restService.updateSearchProfileActionRank = function(filter) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;

            var restService = restServiceFactory.get(UPDATE_CUSTOMIZATION_RANK);
            var entries = [];
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", filter.customizationCode);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variation", filter.variationCode);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "rankBeforeAction", filter.rankBeforeAction);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "rankAfterAction", filter.rankAfterAction);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "actions", filter.actions);

            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", experienceData.catalogDescriptor.catalogId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", experienceData.catalogDescriptor.catalogVersion);
            var requestParams = {
                "params": {
                    "entry": entries
                }
            };
            return restService.save(requestParams);
        };

        restService.getIndexTypesForCatalogVersion = function(productCV) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;

            var restService = restServiceFactory.get(GET_INDEX_TYPES_FOR_SITE);
            var entries = [];

            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "baseSiteId", experienceData.catalogDescriptor.siteId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", productCV.catalog);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", productCV.catalogVersion);
            var requestParams = {
                "params": {
                    "entry": entries
                }
            };
            return restService.save(requestParams);
        };

        return restService;
    });
