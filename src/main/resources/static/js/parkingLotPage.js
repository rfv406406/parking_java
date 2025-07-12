let memberParkingLotDataMap = new Map();

document.querySelector('#parking-lot-information-page-edit-button').addEventListener('click', () => {
    document.querySelector('#parking-lot-information-container-storage-button').textContent = "編輯";
    document.querySelector('#parking-lot-storage-success-message').textContent = '';
});
document.querySelector('#parking-lot-page-increase').addEventListener('click', () => {
    reNewParkingLotForm();
    createSquareInput();
    document.querySelector('#parking-lot-information-container-storage-button').textContent = "儲存";
});
//新增車位輸入框
let inputCount = 0;
const addButton = document.querySelector('#add-input-car-space-container-button');

addButton.addEventListener('click', () => createSquareInput());

function createSquareInput(parkingSquareId = null) {
    inputCount++;
    const inputCarSpaceContainer = document.querySelector('#input-car-space-container');
    // 建立用於車位編號的容器
    const newInputBoxNumber = document.createElement('div');
    newInputBoxNumber.setAttribute("value", inputCount);
    newInputBoxNumber.classList.add('input-box');
    // 建立車位編號的 label
    const labelNumber = document.createElement('label');
    labelNumber.setAttribute('for', `parking-lot-number-${inputCount}`);
    labelNumber.classList.add('consistent-text');
    labelNumber.textContent = `*車位編號${inputCount}：`;
    // 建立車位編號的 input
    const inputNumber = document.createElement('input');
    inputNumber.type = 'number';
    if (parkingSquareId != null) {
        inputNumber.setAttribute("parkingSquareId", parkingSquareId);
    }
    inputNumber.id = `parking-lot-number-${inputCount}`;
    inputNumber.name = `parkingSquareNumber`;
    inputNumber.classList.add('text');
    inputNumber.placeholder = '請輸入車牌編號';
    inputNumber.setAttribute('inputmode', 'numeric');
    inputNumber.setAttribute('oninput', 'this.value = this.value.replace(/\\D/g, "")');
    // 將 label 與 input 加入車位編號的容器
    newInputBoxNumber.appendChild(labelNumber);
    newInputBoxNumber.appendChild(inputNumber);
    // 建立用於車位圖片的容器
    const newInputBoxImage = document.createElement('div');
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
    inputImage.name = `parkingSquareImage${inputCount}`;
    inputImage.classList.add('text');
    inputImage.multiple = true;
    inputImage.style.display = 'none'; // 隱藏 input
    // 將 label 與 input 加入車位圖片的容器
    newInputBoxImage.appendChild(labelImage);
    newInputBoxImage.appendChild(inputImage);
    newInputBoxImage.style.display = 'none';
    // 將建立好的容器加入總的容器中
    inputCarSpaceContainer.appendChild(newInputBoxNumber);
    inputCarSpaceContainer.appendChild(newInputBoxImage);
}

document.querySelector('#parking-lot-container').addEventListener('click', (event) => {
    let parkingLotTable = event.target.closest('.parking-lot-page-table');
    if (parkingLotTable) {
        const parkingLotId = parkingLotTable.querySelector('.parking-lot-information-page-go-button').id;
        const parkingLotData = memberParkingLotDataMap[parkingLotId];
        if (parkingLotData) {
            fillParkingLotForm(parkingLotData);
        }
    }
});
//刪除停車場資料
const parkingLotContainer = document.querySelector('#parking-lot-container');
parkingLotContainer.addEventListener('click', async (event) => {
    if (event.target.matches('.parking-lot-delete-button')) {
        event.preventDefault();

        let parkingLotTableContainer = event.target.closest('#parkingLotTableDivContainer');
        let parkingLotTable = event.target.closest('.parking-lot-page-table');

        if (parkingLotTable) {
            const parkingLotName = parkingLotTable.querySelector('.parking-lot-information-page-go-button');
            const parkingLotId = parkingLotName.getAttribute('id');
            try{
                token = tokenChecking();
                parkingLotTableContainer.remove();
                if (!parkingLotContainer.querySelector(".parking-lot-page-table")) {
                    setEmptyContainerContent(parkingLotContainer);
                }
                const response = await fetchAPI(`/api/parkingLot/${parkingLotId}`, token, "DELETE");
                const data = await handleResponse(response);
            }catch(error){
                handleError(error);
            }
        }
    }
});
// 匯入停車場資訊
document.querySelector('#parking-lot-information-container-storage-button').addEventListener('click', async () => {
    try {
        const buttonText = document.querySelector('#parking-lot-information-container-storage-button').textContent;
        const formData = packingData(); 
        if (!formData) {
            const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message');
            parkingLotStorageSuccessMessage.textContent = '*號欄位為必填項目';
            parkingLotStorageSuccessMessage.style.color = "red";
            return null; 
        }
        await passParkingLotDataToDB(buttonText, formData); 
    } catch(error) {
        const parkingLotStorageSuccessMessage = document.querySelector('#parking-lot-storage-success-message');
        
        if (error.message.includes("gps not found")) {
            parkingLotStorageSuccessMessage.style.color = "red";  
            parkingLotStorageSuccessMessage.textContent = '地址無效或不位於台灣，請重新確認';
        } else if (error.message.includes("ParkingLot is using")) {
            const errorMessage = '停車場目前正在使用中，請勿修改內容資訊。';
            displayAlertMessage(errorMessage);
        } else if (error.message.includes("data type is not number")) {
            parkingLotStorageSuccessMessage.textContent = '輸入格式錯誤! 價格、車長寬、車位編號等請輸入數字。';
            parkingLotStorageSuccessMessage.style.color = "red";
        } else if (error.message.includes("img size too much")) {
            parkingLotStorageSuccessMessage.textContent = '總圖片大小物超過20 MB!';
            parkingLotStorageSuccessMessage.style.color = "red";
        }
        handleError(error);
    }
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
    let price = document.querySelector('#parking-lot-price-input').valueAsNumber;
    let carWidth = document.querySelector('#parking-lot-width-input').valueAsNumber;
    let carHeight = document.querySelector('#parking-lot-height-input').valueAsNumber;
    let imgArray = [];
    let img = document.querySelector('#parking-lot-image-input').files
    let imgSize = 0;
    for (let i = 0; i < img.length; i++) {
        imgArray.push(img[i]);   
        imgSize += img[i].size;
        if (imgSize > 2* 10 * 1024 * 1024) {
            throw new Error("img size too much");
        }     
    }

    if (!name || !address || imgArray.length === 0 || !openingTime || !closingTime || !price || !carWidth || !carHeight) {
        return null;
    }

    if (typeof(price) !== "number" || typeof(carWidth) !== "number" || typeof(carHeight) !== "number") {
        throw new Error("data type is not number");
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
    let textInputs = inputCarSpaceContainer.querySelectorAll('input[type="number"]');
    let carSpaceNumberArray = [];
    textInputs.forEach(input => {
        if (input.value.trim() === "") {
            isEmptyInputFound = true; 
        }
        let carSpaceNumber = {
            "id": input.getAttribute("parkingSquareId") ? input.getAttribute("parkingSquareId") : null,
            "name": input.name,
            "value": input.valueAsNumber
        }

        carSpaceNumberArray.push(carSpaceNumber);
    });
    carSpaceNumberArray.forEach((item, i) => {
        if (item.id == null) {
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

    setTimeout(() => (location.reload()), 1000)
}

getMemberParkingLotData();

async function getMemberParkingLotData(){
    try{
        const token = tokenChecking();
        const payload = await getPayload(token);
        const response = await getParkingLotMap(null, null, null, payload.id);
        const data = await handleResponse(response);
        memberParkingLotDataMap = data;
        displayParkingLotDiv(memberParkingLotDataMap);
    }catch(error){
        handleError(error);
    }
}

function setEmptyContainerContent(container) {
    let separator = document.createElement("div");
    separator.className = "separator";
    container.style.display = 'flex';
    container.style.justifyContent = 'center';
    container.style.alignItems = 'center';
    container.style.height = "20vh";
    container.textContent = '目前無登記的停車場';
    container.after(separator);
}
function displayParkingLotDiv(memberParkingLotDataMap) {
    const container = document.querySelector('#parking-lot-container'); 
    container.textContent = ""; 

    if (!memberParkingLotDataMap || memberParkingLotDataMap == null) {
        setEmptyContainerContent(container);
        return null;
    }

    for (const memberParkingLotData in memberParkingLotDataMap) {        
        createParkingLotDiv(container, memberParkingLotData);
    }
}

function createParkingLotDiv(container, memberParkingLotData) {
    const parkingLotData = memberParkingLotDataMap[memberParkingLotData];
    const parkingLotTableDivContainer = document.createElement('div');
    parkingLotTableDivContainer.id = "parkingLotTableDivContainer";
    parkingLotTableDivContainer.style.position = 'relative';
    const parkingLotTableDiv = document.createElement('div');
    parkingLotTableDiv.className = 'parking-lot-page-table';
    parkingLotTableDiv.id = parkingLotData.parkingLotId;

    const imageDiv = document.createElement('div');
    imageDiv.className = 'image';
    const img = document.createElement('img');
    img.src = parkingLotData.imgUrl && parkingLotData.imgUrl.length > 0 ? parkingLotData.imgUrl[0] : 'image/noimage.png';
    img.onerror = function() {
        this.onerror = null;
        img.src = 'image/noimage.png';
    }
    imageDiv.appendChild(img);
    parkingLotTableDiv.appendChild(imageDiv);

    const name = document.createElement('div');
    name.className = 'parking-lot-information-page-go-button';
    name.setAttribute('id', parkingLotData.parkingLotId);
    name.textContent = parkingLotData.name;
    parkingLotTableDiv.appendChild(name);

    const deleteButton = document.createElement('button');
    deleteButton.type = 'button';
    deleteButton.className = 'parking-lot-delete-button';
    deleteButton.id = 'parking-lot-delete-button';
    deleteButton.textContent = '刪除';

    const separator = document.createElement('div');
    separator.className = 'separator';
    
    parkingLotTableDiv.appendChild(deleteButton);
    parkingLotTableDivContainer.appendChild(parkingLotTableDiv);
    parkingLotTableDivContainer.appendChild(separator);

    container.appendChild(parkingLotTableDivContainer);

    // 使用中禁止修改/刪除
    for (let i=0; i<parkingLotData.carSpaceNumber.length; i++) {
        if (parkingLotData.carSpaceNumber[i].status == "使用中") {
            const mask = document.createElement('div');
            mask.style.justifyContent = "center";
            mask.style.alignItems = "center";
            mask.textContent = "使用中";
            mask.style.fontSize = "30px";
            mask.style.display = "flex";
            mask.style.color = "rgba(255,0,0,0.5)";
            mask.style.position       = 'absolute';
            mask.style.top            = '0';
            mask.style.left           = '0';
            mask.style.right          = '0';
            mask.style.bottom         = '10px';
            mask.style.background     = 'rgba(255,0,0,0.1)';
            mask.style.pointerEvents  = 'all';
            mask.style.zIndex         = '999';
            parkingLotTableDivContainer.appendChild(mask);
            name.style.color = "#e9ebef";
            break;
        }
    }
}

function fillParkingLotForm(parkingLotData) {
    document.querySelector('#parking-lot-id').textContent = parkingLotData.parkingLotId || '無';
    document.querySelector('#parking-lot-name').textContent = parkingLotData.name || '無';
    document.querySelector('#parking-lot-address').textContent = parkingLotData.address || '無';
    document.querySelector('#parking-lot-near-landmark').textContent = parkingLotData.nearLandmark || '無';
    document.querySelector('#parking-lot-opening-time').textContent = parkingLotData.openingTime + ' - ' + parkingLotData.closingTime || '無';
    document.querySelector('#parking-lot-in-out').textContent = parkingLotData.spaceInOut || '無';
    document.querySelector('#parking-lot-price').textContent = parkingLotData.price + ` 元` || '無';
    document.querySelector('#parking-lot-width').textContent = parkingLotData.carWidth + ' m' || '無';
    document.querySelector('#parking-lot-height').textContent = parkingLotData.carHeight + ' m' || '無';
    document.querySelector('#parking-space-total-number').textContent = parkingLotData.carSpaceNumber.length + ' 位' || '無';
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
        if(i == 0) {
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

document.querySelector("#alert-content-checked-button").addEventListener("click", () => {
    location.reload(); 
})