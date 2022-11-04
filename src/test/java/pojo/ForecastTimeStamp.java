package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastTimeStamp
{
  public int dt;
  public ForecastMain main;
  public int visibility;
  public double pop;
  public String dt_txt;

  public ForecastTimeStamp()
  {

  }

  public ForecastTimeStamp(int dt, ForecastMain main, int visibility, double pop, String dt_txt)
  {
    this.dt = dt;
    this.main = main;
    this.visibility = visibility;
    this.pop = pop;
    this.dt_txt = dt_txt;
  }

  public int getDt()
  {
    return dt;
  }

  public ForecastMain getMain()
  {
    return main;
  }

  public int getVisibility()
  {
    return visibility;
  }

  public double getPop()
  {
    return pop;
  }

  public String getDt_txt()
  {
    return dt_txt;
  }
}
