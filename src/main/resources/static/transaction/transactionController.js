angular.module('cryptoTradingApp').controller('transactionController', function($scope, $http, API_URL, $rootScope, $location) {
    console.log("transactionController logging");
    console.log("$rootScope.currentUser : ", $rootScope.currentUser); // access the user's data

    $scope.totalAmount = 0;
    $scope.buy = function(bidPrice, units) {
        console.log("bidPrice : ", bidPrice)
        console.log("units : ", units)
        $scope.totalAmount = (bidPrice * units).toFixed(2);
        $scope.orderType = "BUY"
    };

    $scope.sell = function(askPrice, units) {
        console.log("askPrice : ", askPrice)
        console.log("units : ", units)
        $scope.totalAmount = (askPrice * units).toFixed(2);
        $scope.orderType = "SELL"
    };

    const headers = {
        'Content-Type': 'application/json' // set the content type to JSON
    };

    // Helper function to make POST requests
    function postRequest(endpoint, data) {
        return $http.post(API_URL + endpoint, data, {
                headers: headers
            })
            .then(function(response) {
                return response.data;
            }, function(error) {
                console.error(`Error getting data from ${endpoint}:`, error);
                $scope.error = error.data.errors[0]
                throw error; // Rethrow to handle it where the function is called, if necessary
            });
    }

    $scope.proceed = function(symbol, orderType, units) {
        const jsonData = {
            customerInformation: {
                username: $rootScope.currentUser.username
            },
            transactionInformation: {
                coinName: symbol
            },
            openTransactionRequestBody: {
                orderType: orderType,
                units: units
            }
        };

        postRequest('/trade/openTransaction', jsonData)
            .then(function() {
                $location.path('/user');
            });
    };

           $scope.units = 0;

            $scope.increment = function() {
                $scope.units = ($scope.units * 100 + 1) / 100;
            };

            $scope.decrement = function() {
                if ($scope.units > 0) {
                    $scope.units = ($scope.units * 100 - 1) / 100;
                }
            };

})
    .directive('unitInput', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.find('input').on('keydown', function(event) {
                    if (event.keyCode === 38) { // up arrow
                        scope.increment();
                        scope.$apply();
                    } else if (event.keyCode === 40) { // down arrow
                        scope.decrement();
                        scope.$apply();
                    }
                });
            }
        };


    });