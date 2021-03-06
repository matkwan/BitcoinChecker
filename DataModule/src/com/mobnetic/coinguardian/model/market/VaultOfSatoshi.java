package com.mobnetic.coinguardian.model.market;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mobnetic.coinguardian.model.CheckerInfo;
import com.mobnetic.coinguardian.model.CurrencyPairInfo;
import com.mobnetic.coinguardian.model.Market;
import com.mobnetic.coinguardian.model.Ticker;
import com.mobnetic.coinguardian.model.currency.Currency;
import com.mobnetic.coinguardian.model.currency.VirtualCurrency;

public class VaultOfSatoshi extends Market {

	private final static String NAME = "VaultOfSatoshi";
	private final static String TTS_NAME = "Vault Of Satoshi";
	private final static String URL = "https://api.vaultofsatoshi.com/public/ticker?order_currency=%1$s&payment_currency=%2$s";
	private final static String URL_CURRENCY_PAIRS = "https://api.vaultofsatoshi.com/public/currency";
	private final static HashMap<String, CharSequence[]> CURRENCY_PAIRS = new LinkedHashMap<String, CharSequence[]>();
	static {
		CURRENCY_PAIRS.put(VirtualCurrency.AUR, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.BC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.BIL, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.BTC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.CGB, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.DGC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.DOGE, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.DRK, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.EMC2, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.FLT, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.FTC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.LTC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.MINT, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.NOBL, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.NVC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.PPC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.QRK, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.VTC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.WDC, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.XPM, new String[]{ Currency.CAD, Currency.USD });
		CURRENCY_PAIRS.put(VirtualCurrency.ZET, new String[]{ Currency.CAD, Currency.USD });
	}
	
	public VaultOfSatoshi() {
		super(NAME, TTS_NAME, CURRENCY_PAIRS);
	}
	
	@Override
	public String getUrl(int requestId, CheckerInfo checkerInfo) {
		return String.format(URL, checkerInfo.getCurrencyBaseLowerCase(), checkerInfo.getCurrencyCounterLowerCase());
	}
	
	@Override
	protected void parseTickerFromJsonObject(int requestId, JSONObject jsonObject, Ticker ticker, CheckerInfo checkerInfo) throws Exception {
		final JSONObject dataObject = jsonObject.getJSONObject("data");
		
		ticker.vol = getDoubleFromMtgoxFormatObject(dataObject, "volume_1day");
		ticker.high = getDoubleFromMtgoxFormatObject(dataObject, "max_price");
		ticker.low = getDoubleFromMtgoxFormatObject(dataObject, "min_price");
		ticker.last = getDoubleFromMtgoxFormatObject(dataObject, "closing_price");
		ticker.timestamp = dataObject.getLong("date");
	}
	
	private double getDoubleFromMtgoxFormatObject(JSONObject jsonObject, String key) throws Exception {
		final JSONObject innerObject = jsonObject.getJSONObject(key);
		return innerObject.getDouble("value");
	}
	
	// ====================
	// Get currency pairs
	// ====================
	@Override
	public String getCurrencyPairsUrl(int requestId) {
		return URL_CURRENCY_PAIRS;
	}
	
	@Override
	protected void parseCurrencyPairsFromJsonObject(int requestId, JSONObject jsonObject, List<CurrencyPairInfo> pairs) throws Exception {
		final JSONArray dataJsonArray = jsonObject.getJSONArray("data");
		final ArrayList<String> virtualCurrencies = new ArrayList<String>();
		final ArrayList<String> currencies = new ArrayList<String>();
		for(int i=0; i<dataJsonArray.length(); ++i) {
			final JSONObject currencyJsonObject = dataJsonArray.getJSONObject(i);
			if(currencyJsonObject.getInt("virtual")!=0)
				virtualCurrencies.add(currencyJsonObject.getString("code"));
			else
				currencies.add(currencyJsonObject.getString("code"));
		}
		
		final int virtualCurrenciesCount = virtualCurrencies.size();
		final int currenciesCount = currencies.size();
		for(int i=0; i<virtualCurrenciesCount; ++i) {
			for(int j=0; j<currenciesCount; ++j) {
				pairs.add(new CurrencyPairInfo(virtualCurrencies.get(i), currencies.get(j), null));
			}
			for(int j=0; j<virtualCurrenciesCount; ++j) {
				if(i!=j)
					pairs.add(new CurrencyPairInfo(virtualCurrencies.get(i), virtualCurrencies.get(j), null));
			}
		}
	}
}
