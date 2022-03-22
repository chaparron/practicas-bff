package bff.bridge

import bff.bridge.data.CustomerBridgeImplTestData
import bff.bridge.http.CustomerBridgeImpl
import bff.model.Customer
import bff.model.UpdateStoreException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.*
import org.springframework.web.client.RestOperations
import wabi2b.sdk.api.DetailedException
import wabi2b.sdk.api.Wabi2bSdk

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@RunWith(MockitoJUnitRunner.class)
class CustomerBridgeImplTest extends CustomerBridgeImplTestData {

    @Mock
    RestOperations http

    @Mock
    Wabi2bSdk wabi2bSdk

    @InjectMocks
    private CustomerBridgeImpl customerBridge = new CustomerBridgeImpl(root: new URI("http://localhost:3000/"), wabi2bSdk: wabi2bSdk)

    @Test
    void getProfileCustomerTest() {
        String token = "mockToken"
        RequestEntity requestEntity = RequestEntity.method(HttpMethod.GET, customerBridge.root.resolve("/customer/me"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer ${token}")
                .build()

        Mockito.when(http.<Customer> exchange(requestEntity, Customer))
                .thenReturn(new ResponseEntity<Customer>(CUSTOMER, HttpStatus.OK))

        def response = customerBridge.myProfile(token)

        Assert.assertNotNull(response)
        Assert.assertEquals(token, response.accessToken)

        Mockito.verify(http, Mockito.times(1)).exchange(requestEntity, Customer)
    }

    @Test(expected = UpdateStoreException.class)
    void enableStoreShouldThrowUpdateStoreException() {
        def jwt = "jwt"
        def storeId = "1"
        def mockErrorBody = new HashMap<String, Object>()
        mockErrorBody.put("status", 401)
        mockErrorBody.put("message", "STORE_DOES_NOT_BELONGS_TO_CUSTOMER")

        Mockito.when(wabi2bSdk.enableStore(storeId,jwt)).thenThrow(new DetailedException(mockErrorBody, 401))

        customerBridge.enableStore(jwt, storeId)
    }
}