let memberParkingLotData;
let lastClickedButton = '';
// ------------------------------------------------------------------------------------------------
document.querySelector('#parking-lot-information-page-edit-button').addEventListener('click', function() {
    lastClickedButton = 'edit';
});
document.querySelector('#parking-lot-page-increase').addEventListener('click', function() {
    lastClickedButton = 'storage';
});
// ------------------------------------------------------------------------------------------------
//新增車位輸入框
let inputCount = 1;
const addButton = document.querySelector('#add-input-car-space-container-button')

addButton.addEventListener('click', () => {
    inputCount++;
    const inputCarSpaceContainer = document.querySelector('#input-car-space-container');
    const newInputBoxNumber = document.createElement('div');
    const newInputBoxImage = document.createElement('div');
    newInputBoxNumber.className = 'input-box';
    newInputBoxImage.className = 'input-box';
    newInputBoxNumber.innerHTML = 
    `<label for="parking-lot-number${inputCount}" class="consistent-text">*車位編號${inputCount}：</label>
    <input type="text" id="parking-lot-number-input${inputCount}" name="parkingSquareNumber${inputCount}" class="text" placeholder="請輸入車牌編號">`;
    newInputBoxImage.innerHTML = 
    `<label style="display:none" for="parking-square-image${inputCount}" class="consistent-text">車位圖片${inputCount}：</label>
    <input style="display:none" type="file" id="parking-square-image-input${inputCount}" name="parkingSquareImage${inputCount}" class="text" multiple>`;
    // 隐藏 newInputBoxNumber
    newInputBoxImage.style.display = 'none';
    inputCarSpaceContainer.appendChild(newInputBoxNumber);
    inputCarSpaceContainer.appendChild(newInputBoxImage);
}); 

//刪除停車場資料
document.querySelector('#parking-lot-container').addEventListener('click', async (event) => {
    if (event.target.matches('.parking-lot-delete-button')) {
        event.preventDefault();

        let parkingLotTable = event.target.closest('.parking-lot-page-table');

        if (parkingLotTable) {
            const parkingLotName = parkingLotTable.querySelector('.parking-lot-information-page-go-button');
            const parkingLotId = parkingLotName.getAttribute('data-id');
            // const parkingLotData = memberParkingLotData.data.find(lot => lot.name === parkingLotName); 
            try{
                token = localStorage.getItem('Token')
                const response = await fetchAPI("/api/input_parking_lot_information", token, "DELETE", parkingLotId);
                const data = await handleResponse(response);
                fetchMemberParkingLotPData()
            }catch(error){
                handleError(error);
            }
        }
    }
});
// 匯入停車場資訊
const dataStorage = document.querySelector('#parking-lot-information-container-storage-button')
dataStorage.addEventListener('click', async () => {
    let formData = await packingData(); 
    if (!formData) {
        const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message')
        parkingLotStorageSuccessMessage.textContent = '*號欄位為必填項目'
        return null; // 如果 carSpaceData 为空，中断函数
    }
    await passData(formData); // 将 formData 传递给 passData 函数并等待其执行完成
});

async function getParkingLotInformation(){
    let id = document.querySelector('#parking-lot-id-input').value;
    let name = document.querySelector('#parking-lot-name-input').value;
    let address = document.querySelector('#parking-lot-address-input').value;
    let nearLandmark = document.querySelector('#parking-lot-near-input').value;
    
    let imgArray = [];
    let img = document.querySelector('#parking-lot-image-input').files

    for (let i = 0; i < img.length; i++) {
    imgArray.push(img[i]);
    }
    
    let openingTimeAm = document.querySelector('#parking-lot-opening-time-am-input').value;
    let openingTimePm = document.querySelector('#parking-lot-closing-time-pm-input').value;
    let spaceInOut = document.querySelector('#parking-lot-in-out-input').value;
    let price = document.querySelector('#parking-lot-price-input').value;
    let carWidth = document.querySelector('#parking-lot-width-input').value;
    let carHeight = document.querySelector('#parking-lot-height-input').value;

    if (!name || !address || imgArray.length === 0 || !openingTimeAm || !openingTimePm || !price || !carWidth || !carHeight) {
        return;
    }
    let location = await getLatLonFromAddress(address)
    let Latitude = ''
    let Longitude = ''
    if (location) {
        Latitude = location.lat;
        Longitude = location.lng;
    }

    return {
        id: id,
        name: name,
        address: address,
        nearLandmark: nearLandmark,
        img: imgArray, 
        openingTimeAm: openingTimeAm,
        openingTimePm: openingTimePm,
        spaceInOut: spaceInOut,
        price: price,
        carWidth: carWidth,
        carHeight: carHeight,
        Latitude: Latitude, 
        Longitude: Longitude
    }
};

async function getLatLonFromAddress(address) {
    // const apiKey = 'AIzaSyCiz02ZEX650VEundSMH87J_fHaDtmMQP8'; 
    let address_encoding = encodeURIComponent(address)
    // const url = `https://maps.googleapis.com/maps/api/geocode/json?address=${address_encoding}&key=${apiKey}`;
    try {
        token = localStorage.getItem('Token')
        const response = await fetchAPI('/api/get_Lat_and_Long', token, 'POST',  address_encoding);
        const data = await response.json();

        if (data) {
            const lat = data.lat;
            const lng = data.lng;
            return {'lat': lat, 'lng': lng};
        } else {
            console.error('Geocoding failed');
            return null;
        }
    } catch (error) {
        console.error('Error during geocoding:', error.message);
        return null;
    }
}

function getCarSpaceData() {
    let inputCarSpaceContainer = document.querySelector('#input-car-space-container');

    // 初始化儲存DS
    let organizedData = {
        carSpaceNumber: [],
        carSpaceImage: []
    };
    let isEmptyInputFound = false;
    // 獲取並輸入文本
    let textInputs = inputCarSpaceContainer.querySelectorAll('input[type="text"]');
    textInputs.forEach(input => {
        if (input.value.trim() === "") {
            isEmptyInputFound = true; // 空值輸入
            return;
        }
        organizedData.carSpaceNumber.push({ name: input.name, value: input.value });
    });

    if (isEmptyInputFound) {
        return null; 
    }

    let fileInputs = inputCarSpaceContainer.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        let files = [];
        for (let i=0; i<input.files.length; i++) {
            files.push(input.file[i]);
        }
        organizedData.carSpaceImage.push({ name: input.name, value: files });
    });
    
    return organizedData;
}
    

async function packingData(event){
    // event.preventDefault();
    let parkingLotData = await getParkingLotInformation();
    if (!parkingLotData) {
        return; 
    }
    let carSpaceData = getCarSpaceData();
    if (!carSpaceData) {
        return; 
    }
    // console.log(carSpaceData)
    console.log(parkingLotData)
    let formData = new FormData();

    formData.append('id', parkingLotData.id);
    formData.append('name', parkingLotData.name);
    formData.append('address', parkingLotData.address);
    formData.append('nearLandmark', parkingLotData.nearLandmark);
    formData.append('openingTimeAm', parkingLotData.openingTimeAm);
    formData.append('openingTimePm', parkingLotData.openingTimePm);
    formData.append('spaceInOut', parkingLotData.spaceInOut);
    formData.append('price', parkingLotData.price);
    formData.append('carWidth', parkingLotData.carWidth);
    formData.append('carHeight', parkingLotData.carHeight);
    formData.append('Latitude', parkingLotData.Latitude);
    formData.append('Longitude', parkingLotData.Longitude);

    if (parkingLotData.img && parkingLotData.img.length > 0) {
        for (let i = 0; i < parkingLotData.img.length; i++) {
            formData.append('img', parkingLotData.img[i]);
        }
    } else {
        formData.append('img', '');
    }
    
    carSpaceData.carSpaceNumber.forEach(item => {
        formData.append(item.name, item.value);
    });

    carSpaceData.carSpaceImage.forEach(item => {
        if (item.value.length > 0) {
            item.value.forEach(file => {
                formData.append(item.name, file);
            });
        } else {
            formData.append(item.name, '');
        }
    });

    return formData
};

async function passData(){
    const token = localStorage.getItem('Token');
    let formData = await packingData();
    // await displayFormData(formData) 
    try{
        let response;

        if (lastClickedButton === 'storage') {
            response = await fetchAPI("/api/input_parking_lot_information", token, "POST", formData);
        } else if (lastClickedButton === 'edit') {
            response = await fetchAPI("/api/input_parking_lot_information", token, "PUT", formData);
        }

        const data = await handleResponse(response);    
        if (data.ok){
            const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message')
            parkingLotStorageSuccessMessage.textContent = '儲存成功!';
            setTimeout(() => (location.reload()), 1000)
        }
    }catch(error){
        handleError(error);
    }
}

fetchMemberParkingLotPData();
// let memberParkingLotData;
async function fetchMemberParkingLotPData(){
    try{
        const response = await getMemberParkingLotData();
        memberParkingLotData = await handleResponse(response);
        // let memberParkingLotData = data;
        addParkingLotInDiv(memberParkingLotData)
    }catch(error){
        handleError(error);
    }
}

async function getMemberParkingLotData(){
    const token = localStorage.getItem('Token');
    const response = await fetchAPI("/api/input_parking_lot_information", token, 'GET', null)
    return response;
}

function addParkingLotInDiv(data) {
    if(!data) {
        console.error();
        ("No data found!");
        return null;
    }
    console.log(data)
    const container = document.querySelector('#parking-lot-container'); 
    container.innerHTML = null; 
    if(data.data.length == 0){
        container.textContent = '目前無登記的停車場'
    }
    data.data.forEach(item => {
        const parkingLotDiv = document.createElement('div');
        parkingLotDiv.className = 'parking-lot-page-table';

        const imageDiv = document.createElement('div');
        imageDiv.className = 'image';
        const img = document.createElement('img');
        img.src = item.images && item.images.length > 0 ? item.images[0] : '../static/IMAGE/noimage.png';
        imageDiv.appendChild(img);
        parkingLotDiv.appendChild(imageDiv);

        const name = document.createElement('div');
        name.className = 'parking-lot-information-page-go-button';
        name.setAttribute('data-id', item.id);
        name.textContent = item.name;
        parkingLotDiv.appendChild(name);

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.className = 'parking-lot-delete-button';
        deleteButton.id = 'parking-lot-delete-button';
        deleteButton.textContent = '刪除';
  
        parkingLotDiv.appendChild(deleteButton);

        container.appendChild(parkingLotDiv);

        const separator = document.createElement('div');
        separator.className = 'separator';
        container.appendChild(separator);
    });
}

document.querySelector('#parking-lot-container').addEventListener('click', (event) => {
    let parkingLotTable = event.target.closest('.parking-lot-page-table');

    if (parkingLotTable) {
        const parkingLotName = parkingLotTable.querySelector('.parking-lot-information-page-go-button').textContent;
        const parkingLotData = memberParkingLotData.data.find(lot => lot.name === parkingLotName);
        
        if (parkingLotData) {
            fillParkingLotData(parkingLotData);
        }
    }
});

function fillParkingLotData(parkingLotData) {
    // 更新表格中的數據
    document.querySelector('#parking-lot-id').textContent = parkingLotData.id || '無';
    document.querySelector('#parking-lot-name').textContent = parkingLotData.name || '無';
    document.querySelector('#parking-lot-address').textContent = parkingLotData.address || '無';
    document.querySelector('#parking-lot-near-landmark').textContent = parkingLotData.landmark || '無';
    document.querySelector('#parking-lot-opening-time').textContent = parkingLotData.openingTime + '-' + parkingLotData.closingTime || '無';
    document.querySelector('#parking-lot-in-out').textContent = parkingLotData.spaceInOut || '無';
    // document.querySelector('#parking-lot-price').textContent = parkingLotData.price ? `${parkingLotData.price}元` : '無';
    document.querySelector('#parking-lot-price').textContent = parkingLotData.price + `元` || '無';
    document.querySelector('#parking-lot-width').textContent = parkingLotData.widthLimit + 'm' || '無';
    document.querySelector('#parking-lot-height').textContent = parkingLotData.heightLimit + 'm' || '無';
    document.querySelector('#parking-space-total-number').textContent = parkingLotData.squares.length + '位' || '無';
    // let squareNumbers = parkingLotData.squares && parkingLotData.squares.length > 0
    // ? parkingLotData.squares.map(square => square.square_number).join("、")
    // : '無';
    let squareNumbers;
    if (parkingLotData.squares && parkingLotData.squares.length > 0) {
        squareNumbers = parkingLotData.squares.map(square => square.square_number).join("、");
    } else {
        squareNumbers = '無';
    }
    document.querySelector('#parking-lot-square-page-go-button').textContent = squareNumbers;
    document.querySelector('#parking-lot-id-input').value = parkingLotData.id || '';
    document.querySelector('#parking-lot-name-input').value = parkingLotData.name || '';
    document.querySelector('#parking-lot-address-input').value = parkingLotData.address || '';
    document.querySelector('#parking-lot-near-input').value = parkingLotData.landmark || '';
    document.querySelector('#parking-lot-opening-time-am-input').value = parkingLotData.openingTime || '';
    document.querySelector('#parking-lot-closing-time-pm-input').value = parkingLotData.closingTime || '';
    document.querySelector('#parking-lot-in-out-input').value = parkingLotData.spaceInOut || '';
    document.querySelector('#parking-lot-price-input').value = parkingLotData.price || '';
    document.querySelector('#parking-lot-width-input').value = parkingLotData.widthLimit || '';
    document.querySelector('#parking-lot-height-input').value = parkingLotData.heightLimit || '';
    document.querySelector('#parking-lot-number-input').value = parkingLotData.spaces && parkingLotData.spaces.length > 0 ? parkingLotData.spaces[0].number : '';
}