package bff.mapper

interface CountryMapper {

    /**
     * Build Country Object from original API params
     * @param countryId The country id
     * @param params The original API parameters
     * @return Country
     */
    def buildCountryFromParams(
            String countryId,
            ArrayList params
    )

    /**
     * Build Country Object from original API params
     * @param map The original map for countries API parameters
     * @return Country List
     */
    def buildCountryListFromMapParams(
            LinkedHashMap map
    );
}