let bookingLocationData;
let lastClickedButton = '';

const buttonBooking = document.querySelector('#button-parking-booking');
const carBoardCheckedButton = document.querySelector('#car-board-checked-button');
const chatroomButton = document.querySelector("#button-turning-chatroom");


buttonBooking.addEventListener('click', async () => {
    try{
        lastClickedButton = 'booking';
        let isMemberStatusChecked = await memberStatusChecking();
        if (!isMemberStatusChecked) {
            return null; 
        };
        let carBoardData = await returnCarBoardData();
        await carBoardNumberToSelector(carBoardData);
        toggleClass('#packing-page-container', 'packing-page-container-toggled');
        toggleClass('#packing-page-black-back', 'black-back-toggled');  
        toggleClass('#packing-page-information-none', 'packing-page-information-none-toggled'); 
        toggleClass('#packing-page-car-board-selected', 'packing-page-car-board-selected-toggled');
    }catch(error){
        handleError(error)
    }
});

carBoardCheckedButton.addEventListener('click', async () => {
    try{
        const loader = document.querySelector('#loader');
        let isSquareChecked = carBoardChecking();
        if (!isSquareChecked) {
            return null; 
        };
        loader.style.display = 'flex';
        toggleClass('#packing-page-car-board-selected', 'packing-page-car-board-selected-toggled');
        let parkingLotSquareId = document.querySelector('#data-type-selector').value;
        let carId = document.querySelector('#car-board-number-selector').value;
        let parkingLotId = document.querySelector('#parking-lot-id').textContent;
        let startTime = getCurrentDateTime();
        await passBookingData(parkingLotId, startTime, carId, parkingLotSquareId); 
        await getBookingData();
        loader.style.display = 'none';
        startTimer();
        toggleStopButtonReload();
    }catch(error){
        loader.style.display = 'none';
        handleError(error);
    }
});
//跳轉聯絡頁面
chatroomButton.addEventListener('click', async () => {
    const ownerId = chatroomButton.getAttribute("ownerId");
    const ownerAccount = chatroomButton.getAttribute("ownerAccount");
    const parkingLotId = document.querySelector('#parking-lot-id').textContent;
    const parkingLotName = document.querySelector('#parking-lot-name').textContent
    window.location.href = `/chatroom/${ownerId}/${ownerAccount}/${parkingLotId}/${parkingLotName}`;
})

// 導航
async function navigation(parkingLot) {
    let destination = {
        "lat": parkingLot.latitude,
        "lng": parkingLot.longitude
    }
    let navigation = document.querySelector("#navigation");
    let parkingLotInforTable = document.querySelector("#parking-lot-information-container");
    navigation.addEventListener('click', async (event) => {
        event.stopPropagation();
        directionsRenderer.setMap(map);
        await calculateAndDisplayRoute(directionsService, directionsRenderer, destination);
        map.setCenter(currentPosition);
        map.setZoom(25);
        parkingLotInforTable.style.display = "none";
    });
}

function carBoardChecking(){
    let selector = document.querySelector('#car-board-number-selector');

    if (!selector.value) {
        const alertMessage = '請選擇車牌';
        displayAlertMessage(alertMessage);
        return false; 
    }
    return true; 
};

//停車資料取得
function getBookingInformation(parkingLot){
    bookingLocationData = parkingLot;
};
//取得停車當下時間
function getCurrentDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); 
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

async function getBookingData(){
    try{
        const token = tokenChecking();
        if (token == null) {
            return null;
        }
        const showBookingDataOnParkingPage = await fetchAPI("/api/parkingLotUsage", token, 'GET');
        const data = await handleResponse(showBookingDataOnParkingPage);
        renderParkingPage(data)
    }catch(error){
        handleError(error);
    }
}
getBookingData()

async function passBookingData(parkingLotId, startTime, carId, parkingLotSquareId){
    try{
        const token = tokenChecking();
        carSpaceNumberArray = [
            {
                "status": "使用中",
            }
        ];
        const bookingInformationData = {
            "parkingLotId": parkingLotId,
            "startTime": startTime,
            "carId": carId,
            "parkingLotSquareId": parkingLotSquareId,
            "parkingLot": {
                "carSpaceNumber": carSpaceNumberArray
            }
        };
        const response = await fetchAPI("/api/parkingLotUsage", token, 'POST', bookingInformationData)
        const data = await handleResponse(response);
    }catch(error){
        handleError(error);
    }
}

function parkingLotInformationTable(locationData){
    document.querySelector('#parking-lot-id').textContent = locationData.parkingLotId || '';
    document.querySelector('#parking-lot-name').textContent = locationData.name || '';
    document.querySelector('#parking-lot-address').textContent = locationData.address || '';
    document.querySelector('#parking-lot-near-landmark').textContent = '附近地標: ' + locationData.nearLandmark || '附近地標: '+'無';
    document.querySelector('#parking-lot-opening-time').textContent = locationData.openingTime && locationData.closingTime 
    ? locationData.openingTime + ' - ' + locationData.closingTime: '';
    document.querySelector('#parking-lot-in-out').textContent = locationData.spaceInOut || '';
    document.querySelector('#parking-lot-price').textContent = locationData.price + ' 元' || '';
    document.querySelector('#parking-lot-width').textContent = locationData.carWidth + ' m' || '';
    document.querySelector('#parking-lot-height').textContent = locationData.carHeight + ' m'|| '';
    document.querySelector('#parking-lot-holder-phone').textContent = locationData.cellphone || '連絡電話: '+'無';
    let squaresWithEmptyStatus = locationData.carSpaceNumber.filter(square => square.status == "閒置中").length || 0;
    let totalSquares = locationData.carSpaceNumber ? locationData.carSpaceNumber.length : 0;
    document.querySelector('#parking-space-total-number').textContent = `${squaresWithEmptyStatus} / ${totalSquares} 位`;
    document.querySelector("#button-turning-chatroom").setAttribute("ownerId", locationData.member.id);
    document.querySelector("#button-turning-chatroom").setAttribute("ownerAccount", locationData.member.account);
    
    let select = document.querySelector('#data-type-selector');
    // return option to 0
    select.selectedIndex = 0
    let options = select.querySelectorAll("option");
    for (let i=0; i<options.length; i++) {
        if (Number(options[i].value) != 0) {
            options[i].remove();
        }
    }
    locationData.carSpaceNumber.forEach(square => {
        if (square.status === '閒置中') {
            let option = document.createElement('option')
            option.value = square.id;
            option.textContent = square.value;
            option.style.fontWeight = 'bold';
            option.style.textAlign  = 'center';
            select.appendChild(option);
        }
    });

    updateParkingAvailability(locationData)
    rotationImg(locationData)
};

function updateParkingAvailability(locationData) {
    let allOccupied = true;
    locationData.carSpaceNumber.forEach(square => {
        if(square.status == '閒置中') {
            allOccupied = false;
        };
    })
    const button = document.querySelector('#button-parking-booking');
    const runOut = document.querySelector('.full-capacity-message')
    if (allOccupied) {
        button.style.display = 'none';
        runOut.style.display = 'block';
    }else{
        button.style.display = 'block';
        runOut.style.display = 'none';
    }
}

function renderParkingPage(data){
    if (data == null){
        return null;
    }
    let element = document.querySelector('.packing-page-information-none');
    element.style.display = 'none';
    toggleClass('#packing-page-information', 'packing-page-information-toggled'); 
    document.querySelector('#packing-page-parking-lot-id').textContent = data.id;
    document.querySelector('#packing-page-parking-lot-order-number').textContent = data.orderNumber;
    document.querySelector('#packing-page-parking-lot-name').textContent = data.parkingLot.name;
    document.querySelector('#packing-page-parking-lot-name').setAttribute("value", data.parkingLotId);
    document.querySelector('#packing-page-parking-lot-address').textContent = data.parkingLot.address;
    document.querySelector('#packing-page-parking-lot-space-number').textContent = data.parkingLot.carSpaceNumber[0].value;
    document.querySelector('#packing-page-parking-lot-space-number').setAttribute("value", data.parkingLotSquareId);
    document.querySelector('#packing-page-parking-lot-price').textContent = data.parkingLot.price;
    document.querySelector('#packing-page-parking-lot-start-time').textContent = data.startTime;
};

async function returnCarBoardData(){
    const token = tokenChecking();
    try{
        const response = await fetchAPI("/api/car", token, 'GET');
        const data = await handleResponse(response);
        return data;
    }catch(error){
        handleError(error);
    }
}

async function carBoardNumberToSelector(data){
    let select = document.querySelector('#car-board-number-selector');
    data.forEach(item => {
        let exists = false;
        for (let i=0; i< select.options.length; i++) {
            if( select.options[i].textContent === item.carNumber) {
                exists = true;
                return null;
            }
        }
        if (!exists) {
            let option = document.createElement('option');
            option.value = item.id;
            option.textContent = item.carNumber; 
            select.appendChild(option);
        }
    });
}

// 停車資格確認
//使用者停車狀態for booking
async function memberStatusChecking(){
    try{
        const squareNumber = document.querySelector('#data-type-selector')
        const token = tokenChecking();

        if (token == null){
            const alertMessage = '請先登入以使用完整功能';
            displayAlertMessage(alertMessage);
            return false;
        }

        let memberBalanceStatus = await getMemberBalanceStatus();
        let memberCar = await returnCarBoardData();

        if (memberCar.length === 0){
            const alertMessage = '請先登記車輛!';
            displayAlertMessage(alertMessage);
            return false;
        }
        if (memberBalanceStatus.balance <= 0){
            const alertMessage = '餘額不足，請儲值';
            displayAlertMessage(alertMessage);
            return false; 
        }
        if (memberBalanceStatus.status !== null){
            const alertMessage = '您目前正在停車囉';
            displayAlertMessage(alertMessage);
            return false;
        }
        if (!squareNumber.value || squareNumber.value == null){
            const alertMessage = '請選擇車位編號';
            displayAlertMessage(alertMessage);
            return false; 
        }
        return true;
    }catch(error) {
        handleError(error);
    };
}

//輪播圖
function rotationImg(data) {
    let buttonRight = document.querySelector("#button-img-right");
    let buttonLeft = document.querySelector("#button-img-left");
    let currentImageIndex = 0;
    let imageDiv = document.querySelector('#parking_lot_image_container');

    imageDiv.textContent = '';

    let potContainer = document.createElement("div");
    potContainer.classList.add("pot_container");
    imageDiv.appendChild(potContainer);

    let imageURL = [];
    if (data.imgUrl != null) {
        imageURL = data.imgUrl;
    } else {
        imageURL = ['/image/noimage.png','/image/noimage.png'];
    };

    for (let i = 0; i < imageURL.length; i++) {
        let newDiv = document.createElement("div");
        newDiv.classList.add("image");
        
        let pot = document.createElement("div");
        pot.classList.add("pot");
        potContainer.appendChild(pot);
        
        let img = document.createElement("img");
        img.src = imageURL[i];
        img.onerror = function() {
            this.onerror = null;
            img.src = '/image/noimage.png';
        }
        newDiv.appendChild(img);
        imageDiv.appendChild(newDiv);
    }

    let pot = document.querySelectorAll(".pot")
    let images = imageDiv.querySelectorAll('.image');
    let pot2 = document.createElement("div");
    pot2.classList.add("pot_pot");
    let pot2length = 0;
    pot[pot2length].appendChild(pot2);
    
    buttonRight.onclick = () => {
        if (currentImageIndex < images.length - 1) {
            currentImageIndex++;
            pot2length++;
            pot[pot2length].appendChild(pot2);
        } else {
            currentImageIndex = 0; // return first
            pot2length = 0;
            pot[pot2length].appendChild(pot2);
        }
        imageDiv.scrollLeft = images[currentImageIndex].offsetLeft;
    };

    buttonLeft.onclick = () => {
        if (currentImageIndex > 0) {
            currentImageIndex--;
            pot2length--;
            pot[pot2length].appendChild(pot2);
        } else {
            currentImageIndex = images.length - 1; // last pic
            pot2length = pot.length - 1;
            pot[pot2length].appendChild(pot2);
        }
        imageDiv.scrollLeft = images[currentImageIndex].offsetLeft;
    };    
}
