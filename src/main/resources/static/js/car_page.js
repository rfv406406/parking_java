initCarPage();

async function initCarPage(){
  const token = tokenChecking();
  try{
      const getCarBoardData = await fetchAPI("/api/car", token, "GET");
      const data = await handleResponse(getCarBoardData);
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
    message.style.color = "green";
    message.textContent = '新增成功!';
    setTimeout(() => (location.reload()), 1000)
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
            const carId = carTable.id;
            const response = await fetchAPI(`/api/car/${carId}`, token, 'DELETE')
            const data = await handleResponse(response);
            await initCarPage();
          }catch(error){
            handleError(error);
          }
      }
  }
});

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
