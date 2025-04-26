let memberParkingLotData;
// ------------------------------------------------------------------------------------------------
document.querySelector('#parking-lot-information-page-edit-button').addEventListener('click', () => {
    document.querySelector('#parking-lot-information-container-storage-button').textContent = "編輯";
    document.querySelector('#parking-lot-storage-success-message').textContent = '';
});
document.querySelector('#parking-lot-page-increase').addEventListener('click', () => {
    reNewParkingLotForm();
    createSquareInput();
    document.querySelector('#parking-lot-information-container-storage-button').textContent = "儲存";
});
// ------------------------------------------------------------------------------------------------
//新增車位輸入框
let inputCount = 0;
const addButton = document.querySelector('#add-input-car-space-container-button');

addButton.addEventListener('click', () => createSquareInput());

function createSquareInput(parkingSquareId = null) {
    inputCount++;
    const inputCarSpaceContainer = document.querySelector('#input-car-space-container');
    // 建立用於車位編號的容器
    const newInputBoxNumber = document.createElement('div');
    // newInputBoxNumber.id = `input-car-space-id-${inputCount}`;
    newInputBoxNumber.setAttribute("value", inputCount);
    newInputBoxNumber.classList.add('input-box');
    
    // 建立車位編號的 label
    const labelNumber = document.createElement('label');
    labelNumber.setAttribute('for', `parking-lot-number-${inputCount}`);
    labelNumber.classList.add('consistent-text');
    labelNumber.textContent = `*車位編號${inputCount}：`;
    
    // 建立車位編號的 input
    const inputNumber = document.createElement('input');
    inputNumber.type = 'text';
    // inputNumber.id = `parking-lot-number-input-${inputCount}`;
    if (parkingSquareId != null) {
        inputNumber.setAttribute("parkingSquareId", parkingSquareId);
    }
    inputNumber.name = `parkingSquareNumber`;
    inputNumber.classList.add('text');
    inputNumber.placeholder = '請輸入車牌編號';
    
    // 將 label 與 input 加入車位編號的容器
    newInputBoxNumber.appendChild(labelNumber);
    newInputBoxNumber.appendChild(inputNumber);
    
    // 建立用於車位圖片的容器
    const newInputBoxImage = document.createElement('div');
    // newInputBoxImage.id = `input-car-space-image-${inputCount}`;
    newInputBoxImage.setAttribute("value", inputCount);
    newInputBoxImage.classList.add('input-box');
    
    // 建立車位圖片的 label
    const labelImage = document.createElement('label');
    labelImage.setAttribute('for', `parking-square-image-${inputCount}`);
    labelImage.classList.add('consistent-text');
    labelImage.style.display = 'none'; // 隱藏 label
    labelImage.textContent = `車位圖片${inputCount}：`;
    
    // 建立車位圖片的 input
    const inputImage = document.createElement('input');
    inputImage.type = 'file';
    // inputImage.id = `parking-square-image-input${inputCount}`;
    inputImage.name = `parkingSquareImage${inputCount}`;
    inputImage.classList.add('text');
    inputImage.multiple = true;
    inputImage.style.display = 'none'; // 隱藏 input
    
    // 將 label 與 input 加入車位圖片的容器
    newInputBoxImage.appendChild(labelImage);
    newInputBoxImage.appendChild(inputImage);
    
    // 如果需要隱藏整個車位圖片區塊，也可以設定其 display 屬性
    newInputBoxImage.style.display = 'none';
    
    // 將建立好的容器加入總的容器中
    inputCarSpaceContainer.appendChild(newInputBoxNumber);
    inputCarSpaceContainer.appendChild(newInputBoxImage);
}

document.querySelector('#parking-lot-container').addEventListener('click', (event) => {
    let parkingLotTable = event.target.closest('.parking-lot-page-table');
    if (parkingLotTable) {
        const parkingLotName = parkingLotTable.querySelector('.parking-lot-information-page-go-button').textContent;
        const parkingLotData = memberParkingLotData.find(lot => lot.name === parkingLotName);
        if (parkingLotData) {
            fillParkingLotForm(parkingLotData);
        }
    }
});
//刪除停車場資料
document.querySelector('#parking-lot-container').addEventListener('click', async (event) => {
    if (event.target.matches('.parking-lot-delete-button')) {
        event.preventDefault();

        let parkingLotTable = event.target.closest('.parking-lot-page-table');

        if (parkingLotTable) {
            const parkingLotName = parkingLotTable.querySelector('.parking-lot-information-page-go-button');
            const parkingLotId = parkingLotName.getAttribute('id');
            try{
                token = tokenChecking();
                const response = await fetchAPI(`/api/parkingLot/${parkingLotId}`, token, "DELETE");
                const data = await handleResponse(response);
                getMemberParkingLotPData()
            }catch(error){
                handleError(error);
            }
        }
    }
});
// 匯入停車場資訊
document.querySelector('#parking-lot-information-container-storage-button').addEventListener('click', async () => {
    let buttonText = document.querySelector('#parking-lot-information-container-storage-button').textContent;
    let formData = packingData(); 
    if (!formData) {
        const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message');
        parkingLotStorageSuccessMessage.textContent = '*號欄位為必填項目';
        parkingLotStorageSuccessMessage.style.color = "red";
        return null; 
    }
    await passParkingLotDataToDB(buttonText, formData); 
});

function packingData(){
    let formData = new FormData();

    let parkingLotId = document.querySelector('#parking-lot-id').textContent;
    let name = document.querySelector('#parking-lot-name-input').value;
    let address = document.querySelector('#parking-lot-address-input').value;
    let nearLandmark = document.querySelector('#parking-lot-near-input').value;
    let openingTime = document.querySelector('#parking-lot-opening-time-am-input').value;
    let closingTime = document.querySelector('#parking-lot-closing-time-pm-input').value;
    let spaceInOut = document.querySelector('#parking-lot-in-out-input').value;
    let price = document.querySelector('#parking-lot-price-input').value;
    let carWidth = document.querySelector('#parking-lot-width-input').value;
    let carHeight = document.querySelector('#parking-lot-height-input').value;
    let imgArray = [];
    let img = document.querySelector('#parking-lot-image-input').files
    for (let i = 0; i < img.length; i++) {
        imgArray.push(img[i]);
    }

    if (!name || !address || imgArray.length === 0 || !openingTime || !closingTime || !price || !carWidth || !carHeight) {
        return null;
    }

    formData.append('parkingLotId', parkingLotId);
    formData.append('name', name);
    formData.append('address', address);
    formData.append('nearLandmark', nearLandmark);
    formData.append('openingTime', openingTime);
    formData.append('closingTime', closingTime);
    formData.append('spaceInOut', spaceInOut);
    formData.append('price', price);
    formData.append('carWidth', carWidth);
    formData.append('carHeight', carHeight);
    imgArray.forEach(file => {formData.append("img", file)});

    let inputCarSpaceContainer = document.querySelector('#input-car-space-container');
    let isEmptyInputFound = false;
    let textInputs = inputCarSpaceContainer.querySelectorAll('input[type="text"]');
    let carSpaceNumberArray = [];
    textInputs.forEach(input => {
        if (input.value.trim() === "") {
            isEmptyInputFound = true; // 空值
        }
        let carSpaceNumber = {
            "id": input.getAttribute("parkingSquareId") ? input.getAttribute("parkingSquareId"):null,
            "name": input.name,
            "value": input.value
        }
        carSpaceNumberArray.push(carSpaceNumber);
    });
    carSpaceNumberArray.forEach((item, i) => {
        if ( item.id == null ) {
            formData.append(`carSpaceNumber[${i}].id`, "");
        } else {
            formData.append(`carSpaceNumber[${i}].id`, item.id);
        }
        formData.append(`carSpaceNumber[${i}].name`, item.name);
        formData.append(`carSpaceNumber[${i}].value`, item.value);
    })
    if (isEmptyInputFound) {
        return null;
    }

    return formData
};
    
async function passParkingLotDataToDB(buttonText, formData){
    const token = tokenChecking();
    const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message');
    // await displayFormData(formData) 
    try{
        if (buttonText === '儲存') {
            let response = await fetchAPI("/api/parkingLot", token, "POST", formData);
            const data = await handleResponse(response);  
            parkingLotStorageSuccessMessage.style.color = "green";  
            parkingLotStorageSuccessMessage.textContent = '儲存成功!';
        } 
        if (buttonText === '編輯') {
            let response = await fetchAPI(`/api/parkingLot/${formData.get('parkingLotId')}`, token, "PUT", formData);
            const data = await handleResponse(response);
            parkingLotStorageSuccessMessage.style.color = "green";  
            parkingLotStorageSuccessMessage.textContent = '編輯成功!';
        }

        // setTimeout(() => (location.reload()), 1000)

    }catch(error){
        if (error.message.includes("gps not found")) {
            parkingLotStorageSuccessMessage.style.color = "red";  
            parkingLotStorageSuccessMessage.textContent = '地址無效或不位於台灣，請重新確認';
        }
        handleError(error);
    }
}

getMemberParkingLotPData();
// let memberParkingLotData;
async function getMemberParkingLotPData(){
    try{
        const token = tokenChecking();
        // const response = await fetchAPI("/api/input_parking_lot_information", token, 'GET', null)
        const response = await fetchAPI("/api/parkingLot", token, 'GET')

        // const response = await getMemberParkingLotData();
        memberParkingLotData = await handleResponse(response);
        // let memberParkingLotData = data;
        addParkingLotInDiv(memberParkingLotData)
    }catch(error){
        handleError(error);
    }
}

function addParkingLotInDiv(data) {
    const container = document.querySelector('#parking-lot-container'); 
    // container.innerHTML = null; 
    container.textContent = ""; 
    if(data.length == 0){
        let separator = document.createElement("div");
        separator.className = "separator";
        container.style.display = 'flex';
        container.style.justifyContent = 'center';
        container.style.alignItems = 'center';
        container.style.height = "20vh";
        container.textContent = '目前無登記的停車場';
        container.after(separator);
    }
    data.forEach(item => {
        const parkingLotDiv = document.createElement('div');
        parkingLotDiv.className = 'parking-lot-page-table';

        const imageDiv = document.createElement('div');
        imageDiv.className = 'image';
        const img = document.createElement('img');
        img.src = item.imgUrl && item.imgUrl.length > 0 ? item.imgUrl[0] : 'image/noimage.png';
        img.onerror = function() {
            this.onerror = null;
            img.src = 'image/noimage.png';
        }
        imageDiv.appendChild(img);
        parkingLotDiv.appendChild(imageDiv);

        const name = document.createElement('div');
        name.className = 'parking-lot-information-page-go-button';
        name.setAttribute('id', item.parkingLotId);
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

function fillParkingLotForm(parkingLotData) {
    // 更新表格中的數據
    document.querySelector('#parking-lot-id').textContent = parkingLotData.parkingLotId || '無';
    document.querySelector('#parking-lot-name').textContent = parkingLotData.name || '無';
    document.querySelector('#parking-lot-address').textContent = parkingLotData.address || '無';
    document.querySelector('#parking-lot-near-landmark').textContent = parkingLotData.nearLandmark || '無';
    document.querySelector('#parking-lot-opening-time').textContent = parkingLotData.openingTime + ' - ' + parkingLotData.closingTime || '無';
    // document.querySelector('#parking-lot-opening-time-am-input').value = parkingLotData.openingTime || '';
    // document.querySelector('#parking-lot-closing-time-pm-input').value = parkingLotData.closingTime || '';
    document.querySelector('#parking-lot-in-out').textContent = parkingLotData.spaceInOut || '無';
    // document.querySelector('#parking-lot-price').textContent = parkingLotData.price ? `${parkingLotData.price}元` : '無';
    document.querySelector('#parking-lot-price').textContent = parkingLotData.price + ` 元` || '無';
    document.querySelector('#parking-lot-width').textContent = parkingLotData.carWidth + ' m' || '無';
    document.querySelector('#parking-lot-height').textContent = parkingLotData.carHeight + ' m' || '無';
    document.querySelector('#parking-space-total-number').textContent = parkingLotData.carSpaceNumber.length + ' 位' || '無';
    // let squareNumbers = parkingLotData.squares && parkingLotData.squares.length > 0
    // ? parkingLotData.squares.map(square => square.square_number).join("、")
    // : '無';
    reNewSquareForm();
    let carSpaceNumber = [];
    if (parkingLotData.carSpaceNumber && parkingLotData.carSpaceNumber.length > 0) {
        carSpaceNumber = parkingLotData.carSpaceNumber.map(carSpace => { return carSpace.value }).join("、");
    } else {
        carSpaceNumber = '無';
    }
    document.querySelector('#parking-lot-square-page-go-button').textContent = carSpaceNumber;
    for (let i=0; i<parkingLotData.carSpaceNumber.length; i++) {
        createSquareInput(parkingLotData.carSpaceNumber[i].id);
    }
    let inputBoxContainer = document.querySelector(".input-box-container");
    let parkingLotNumberInput = inputBoxContainer.querySelectorAll(".input-box");
    for (let i=0; i<parkingLotData.carSpaceNumber.length; i++) {
        if(i==0) {
            parkingLotNumberInput[i].querySelector('input[name="parkingSquareNumber"]').value = parkingLotData.carSpaceNumber[i].value;
        } else {
            parkingLotNumberInput[i*2].querySelector('input[name="parkingSquareNumber"]').value = parkingLotData.carSpaceNumber[i].value;
        }
    }

    document.querySelector('#parking-lot-id-input').value = parkingLotData.parkingLotId || '';
    document.querySelector('#parking-lot-name-input').value = parkingLotData.name || '';
    document.querySelector('#parking-lot-address-input').value = parkingLotData.address || '';
    document.querySelector('#parking-lot-near-input').value = parkingLotData.nearLandmark || '';
    document.querySelector('#parking-lot-opening-time-am-input').value = parkingLotData.openingTime || '';
    document.querySelector('#parking-lot-closing-time-pm-input').value = parkingLotData.closingTime || '';
    document.querySelector('#parking-lot-in-out-input').value = parkingLotData.spaceInOut || '';
    document.querySelector('#parking-lot-price-input').value = parkingLotData.price || '';
    document.querySelector('#parking-lot-width-input').value = parkingLotData.carWidth || '';
    document.querySelector('#parking-lot-height-input').value = parkingLotData.carHeight || '';
}

function reNewParkingLotForm() {
    document.querySelector('#parking-lot-id-input').value = '';
    document.querySelector('#parking-lot-name-input').value = '';
    document.querySelector('#parking-lot-address-input').value = '';
    document.querySelector('#parking-lot-near-input').value = '';
    document.querySelector('#parking-lot-opening-time-am-input').value = '';
    document.querySelector('#parking-lot-closing-time-pm-input').value = '';
    document.querySelector('#parking-lot-in-out-input').value = '';
    document.querySelector('#parking-lot-price-input').value = '';
    document.querySelector('#parking-lot-width-input').value = '';
    document.querySelector('#parking-lot-height-input').value = '';
    document.querySelector('#parking-lot-storage-success-message').textContent = '';
    reNewSquareForm();
}

function reNewSquareForm() {
    let inputBoxContainer = document.querySelector("#input-car-space-container");
    let InputDiv = inputBoxContainer.querySelectorAll(".input-box");
    for (let i=InputDiv.length-1; i>=0; i--) {
        InputDiv[i].remove();
    }
    inputCount = 0;
}