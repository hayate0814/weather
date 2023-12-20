// SearchForm.js
import React, { useState } from 'react';

const SearchForm = ({ onSearch }) => {
  const [selectedRegion, setSelectedRegion] = useState('');
  const [selectedCity, setSelectedCity] = useState('');

  const handleRegionChange = (event) => {
    setSelectedRegion(event.target.value);

    // リージョンが変更されたら都市をリセット
    setSelectedCity('');
  };

  const handleCityChange = (event) => {
    setSelectedCity(event.target.value);
  };

  const handleSearch = () => {
    onSearch(selectedCity);
  };

  return (
    <div className="search-container">
      <label htmlFor="regionSelect">エリアを選択してください</label>
      <div>
        <select
          id="regionSelect"
          value={selectedRegion}
          onChange={handleRegionChange}
        >
          <option value="">選択してください</option>
          <option value="kanto">関東</option>
          <option value="kansai">関西</option>
          <option value="chugoku">中国</option>
          <option value="shikoku">四国</option>
          <option value="kyushu">九州</option>
          {/* 他の地方も同様に追加 */}
        </select>
      </div>

      {selectedRegion && (
        <div>
          <label htmlFor="citySelect"></label>
          <select
            id="citySelect"
            value={selectedCity}
            onChange={handleCityChange}
          >
            <option value="">選択してください</option>
            {selectedRegion === 'kanto' && (
              <>
                <option value="130010">東京</option>
                <option value="140010">神奈川</option>
                <option value="110010">埼玉</option>
                <option value="120010">千葉</option>
                <option value="820010">茨城</option>
                <option value="910010">栃木</option>
                <option value="100010">群馬</option>
              </>
            )}
            {selectedRegion === 'kansai' && (
              <>
                <option value="270000">大阪</option>
                <option value="260010">京都</option>
                <option value="270020">兵庫</option>
                <option value="250010">滋賀</option>
                <option value="280010">奈良</option>
                <option value="240010">和歌山</option>
              </>
            )}
            {selectedRegion === 'chugoku' && (
              <>
                <option value="340010">広島</option>
                <option value="330010">岡山</option>
                <option value="340020">鳥取</option>
                <option value="320010">島根</option>
                <option value="370000">山口</option>
              </>
            )}
            {selectedRegion === 'shikoku' && (
              <>
                <option value="380010">香川</option>
                <option value="380020">愛媛</option>
                <option value="380030">高知</option>
                <option value="380040">徳島</option>
              </>
            )}
            {selectedRegion === 'kyushu' && (
              <>
                <option value="400010">福岡</option>
                <option value="400020">佐賀</option>
                <option value="420010">長崎</option>
                <option value="410010">熊本</option>
                <option value="430010">大分</option>
                <option value="440010">宮崎</option>
                <option value="450010">鹿児島</option>
                <option value="460010">沖縄</option>
              </>
            )}
          </select>
        </div>
      )}

      <button onClick={handleSearch}>Search</button>
    </div>
  );
};

export default SearchForm;
