# FinTechCryptoTradingApplication
## A Crypto Trading System using Java Spring Boot and H2 Database.

## [Binance API](https://api.binance.com/api/v3/ticker/bookTicker)
[Binance API Documentation](https://developers.binance.com/docs/binance-spot-api-docs/rest-api#symbol-order-book-ticker)

## [Huobi API](https://api.huobi.pro/market/tickers)
[Huobi API Documentation](https://huobiapi.github.io/docs/spot/v1/en/#get-latest-tickers-for-all-pairs)

## H2 Database
### Access
|H2 Console URL |http://localhost:8080/h2-console/|
|---------------|---------------------------------|
|Driver Class |org.h2.Driver|
|JDBC URL |jdbc:h2:mem:testdb|
|User Name |user|
|Password |password|

### Tables

|Tables|Remarks|
|------|-------|
|COIN_PRICE|Stores the **ASK_PRICE** and **BID_PRICE**, which will be updated every 10 seconds by the App.|
|TRANSACTIONS|Stores all Transactions made by the users<br>**CURRENT_PRICE** will be updated every 10 seconds by the App for all transactions with **OPENED** STATUS.<br>|
|USER|Stores available users.<br>|
|USER_CRYPTO_WALLET|Stores the Crypto Currencies purchased/sold by the user, where balance are the number of units purchased/sold.<br>Balance will be deducted when a user decides to CLOSE the transaction.<br>Should the Balance reach 0, the row will be deleted.<br>|
|USER_WALLET|Stores the account balance for amount of USDT.<br>|

## Endpoints
|End Points|Remarks|Sample Request/Response|
|-|-|-|
|http://localhost:8080/coin/getCoinPrice|To get the latest aggregated coin prices, which are stored in the **COIN_PRICE** database table|<pre>&#13;Request : <br>http://localhost:8080/coin/getCoinPrice<br><br>Response : <br>[&#13;  {&#13;    "id": 1,&#13;    "symbol": "BTCUSDT",&#13;    "bidPrice": 67647.82,&#13;    "askPrice": 67648.01,&#13;    "updatedOn": "2024-06-13T12:30:34.198279"&#13;},&#13;{&#13;    "id": 2,&#13;    "symbol": "ETHUSDT",&#13;    "bidPrice": 3515.64,&#13;    "askPrice": 3515.78,&#13;    "updatedOn": "2024-06-13T12:30:34.202309"&#13;    }&#13;]</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/2c07b992-2299-4892-9dae-cf834f86bea7)|
|http://localhost:8080/user/{username}/walletBalance|To view the USDT Wallet Balance, used for buying/selling, from **USER_WALLET** table|Request : <br>http://localhost:8080/user/user/walletBalance<br><br>Response : <br><pre>&#13;[&#13;  {&#13;    "id": 1,&#13;    "user": "user",&#13;    "symbol": "USDT",&#13;    "accountBalance": 46620.992&#13;  }&#13;]</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/13bd20bc-bbf3-4b32-ac5a-256b69d67923)|
|http://localhost:8080/user/{username}/cryptoWalletBalance|To view the Crypto Currencies Wallet Balance, from **USER_CRYPTO_WALLET** table|Request : <br>http://localhost:8080/user/user/cryptoWalletBalance<br><br>Response : <br><pre>&#13;[&#13;  {&#13;    "id": 2,&#13;    "user": "user",&#13;    "symbol": "BTCUSDT",&#13;    "balance": 0.05&#13;    }&#13;]</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/d785b08b-42a9-4344-a49b-1999541a4b8b)|
|http://localhost:8080/user/{username}/transactions|To view all Transactions, made by the User, from **TRANSACTIONS** table|Request : <br>http://localhost:8080/user/user/transactions<br><br>Response : <br><pre>&#13;[&#13;  {&#13;    "transactionReferenceNumber": 1001,&#13;    "username": "user",&#13;    "coinName": "BTCUSDT",&#13;    "orderType": "BUY",&#13;    "status": "OPEN",&#13;    "units": 0.05,&#13;    "entryPrice": 67597.48,&#13;    "currentPrice": 67572.59,&#13;    "amountPaidFor": 3379.8738,&#13;    "amountSoldFor": 0.0,&#13;    "profitLoss": -24.882812,&#13;    "transactionDate": "2024-06-13T12:36:48.982977",&#13;    "updatedOn": "2024-06-13T12:37:42.603889"&#13;  },&#13;  {&#13;    "transactionReferenceNumber": 1000,&#13;    "username": "user",&#13;    "coinName": "BTCUSDT",&#13;    "orderType": "BUY",&#13;    "status": "CLOSED",&#13;    "units": 0.05,&#13;    "entryPrice": 67625.92,&#13;    "currentPrice": 67643.24,&#13;    "amountPaidFor": 3381.2961,&#13;    "amountSoldFor": 3382.162,&#13;    "profitLoss": 17.320312,&#13;    "transactionDate": "2024-06-13T12:13:37.115693",&#13;    "updatedOn": "2024-06-13T12:18:46.57601"&#13;  }&#13;]</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/b752a18e-74ef-4de8-a6a9-fed31c9e191e)|
|http://localhost:8080/user/{trade}/openTransaction|To buy/sell the supported Crypto Currencies, and update **TRANSACTIONS** table.<br>(Currently supported Crypto Currencies : Ethereum - ETHUSDT and Bitcoin - BTCUSDT)|JSON Request:<br><pre lang="json">{&#13;  "username": "user",&#13;  "coinName": "BTCUSDT",&#13;  "orderType": "buy",&#13;  "units": "0.05"&#13;}</pre> JSON Response:<br><pre lang="json">{&#13;  "transactionReferenceNumber": 1001,&#13;  "username": "user",&#13;  "coinName": "BTCUSDT",&#13;  "orderType": "BUY",&#13;  "status": "OPEN",&#13;  "units": 0.05,&#13;  "entryPrice": 67597.48,&#13;  "currentPrice": 67618.32,&#13;  "amountPaidFor": 3379.8738,&#13;  "amountSoldFor": 0.0,&#13;  "profitLoss": 20.84375,&#13;  "transactionDate": "2024-06-13T12:36:48.982977",&#13;  "updatedOn": "2024-06-13T12:36:48.982977"&#13;}</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/1caacdd2-d2e0-45cb-9b46-cf658e07434e)|
|http://localhost:8080/user/{trade}/closeTransaction|To close the transaction, in **TRANSACTION** table, and route the profit to USER_WALLET table|JSON Request:<br><pre lang="json">{&#13;  "username": "user",&#13;  "coinName": "BTCUSDT",&#13;  "transactionReferenceNumber": "1000"&#13;}</pre> JSON Response:<br><pre lang="json">{&#13;  "transactionReferenceNumber": 1000,&#13;  "username": "user",&#13;  "coinName": "BTCUSDT",&#13;  "orderType": "BUY",&#13;  "status": "CLOSED",&#13;  "units": 0.05,&#13;  "entryPrice": 67625.92,&#13;  "currentPrice": 67643.24,&#13;  "amountPaidFor": 3381.2961,&#13;  "amountSoldFor": 3382.162,&#13;  "profitLoss": 17.320312,&#13;  "transactionDate": "2024-06-13T12:13:37.115693",&#13;  "updatedOn": "2024-06-13T12:18:46.57601"&#13;}</pre>Screenshot :<br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/35c2984f-3066-4ad9-8abf-7293126fbf4a)|


## Technology Used : 
* Java
* H2 Database
* [Binance API](https://api.binance.com/api/v3/ticker/bookTicker)
* [Huobi API](https://api.huobi.pro/market/tickers)

## Note : 
Currently supported symbols : **ETHUSDT, BTCUSDT**.
<br>Add to [CommonConstants.java](https://github.com/mryswah/FinTechCryptoTradingApplication/blob/master/src/main/java/com/fintech/cryptotrading/constant/CommonConstant.java#L21-L25) to enable price aggregation for new additional symbols. <br>**(Assuming that the symbols are supported by both Binance and Huobi APIs)**
<br><br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/af901ca4-0ec7-451b-857a-ba0fe5893f7e)

Two users, user and user1, are created by default.
<br>user has 50000 USDT initial value.
<br>Modify the following [data.sql](https://github.com/mryswah/FinTechCryptoTradingApplication/blob/master/src/main/resources/data.sql#L1-L8) to update the initial database records on server startup.
<br><br>![image](https://github.com/mryswah/FinTechCryptoTradingApplication/assets/36470768/b14e3a7e-419f-4330-ba27-185c30eaa3b8)
<br>

