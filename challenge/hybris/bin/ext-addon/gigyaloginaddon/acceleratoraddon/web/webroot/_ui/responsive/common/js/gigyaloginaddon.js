/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

gigyaHybris = window.gigyaHybris || {};
gigyaHybris.gigyaFunctions = gigyaHybris.gigyaFunctions || {};
gigyaHybris.gigyaCache = gigyaHybris.gigyaCache || {};
window.gigyaHybris.authenticated = ACC.authenticated;


gigyaHybris.gigyaFunctions.logout = function(response) {
    gigya.accounts.logout();
    window.location.href = gigyaHybris.logoutUrl;
};

gigyaHybris.gigyaFunctions.raasLogin = function(response) {
    jQuery.ajax(ACC.config.contextPath + "/gigyaraas/login", {
        data: {
            gigyaData: JSON.stringify(response)
        },
        dataType: "json",
        type: "post"
    }).done(function(data, textStatus, jqXHR) {
        if (data.code !== 0) {
            ACC.colorbox.open(data.message,{
              html : $(document).find("#dialog").html(),
              maxWidth:"100%",
              width:"420px",
              initialWidth :"420px",
              height:"300px"
            });
        } else {
            window.location = ACC.config.contextPath;
        }
    });
};


gigyaHybris.gigyaFunctions.raasEditProfile = function(response) {
    $.ajax(ACC.config.contextPath + "/gigyaraas/profile", {
        data: {
            gigyaData: JSON.stringify(response.response)
        },
        dataType: "json",
        type: "post"
    }).done(function(data, textStatus, jqXHR) {
        if (data.code !== 0) {
            ACC.colorbox.open(data.message,{
                html : $(document).find("#dialog").html(),
                maxWidth:"100%",
                width:"420px",
                initialWidth :"420px",
                height:"300px"
              });
        } else {
            window.location.reload(false);
        }
    });
};


gigyaHybris.gigyaFunctions.raasClick = function() {
    $(".gigya-raas-link").click(
        function(event) {
            event.preventDefault();
            var id = $(this).attr("data-gigya-id");
            gigya.accounts.showScreenSet(window.gigyaHybris.raas[id]);
        });
};


gigyaHybris.gigyaFunctions.raasEmbed = function() {
    if (gigyaHybris.raas) {
        $.each(gigyaHybris.raas, function(name, params) {
            if(!params.profileEdit && params.containerID){
                gigya.accounts.showScreenSet(params);
            }
            
            if(params.profileEdit && params.containerID){
                gigya.accounts.showScreenSet({
                    screenSet : params.screenSet,
                    startScreen : params.startScreen,
                    containerID : params.containerID,
                    onAfterSubmit : gigyaHybris.gigyaFunctions.raasEditProfile
                });
            }
        });
    }
};

/*
 * Register login events
 */
function gigyaRegister() {
    if (ACC.gigyaUserMode === "raas") {
        gigya.accounts.addEventHandlers({
            onLogin: gigyaHybris.gigyaFunctions.raasLogin,
            onLogout: gigyaHybris.gigyaFunctions.logout,
        });
    }
}

function interceptLogoutClickEvent(e) {
    var target = e.target || e.srcElement;
    if (target.tagName === 'A' && target.getAttribute('href').endsWith('/logout')) {
        gigya.accounts.logout();
    }
}


$(document).ready(function() {
    gigyaRegister();
    gigyaHybris.gigyaFunctions.raasClick();
    gigyaHybris.gigyaFunctions.raasEmbed();
    if (document.addEventListener) {
        document.addEventListener('click', interceptLogoutClickEvent);
    } else if (document.attachEvent) {
        document.attachEvent('onclick', interceptLogoutClickEvent);
    }
});