angular.module('cryptoTradingApp').controller('coinPriceController', function($scope, $http, API_URL) {
        console.log("coinPriceController logging")
        $scope.showBuySellCard = false;
        $scope.selectedCoin = {};

        $http.get(API_URL + '/coin/getCoinPrice').then(function(response) {
            $scope.coinPrice = response;
        });

        $scope.displayTradingCard = function(coin) {
            console.log("Symbol clicked:", coin);
            // do something with the symbol
            $scope.showTradingCard = true;
            $scope.coin = coin;
        };