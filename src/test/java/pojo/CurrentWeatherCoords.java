package pojo;

public class CurrentWeatherCoords
{
  private Double lon;
  private Double lat;

  public CurrentWeatherCoords()
  {
  }


  public CurrentWeatherCoords(Double lon, Double lat)
  {
    this.lon = lon;
    this.lat = lat;
  }

  public Double getLon()
  {
    return lon;
  }

  public Double getLat()
  {
    return lat;
  }
}
