package bff.model

import sun.util.locale.LanguageTag

class I18N {

    Map<String, String> entries
    String defaultEntry

    String getOrDefault(LanguageTag languageTag) {
        entries.get(languageTag.language, defaultEntry)
    }

}
