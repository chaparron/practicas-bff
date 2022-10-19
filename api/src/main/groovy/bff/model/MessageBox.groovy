package bff.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeFields = true)
@EqualsAndHashCode
class MessageBox {
    private String icon
    private String titleKey
    private String descriptionKey

    MessageBox(String icon, String titleKey, String descriptionKey) {
        this.icon = icon // TODO: EVALUAR SI ESTO LO VAMOS A MANDAR DE BACK
        this.titleKey = titleKey
        this.descriptionKey = descriptionKey
    }

    String getIcon() {
        return icon
    }

    String getTitleKey() {
        return titleKey
    }

    String getDescriptionKey() {
        return descriptionKey
    }
}