let timerInterval; // 用於保存計時器引用的全局變量

function startTimer() {
    const now = Math.floor(Date.now() / 1000);
    let storedStartTime = localStorage.getItem("timerStart");

    // 如果沒有儲存的開始時間，則設置現在為開始時間
    if (!storedStartTime) {
        localStorage.setItem("timerStart", now);
    }
    // toggleStopButtonReload();
    // 計算已經過去的時間並開始計時
    let elapsedTime = now - parseInt(localStorage.getItem("timerStart"), 10);
    timerInterval = setInterval(() => {
        elapsedTime++;
        updateTimerDisplay(elapsedTime);
    }, 1000);
}

function updateTimerDisplay(timerValue) {
    // 如果 timerValue 不是數字，設置為 0
    if (isNaN(timerValue)) {
        timerValue = 0;
    }
    // 將總秒數轉換為天、小時、分、秒
    let seconds = timerValue % 60;
    let totalMinutes = Math.floor(timerValue / 60);
    let minutes = totalMinutes % 60;
    let totalHours = Math.floor(totalMinutes / 60);
    let hours = totalHours % 24;
    let days = Math.floor(totalHours / 24);

    // 創建顯示字符串
    let displayString = `${days}天 ${hours}小時 ${minutes}分 ${seconds}秒`;

    // 更新DOM元素
    const timerDisplay = document.querySelector('#timerDisplay');
    timerDisplay.textContent = displayString;
}

function getElapsedTime() {
    const storedStartTime = localStorage.getItem("timerStart");
    if (!storedStartTime) {
        return 0;
    }
    const startTime = parseInt(storedStartTime, 10);
    const now = Math.floor(Date.now() / 1000);
    return now - startTime;
}

window.addEventListener('load', () => {
    if (localStorage.getItem("timerStart")) {
        startTimer();
    }
});

function stopTimer() {
    clearInterval(timerInterval); 
    localStorage.removeItem("timerStart");
}

function toggleStopButtonReload() {
    const stopButton = document.querySelector('#parking-stop-button');
    const cancelMessage = document.querySelector('#cancel-message');
    const bookingTime = parseInt(localStorage.getItem('timerStart'), 10);

    if (bookingTime) {
        const currentTime = Math.floor(Date.now() / 1000);
        const timeElapsed = currentTime - bookingTime;

        if (timeElapsed < 30) { // under 5 mins
            if (lastClickedButton === 'reservation') {
                stopButton.textContent = '取消預約';
            } else if (lastClickedButton === 'booking') {
                stopButton.textContent = '取消停車';
            }
            cancelMessage.textContent = '5分鐘內可免費取消';

            setTimeout(() => {
                stopButton.textContent = '結帳'; 
                cancelMessage.textContent = '';
            }, (30 - timeElapsed) * 1000);
        }
    }else{
        console.error("no timer in localStorage")
    }
};

buttonParkingStop = document.querySelector('#parking-stop-button');

buttonParkingStop.addEventListener('click', async () => {
    const parkingLotIdElement = document.querySelector('#packing-page-parking-lot-id');
    const parkingLotIdText = parkingLotIdElement.textContent;
    let ParkingStopTime = getCurrentDateTime();
    await passParkingStopData(parkingLotIdText, ParkingStopTime); 
    await fetchData();
    await returnBookingData();
    stopTimer();
    removeClass('#packing-page-container', ['packing-page-container-toggled'])
    thankMessage()
    setTimeout(() => { location.reload() }, 1000);
});

function thankMessage(){
    const alertContent = document.querySelector("#alert-content")
    alertContent.textContent = '感謝您的消費';
    toggleClass('#alert-page-container', 'alert-page-container-toggled');
    toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
};

async function passParkingStopData(parkingLotIdText, ParkingStopTime){
    try{
        const response = await inputParkingStopDataToDB(parkingLotIdText, ParkingStopTime);
        const data = await handleResponse(response);
        // await fetchData();
    }catch(error){
        handleError(error);
    }
}

async function inputParkingStopDataToDB(parkingLotIdText, ParkingStopTime){
    const token = localStorage.getItem('Token');
    const ParkingStopData = {
        stopData: parkingLotIdText,
        stopTime: ParkingStopTime
    };
    console.log(ParkingStopData)
    const response = await fetch("/api/input_stopping_data", {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(ParkingStopData), 
    });
    return response;
};

