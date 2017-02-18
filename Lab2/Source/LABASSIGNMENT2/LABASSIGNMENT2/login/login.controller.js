(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$location', 'AuthenticationService', 'FlashService'];
    function LoginController($location, AuthenticationService, FlashService) {
        var maincontrol = this;

        maincontrol.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
            maincontrol.dataLoading = true;
            AuthenticationService.Login(maincontrol.username, maincontrol.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(maincontrol.username, maincontrol.password);
                    $location.path('/');

                } else {
                    FlashService.Error(response.message);
                    maincontrol.dataLoading = false;
                }
            });
        };

    }

})();
