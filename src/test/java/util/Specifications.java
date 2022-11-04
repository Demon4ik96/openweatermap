package util;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.Map;

public abstract class Specifications
{
  public static RequestSpecification requestSpec(String url)
  {
    return new RequestSpecBuilder()
        .setBaseUri(url)
        .setContentType(ContentType.JSON)
        .addQueryParams(Map.of("appid", "7411600e014b0376c0cceece0f4f6c5c"))
        .build();
  }

  public static ResponseSpecification responseSpec200()
  {
    return new ResponseSpecBuilder()
        .expectStatusCode(HttpStatus.SC_OK)
        .build();
  }

  public static void setSpecification(RequestSpecification request, ResponseSpecification response)
  {
    RestAssured.requestSpecification = request;
    RestAssured.responseSpecification = response;
  }
}
