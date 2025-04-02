// 更新滑塊值顯示位置的函數
function updateSliderValuePosition(slider, output, labelWidth, thumbWidth = 8) {
    output.innerHTML = slider.value + (output.id === 'valuePrice' ? '元' : ' m ');
    let percent = (slider.value - slider.min) / (slider.max - slider.min);
    let sliderWidth = slider.offsetWidth;
    let newPosition = percent * (sliderWidth - thumbWidth) + labelWidth;

    if (percent == 0){
        output.style.left = labelWidth + 'px';
    } else if (percent <= 0.95){
        output.style.left = newPosition - (output.offsetWidth / 3) + 'px';
    } else {
        output.style.left = newPosition - (output.offsetWidth / 3) - 10 + 'px';
    }
}

// 為每個滑塊添加輸入事件監聽器
function initializeSlider(sliderId, outputId) {
    let slider = document.getElementById(sliderId);
    let output = document.getElementById(outputId);
    let labelWidth = document.querySelector(".slider-label").offsetWidth;

    slider.addEventListener('input', () => updateSliderValuePosition(slider, output, labelWidth));
    updateSliderValuePosition(slider, output, labelWidth); // 初始化
}

initializeSlider('rangeMaxPrice', 'valuePrice');
initializeSlider('rangeMaxDistance', 'valueDistance');
initializeSlider('rangeMaxLength', 'valueLength');
initializeSlider('rangeMaxWidth', 'valueWidth');