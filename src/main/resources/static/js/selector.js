const parkingLotParameters = JSON.parse(localStorage.getItem("parkingLotParameters"));

function initializeSlider(sliderId, outputId, parasName) {
    let slider = document.querySelector(sliderId)
    let output = document.querySelector(outputId);

    if (parkingLotParameters) {
        slider.value = Number(parkingLotParameters[parasName]);
        output.textContent = parkingLotParameters[parasName] + (output.id === 'valuePrice' ? ' 元' : ' m');
    } else {
        slider.value = slider.max;
        output.textContent = slider.max + (output.id === 'valuePrice' ? ' 元' : ' m');
    }
    
    slider.addEventListener('input', () => {
        updateSliderValuePosition(slider, output);
    });
}

function updateSliderValuePosition(slider, output) {
    output.textContent = slider.value + (output.id === 'valuePrice' ? ' 元' : ' m');
}

document.querySelector(".main-selector-button").addEventListener("click", () => {
    const container = document.querySelector(".slider-container");
    const messageDiv = document.createElement('div');
    const price = document.querySelector("#valuePrice").textContent.split('元')[0];
    const distance = document.querySelector("#valueDistance").textContent.split('m')[0];
    const carHeight = document.querySelector("#valueHeight").textContent.split('m')[0];
    const carWidth = document.querySelector("#valueWidth").textContent.split('m')[0];
    storedSliderValues(price, distance, carHeight, carWidth);

    if (localStorage.getItem("parkingLotParameters")) {
        messageDiv.style.color = "green";
        messageDiv.style.fontSize = "20px";
        messageDiv.textContent = "設定完成!";
        container.appendChild(messageDiv);
        setTimeout(() => {window.location.href = "/index"}, 1000);
    } else {
        messageDiv.style.color = "red";
        messageDiv.style.fontSize = "20px";
        messageDiv.textContent = "設定失敗，請稍後再試!";
        container.appendChild(messageDiv);
    }

})

function storedSliderValues(price, distance, carHeight, carWidth) {
    const values = {
        "price" : price,
        "distance" : distance,
        "carHeight" : carHeight,
        "carWidth" : carWidth
    }
    const valuesToString = JSON.stringify(values);

    localStorage.setItem('parkingLotParameters', valuesToString);
}

initializeSlider('#rangeMaxPrice', '#valuePrice', "price");
initializeSlider('#rangeMaxDistance', '#valueDistance', "distance");
initializeSlider('#rangeMaxHeight', '#valueHeight', "carHeight");
initializeSlider('#rangeMaxWidth', '#valueWidth', "carWidth");