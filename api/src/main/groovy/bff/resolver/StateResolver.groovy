package bff.resolver

import bff.model.State
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import wabi2b.sdk.regional.RegionalConfigSdk

@Component
class StateResolver implements GraphQLResolver<State> {

    @Autowired
    RegionalConfigSdk regionalConfigSdk

    String name(State state){
        if(state.name == null && state.countryId != null){
            regionalConfigSdk.findStatesForCountry(state.countryId).find{it.isoCode == state.id}?.name
        }else{
            state.name
        }
    }

}