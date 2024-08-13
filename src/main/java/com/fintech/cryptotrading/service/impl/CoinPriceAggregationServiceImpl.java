package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.constant.CommonConstant;
import com.fintech.cryptotrading.model.CoinPrice;
import com.fintech.cryptotrading.model.Transactions;
import com.fintech.cryptotrading.repository.CoinPriceRepository;
import com.fintech.cryptotrading.repository.TransactionsRepository;
import com.fintech.cryptotrading.response.binance.BinanceResponse;
import com.fintech.cryptotrading.response.huobi.HuobiData;
import com.fintech.cryptotrading.response.huobi.HuobiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CoinPriceAggregationServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(CoinPriceAggregationServiceImpl.class);

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
    public void retrieveAndStoreCoinPrices() {
        long startTime = System.nanoTime();
        // Retrieve prices from Binance
        List<BinanceResponse> binancePrices = callBinaceApi();

        // Retrieve prices from Huobi
        List<HuobiData> houbiPrices = callHuobiApi();

        // Assuming either one of the API returns no response, we'll take the price from the other API
        if (binancePrices == null || houbiPrices == null) {
            updateCoinPrices(binancePrices, CommonConstant.BINANCE);
            updateCoinPrices(houbiPrices, CommonConstant.HUOBI);
        }

        // Assuming both APIs return a successful response
        if (binancePrices != null && houbiPrices != null) {
            Map<String, HuobiData> houbiPricesMap = new HashMap<>();
            for (HuobiData hp : houbiPrices) {
                houbiPricesMap.put(hp.getSymbol().toUpperCase(), hp);
            }

            // To update the Ask/Bid Price in coin_price table
            for (BinanceResponse bp : binancePrices) {
                String symbol = bp.getSymbol().toUpperCase();
                if (houbiPricesMap.containsKey(symbol)) {
                    HuobiData hp = houbiPricesMap.get(symbol);
                    CoinPrice coinPrice = coinPriceRepository.findByName(symbol);
                    if (coinPrice == null) {
                        coinPrice = new CoinPrice();
                        coinPrice.setSymbol(symbol);
                    }
                    float bid = Math.min(bp.getBidPrice(), hp.getBid());
                    float ask = Math.max(bp.getAskPrice(), hp.getAsk());
                    coinPrice.setBidPrice(bid);
                    coinPrice.setAskPrice(ask);
                    coinPriceRepository.saveAndFlush(coinPrice);
                }
            }
        }

        // To update the Profit/Loss & Current Price in transaction table, for transactions which are OPEN
      List<Transactions> transactions = transactionsRepository.findAllOpen(CommonConstant.OPEN);
        if (!transactions.isEmpty()) {
            Map<String, CoinPrice> coinPriceMap = coinPriceRepository.findAll().stream()
                    .collect(Collectors.toMap(CoinPrice::getSymbol, Function.identity()));
            List<Transactions> updatedTransactions = transactions.stream().map(
                    t -> {
                        CoinPrice cp = coinPriceMap.get(t.getCoinName());
                        if (cp != null) {
                            if (t.getOrderType().equalsIgnoreCase(CommonConstant.BUY)) {
                                t.setCurrentPrice(cp.getBidPrice());
                            } else {
                                t.setCurrentPrice(cp.getAskPrice());
                            }
                            t.setProfitLoss(t.getCurrentPrice() - t.getEntryPrice());
                        }
                        return t;
                    }).collect(Collectors.toList());

            transactionsRepository.saveAll(updatedTransactions);
        }

        double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
        log.info("Time taken (in seconds) for Scheduler : {}", estimatedTime);
    }

    private List<BinanceResponse> callBinaceApi() {
        long startTime = System.nanoTime();
        List<BinanceResponse> binancePrices;
        try {
            ResponseEntity<BinanceResponse[]> binanceResponse = restTemplate.getForEntity(CommonConstant.BINANCE_URL, BinanceResponse[].class);
            binancePrices = Arrays.stream(Objects.requireNonNull(binanceResponse.getBody()))
                    .filter(coin -> Arrays.stream(CommonConstant.SupportedSymbols.values())
                            .anyMatch(symbol -> symbol.getSymbol().equalsIgnoreCase(coin.getSymbol())))
                    .sorted(Comparator.comparing(BinanceResponse::getSymbol))
                    .toList();

        } catch (RestClientException ex) {
            log.error("Error with Binance Api : {}", ex.getMessage());
            return null;
        }
        double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
        log.info("Time taken (in seconds) for Binance Api call {}", estimatedTime);
        return binancePrices;
    }

    private List<HuobiData> callHuobiApi() {
        long startTime = System.nanoTime();
        List<HuobiData> huobiData;
        try {
            ResponseEntity<HuobiResponse> huobiResponse = restTemplate.getForEntity(CommonConstant.HUOBI_URL, HuobiResponse.class);
            huobiData = Objects.requireNonNull(huobiResponse.getBody()).getData().stream()
                    .filter(coin -> Arrays.stream(CommonConstant.SupportedSymbols.values())
                            .anyMatch(symbol -> symbol.getSymbol().equalsIgnoreCase(coin.getSymbol())))
                    .sorted(Comparator.comparing(HuobiData::getSymbol))
                    .toList();

        } catch (RestClientException ex) {
            log.error("Error with Huobi Api : {}", ex.getMessage());
            return null;
        }
        double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
        log.info("Time taken (in seconds) for Huobi Api call {}", estimatedTime);
        return huobiData;
    }

    private void updateCoinPrices(List<?> prices, String api) {
        if (prices != null) {
            for (Object price : prices) {
                String symbol;
                float bid;
                float ask;
                if (api.equals(CommonConstant.BINANCE)) {
                    BinanceResponse bp = (BinanceResponse) price;
                    symbol = bp.getSymbol();
                    bid = bp.getBidPrice();
                    ask = bp.getAskPrice();
                } else {
                    HuobiData hp = (HuobiData) price;
                    symbol = hp.getSymbol();
                    bid = hp.getBid();
                    ask = hp.getAsk();
                }
                CoinPrice coinPrice = coinPriceRepository.findByName(symbol.toUpperCase());
                if (coinPrice == null) {
                    coinPrice = new CoinPrice();
                    coinPrice.setSymbol(symbol.toUpperCase());
                }
                coinPrice.setBidPrice(bid);
                coinPrice.setAskPrice(ask);
                coinPriceRepository.saveAndFlush(coinPrice);
            }
        }
    }
}
