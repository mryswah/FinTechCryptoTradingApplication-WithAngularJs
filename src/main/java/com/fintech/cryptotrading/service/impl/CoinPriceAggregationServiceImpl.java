package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.constant.CommonConstant;
import com.fintech.cryptotrading.model.CoinPrice;
import com.fintech.cryptotrading.model.Transactions;
import com.fintech.cryptotrading.model.binance.BinanceResponse;
import com.fintech.cryptotrading.model.exceptions.ApiException;
import com.fintech.cryptotrading.model.huobi.HuobiData;
import com.fintech.cryptotrading.model.huobi.HuobiResponse;
import com.fintech.cryptotrading.repository.CoinPriceRepository;
import com.fintech.cryptotrading.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class CoinPriceAggregationServiceImpl{

    private final RestTemplate restTemplate;
    private final CoinPriceRepository coinPriceRepository;
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public CoinPriceAggregationServiceImpl(RestTemplate restTemplate, CoinPriceRepository coinPriceRepository, TransactionsRepository transactionsRepository) {
        this.restTemplate = restTemplate;
        this.coinPriceRepository = coinPriceRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void retrieveAndStoreCoinPrices() throws ApiException {
        // Retrieve prices from Binance
        List<BinanceResponse> binancePrices = callBinaceApi();

        // Retrieve prices from Huobi
        List<HuobiData> houbiPrices = callHuobiApi();

        // To update the Ask/Bid Price in coin_price table
        for (BinanceResponse bp : binancePrices) {
            for (HuobiData hp : houbiPrices) {
                if (bp.getSymbol().equalsIgnoreCase(hp.getSymbol())) {
                    CoinPrice coinPrice = coinPriceRepository.findByName(bp.getSymbol());
                    if(coinPrice==null){
                        coinPrice = new CoinPrice();
                        coinPrice.setSymbol(bp.getSymbol());
                    }
                    float bid = Math.min(bp.getBidPrice(), hp.getBid());
                    float ask = Math.max(bp.getAskPrice(), hp.getAsk());
                    coinPrice.setBidPrice(bid);
                    coinPrice.setAskPrice(ask);
                    coinPriceRepository.save(coinPrice);
                }
            }
        }
        // To update the Profit/Loss & Current Price in transaction table, for transactions which are OPEN
        List<CoinPrice> coinPrice = coinPriceRepository.findAll();
        List<Transactions> transactions = transactionsRepository.findAllOpen(CommonConstant.OPEN);
        for (CoinPrice cp : coinPrice) {
            for(Transactions t : transactions){
                if (t.getCoinName().equals(cp.getSymbol())) {
                    if(t.getOrderType().equalsIgnoreCase(CommonConstant.BUY)){
                        t.setCurrentPrice(cp.getBidPrice());
                    } else {
                        t.setCurrentPrice(cp.getAskPrice());
                    }
                    t.setProfitLoss(t.getCurrentPrice() - t.getEntryPrice());
                    transactionsRepository.save(t);
                }
            }
        }
    }

    private List<BinanceResponse> callBinaceApi() throws ApiException {
        long startTime = System.nanoTime();
        List<BinanceResponse> binancePrices;
        try {
            ResponseEntity<BinanceResponse[]>  binanceResponse = restTemplate.getForEntity(CommonConstant.BINANCE_URL, BinanceResponse[].class);
            binancePrices = Arrays.stream(binanceResponse.getBody())
                    .filter(coin -> CommonConstant.ETHUSDT.equalsIgnoreCase(coin.getSymbol()) || CommonConstant.BTCUSDT.equalsIgnoreCase(coin.getSymbol()))
                    .sorted(Comparator.comparing(BinanceResponse::getSymbol))
                    .toList();
        } catch (RestClientException ex){
            throw new ApiException("Error with Binance Api : "+ ex.getMessage());
        }
        double estimatedTime = (double) (System.nanoTime() - startTime)/CommonConstant.BILLION;
        System.out.println("Time taken (in seconds) for Binance Api call : " + estimatedTime);
        return binancePrices;
    }

    private List<HuobiData> callHuobiApi() throws ApiException {
        long startTime = System.nanoTime();
        List<HuobiData> huobiData;
        try {
            ResponseEntity<HuobiResponse> huobiResponse = restTemplate.getForEntity(CommonConstant.HUOBI_URL, HuobiResponse.class);
            huobiResponse.getBody().getData().stream().filter(coin -> CommonConstant.ETHUSDT.equals(coin.getSymbol()) || CommonConstant.BTCUSDT.equals(coin.getSymbol()));
            huobiData = huobiResponse.getBody().getData().stream()
                    .filter(coin -> CommonConstant.ETHUSDT.equalsIgnoreCase(coin.getSymbol()) || CommonConstant.BTCUSDT.equalsIgnoreCase(coin.getSymbol()))
                    .sorted(Comparator.comparing(HuobiData::getSymbol))
                    .toList();
        } catch (RestClientException ex) {
            throw new ApiException("Error with Huobi Api : "+ ex.getMessage());
        }
        double estimatedTime = (double) (System.nanoTime() - startTime)/CommonConstant.BILLION;
        System.out.println("Time taken (in seconds) for Huobi Api call : " + estimatedTime);
        return huobiData;
    }

}
