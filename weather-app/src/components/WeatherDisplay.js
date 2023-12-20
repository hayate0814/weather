// WeatherDisplay.js
import React from "react";

const WeatherDisplay = ({ cityName, forecasts }) => {
  // データをログに表示
  console.log("Forecasts Data:", forecasts);

  return (
    <div className="weather-container">
      <h2 className="weather-title">{`Weather Forecast for ${cityName}`}</h2>
      <div className="weather-info">
        <table className="weather-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Max Temperature (°C)</th>
              <th>Min Temperature (°C)</th>
              <th>Condition</th>
            </tr>
          </thead>
          <tbody>
            {forecasts.map((forecast, index) => {
              // 各予報の最高気温と最低気温をログに表示
              console.log(`Forecast ${index + 1}: Max Temp - ${forecast.temperature.max ? forecast.temperature.max.celsius : "/"}, Min Temp - ${forecast.temperature.min && forecast.temperature.min.celsius ? forecast.temperature.min.celsius : "/"}`);

              return (
                <tr key={index} className="forecast-item">
                  <td className="forecast-date">{`${forecast.dateLabel} ${forecast.date}`}</td>
                  <td>{forecast.temperature.max ? forecast.temperature.max.celsius : "/"}</td>
                  <td>{forecast.temperature.min && forecast.temperature.min.celsius ? forecast.temperature.min.celsius : "/"}</td>
                  <td>{forecast.telop}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default WeatherDisplay;
