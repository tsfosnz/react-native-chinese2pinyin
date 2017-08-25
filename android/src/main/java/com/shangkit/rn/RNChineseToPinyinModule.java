//  Copyright © 2017 Tom Silver
//  @email workingasprogrammer@gmail.com.
//  @License MIT

package com.shangkit.rn;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * RNChineseToPinyinModule, to get pinyin for chinese hanzi, it support a
 * common hanzi set, not all of them.
 * <p>
 * According to wikipedia:
 * <p>
 * https://en.wikipedia.org/wiki/CJK_Unified_Ideographs
 * <p>
 * The basic block named CJK Unified Ideographs (4E00–9FFF) contains 20,971 basic Chinese characters in the range U+4E00 through U+9FEA
 */
public class RNChineseToPinyinModule extends ReactContextBaseJavaModule {

    private static final String E_EXCEPTION = "E_EXCEPTION";

    public RNChineseToPinyinModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNChineseToPinyin";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, "");
        constants.put(DURATION_LONG_KEY, "");
        return constants;
    }

    /**
     * getPinYinSync, the method to get pinyin, full or abbreviation, it only support
     * a small set of hanzi, due to the limitation of the class from Android Open Source
     *
     * @param characters a mixed string with any kind character
     * @return the string value of pinyin
     */
    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableMap getPinyinSync(String characters) {

        String full = "";
        String abbr = "";

        WritableMap pinyin = Arguments.createMap();

        try {
            for (int i = 0; i < characters.length(); i++) {

                char c = characters.charAt(i);
                String r = String.valueOf(c);

                // that's chinese, korean etc

                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {

                    // that's chinese only

                    if (c >= 0x4e00 && c <= 0x9fea) {

                        // will get empty string, if not support yet

                        String letter = getSinglePinyin(r);
                        full = full.concat(letter);

                        if (full.length() > 0) {
                            abbr = abbr.concat(String.valueOf(letter.charAt(0)));
                        } else {
                            abbr = abbr.concat("");
                        }
                    }

                } else if (r.matches("^[a-zA-Z0-9]$")) {
                    full = full.concat(r);
                    abbr = abbr.concat(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pinyin.putString("full", full.toUpperCase());
        pinyin.putString("abbr", abbr.toUpperCase());

        return pinyin;
    }


    /**
     * getPinYinSync, the method to get pinyin, full or abbreviation, it only support
     * a small set of hanzi, due to the limitation of the class from Android Open Source
     *
     * @param characters a mixed string with any kind character
     * @param promise    a promise from javascript to resolve or reject the result
     * @return no
     */
    @ReactMethod
    public void getPinyin(final String characters,
                          Promise promise) {


        String full = "";
        String abbr = "";

        WritableMap pinyin = Arguments.createMap();

        try {
            for (int i = 0; i < characters.length(); i++) {

                char c = characters.charAt(i);
                String r = String.valueOf(c);

                // that's chinese, korean etc

                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {

                    // that's chinese only

                    if (c >= 0x4e00 && c <= 0x9fea) {

                        // will get empty string, if not support yet
                        String letter = getSinglePinyin(r);
                        full = full.concat(letter);

                        if (full.length() > 0) {
                            abbr = abbr.concat(String.valueOf(letter.charAt(0)));
                        } else {
                            abbr = abbr.concat("");
                        }
                    }

                } else if (r.matches("^[a-zA-Z0-9]$")) {
                    full = full.concat(r);
                    abbr = abbr.concat(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(E_EXCEPTION, e);
        }

        pinyin.putString("full", full.toUpperCase());
        pinyin.putString("abbr", abbr.toUpperCase());

        promise.resolve(pinyin);
    }

    private String getSinglePinyin(String character) {

        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(character);

        if (!tokens.isEmpty()) {
            for (HanziToPinyin.Token token : tokens) {
                // Log.i("chinese", character);
                // Log.i("pinyin", token.target.toString());
                return token.target.toString();
            }
        }

        return "";
    }


    @ReactMethod
    public void realmTest(final String name,
                          Promise promise) {

        try {

            Realm.init(this.getReactApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().build();
            Realm.setDefaultConfiguration(config);
            // Realm realm = Realm.getDefaultInstance();

            DynamicRealm realm = DynamicRealm.getInstance(config);


            RealmResults<DynamicRealmObject> r = realm.where("Client")
                    .findAll();

            Log.i("TEST_REALM", r.size() + "");


        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(E_EXCEPTION, e);
            return;
        }

        promise.resolve("");
    }
}
