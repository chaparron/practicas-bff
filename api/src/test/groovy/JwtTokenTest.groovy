import bff.DecoderName
import bff.InvalidToken
import bff.JwtToken
import org.junit.Assert
import org.junit.Test

class JwtTokenTest {

    @Test
    void can_decode_entity_id_from_token(){
        JwtToken.fromString("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6MTc0OTcsInVzZXJuYW1lIjpudWxsLCJmaXJzdE5hbWUiOm51bGwsImxhc3ROYW1lIjpudWxsLCJwaG9uZSI6bnVsbCwiY3JlZGVudGlhbHMiOm51bGwsInByb2ZpbGVzIjpudWxsLCJjb3VudHJpZXMiOm51bGwsImNyZWF0ZWQiOm51bGwsImFjY2VwdFdoYXRzQXBwIjp0cnVlfSwiYWNjZXB0ZWQiOjE2MTM4MDk5MDkwMDB9LCJlbnRpdHlJZCI6IjE1NTg1Iiwic3RhdGUiOm51bGwsImV4cCI6MTYyMDk1NzUxNywidXNlciI6eyJpZCI6MTc0OTcsInVzZXJuYW1lIjoicm9tYW4tcHJva0B5YW5kZXgucnUiLCJwcm9maWxlcyI6W3siaWQiOjgsIm5hbWUiOiJGRV9DVVNUT01FUiIsImF1dGhvcml0aWVzIjpudWxsfV0sImZpcnN0TmFtZSI6ItCi0LXRgdGCIiwibGFzdE5hbWUiOiLQotC10YHRgtC-0LLRi9C5IiwiY291bnRyaWVzIjpbeyJpZCI6InJ1IiwibmFtZSI6IlJ1c2lhIn1dfSwiYXV0aG9yaXRpZXMiOlsiRkVfV0VCIl0sImp0aSI6IjRhMDZiNTQxLWE4OGEtNGQxMy04NTgxLWRiNzU5MDM1YjFkYSIsImNsaWVudF9pZCI6ImludGVybmFsX2FwaSJ9.MHG-0y1QtTAbzcgshBvrXZDfH7RQ3QktKKD6l16lZIo",
                DecoderName.ENTITY_ID)
    }

    @Test
    void can_decode_username_from_token(){
        JwtToken.fromString("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6MTc0OTcsInVzZXJuYW1lIjpudWxsLCJmaXJzdE5hbWUiOm51bGwsImxhc3ROYW1lIjpudWxsLCJwaG9uZSI6bnVsbCwiY3JlZGVudGlhbHMiOm51bGwsInByb2ZpbGVzIjpudWxsLCJjb3VudHJpZXMiOm51bGwsImNyZWF0ZWQiOm51bGwsImFjY2VwdFdoYXRzQXBwIjp0cnVlfSwiYWNjZXB0ZWQiOjE2MTM4MDk5MDkwMDB9LCJlbnRpdHlJZCI6IjE1NTg1Iiwic3RhdGUiOm51bGwsImV4cCI6MTYyMDk1NzUxNywidXNlciI6eyJpZCI6MTc0OTcsInVzZXJuYW1lIjoicm9tYW4tcHJva0B5YW5kZXgucnUiLCJwcm9maWxlcyI6W3siaWQiOjgsIm5hbWUiOiJGRV9DVVNUT01FUiIsImF1dGhvcml0aWVzIjpudWxsfV0sImZpcnN0TmFtZSI6ItCi0LXRgdGCIiwibGFzdE5hbWUiOiLQotC10YHRgtC-0LLRi9C5IiwiY291bnRyaWVzIjpbeyJpZCI6InJ1IiwibmFtZSI6IlJ1c2lhIn1dfSwiYXV0aG9yaXRpZXMiOlsiRkVfV0VCIl0sImp0aSI6IjRhMDZiNTQxLWE4OGEtNGQxMy04NTgxLWRiNzU5MDM1YjFkYSIsImNsaWVudF9pZCI6ImludGVybmFsX2FwaSJ9.MHG-0y1QtTAbzcgshBvrXZDfH7RQ3QktKKD6l16lZIo",
                DecoderName.USERNAME)
    }

    @Test(expected = InvalidToken.class)
    void countryFromStringNo3FieldThrowsInvalidTokenTest() throws InvalidToken {
        JwtToken.countryFromString("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.MHG-0y1QtTAbzcgshBvrXZDfH7RQ3QktKKD6l16lZIo")
    }

    @Test(expected = InvalidToken.class)
    void countryFromStringIllegalArgumentThrowsInvalidTokenTest() throws InvalidToken {
        JwtToken.countryFromString("bad.token.test")
    }

    @Test
    void countryFromStringArTest() {
        String expectedCountryId = "ar"
        String arToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        String actualCountryId = JwtToken.countryFromString(arToken)
        Assert.assertEquals(expectedCountryId, actualCountryId)
    }
}
