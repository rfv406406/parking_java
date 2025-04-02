fetchIdPageData();
async function fetchIdPageData(){
    try{
      const token = localStorage.getItem('Token');
      const response = await fetchAPI("/api/get_id_page_data", token, 'GET');
      const data = await handleResponse(response);
      idDataInTableAndInput(data);
    }catch(error){
      handleError(error);
    }
}

function idDataInTableAndInput(data){
  document.querySelector('#id_page_id_number').textContent = data.data.id || '無';
  document.querySelector('#id_page_account').textContent = data.data.account || '無';
  document.querySelector('#id_page_name').textContent = data.data.name || '無';
  document.querySelector('#id_page_e-mail').textContent = data.data.email || '無';
  document.querySelector('#id_page_birthday').textContent = data.data.birthday || '無';
  document.querySelector('#id_page_phone').textContent = data.data.cellphone || '無';
  document.querySelector('#name-information').textContent = data.data.name || '';
  document.querySelector('#email-information').textContent = data.data.email || '';
  document.querySelector('#birthday-information').textContent = data.data.birthday || '';
  document.querySelector('#callPhone-information').textContent = data.data.cellphone || '';
}

const idPageStorageButton = document.querySelector('#id_page_storage-button');

idPageStorageButton.addEventListener('click', InputIdPageData);

async function InputIdPageData(){
  try{
    const token = localStorage.getItem('Token');
    let idData  = GetIdDataInInput();
    if (!idData) {
      return null;
    } else{
      const response = await fetchAPI("/api/input_id_page_data", token, 'POST', idData)
      // const response = await inputIdDataToDB();
      const data = await handleResponse(response);
      idEditMessage(data);
    }
  }catch(error){
    handleError(error);
  }
}

function GetIdDataInInput() {
  let name = document.querySelector('#name-information').value;
  let email = document.querySelector('#email-information').value;
  let birthday = document.querySelector('#birthday-information').value;
  let cellphone = document.querySelector('#callPhone-information').value;
  let password = document.querySelector('#password-information').value;
  if (!name && !email && !birthday && !cellphone && !password) {
    let messageContainer = document.querySelector('#id-edit-success-message');
    messageContainer.textContent = '請輸入編輯資料!';
    return null;
  }
  return {
    name: name,
    email: email,
    birthday: birthday,
    cellphone: cellphone,
    password: password
  };
}

function idEditMessage(data){
  let messageContainer = document.querySelector('#id-edit-success-message')
  if (data.ok){
      messageContainer.textContent = '編輯成功!';
      window.location.href = '/id'
  }
};