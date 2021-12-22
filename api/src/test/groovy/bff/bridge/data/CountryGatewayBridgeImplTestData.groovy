package bff.bridge.data

abstract class CountryGatewayBridgeImplTestData {

    protected static String countryEsPublicStr =
            "{\n" +
                    "    \"id\": \"es\",\n" +
                    "    \"config\": [\n" +
                    "        {\n" +
                    "            \"key\": \"name\",\n" +
                    "            \"value\": \"España\",\n" +
                    "            \"private\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"name-en\",\n" +
                    "            \"value\": \"Spain\",\n" +
                    "            \"private\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"locale\",\n" +
                    "            \"value\": \"es_ES\",\n" +
                    "            \"private\": false\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"

    protected static String countryArPublicStr =
            "{\n" +
                    "    \"id\": \"ar\",\n" +
                    "    \"config\": [\n" +
                    "        {\n" +
                    "            \"key\": \"name\",\n" +
                    "            \"value\": \"Argentina\",\n" +
                    "            \"private\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"name-en\",\n" +
                    "            \"value\": \"Argentina\",\n" +
                    "            \"private\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"locale\",\n" +
                    "            \"value\": \"es_AR\",\n" +
                    "            \"private\": false\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"

    protected static String homeCountriesResponse = "[\n" +
            "  {\n" +
            "    \"id\": \"eg\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Egipto\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "    {\n" +
            "      \"key\": \"timezone\",\n" +
            "      \"value\": \"Africa/Cairo\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalId\",\n" +
            "      \"value\": \"TIN\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMask\",\n" +
            "      \"value\": \"D*\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMaskRegex\",\n" +
            "      \"value\": \"^[a-zA-Z0-9]*\$\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "      {\n" +
            "        \"key\": \"locale\",\n" +
            "        \"value\": \"ar-EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"مصر\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Egypt\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-es\",\n" +
            "        \"value\": \"Egipto\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300eg.png\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"tyc\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-T-C?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"pp\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-P-P?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"cookies\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-P-C?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"faqs\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"about\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"operation\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"complaint\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ph\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Philippines\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "    {\n" +
            "      \"key\": \"timezone\",\n" +
            "      \"value\": \"Asia/Manila\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalId\",\n" +
            "      \"value\": \"TIN\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMask\",\n" +
            "      \"value\": \"000000009999\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMaskRegex\",\n" +
            "      \"value\": \"^\\\\d{8,12}\$\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "      {\n" +
            "        \"key\": \"locale\",\n" +
            "        \"value\": \"ms-MY\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"فيلبيني\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Philippines\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-es\",\n" +
            "        \"value\": \"Filipinas\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300ph.png\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"tyc\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Philipines-T-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"pp\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Philipines-P-P?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"cookies\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Algeria-P-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ma\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Morocco\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "    {\n" +
            "      \"key\": \"timezone\",\n" +
            "      \"value\": \"Africa/Casablanca\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalId\",\n" +
            "      \"value\": \"ICE\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMask\",\n" +
            "      \"value\": \"000000000000000\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMaskRegex\",\n" +
            "      \"value\": \"^\\\\d{15}\$\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "      {\n" +
            "        \"key\": \"locale\",\n" +
            "        \"value\": \"ar-MA\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"المغرب\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Morocco\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-es\",\n" +
            "        \"value\": \"Marruecos\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300ma.png\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  }]"

    protected static final publicCountryResponse = "{\n" +
            "  \"id\": \"ru\",\n" +
            "  \"config\": [\n" +
            "    {\n" +
            "      \"key\": \"name\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"timezone\",\n" +
            "      \"value\": \"Europe/Moscow\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalId\",\n" +
            "      \"value\": \"INN\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMask\",\n" +
            "      \"value\": \"000000000099999\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"legalMaskRegex\",\n" +
            "      \"value\": \"^\\\\d{10,15}\$\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-ar\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-en\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-es\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-my\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-pt\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-ru\",\n" +
            "      \"value\": \"Россия\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-vn\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"name-zh\",\n" +
            "      \"value\": \"Rusia\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"country_code\",\n" +
            "      \"value\": \"+7\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"currency\",\n" +
            "      \"value\": \"₽\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"currency_code\",\n" +
            "      \"value\": \"RUB\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"lat\",\n" +
            "      \"value\": \"55.6641779\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"lng\",\n" +
            "      \"value\": \"37.1684867\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"language\",\n" +
            "      \"value\": \"ru\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"locale\",\n" +
            "      \"value\": \"ru-RU\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"wabipay_enabled\",\n" +
            "      \"value\": \"true\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"wabipay_wabicredits_enabled\",\n" +
            "      \"value\": \"true\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"wabipay_money_enabled\",\n" +
            "      \"value\": \"true\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"wabipay_convert_wc_to_money_when_releasing\",\n" +
            "      \"value\": \"false\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"whatsapp_number\",\n" +
            "      \"value\": \"541161290635\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"phone_number\",\n" +
            "      \"value\": \"+541120400002\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"direction\",\n" +
            "      \"value\": \"ltr\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"service_fee\",\n" +
            "      \"value\": \"0.00\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"service_fee_type\",\n" +
            "      \"value\": \"WABICREDITS_PERCENTAGE\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"display_fee_on_supplier_adm\",\n" +
            "      \"value\": \"false\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"flag\",\n" +
            "      \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300ru.png\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"tyc\",\n" +
            "      \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Russia-T-C?language=ru\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"pp\",\n" +
            "      \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Russia-P-P?language=ru\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"cookies\",\n" +
            "      \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Russia-P-C?language=ru\",\n" +
            "      \"private\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"faqs\",\n" +
            "      \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ru\",\n" +
            "      \"private\": false\n" +
            "    }\n" +
            "  ],\n" +
            "  \"enabled\": true\n" +
            "}"
}
