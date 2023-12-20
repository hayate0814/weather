// App.js
// ...（先頭のimport文などはそのまま）
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import SearchForm from './components/SearchForm';
import WeatherDisplay from './components/WeatherDisplay';

const App = () => {
  const [weatherData, setWeatherData] = useState(null);
  const [cityName, setCityName] = useState('');

  useEffect(() => {
    const initialCityCode = '130010'; // 東京の都市番号
    handleSearch(initialCityCode);
  }, []);

  const handleSearch = async (cityCode) => {
    const apiUrl = `https://weather.tsukumijima.net/api/forecast/city/${cityCode}`;
    try {
      const response = await axios.get(apiUrl);
      setWeatherData(response.data);
      setCityName(response.data.location.city);
    } catch (e) {
      console.error("Error fetching weather data:", e);
    }
  };

  return (
    <div className="app-container">
      <h1>Weather Forecast App</h1>
      <SearchForm onSearch={handleSearch} />
      {weatherData && <WeatherDisplay cityName={cityName} forecasts={weatherData.forecasts} />}
    </div>
  );
};

export default App;
