import io.restassured.RestAssured;
import org.apache.commons.validator.GenericValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.ForecastTimeStamp;
import util.Specifications;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForecastTest extends Specifications
{
  private static final String URL = "https://api.openweathermap.org/data/2.5/forecast";
  private static final Map<String, Double> IF_COORDINATES = Map.of("lat", 48.9225224, "lon", 24.7103188);

  @BeforeClass
  public void setupRequestSpecification()
  {
    Specifications.setSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
  }

  @Test
  public void verifyDtSorted()
  {
    List<ForecastTimeStamp> forecasts = RestAssured
        .given()
        .queryParams(IF_COORDINATES)
        .when()
        .get()
        .then()
        .extract().body().jsonPath().getList("list", ForecastTimeStamp.class);

    List<Integer> dtFromForecasts = forecasts.stream().map(ForecastTimeStamp::getDt).collect(Collectors.toList());
    List<Integer> dtSorted = dtFromForecasts.stream().sorted().collect(Collectors.toList());
    Assert.assertEquals(dtSorted, dtFromForecasts);
  }

  @Test
  public void verifyTempBetweenMinAndMax()
  {
    List<ForecastTimeStamp> forecasts = RestAssured
        .given()
        .queryParams(IF_COORDINATES)
        //use parameters to convert to metric system(Celsium)
        .queryParam("units", "metric")
        .when()
        .get()
        .then()
        .extract().body().jsonPath().getList("list", ForecastTimeStamp.class);

    boolean isAllTempCorrect = forecasts.stream().map(ForecastTimeStamp::getMain).allMatch(forecastMain ->
    {
      double temp = forecastMain.getTemp();
      double maxTemp = forecastMain.getTemp_max();
      double minTemp = forecastMain.getTemp_min();
      return temp >= minTemp && temp <= maxTemp;
    });
    Assert.assertTrue(isAllTempCorrect);
  }

  @Test
  public void verifyDateTimePattern()
  {
    String dateTimePattern = "yyyy-MM-dd HH:mm:SS";

    List<ForecastTimeStamp> forecasts = RestAssured
        .given()
        .queryParams(IF_COORDINATES)
        .when()
        .get()
        .then()
        .extract().body().jsonPath().getList("list", ForecastTimeStamp.class);

    Assert.assertTrue(forecasts.stream().map(ForecastTimeStamp::getDt_txt)
        .allMatch(dateTimeText-> GenericValidator.isDate(dateTimeText, dateTimePattern, true)));
  }
}
