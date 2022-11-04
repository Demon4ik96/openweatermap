import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.hamcrest.number.OrderingComparison;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.CurrentWeatherCoords;
import util.Specifications;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CurrentWeatherTest extends Specifications
{
  @BeforeClass
  public void setupRequestSpecification()
  {
    final String URL = "https://api.openweathermap.org";
    Specifications.setSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
  }

  @DataProvider(name="CoordinateDataProvider")
  @BeforeClass(dependsOnMethods = { "setupRequestSpecification" })
  public Object[][] getLondonCoordinates()
  {
    final Map<String, String> requestParams = Map.of("q", "London", "limit", "5");

    List<Map<String, Object>> responseBody = RestAssured
        .given()
        .queryParams(requestParams)
        .when()
        //geocoding api endpoint for getting coordinates by CityName
        .get("/geo/1.0/direct")
        .then()
        .extract()
        .body()
        .as(new TypeRef<>() {});

    Optional<Object> latitude =
        responseBody.stream().filter(jsonMap->jsonMap.get("country").equals("GB")).map(jsonMap->jsonMap.get("lat")).findFirst();

    Optional<Object> longtitude =
        responseBody.stream().filter(jsonMap->jsonMap.get("country").equals("GB")).map(jsonMap->jsonMap.get("lon")).findFirst();

    if(latitude.isPresent() && longtitude.isPresent())
    {
      return new Object[][]{{latitude.get(), longtitude.get()}};
    }
    return new Object[][]{};
  }

  //verify that coordinates from request parameters equals to coordinate in response rounded to two decimals places.
  @Test(dataProvider = "CoordinateDataProvider")
  public void verifyCoordinates(Double lat, Double lon)
  {
    CurrentWeatherCoords coords = RestAssured
        .given()
        .queryParam("lat", lat)
        .queryParam("lon", lon)
        .when()
        .get("/data/2.5/weather")
        .then().log().all()
        .extract().body().jsonPath().getObject("coord", CurrentWeatherCoords.class);

    String requestLat = new DecimalFormat("0.00").format(lat);
    String requestLon = new DecimalFormat("0.00").format(lon);
    String responseLat = new DecimalFormat("0.00").format(coords.getLat());
    String responseLon = new DecimalFormat("0.00").format(coords.getLon());

    Assert.assertEquals(requestLat, responseLat);
    Assert.assertEquals(requestLon, responseLon);
  }

  //verify that date and time in header response equal to current date and time.
  @Test(dataProvider = "CoordinateDataProvider")
  public void verifyDateTimeInHeader(Double lat, Double lon)
  {
    RestAssured
        .given()
        .queryParam("lat", lat)
        .queryParam("lon", lon)
        .when()
        .get("/data/2.5/weather")
        .then()
        .header("Date", date -> LocalDate.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME),
            OrderingComparison.comparesEqualTo(LocalDate.now(ZoneOffset.UTC)));
  }

  @Test(dataProvider = "CoordinateDataProvider")
  public void verifyXmlMode(Double lat, Double lon)
  {
    RestAssured
        .given()
        .queryParam("lat", lat)
        .queryParam("lon", lon)
        .queryParam("mode", "xml")
        .when()
        .get("/data/2.5/weather")
        .then()
        .contentType(ContentType.XML);
  }
}
