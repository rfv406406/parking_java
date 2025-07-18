let timerInterval;

function startTimer() {
    const currentTimeToSec = Math.floor(Date.now() / 1000);
    let storedStartTime = localStorage.getItem("timerStart");

    if (!storedStartTime) {
        localStorage.setItem("timerStart", currentTimeToSec);
    }
    // 計算已經過去的時間並開始計時
    let elapsedTime = currentTimeToSec - localStorage.getItem("timerStart");
    // 避免初始的停頓
    updateTimerDisplay(elapsedTime);
    timerInterval = setInterval(() => {
        elapsedTime++;
        updateTimerDisplay(elapsedTime);
    }, 1000);
}

function updateTimerDisplay(timerValue) {
    let seconds = timerValue % 60;
    let totalMinutes = Math.floor(timerValue / 60);
    let minutes = totalMinutes % 60;
    let totalHours = Math.floor(totalMinutes / 60);
    let hours = totalHours % 24;
    let days = Math.floor(totalHours / 24);

    let displayString = `${days}天 ${hours}小時 ${minutes}分 ${seconds}秒`;

    const timerDisplay = document.querySelector('#timerDisplay');
    timerDisplay.textContent = displayString;
}

function getElapsedTime() {
    const storedStartTime = localStorage.getItem("timerStart");
    if (!storedStartTime) {
        return 0;
    }
    const now = Math.floor(Date.now() / 1000);
    return now - storedStartTime;
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
};

buttonParkingStop = document.querySelector('#parking-stop-button');

buttonParkingStop.addEventListener('click', async () => {
    const parkingLotIdElement = document.querySelector('#packing-page-parking-lot-name').getAttribute("value");
    const parkingLotSquareIdElement = document.querySelector('#packing-page-parking-lot-space-number').getAttribute("value");
    const orderNumberElement = document.querySelector('#packing-page-parking-lot-order-number');
    const startTimeElement = document.querySelector('#packing-page-parking-lot-start-time');
    const priceElement = document.querySelector('#packing-page-parking-lot-price');
    const balanceElement = document.querySelector('#balanceDiv');

    const parkingLotId = parkingLotIdElement;
    const parkingLotSquareId = parkingLotSquareIdElement;
    const orderNumber = orderNumberElement.textContent;
    const startTime = startTimeElement.textContent;
    const price = priceElement.textContent;
    const balance = balanceElement.textContent.replace(/\D/g, '');

    try {
        stopTimer();
        await passParkingStopData(parkingLotId, parkingLotSquareId, startTime, price, balance, orderNumber);
        thankMessage();
    } catch(error) {
        handleError(error);
        if (error.message.includes("餘額不足")) {
            const errorMessage =  "餘額不足! 請先加值!";
            displayAlertMessage(errorMessage);
        }
    }
});

async function passParkingStopData(parkingLotId, parkingLotSquareId, startTime, price, balance, orderNumber){
    try{
        const token = tokenChecking();
        carSpaceNumberArray = [
            {
                "status": "閒置中",
            }
        ];
        const parkingStopData = {
            "parkingLotId": parkingLotId,
            "parkingLotSquareId": parkingLotSquareId,
            "startTime": startTime,
            "price": price,
            "balance": balance,
            "parkingLot": {
                "carSpaceNumber": carSpaceNumberArray
            }
        };
        console.log(parkingStopData)
        const response = await fetchAPI(`/api/parkingLotUsage/${orderNumber}`, token, "PUT", parkingStopData);
        await handleResponse(response);
    }catch(error){
        handleError(error);
        throw error;
    }
}

function thankMessage(){
    removeClass('#packing-page-container', ['packing-page-container-toggled']);
    const alertMessage =  '感謝您的消費!';
    const alertButton = displayAlertMessage(alertMessage);
    alertButton.addEventListener("click", () => {
        location.reload();
    })
};
