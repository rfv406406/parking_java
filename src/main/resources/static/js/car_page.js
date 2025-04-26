initCarPage();
// let memberCarData;
async function initCarPage(){
  const token = tokenChecking();
  try{
      const getCarBoardData = await fetchAPI("/api/car", token, "GET");
      const data = await handleResponse(getCarBoardData);
      // memberCarData = data;
      renderCarInformations(data);
    }catch(error){
      handleError(error);
    }
}

// 匯入車牌資訊
const carBoardDataStorage = document.querySelector('#plate-board-data-submit')
carBoardDataStorage.addEventListener('click', async (event) => {
  event.preventDefault();
  const token = tokenChecking();
  // let carNumberFormatTest = carNumberFormatTest();
  let re = new RegExp('^[A-Z]{3}-\\d{4}$');
  let message = document.querySelector('#car-page-message');
  let carNumber = document.querySelector('#plate-board-number').value;
  if (carNumber == ""){
    message.textContent = '請輸入車牌';
    return false;
  }
  if (!re.test(carNumber)) {
    message.textContent = '車牌格式錯誤';
    return false;
  }

  try {
    let formData = getCarInformation();
    const response = await fetchAPI("/api/car", token, 'POST', formData);
    const data = await handleResponse(response);
    // console.log(data);
    // await passCarBoardData(formData); 
    initCarPage();
  } catch(error) {
    handleError(error);
  }
});

//刪除停車場資料
document.querySelector('.parking-lot-container').addEventListener('click', async (event) => {
  if (event.target.matches('.parking-lot-delete-button')) {
      event.preventDefault();
      const token = tokenChecking();
      let carTable = event.target.closest('.parking-lot-table');
      if (carTable) {
          try{
            // const cartBoardNumber = carTable.querySelector('.parking-lot-information-page-go-button').textContent;
            // const carData = memberCarData.data.find(lot => lot.carboard_number === cartBoardNumber); 
            // const response = await deleteCarData(carData);
            const carId = carTable.id;
            const response = await fetchAPI(`/api/car/${carId}`, token, 'DELETE')
            const data = await handleResponse(response);
            initCarPage();
          }catch(error){
            handleError(error);
          }
      }
  }
});

// function carNumberFormatTest() {
//   let re = new RegExp('^[A-Z]{3}-\\d{4}$');
//   let message = document.querySelector('#car-page-message');
//   let carNumber = document.querySelector('#plate-board-number').value;
//   if (carNumber.isEmpty()){
//     message.textContent = '請輸入車牌';
//     return false;
//   }
//   if (!re.test(carNumber)) {
//     message.textContent = '車牌格是錯誤';
//     return false;
//   }
//   return true;
// }

function getCarInformation(){
  let carNumber = document.querySelector('#plate-board-number').value;
  let image = document.querySelector('#car-img-file').files[0]

  let formData = new FormData();
  
  formData.append('carNumber', carNumber);
  if (image) {
    formData.append('carImage', image);
  }

  return formData;
};

// async function passCarBoardData(formData){
//   try{
//       const response = await inputCarBoardDataToDB(formData);
//       const data = await handleResponse(response);
//       // await fetchData();
//   }catch(error){
//       handleError(error);
//   }
// }

// async function inputCarBoardDataToDB(formData){
//   const token = tokenChecking();
//   const response = await fetchAPI("/api/input_car_board_data", token, 'POST', formData)
//   return response;
// }

function renderCarInformations(data) {
  const container = document.querySelector('#parking-lot-container'); 
  container.textContent = ''; 
  if (data == null || data.length === 0) {
    let separator = document.createElement("div");
    separator.className = "separator";
    container.style.display = 'flex';
    container.style.justifyContent = 'center';
    container.style.alignItems = 'center';
    container.style.height = "240px";
    container.textContent = '目前無登記的車牌';
    container.after(separator);
    return true;
  }

  data.forEach(item => {
    const parkingLotDivContainer = document.createElement('div');
    parkingLotDivContainer.className = 'parking-lot-table';
    parkingLotDivContainer.id = item.id;
    const parkingLotDiv = document.createElement('div');
    parkingLotDiv.className = 'parking-lot-infor';
    
    const nameDiv = document.createElement('div');
    nameDiv.className = 'parking-lot-information-page-go-button';
    // nameDiv.textContent = item.carboard_number;
    nameDiv.textContent = item.carNumber;
    parkingLotDiv.appendChild(nameDiv);

    const imageDiv = document.createElement('div');
    imageDiv.className = 'image';
    const img = document.createElement('img');
    img.src = item.carImageUrl != null ? item.carImageUrl : 'image/noimage.png';
    img.onerror = function() {
      this.onerror = null;
      img.src = 'image/noimage.png';
    }
    imageDiv.appendChild(img);
    parkingLotDiv.appendChild(imageDiv);

    const deleteButton = document.createElement('button');
    deleteButton.type = 'button';
    deleteButton.className = 'parking-lot-delete-button';
    deleteButton.textContent = '刪除';
    deleteButton.style.display = 'none';

    // 點擊 parking-lot-page-table 切換刪除按鈕成顯示
    parkingLotDiv.addEventListener('click', () => {
        if(deleteButton.style.display === "none") {
          deleteButton.style.display = 'block';
        }else{
          deleteButton.style.display = 'none';
        } 
    });
    parkingLotDivContainer.appendChild(parkingLotDiv);
    parkingLotDiv.after(deleteButton);
    container.appendChild(parkingLotDivContainer);

    const separator = document.createElement('div');
    separator.className = 'separator';
    container.appendChild(separator);
  });
}

// async function deleteCarData(data){
//   const token = tokenChecking();
//   const response = await fetchAPI("/api/input_car_board_data", token, 'DELETE', data)
//   return response;
// }