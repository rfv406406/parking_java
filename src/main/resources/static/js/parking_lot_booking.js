let bookingLocationData;
let lastClickedButton = '';

buttonBooking = document.querySelector('#button-parking-booking')
carBoardCheckedButton = document.querySelector('#car-board-checked-button')


buttonBooking.addEventListener('click', async () => {
    try{
        lastClickedButton = 'booking';
        // console.log(isTokenChecked)
        let isMemberStatusChecked = await memberStatus();
        // console.log(isMemberStatusChecked)
        if (!isMemberStatusChecked) {
            return; 
        };
        await returnCarBoardData();
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
        let isSquareChecked = carBoardChecking();
        if (!isSquareChecked) {
            return; 
            };
        toggleClass('#packing-page-car-board-selected', 'packing-page-car-board-selected-toggled');
        let squareNumber = document.querySelector('#data-type-selector').value
        let carBoardSelected = document.querySelector('#car-board-number-selector').value
        let bookingData = bookingLocationData; // return data get
        let bookingTime = getCurrentDateTime();
        await passBookingData(bookingData, bookingTime, carBoardSelected, squareNumber); 
        await returnBookingData()
        await fetchData()
        startTimer();
        toggleStopButtonReload();
    }catch(error){
        handleError(error);
    }
});

function carBoardChecking(){
    let selector = document.querySelector('#car-board-number-selector');
    // 檢查選擇器是否有值
    if (!selector.value) {
        const alertContent = document.querySelector("#alert-content")
        alertContent.textContent = '請選擇車牌';
        toggleClass('#alert-page-container', 'alert-page-container-toggled');
        toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
        return false; 
    }
    return true; 
};

//停車資料取得
async function getBookingInformation(locationData){
    console.log(locationData)
    bookingLocationData = locationData
};
//取得停車當下時間
function getCurrentDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // 月份是從 0 開始的
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

async function returnBookingData(){
    const token = localStorage.getItem('Token');
    try{
        if(!token){
            throw new Error('please sign in!')
        }
        const showBookingDataOnParkingPage = await fetchAPI("/api/get_booking_information", token, 'GET');
        const data = await handleResponse(showBookingDataOnParkingPage);
        console.log(data)
        renderParkingPage(data)
    }catch(error){
        handleError(error);
    }
}
returnBookingData()

async function passBookingData(bookingData, bookingTime, carBoardSelected, squareNumber){
    try{
        console.log(bookingData);
        console.log(bookingTime);
        console.log(carBoardSelected);
        console.log(squareNumber);
        const response = await inputBookingDataToDB(bookingData, bookingTime ,carBoardSelected, squareNumber);
        const data = await handleResponse(response);
        console.log(data)
    }catch(error){
        handleError(error);
    }
}

async function inputBookingDataToDB(bookingData, bookingTime ,carBoardSelected, squareNumber){
    const token = localStorage.getItem('Token');
    const bookingInformationData = {
        bookingData: bookingData,
        bookingTime: bookingTime,
        carBoardSelected: carBoardSelected,
        squareNumber: squareNumber
    };
    console.log(bookingInformationData);
    const response = await fetchAPI("/api/input_booking_information", token, 'POST', bookingInformationData)
    return response;
}

function parkingLotInformationTable(locationData){
    console.log(locationData)
    document.querySelector('#parking-lot-name').textContent = locationData.name || '';
    document.querySelector('#parking-lot-address').textContent = locationData.address || '';
    document.querySelector('#parking-lot-near-landmark').textContent = '附近地標:' + locationData.landmark || '附近地標:'+'無';
    document.querySelector('#parking-lot-opening-time').textContent = locationData.openingTime && locationData.closingTime 
    ? locationData.openingTime + ' - ' + locationData.closingTime: '';
    document.querySelector('#parking-lot-in-out').textContent = locationData.spaceInOut || '';
    document.querySelector('#parking-lot-price').textContent = locationData.price + '元' || '';
    document.querySelector('#parking-lot-width').textContent = locationData.widthLimit + 'm' || '';
    document.querySelector('#parking-lot-height').textContent = locationData.heightLimit + 'm'|| '';
    document.querySelector('#parking-lot-holder-phone').textContent = locationData.cellphone || '連絡電話:'+'無';
    let squaresWithEmptyStatus = locationData.squares ? locationData.squares.filter(square => !square.status).length : 0;
    let totalSquares = locationData.squares ? locationData.squares.length : 0;
    document.querySelector('#parking-space-total-number').textContent = `${squaresWithEmptyStatus} / ${totalSquares} 位`;    
    
    let select = document.querySelector('#data-type-selector');
    select.innerHTML = `<option value="title">選擇車位</option>`;
 
    locationData.squares.forEach(square => {
        if(square.status === '閒置中'){
            let option = document.createElement('option')
            option.value = square.square_number;
            option.textContent = square.square_number;
            select.appendChild(option);
        }
    });

    updateParkingAvailability(locationData)
    rotationImg(locationData)
};

function updateParkingAvailability(locationData) {
    let allOccupied = false;
    locationData.squares.forEach(square => {
        if(square.status !== '閒置中') {
            allOccupied = true;
        };
    })
    // const allOccupied = locationData.squares.every(square => square.status !== '閒置中');
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
    if (data.data == '目前尚無停車資訊'){
        return
    }
    let element = document.querySelector('.packing-page-information-none');
    element.style.display = 'none'
    toggleClass('#packing-page-information', 'packing-page-information-toggled'); 
    document.querySelector('#packing-page-parking-lot-id').textContent = data.data[0].id;
    document.querySelector('#packing-page-parking-lot-name').textContent = data.data[0].parkinglotname;
    document.querySelector('#packing-page-parking-lot-address').textContent = data.data[0].address;
    document.querySelector('#packing-page-parking-lot-space-number').textContent = data.data[0].square_number;
    document.querySelector('#packing-page-parking-lot-price').textContent = data.data[0].price;
    document.querySelector('#packing-page-parking-lot-start-time').textContent = data.data[0].starttime;
};

async function returnCarBoardData(){
    const token = localStorage.getItem('Token');
    try{
        const response = await fetchAPI("/api/input_car_board_data", token, 'GET');
        const data = await handleResponse(response);
        // console.log(data);
        await carBoardNumberToSelector(data);
        return data;
    }catch(error){
        handleError(error);
    }
}

async function carBoardNumberToSelector(data){
    console.log(data);
    let select = document.querySelector('#car-board-number-selector');
    data.data.forEach(item => {
        let exists = false;
        for (let i=0; i< select.options.length; i++) {
            if( select.options[i].textContent === item.carboard_number) {
                exists = true;
                return;
            }
        }
        if (!exists) {
            let option = document.createElement('option');
            option.textContent = item.carboard_number; 
            select.appendChild(option);
        }
    });
}

// 停車資格確認
//使用者停車狀態for booking
async function memberStatus(){
    try{
        let parkingStatus = await getMemberStatus()
        let memberCar = await returnCarBoardData()
        const alertContent = document.querySelector("#alert-content")
        const squareNumber = document.querySelector('#data-type-selector')
        const token = localStorage.getItem('Token');
        console.log(parkingStatus)
        if (!token){
            // const alertContent = document.querySelector("#alert-content")
            alertContent.textContent = '請先登入以使用完整功能';
            toggleClass('#alert-page-container', 'alert-page-container-toggled');
            toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
        }
        if (memberCar.data.length === 0){
            alertContent.textContent = '請先登記車輛!';
            toggleClass('#alert-page-container', 'alert-page-container-toggled');
            toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
            return false;
        }
        if (parkingStatus.data.Balance <= 0){
            alertContent.textContent = '餘額不足，請儲值';
            toggleClass('#alert-page-container', 'alert-page-container-toggled');
            toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
            return false; 
        }
        if (parkingStatus.data.status !== null){
            alertContent.textContent = '您目前正在停車囉';
            toggleClass('#alert-page-container', 'alert-page-container-toggled');
            toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
            return false;
        }
        if (squareNumber.value === "title"){
            alertContent.textContent = '請選擇車位編號';
            toggleClass('#alert-page-container', 'alert-page-container-toggled');
            toggleClass('#alert-page-black-back', 'alert-page-black-back-toggled');
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

    imageDiv.innerHTML = '';

    let potContainer = document.createElement("div");
    potContainer.classList.add("pot_container");
    imageDiv.appendChild(potContainer);

    let imageURL;
    if (data.images && data.images.length > 0) {
        imageURL = data.images;
    } else {
        imageURL = ['https://d1hxt3hn1q2xo2.cloudfront.net/1702149637-20210325-121559_U7321_M680292_0f7b.jpg'];
    };

    for (let i = 0; i < imageURL.length; i++) {
        let newDiv = document.createElement("div");
        newDiv.classList.add("image");
        
        let pot = document.createElement("div");
        pot.classList.add("pot");
        potContainer.appendChild(pot);
        
        let img = document.createElement("img");
        img.src = imageURL[i];
        newDiv.appendChild(img);
        imageDiv.appendChild(newDiv);
    }

    let pot = document.querySelectorAll(".pot")
    let images = imageDiv.querySelectorAll('.image');
    let pot2 = document.createElement("div");
    pot2.classList.add("pot_pot");
    let pot2length = 0;
    pot[pot2length].appendChild(pot2);
    
    buttonRight.onclick = function () {
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

    buttonLeft.onclick = function () {
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
