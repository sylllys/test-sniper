package sylllys.test.sniper.utilities;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;

public class TestEndpoint {

  protected String endPointURL = null;
  protected Response response = null;
  protected RequestSpecification request = null;


  public Response getResponse() {
    return this.response;
  }

  public void constructRequest(String url, HashMap<String, String> requestHeaders,
      HashMap<String, String> queryParams, Object body) throws Exception {

    request = RestAssured.given();

    endPointURL = url;

    if (requestHeaders != null) {
      for (String key : requestHeaders.keySet()) {
        requestHeaders.put(key, requestHeaders.get(key));
      }

      while (requestHeaders.values().remove(null)) {
        ;
      }

      request.headers(requestHeaders);
    }
    if (body != null) {
      request.body(body);
    }

    if (queryParams != null) {
      for (
          String key : queryParams.keySet()) {
        queryParams.put(key, queryParams.get(key));
      }

      while (queryParams.values().

          remove(null)) {
        ;
      }

      request.queryParams(queryParams);
    }

  }

  public void sendRequest(HttpMethods method) throws Exception {

    int retry = 0;
    do {
      switch (method) {
        case GET:
          response = request.get(endPointURL);
          break;
        case PUT:
          response = request.put(endPointURL);
          break;
        case POST:
          response = request.post(endPointURL);
          break;
        case DELETE:
          response = request.delete(endPointURL);
          break;
        case PATCH:
          response = request.patch(endPointURL);
          break;
      }
      if (response.getStatusCode() == 500 && retry < 5) {
        Thread.sleep(1000 * 10);
        retry++;
        System.out.println("Retrying request " + retry);
      } else {
        break;
      }
    } while (true);
  }

}
