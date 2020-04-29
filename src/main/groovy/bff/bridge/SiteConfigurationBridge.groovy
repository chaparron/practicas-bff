package bff.bridge

import bff.model.BannerDialogResult

interface SiteConfigurationBridge {

    BannerDialogResult getBannerDialog(String accessToken)

}
