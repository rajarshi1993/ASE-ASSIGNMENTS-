(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['UserService', '$location', '$rootScope', 'FlashService'];
    function RegisterController(UserService, $location, $rootScope, FlashService) {
        var maincontrol = this;

        maincontrol.register = register;

        function register() {
            maincontrol.dataLoading = true;
            UserService.Create(maincontrol.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Successful registration', true);
                        $location.path('/login');
                    } else {
                        FlashService.Error(response.message);
                        maincontrol.dataLoading = false;
                    }
                });
        }
    }

})();
